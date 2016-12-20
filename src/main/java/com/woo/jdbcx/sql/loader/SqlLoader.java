package com.woo.jdbcx.sql.loader;

import java.io.IOException;
import java.io.StringWriter;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.woo.jdbcx.sql.loader.SqlTemplateLoaderFactory.SqlTemplateLoader;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 
 * To use sql-loader, you need to initial a Freemarker configuration first.
 * For spring-boot style {@link com.woo.jdbcx.configs.Configurations.FreeMarkerConfiguration}
 * 
 * For spring-xml-cfg style, you can share an exists freemarker which is used by springmvc or other.
 * 
<pre>
<bean id="xmlTemplate" class="com.woo.jdbcx.sql.loader.SqlTemplateLoaderFactory.SqlTemplateLoader" >
	<property name="locations">
		<list>
			<value>classpath:/templates/</value>
			<value>classpath:/template2/sample.xml</value>
		</list>
	</property>
</bean>

<bean id="freemarkerConfigurer" class="org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean" 
	lazy-init="false">
	<property name="preTemplateLoaders">
		<list>
			<ref bean="xmlTemplate" />
		</list>
	</property>
	<property name="defaultEncoding" value="UTF-8" />
	<property name="freemarkerSettings">
		<props>
			<prop key="template_update_delay">0</prop>
		</props>
	</property>
</bean>
</pre>

 * 
 * @author Woo Cupid
 * @date 2016年2月2日
 * @version $Revision$
 */
@Component
@ConfigurationProperties(prefix = "spring.jdbcx.sql")
public class SqlLoader {

	private Logger logger = LoggerFactory.getLogger(SqlLoader.class);

	String[] templatePath;
	String templateEncoding = "UTF-8";
	Long updateDelay = 5000L;

	private Configuration configuration;

	@PostConstruct
	public void initConfiguration() throws IOException {
		if (templatePath == null || templatePath.length == 0) {
			throw new RuntimeException("no sql template path has been set");
		}

		// build template loader
		SqlTemplateLoaderFactory sqlTemplateFactory = new SqlTemplateLoaderFactory();
		sqlTemplateFactory.setLocations(templatePath);
		SqlTemplateLoader sqlTemplateLoader = sqlTemplateFactory.createSqlTemplateLoader();

		// build configuration
		Configuration configuration = new Configuration(Configuration.getVersion());
		configuration.setTemplateLoader(sqlTemplateLoader);
		configuration.setTemplateUpdateDelayMilliseconds(updateDelay);
		configuration.setDefaultEncoding(templateEncoding);
		this.configuration = configuration;
	}

	private Template getTemplate(String name) {
		try {
			return configuration.getTemplate(name);
		} catch (IOException e) {
			logger.error("Can not get freemarker template resource", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * process template to string
	 * 
	 * @param template
	 * @param model
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public String processTpl(Template template, Object model) throws IOException, TemplateException {
		StringWriter result = new StringWriter();
		template.process(model, result);
		return result.toString();
	}

	/**
	 * 
	 * get the SQL which is a plain-text SQL
	 * 
	 * @param sqlTplName
	 * @return
	 */
	public String getSql(String sqlTplName) {
		try {
			Template template = getTemplate(sqlTplName);
			String result = processTpl(template, null);
			return result.trim();
		} catch (IOException e) {
			logger.error("Can not get freemarker template resource", e);
			throw new RuntimeException(e);
		} catch (TemplateException e) {
			logger.error("There got a grammar error in freemarker template " + sqlTplName, e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * get the processed SQL with model as context
	 * 
	 * @param sqlTplName
	 * @param model
	 * @return
	 */
	public String getSql(String sqlTplName, Object model) {
		try {
			Template template = getTemplate(sqlTplName);
			return processTpl(template, model);
		} catch (IOException e) {
			logger.error("Can not get freemarker template resource", e);
			throw new RuntimeException(e);
		} catch (TemplateException e) {
			logger.error("There got a grammar error in freemarker template " + sqlTplName, e);
			throw new RuntimeException(e);
		}
	}

	public void setTemplateEncoding(String templateEncoding) {
		this.templateEncoding = templateEncoding;
	}

	public void setUpdateDelay(Long updateDelay) {
		this.updateDelay = updateDelay;
	}

	public void setTemplatePath(String[] templatePath) {
		this.templatePath = templatePath;
	}

}
