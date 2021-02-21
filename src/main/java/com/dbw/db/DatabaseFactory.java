package com.dbw.db;

import com.dbw.cfg.Config;
import com.dbw.cfg.DatabaseConfig;
import com.dbw.err.UnknownDbTypeException;
import com.dbw.log.ErrorMessages;

public class DatabaseFactory {

    public static Database getDatabase(Config config) throws UnknownDbTypeException {
        if (config.getDatabase().getType().equals(DatabaseType.POSTGRES.type)) {
            return new Postgres(config);
        } else if (config.getDatabase().getType().equals(DatabaseType.ORCL.type)) {
            return new Orcl(config);
        }
        throw new UnknownDbTypeException(ErrorMessages.UNKNOWN_DB_TYPE);
    }
    
}
