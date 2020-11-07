package com.dbw.err;

public class AppInitException extends DbwException {
    
    public AppInitException(String errorMessage, Exception childException) {
        super(errorMessage);
        setChildException(childException);
        if (childException instanceof DbwException) {
            setDebug(((DbwException)childException).getDebug());
        }
    }

}
