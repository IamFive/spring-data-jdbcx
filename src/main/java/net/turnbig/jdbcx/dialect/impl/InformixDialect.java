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

package net.turnbig.jdbcx.dialect.impl;

import java.text.MessageFormat;

import org.springframework.data.domain.Pageable;

import net.turnbig.jdbcx.dialect.SelectSqlUtils;

public class InformixDialect extends AbstractSQLDialect {
	public String getPageSql(String sql) {
		StringBuilder sqlBuilder = new StringBuilder(sql.length() + 40);
		sqlBuilder.append("select skip {1} first {2} * from ({0}) temp_t");
		return sqlBuilder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.woo.jdbcx.dialect.SQLDialect#getPageableSql(java.lang.String, org.springframework.data.domain.Pageable)
	 */
	@Override
	public String getPageableSql(String sql, Pageable pageable) {
		String sortedSql = SelectSqlUtils.addSort(sql, pageable.getSort());
		String pagedSql = "select skip {1} first {2} * from ({0}) temp_t";
		return MessageFormat.format(pagedSql, sortedSql, pageable.getOffset(), pageable.getPageSize());
	}

}