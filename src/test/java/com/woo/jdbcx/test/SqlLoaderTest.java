/**
 * @(#)PageableTest.java 2016年1月28日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package com.woo.jdbcx.test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.woo.jdbcx.H2Env;
import com.woo.jdbcx.JdbcxPagingDaoSupport;
import com.woo.jdbcx.modal.Member;
import com.woo.jdbcx.sql.loader.SqlLoader;

/**
 * @author Woo Cupid
 * @date 2016年1月28日
 * @version $Revision$
 */
public class SqlLoaderTest extends H2Env {

	private static final Logger logger = LoggerFactory.getLogger(SqlLoaderTest.class);

	@Autowired
	SqlLoader sqlLoader;

	@Autowired
	JdbcxPagingDaoSupport jdbcx;

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
