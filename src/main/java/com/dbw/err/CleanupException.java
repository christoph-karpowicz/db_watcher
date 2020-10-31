package com.dbw.err;

public class CleanupException extends DbwException {

    public CleanupException(String errorMessage, Class childException) {
        super(errorMessage);
        setChildException(childException);
        setExit();
    }
    
}
