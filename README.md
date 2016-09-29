Introduction
---
Database table to Java Pojo class, support JPA annotations.
<br/>
Support `MySQL`,`Oracle` database.(current version only support `MySQL`)
<br/>

Usage
---
```
OrmGenerator.Options options = new OrmGenerator.Options();
options.annotation = true;
options.comment = true;
OrmGenerator ormTool = new OrmGenerator("127.0.0.1", 3306, "username", "password", "dbName", OrmBase.Database.MySQL, options);
String entity = ormTool.generateEntity("table_name");
System.out.println(entity); //this is java pojo class string
```
OrmGenerator.Options has 3 option param
```
OrmGenerator.Options.annotation //generate JPA annotation, default false
OrmGenerator.Options.comment //generate Javadoc comment via field remark, default true
OrmGenerator.Options.mappingHandler // advanced mapping handler, custom field type mapping
```
