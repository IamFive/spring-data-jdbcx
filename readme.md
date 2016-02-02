# spring-data-jdbcx
Spring-data-jdbcx is an extention for spring-jdbc-template. Spring-data-jdbcx makes it easy to connect with databases.

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
