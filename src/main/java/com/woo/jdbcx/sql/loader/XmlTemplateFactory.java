package com.woo.jdbcx.sql.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.woo.jdbcx.sql.loader.XmlTemplateFactory.XmlTemplateLoader;

import freemarker.cache.StringTemplateLoader;

/**
 * 

<h3>xml based template factory for freemarker</h3>

spring xml configuration sample:
<pre>
<bean id="xmlTemplate" class="studio.five.lol.base.freemarker.XmlTemplateFactory" >
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
 */
public class XmlTemplateFactory implements FactoryBean<XmlTemplateLoader>, InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(XmlTemplateFactory.class);

	private ResourceLoader resourceLoader = new DefaultResourceLoader();
	private String[] locations;

	private XmlTemplateLoader templateLoader = new XmlTemplateLoader();

	@Override
	public void afterPropertiesSet() throws Exception {
		for (String path : locations) {
			loadTemplates(path);
		}
	}

	/**
	 * load templates from a special path,
	 * 
	 * <li>classpath:templates/template1.xml</li>
	 * <li>classpath:templates/</li>
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void loadTemplates(String path) throws IOException {
		Resource r = resourceLoader.getResource(path);
		if (r.exists()) {
			List<XmlTemplate> templates = new ArrayList<XmlTemplate>();
			try {
				logger.info("load xml freemarker template from : {}", r.getFile().getAbsolutePath());
				templates = parseTemplate(r.getFile());
			} catch (Exception e) {
				InputStream is = r.getInputStream();
				templates = parseTemplate(is);
			}

			for (XmlTemplate xmlTemplate : templates) {
				templateLoader.putTemplate(xmlTemplate.getName(), xmlTemplate.getTemplate(), xmlTemplate.getLastModified());
				templateLoader.addMapper(templateLoader.findTemplateSource(xmlTemplate.getName()), xmlTemplate.getTplFilePath());
			}
		}
	}

	/**
	 * when template file is in JAR-File, we can't get the File directly
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static List<XmlTemplate> parseTemplate(InputStream is) throws IOException {
		String content = IOUtils.toString(is);
		List<XmlTemplate> templates = XmlHelper.fromXML(content, XmlTemplate.class);
		for (XmlTemplate xmlTemplate : templates) {
			xmlTemplate.setLastModified(new Date().getTime());
			xmlTemplate.setTplFilePath("");
		}
		return templates;
	}

	public static List<XmlTemplate> parseTemplate(File file) {
		List<XmlTemplate> result = new ArrayList<XmlTemplate>();
		if (file.isFile()) {
			List<XmlTemplate> templates = XmlHelper.fromXML(file, XmlTemplate.class);
			for (XmlTemplate xmlTemplate : templates) {
				xmlTemplate.setLastModified(file.lastModified());
				xmlTemplate.setTplFilePath(file.getAbsolutePath());
			}
			result.addAll(templates);
		} else if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				result.addAll(parseTemplate(f));
			}
		}

		return result;
	}

	@Override
	public XmlTemplateLoader getObject() throws Exception {
		return templateLoader;
	}

	@Override
	public Class<XmlTemplateLoader> getObjectType() {
		return XmlTemplateLoader.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public String[] getLocations() {
		return locations;
	}

	public void setLocations(String[] locations) {
		this.locations = locations;
	}

	public XmlTemplateLoader getTemplateLoader() {
		return templateLoader;
	}

	public void setTemplateLoader(XmlTemplateLoader templateLoader) {
		this.templateLoader = templateLoader;
	}

	public static class XmlTemplateLoader extends StringTemplateLoader {

		private HashMap<Object, String> resourceMapper = new HashMap<Object, String>();

		/*
		 * (non-Javadoc)
		 * 
		 * @see freemarker.cache.StringTemplateLoader#findTemplateSource(java.lang.String)
		 */
		@Override
		public Object findTemplateSource(String name) {
			// reload template
			Object stringTemplateSource = super.findTemplateSource(name);
			if (stringTemplateSource != null && resourceMapper.containsKey(stringTemplateSource)) {
				String path = resourceMapper.get(stringTemplateSource);
				List<XmlTemplate> tpls = parseTemplate(new File(path));
				for (XmlTemplate xmlTemplate : tpls) {
					putTemplate(xmlTemplate.getName(), xmlTemplate.getTemplate(), xmlTemplate.getLastModified());
					addMapper(super.findTemplateSource(name), xmlTemplate.getTplFilePath());
				}
			}
			return super.findTemplateSource(name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemarker.cache.StringTemplateLoader#getLastModified(java.lang.Object
		 * )
		 */
		@Override
		public long getLastModified(Object templateSource) {
			String path = resourceMapper.get(templateSource);
			File f = new File(path);
			return f.lastModified();
		}

		public void addMapper(Object object, String path) {
			this.resourceMapper.put(object, path);
		}

	}

	@XStreamAlias(value = "XmlTemplate")
	public static class XmlTemplate {

		private String name;
		private String template;
		private long lastModified;
		private String tplFilePath;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getTemplate() {
			return template;
		}

		public void setTemplate(String template) {
			this.template = template;
		}

		public long getLastModified() {
			return lastModified;
		}

		public void setLastModified(long lastModified) {
			this.lastModified = lastModified;
		}

		@Override
		public String toString() {
			return "XmlTemplate [name=" + name + ", template=" + template + ", lastModified=" + lastModified + "]";
		}

		/**
		 * @return the tplFilePath
		 */
		public String getTplFilePath() {
			return tplFilePath;
		}

		/**
		 * @param tplFilePath
		 *            the tplFilePath to set
		 */
		public void setTplFilePath(String tplFilePath) {
			this.tplFilePath = tplFilePath;
		}

	}
}
