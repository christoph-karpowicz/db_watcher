package com.dbw.db;

import com.dbw.cfg.DatabaseConfig;

public abstract class Database {
    DatabaseConfig config;

    public Database(DatabaseConfig config) {
        this.config = config;
    }

    public abstract void connect();

    public abstract boolean findAuditTable();

    public abstract void createAuditTable();

    public abstract void close();
    
}