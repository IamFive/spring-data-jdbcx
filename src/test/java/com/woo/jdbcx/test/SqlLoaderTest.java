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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.woo.jdbcx.Application;
import com.woo.jdbcx.JdbcxPagingDaoSupportImpl;
import com.woo.jdbcx.modal.Member;
import com.woo.jdbcx.sql.loader.SqlLoader;

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

	@Autowired
	SqlLoader sqlLoader;

	@Autowired
	JdbcxPagingDaoSupportImpl jdbcx;

	@Test
	public void loadSqlTest() throws TemplateNotFoundException, MalformedTemplateNameException, ParseException,
			IOException, TemplateException {
		Template template = cfg.getTemplate("member.select.all.columns");
		StringWriter sw = new StringWriter();
		template.process(new Object(), sw);
		logger.info("{}", sw);
	}

	@Test
	public void loadSql2Test() {
		String sql = sqlLoader.getSql("member.select.all.columns");
		logger.info(sql);

		Map<String, String> context = new HashMap<String, String>();
		context.put("id", "1");
		String sql2 = sqlLoader.getSql("member.select.all.columns", context);
		logger.info(sql2);
	}

	@Test
	public void queryWithSqlLoaderTest() {
		String sql = sqlLoader.getSql("member.query.createon.after");
		logger.info("query sql is: {}", sql);

		Map<String, Object> context = new HashMap<String, Object>();
		context.put("createdOn", new Date());
		List<Member> members = jdbcx.queryForListBean(sql, context, Member.class);
		logger.info("result is : {}", members);
	}

}
