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
    private Connection conn;

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

    public static String prepareQuery(String query, Object... args) {
        if (args.length > 0) {
            return String.format(query, args);
        }
        return query;
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

    public abstract List<AuditRecord> selectAuditRecords(int fromId);
    
    protected List<AuditRecord> selectAuditRecords(String query, int fromId) {
        List<AuditRecord> result = new ArrayList<AuditRecord>();
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, fromId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                AuditRecord auditRecord = new AuditRecord();
                auditRecord.setId(rs.getInt("id"));
                auditRecord.setTableName(rs.getString("table_name"));
                auditRecord.setOldData(rs.getString("old_state"));
                auditRecord.setNewData(rs.getString("new_state"));
                auditRecord.setOperation(rs.getString("operation"));
                if (columnExists(rs, "query")) {
                    auditRecord.setQuery(rs.getString("query"));
                }
                auditRecord.setTimestamp(rs.getDate("timestamp"));
                result.add(auditRecord);
            }
            pstmt.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        return result;
    }

    private boolean columnExists(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
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

    public abstract int selectMaxId();

    protected int selectMaxId(String query) {
        int result = 0;
        try {
            Statement pstmt = conn.createStatement();
            ResultSet rs = pstmt.executeQuery(query);
            if (rs.next()) {
                result = rs.getInt("max");
            } else {
                throw new Exception("Couldn't select audit table's max id.");
            }
            pstmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        return result;
    }

    public Connection getConn() {
        return conn;
    }

    protected void setConn(Connection conn) {
        this.conn = conn;
    }

    public DatabaseConfig getConfig() {
        return config;
    }

    public abstract void connect();

    public abstract void prepare(List<String> watchedTables);

    public abstract void clean(List<String> watchedTables);

    public abstract void close();
    
}