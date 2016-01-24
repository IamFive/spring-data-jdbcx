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


package com.woo.jdbcx.dialect;

import java.util.List;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.WithItem;

/**
 * 处理 Order by
 *
 * @author liuzh
 * @since 2015-06-27
 */
public class OrderByParser {
	/**
	 * convert to order by sql
	 *
	 * @param sql
	 * @param orderBy
	 * @return
	 */
	public static String converToOrderBySql(String sql, String orderBy) {
		// 解析SQL
		Statement stmt = null;
		try {
			stmt = CCJSqlParserUtil.parse(sql);
			Select select = (Select) stmt;
			SelectBody selectBody = select.getSelectBody();
			// 处理body-去最外层order by
			List<OrderByElement> orderByElements = extraOrderBy(selectBody);
			String defaultOrderBy = PlainSelect.orderByToString(orderByElements);
			if (defaultOrderBy.indexOf('?') != -1) {
				throw new RuntimeException("原SQL[" + sql + "]中的order by包含参数，因此不能使用OrderBy插件进行修改!");
			}
			// 新的sql
			sql = select.toString();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return sql + " order by " + orderBy;
	}

	/**
	 * extra order by and set default orderby to null
	 *
	 * @param selectBody
	 */
	public static List<OrderByElement> extraOrderBy(SelectBody selectBody) {
		if (selectBody instanceof PlainSelect) {
			List<OrderByElement> orderByElements = ((PlainSelect) selectBody).getOrderByElements();
			((PlainSelect) selectBody).setOrderByElements(null);
			return orderByElements;
		} else if (selectBody instanceof WithItem) {
			WithItem withItem = (WithItem) selectBody;
			if (withItem.getSelectBody() != null) {
				return extraOrderBy(withItem.getSelectBody());
			}
		} else {
			SetOperationList operationList = (SetOperationList) selectBody;
			if ((operationList.getSelects() != null) && (operationList.getSelects().size() > 0)) {
				List<SelectBody> plainSelects = operationList.getSelects();
				return extraOrderBy(plainSelects.get(plainSelects.size() - 1));
			}
		}
		return null;
	}
}
