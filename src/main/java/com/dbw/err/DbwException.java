package com.dbw.err;

import java.util.Objects;

import com.dbw.app.App;

public class DbwException extends Exception {
    private Exception childException;
    private boolean isRecoverable = false;

    public DbwException(String errorMessage) {
        super(errorMessage);
    }

    public DbwException setRecoverable() {
        this.isRecoverable = true;
        return this;
    }

    protected void setChildException(Exception childException) {
        this.childException = childException;
    }

    public void handle() {
        if (App.options.getDebug()) {
            printDebugMessage();
        } else {
            printProductionMessage();
        }

        if (!isRecoverable) {
            System.exit(0);
        }
    }

    private void printDebugMessage() {
        if (Objects.isNull(childException)) {
            System.out.printf("%s: %s\n", this.getClass().getName(), this.getMessage());
            this.printStackTrace();
        } else {
            System.out.printf("%s\nCaused by %s: %s\n", this.getClass().getName(), childException.getClass().getName(), this.getMessage());
            childException.printStackTrace();
        }
    }
    
    private void printProductionMessage() {
        System.out.printf("ERROR: %s\n", this.getMessage());
    }
    
}
