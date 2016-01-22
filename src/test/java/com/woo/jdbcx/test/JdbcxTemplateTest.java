/**
 * @(#)JdbcxTemplateTest.java 2016年1月21日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package com.woo.jdbcx.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
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
		sample.setName("woo");
		List<Member> members = jdbc.queryForListBean("select * from member where name = :name", sample, Member.class);
		Assert.assertEquals("name with woo only 1 record", members.size(), 1);

		Map<String, String> params = new HashMap<String, String>();
		params.put("name", "woo");
		List<Member> members2 = jdbc.queryForListBean("select * from member where name = :name", params, Member.class);
		Assert.assertEquals("name with woo only 1 record", members2.size(), 1);

		Member member = members.get(0);
		Assert.assertEquals("ip should be 127.0.0.1", member.getRegistIp(), "127.0.0.1");
	}

	@Test
	public void testQueryListMap() {
		List<Map<String, Object>> members = jdbc.queryForListMap("select * from member order by id asc");
		Assert.assertEquals("total records is 2", members.size(), 2);
		Map<String, Object> member = members.get(0);
		Assert.assertEquals("ip should be 127.0.0.1", member.get("name"), "woo");
	}

	@Test
	public void testQueryPrimitiveObject() {
		Integer count = jdbc.queryForObject("select count(0) from member", Integer.class);
		Assert.assertEquals("total records is 2", count.intValue(), 2);

		Long countLong = jdbc.queryForObject("select count(0) from member", Long.class);
		Assert.assertEquals("total records is 2", countLong.intValue(), 2);

		Member m = new Member();
		m.setName("woo");
		Boolean isAdmin = jdbc.queryForObject("select is_admin from member where name = :name", m, Boolean.class);
		Assert.assertTrue(isAdmin);

	}

	@Test
	public void testUpdate() {
		Member m = new Member();
		m.setName("woo2");
		m.setId(1);
		m.setRegistIp("192.168.1.110");
		int update = jdbc.update("update member set name = :name, regist_ip = :registIp where id = :id", m);
		Assert.assertEquals(update, 1);
		
		Member member = jdbc.queryForBean("select name, regist_ip from member where id = 1", Member.class);
		Assert.assertEquals(member.getName(), "woo2");
		Assert.assertEquals(member.getRegistIp(), "192.168.1.110");
		Assert.assertNull(member.getId());
	}

}
