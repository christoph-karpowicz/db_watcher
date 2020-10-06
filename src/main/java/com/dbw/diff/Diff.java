package com.dbw.diff;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public abstract class Diff {
    private Map<String, Object> oldState;
    private Map<String, Object> newState;
    private List<StateColumn> statePairs;
    
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

    public void createStatePairs() {
        statePairs = new ArrayList<StateColumn>();
        Set<String> columnNames = oldState.keySet();
        for (String columnName : columnNames) {
            String oldStateValue = (String)oldState.get(columnName);
            String newStateValue = (String)newState.get(columnName);
            StateColumn statePair = new StateColumn(columnName, oldStateValue, newStateValue);
            statePair.compare();
            statePairs.add(statePair);
        }
    }

    public List<StateColumn> getStatePairs() {
        return ImmutableList.copyOf(statePairs);
    }

}
