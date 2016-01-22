spring-data-jdbcx
====

1. abstract more friendly jdbc-template-API
- use Bean as named-query parameter
- return Bean for query directly
- pagable support

2. hiding lesser-used jdbc-template-API
- you can still use the API by get original template

3. sql template support
- use freemarker as template engineer - (dynamic sql benifed from freemarker)
- use seperated xml to define sql templates