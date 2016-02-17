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

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.DataAccessException;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.woo.jdbcx.dialect.Databases;
import com.woo.jdbcx.dialect.SQLDialect;

/**
 * Extends Named-Query-JDBC-Template with more friendly API
 * 
 * @author Woo Cupid
 * @date 2016年1月21日
 * @version $Revision$
 */
@Component
public class JdbcxDaoSupport extends NamedParameterJdbcDaoSupport {

	private static final Logger logger = LoggerFactory.getLogger(JdbcxDaoSupport.class);

	HashMap<Class<?>, BeanPropertyRowMapper<?>> beanPropsRowMapperMapper = new HashMap<Class<?>, BeanPropertyRowMapper<?>>();

	// used to convert some special jdbc value type to java object
	@Autowired(required = false)
	ConversionService conversionService;

	SQLDialect dialect;

	@Autowired
	DataSource dataSource;

	public void setDialect(SQLDialect dialect) {
		this.dialect = dialect;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@PostConstruct
	public void init() {
		setDataSource(dataSource);
		try {
			DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
			dialect = Databases.fromMetaData(metaData).getDialect();
			logger.info("[jdbcx] bind dialect to : {}", dialect.getClass());
		} catch (SQLException e) {
			logger.error("could not get datasource connection url", e);
		}
	}

	public static class JdbcxBeanPropertyRowMapper<T> extends BeanPropertyRowMapper<T> {

		private ConversionService cs;

		public JdbcxBeanPropertyRowMapper() {
			super();
		}

		public JdbcxBeanPropertyRowMapper(Class<T> mappedClass, boolean checkFullyPopulated) {
			super(mappedClass, checkFullyPopulated);
		}

		public JdbcxBeanPropertyRowMapper(Class<T> mappedClass) {
			super(mappedClass);
		}

		public JdbcxBeanPropertyRowMapper(Class<T> mappedClass, final ConversionService cs) {
			super(mappedClass);
			this.cs = cs;
		}

		@Override
		protected void initBeanWrapper(BeanWrapper bw) {
			super.initBeanWrapper(bw);
			if (cs == null) {
				cs = new DefaultFormattingConversionService(true);
			}
			bw.setConversionService(cs);
		}

	}

	@SuppressWarnings("unchecked")
	protected <T> JdbcxBeanPropertyRowMapper<T> getBeanPropsRowMapper(Class<T> mapResultToClass) {
		if (!beanPropsRowMapperMapper.containsKey(mapResultToClass)) {
			beanPropsRowMapperMapper.put(mapResultToClass,
					new JdbcxBeanPropertyRowMapper<T>(mapResultToClass, conversionService));
		}
		return (JdbcxBeanPropertyRowMapper<T>) beanPropsRowMapperMapper.get(mapResultToClass);
	}

	// ============================ multiply fields returned =====================//

	public <T> List<T> queryForListBean(String sql, Map<String, ?> paramMap, Class<T> mapResultToClass)
			throws DataAccessException {
		return getNamedParameterJdbcTemplate().query(sql, paramMap, getBeanPropsRowMapper(mapResultToClass));
	}

	public <T> List<T> queryForListBean(String sql, Object beanParamSource, Class<T> mapResultToClass)
			throws DataAccessException {
		return getNamedParameterJdbcTemplate().query(sql, new BeanPropertySqlParameterSource(beanParamSource),
				getBeanPropsRowMapper(mapResultToClass));
	}

	public <T> List<T> queryForListBean(String sql, Class<T> mapResultToClass) {
		return getNamedParameterJdbcTemplate().query(sql, getBeanPropsRowMapper(mapResultToClass));
	}

	public <T> T queryForBean(String sql, Object beanParamSource, Class<T> mapResultToClass)
			throws DataAccessException {
		return getNamedParameterJdbcTemplate().queryForObject(sql, new BeanPropertySqlParameterSource(beanParamSource),
				getBeanPropsRowMapper(mapResultToClass));
	}

	public <T> T queryForBean(String sql, Map<String, ?> paramMap, Class<T> mapResultToClass)
			throws DataAccessException {
		return getNamedParameterJdbcTemplate().queryForObject(sql, paramMap, getBeanPropsRowMapper(mapResultToClass));
	}

	public <T> T queryForBean(String sql, Class<T> mapResultToClass) {
		return getNamedParameterJdbcTemplate().queryForObject(sql, EmptySqlParameterSource.INSTANCE,
				getBeanPropsRowMapper(mapResultToClass));
	}

	public Map<String, Object> queryForMap(String sql, Object beanParamSource) {
		return getNamedParameterJdbcTemplate().queryForMap(sql, new BeanPropertySqlParameterSource(beanParamSource));
	}

	public Map<String, Object> queryForMap(String sql, Map<String, ?> paramMap) {
		return getNamedParameterJdbcTemplate().queryForMap(sql, paramMap);
	}

	public Map<String, Object> queryForMap(String sql) {
		return getNamedParameterJdbcTemplate().queryForMap(sql, EmptySqlParameterSource.INSTANCE);
	}

	public List<Map<String, Object>> queryForListMap(String sql, Map<String, ?> paramMap) {
		return getNamedParameterJdbcTemplate().queryForList(sql, paramMap);
	}

	public List<Map<String, Object>> queryForListMap(String sql, Object beanParamSource) {
		return getNamedParameterJdbcTemplate().queryForList(sql, new BeanPropertySqlParameterSource(beanParamSource));
	}

	public List<Map<String, Object>> queryForListMap(String sql) {
		return getNamedParameterJdbcTemplate().queryForList(sql, EmptySqlParameterSource.INSTANCE);
	}

	// ============================ multiply fields returned =====================//

	// ============================ single field returned =====================//
	public <T> T queryForObject(String sql, Class<T> requiredType) {
		return getNamedParameterJdbcTemplate().queryForObject(sql, EmptySqlParameterSource.INSTANCE, requiredType);
	}

	public <T> T queryForObject(String sql, Object beanParamSource, Class<T> requiredType) {
		return getNamedParameterJdbcTemplate().queryForObject(sql, new BeanPropertySqlParameterSource(beanParamSource),
				requiredType);
	}

	public <T> T queryForObject(String sql, Map<String, ?> paramMap, Class<T> requiredType) {
		return getNamedParameterJdbcTemplate().queryForObject(sql, paramMap, requiredType);
	}

	public <T> List<T> queryForList(String sql, Object beanParamSource, Class<T> elementType)
			throws DataAccessException {
		return getNamedParameterJdbcTemplate().queryForList(sql, new BeanPropertySqlParameterSource(beanParamSource),
				elementType);
	}

	public <T> List<T> queryForList(String sql, Map<String, ?> paramMap, Class<T> elementType)
			throws DataAccessException {
		return getNamedParameterJdbcTemplate().queryForList(sql, paramMap, elementType);
	}

	public <T> List<T> queryForList(String sql, Class<T> elementType) {
		return getNamedParameterJdbcTemplate().queryForList(sql, EmptySqlParameterSource.INSTANCE, elementType);
	}

	// ============================ single field returned =====================//

	/**
	 * execute SQL (insert/update/etc)
	 * 
	 * @param sql
	 * @param beanParamSource
	 * @return
	 */
	public int update(String sql, Object beanParamSource) {
		return getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(beanParamSource));
	}

	public int update(String sql, Map<String, ?> paramMap) {
		return getNamedParameterJdbcTemplate().update(sql, paramMap);
	}

	public KeyHolder insert(String sql, Object beanParamSource) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(beanParamSource), keyHolder);
		return keyHolder;
	}

	public KeyHolder insert(String sql, Object beanParamSource, KeyHolder generatedKeyHolder, String[] keyColumnNames)
			throws DataAccessException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(beanParamSource),
				generatedKeyHolder, keyColumnNames);
		return keyHolder;
	}

	public final int[] batchUpdate(String sql, @SuppressWarnings("unchecked") Map<String, ?>... batchValues) {
		SqlParameterSource[] batchArgs = new SqlParameterSource[batchValues.length];
		int i = 0;
		for (Map<String, ?> values : batchValues) {
			batchArgs[i] = new MapSqlParameterSource(values);
			i++;
		}
		return getNamedParameterJdbcTemplate().batchUpdate(sql, batchArgs);
	}

	public final int[] batchUpdate(String sql, List<?> batchArgs) {
		SqlParameterSource[] params = new SqlParameterSource[batchArgs.size()];
		for (int i = 0; i < batchArgs.size(); i++) {
			params[i] = new BeanPropertySqlParameterSource(batchArgs.get(i));
		}
		return getNamedParameterJdbcTemplate().batchUpdate(sql, params);
	}

}
