package com.dbw.db;

public enum Operation {
    INSERT("I"),
    UPDATE("U"),
    DELETE("D");

    public final String symbol;
 
    private Operation(String symbol) {
        this.symbol = symbol;
    }

    public static Operation valueOfSymbol(String symbol) {
        for (Operation e : values()) {
            if (e.symbol.equals(symbol)) {
                return e;
            }
        }
        return null;
    }
}
