package com.dbw.diff;

import java.util.Map;

public abstract class Diff {
    private Map<String, Object> oldState;
    private Map<String, Object> newState;
    
    public void parseOldData(String oldData) {
        oldState = parseData(oldData);
    }

    public void parseNewData(String newData) {
        newState = parseData(newData);
    }

    protected abstract Map<String, Object> parseData(String data);
    
    public Map<String, Object> getOldState() {
        return oldState;
    }

    public Map<String, Object> getNewState() {
        return newState;
    }

}
