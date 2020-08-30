package com.dbw.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.dbw.cfg.Config;
import com.dbw.cfg.DatabaseConfig;

public class Postgres extends Database {
    private Connection conn;

    public Postgres(DatabaseConfig config) {
        super(config);
    }

    public void connect() {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(getConnectionString(), config.getUser(), config.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    private String getConnectionString() {
        return new StringBuilder()
            .append("jdbc:" + config.getType())
            .append("://" + config.getHost())
            .append(":" + config.getPort())
            .append("/" + config.getName())
            .toString();
    }

    public boolean findAuditTable() {
        boolean auditTableFound = false;
        try {
            PreparedStatement pstmt = conn.prepareStatement(
                "SELECT EXISTS (" +
                "    SELECT FROM information_schema.tables " +
                "    WHERE  table_schema = ?" +
                "    AND    table_name   = ?" +
                ") as audit_table_exists;"
            );
            pstmt.setString(1, Config.DEFAULT_SCHEMA);
            pstmt.setString(2, Config.DEFAULT_AUDIT_TABLE_NAME);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                auditTableFound = rs.getBoolean("audit_table_exists");
            }
            pstmt.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        return auditTableFound;
    }

    public void createAuditTable() {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(
                "CREATE TABLE " + Config.DEFAULT_AUDIT_TABLE_NAME + " " +
                "(id            INT PRIMARY KEY NOT NULL," +
                " old           TEXT, " +
                " new           TEXT, " +
                " operation     VARCHAR(6) NOT NULL, " +
                " timestamp     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL)"
            );
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        System.out.println("Database connection closed.");
    }
}