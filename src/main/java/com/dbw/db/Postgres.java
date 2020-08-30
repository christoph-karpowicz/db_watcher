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
        executeUpdate(
            "CREATE TABLE " + Config.DEFAULT_AUDIT_TABLE_NAME + " " +
            "(id            SERIAL PRIMARY KEY NOT NULL," +
            " table_name    VARCHAR(100), " +
            " old           TEXT, " +
            " new           TEXT, " +
            " operation     VARCHAR(6) NOT NULL, " +
            " query         TEXT, " +
            " timestamp     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL)"
        );
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
        executeUpdate("DROP FUNCTION IF EXISTS dbw_audit_func;");
    }

    public boolean auditTriggerExists(String tableName) {
        String[] stringArgs = {"dbw_" + tableName + "_audit"};
        return objectExists(PostgresQueries.FIND_AUDIT_TRIGGER, stringArgs);
    }

    public void createAuditTrigger(String tableName) {
        executeUpdate(
            "CREATE TRIGGER dbw_" + tableName + "_audit" +
            " AFTER INSERT OR UPDATE OR DELETE ON " + tableName +
            " FOR EACH ROW EXECUTE PROCEDURE dbw_audit_func();"
        );
    }

    public void dropAuditTrigger(String tableName) {
        executeUpdate("DROP TRIGGER IF EXISTS dbw_" + tableName + "_audit ON " + tableName + ";");
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