package com.dbw.err;

public class PreparationException extends DbwException {

    public PreparationException(String errorMessage, Exception childException) {
        super(errorMessage);
        setChildException(childException);
    }
    
}
