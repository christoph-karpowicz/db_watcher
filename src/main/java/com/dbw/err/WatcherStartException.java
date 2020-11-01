package com.dbw.err;

public class WatcherStartException extends DbwException {

    public WatcherStartException(String errorMessage, Exception childException) {
        super(errorMessage);
        setChildException(childException);
    }
    
}
