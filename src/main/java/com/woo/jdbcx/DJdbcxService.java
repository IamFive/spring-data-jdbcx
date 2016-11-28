/**
 * @(#)DJdbcxService.java 2016年11月28日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package com.woo.jdbcx;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.woo.qb.segment.SqlSegment;

/**
 * enable JDBCX service with dynamic SQL-segment feature
 * <br/>
 * dynamic sql segment feature is provided by Query-Builder<https://github.com/IamFive/query-builder>
 * 
 * @author Woo Cupid
 * @date 2016年11月28日
 * @version $Revision$
 */
public class DJdbcxService<Entity, PK extends Serializable> extends JdbcxService<Entity, PK> {

	public Entity findByNamedSqlSegment(SqlSegment segment) {
		String condition = segment.asSql();
		String sql = getAllSql + " where " + condition;
		if (segment.isParamRequired()) {
			List<Entity> results = DAO.queryForListBean(sql, segment.getKeyedParams(), entityClazz);
			if(CollectionUtils.isEmpty(results)) {
				return null;
			}
			return results.get(0);
		} else {
			List<Entity> results = DAO.queryForListBean(sql, entityClazz);
			if (CollectionUtils.isEmpty(results)) {
				return null;
			}
			return results.get(0);
		}
	}

	public List<Entity> findListByNamedSqlSegment(SqlSegment segment) {
		String condition = segment.asSql();
		String sql = getAllSql + " where " + condition;
		if (segment.isParamRequired()) {
			return DAO.queryForListBean(sql, segment.getKeyedParams(), entityClazz);
		} else {
			return DAO.queryForListBean(sql, entityClazz);
		}
	}

}
