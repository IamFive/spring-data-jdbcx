/**
 * @(#)JdbcxTemplateTest.java 2016年1月21日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package com.woo.jdbcx.test;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.woo.jdbcx.Application;
import com.woo.jdbcx.JdbcxDaoSupportImpl;
import com.woo.jdbcx.modal.Member;

/**
 * @author Woo Cupid
 * @date 2016年1月21日
 * @version $Revision$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class JdbcxTemplateTest {

	private static final Logger logger = LoggerFactory.getLogger(JdbcxTemplateTest.class);

	@Autowired
	JdbcxDaoSupportImpl jdbc;

	@Before
	public void initdb() {

	}

	@Test
	public void testQueryListBean() {
		Member sample = new Member();
		sample.setName("lol2");
		List<Member> members = jdbc.queryForListBean("select * from member where name = :name", sample, Member.class);

	}
}
