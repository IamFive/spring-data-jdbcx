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

/**
 * 

Oracle before 12c, the pagination SQL sample:
<pre>
SELECT * FROM (
    SELECT rownum rnum, a.* 
    FROM(
        SELECT fieldA,fieldB 
        FROM table 
        ORDER BY fieldA 
    ) a 
    WHERE rownum <=5+14
)
WHERE rnum >=5
</pre>


Oracle 12c, sql Sample:
<pre>
SELECT fieldA,fieldB 
FROM table 
ORDER BY fieldA 
OFFSET 5 ROWS FETCH NEXT 14 ROWS ONLY;
</pre>

 * @author Woo Cupid
 * @date 2016年1月28日
 * @version $Revision$
 */
public class OracleDialect extends AbstractSQLDialect {

	/*
	 * 
	 * @see com.woo.jdbcx.dialect.SQLDialect#getPageableSql(java.lang.String, org.springframework.data.domain.Pageable)
	 */
	@Override
	public String getPageableSql(String sql, Pageable pageable) {
		String sortedSql = SelectSqlUtils.addSort(sql, pageable.getSort());
		int startRow = pageable.getOffset();
		int endRow = pageable.getOffset() + pageable.getPageSize();
		String pagedSql = "select * from ( select tmp_page.*, rownum row_id from ({0}) tmp_page "
				+ " where rownum <= {1} ) where row_id > {2}";
		return MessageFormat.format(pagedSql, sortedSql, endRow, startRow);
	}

}