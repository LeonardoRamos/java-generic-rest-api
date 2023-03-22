package com.generic.rest.api.repository.core;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.springframework.stereotype.Component;

import com.generic.rest.api.Constants;
import com.generic.rest.api.Constants.MSGERROR;
import com.generic.rest.api.domain.core.filter.AggregateFunction;
import com.generic.rest.api.domain.core.filter.FilterExpression;
import com.generic.rest.api.domain.core.filter.FilterField;
import com.generic.rest.api.domain.core.filter.FilterOrder;
import com.generic.rest.api.domain.core.filter.LogicOperator;
import com.generic.rest.api.domain.core.filter.RequestFilter;
import com.generic.rest.api.exception.BadRequestApiException;
import com.generic.rest.api.util.ReflectionUtils;
import com.generic.rest.api.util.StringParserUtils;

@Component
@SuppressWarnings({ "unchecked", "rawtypes" } )
public class ApiQueryBuilder<E> {
	
	public Boolean containsMultiValuedProjection(List<Selection<? extends Object>> projection) {
		if (projection == null || projection.isEmpty()) {
			return Boolean.FALSE;
		}
		
		for (Selection<? extends Object> projectionField : projection) {
			Path<Object> attributePath = (Path<Object>) projectionField;
			
			if (Collection.class.isAssignableFrom(attributePath.getJavaType())) {
				return Boolean.TRUE;
			}
		}
		
		return Boolean.FALSE;
	}
	
	public List<Selection<? extends Object>> getGroupByFields(RequestFilter requestFilter, Root<?> root, Class<E> entityClass) throws BadRequestApiException {
		try {
			List<String> groupByFields = requestFilter.getParsedGroupBy();
			return buildProjectionSelection(root, entityClass, groupByFields);
			
		} catch (Exception e) {
			throw new BadRequestApiException(String.format(MSGERROR.PARSE_PROJECTIONS_ERROR, requestFilter.getProjection()), e);
		}
	}
	
	public List<Selection<? extends Object>> getProjectionFields(RequestFilter requestFilter, Root<?> root, Class<E> entityClass) throws BadRequestApiException {
		try {
			List<String> projectionFields = requestFilter.getParsedProjection();
			return buildProjectionSelection(root, entityClass, projectionFields);
			
		} catch (Exception e) {
			throw new BadRequestApiException(String.format(MSGERROR.PARSE_PROJECTIONS_ERROR, requestFilter.getProjection()), e);
		}
	}
	
	private List<Selection<? extends Object>> buildProjectionSelection(Root<?> root, Class<E> entityClass,
			List<String> projectionFields) throws NoSuchFieldException {
		
		List<Selection<? extends Object>> projection = new ArrayList<>();
		
		if (!projectionFields.isEmpty()) {
			
			for (String fieldName : projectionFields) {
				List<Field> fields = splitFields(entityClass, fieldName);
				projection.add(buildFieldExpression(fields, root));
			}
		}
		
		return projection;
	}
	
	public List<Selection<? extends Object>> buildAggregateSelection(Root<?> root, CriteriaBuilder criteriaBuilder, Class<E> entityClass,
			RequestFilter requestFilter) throws BadRequestApiException {
		try {
			List<String> sumFields = requestFilter.getParsedSum();
			List<String> avgFields = requestFilter.getParsedAvg();
			List<String> countFields = requestFilter.getParsedCount();
			List<String> countDistinctFields = requestFilter.getParsedCountDistinct();
			List<String> groupByFields = requestFilter.getParsedGroupBy();
			
			List<Selection<? extends Object>> aggregationFields = new ArrayList<>();
			
			if (!sumFields.isEmpty()) {
				addAggregationFields(root, criteriaBuilder, entityClass, sumFields, aggregationFields, AggregateFunction.SUM.name());
			}
			
			if (!countFields.isEmpty()) {
				addAggregationFields(root, criteriaBuilder, entityClass, countFields, aggregationFields, AggregateFunction.COUNT.name());
			}
			
			if (!countDistinctFields.isEmpty()) {
				addAggregationFields(root, criteriaBuilder, entityClass, countDistinctFields, aggregationFields, AggregateFunction.COUNT_DISTINCT.name());
			}
			
			if (!avgFields.isEmpty()) {
				addAggregationFields(root, criteriaBuilder, entityClass, avgFields, aggregationFields, AggregateFunction.AVG.name());
			}
			
			if (!groupByFields.isEmpty()) {
				addAggregationFields(root, criteriaBuilder, entityClass, groupByFields, aggregationFields, null);
			}
			
			return aggregationFields;
			
		} catch (Exception e) {
			throw new BadRequestApiException(String.format(MSGERROR.PARSE_PROJECTIONS_ERROR, requestFilter.getProjection()), e);
		}
	}

	private void addAggregationFields(Root<?> root, CriteriaBuilder criteriaBuilder, Class<E> entityClass,
			List<String> requestFields, List<Selection<? extends Object>> aggregationFields, String aggregateFunction) throws NoSuchFieldException {
		
		for (String fieldName : requestFields) {
			List<Field> fields = splitFields(entityClass, fieldName);
			
			if (Boolean.TRUE.equals(AggregateFunction.isSumFunction(aggregateFunction))) {
				aggregationFields.add(criteriaBuilder.sum(buildFieldExpression(fields, root)));
				
			} else if (Boolean.TRUE.equals(AggregateFunction.isAvgFunction(aggregateFunction))) {
				aggregationFields.add(criteriaBuilder.avg(buildFieldExpression(fields, root)));
				
			} else if (Boolean.TRUE.equals(AggregateFunction.isCountFunction(aggregateFunction))) {
				aggregationFields.add(criteriaBuilder.count(buildFieldExpression(fields, root)));
				
			} else if (Boolean.TRUE.equals(AggregateFunction.isCountDistinctFunction(aggregateFunction))) {
				aggregationFields.add(criteriaBuilder.countDistinct(buildFieldExpression(fields, root)));
				
			} else {
				aggregationFields.add(buildFieldExpression(fields, root));
			}
		}
	}

	public List<Predicate> getRestrictions(
			Class<E> entityClass,
			RequestFilter requestFilter, 
			CriteriaBuilder criteriaBuilder,
			Root<?> root) throws BadRequestApiException {
		try {
			FilterExpression currentExpression = FilterExpression.buildFilterExpressions(requestFilter.getFilter());
			List<Predicate> restrictions = new ArrayList<>();
			
			while (currentExpression != null) {
				List<Predicate> conjunctionRestrictions = new ArrayList<>();
				
				if (currentExpression.getFilterField() != null) {
					if (LogicOperator.OR.equals(currentExpression.getLogicOperator())) {
						
						currentExpression = getOrRestrictions(entityClass, criteriaBuilder, root, currentExpression, conjunctionRestrictions);
						
						List<Predicate> orParsedRestrictions = new ArrayList<>();
						orParsedRestrictions.add(criteriaBuilder.or(conjunctionRestrictions.toArray(new Predicate[]{})));
					
						restrictions.add(criteriaBuilder.and(orParsedRestrictions.toArray(new Predicate[]{})));

					} else {
						conjunctionRestrictions.add(buildPredicate(entityClass, currentExpression.getFilterField(), criteriaBuilder, root));
						restrictions.add(criteriaBuilder.and(conjunctionRestrictions.toArray(new Predicate[]{})));
					}
				}
				
				currentExpression = currentExpression != null ?currentExpression.getFilterNestedExpression() : currentExpression;
			}
	        
			return restrictions;
		
		} catch (Exception e) {
			throw new BadRequestApiException(String.format(MSGERROR.PARSE_FILTER_FIELDS_ERROR, requestFilter.getFilter()), e);
		}
	}

	private FilterExpression getOrRestrictions(Class<E> entityClass, CriteriaBuilder criteriaBuilder, Root<?> root,
			FilterExpression currentExpression, List<Predicate> conjunctionRestrictions)
			throws NoSuchFieldException, IOException {
		
		do {
			conjunctionRestrictions.add(buildPredicate(entityClass, currentExpression.getFilterField(), criteriaBuilder, root));
			currentExpression = currentExpression.getFilterNestedExpression();
		} while (currentExpression != null && LogicOperator.OR.equals(currentExpression.getLogicOperator()));
		
		if (currentExpression != null && currentExpression.getFilterField() != null) {
			conjunctionRestrictions.add(buildPredicate(entityClass, currentExpression.getFilterField(), criteriaBuilder, root));
		}
		
		return currentExpression;
	}

	private Predicate buildPredicate(
			Class<E> entityClass,
			FilterField filterField, 
			CriteriaBuilder criteriaBuilder, 
			Root<?> root) throws NoSuchFieldException, IOException {

		List<Field> fields = splitFields(entityClass, filterField.getField());
		Field field = null;
		
		switch (filterField.getFilterOperator()) {
			case IN:
				field = getSignificantField(fields);
				return buildFieldExpression(fields, root)
						.in(ReflectionUtils.getFieldList(
								StringParserUtils.replace(StringParserUtils.replace(filterField.getValue(), "(", ""), ")", ""),
								field.getType()));
			case OU:
				field = getSignificantField(fields);
				return buildFieldExpression(fields, root)
						.in(ReflectionUtils.getFieldList(filterField.getValue(), field.getType())).not();
			case GE:
				return criteriaBuilder.greaterThanOrEqualTo(buildFieldExpression(
						fields, root), (Comparable) getTypifiedValue(filterField, fields));
			case GT:
				return criteriaBuilder.greaterThan(buildFieldExpression(
						fields, root), (Comparable) getTypifiedValue(filterField, fields));
			case LE:
				return criteriaBuilder.lessThanOrEqualTo(buildFieldExpression(
						fields, root), (Comparable) getTypifiedValue(filterField, fields));
			case LT:
				return criteriaBuilder.lessThan(buildFieldExpression(
						fields, root), (Comparable) getTypifiedValue(filterField, fields));
			case NE:
				if (Constants.NULL_VALUE.equals(filterField.getValue())) {
					return criteriaBuilder.isNotNull(buildFieldExpression(fields, root));
				}
				return criteriaBuilder.notEqual(buildFieldExpression(
						fields, root), getTypifiedValue(filterField, fields));
			case LK:
				return criteriaBuilder.like(criteriaBuilder.upper(buildFieldExpression(fields, root)), 
						"%" + ((String) getTypifiedValue(filterField, fields)).toUpperCase() + "%");
			case EQ:
			default:
				if (Constants.NULL_VALUE.equals(filterField.getValue())) {
					return criteriaBuilder.isNull(buildFieldExpression(fields, root));
				}
				return criteriaBuilder.equal(buildFieldExpression(
						fields, root), getTypifiedValue(filterField, fields));
		}
	}

	private List<Field> splitFields(Class<E> entityClass, String fieldName) throws NoSuchFieldException, SecurityException {
		List<Field> fields = new ArrayList<>();
		List<String> attributeNames =  StringParserUtils.splitStringList(fieldName, '.');
		Class<?> currentFieldClass = entityClass;
		
		for (String attributeName : attributeNames) {
			Field field = ReflectionUtils.getEntityFieldByName(currentFieldClass, attributeName);
			currentFieldClass = field.getType();
			fields.add(field);
		}
		
		return fields;
	}

	private Object getTypifiedValue(FilterField filterField, List<Field> fields)
			throws IOException, NoSuchFieldException {
		
		Field field = getSignificantField(fields);
		return ReflectionUtils.getEntityValueParsed(filterField.getValue(), field.getType());
	}

	private Field getSignificantField(List<Field> fields) throws NoSuchFieldException {
		if (fields.isEmpty()) {
			throw new NoSuchFieldException();
		}
		
		return fields.get(fields.size() - 1);
	}
	
	private Expression buildFieldExpression(List<Field> fields, Root<?> root) throws NoSuchFieldException {
		Path<E> expressionPath = null;
		
		for (Field field : fields) {
			if (expressionPath == null) {
				expressionPath = root.get(field.getName());
				expressionPath.alias(field.getName());
			} else {
				expressionPath = expressionPath.get(field.getName());
				expressionPath.alias(field.getName());
			}
		}
		
		if (expressionPath == null) {
			throw new NoSuchFieldException();
		}
		
		return expressionPath;
	}
	
	public List<Order> getOrders(
			RequestFilter requestFilter, 
			CriteriaBuilder criteriaBuilder,
			Root<?> root,
			Class<E> entityClass) throws BadRequestApiException {
		
		try {
			List<FilterOrder> filterOrders = FilterOrder.buildFilterOrders(requestFilter.getSort());
			List<Order> orders = new ArrayList<>();
	        
			if (filterOrders != null && !filterOrders.isEmpty()) {
	        	
	        	for (FilterOrder filterOrder : filterOrders) {
	        		List<Field> fields =  splitFields(entityClass, filterOrder.getField());

	        		switch (filterOrder.getSortOrder()) {
	        			case DESC:
	        				orders.add(criteriaBuilder.desc(buildFieldExpression(fields, root)));
	        				break;
	        			case ASC:
	        			default:
	        				orders.add(criteriaBuilder.asc(buildFieldExpression(fields, root)));
	        				break;
	        		}
	        	}
	        }
			
			return orders;
		
		} catch (Exception e) {
			throw new BadRequestApiException(String.format(MSGERROR.PARSE_SORT_ORDER_ERROR, requestFilter.getSort()));
		}
	}
	
}