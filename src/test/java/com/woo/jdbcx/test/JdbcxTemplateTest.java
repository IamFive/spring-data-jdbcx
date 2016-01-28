/*******************************************************************************
 *
 * Copyright 2014-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.woo.jdbcx.test;

import java.util.Date;
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
import com.woo.jdbcx.JdbcxPagingDaoSupportImpl;
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
	JdbcxPagingDaoSupportImpl jdbc;

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

	@Test
	public void testBatchUpdate() {
		Map<String, Object> record1 = new HashMap<String, Object>();
		record1.put("id", 1);
		record1.put("name", "woo2");

		Map<String, Object> record2 = new HashMap<String, Object>();
		record2.put("id", 2);
		record2.put("name", "Five2");

		int[] batchUpdate = jdbc.batchUpdate("update member set name = :name where id = :id", record1, record2);
		Assert.assertArrayEquals(batchUpdate, new int[] { 1, 1 });

		List<String> nameList = jdbc.queryForList("select name from member", String.class);
		Assert.assertArrayEquals(nameList.toArray(), new String[] { "woo2", "Five2" });
	}

	@Test
	public void testBatchInsert() {
		Member m1 = new Member();
		m1.setName("insert-name-1");
		m1.setRegistIp("192.168.1.100");
		m1.setIsAdmin(true);
		m1.setCreatedOn(new Date());
		m1.setUpdatedOn(new Date());

		Member m2 = new Member();
		m2.setName("insert-name-2");
		m2.setRegistIp("192.168.1.101");
		m2.setIsAdmin(false);
		m2.setCreatedOn(new Date());
		m2.setUpdatedOn(new Date());

		int[] batchUpdate = jdbc
				.batchUpdate("insert into member (name, regist_ip, created_on, updated_on, is_admin) VALUES "
						+ "(:name, :registIp, :createdOn, :updatedOn, :isAdmin)", m1, m2);
		Assert.assertArrayEquals(batchUpdate, new int[] { 1, 1 });

		List<String> nameList = jdbc.queryForList("select name from member order by id asc", String.class);
		Assert.assertArrayEquals(nameList.toArray(), new String[] { "woo", "Five", "insert-name-1", "insert-name-2" });
	}

}
