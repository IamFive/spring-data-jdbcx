/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package net.turnbig.jdbcx.utilities;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.JSONPObject;

/**
 * 简单封装Jackson，实现JSON String<->Java Object的Mapper.
 * 
 * 封装不同的输出风格, 使用不同的builder函数创建实例.
 * 
 * @author calvin
 */
public class JsonMapper {

	private static Logger logger = LoggerFactory.getLogger(JsonMapper.class);

	private final ObjectMapper mapper;

	public JsonMapper() {
		this(null);
	}

	public JsonMapper(Include include) {
		mapper = new ObjectMapper();
		// 设置输出时包含属性的风格
		if (include != null) {
			mapper.setSerializationInclusion(include);
		}

		SimpleModule module = new SimpleModule();
		module.addDeserializer(Date.class, new Jackson2DateDeserializer());
		mapper.registerModule(module);

		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		// mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

		// 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	/**
	 * 创建只输出非Null且非Empty(如List.isEmpty)的属性到Json字符串的Mapper,建议在外部接口中使用.
	 */
	public static JsonMapper nonEmptyMapper() {
		return new JsonMapper(Include.NON_EMPTY);
	}

	/**
	 * 创建只输出初始值被改变的属性到Json字符串的Mapper, 最节约的存储方式，建议在内部接口中使用。
	 */
	public static JsonMapper nonDefaultMapper() {
		return new JsonMapper(Include.NON_DEFAULT);
	}

	/**
	 * Object可以是POJO，也可以是Collection或数组。
	 * 如果对象为Null, 返回"null".
	 * 如果集合为空集合, 返回"[]".
	 */
	public String toJson(Object object) {

		try {
			return mapper.writeValueAsString(object);
		} catch (IOException e) {
			logger.warn("write to json string error:" + object, e);
			return null;
		}
	}

	public <T> T getBean(String jsonString, Class<T> beanClazz) {
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}

		try {
			return mapper.readValue(jsonString, beanClazz);
		} catch (Exception e) {
			logger.warn("convert string to bean.", e);
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	public <T> List<T> getListBean(String jsonString, Class<T> beanClazz) {
		try {
			JavaType typeRef = TypeFactory.defaultInstance().constructParametricType(List.class, beanClazz);
			return mapper.readValue(jsonString, typeRef);
		} catch (Exception e) {
			logger.warn("convert string to list bean.", e);
			return null;
		}
	}

	public <K, V> HashMap<K, V> getMapBean(String jsonString, Class<K> keyClazz, Class<V> valueClazz) {
		try {
			JavaType typeRef = TypeFactory.defaultInstance().constructMapType(HashMap.class, keyClazz, valueClazz);
			return mapper.readValue(jsonString, typeRef);
		} catch (Exception e) {
			logger.warn("convert string to map bean.", e);
			return null;
		}
	}

	public <K, IK, IV> HashMap<K, Map<IK, IV>> getMapBean(String jsonString, Class<K> keyClazz, Class<IK> innerKeyClazz,
			Class<IV> innerValueClazz) {
		try {
			JavaType innerType = TypeFactory.defaultInstance().constructMapType(HashMap.class, innerKeyClazz,
					innerValueClazz);
			JavaType keyType = TypeFactory.defaultInstance().constructType(keyClazz);
			JavaType typeRef = TypeFactory.defaultInstance().constructMapType(HashMap.class, keyType, innerType);
			return mapper.readValue(jsonString, typeRef);
		} catch (IOException e) {
			logger.warn("convert string to map bean.", e);
			return null;
		}
	}

	public <T> T getBean(String jsonString, JavaType type) {
		try {
			return mapper.readValue(jsonString, type);
		} catch (Exception e) {
			logger.warn("convert string to bean.", e);
			return null;
		}
	}

	public <T> T getBean(String jsonString, TypeReference<T> type) {
		try {
			return mapper.readValue(jsonString, type);
		} catch (Exception e) {
			logger.warn("convert string to bean.", e);
			return null;
		}
	}

	public Map<?, ?> convertToMap(Object object) {
		try {
			return mapper.convertValue(object, Map.class);
		} catch (Exception e) {
			logger.warn("convert object to map.", e);
			return null;
		}
	}

	/**
	 * 当JSON里只含有Bean的部分屬性時，更新一個已存在Bean，只覆蓋該部分的屬性.
	 */
	public void update(String jsonString, Object object) {
		try {
			mapper.readerForUpdating(object).readValue(jsonString);
		} catch (JsonProcessingException e) {
			logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
		} catch (IOException e) {
			logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
		}
	}

	/**
	 * 輸出JSONP格式數據.
	 */
	public String toJsonP(String functionName, Object object) {
		return toJson(new JSONPObject(functionName, object));
	}

	/**
	 * 設定是否使用Enum的toString函數來讀寫Enum,
	 * 為False時時使用Enum的name()函數來讀寫Enum, 默認為False.
	 * 注意本函數一定要在Mapper創建後, 所有的讀寫動作之前調用.
	 */
	public void enableEnumUseToString() {
		mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
	}

	/**
	 * 取出Mapper做进一步的设置或使用其他序列化API.
	 */
	public ObjectMapper getMapper() {
		return mapper;
	}

	public class Jackson2DateDeserializer extends DateDeserializer {

		private static final long serialVersionUID = -8949017939757220442L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser,
		 * com.fasterxml.jackson.databind.DeserializationContext)
		 */
		@Override
		public Date deserialize(JsonParser jp, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			try {
				return super.deserialize(jp, ctxt);
			} catch (Exception e) {
				Date convert = doConvertToDate(jp.getText(), Locale.CHINA);
				return convert;
			}
		}

		private DateFormat[] getDateFormats(Locale locale) {
			DateFormat ls = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat ss = new SimpleDateFormat("yyyy-MM-dd");

			DateFormat dt1 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, locale);
			DateFormat dt2 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale);
			DateFormat dt3 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);

			DateFormat d1 = DateFormat.getDateInstance(DateFormat.SHORT, locale);
			DateFormat d2 = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
			DateFormat d3 = DateFormat.getDateInstance(DateFormat.LONG, locale);

			DateFormat rfc3399 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			return new DateFormat[] { ls, ss, dt1, dt2, dt3, rfc3399, d1, d2, d3 };
		}

		private Date doConvertToDate(Object value, Locale locale) {
			Date result = null;
			if (value instanceof String) {
				DateFormat[] dfs = getDateFormats(locale);
				for (DateFormat df1 : dfs) {
					try {
						result = df1.parse(value.toString());
						if (result != null) {
							break;
						}
					} catch (ParseException ignore) {
					}
				}
			} else if (value instanceof Object[]) {
				// let's try to convert the first element only
				Object[] array = (Object[]) value;
				if (array.length >= 1) {
					Object v = array[0];
					result = doConvertToDate(v, locale);
				}
			} else if (Date.class.isAssignableFrom(value.getClass())) {
				result = (Date) value;
			}

			return result;
		}
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {

		String json = "[{\"asddd\":1}";

		boolean isJson = true;

		try {
			ObjectMapper m = new ObjectMapper();
			if (json.trim().startsWith("[")) {
				JavaType typeRef = TypeFactory.defaultInstance().constructParametricType(List.class, Map.class);
				m.readValue(json, typeRef);
			} else {
				JavaType typeRef = TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class,
						Object.class);
				m.readValue(json, typeRef);
				m.readValue(json, typeRef);
			}
		} catch (Exception e) {
			isJson = false;
		}

		System.out.println(isJson);
	}
}
