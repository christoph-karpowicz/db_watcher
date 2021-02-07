package com.dbw.err;

public class ConfigException extends DbwException {
    
    public ConfigException(String errorMessage, Exception childException) {
        super(errorMessage);
        setChildException(childException);
    }

    public ConfigException(String errorMessage) {
        super(errorMessage);
    }
    
}
