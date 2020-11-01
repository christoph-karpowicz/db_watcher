package com.dbw.err;

public class CleanupException extends DbwException {

    public CleanupException(String errorMessage, Exception childException) {
        super(errorMessage);
        setChildException(childException);
    }
    
}
