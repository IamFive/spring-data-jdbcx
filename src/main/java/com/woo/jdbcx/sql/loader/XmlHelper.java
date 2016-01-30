/**
 * @(#)XmlUtils.java 2012-1-6
 * Copyright 2000-2012 by iampurse@vip.qq.com. All rights reserved.
 */
package com.woo.jdbcx.sql.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * A XML utility base on XStream.
 * 
 * @author qianbiao.wu
 * @date 2012-1-6
 * @version $Revision$
 */
public class XmlHelper {

	private static Logger logger = LoggerFactory.getLogger(XmlHelper.class);

	private static XStream xmlConvertor;

	static {
		xmlConvertor = new XStream();
		xmlConvertor.autodetectAnnotations(true);
		xmlConvertor.registerConverter(new DateConverter(Locale.CHINA));
	}

	@SuppressWarnings("unchecked")
	public static <T> T fromXML(String xml) {
		return (T) xmlConvertor.fromXML(xml);
	}

	@SuppressWarnings("unchecked")
	public static <T> T fromXML(String xml, Class<?>... clazz) {
		xmlConvertor.processAnnotations(clazz);
		return (T) xmlConvertor.fromXML(xml);
	}

	public static String toXML(Object object, Class<?>... clazz) {
		xmlConvertor.processAnnotations(clazz);
		return xmlConvertor.toXML(object);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T fromXML(InputStream is, Class<?>... clazz) {
		xmlConvertor.processAnnotations(clazz);
		return (T) xmlConvertor.fromXML(is);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T fromXML(File file, Class<?>... clazz) {
		try {
			String xml = FileUtils.readFileToString(file, "UTF-8");
			xmlConvertor.processAnnotations(clazz);
			return (T) xmlConvertor.fromXML(xml);
		} catch (IOException e) {
			logger.warn("Could not read file.", e);
			throw new RuntimeException(e);
		}

	}

	public static void toXML(Object object, File file) {
		try {
			String xml = xmlConvertor.toXML(object);
			FileUtils.writeStringToFile(file, xml, "UTF-8");
		} catch (IOException e) {
			logger.warn("Could not read file.", e);
			throw new RuntimeException(e);
		}
	}

	public static class DateConverter implements Converter {

		private final Locale locale;

		public DateConverter(Locale locale) {
			super();
			this.locale = locale;
		}

		@Override
		@SuppressWarnings("rawtypes")
		public boolean canConvert(Class clazz) {
			return Date.class.isAssignableFrom(clazz);
		}

		@Override
		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
			Date date = (Date) value;
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			writer.setValue(formatter.format(date));
		}

		@Override
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			Date result = null;
			String value = reader.getValue();
			DateFormat[] dfs = getDateFormats(locale);
			for (DateFormat df1 : dfs) {
				try {
					result = df1.parse(value.toString());
					if (result != null) {
						break;
					}
				} catch (ParseException ignore) {
					// ignore here.
				}
			}

			if (value != null && value.trim().length() > 0 && (result == null)) {
				throw new ConversionException(String.format("Could not parse %s to Date.class", value));
			}

			return result;
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

	}

}
