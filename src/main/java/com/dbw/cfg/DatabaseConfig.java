package com.dbw.cfg;

public class DatabaseConfig {
    private String name;
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
