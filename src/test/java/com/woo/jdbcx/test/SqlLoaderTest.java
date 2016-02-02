/**
 * @(#)PageableTest.java 2016年1月28日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package com.woo.jdbcx.test;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.woo.jdbcx.Application;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

/**
 * @author Woo Cupid
 * @date 2016年1月28日
 * @version $Revision$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class SqlLoaderTest {

	private static final Logger logger = LoggerFactory.getLogger(SqlLoaderTest.class);

	@Autowired
	Configuration cfg;

	@Test
	public void loadSqlTest() throws TemplateNotFoundException, MalformedTemplateNameException, ParseException,
			IOException, TemplateException {
		Template template = cfg.getTemplate("member.select.all.columns");
		StringWriter sw = new StringWriter();
		template.process(new Object(), sw);
		logger.info("{}", sw);
	}

}
