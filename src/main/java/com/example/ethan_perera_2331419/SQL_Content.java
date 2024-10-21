package com.example.ethan_perera_2331419;

import javafx.scene.control.Alert;

import java.sql.*;

public class SQL_Content extends fundamental_tools {
    final String dbUrl = "jdbc:mysql://localhost:3306/personalized_news_system";
    Connection conn;
    PreparedStatement pstmt;
    ResultSet rs;
    String SQL_Query;

    // Constructor: Establishes the database connection
    public SQL_Content() {
        try {
            conn = DriverManager.getConnection(dbUrl);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database connection error.", Alert.AlertType.ERROR);
        }
    }

    // Sets the SQL query
    public void set_query(String SQL_Query) {
        this.SQL_Query = SQL_Query;
    }

    // Returns a PreparedStatement object without closing it prematurely
    public PreparedStatement getPreparedStatement() {
        try {
            pstmt = conn.prepareStatement(SQL_Query);
            return pstmt;
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error creating PreparedStatement.", Alert.AlertType.ERROR);
        }
        return null;
    }

    // Executes the query and returns the ResultSet without closing it prematurely
    public ResultSet get_ResultSet(PreparedStatement pstmt) {
        try {
            rs = pstmt.executeQuery();
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error executing query.", Alert.AlertType.ERROR);
        }
        return null;
    }

    // Closes the PreparedStatement and ResultSet when done
    public void closeResources() {
        try {
            if (rs != null) rs.close();  // Close ResultSet
            if (pstmt != null) pstmt.close();  // Close PreparedStatement
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Closes the database connection
    public void close_connection() {
        try {
            if (conn != null) conn.close();  // Close connection
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
