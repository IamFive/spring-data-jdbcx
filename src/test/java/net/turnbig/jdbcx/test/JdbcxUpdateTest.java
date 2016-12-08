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

package net.turnbig.jdbcx.test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.turnbig.jdbcx.H2Env;
import net.turnbig.jdbcx.JdbcxPagingDaoSupport;
import net.turnbig.jdbcx.modal.Member;

/**
 * @author Woo Cupid
 * @date 2016年1月21日
 * @version $Revision$
 */
public class JdbcxUpdateTest extends H2Env {


	@Autowired
	JdbcxPagingDaoSupport jdbc;


	@Test
	public void testUpdate() {
		Member m = new Member();
		m.setName("wooooow");
		m.setId(1);
		m.setRegistIp("192.168.1.110");
		int update = jdbc.update("update member set name = :name, regist_ip = :registIp where id = :id", m);
		Assert.assertEquals(update, 1);

		Member member = jdbc.queryForBean("select name, regist_ip from member where id = 1", Member.class);
		Assert.assertEquals(member.getName(), "wooooow");
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

		List<String> nameList = jdbc.queryForList("select name from member where id <= 2", String.class);
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
						+ "(:name, :registIp, :createdOn, :updatedOn, :isAdmin)", Lists.newArrayList(m1, m2));
		Assert.assertArrayEquals(batchUpdate, new int[] { 1, 1 });

		List<String> nameList = jdbc.queryForList("select name from member order by id asc", String.class);
		Assert.assertArrayEquals(nameList.toArray(), new String[] { "woo", "Five", "insert-name-1", "insert-name-2" });
	}

}
