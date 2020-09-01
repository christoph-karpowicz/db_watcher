package com.dbw.db; 

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.dbw.cfg.Config;
import com.dbw.cfg.DatabaseConfig;

public class Postgres extends Database {

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

    public boolean auditTableExists() {
        String[] stringArgs = {Config.DEFAULT_SCHEMA, Config.DEFAULT_AUDIT_TABLE_NAME};
        return objectExists(PostgresQueries.FIND_AUDIT_TABLE, stringArgs);
    }

    public void createAuditTable() {
        executeUpdate(PostgresQueries.CREATE_AUDIT_TABLE, Config.DEFAULT_AUDIT_TABLE_NAME);
    }

    public void dropAuditTable() {
        executeUpdate("DROP TABLE " + Config.DEFAULT_AUDIT_TABLE_NAME);
    }

    public boolean auditFunctionExists() {
        String[] stringArgs = {};
        return objectExists(PostgresQueries.FIND_AUDIT_FUNCTION, stringArgs);
    }

    public void createAuditFunction() {
        executeUpdate(PostgresQueries.CREATE_AUDIT_FUNCTION);
    }

    public void dropAuditFunction() {
        executeUpdate(PostgresQueries.DROP_AUDIT_FUNCTION);
    }

    public boolean auditTriggerExists(String tableName) {
        String[] stringArgs = {"dbw_" + tableName + "_audit"};
        return objectExists(PostgresQueries.FIND_AUDIT_TRIGGER, stringArgs);
    }

    public void createAuditTrigger(String tableName) {
        executeUpdate(PostgresQueries.CREATE_AUDIT_TRIGGER, tableName, tableName);
    }

    public void dropAuditTrigger(String tableName) {
        executeUpdate(PostgresQueries.DROP_AUDIT_TRIGGER, tableName, tableName);
    }

    public String[] selectAuditTriggers() {
        String[] stringArgs = {};
        List<String> auditTriggers = selectStringArray(PostgresQueries.SELECT_AUDIT_TRIGGERS, stringArgs);
        String[] auditTriggerNames = new String[auditTriggers.size()];
        for (short i = 0; i < auditTriggers.size(); i++) {
            String auditTriggerName = auditTriggers.get(i).split("_")[1];
            auditTriggerNames[i] = auditTriggerName;
        }
        return auditTriggerNames;
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