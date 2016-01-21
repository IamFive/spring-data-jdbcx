/**
 * @(#)Configurations.java 2016年1月21日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package com.woo.jdbcx.configs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;

/**
 * @author Woo Cupid
 * @date 2016年1月21日
 * @version $Revision$
 */
@Component
public class Configurations {

	@Autowired
	private DataSourceProperties properties;


	@Bean
	@ConfigurationProperties(prefix = DataSourceProperties.PREFIX)
	public DataSource dataSourceSpied() {
		DataSourceBuilder factory = DataSourceBuilder.create(this.properties.getClassLoader())
				.driverClassName(this.properties.getDriverClassName()).url(this.properties.getUrl())
				.username(this.properties.getUsername()).password(this.properties.getPassword());
		return new DataSourceSpy(factory.build());
		// return factory.build();
	}



}
