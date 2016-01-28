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

package com.woo.jdbcx.dialect.impl;

import java.text.MessageFormat;

import org.springframework.data.domain.Pageable;

import com.woo.jdbcx.dialect.SelectSqlUtils;

public class Db2Dialect extends AbstractSQLDialect {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.woo.jdbcx.dialect.SQLDialect#getPageableSql(java.lang.String, org.springframework.data.domain.Pageable)
	 */
	@Override
	public String getPageableSql(String sql, Pageable pageable) {
		String sortedSql = SelectSqlUtils.addSort(sql, pageable.getSort());
		int startRow = pageable.getOffset();
		int endRow = pageable.getOffset() + pageable.getPageSize();
		String pagedSql = "select * from ( select tmp_page.*,rownumber() over() as row_id from ({0}) as tmp_page"
				+ ") where row_id between {1} and {2} ";
		return MessageFormat.format(pagedSql, sortedSql, startRow, endRow);
	}

}