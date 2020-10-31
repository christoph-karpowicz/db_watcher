package com.dbw.err;

public class WatcherStartException extends DbwException {

    public WatcherStartException(String errorMessage, Class childException) {
        super(errorMessage);
        setChildException(childException);
        setExit();
    }
    
}
