package com.demo.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseUtil {
    private static final String URL="jdbc:mysql://localhost:3306/bank_db";
    private static final String USER="hari";
    private static final String PASSWORD="Hariraja$1";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL,USER,PASSWORD);
    }
}
