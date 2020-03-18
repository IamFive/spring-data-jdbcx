# spring-data-jdbcx
Spring-data-jdbcx is an extention for spring-jdbc-template. Spring-data-jdbcx makes it easy to connect with databases.

## how to use

You can download from maven central, add dependency to your pom.xml

```
	<dependency>
		<groupId>net.turnbig</groupId>
		<artifactId>spring-data-jdbcx</artifactId>
		<version>2.0.6</version>
	</dependency>
```

For more example, please check the [unit tests](https://github.com/IamFive/spring-data-jdbcx/tree/master/src/test/java/net/turnbig/jdbcx/test). 


To integrate with spring boot, add `net.turnbig.jdbcx` to auto scan package. 
And if you want to use the sql template feature, add configurations below:

```
# SQL template loading path
spring.jdbcx.sql.template-path = classpath:/sql-template
# SQL template encoding
spring.jdbcx.sql.template-encoding = UTF-8
# SQL auto refresh delay
spring.jdbcx.sql.update-delay = 0
```



## Planed Features

### abstract more friendly jdbc-template-API
- [x] use Bean/Map as named-query parameter
- [x] return Bean/Map for query directly
- [x] pageable(order by included) support 

### hiding lesser-used jdbc-template-API
- [x] you can still use the API by get original template

### sql template support
- [x] use freemarker as template engineer - (dynamic sql benifed from freemarker)
- [x] use seperated xml to define sql templates

### customer converter
- [x] you can inject a customer converter service for the type not support by default
- [x] PGobject to Map/List/Bean converter


## why spring-data-jdbcx
For database tools, I think the most important parts are:
- full control of SQL (what sql will run, query/update/insert/delete/batch/etc)
- dynamic & clear & structed SQL seperated from code (SQL loaded from xml or somewhere, Mybatis do pretty good in this part)
- simply result-set mapping to JavaBean/Map/Integer/String/etc
- Pagination & dynamic Order inject support

There got many database tools, Hibernate, Mybatis, Jooq etc. Of course they are good, but spring-data-jdbcx will be more Smart. With spring-data-jdbcx, you will benifit from all spring-jdbc-template's adventage(low level api), you will got high level mapping too. You could pass JavaBean/Map as parameter, you could simply get Bean/map/primitive-type as result. It got pagination featurn which almost support all kind of select-sql and database. to be continue...
