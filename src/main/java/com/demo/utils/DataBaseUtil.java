package com.demo.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseUtil {
    private static final String URL="jdbc:mysql://mysql:3306/bank_db";
    private static final String USER="user";
    private static final String PASSWORD="password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL,USER,PASSWORD);
    }
}
