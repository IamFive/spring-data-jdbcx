/**
 * @(#)JdbcxService.java 2016年2月17日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.jdbcx;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.google.common.base.CaseFormat;

import net.turnbig.jdbcx.sql.loader.SqlLoader;

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
	protected Field idField;

	@Autowired
	protected JdbcxPagingDaoSupport DAO;

	@Autowired(required = false)
	protected SqlLoader SQL;

	String getAllSql;
	String getByIdSql;
	String listByIdSql;
	String deleteByIdSql;
	String insertSql;

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
		try {
			guessTableMeta();
			generateSql();
		} catch (NoSuchFieldException | SecurityException e) {
			logger.error("initial jdbcx service failed", e);
			throw new RuntimeException(e);
		}
	}

	private void generateSql() {
		getAllSql = MessageFormat.format("select * from {0}", tableName);
		getByIdSql = MessageFormat.format("select * from {0} where {1} = :id", tableName, idColumnName);
		listByIdSql = MessageFormat.format("select * from {0} where {1} in (:id)", tableName, idColumnName);
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

	public List<Entity> list(List<PK> list) {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("id", list);
			return DAO.queryForListBean(listByIdSql, param, entityClazz);
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

	/**
	 * if no record matches condition, null will be returned
	 * if more than one record matches, first will be returned
	 * 
	 * @param fvs
	 * @return
	 */
	public Entity findByFields(FieldValue... fvs) {
		Map<String, Object> param = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer("select * from ").append(tableName).append(" where 1=1 ");
		for (FieldValue fv : fvs) {
			String dbFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fv.getFieldName());
			sb.append(" and ").append(dbFieldName).append(" = :").append(fv.getFieldName());
			param.put(fv.getFieldName(), fv.getFieldValue());
		}
		sb.append(" limit 1");
		List<Entity> result = DAO.queryForListBean(sb.toString(), param, entityClazz);
		if (CollectionUtils.isEmpty(result)) {
			return null;
		} else {
			return result.get(0);
		}
	}

	public List<Entity> findListByFields(FieldValue... fvs) {
		Map<String, Object> param = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer("select * from ").append(tableName).append(" where 1=1 ");
		for (FieldValue fv : fvs) {
			String dbFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fv.getFieldName());
			sb.append(" and ").append(dbFieldName);
			if (fv.getFieldValue() == null) {
				sb.append(" is null");
			} else {
				sb.append(" = :").append(fv.getFieldName());
				param.put(fv.getFieldName(), fv.getFieldValue());
			}
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

	public int updateFields(PK id, FieldValue... fvs) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", id);

		StringBuffer sb = new StringBuffer("update ").append(tableName).append(" set ");
		boolean addComma = false;
		for (FieldValue fv : fvs) {
			sb.append(addComma ? "," : "");
			String dbFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fv.getFieldName());
			sb.append(dbFieldName);
			if (fv.getFieldValue() == null) {
				sb.append(" = null");
			} else {
				sb.append(" = :").append(fv.getFieldName());
				param.put(fv.getFieldName(), fv.getFieldValue());
			}
			addComma = true;
		}
		sb.append(" where ").append(this.idColumnName).append(" = :id");
		return DAO.update(sb.toString(), param);
	}

	public Entity insert(Entity entity) {
		KeyHolder insert = DAO.insert(this.insertSql, entity, idColumnName);
		ReflectionUtils.setField(idField, entity, insert.getKey());
		return entity;
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

	private void guessTableMeta() throws NoSuchFieldException, SecurityException {
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
		Field[] fields = FieldUtils.getAllFields(entityClazz);
		List<String> fieldNames = new ArrayList<String>(fields.length);
		for (Field field : fields) {
			int modifiers = field.getModifiers();
			if (field.isAnnotationPresent(Id.class) || field.getName().equals("id")) {
				if (field.isAnnotationPresent(Column.class)) {
					Column column = field.getAnnotation(Column.class);
					if (column != null) {
						idColumnName = column.name();
					}
				}
				if (StringUtils.isEmpty(idColumnName)) {
					idColumnName = upperCamelToUnderscore(field.getName());
				}
				idField = field;
			} else if (!field.isAnnotationPresent(Transient.class) && !Modifier.isFinal(modifiers)
					&& !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)) {
				fieldNames.add(field.getName());
			}
		}

		if (StringUtils.isEmpty(idColumnName)) {
			logger.info("[{}] not @Id annotation is fould, will use *id* as id column name", entityClazz);
			idColumnName = "id";
			idField = entityClazz.getDeclaredField(idColumnName);
		}

		idField.setAccessible(true);
		logger.info("[{}] detected table meta: table-name `{}`, id-column-name `{}`", entityClazz, tableName,
				idColumnName);

		this.insertSql = generateInsertSql(fieldNames);
		logger.debug("[{}] generated insert sql is `{}`", entityClazz, insertSql);

		// TODO update sql ? i don't like this

	}

	/**
	 * @param fieldNames
	 * @return 
	 */
	protected String generateInsertSql(List<String> fieldNames) {
		// generate insert sql
		String valueKeys = StringUtils.collectionToDelimitedString(fieldNames, ",", ":", "");
		CollectionUtils.transform(fieldNames, new Transformer<String, String>() {
			@Override
			public String transform(String input) {
				return upperCamelToUnderscore(input);
			}
		});
		String insertKeys = StringUtils.collectionToDelimitedString(fieldNames, ",");
		return String.format("insert into %s (%s) values (%s)", tableName, insertKeys, valueKeys);
	}

	/**
	 * @param field
	 * @return
	 */
	protected String upperCamelToUnderscore(String value) {
		return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, value);
	}

	@SuppressWarnings("unchecked")
	public HashMap<PK, Entity> mapped(List<Entity> list) {
		HashMap<PK, Entity> mapped = new HashMap<PK, Entity>(list.size());
		for (Entity entity : list) {
			try {
				mapped.put((PK) idField.get(entity), entity);
			} catch (Exception e) {
				// ignore should not happen?
				logger.warn("could not get id field value for entity {}", entity.getClass());
			}
		}
		return mapped;
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
