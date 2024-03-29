package com.dbw.diff;

import com.dbw.db.Operation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

@Getter
public abstract class Diff {
    private Map<String, Object> oldState;
    private Map<String, Object> newState;

    public void parseOldData(String oldData) throws JsonProcessingException, SQLException  {
        oldState = parseData(oldData);
    }

    public void parseNewData(String newData) throws JsonProcessingException, SQLException  {
        newState = parseData(newData);
    }

    protected abstract Map<String, Object> parseData(String data) throws JsonProcessingException, SQLException;
    
    public Set<String> getStateColumnNames(Operation operation) {
        if (operation.equals(Operation.INSERT)) {
            return getNewState().keySet();
        } else {
            return getOldState().keySet();
        }
    }

}
