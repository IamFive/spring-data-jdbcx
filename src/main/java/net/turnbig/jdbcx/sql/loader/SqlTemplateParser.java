/**
 * @(#)TemplateParser.java 2016年2月2日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.jdbcx.sql.loader;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.turnbig.jdbcx.sql.loader.SqlTemplateLoaderFactory.SqlTemplate;
import net.turnbig.jdbcx.sql.loader.SqlTemplateLoaderFactory.SqlTemplates;

/**
 * @author Woo Cupid
 * @date 2016年2月2日
 * @version $Revision$
 */
public class SqlTemplateParser {

	private static final Logger logger = LoggerFactory.getLogger(SqlTemplateParser.class);

	static JAXBContext ctx = null;

	public static JAXBContext getJaxbContext() {
		if(ctx == null) {
			try {
				ctx = JAXBContext.newInstance(SqlTemplate.class, SqlTemplates.class);
			} catch (JAXBException e) {
				// should not happen
				logger.error("could not create sql templates jaxb context", e);
			}
		}
		
		return ctx;
	}

	public static SqlTemplates fromXML(String xml) {
		try {
			Unmarshaller um = getJaxbContext().createUnmarshaller();
			SqlTemplates unmarshal = (SqlTemplates) um.unmarshal(new StringReader(xml));
			return unmarshal;
		} catch (JAXBException e) {
			throw new RuntimeException("could not parse sql-template-xml", e);
		}
	}

	public static SqlTemplates fromXML(File f) {
		try {
			Unmarshaller um = getJaxbContext().createUnmarshaller();
			SqlTemplates unmarshal = (SqlTemplates) um.unmarshal(f);
			return unmarshal;
		} catch (JAXBException e) {
			throw new RuntimeException("could not parse sql-template-xml", e);
		}
	}

	public static SqlTemplates fromXML(InputStream is) {
		try {
			Unmarshaller um = getJaxbContext().createUnmarshaller();
			SqlTemplates unmarshal = (SqlTemplates) um.unmarshal(is);
			return unmarshal;
		} catch (JAXBException e) {
			throw new RuntimeException("could not parse sql-template-xml", e);
		}
	}

}
