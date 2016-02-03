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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.woo.jdbcx.Application;
import com.woo.jdbcx.JdbcxPagingDaoSupportImpl;
import com.woo.jdbcx.modal.Member;
import com.woo.jdbcx.sql.loader.SqlLoader;

/**
 * @author Woo Cupid
 * @date 2016年1月21日
 * @version $Revision$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class JdbcxInsertTest {


	@Autowired
	JdbcxPagingDaoSupportImpl jdbc;

	@Autowired
	SqlLoader sqlLoader;



	@Test
	public void insertAndReturnKeyTest() {
		Member m = new Member();
		m.setName("hahaha");
		m.setRegistIp("192.168.1.12");
		m.setIsAdmin(true);
		m.setCreatedOn(new Date());
		m.setUpdatedOn(new Date());

		String sql = sqlLoader.getSql("member.insert.new");
		KeyHolder keyHolder = jdbc.insert(sql, m);
		
		Member member = jdbc.queryForBean("select * from member where name = 'hahaha'", Member.class);
		Assert.assertEquals(keyHolder.getKey().intValue(), member.getId().intValue());
	}

}
