/**
 * @(#)JdbcxPagingDaoSupport.java 2016年1月28日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package com.woo.jdbcx;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;

/**
 * @author Woo Cupid
 * @date 2016年1月28日
 * @version $Revision$
 */
public class JdbcxPagingDaoSupport extends JdbcxDaoSupport {

	public <T> List<T> queryForListBean(String sql, Map<String, ?> paramMap, Class<T> mapResultToClass,
			Pageable pageable) {
		String countSql = dialect.getCountSql(sql);
		Integer count = queryForObject(countSql, paramMap, Integer.class);
		if (count > pageable.getOffset()) {
			String pageableSql = dialect.getPageableSql(countSql, pageable);
			return jdbcTemplate.query(pageableSql, paramMap, new BeanPropertyRowMapper<T>(mapResultToClass));
		} else {
			return Collections.<T> emptyList();
		}
	}

	public <T> List<T> queryForListBean(String sql, Object beanParamSource, Class<T> mapResultToClass,
			Pageable pageable) {
		BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(beanParamSource);
		String countSql = dialect.getCountSql(sql);
		Integer count = queryForObject(countSql, paramSource, Integer.class);
		if (count > pageable.getOffset()) {
			String pageableSql = dialect.getPageableSql(countSql, pageable);
			return jdbcTemplate.query(pageableSql, paramSource, new BeanPropertyRowMapper<T>(mapResultToClass));
		} else {
			return Collections.<T> emptyList();
		}
	}

	public <T> List<T> queryForListBean(String sql, Class<T> mapResultToClass, Pageable pageable) {
		String countSql = dialect.getCountSql(sql);
		Integer count = queryForObject(countSql, Integer.class);
		if (count > pageable.getOffset()) {
			String pageableSql = dialect.getPageableSql(countSql, pageable);
			return jdbcTemplate.query(pageableSql, new BeanPropertyRowMapper<T>(mapResultToClass));
		} else {
			return Collections.<T> emptyList();
		}
	}

	public List<Map<String, Object>> queryForListMap(String sql, Map<String, ?> paramMap, Pageable pageable) {
		String countSql = dialect.getCountSql(sql);
		Integer count = queryForObject(countSql, paramMap, Integer.class);
		if (count > pageable.getOffset()) {
			String pageableSql = dialect.getPageableSql(countSql, pageable);
			return jdbcTemplate.queryForList(pageableSql, paramMap);
		} else {
			return Collections.<Map<String, Object>> emptyList();
		}
	}

	public List<Map<String, Object>> queryForListMap(String sql, Object beanParamSource, Pageable pageable) {
		BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(beanParamSource);
		String countSql = dialect.getCountSql(sql);
		Integer count = queryForObject(countSql, paramSource, Integer.class);
		if (count > pageable.getOffset()) {
			String pageableSql = dialect.getPageableSql(countSql, pageable);
			return jdbcTemplate.queryForList(pageableSql, new BeanPropertySqlParameterSource(beanParamSource));
		} else {
			return Collections.<Map<String, Object>> emptyList();
		}
	}

	public List<Map<String, Object>> queryForListMap(String sql, Pageable pageable) {
		String countSql = dialect.getCountSql(sql);
		Integer count = queryForObject(countSql, Integer.class);
		if (count > pageable.getOffset()) {
			String pageableSql = dialect.getPageableSql(countSql, pageable);
			return jdbcTemplate.queryForList(pageableSql, EmptySqlParameterSource.INSTANCE);
		} else {
			return Collections.<Map<String, Object>> emptyList();
		}
	}

	public <T> List<T> queryForList(String sql, Object beanParamSource, Class<T> elementType, Pageable pageable) {
		BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(beanParamSource);
		String countSql = dialect.getCountSql(sql);
		Integer count = queryForObject(countSql, paramSource, Integer.class);
		if (count > pageable.getOffset()) {
			String pageableSql = dialect.getPageableSql(countSql, pageable);
			return jdbcTemplate.queryForList(pageableSql, new BeanPropertySqlParameterSource(beanParamSource),
					elementType);
		} else {
			return Collections.<T> emptyList();
		}
	}

	public <T> List<T> queryForList(String sql, Map<String, ?> paramMap, Class<T> elementType, Pageable pageable) {
		String countSql = dialect.getCountSql(sql);
		Integer count = queryForObject(countSql, paramMap, Integer.class);
		if (count > pageable.getOffset()) {
			String pageableSql = dialect.getPageableSql(countSql, pageable);
			return jdbcTemplate.queryForList(pageableSql, paramMap, elementType);
		} else {
			return Collections.<T> emptyList();
		}
	}

	public <T> List<T> queryForList(String sql, Class<T> elementType, Pageable pageable) {
		String countSql = dialect.getCountSql(sql);
		Integer count = queryForObject(countSql, EmptySqlParameterSource.INSTANCE, Integer.class);
		if (count > pageable.getOffset()) {
			String pageableSql = dialect.getPageableSql(countSql, pageable);
			return jdbcTemplate.queryForList(pageableSql, EmptySqlParameterSource.INSTANCE, elementType);
		} else {
			return Collections.<T> emptyList();
		}
	}

}
