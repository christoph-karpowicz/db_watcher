package com.dbw.diff;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dbw.db.Postgres;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

public class JsonDiff extends Diff {
    
    protected Map<String, Object> parseData(String data) throws Exception {
        Map<String, Object> parsedData = new LinkedHashMap<String, Object>();
        if (Strings.isNullOrEmpty(data)) {
            return ImmutableMap.copyOf(parsedData);
        }
        Map<String, Object> parsedJson = new ObjectMapper().readValue(data, LinkedHashMap.class);
        List<Object> parsedJsonValues = new ArrayList<Object>(parsedJson.values());
        for (short i = 0; i < Postgres.COLUMN_NAMES.length; i++) {
            parsedData.put(Postgres.COLUMN_NAMES[i], parsedJsonValues.get(i));
        }
        return ImmutableMap.copyOf(parsedData);
    }

}
