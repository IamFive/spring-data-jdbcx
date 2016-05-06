/**
 * @(#)JdbcxService.java 2016年2月17日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package com.woo.jdbcx;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.google.common.base.CaseFormat;

/**
 * 
 * basic service with jdbcx-paging-dao-support
 * 
 * @author Woo Cupid
 * @date 2016年2月17日
 * @version $Revision$
 */
public class JdbcxService<Entity, PK extends Serializable> {

	private static final Logger logger = LoggerFactory.getLogger(JdbcxService.class);

	// Entity class
	protected Class<Entity> entityClazz;
	protected String tableName;
	protected String idColumnName;

	@Autowired
	protected JdbcxPagingDaoSupport DAO;

	String getAllSql;
	String getByIdSql;
	String deleteByIdSql;

	@SuppressWarnings("unchecked")
	public JdbcxService() {
		entityClazz = (Class<Entity>) getSuperClassGenricType(getClass(), 0);
		initial(entityClazz);
	}

	public JdbcxService(Class<Entity> EntityClazz) {
		initial(EntityClazz);
	}

	private void initial(Class<Entity> EntityClazz) {
		this.entityClazz = EntityClazz;
		guessTableMeta();
		generateSql();
	}

	private void generateSql() {
		getAllSql = MessageFormat.format("select * from {0}", tableName);
		getByIdSql = MessageFormat.format("select * from {0} where {1} = :id", tableName, idColumnName);
		deleteByIdSql = MessageFormat.format("delete from {0} where {1} = :id", tableName, idColumnName);
	}

	public Entity get(PK id) {
		try {
			Map<String, PK> param = new HashMap<String, PK>();
			param.put("id", id);
			return DAO.queryForBean(getByIdSql, param, entityClazz);
		} catch (Exception e) {
			return null;
		}
	}

	public Page<Entity> getAll(Pageable p) {
		return DAO.queryForListBean(getAllSql, entityClazz, p);
	}

	public List<Entity> getAll() {
		return DAO.queryForListBean(getAllSql, entityClazz);
	}

	public Entity findByFields(FieldValue... fvs) {
		Map<String, Object> param = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer("select * from ").append(tableName).append(" where 1=1 ");
		for (FieldValue fv : fvs) {
			String dbFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fv.getFieldName());
			sb.append(" and ").append(dbFieldName).append(" = :").append(fv.getFieldName());
			param.put(fv.getFieldName(), fv.getFieldValue());
		}
		return DAO.queryForBean(sb.toString(), param, entityClazz);
	}

	public List<Entity> findListByFields(FieldValue... fvs) {
		Map<String, Object> param = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer("select * from ").append(tableName).append(" where 1=1 ");
		for (FieldValue fv : fvs) {
			String dbFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fv.getFieldName());
			sb.append(" and ").append(dbFieldName).append(" = :").append(fv.getFieldName());
			param.put(fv.getFieldName(), fv.getFieldValue());
		}
		return DAO.queryForListBean(sb.toString(), param, entityClazz);
	}

	public Page<Entity> findListByFields(List<FieldValue> fvs, Pageable p) {
		Map<String, Object> param = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer("select * from ").append(tableName).append(" where 1=1 ");
		for (FieldValue fv : fvs) {
			String dbFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fv.getFieldName());
			sb.append(" and ").append(dbFieldName).append(" = :").append(fv.getFieldName());
			param.put(fv.getFieldName(), fv.getFieldValue());
		}

		return DAO.queryForListBean(sb.toString(), param, entityClazz, p);
	}

	public Integer countByFields(FieldValue... fvs) {
		Map<String, Object> param = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer("select count(*) from ").append(tableName).append(" where 1=1 ");
		for (FieldValue fv : fvs) {
			String dbFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fv.getFieldName());
			sb.append(" and ").append(dbFieldName).append(" = :").append(fv.getFieldName());
			param.put(fv.getFieldName(), fv.getFieldValue());
		}
		return DAO.queryForObject(sb.toString(), param, Integer.class);
	}

	public int delete(PK id) {
		Map<String, PK> paramMap = new HashMap<String, PK>();
		paramMap.put("id", id);
		int count = DAO.update(deleteByIdSql, paramMap);
		return count;
	}

	private Class<?> getSuperClassGenricType(final Class<?> targetClass, final int index) {
		Assert.notNull(targetClass, "targetClass不能为空");

		Type genType = targetClass.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			logger.warn(targetClass.getSimpleName() + "'s superclass not ParameterizedType");
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			logger.warn("Index: " + index + ", Size of " + targetClass.getSimpleName() + "'s Parameterized Type: "
					+ params.length);
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			logger.warn(targetClass.getSimpleName()
					+ " not set the actual Class targetClassn superclass generic parameter");
			return Object.class;
		}

		return (Class<?>) params[index];
	}

	private void guessTableMeta() {
		// get table name if entity is annotated by @Table
		Table table = AnnotationUtils.findAnnotation(entityClazz, Table.class);
		if (table != null) {
			tableName = table.name();
		}

		// if no table name specified, detect from class name
		if (StringUtils.isEmpty(tableName)) {
			logger.info("[{}] not @Table annotation with name is fould", entityClazz);
			String clazz = entityClazz.getName();
			int lastIndexOf = clazz.lastIndexOf(".");
			String className = clazz.substring(lastIndexOf + 1);
			tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className);
		}

		// guess id column name
		Field[] fields = entityClazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Id.class)) {
				if (field.isAnnotationPresent(Column.class)) {
					Column column = field.getAnnotation(Column.class);
					if (column != null) {
						idColumnName = column.name();
					}
				}
				if (StringUtils.isEmpty(idColumnName)) {
					idColumnName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
				}
				break;
			}
		}

		if (StringUtils.isEmpty(idColumnName)) {
			logger.info("[{}] not @Id annotation is fould, will use *id* as id column name", entityClazz);
			idColumnName = "id";
		}

		logger.info("[{}] detected table meta: table-name {}, id-column-name {}", entityClazz, tableName, idColumnName);
	}

	public static class FieldValue {
		private String fieldName;
		private Object fieldValue;

		public FieldValue(String fieldName, Object fieldValue) {
			this.fieldName = fieldName;
			this.fieldValue = fieldValue;
		}

		public static FieldValue of(String name, Object value) {
			return new FieldValue(name, value);
		}

		public String getFieldName() {
			return fieldName;
		}

		public Object getFieldValue() {
			return fieldValue;
		}

	}

	public static void main(String[] args) {
		Class<?> clazz = String.class;

		// System.out.println(clazz.getEnclosingClass().getName());
		System.out.println(clazz.getName());
		// System.out.println(clazz.getDeclaringClass().getName());
	}

}
