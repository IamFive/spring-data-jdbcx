/**
 * @(#)PGObjectToMapConverter.java 2016年2月2日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package com.woo.jdbcx.convertor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.postgresql.util.PGobject;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import com.woo.jdbcx.utilities.JsonMapper;

/**
 * convert PostGreSql PGObject to Java objects
 * 
 * @author Woo Cupid
 * @date 2016年2月2日
 * @version $Revision$
 */
public class PGObjectConverter implements ConditionalGenericConverter {

	private ConversionService conversionService;

	public PGObjectConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	/* (non-Javadoc)
	 * @see org.springframework.core.convert.converter.GenericConverter#getConvertibleTypes()
	 */
	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		Set<ConvertiblePair> convertables = new HashSet<ConvertiblePair>();
		convertables.add(new ConvertiblePair(PGobject.class, Map.class));
		convertables.add(new ConvertiblePair(PGobject.class, List.class));
		return convertables;
	}

	/* (non-Javadoc)
	 * @see org.springframework.core.convert.converter.GenericConverter#convert(java.lang.Object, org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor)
	 */
	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (source != null) {
			if (source instanceof PGobject) {
				PGobject pgobject = (PGobject) source;
				String type = pgobject.getType();
				String value = pgobject.getValue();
				if ("jsonb".equals(type) || "json".equals(type)) {
					ResolvableType resolvableType = targetType.getResolvableType();
					if (resolvableType.getRawClass().isAssignableFrom(Map.class)) {
						HashMap<String, Object> mapBean = JsonMapper.nonEmptyMapper().getMapBean(value, String.class,
								Object.class);
						return mapBean;
					} else if (resolvableType.getRawClass().isAssignableFrom(List.class)) {
						List<Object> mapBean = JsonMapper.nonEmptyMapper().getListBean(value, Object.class);
						return mapBean;
					} else {
						Object bean = JsonMapper.nonEmptyMapper().getBean(value, resolvableType.getRawClass());
						return bean;
					}
				} else if ("array".equals(type)) {
					//TODO
				}
			}
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see org.springframework.core.convert.converter.ConditionalConverter#matches(org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor)
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
