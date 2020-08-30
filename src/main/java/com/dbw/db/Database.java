package com.dbw.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.dbw.cfg.DatabaseConfig;

public abstract class Database {
    protected DatabaseConfig config;
    protected Connection conn;

    public Database(DatabaseConfig config) {
        this.config = config;
    }

    public abstract void connect();

    public void executeUpdate(String query) {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
    }

    public boolean objectExists(String query, String[] stringArgs) {
        boolean exists = false;
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (short i = 0; i < stringArgs.length; i++) {
                pstmt.setString(i + 1, stringArgs[i]);
            }
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                exists = rs.getBoolean("exists");
            }
            pstmt.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        return exists;
    }

    public abstract boolean auditTableExists();

    public abstract void createAuditTable();

    public abstract boolean auditFunctionExists();

    public abstract void createAuditFunction();

    public abstract boolean auditTriggerExists(String tableName);

    public abstract void createAuditTrigger(String tableName);

    public abstract void close();
    
}