package com.dbw.err;

public class WatcherRunException extends DbwException {

    public WatcherRunException(String errorMessage, Class childException) {
        super(errorMessage);
        setChildException(childException);
    }
    
}
