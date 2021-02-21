package com.dbw.diff;

import com.dbw.db.Common;
import com.dbw.db.Postgres;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import java.util.*;

public class JsonDiff extends Diff {
    private final String[] tableColumnNames;

    public JsonDiff(String[] tableColumnNames) {
        this.tableColumnNames = tableColumnNames;
    }

    protected Map<String, Object> parseData(String data) throws JsonProcessingException {
        Map<String, Object> parsedData = new LinkedHashMap<>();
        if (Strings.isNullOrEmpty(data)) {
            return ImmutableMap.copyOf(parsedData);
        }
        Map<String, Object> parsedJson = new ObjectMapper().readValue(data, LinkedHashMap.class);
        List<Object> parsedJsonValues = new ArrayList<>(parsedJson.values());
        for (short i = 0; i < tableColumnNames.length; i++) {
            Object columnStateValue = Optional.ofNullable(parsedJsonValues.get(i)).orElse(Common.NULL_AS_STRING);
            parsedData.put(tableColumnNames[i], columnStateValue);
        }
        return ImmutableMap.copyOf(parsedData);
    }

}
