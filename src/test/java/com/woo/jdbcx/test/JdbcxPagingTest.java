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

import java.util.ArrayList;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.woo.jdbcx.Application;
import com.woo.jdbcx.JdbcxPagingDaoSupport;
import com.woo.jdbcx.modal.Member;

/**
 * @author Woo Cupid
 * @date 2016年1月21日
 * @version $Revision$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class JdbcxPagingTest {
	
	private static final Logger logger = LoggerFactory.getLogger(JdbcxPagingTest.class);

	@Autowired
	JdbcxPagingDaoSupport jdbc;

	@Before
	public void init() {
		
		jdbc.update("delete from member", null);
		
		List<Member> list = new ArrayList<Member>();
		for (int i = 1; i <= 200; i++) {
			Member m = new Member();
			m.setName("batched-" + i);
			m.setRegistIp("192.168.1."+ i);
			m.setIsAdmin(true);
			m.setCreatedOn(new Date());
			m.setUpdatedOn(new Date());
			list.add(m);
		}
		
		jdbc.batchUpdate("insert into member (name, regist_ip, created_on, updated_on, is_admin) VALUES "
				+ "(:name, :registIp, :createdOn, :updatedOn, :isAdmin)", list);
	}

	@Test
	public void testQueryPagedListBean() {
		PageRequest pr = new PageRequest(0, 10);
		Page<Member> members = jdbc.queryForListBean("select * from member where name like 'batched%'", Member.class, pr);
		Assert.assertEquals("name like batch get 10 record", members.getSize(), 10);
		Assert.assertEquals("name like batch get 10 record", members.getTotalElements(), 200);
		Assert.assertEquals("name like batch get 10 record", members.getTotalPages(), 20);
		
		
		PageRequest pr2 = new PageRequest(1, 20, new Sort(new Order(Direction.ASC, "id")));
		Page<Map<String, Object>> members2 = jdbc.queryForListMap("select * from member where name like 'batched%'",  pr2);
		Assert.assertEquals("name like batch get 20 record", members2.getSize(), 20);
		Assert.assertEquals("name like batch get 200 total", members2.getTotalElements(), 200);
		Assert.assertEquals("name like batch get 10 pages", members2.getTotalPages(), 10);
		logger.info("{}", members2.getContent());
		
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", "batched%");
		members2 = jdbc.queryForListMap("select * from member where name like :name",  params, pr2);
		Assert.assertEquals("name like batch get 20 record", members2.getSize(), 20);
		Assert.assertEquals("name like batch get 200 total", members2.getTotalElements(), 200);
		Assert.assertEquals("name like batch get 10 pages", members2.getTotalPages(), 10);
		logger.info("{}", members2.getContent());



//		Map<String, String> params = new HashMap<String, String>();
//		params.put("name", "woo");
//		List<Member> members2 = jdbc.queryForListBean("select * from member where name = :name", params, Member.class);
//		Assert.assertEquals("name with woo only 1 record", members2.size(), 1);
//
//		Member member = members.get(0);
//		Assert.assertEquals("ip should be 127.0.0.1", member.getRegistIp(), "127.0.0.1");
	}

}
