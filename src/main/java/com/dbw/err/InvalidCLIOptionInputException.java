package com.dbw.err;

public class InvalidCLIOptionInputException extends DbwException {

    public InvalidCLIOptionInputException(String errorMessage, Exception childException, boolean debug) {
        super(errorMessage);
        setChildException(childException);
        setDebug(debug);
    }
    
}
