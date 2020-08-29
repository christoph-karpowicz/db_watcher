package com.dbw.cfg;

import java.util.List;

public class Config {
    public static final String DEFAULT_PATH = "./config.yml";
    
    private DatabaseConfig database;
    private List<String> tables;

    public DatabaseConfig getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseConfig database) {
        this.database = database;
    }

    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }
}