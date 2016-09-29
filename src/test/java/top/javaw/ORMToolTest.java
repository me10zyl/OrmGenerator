package top.javaw;

import org.junit.Test;
import top.javaw.orm.OrmBase;
import top.javaw.orm.OrmGenerator;

/**
 * Created by ZyL on 2016/9/28.
 */
public class ORMToolTest {

    @Test
    public void test() {
        OrmGenerator.Options options = new OrmGenerator.Options();
        options.annotation = true;
        OrmGenerator ormTool = new OrmGenerator("127.0.0.1", 3306, "username", "password", "dbName", OrmBase.Database.MySQL, options);
        String entity = ormTool.generateEntity("table_name");
        System.out.println(entity);
    }
}
