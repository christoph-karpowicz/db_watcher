package com.dbw.db;

import com.dbw.cfg.DatabaseConfig;
import com.dbw.log.ErrorMessages;

public class DatabaseFactory {

    public static Database getDatabase(DatabaseConfig config) throws Exception {
        if (config.getType().equals(DatabaseType.POSTGRES.type)) {
            return new Postgres(config);
        } else if (config.getType().equals(DatabaseType.ORCL.type)) {
            return new Orcl(config);
        }
        throw new Exception(ErrorMessages.UNKNOWN_DB_TYPE);
    }
    
}