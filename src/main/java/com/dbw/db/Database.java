package com.dbw.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.dbw.cfg.DatabaseConfig;

public abstract class Database {
    protected DatabaseConfig config;
    protected Connection conn;

    public Database(DatabaseConfig config) {
        this.config = config;
    }

    protected void executeUpdate(String query, Object... args) {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(prepareQuery(query, args));
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
    }

    private static String prepareQuery(String query, Object... args) {
        return String.format(query, args);
    }

    protected boolean objectExists(String query, String[] stringArgs) {
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

    protected List<String> selectStringArray(String query, String[] stringArgs) {
        List<String> result = new ArrayList<String>();
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (short i = 0; i < stringArgs.length; i++) {
                pstmt.setString(i + 1, stringArgs[i]);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("item"));
            }
            pstmt.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        return result;
    }

    public abstract void connect();
    
    public abstract boolean auditTableExists();

    public abstract void createAuditTable();

    public abstract boolean auditFunctionExists();

    public abstract void createAuditFunction();

    public abstract boolean auditTriggerExists(String tableName);

    public abstract void createAuditTrigger(String tableName);

    public abstract void dropAuditTrigger(String tableName);
    
    public abstract String[] selectAuditTriggers();

    public abstract void close();
    
}