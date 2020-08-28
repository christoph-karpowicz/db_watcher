package com.dbw.db;

import com.dbw.cfg.DatabaseConfig;

public abstract class Database {
    DatabaseConfig config;

    public Database(DatabaseConfig config) {
        this.config = config;
    }

    public abstract void connect();
}