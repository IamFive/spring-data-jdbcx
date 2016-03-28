/**
 * @(#)AA.java 2014年2月21日
 *
 * Copyright 2008-2014 by Woo Cupid.
 * All rights reserved.
 * 
 */
package com.woo.jdbcx.convertor;

import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.GenericConversionService;

/**
 * @author Woo Cupid
 * @date 2014年2月21日
 * @version $Revision$
 */
public class PGConversionServiceFactoryBean extends ConversionServiceFactoryBean {

	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		GenericConversionService registry = (GenericConversionService) this.getObject();
		registry.addConverter(new StringToDateConverter());
		registry.addConverter((GenericConverter) new PGObjectConverter(this.getObject()));
	}
}
