package com.dbw.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.dbw.cfg.Config;
import com.dbw.cfg.DatabaseConfig;
import com.dbw.log.Level;
import com.dbw.log.Logger;

public class Orcl extends Database {

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

    public void clean(List<String> watchedTables) {

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
