package com.dbw.cfg;

import com.google.common.base.Strings;

public class DatabaseConfig {
    public static final String DEFAULT_SCHEMA = "public";

    private String name;
    private String schema;
    private String type;
    private String host;
    private int port;
    private String connectionString;
    private String user;
    private String password;
    private String driverPath;

    public String getName() {
        return name;
    }

    public String getSchema() {
        return Strings.isNullOrEmpty(schema) ? DEFAULT_SCHEMA : schema;
    }

    public String getType() {
        return type;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDriverPath() {
        return driverPath;
    }
}
