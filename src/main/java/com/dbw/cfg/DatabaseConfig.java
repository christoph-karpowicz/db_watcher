package com.dbw.cfg;

import com.google.common.base.Strings;
import lombok.Getter;

@Getter
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

    public String getSchema() {
        return Strings.isNullOrEmpty(schema) ? DEFAULT_SCHEMA : schema;
    }
}
