package com.dbw.diff;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dbw.db.Postgres;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

public class JsonDiff extends Diff {
    
    protected Map<String, Object> parseData(String data) {
        Map<String, Object> parsedJson;
        Map<String, Object> parsedData = new LinkedHashMap<String, Object>();
        try {
            parsedJson = new ObjectMapper().readValue(data, LinkedHashMap.class);
            List<Object> parsedJsonValues = new ArrayList<Object>(parsedJson.values());
            for (short i = 0; i < Postgres.COLUMN_NAMES.length; i++) {
                parsedData.put(Postgres.COLUMN_NAMES[i], parsedJsonValues.get(i));
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            e.printStackTrace();
        }
        return ImmutableMap.copyOf(parsedData);
    }

}
