/**
 * @(#)JSQLParserTest.java 2016年1月25日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package com.woo.jdbcx.test;

import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import com.woo.jdbcx.dialect.SelectSqlUtils;

import net.sf.jsqlparser.JSQLParserException;

/**
 * @author Woo Cupid
 * @date 2016年1月25日
 * @version $Revision$
 */
public class SelectSqlUtilsTest {
	
	private static final Logger logger = LoggerFactory.getLogger(SelectSqlUtilsTest.class);

	@Test
	public void plainSelectAddOrderByTest() throws JSQLParserException {
		String sql = "select * from member order by id asc, name desc limit 1 offset 2";
		String handledSql = SelectSqlUtils.addSort(sql,
				new Sort(Lists.newArrayList(new Order(Direction.DESC, "age"), new Order(Direction.ASC, "weight"))));
		String handledSql2 = SelectSqlUtils.addSort(sql, "age desc", "weight asc");
		Assert.assertEquals(handledSql.toLowerCase(),
				"select * from member order by id asc, name desc, age DESC, weight ASC limit 1 offset 2".toLowerCase());
		Assert.assertEquals(handledSql2.toLowerCase(),
				"select * from member order by id asc, name desc, age DESC, weight ASC limit 1 offset 2".toLowerCase());
		logger.info("new sql: {}", handledSql);
	}

	@Test
	public void setSelectAddOrderByTest() throws JSQLParserException {
		String sql = "(select * from member) union all (select * from member2 order by name desc) order by id asc, name desc limit 1 offset 2";
		String handledSql = SelectSqlUtils.addSort(sql,
				new Sort(Lists.newArrayList(new Order(Direction.DESC, "age"), new Order(Direction.ASC, "weight"))));
		String handledSql2 = SelectSqlUtils.addSort(sql, "age desc", "weight asc");
		Assert.assertEquals(handledSql.toLowerCase(),
				"(select * from member) union all (select * from member2 order by name desc) order by id asc, name desc, age DESC, weight ASC limit 1 offset 2"
						.toLowerCase());
		Assert.assertEquals(handledSql2.toLowerCase(),
				"(select * from member) union all (select * from member2 order by name desc) order by id asc, name desc, age DESC, weight ASC limit 1 offset 2"
						.toLowerCase());
	}

	@Test
	public void getCountSqlTest() throws JSQLParserException {
		String sql = "with a as (select * from level limit 10) select name, count(0) from member, a where id > 10 and level = a.level group by name";
		String countSql = SelectSqlUtils.getCountSql(sql);
		logger.info("{}", countSql);

		String sql1 = "select name from member where id > 10 and level = a.level";
		String countSql1 = SelectSqlUtils.getCountSql(sql1);
		logger.info("{}", countSql1);
	}

}
