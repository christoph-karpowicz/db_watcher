package com.dbw.diff;

public class StateColumn {
    private boolean diff;
    private final String columnName;
    private final String oldState;
    private final String newState;
    private final int maxWidth;
    private boolean isCut;

    public StateColumn(String columnName, String oldState, String newState) {
        this.columnName = columnName;
        this.oldState = oldState;
        this.newState = newState;
        this.maxWidth = calculateMaxWidth();
    }

    private int calculateMaxWidth() {
        int maxLength = columnName.length();
        int oldStateLength = oldState == null ? 0 : oldState.length();
        int newStateLength = newState == null ? 0 : newState.length();
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
        boolean isInsertOrDeleteOperation = oldState == null || newState == null;
        if (!isInsertOrDeleteOperation && !oldState.equals(newState)) {
            setHasDiff();
        }
    }

}
