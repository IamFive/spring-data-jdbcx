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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.woo.jdbcx.H2Env;
import com.woo.jdbcx.modal.Member;
import com.woo.jdbcx.service.MemberService;
import com.woo.qb.segment.SegmentFactory;
import com.woo.qb.segment.SqlSegment;
import com.woo.qb.segment.impl.combined.CombinedSqlSegment;


/**
 * @author Woo Cupid
 * @date 2016年1月21日
 * @version $Revision$
 */
public class DJdbcxServiceTest extends H2Env {


	@Autowired
	MemberService memberService;

	@Test
	public void testFindNamedSqlSegment() {
		SegmentFactory $ = SegmentFactory.namedQuery();
		CombinedSqlSegment ss = $.and($.eq("id", 1), $.eq("regist_ip", "127.0.0.1"));
		Member member = memberService.findByNamedSqlSegment(ss);
		Assert.assertEquals(member.getName(), "woo");
	}

	@Test
	public void testFindListByNamedSqlSegment() {
		SegmentFactory $ = SegmentFactory.namedQuery();
		SqlSegment ss = $.eq("regist_ip", "127.0.0.1");
		List<Member> member = memberService.findListByNamedSqlSegment(ss);
		Assert.assertEquals(member.size(), 1);
	}

}
