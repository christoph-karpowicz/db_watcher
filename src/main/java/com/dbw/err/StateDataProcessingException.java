package com.dbw.err;

public class StateDataProcessingException extends DbwException {

    public StateDataProcessingException(String errorMessage, Exception childException) {
        super(errorMessage);
        setChildException(childException);
    }
    
}
