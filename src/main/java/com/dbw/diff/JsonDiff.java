package com.dbw.diff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

public class JsonDiff extends Diff {
    
    protected Map<String, Object> parseData(String data) {
        Map<String, Object> parsedJson;
        Map<String, Object> parsedData = new HashMap<String, Object>();
        try {
            parsedJson = new ObjectMapper().readValue(data, HashMap.class);
            String[] columnNames = new String[]{"id", "table_name", "old_state", "new_state", "operation", "query", "timestamp"};
            List<Object> parsedJsonValues = new ArrayList<Object>(parsedJson.values());
            for (short i = 0; i < columnNames.length; i++) {
                parsedData.put(columnNames[i], parsedJsonValues.get(i));
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        // System.out.println(parsedData);
        return ImmutableMap.copyOf(parsedData);
    }

}
