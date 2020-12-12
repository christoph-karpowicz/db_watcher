package com.dbw.diff;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dbw.db.Operation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public abstract class Diff {
    private Map<String, Object> oldState;
    private Map<String, Object> newState;
    private List<StateColumn> stateColumns;
    
    public void parseOldData(String oldData) throws JsonProcessingException, JsonMappingException {
        oldState = parseData(oldData);
    }

    public void parseNewData(String newData) throws JsonProcessingException, JsonMappingException {
        newState = parseData(newData);
    }

    protected abstract Map<String, Object> parseData(String data) throws JsonProcessingException, JsonMappingException;
    
    public Map<String, Object> getOldState() {
        return ImmutableMap.copyOf(oldState);
    }

    public Map<String, Object> getNewState() {
        return ImmutableMap.copyOf(newState);
    }

    public List<StateColumn> getStateColumns() {
        return ImmutableList.copyOf(stateColumns);
    }

    public Set<String> getStateColumnNames(Operation operation) {
        if (operation.equals(Operation.INSERT)) {
            return getNewState().keySet();
        } else {
            return getOldState().keySet();
        }
    }

}
