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



package com.woo.jdbcx.dialect.impl;

import com.woo.jdbcx.dialect.SQLDialect;
import com.woo.jdbcx.dialect.Dialects;
import com.woo.jdbcx.dialect.SqlParser;

/**
 * @author liuzh
 */
public abstract class AbstractSQLDialect implements SQLDialect {
    //处理SQL
    public static final SqlParser sqlParser = new SqlParser();

    public static SQLDialect newParser(Dialects dialect) {
        SQLDialect parser = null;
        switch (dialect) {
            case mysql:
//            case mariadb:
//            case sqlite:
//                parser = new MysqlParser();
//                break;
//            case oracle:
//                parser = new OracleParser();
//                break;
//            case hsqldb:
//                parser = new HsqldbParser();
//                break;
//            case sqlserver:
//                parser = new SqlServerParser();
//                break;
//            case sqlserver2012:
//                parser = new SqlServer2012Dialect();
//                break;
//            case db2:
//                parser = new Db2Parser();
//                break;
            case postgresql:
                parser = new PostgreDialect();
                break;
//            case informix:
//                parser = new InformixParser();
//                break;
//            case h2:
//                parser = new H2Parser();
//                break;
            default:
                throw new RuntimeException("分页插件" + dialect + "方言错误!");
        }
        return parser;
    }



    public String getCountSql(final String sql) {
        return sqlParser.getSmartCountSql(sql);
    }

    public abstract String getPageSql(String sql);

}
