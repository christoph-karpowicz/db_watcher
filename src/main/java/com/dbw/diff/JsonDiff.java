package com.dbw.diff;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.dbw.db.Common;
import com.dbw.db.Postgres;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

public class JsonDiff extends Diff {
    private Postgres db;
    private String tableName;

    public JsonDiff(Postgres db, String tableName) {
        this.db = db;
        this.tableName = tableName;
    }
    
    protected Map<String, Object> parseData(String data) throws Exception {
        Map<String, Object> parsedData = new LinkedHashMap<String, Object>();
        if (Strings.isNullOrEmpty(data)) {
            return ImmutableMap.copyOf(parsedData);
        }
        Map<String, Object> parsedJson = new ObjectMapper().readValue(data, LinkedHashMap.class);
        List<Object> parsedJsonValues = new ArrayList<Object>(parsedJson.values());
        String[] tableColumnNames = db.getWatchedTablesColumnNames().get(tableName);
        for (short i = 0; i < tableColumnNames.length; i++) {
            Object columnStateValue = Optional.ofNullable(parsedJsonValues.get(i)).orElse(Common.NULL_AS_STRING);
            parsedData.put(tableColumnNames[i], columnStateValue);
        }
        return ImmutableMap.copyOf(parsedData);
    }

}
