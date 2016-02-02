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

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.woo.jdbcx.dialect.exception.NotImplementDialectException;
import com.woo.jdbcx.dialect.impl.Db2Dialect;
import com.woo.jdbcx.dialect.impl.H2Dialect;
import com.woo.jdbcx.dialect.impl.HsqldbDialect;
import com.woo.jdbcx.dialect.impl.InformixDialect;
import com.woo.jdbcx.dialect.impl.MariaDialect;
import com.woo.jdbcx.dialect.impl.MysqlDialect;
import com.woo.jdbcx.dialect.impl.OracleDialect;
import com.woo.jdbcx.dialect.impl.PostgreDialect;
import com.woo.jdbcx.dialect.impl.SqliteDialect;

public enum Databases {

	//@off
	// For regular MySQL, MariaDB and Google Cloud SQL.
    // Google Cloud SQL returns different names depending on the environment and the SDK version.
    // ex.: Google SQL Service/MySQL
    mysql("MySQL", MysqlDialect.class), 
    mariadb("MySQL", MariaDialect.class), 
    sqlite("SQLite",SqliteDialect.class), 
    hsqldb("HSQL Database Engine",HsqldbDialect.class), 
    postgresql("PostgreSQL",PostgreDialect.class), 
    h2("H2",H2Dialect.class), 
	oracle("Oracle", OracleDialect.class), 
    db2("DB2", Db2Dialect.class), 
    informix("informix", InformixDialect.class), 
    //sqlserver, 
    //sqlserver2012;
    ;
	//@on

	private static final Logger logger = LoggerFactory.getLogger(Databases.class);

	Class<? extends SQLDialect> dialect;
	String dbName;
	static List<String> names = new ArrayList<String>();

	Databases(String dbName, Class<? extends SQLDialect> dialect) {
		this.dbName = dbName;
		this.dialect = dialect;
	}

	public SQLDialect getDialect() {
		try {
			return this.dialect.newInstance();
		} catch (Exception e) {
			// should not happen
			logger.error("failed to create dialect instance", e);
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

	/**
	 * @param metaData
	 * @return 
	 * @throws SQLException 
	 */
	public static Databases fromMetaData(DatabaseMetaData metaData) throws SQLException {

		String dbName = metaData.getDatabaseProductName();
		int databaseMajorVersion = metaData.getDatabaseMajorVersion();
		int databaseMinorVersion = metaData.getDatabaseMinorVersion();

		String format = MessageFormat.format("Database: {0} ({1} {2}.{3})", metaData.getURL(), dbName,
				databaseMajorVersion, databaseMinorVersion);
		logger.info(format);

		if (dbName == null) {
			throw new NotImplementDialectException("Unable to determine database. Database Product name is null.");
		}

		if (dbName.startsWith("Microsoft SQL Server")) {
			// TODO different version
			throw new IllegalArgumentException("Microsoft SQL Server is not support for now.");
		} else if (dbName.startsWith("Oracle")) {
			// Oracle 12 supports row fetch, TODO
			return Databases.oracle;
		} else if (dbName.contains("Mysql")) {
			return Databases.mysql;
		} else {
			Databases[] databases = Databases.values();
			for (Databases db : databases) {
				if (dbName.toLowerCase().startsWith(db.dbName.toLowerCase())) {
					return db;
				}
			}
		}

		throw new NotImplementDialectException("Unable to determine database. Database Product name is " + dbName);
	}

	/**
	 * 
	 * @param jdbcUrl
	 * @return
	 */
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
