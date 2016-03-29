/**
 * @(#)PGObjectToMapConverter.java 2016年2月2日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package com.woo.jdbcx.convertor;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

/**
 * convert PostGreSql PGObject to Java objects
 * 
 * @author Woo Cupid
 * @date 2016年2月2日
 * @version $Revision$
 */
public class PGArrayConverter implements ConditionalGenericConverter {

	private static final Logger logger = LoggerFactory.getLogger(PGArrayConverter.class);

	private ConversionService conversionService;

	public PGArrayConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.core.convert.converter.GenericConverter#getConvertibleTypes()
	 */
	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		Set<ConvertiblePair> convertables = new HashSet<ConvertiblePair>();
		convertables.add(new ConvertiblePair(java.sql.Array.class, List.class));
		convertables.add(new ConvertiblePair(java.sql.Array.class, Object[].class));
		return convertables;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.core.convert.converter.GenericConverter#convert(java.lang.Object,
	 * org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor)
	 */
	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source != null) {
			if (source instanceof java.sql.Array) {
				java.sql.Array jdbc4Array = (java.sql.Array) source;
				try {
					// Type type = targetType.getResolvableType().getGeneric(0).getType();
					ResolvableType resolvableType = targetType.getResolvableType();
					Object[] array = (Object[]) jdbc4Array.getArray();
					if (resolvableType.getRawClass().isAssignableFrom(List.class)) {
						List<Object> asList = Arrays.asList(array);
						return asList;
					} else {
						Object target = Array.newInstance(targetType.getElementTypeDescriptor().getType(),
								array.length);
						for (int i = 0; i < array.length; i++) {
							Object object = array[i];
							Array.set(target, i, object);
						}
						return target;
					}
				} catch (SQLException e) {
					logger.error("Could not convert jdbc4 array to List<?>", e);
				}
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.core.convert.converter.ConditionalConverter#matches(org.springframework.core.convert.
	 * TypeDescriptor, org.springframework.core.convert.TypeDescriptor)
	 */
	@Override
	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return canConvertElements(sourceType.getElementTypeDescriptor(), targetType.getElementTypeDescriptor(),
				this.conversionService);
	}

	public static boolean canConvertElements(TypeDescriptor sourceElementType, TypeDescriptor targetElementType,
			ConversionService conversionService) {
		if (targetElementType == null) {
			// yes
			return true;
		}
		if (sourceElementType == null) {
			// maybe
			return true;
		}
		if (conversionService.canConvert(sourceElementType, targetElementType)) {
			// yes
			return true;
		} else if (sourceElementType.getType().isAssignableFrom(targetElementType.getType())) {
			// maybe;
			return true;
		} else {
			// no;
			return false;
		}
	}

}
