package com.dbw.err;

public class AppInitException extends DbwException {
    
    public AppInitException(String errorMessage) {
        super(errorMessage);
        setExit();
    }

    public AppInitException(String errorMessage, Class childException) {
        super(errorMessage);
        setChildException(childException);
        setExit();
    }

}
