package com.dbw.err;

import java.util.Objects;

public class DbwException extends Exception {
    private Class childException;
    private boolean exit;

    public DbwException(String errorMessage) {
        super(errorMessage);
    }

    public void setExit() {
        this.exit = true;
    }

    protected void setChildException(Class childException) {
        this.childException = childException;
    }

    public void handle() {
        if (Objects.isNull(childException)) {
            System.out.printf("%s: %s\n", this.getClass().getName(), this.getMessage());
        } else {
            System.out.printf("%s\nCaused by %s: %s\n", this.getClass().getName(), childException.getName(), this.getMessage());
        }
        this.printStackTrace();

        if (exit) {
            System.exit(0);
        }
    }
    
}
