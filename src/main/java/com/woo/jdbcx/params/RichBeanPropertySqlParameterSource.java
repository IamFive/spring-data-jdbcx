/**
 * @(#)RichBeanPropertySqlParameterSource.java 2016年4月24日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package com.woo.jdbcx.params;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

/**
 * @author Woo Cupid
 * @date 2016年4月24日
 * @version $Revision$
 */
public class RichBeanPropertySqlParameterSource extends BeanPropertySqlParameterSource {

	protected BeanWrapper beanWrapper;

	/**
	 * @param object
	 */
	public RichBeanPropertySqlParameterSource(Object object) {
		super(object);
		this.beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource#getValue(java.lang.String)
	 */
	// @Override
	// public Object getValue(String paramName) throws IllegalArgumentException {
	// Object value = super.getValue(paramName);
	// if (value instanceof List) {
	// List list = (List) value;
	// Array[] a = new Array[list.size()];
	// for (int i = 0; i < list.size(); i++) {
	// }
	// // for (Object object : list) {
	// // a
	// // }
	// return a;
	// }
	// return value;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource#getSqlType(java.lang.String)
	 */
	// @Override
	// public int getSqlType(String paramName) {
	// int sqlType = super.getSqlType(paramName);
	// if (sqlType != TYPE_UNKNOWN) {
	// return sqlType;
	// }
	//
	// Class<?> propType = this.beanWrapper.getPropertyType(paramName);
	// if (List.class.isAssignableFrom(propType)) {
	// return Types.ARRAY;
	// }
	//
	// return sqlType;
	// }

}
