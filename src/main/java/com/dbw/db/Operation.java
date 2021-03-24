package com.dbw.db;

import com.dbw.err.UnknownDbOperationException;
import com.dbw.log.ErrorMessages;

import java.util.Arrays;

public enum Operation {
    INSERT("I"),
    UPDATE("U"),
    DELETE("D");

    public final String symbol;
 
    Operation(String symbol) {
        this.symbol = symbol;
    }

    public static Operation valueOfSymbol(String symbol) throws UnknownDbOperationException {
        for (Operation e : values()) {
            if (e.symbol.equals(symbol)) {
                return e;
            }
        }
        throw new UnknownDbOperationException(ErrorMessages.UNKNOWN_DB_OPERATION);
    }

    public boolean in(Operation... operations) {
        return Arrays.asList(operations).contains(this);
    }
}
