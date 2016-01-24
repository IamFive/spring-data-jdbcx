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

package com.woo.jdbcx.dialect;

import java.text.MessageFormat;

import com.woo.jdbcx.dialect.impl.MysqlDialect;
import com.woo.jdbcx.dialect.impl.PostgreDialect;

public enum Dialects {
	//@off
    mysql(MysqlDialect.class), 
    //    mariadb, sqlite, oracle, hsqldb, 
    postgresql(PostgreDialect.class), 
    //    sqlserver, db2, informix, h2, sqlserver2012;
    ;
	//@on

	private Class<? extends SQLDialect> clazz;

	Dialects(Class<? extends SQLDialect> clazz) {
		this.clazz = clazz;
	}

	public static Dialects of(String dialect) {
		try {
			Dialects d = Dialects.valueOf(dialect.toLowerCase());
			return d;
		} catch (IllegalArgumentException e) {
			String dialects = null;
			for (Dialects d : Dialects.values()) {
				if (dialects == null) {
					dialects = d.toString();
				} else {
					dialects += "," + d;
				}
			}

			String format = MessageFormat.format("dialect[{0}] is not support, available dialects are : [{1}]", dialect,
					dialects);
			throw new IllegalArgumentException(format);
		}
	}

	public static String[] dialects() {
		Dialects[] dialects = Dialects.values();
		String[] ds = new String[dialects.length];
		for (int i = 0; i < dialects.length; i++) {
			ds[i] = dialects[i].toString();
		}
		return ds;
	}

	public static String fromJdbcUrl(String jdbcUrl) {
		String[] dialects = dialects();
		for (String dialect : dialects) {
			if (jdbcUrl.indexOf(":" + dialect + ":") != -1) {
				return dialect;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		Dialects.of("unknown");
	}
}
