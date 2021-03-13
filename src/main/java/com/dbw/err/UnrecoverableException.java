package com.dbw.err;

public class UnrecoverableException extends DbwException {

    public UnrecoverableException(String name, String errorMessage, Exception childException) {
        super(errorMessage);
        setChildException(childException);
        setRecoverable();
    }
}
