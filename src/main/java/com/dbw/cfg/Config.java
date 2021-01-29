package com.dbw.cfg;

import java.util.Collections;
import java.util.List;

public class Config {
    public static final String DEFAULT_SCHEMA = "public";
    
    private DatabaseConfig database;
    private List<String> tables;

    public DatabaseConfig getDatabase() {
        return database;
    }

    public List<String> getTables() {
        return Collections.unmodifiableList(tables);
    }
}