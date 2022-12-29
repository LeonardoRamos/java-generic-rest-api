package com.generic.rest.api.domain.core.filter;

public class FilterExpression {
    
    private FilterField filterField;
    private FilterExpression nestedFilterExpression;
    private LogicOperator logicOperator;
    
    public FilterField getFilterField() {
        return filterField;
    }
    
    public void setFilterField(FilterField filterField) {
        this.filterField = filterField;
    }
    
    public FilterExpression getFilterNestedExpression() {
        return nestedFilterExpression;
    }
    
    public void setNestedFilterExpression(FilterExpression nestedFilterExpression) {
        this.nestedFilterExpression = nestedFilterExpression;
    }

	public LogicOperator getLogicOperator() {
		return logicOperator;
	}

	public void setLogicOperator(LogicOperator logicOperator) {
		this.logicOperator = logicOperator;
	}
	
	public static FilterExpression buildFilterExpressions(String expressionString) {
		FilterExpression currentExpression = new FilterExpression();
        
		if (expressionString == null) {
			return currentExpression;
		}
		
		FilterExpression initialExpression = currentExpression;
        StringBuilder word = new StringBuilder();
        
        Integer i = 0;
        while (i < expressionString.length()) {
            
        	if (expressionString.charAt(i) != '_') {
                word.append(expressionString.charAt(i));
                
            } else {
            	LogicOperator logicOperator = processOperator(expressionString, i);

            	if (logicOperator != null) {
            		currentExpression.setLogicOperator(logicOperator);
            		currentExpression.setFilterField(FilterField.buildFilterField(word.toString().trim()));
            		currentExpression = processNewExpressionNode(currentExpression);
            		
            		i += logicOperator.getOperator().length() - 1;
            		word = new StringBuilder();
            	
            	} else {
            		word.append(expressionString.charAt(i));
            	}
            }
        	
        	i++;
        }
        
        currentExpression.setFilterField(FilterField.buildFilterField(word.toString().trim()));
        processNewExpressionNode(currentExpression);
        
        return initialExpression;
	}
	
	private static FilterExpression processNewExpressionNode(FilterExpression currentExpression) {
		if (currentExpression.getLogicOperator() != null) {
    		FilterExpression newExpression = new FilterExpression();
    		currentExpression.setNestedFilterExpression(newExpression);
    		currentExpression = currentExpression.getFilterNestedExpression();
    	}
		
		return currentExpression;
	}
	
	private static LogicOperator processOperator(String expressionString, int index) {
		StringBuilder logicOperator = new StringBuilder();
		Boolean appendOperation = Boolean.TRUE;
		
		do {
			logicOperator.append(expressionString.charAt(index));
		    index++;
		    
		    if (index >= expressionString.length() || expressionString.charAt(index) == '_') {
		    	
		    	appendOperation = Boolean.FALSE;
		    	
		    	if (index < expressionString.length() && expressionString.charAt(index) == '_') {
		    		logicOperator.append(expressionString.charAt(index));
		    	}
		    }
		    
		} while (Boolean.TRUE.equals(appendOperation));
		
		return LogicOperator.getLogicOperator(logicOperator.toString().trim());
	}
	
	@Override
	public String toString() {
		return "Expression [filterField=" + filterField + ", logicOperator=" + logicOperator + 
				", expression=" + nestedFilterExpression +  "]";
	}
	
}
