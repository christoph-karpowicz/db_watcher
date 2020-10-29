package com.dbw.err;

public class PreparationException extends DbwException {

    public PreparationException(String errorMessage) {
        super(errorMessage);
        setExit();
    }

    public PreparationException(String errorMessage, Class childException) {
        super(errorMessage);
        setChildException(childException);
        setExit();
    }
    
}
