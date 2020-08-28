package com.dbw.db;

import java.sql.Connection;
import java.sql.DriverManager;

import com.dbw.cfg.DatabaseConfig;

public class Postgres extends Database {
    public Postgres(DatabaseConfig config) {
        super(config);
    }

    public void connect() {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                .getConnection("jdbc:"+config.getType()+"://"+config.getHost()+":"+config.getPort()+"/" + config.getName(),
                config.getUser(), config.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }
}