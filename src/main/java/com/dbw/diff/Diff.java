package com.dbw.diff;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public abstract class Diff {
    private Map<String, Object> oldState;
    private Map<String, Object> newState;
    private List<StateColumn> stateColumns;
    
    public void parseOldData(String oldData) {
        oldState = parseData(oldData);
    }

    public void parseNewData(String newData) {
        newState = parseData(newData);
    }

    protected abstract Map<String, Object> parseData(String data);
    
    public Map<String, Object> getOldState() {
        return ImmutableMap.copyOf(oldState);
    }

    public Map<String, Object> getNewState() {
        return ImmutableMap.copyOf(newState);
    }

    public List<StateColumn> getStateColumns() {
        return ImmutableList.copyOf(stateColumns);
    }

}
