package com.dbw.db;

public enum DatabaseType {
    POSTGRES("postgresql");

    public final String type;

    private DatabaseType(String type) {
        this.type = type;
    }
}