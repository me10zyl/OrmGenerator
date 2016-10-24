package com.github.ormgenerator;

import org.junit.Test;
import com.github.ormgenerator.orm.OrmBase;
import com.github.ormgenerator.orm.OrmGenerator;

import java.sql.SQLException;

/**
 * Created by ZyL on 2016/9/28.
 */
public class ORMToolTest {

    @Test
    public void test() throws SQLException {
        OrmGenerator.Options options = new OrmGenerator.Options();
        options.annotation = true;
        OrmGenerator ormTool = new OrmGenerator("127.0.0.1", 3306, "username", "password", "dbName", OrmBase.Database.MySQL, options);
        String entity = ormTool.generateEntity("table_name");
        System.out.println(entity);
    }
}
