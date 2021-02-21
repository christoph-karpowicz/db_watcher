package com.dbw.err;

public class CachePersistenceException extends DbwException {

    public CachePersistenceException(String errorMessage, Exception childException) {
        super(errorMessage);
        setChildException(childException);
    }
}
