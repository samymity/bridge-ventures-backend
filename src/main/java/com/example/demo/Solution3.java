package com.example.demo;


import java.sql.*;

public class Solution3 {
  public static void main(String[] args) {
    String password = "Software123#";
    String url = "jdbc:postgresql://database-1.c1fzwwqtvjai.us-east-1.rds.amazonaws.com:5432/postgres";
    
    try (Connection conn = DriverManager.getConnection(url, "postgres", password)) {
        System.out.println("Connected to RDS PostgreSQL successfully!");

        Statement stmt = conn.createStatement();

        // 1. Create Table
        String createTable = "CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY, name VARCHAR(50), role VARCHAR(50))";
        stmt.executeUpdate(createTable);
        System.out.println("Table created.");

        // 2. Insert Data
        String insertData = "INSERT INTO users (name, role) VALUES ('Sandeep', 'Full Stack Developer')";
        stmt.executeUpdate(insertData);
        System.out.println("Data inserted.");

        // 3. Read Data
        ResultSet rs = stmt.executeQuery("SELECT * FROM users");
        while (rs.next()) {
            System.out.println("User: " + rs.getString("name") + " | Role: " + rs.getString("role"));
        }
    }
     catch (SQLException e) {
      System.err.println("Database error: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }
}