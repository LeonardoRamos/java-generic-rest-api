package com.generic.rest.api.repository.core.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Selection;

import org.hibernate.jpa.criteria.expression.function.AggregationFunction;
import org.hibernate.mapping.Set;
import org.springframework.stereotype.Component;

import com.generic.rest.api.domain.core.BaseApiEntity;
import com.generic.rest.api.domain.core.filter.AggregateFunction;
import com.generic.rest.api.util.ReflectionUtils;

@Component
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ApiResultMapper<ENTITY extends BaseApiEntity> {
	
	public List<ENTITY> mapResultSet(
			Class<ENTITY> entityClass, 
			List<Object> result, 
			List<Selection<? extends Object>> projection) throws Exception {
		
		List<ENTITY> entities = new ArrayList<>();
		
		for (Object row : result) {
			
			if (row != null) {
				if (Object[].class.equals(row.getClass())) {
					mapSimpleValuesSelection(entityClass, projection, entities, row);
					
				} else if (entityClass.equals(row.getClass())) {
					mapEntityObject(entityClass, projection, entities, row);
					
				} else {
					mapEntityValues(entityClass, projection, entities, row);
				}
			}
		}
		
		return entities;
	}
	
	private void mapSimpleValuesSelection(Class<ENTITY> entityClass, List<Selection<? extends Object>> projection,
			List<ENTITY> entities, Object row) throws Exception {
		
		Object[] fieldData = (Object[]) row;
		
		Constructor<?> constructor = entityClass.getConstructor();
		ENTITY object = (ENTITY) constructor.newInstance();
		
		for (int i = 0; i < fieldData.length; i++) {
		
			if (projection.get(i) instanceof AggregationFunction) {
				AggregationFunction aggregationFunction = (AggregationFunction) projection.get(i);
				Path<Object> attributePath = (Path<Object>) aggregationFunction.getArgumentExpressions().get(0);
				
				if (AggregateFunction.isCountFunction(aggregationFunction.getFunctionName())) {
					object.addCount(mapAggregationField(entityClass, (Long) fieldData[i], attributePath));
					
				} else if (AggregateFunction.isSumFunction(aggregationFunction.getFunctionName())) {
					if (fieldData[i].getClass().equals(Double.class)) {
						object.addSum(mapAggregationField(entityClass, BigDecimal.valueOf((Double) fieldData[i]), attributePath));
					} else {
						object.addSum(mapAggregationField(entityClass, (Long) fieldData[i], attributePath));
					}
					
				} else if (AggregateFunction.isAvgFunction(aggregationFunction.getFunctionName())) {
					if (fieldData[i].getClass().equals(Double.class)) {
						object.addAvg(mapAggregationField(entityClass, BigDecimal.valueOf((Double) fieldData[i]), attributePath));
					} else {
						object.addAvg(mapAggregationField(entityClass, (Long) fieldData[i], attributePath));
					}
				}
				
			} else {
				Path<Object> attributePath = (Path<Object>) projection.get(i);
				mapProjectionField(entityClass, fieldData[i], attributePath, object);
			}
		}
		
		entities.add(object);
	}
	
	private void mapEntityObject(Class<ENTITY> entityClass, List<Selection<? extends Object>> projection,
			List<ENTITY> entities, Object row)
			throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		
		ENTITY entity = (ENTITY) row;
		
		if (projection == null || projection.isEmpty()) {
			entities.add(entity);
			return;
		}
		
		Constructor<?> constructor = entityClass.getConstructor();
		ENTITY object = (ENTITY) constructor.newInstance();
		
		for (Field field : entityClass.getDeclaredFields()) {
		    field.setAccessible(true);
		    
			if (isInProjection(field.getName(), projection)) {
				field.set(object, field.get(entity));
			}
		}
		
		entities.add(object);
	}
	
	private Boolean isInProjection(String fieldName, List<Selection<? extends Object>> projection) {
		if (projection == null || projection.isEmpty()) {
			return Boolean.FALSE;
		}
		
		for (Selection<? extends Object> projectionField : projection) {
			if (fieldName.equals(projectionField.getAlias())) {
				return Boolean.TRUE;
			}
		}
		
		return Boolean.FALSE;
	}

	private void mapEntityValues(Class<ENTITY> entityClass, List<Selection<? extends Object>> projection,
			List<ENTITY> entities, Object row) throws Exception {
		
		Constructor<?> constructor = entityClass.getConstructor();
		ENTITY object = (ENTITY) constructor.newInstance();
		
		if (projection.get(0) instanceof AggregationFunction) {
			AggregationFunction aggregationFunction = (AggregationFunction) projection.get(0);
			Path<Object> attributePath = (Path<Object>) aggregationFunction.getArgumentExpressions().get(0);
			
			if (AggregateFunction.isCountFunction(aggregationFunction.getFunctionName()) ||
					AggregateFunction.isCountDistinctFunction(aggregationFunction.getFunctionName())) {
				object.addCount(mapAggregationField(entityClass, (Long) row, attributePath));
				
			} else if (AggregateFunction.isSumFunction(aggregationFunction.getFunctionName())) {
				if (row.getClass().equals(Double.class)) {
					object.addSum(mapAggregationField(entityClass, BigDecimal.valueOf((Double) row), attributePath));
				} else {
					object.addSum(mapAggregationField(entityClass, (Long) row, attributePath));
				}
				
			} else if (AggregateFunction.isAvgFunction(aggregationFunction.getFunctionName())) {
				if (row.getClass().equals(Double.class)) {
					object.addAvg(mapAggregationField(entityClass, BigDecimal.valueOf((Double) row), attributePath));
				} else {
					object.addAvg(mapAggregationField(entityClass, (Long) row, attributePath));
				}
			}
			
		} else {
			Path<Object> attributePath = (Path<Object>) projection.get(0);
			mapProjectionField(entityClass, row, attributePath, object);
		}
		
		entities.add(object);
	}
	
	private void mapProjectionField(Class<ENTITY> entityClass, Object fieldData, Path<Object> attributePath, ENTITY object) throws Exception {
		Stack<Map<String, Class>> fieldPaths = buildNestedFieldStack(entityClass, attributePath);

		Map<String, Class> rootFielddMap = fieldPaths.pop();
		Map.Entry<String, Class> rootFieldEntry = rootFielddMap.entrySet().iterator().next();
		
		if (fieldPaths.isEmpty()) {
			Field fieldRoot = ReflectionUtils.getEntityFieldByName(entityClass, rootFieldEntry.getKey());
			fieldRoot.setAccessible(true);
			
			setFieldValueResult(fieldData, object, fieldRoot);
		
		} else {
			Constructor<?> constructorRootField = rootFieldEntry.getValue().getConstructor();
			Object rootObject = constructorRootField.newInstance();
			
			Field fieldRoot = ReflectionUtils.getEntityFieldByName(entityClass, rootFieldEntry.getKey());
			fieldRoot.setAccessible(true);
				
			Object currentObject = rootObject;
			
			while (!fieldPaths.isEmpty()) {
				Map<String, Class> fieldMap = fieldPaths.pop();
				Map.Entry<String, Class> fieldEntry = fieldMap.entrySet().iterator().next();
				
				if (fieldPaths.isEmpty()) {
					Field currentField = ReflectionUtils.getEntityFieldByName(currentObject.getClass(), fieldEntry.getKey());
					currentField.setAccessible(true);
					
					setFieldValueResult(fieldData, currentObject, currentField);
					
				} else {
					Constructor<?> constructorField = fieldEntry.getValue().getConstructor();
					Object fieldObject = constructorField.newInstance();
					
					Field currentField = ReflectionUtils.getEntityFieldByName(currentObject.getClass(), fieldEntry.getKey());
					currentField.setAccessible(true);
					
					setFieldValueResult(fieldObject, currentObject, currentField);
				
					currentObject = fieldObject;
				}
			}
			
			setFieldValueResult(rootObject, object, fieldRoot);
		}
	}

	private Stack<Map<String, Class>> buildNestedFieldStack(Class<ENTITY> entityClass, Path<Object> attributePath) {
		Stack<Map<String, Class>> fieldPaths = new Stack<>();
		
		do {
			if (!entityClass.equals(attributePath.getJavaType())) {
				fieldPaths.push(Collections.singletonMap(attributePath.getAlias(), attributePath.getJavaType()));
			}
			
			attributePath = (Path<Object>) attributePath.getParentPath();
			
		} while (attributePath.getParentPath() != null);
		
		return fieldPaths;
	}
	
	private Map<String, Object> mapAggregationField(Class<ENTITY> entityClass, Object fieldData, Path<Object> attributePath) throws Exception {
		Map<String, Object> aggregation = new HashMap<>();
		
		do {
			if (!entityClass.equals(attributePath.getJavaType())) {
				aggregation = new HashMap<>();
				aggregation.put(attributePath.getAlias(), fieldData);
				
				fieldData = aggregation;
			}
			
			attributePath = (Path<Object>) attributePath.getParentPath();
		
		} while (attributePath.getParentPath() != null);
		
		return aggregation;
	}

	private void setFieldValueResult(Object fieldDataValue, Object object, Field field) throws Exception {
		if (Collection.class.isAssignableFrom(field.getType())) {
			Collection collection = (Collection) field.get(object);
			
			if (collection == null) {
				
				if (Set.class.equals(field.getType())) {
					collection = new HashSet<>();
				
				} else if (Queue.class.equals(field.getType())) {
					collection = new PriorityQueue<>();
				
				} else {
					collection = new ArrayList<>();
				}
			}
			
			collection.add(fieldDataValue);
			field.set(object, collection);
			
		} else {
			field.set(object, fieldDataValue);
		}
	}
	
}