package com.dbw.state;

public class StatePair {
    private boolean diff;
    private String columnName;
    private String oldState;
    private String newState;

    public StatePair(String columnName, String oldState, String newState) {
        this.columnName = columnName;
        this.oldState = oldState;
        this.newState = newState;
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

    public void compare() {
        if (!oldState.equals(newState)) {
            setHasDiff();
        }
    }

}