/*******************************************************************************
 *
 * Copyright 2014-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.woo.jdbcx.configs;

import java.io.IOException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.stereotype.Component;

import com.woo.jdbcx.convertor.PGObjectConverter;
import com.woo.jdbcx.sql.loader.SqlTemplateLoaderFactory;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;

/**
 * @author Woo Cupid
 * @date 2016年1月21日
 * @version $Revision$
 */
@Component
public class Configurations {

	@org.springframework.context.annotation.Configuration
	public static class SpiedDatasourceConfig {

		@Autowired
		private DataSourceProperties properties;

		@Bean
		public DataSource dataSourceSpied() {
			DataSourceBuilder factory = DataSourceBuilder.create(this.properties.getClassLoader())
					.driverClassName(this.properties.getDriverClassName()).url(this.properties.getUrl())
					.username(this.properties.getUsername()).password(this.properties.getPassword());
			return new DataSourceSpy(factory.build());
			// return factory.build();
		}
	}


	@ConfigurationProperties(prefix = "spring.jdbcx.sql")
	@org.springframework.context.annotation.Configuration
	public static class FreeMarkerSqlTemplateLoader {

		String templatePath;

		@Bean(name = "sql-template-loader")
		public TemplateLoader sqlTemplateConfiguration() throws IOException {
			SqlTemplateLoaderFactory sqlTemplateFactory = new SqlTemplateLoaderFactory();
			sqlTemplateFactory.setLocations(new String[] { templatePath });
			return sqlTemplateFactory.createSqlTemplateLoader();
		}

		public void setTemplatePath(String templatePath) {
			this.templatePath = templatePath;
		}

	}

	@org.springframework.context.annotation.Configuration
	public static class FreeMarkerConfiguration {

		@Resource(name = "sql-template-loader")
		protected TemplateLoader sqlTemplateLoader;

		@Bean
		protected Configuration buildFreemarkerConfiguration() {
			Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
			configuration.setTemplateLoader(sqlTemplateLoader);
			configuration.setTemplateUpdateDelayMilliseconds(0);
			configuration.setDefaultEncoding("UTF-8");
			return configuration;
		}
	}

	@org.springframework.context.annotation.Configuration
	public static class ConversionServiceConfig {

		@Bean
		protected ConversionService buildConversionService() {
			DefaultFormattingConversionService cs = new DefaultFormattingConversionService(true);
			//			cs.addConverter(String.class, Date.class, new StringToDateConverter());
			//			cs.addConverter(String.class, Timestamp.class, new StringToDateConverter());
			cs.addConverter(new PGObjectConverter(cs));
			return cs;
		}
	}

}
