Introduction
---
Database table to Java Pojo class, support JPA annotations.
<br/>
Support `MySQL`,`Oracle` database.
<br/>

Basic Usage
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
OrmGenerator.Options.oldFieldComment //add Javadoc comment to old class files,default true
```
Advanced
---
####database student table
|id|name|score|
|:---:|:---:|:---:|
|1 | test|100|            
|2 | test2|99|
####database table fields comment

>id - __Student ID__
name - __Student name__
score - __Student score__

####existed testClass file
```
package abc;
import java.lang.*;
public class Student{
  private Long id;
  private String name;
  
  public void eat(){
      //this method will be reserved
  }
}
```
####Code
```
OrmGenerator.Options options = new OrmGenerator.Options();
options.annotation = true;
options.comment = true;
OrmGenerator ormTool = new OrmGenerator("127.0.0.1", 3306, "username", "password", "dbName", OrmBase.Database.MySQL, options);
InputStream in = this.getClass().getResourceAsStream("testClass"); //existed java pojo class file
String entity = ormTool.generateEntity("table_name", in);
System.out.println(entity); //this will output new pojo class file based on 'testClass'
```
####Output
```
public class Student{
  /** Student ID */
  private Long id;
  /** Student name */
  private String name;
  /** Student score */
  private String score;
  
  public void eat(){
        //this method will be reserved
  }
}
```
