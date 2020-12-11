package com.dbw.err;

public class InitialAuditRecordDeleteException extends DbwException {

    public InitialAuditRecordDeleteException(String errorMessage, Exception childException) {
        super(errorMessage);
        setChildException(childException);
    }
    
}
