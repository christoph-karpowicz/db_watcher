package com.dbw.err;

public class DbConnectionException extends DbwException {

    public DbConnectionException(String errorMessage, Exception childException) {
        super(errorMessage);
        setChildException(childException);
    }

}
