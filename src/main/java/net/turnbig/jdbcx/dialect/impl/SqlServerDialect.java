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

import org.springframework.data.domain.Pageable;

/**

SqlServer before 2012, the best pagination SQL sample:
<pre>
WITH Results_CTE AS
(
    SELECT
        Col1, Col2, ...,
        ROW_NUMBER() OVER (ORDER BY SortCol1, SortCol2, ...) AS RowNum
    FROM Table
    WHERE <whatever>
)
SELECT *
FROM Results_CTE
WHERE RowNum >= @Offset
AND RowNum < @Offset + @Limit


select top {LIMIT HERE} * from (
      select *, ROW_NUMBER() over (order by {ORDER FIELD}) as r_n_n 
      from {YOUR TABLES} where {OTHER OPTIONAL FILTERS}
) xx where r_n_n >={OFFSET HERE}
</pre>


 * 
 * @author Woo Cupid
 * @date 2016年1月28日
 * @version $Revision$
 */
public class SqlServerDialect extends AbstractSQLDialect {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.woo.jdbcx.dialect.SQLDialect#getPageSql(java.lang.String, org.springframework.data.domain.Pageable)
	 */
	@Override
	public String getPageableSql(String sql, Pageable pageable) {
		throw new RuntimeException("not implement yet");
	}

}