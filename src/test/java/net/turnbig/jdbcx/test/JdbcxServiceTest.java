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

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.turnbig.jdbcx.H2Env;
import net.turnbig.jdbcx.JdbcxService.FieldValue;
import net.turnbig.jdbcx.modal.Member;
import net.turnbig.jdbcx.service.MemberService;


/**
 * @author Woo Cupid
 * @date 2016年1月21日
 * @version $Revision$
 */
public class JdbcxServiceTest extends H2Env {


	@Autowired
	MemberService memberService;

	@Test
	public void testGetById() {
		Member member = memberService.get(1);
		Assert.assertEquals(member.getName(), "woo");
	}

	@Test
	public void testDelById() {
		int count = memberService.delete(2);
		Assert.assertEquals(count, 1);
	}

	@Test
	public void testFindByField() {
		Member member = memberService.findByFields(FieldValue.of("id", 1), FieldValue.of("regist_ip", "127.0.0.1"));
		Assert.assertEquals(member.getName(), "woo");
	}

	@Test
	public void testCountByField() {
		Integer count = memberService.countByFields(FieldValue.of("id", 1), FieldValue.of("regist_ip", "127.0.0.1"));
		Assert.assertEquals(count.intValue(), 1);

		Integer count2 = memberService.countByFields(FieldValue.of("id", 0), FieldValue.of("regist_ip", "127.0.0.1"));
		Assert.assertEquals(count2.intValue(), 0);
	}
}
