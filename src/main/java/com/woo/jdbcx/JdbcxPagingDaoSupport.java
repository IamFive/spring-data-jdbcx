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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;

/**
 * @author Woo Cupid
 * @date 2016年1月28日
 * @version $Revision$
 */
public class JdbcxPagingDaoSupport extends JdbcxDaoSupport {

	public <T> Page<T> queryForListBean(String sql, Map<String, ?> paramMap, Class<T> mapResultToClass,
			Pageable pageable) {
		String countSql = dialect.getCountSql(sql);
		Integer count = queryForObject(countSql, paramMap, Integer.class);
		if (count > pageable.getOffset()) {
			String pageableSql = dialect.getPageableSql(sql, pageable);
			List<T> list = getNamedParameterJdbcTemplate().query(pageableSql, paramMap,
					getBeanPropsRowMapper(mapResultToClass));
			return new PageImpl<T>(list, pageable, count);
		} else {
			return new PageImpl<T>(Collections.<T> emptyList(), pageable, count);
		}
	}

	public <T> Page<T> queryForListBean(String sql, Object beanParamSource, Class<T> mapResultToClass,
			Pageable pageable) {
		BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(beanParamSource);
		String countSql = dialect.getCountSql(sql);
		Integer count = queryForObject(countSql, paramSource, Integer.class);
		if (count > pageable.getOffset()) {
			String pageableSql = dialect.getPageableSql(sql, pageable);
			List<T> list = getNamedParameterJdbcTemplate().query(pageableSql, paramSource,
					getBeanPropsRowMapper(mapResultToClass));
			return new PageImpl<T>(list, pageable, count);
		} else {
			return new PageImpl<T>(Collections.<T> emptyList(), pageable, count);
		}
	}

	public <T> Page<T> queryForListBean(String sql, Class<T> mapResultToClass, Pageable pageable) {
		String countSql = dialect.getCountSql(sql);
		Integer count = queryForObject(countSql, Integer.class);
		if (count > pageable.getOffset()) {
			String pageableSql = dialect.getPageableSql(sql, pageable);
			List<T> list = getNamedParameterJdbcTemplate().query(pageableSql, getBeanPropsRowMapper(mapResultToClass));
			return new PageImpl<T>(list, pageable, count);
		} else {
			return new PageImpl<T>(Collections.<T> emptyList(), pageable, count);
		}
	}

	public Page<Map<String, Object>> queryForListMap(String sql, Map<String, ?> paramMap, Pageable pageable) {
		String countSql = dialect.getCountSql(sql);
		Integer count = queryForObject(countSql, paramMap, Integer.class);
		if (count > pageable.getOffset()) {
			String pageableSql = dialect.getPageableSql(sql, pageable);
			List<Map<String, Object>> list = getNamedParameterJdbcTemplate().queryForList(pageableSql, paramMap);
			return new PageImpl<Map<String, Object>>(list, pageable, count);
		} else {
			return new PageImpl<Map<String, Object>>(Collections.<Map<String, Object>> emptyList(), pageable, count);
		}
	}

	public Page<Map<String, Object>> queryForListMap(String sql, Object beanParamSource, Pageable pageable) {
		BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(beanParamSource);
		String countSql = dialect.getCountSql(sql);
		Integer count = queryForObject(countSql, paramSource, Integer.class);
		if (count > pageable.getOffset()) {
			String pageableSql = dialect.getPageableSql(sql, pageable);
			List<Map<String, Object>> list = getNamedParameterJdbcTemplate().queryForList(pageableSql,
					new BeanPropertySqlParameterSource(beanParamSource));
			return new PageImpl<Map<String, Object>>(list, pageable, count);
		} else {
			return new PageImpl<Map<String, Object>>(Collections.<Map<String, Object>> emptyList(), pageable, count);
		}
	}

	public Page<Map<String, Object>> queryForListMap(String sql, Pageable pageable) {
		String countSql = dialect.getCountSql(sql);
		Integer count = queryForObject(countSql, Integer.class);
		if (count > pageable.getOffset()) {
			String pageableSql = dialect.getPageableSql(sql, pageable);
			List<Map<String, Object>> list = getNamedParameterJdbcTemplate().queryForList(pageableSql,
					EmptySqlParameterSource.INSTANCE);
			return new PageImpl<Map<String, Object>>(list, pageable, count);
		} else {
			return new PageImpl<Map<String, Object>>(Collections.<Map<String, Object>> emptyList(), pageable, count);
		}
	}

	public <T> Page<T> queryForList(String sql, Object beanParamSource, Class<T> elementType, Pageable pageable) {
		BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(beanParamSource);
		String countSql = dialect.getCountSql(sql);
		Integer count = queryForObject(countSql, paramSource, Integer.class);
		if (count > pageable.getOffset()) {
			String pageableSql = dialect.getPageableSql(sql, pageable);
			List<T> list = getNamedParameterJdbcTemplate().queryForList(pageableSql,
					new BeanPropertySqlParameterSource(beanParamSource), elementType);
			return new PageImpl<T>(list, pageable, count);
		} else {
			return new PageImpl<T>(Collections.<T> emptyList(), pageable, count);
		}
	}

	public <T> Page<T> queryForList(String sql, Map<String, ?> paramMap, Class<T> elementType, Pageable pageable) {
		String countSql = dialect.getCountSql(sql);
		Integer count = queryForObject(countSql, paramMap, Integer.class);
		if (count > pageable.getOffset()) {
			String pageableSql = dialect.getPageableSql(sql, pageable);
			List<T> list = getNamedParameterJdbcTemplate().queryForList(pageableSql, paramMap, elementType);
			return new PageImpl<T>(list, pageable, count);
		} else {
			return new PageImpl<T>(Collections.<T> emptyList(), pageable, count);
		}
	}

	public <T> Page<T> queryForList(String sql, Class<T> elementType, Pageable pageable) {
		String countSql = dialect.getCountSql(sql);
		Integer count = queryForObject(countSql, EmptySqlParameterSource.INSTANCE, Integer.class);
		if (count > pageable.getOffset()) {
			String pageableSql = dialect.getPageableSql(sql, pageable);
			List<T> list = getNamedParameterJdbcTemplate().queryForList(pageableSql, EmptySqlParameterSource.INSTANCE,
					elementType);
			return new PageImpl<T>(list, pageable, count);
		} else {
			return new PageImpl<T>(Collections.<T> emptyList(), pageable, count);
		}
	}

}
