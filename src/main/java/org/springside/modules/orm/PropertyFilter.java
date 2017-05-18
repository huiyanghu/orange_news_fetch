    
  
  
  
  
  
  
package org.springside.modules.orm;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

    
  
  
  
  
  
  
  
  
public class PropertyFilter {

	    
  
  
	private static final String OR_SEPARATOR = "_OR_";

	    
  
  
	public enum MatchType {
		EQ, LIKE, LT, GT, LE, GE;
	}

	    
  
  
	public enum PropertyType {
		S(String.class), I(Integer.class), L(Long.class), F(Float.class), D(Date.class);

		PropertyType(Class<?> clazz) {
			this.clazz = clazz;
		}

		private Class<?> clazz;

		public Class<?> getValue() {
			return clazz;
		}
	}

	private String[] propertyNames = null;
	private Class<?> propertyType = null;
	private Object propertyValue = null;
	private MatchType matchType = MatchType.EQ;

	public PropertyFilter() {
	}

	    
  
  
  
  
	public PropertyFilter(final String filterName, final Object value) {

		String matchTypeStr = StringUtils.substringBefore(filterName, "_");
		String matchTypeCode = StringUtils.substring(matchTypeStr, 0, matchTypeStr.length() - 1);
		String propertyTypeCode = StringUtils.substring(matchTypeStr, matchTypeStr.length() - 1, matchTypeStr.length());
		try {
			matchType = Enum.valueOf(MatchType.class, matchTypeCode);
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("filter名称" + filterName + "没有按规则编写,无法得到属性比较类型.", e);
		}

		try {
			propertyType = Enum.valueOf(PropertyType.class, propertyTypeCode).getValue();
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("filter名称" + filterName + "没有按规则编写,无法得到属性值类型.", e);
		}

		String propertyNameStr = StringUtils.substringAfter(filterName, "_");
		propertyNames = StringUtils.split(propertyNameStr, PropertyFilter.OR_SEPARATOR);

		Assert.isTrue(propertyNames.length > 0, "filter名称" + filterName + "没有按规则编写,无法得到属性名称.");

		this.propertyValue = value;
	}

	    
  
  
	public boolean isMultiProperty() {
		return (propertyNames.length > 1);
	}

	    
  
  
	public String[] getPropertyNames() {
		return propertyNames;
	}

	    
  
  
	public String getPropertyName() {
		if (propertyNames.length > 1)
			throw new IllegalArgumentException("There are not only one property");
		return propertyNames[0];
	}

	    
  
  
	public Object getPropertyValue() {
		return propertyValue;
	}

	    
  
  
	public Class<?> getPropertyType() {
		return propertyType;
	}

	    
  
  
	public MatchType getMatchType() {
		return matchType;
	}
}
