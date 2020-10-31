package com.dbw.err;

import java.util.Objects;

import com.dbw.app.App;

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
        if (App.options.getDebug()) {
            printDebugMessage();
        } else {
            printProductionMessage();
        }

        if (exit) {
            System.exit(0);
        }
    }

    private void printDebugMessage() {
        if (Objects.isNull(childException)) {
            System.out.printf("%s: %s\n", this.getClass().getName(), this.getMessage());
        } else {
            System.out.printf("%s\nCaused by %s: %s\n", this.getClass().getName(), childException.getName(), this.getMessage());
        }
        this.printStackTrace();
    }
    
    private void printProductionMessage() {
        System.out.printf("ERROR: %s\n", this.getMessage());
    }
    
}
