spring-data-jdbcx
====

1. abstract more friendly jdbc-template-API
- use Bean as named-query parameter
- return Bean for query directly

2. hiding lesser-used jdbc-template-API
- you can still use the API by get original template

3. add JPA-style implemention
- Pagable support
- common JPARepository methods

4. sql template support
- use freemarker as template engineer - (dynamic sql benifed from freemarker)
- use seperated xml to define sql templates