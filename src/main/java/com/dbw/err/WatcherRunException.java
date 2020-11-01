package com.dbw.err;

public class WatcherRunException extends DbwException {

    public WatcherRunException(String errorMessage, Exception childException) {
        super(errorMessage);
        setChildException(childException);
    }
    
}
