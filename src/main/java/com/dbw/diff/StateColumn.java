package com.dbw.diff;

public class StateColumn {
    private boolean diff;
    private String columnName;
    private String oldState;
    private String newState;
    private int maxLength;
    private boolean isCut;

    public StateColumn(String columnName, String oldState, String newState) {
        this.columnName = columnName;
        this.oldState = oldState;
        this.newState = newState;
        this.maxLength = calculateMaxLength();
    }

    private int calculateMaxLength() {
        int maxLength = columnName.length();
        int oldStateLength = oldState.length();
        int newStateLength = newState.length();
        if (oldStateLength > maxLength) {
            maxLength = oldStateLength;
        }
        if (newStateLength > maxLength) {
            maxLength = newStateLength;
        }
        return maxLength;
    }

    public int getMaxLength() {
        return maxLength;
    }
    
    private void setHasDiff() {
        this.diff = true;
    }
    
    public boolean hasDiff() {
        return diff;
    }

    public String getColumnName() {
        return columnName;
    }
    
    public String getOldState() {
        return oldState;
    }
    
    public String getNewState() {
        return newState;
    }

    public boolean isCut() {
        return isCut;
    }

    public void setCut(boolean isCut) {
        this.isCut = isCut;
    }

    public void compare() {
        if (!oldState.equals(newState)) {
            setHasDiff();
        }
    }

}