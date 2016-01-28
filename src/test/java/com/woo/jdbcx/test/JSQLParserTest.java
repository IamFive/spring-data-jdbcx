/**
 * @(#)JSQLParserTest.java 2016年1月25日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package com.woo.jdbcx.test;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;

/**
 * @author Woo Cupid
 * @date 2016年1月25日
 * @version $Revision$
 */
public class JSQLParserTest {
	
	private static final Logger _logger = LoggerFactory.getLogger(JSQLParserTest.class);
	
	@Test
	public void findOrderByTest() throws JSQLParserException {
		String sql = "select * from member order by id asc, name desc";
		Select smt1 = (Select) CCJSqlParserUtil.parse(sql);
		SelectBody select = smt1.getSelectBody();
		
		String sql2 = "with temp as (select * from member where id > 3) select * from member where id = temp.id";
		Statement smt2 = CCJSqlParserUtil.parse(sql2);
		
		String sql3 = "with temp as (select * from member where id > 3)";
		Statement statement3 = CCJSqlParserUtil.parse(sql3);
		Assert.assertTrue("with is a select", !(statement3 instanceof Select) );
		
	}



}
