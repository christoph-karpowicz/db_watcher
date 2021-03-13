package com.dbw.err;

public class RecoverableException extends DbwException {

    public RecoverableException(String name, String errorMessage, Exception childException) {
        super(errorMessage);
        setChildException(childException);
        setRecoverable();
    }
}
