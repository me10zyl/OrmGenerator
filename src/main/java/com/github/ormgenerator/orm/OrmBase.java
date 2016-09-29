package com.github.ormgenerator.orm;

import java.sql.*;

/**
 * Created by ZyL on 2016/9/28.
 */
public abstract class OrmBase {
    protected Connection connection;
    protected PreparedStatement preparedStatement;
    protected ResultSet resultSet;
    protected Database database;
    protected String host;
    protected int port;
    protected String dbName;
    protected String username;
    protected String password;

    public enum Database {
        MySQL, Oracle
    }

    public OrmBase(String host, int port, String username, String password, String dbName, Database database) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.dbName = dbName;
        this.database = database;
    }

    protected Connection getConnection() {
        try {
            String url = null;
            switch (database) {
                case MySQL:
                    Class.forName("com.mysql.jdbc.Driver");
                    url = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
                    break;
                case Oracle:
                    Class.forName("com.mysql.jdbc.Driver");
                    break;
                default:
                    break;
            }
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    protected PreparedStatement getPreparedStatement(String sql) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = getConnection().prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preparedStatement;
    }

    protected void release() {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
