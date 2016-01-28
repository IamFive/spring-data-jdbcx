/**
 * @(#)JSQLParserTest.java 2016年1月25日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package com.woo.jdbcx.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;

/**
 * @author Woo Cupid
 * @date 2016年1月25日
 * @version $Revision$
 */
public class JSQLParserTest {
	
	private static final Logger logger = LoggerFactory.getLogger(JSQLParserTest.class);

	@Test
	public void findOrderByTest() throws JSQLParserException {
		String sql = "select * from member order by id asc, name desc";
		Select parsed = (Select) CCJSqlParserUtil.parse(sql);
		SelectBody selectBody = parsed.getSelectBody();
	}



}
