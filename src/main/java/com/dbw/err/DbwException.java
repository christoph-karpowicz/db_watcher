package com.dbw.err;

import java.util.Objects;

import com.dbw.app.App;
import com.dbw.log.Level;
import com.dbw.log.Logger;

public class DbwException extends Exception {
    private Exception childException;
    private boolean isRecoverable = false;
    private boolean debug = false;

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

    protected void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void handle() {
        if (debug || (!Objects.isNull(App.options) && App.options.getDebug())) {
            printDebugMessage();
        } else {
            printProductionMessage();
        }

        if (!isRecoverable) {
            System.exit(0);
        }
    }

    private void printDebugMessage() {
        if (childException == null) {
            System.out.printf("%s: %s\n", this.getClass().getName(), this.getMessage());
            this.printStackTrace();
        } else {
            System.out.printf("%s\nCaused by %s: %s\n", this.getClass().getName(), childException.getClass().getName(), this.getMessage());
            childException.printStackTrace();
        }
    }
    
    private void printProductionMessage() {
        Logger.log(Level.ERROR, this.getMessage());
    }
    
}
