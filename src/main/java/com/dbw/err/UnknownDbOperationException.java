package com.dbw.err;

public class UnknownDbOperationException extends DbwException {

    public UnknownDbOperationException(String errorMessage) {
        super(errorMessage);
    }
    
}
