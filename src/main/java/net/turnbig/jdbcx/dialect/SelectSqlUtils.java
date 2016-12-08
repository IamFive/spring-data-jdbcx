/**
 * @(#)SelectSqlUtils.java 2016年1月28日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.jdbcx.dialect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.Assert;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.WithItem;
import net.turnbig.jdbcx.dialect.exception.GeneratePagingSqlException;

/**
 * 
 * <h3>Select SQL utilities</h3>
 * 
 * <li>dynamic add order by expressions</li>
 * 
 * 
 * <h3>Select SQL samples - copied from postgres document</h3>
 * 
 * <pre class="SYNOPSIS">
    [ WITH [ RECURSIVE ] with_query [, ...] ] 
    SELECT [ ALL | DISTINCT [ ON ( expression [, ...] ) ] ] * | expression [ [ AS ] output_name ] [, ...]
    [ FROM from_item [, ...] ]
    [ WHERE condition ]
    [ GROUP BY expression [, ...] ]
    [ HAVING condition [, ...] ]
    [ WINDOW window_name AS ( window_definition ) [, ...] ]
    [ { UNION | INTERSECT | EXCEPT } [ ALL ] select ]
    [ ORDER BY expression [ ASC | DESC | USING operator ] [ NULLS { FIRST | LAST } ] [, ...] ]
    [ LIMIT { count | ALL } ]
    [ OFFSET start [ ROW | ROWS ] ]
    [ FETCH { FIRST | NEXT } [ count ] { ROW | ROWS } ONLY ]
    [ FOR { UPDATE | SHARE } [ OF table_name [, ...] ] [ NOWAIT ] [...] ]

<span>where from_item can be one of:</span>

    [ ONLY ] table_name [ * ] [ [ AS ] alias [ ( column_alias [, ...] ) ] ]
    ( select ) [ AS ] alias [ ( column_alias [, ...] ) ]
    with_query_name [ [ AS ] alias [ ( column_alias [, ...] ) ] ]
    function_name ( [ argument [, ...] ] ) [ AS ] alias [ ( column_alias [, ...] | column_definition [, ...] ) ]
    function_name ( [ argument [, ...] ] ) AS ( column_definition [, ...] )
    from_item [ NATURAL ] join_type from_item [ ON join_condition | USING ( join_column [, ...] ) ]

<span>and with_query is:</span>

    with_query_name [ ( column_name [, ...] ) ] AS ( select )
    
    TABLE { [ ONLY ] table_name [ * ] | with_query_name }
 * </pre>
 * 
 * @author Woo Cupid
 * @date 2016年1月28日
 * @version $Revision$
 */
public class SelectSqlUtils {

	private static final Logger logger = LoggerFactory.getLogger(SelectSqlUtils.class);

	private static final List<SelectItem> COUNT_ITEM;
	private static final Alias TABLE_ALIAS;

	static {
		COUNT_ITEM = new ArrayList<SelectItem>();
		COUNT_ITEM.add(new SelectExpressionItem(new Column("count(*)")));

		TABLE_ALIAS = new Alias("table_count");
		TABLE_ALIAS.setUseAs(false);
	}

	/**
	 * get pageable SQL which support limit x offset x
	 * 
	 * @see SelectSqlUtils#getPageableSqlWithLimitOffset(Select, Pageable)
	 * @param sql
	 * @param pageable
	 * @return
	 */
	public static String getPageableSqlWithLimitOffset(String sql, Pageable pageable) {
		Select select = parseSelectSql(sql);
		getPageableSqlWithLimitOffset(select, pageable);
		return select.toString();
	}

	/**
	 * 
	 * get pageable SQL which support limit x offset x
	 * 
	 * <li>Pageable's page starts from *zero*</li>
	 * 
	 * @param select
	 * @param pageable
	 */
	public static void getPageableSqlWithLimitOffset(Select select, Pageable pageable) {
		SelectSqlUtils.addSort(select, pageable.getSort()); // add sort expression
		SelectBody sb = select.getSelectBody();
		if (sb instanceof PlainSelect) {
			Limit limit = new Limit();
			limit.setRowCount(pageable.getPageSize());
			limit.setOffset(pageable.getOffset());
			((PlainSelect) sb).setLimit(limit);
		} else if (sb instanceof SetOperationList) {
			Limit limit = new Limit();
			limit.setRowCount(pageable.getPageSize());
			limit.setOffset(pageable.getOffset());
			((SetOperationList) sb).setLimit(limit);
		} else if (sb instanceof WithItem) {
			// should not happen ?
			logger.error(
					"select body could not be a with-item, please report the issue to https://github.com/IamFive/spring-data-jdbcx");
			throw new GeneratePagingSqlException(select.toString(), "SQL body could not be a with-item");
		}
	}

	/**
	 * @param sql
	 * @return
	 */
	public static Select parseSelectSql(String sql) {
		Statement smt = null;
		try {
			smt = CCJSqlParserUtil.parse(sql);
			if (!(smt instanceof Select)) {
				throw new GeneratePagingSqlException(sql, "SQL should be a legal select SQL");
			}
			return (Select) smt;
		} catch (JSQLParserException e) {
			throw new GeneratePagingSqlException(sql, "SQL is illegal");
		}
	}

	/***
	 * <h3>add sort to the SQL</h3> if the SQL has got an order expression, will append new "Sort" to the tail
	 * 
	 * @param sql
	 * @param sort
	 * @return
	 * @throws JSQLParserException
	 */
	public static String addSort(String sql, Sort sort) {
		if (sort != null) {
			Iterator<Order> iterator = sort.iterator();
			if (iterator.hasNext()) {
				Select select = parseSelectSql(sql);
				addSort(select, sort);
				return select.toString();
			}
		}

		return sql;
	}

	public static Select addSort(Select select, Sort sort) {
		if (sort != null) {
			Iterator<Order> iterator = sort.iterator();
			if (iterator.hasNext()) {
				SelectBody sb = select.getSelectBody();
				if (sb instanceof PlainSelect) {
					// FIXME should we remove elements with same property name?
					List<OrderByElement> elements = ((PlainSelect) sb).getOrderByElements();
					elements = elements == null ? new ArrayList<OrderByElement>() : elements;
					elements.addAll(buildOrderByElements(iterator));
					((PlainSelect) sb).setOrderByElements(elements);
				} else if (sb instanceof SetOperationList) {
					// FIXME should we remove elements with same property name?
					List<OrderByElement> elements = ((SetOperationList) sb).getOrderByElements();
					elements = elements == null ? new ArrayList<OrderByElement>() : elements;
					elements.addAll(buildOrderByElements(iterator));
					((SetOperationList) sb).setOrderByElements(elements);
				} else if (sb instanceof WithItem) {
					// should not happen ?
					logger.error(
							"select body could not be a with-item, please report the issue to https://github.com/IamFive/spring-data-jdbcx");
					throw new GeneratePagingSqlException("SQL body could not be a with-item");
				}
			}
		}

		return select;
	}


	/**
	 * 
	 * @param sql
	 * @param orderbys id desc, name asc
	 * @return
	 */
	public static String addSort(String sql, String... orderbys) {
		List<Order> list = new ArrayList<Order>();
		for (String orderby : orderbys) {
			String[] split = orderby.split(" ");
			Assert.isTrue(split.length == 2, "order by string should like: property [asc|desc]");
			list.add(new Order(Direction.fromString(split[1]), split[0]));
		}

		if (list.size() > 0) {
			Sort sort = new Sort(list);
			return addSort(sql, sort);
		}

		return sql;
	}

	/**
	 * @param iterator
	 * @param elements
	 * @return
	 */
	private static List<OrderByElement> buildOrderByElements(Iterator<Order> iterator) {
		List<OrderByElement> list = new ArrayList<OrderByElement>();
		while (iterator.hasNext()) {
			Order order = iterator.next();
			OrderByElement e = new OrderByElement();
			e.setExpression(new Column(order.getProperty()));
			e.setAsc(Direction.ASC.equals(order.getDirection()));
			e.setAscDescPresent(true);
			list.add(e);
		}
		return list;
	}

	/**
	 * <h3>generate count SQL</h3>
	 * <li>if SQL is a plain sql with out aggregate functions, will replace select columns with count(*)</li>
	 * <li>else will use a temp table to count</li>
	 * 
	 * @param sql
	 * @return
	 */
	public static String getCountSql(String sql) {
		Select select = parseSelectSql(sql);
		SelectBody sb = select.getSelectBody();

		if (sb instanceof PlainSelect) {
			PlainSelect plain = (PlainSelect) sb;
			// remove order by expression of the main select body
			plain.setOrderByElements(null);
			if (!hasAggregateFunc(plain)) {
				// we can directly replace columns with count(*)
				plain.setSelectItems(COUNT_ITEM);
				return select.toString();
			}
		} else if (sb instanceof SetOperationList) {
			/**
			  sub-select's order-by expression should not be removed.
			  for example: 
				  (select * from member order by name desc limit 1 offset 10) 
				  	union 
				  (select * from member order by name desc limit 10 )
				  order by name desc
			 **/
			SetOperationList list = (SetOperationList) sb;
			// remove order by expression of the main select body
			list.setOrderByElements(null);
		} else if (sb instanceof WithItem) {
			// should not happen ?
			logger.error(
					"select body could not be a with-item, please report the issue to https://github.com/IamFive/spring-data-jdbcx");
			throw new GeneratePagingSqlException("SQL body could not be a with-item");
		}


		return getCountSqlWithTempTable(select);
	}

	/**
	    build count SQL with a temp table
		select count(*) from (
			xxxxx
		) tmp
	 * @param select
	 * @param sb
	 * @return
	 */
	private static String getCountSqlWithTempTable(Select select) {
		PlainSelect plainSelect = new PlainSelect();
		SubSelect subSelect = new SubSelect();
		subSelect.setSelectBody(select.getSelectBody());
		subSelect.setAlias(TABLE_ALIAS);
		plainSelect.setFromItem(subSelect);
		plainSelect.setSelectItems(COUNT_ITEM);
		select.setSelectBody(plainSelect);
		return select.toString();
	}

	/**
	 * 是否包含聚合函数
	 * @param select
	 * @return
	 */
	public static boolean hasAggregateFunc(PlainSelect select) {
		// 包含group by的时候不可以
		if (select.getGroupByColumnReferences() != null) {
			return true;
		}
		// 包含distinct的时候不可以
		if (select.getDistinct() != null) {
			return true;
		}

		// aggregate function must be used with group by
		// so, validate group by is enough.
		/**
		for (SelectItem item : select.getSelectItems()) {
			// 如果查询列中包含函数，也不可以，函数可能会聚合列
			if (item instanceof SelectExpressionItem) {
				if (((SelectExpressionItem) item).getExpression() instanceof Function) {
					return true;
				}
			}
		}*/
		return false;
	}

	/**
	 * 
	 * /we just leave illegal situation to users ?
		do we need to check whether a SQL could be count or not ?
	 * @param sql
	 * @param sb
	 */
	public static void delOrderBy(SelectBody sb) {
		// remove order by expression of the main select body
		if (sb instanceof PlainSelect) {
			PlainSelect plain = (PlainSelect) sb;
			plain.setOrderByElements(null);
		} else if (sb instanceof SetOperationList) {
			/**
			  sub-select's order-by expression should not be removed.
			  for example: 
				  (select * from member order by name desc limit 1 offset 10) 
				  	union 
				  (select * from member order by name desc limit 10 )
				  order by name desc
			 **/
			SetOperationList list = (SetOperationList) sb;
			list.setOrderByElements(null);
		} else if (sb instanceof WithItem) {
			// should not happen ?
			logger.error(
					"select body could not be a with-item, please report the issue to https://github.com/IamFive/spring-data-jdbcx");
			throw new GeneratePagingSqlException("SQL body could not be a with-item");
		}
	}

}
