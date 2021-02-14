package com.dbw.diff;

import java.util.Objects;

public class StateColumn {
    private boolean diff;
    private String columnName;
    private String oldState;
    private String newState;
    private int maxWidth;
    private boolean isCut;

    public StateColumn(String columnName, String oldState, String newState) {
        this.columnName = columnName;
        this.oldState = oldState;
        this.newState = newState;
        this.maxWidth = calculateMaxWidth();
    }

    private int calculateMaxWidth() {
        int maxLength = columnName.length();
        int oldStateLength = Objects.isNull(oldState) ? 0 : oldState.length();
        int newStateLength = Objects.isNull(newState) ? 0 : newState.length();
        if (oldStateLength > maxLength) {
            maxLength = oldStateLength;
        }
        if (newStateLength > maxLength) {
            maxLength = newStateLength;
        }
        return maxLength;
    }

    public int getMaxWidth() {
        return maxWidth;
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
        boolean isInsertOrDeleteOperation = Objects.isNull(oldState) || Objects.isNull(newState);
        if (!isInsertOrDeleteOperation && !oldState.equals(newState)) {
            setHasDiff();
        }
    }

}