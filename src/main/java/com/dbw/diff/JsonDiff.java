package com.dbw.diff;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonDiff extends Diff {
    
    protected Map<String, Object> parseData(String data) {
        Map<String, Object> parsedData;
        try {
            parsedData = new ObjectMapper().readValue(data, HashMap.class);
        } catch (Exception e) {
            parsedData = new HashMap<String, Object>();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        return parsedData;
    }

}
