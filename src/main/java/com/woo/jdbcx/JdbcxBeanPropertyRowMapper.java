/**
 * @(#)x.java 2016年5月8日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package com.woo.jdbcx;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * @author Woo Cupid
 * @date 2016年5月8日
 * @version $Revision$
 */
public class JdbcxBeanPropertyRowMapper<T> extends BeanPropertyRowMapper<T> {

	/** The class we are mapping to */
	private Class<T> mappedClass;

	/** Map of the fields we provide mapping for */
	private Map<String, PropertyDescriptor> mappedFields;

	/** Set of bean properties we provide mapping for */
	private Set<String> mappedProperties;

	private ConversionService cs;

	public JdbcxBeanPropertyRowMapper() {
		super();
	}

	public JdbcxBeanPropertyRowMapper(Class<T> mappedClass, boolean checkFullyPopulated) {
		super(mappedClass, checkFullyPopulated);
	}

	public JdbcxBeanPropertyRowMapper(Class<T> mappedClass) {
		super(mappedClass);
	}

	public JdbcxBeanPropertyRowMapper(Class<T> mappedClass, final ConversionService cs) {
		super(mappedClass);
		this.cs = cs;
	}

	/**
	 * copied from super-class to backwards compatibility for Spring<4.2 
	 * @param name the original name
	 * @return the converted name
	 * @since 4.2
	 * @see #lowerCaseName
	 */
	protected String underscoreName(String name) {
		if (!StringUtils.hasLength(name)) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		result.append(lowerCaseName(name.substring(0, 1)));
		for (int i = 1; i < name.length(); i++) {
			String s = name.substring(i, i + 1);
			String slc = lowerCaseName(s);
			if (!s.equals(slc)) {
				result.append("_").append(slc);
			} else {
				result.append(s);
			}
		}
		return result.toString();
	}

	/**
	 * copied from super-class to backwards compatibility for Spring<4.2 
	 * 
	 * @param name the original name
	 * @return the converted name
	 * @since 4.2
	 */
	protected String lowerCaseName(String name) {
		return name.toLowerCase(Locale.US);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.jdbc.core.BeanPropertyRowMapper#initialize(java.lang.Class)
	 */
	@Override
	protected void initialize(Class<T> mappedClass) {
		this.mappedClass = mappedClass;
		this.mappedFields = new HashMap<String, PropertyDescriptor>();
		this.mappedProperties = new HashSet<String>();
		PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(mappedClass);
		for (PropertyDescriptor pd : pds) {
			if (pd.getWriteMethod() != null) {
				this.mappedFields.put(lowerCaseName(pd.getName()), pd);
				String underscoredName = underscoreName(pd.getName());
				if (!lowerCaseName(pd.getName()).equals(underscoredName)) {
					this.mappedFields.put(underscoredName, pd);
				}
				this.mappedProperties.add(pd.getName());
			}
		}
	}

	@Override
	protected void initBeanWrapper(BeanWrapper bw) {
		super.initBeanWrapper(bw);
		if (cs == null) {
			cs = new DefaultFormattingConversionService(true);
		}
		bw.setConversionService(cs);
		bw.setAutoGrowNestedPaths(true);
	}

	@Override
	public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
		Assert.state(this.mappedClass != null, "Mapped class was not specified");
		T mappedObject = BeanUtils.instantiate(this.mappedClass);
		BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);
		initBeanWrapper(bw);

		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Set<String> populatedProperties = (isCheckFullyPopulated() ? new HashSet<String>() : null);

		for (int index = 1; index <= columnCount; index++) {
			String column = JdbcUtils.lookupColumnName(rsmd, index);
			String field = column.replaceAll(" ", "");
			PropertyDescriptor pd = mappedFields.get(lowerCaseName(field));
			if (pd != null) {
				try {
					Object value = getColumnValue(rs, index, pd);
					if (rowNumber == 0 && logger.isDebugEnabled()) {
						logger.debug("Mapping column '" + column + "' to property '" + pd.getName() + "' of type ["
								+ ClassUtils.getQualifiedName(pd.getPropertyType()) + "]");
					}
					try {
						bw.setPropertyValue(pd.getName(), value);
					} catch (TypeMismatchException ex) {
						if (value == null && this.isPrimitivesDefaultedForNullValue()) {
							if (logger.isDebugEnabled()) {
								logger.debug("Intercepted TypeMismatchException for row " + rowNumber + " and column '"
										+ column + "' with null value when setting property '" + pd.getName()
										+ "' of type [" + ClassUtils.getQualifiedName(pd.getPropertyType())
										+ "] on object: " + mappedObject, ex);
							}
						} else {
							throw ex;
						}
					}
					if (populatedProperties != null) {
						populatedProperties.add(pd.getName());
					}
				} catch (NotWritablePropertyException ex) {
					throw new DataRetrievalFailureException(
							"Unable to map column '" + column + "' to property '" + pd.getName() + "'", ex);
				}
			} else if (field.contains(".")) {
				// nest object property
				Object value = JdbcUtils.getResultSetValue(rs, index);
				try {
					String camelCaseName = camelCaseName(field);
					bw.setPropertyValue(camelCaseName, value);
				} catch (TypeMismatchException ex) {
					logger.debug("Could not set property for column " + column);
				}

			} else {
				// No PropertyDescriptor found
				if (rowNumber == 0 && logger.isDebugEnabled()) {
					logger.debug("No property found for column '" + column + "' mapped to field '" + field + "'");
				}
			}
		}

		if (populatedProperties != null && !populatedProperties.equals(this.mappedProperties)) {
			throw new InvalidDataAccessApiUsageException(
					"Given ResultSet does not contain all fields " + "necessary to populate object of class ["
							+ this.mappedClass.getName() + "]: " + this.mappedProperties);
		}

		return mappedObject;
	}

	protected String camelCaseName(String name) {
		if (!StringUtils.hasLength(name)) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		String[] splitted = name.split("_");
		for (String split : splitted) {
			result.append(StringUtils.capitalize(split));
		}
		return StringUtils.uncapitalize(result.toString());
	}

}