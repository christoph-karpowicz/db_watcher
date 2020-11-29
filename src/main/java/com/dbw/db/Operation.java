package com.dbw.db;

import com.dbw.log.ErrorMessages;

public enum Operation {
    INSERT("I"),
    UPDATE("U"),
    DELETE("D");

    public final String symbol;
 
    private Operation(String symbol) {
        this.symbol = symbol;
    }

    public static Operation valueOfSymbol(String symbol) throws Exception {
        for (Operation e : values()) {
            if (e.symbol.equals(symbol)) {
                return e;
            }
        }
        throw new Exception(ErrorMessages.UNKNOWN_DB_OPERATION);
    }
}
