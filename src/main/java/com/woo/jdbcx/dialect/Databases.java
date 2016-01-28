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
import java.util.ArrayList;
import java.util.List;

import com.woo.jdbcx.dialect.impl.H2Dialect;
import com.woo.jdbcx.dialect.impl.HsqldbDialect;
import com.woo.jdbcx.dialect.impl.MariaDialect;
import com.woo.jdbcx.dialect.impl.MysqlDialect;
import com.woo.jdbcx.dialect.impl.PostgreDialect;
import com.woo.jdbcx.dialect.impl.SqliteDialect;

public enum Databases {
	//@off
    mysql(MysqlDialect.class), 
    mariadb(MariaDialect.class), 
    sqlite(SqliteDialect.class), 
    hsqldb(HsqldbDialect.class), 
    postgresql(PostgreDialect.class), 
    h2(H2Dialect.class), 
    //sqlserver, 
	//oracle, 
    //db2, 
    //informix, 
    //sqlserver2012;
    ;
	//@on

	Class<? extends SQLDialect> dialect;
	static List<String> names = new ArrayList<String>();

	Databases(Class<? extends SQLDialect> dialect) {
		this.dialect = dialect;
	}

	public SQLDialect getDialect() {
		try {
			return this.dialect.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// should not happen
		}
		return null;
	}

	public static Databases of(String name) {
		try {
			Databases d = Databases.valueOf(name.toLowerCase());
			return d;
		} catch (IllegalArgumentException e) {
			String format = MessageFormat.format("Database[{0}] is not support, available databases : {1}", name,
					names());
			throw new IllegalArgumentException(format);
		}
	}

	public static Databases fromJdbcUrl(String jdbcUrl) {
		for (String database : names()) {
			if (jdbcUrl.indexOf(":" + database + ":") != -1) {
				return Databases.of(database);
			}
		}
		return null;
	}

	public static List<String> names() {
		if (names.size() == 0) {
			Databases[] databases = Databases.values();
			for (Databases db : databases) {
				names.add(db.name());
			}
		}
		return names;
	}

	public static void main(String[] args) {
		Databases.of("unknown");
	}
}
