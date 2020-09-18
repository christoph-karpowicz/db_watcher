package com.dbw.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.dbw.cfg.Config;
import com.dbw.cfg.DatabaseConfig;
import com.dbw.log.Level;
import com.dbw.log.Logger;

public class Orcl extends Database {
    public final static short STATE_COLUMN_MAX_LENGTH = 2000;

    public Orcl(DatabaseConfig config) {
        super(config);
    }

    public void connect() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection conn = DriverManager.getConnection(getConnectionString(), config.getUser(), config.getPassword());
            setConn(conn);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        Logger.log(Level.INFO, "Database opened successfully.");
    }

    private String getConnectionString() {
        return config.getConnectionString();
    }

    public void prepare(List<String> watchedTables) {
        OrclPrepareService orclPrepareService = new OrclPrepareService(this, watchedTables);
        orclPrepareService.prepare();
    }

    public boolean auditTableExists() {
        String[] stringArgs = {config.getUser().toUpperCase(), Config.DEFAULT_AUDIT_TABLE_NAME.toUpperCase()};
        return objectExists(OrclQueries.FIND_AUDIT_TABLE, stringArgs);
    }

    public void createAuditTable() {
        executeUpdate(OrclQueries.CREATE_AUDIT_TABLE, Config.DEFAULT_AUDIT_TABLE_NAME);
        Logger.log(Level.INFO, "Audit table has been created.");
    }

    public void dropAuditTable() {
        executeUpdate("DROP TABLE " + Config.DEFAULT_AUDIT_TABLE_NAME);
        Logger.log(Level.INFO, "Audit table has been dropped.");
    }

    public boolean auditTriggerExists(String tableName) {
        String[] stringArgs = {"DBW_" + tableName + "_AUDIT"};
        return objectExists(OrclQueries.FIND_AUDIT_TRIGGER, stringArgs);
    }

    public void createAuditTrigger(String tableName, String query) {
        executeUpdate(query);
        Logger.log(Level.INFO, String.format("Audit trigger for table \"%s\" has been created.", tableName));
    }

    public void dropAuditTrigger(String tableName) {
        executeUpdate(OrclQueries.DROP_AUDIT_TRIGGER, tableName, tableName);
        Logger.log(Level.INFO, String.format("Audit trigger for table \"%s\" has been dropped.", tableName));
    }

    public String[] selectAuditTriggers() {
        String[] stringArgs = {};
        List<String> auditTriggers = selectStringArray(OrclQueries.SELECT_AUDIT_TRIGGERS, stringArgs);
        String[] auditTriggerNames = new String[auditTriggers.size()];
        for (short i = 0; i < auditTriggers.size(); i++) {
            String auditTriggerName = auditTriggers.get(i).split("_")[1];
            auditTriggerNames[i] = auditTriggerName;
        }
        return auditTriggerNames;
    }

    public Column[] selectTableColumns(String tableName) {
        List<Column> result = new ArrayList<Column>();
        try {
            PreparedStatement pstmt = getConn().prepareStatement(OrclQueries.SELECT_TABLE_COLUMNS);
            pstmt.setString(1, tableName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Column column = new Column();
                column.setName(rs.getString("COLUMN_NAME"));
                column.setDataType(rs.getString("DATA_TYPE"));
                result.add(column);
            }
            pstmt.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        Column[] columns = new Column[result.size()];
        return result.toArray(columns);
    }
 
    public void clean(List<String> watchedTables) {
        for (String tableName : watchedTables) {
            dropAuditTrigger(tableName);
        }
        dropAuditTable();
    }

    public void close() {
        try {
            getConn().close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        Logger.log(Level.INFO, "Database connection closed.");
    }
    
}
