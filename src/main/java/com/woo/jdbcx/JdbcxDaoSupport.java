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

package com.woo.jdbcx;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;

import com.woo.jdbcx.dialect.Databases;
import com.woo.jdbcx.dialect.SQLDialect;


/**
 * Extends Named-Query-JDBC-Template with more friendly API
 * 
 * @author Woo Cupid
 * @date 2016年1月21日
 * @version $Revision$
 */
public abstract class JdbcxDaoSupport extends NamedParameterJdbcDaoSupport {

	private static final Logger logger = LoggerFactory.getLogger(JdbcxDaoSupport.class);

	SQLDialect dialect;

	@Autowired
	DataSource dataSource;

	NamedParameterJdbcTemplate jdbcTemplate;


	public void setDialect(SQLDialect dialect) {
		this.dialect = dialect;
	}

	@PostConstruct
	public void init() {
		setDataSource(dataSource);
		jdbcTemplate = getNamedParameterJdbcTemplate();

		try {
			String url = dataSource.getConnection().getMetaData().getURL();
			logger.debug("[jdbcx] detect datasource connection url is : {}", url);
			dialect = Databases.fromJdbcUrl(url).getDialect();
			logger.debug("[jdbcx] bind dialect to : {}", dialect.getClass());
		} catch (SQLException e) {
			logger.error("could not get datasource connection url", e);
		}
	}

	// ============================ multiply fields returned =====================//

	public <T> List<T> queryForListBean(String sql, Map<String, ?> paramMap, Class<T> mapResultToClass)
			throws DataAccessException {
		return jdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper<T>(mapResultToClass));
	}

	public <T> List<T> queryForListBean(String sql, Map<String, ?> paramMap, Class<T> mapResultToClass,
			Pageable pageable) throws DataAccessException {
		// TODO
		// 1. get count sql
		// 2. get pageable sql
		return jdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper<T>(mapResultToClass));
	}

	public <T> List<T> queryForListBean(String sql, Object beanParamSource, Class<T> mapResultToClass)
			throws DataAccessException {
		return jdbcTemplate.query(sql, new BeanPropertySqlParameterSource(beanParamSource),
				new BeanPropertyRowMapper<T>(mapResultToClass));
	}

	public <T> List<T> queryForListBean(String sql, Class<T> mapResultToClass) throws DataAccessException {
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<T>(mapResultToClass));
	}

	public <T> T queryForBean(String sql, Object beanParamSource, Class<T> mapResultToClass)
			throws DataAccessException {
		return jdbcTemplate.queryForObject(sql, new BeanPropertySqlParameterSource(beanParamSource),
				new BeanPropertyRowMapper<T>(mapResultToClass));
	}

	public <T> T queryForBean(String sql, Map<String, ?> paramMap, Class<T> mapResultToClass)
			throws DataAccessException {
		return jdbcTemplate.queryForObject(sql, paramMap, new BeanPropertyRowMapper<T>(mapResultToClass));
	}

	public <T> T queryForBean(String sql, Class<T> mapResultToClass) throws DataAccessException {
		return jdbcTemplate.queryForObject(sql, EmptySqlParameterSource.INSTANCE,
				new BeanPropertyRowMapper<T>(mapResultToClass));
	}

	public Map<String, Object> queryForMap(String sql, Object beanParamSource) throws DataAccessException {
		return jdbcTemplate.queryForMap(sql, new BeanPropertySqlParameterSource(beanParamSource));
	}

	public Map<String, Object> queryForMap(String sql, Map<String, ?> paramMap) throws DataAccessException {
		return jdbcTemplate.queryForMap(sql, paramMap);
	}

	public Map<String, Object> queryForMap(String sql) throws DataAccessException {
		return jdbcTemplate.queryForMap(sql, EmptySqlParameterSource.INSTANCE);
	}

	public List<Map<String, Object>> queryForListMap(String sql, Map<String, ?> paramMap) throws DataAccessException {
		return jdbcTemplate.queryForList(sql, paramMap);
	}

	public List<Map<String, Object>> queryForListMap(String sql, Object beanParamSource) throws DataAccessException {
		return jdbcTemplate.queryForList(sql, new BeanPropertySqlParameterSource(beanParamSource));
	}

	public List<Map<String, Object>> queryForListMap(String sql) throws DataAccessException {
		return jdbcTemplate.queryForList(sql, EmptySqlParameterSource.INSTANCE);
	}

	// ============================ multiply fields returned =====================//

	// ============================ single field returned =====================//
	public <T> T queryForObject(String sql, Class<T> requiredType) throws DataAccessException {
		return jdbcTemplate.queryForObject(sql, EmptySqlParameterSource.INSTANCE, requiredType);
	}

	public <T> T queryForObject(String sql, Object beanParamSource, Class<T> requiredType) throws DataAccessException {
		return jdbcTemplate.queryForObject(sql, new BeanPropertySqlParameterSource(beanParamSource), requiredType);
	}

	public <T> T queryForObject(String sql, Map<String, ?> paramMap, Class<T> requiredType) throws DataAccessException {
		return jdbcTemplate.queryForObject(sql, paramMap, requiredType);
	}

	public <T> List<T> queryForList(String sql, Object beanParamSource, Class<T> elementType)
			throws DataAccessException {
		return jdbcTemplate.queryForList(sql, new BeanPropertySqlParameterSource(beanParamSource), elementType);
	}

	public <T> List<T> queryForList(String sql, Map<String, ?> paramMap, Class<T> elementType)
			throws DataAccessException {
		return jdbcTemplate.queryForList(sql, paramMap, elementType);
	}

	public <T> List<T> queryForList(String sql, Class<T> elementType) throws DataAccessException {
		return jdbcTemplate.queryForList(sql, EmptySqlParameterSource.INSTANCE, elementType);
	}

	// ============================ single field returned =====================//

	public int update(String sql, Object beanParamSource) throws DataAccessException {
		return jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(beanParamSource));
	}

	public int update(String sql, Map<String, ?> paramMap) throws DataAccessException {
		return jdbcTemplate.update(sql, paramMap);
	}

	public int update(String sql, Object beanParamSource, KeyHolder generatedKeyHolder) throws DataAccessException {
		return jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(beanParamSource), generatedKeyHolder);
	}

	public int update(String sql, Object beanParamSource, KeyHolder generatedKeyHolder, String[] keyColumnNames)
			throws DataAccessException {
		return jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(beanParamSource), generatedKeyHolder,
				keyColumnNames);
	}

	@SafeVarargs
	public final int[] batchUpdate(String sql, Map<String, ?>... batchValues) {
		SqlParameterSource[] batchArgs = new SqlParameterSource[batchValues.length];
		int i = 0;
		for (Map<String, ?> values : batchValues) {
			batchArgs[i] = new MapSqlParameterSource(values);
			i++;
		}
		return jdbcTemplate.batchUpdate(sql, batchArgs);
	}

	public final int[] batchUpdate(String sql, Object... batchArgs) {
		SqlParameterSource[] params = new SqlParameterSource[batchArgs.length];
		for (int i = 0; i < batchArgs.length; i++) {
			params[i] = new BeanPropertySqlParameterSource(batchArgs[i]);
		}
		return jdbcTemplate.batchUpdate(sql, params);
	}

}
