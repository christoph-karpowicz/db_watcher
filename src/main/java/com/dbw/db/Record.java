package com.dbw.db;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Record {
    private Map<String,Object> oldState;
    private Map<String,Object> newState;

    public void parseOldData(String oldData) {
        oldState = parseData(oldData);
    }

    public void parseNewData(String newData) {
        newState = parseData(newData);
    }
    
    private Map<String,Object> parseData(String data) {
        Map<String,Object> parsedData;
        try {
            parsedData = new ObjectMapper().readValue(data, HashMap.class);
        } catch (Exception e) {
            parsedData = new HashMap<String,Object>();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        return parsedData;
    }

    public Map<String, Object> getOldState() {
        return oldState;
    }

    public Map<String, Object> getNewState() {
        return newState;
    }
}
