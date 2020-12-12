package com.dbw.err;

public class PurgeException extends DbwException {

    public PurgeException(String errorMessage, Exception childException) {
        super(errorMessage);
        setChildException(childException);
    }
    
}
