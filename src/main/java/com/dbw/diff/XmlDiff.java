package com.dbw.diff;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.dbw.db.Common;
import com.dbw.state.XmlColumnState;
import com.dbw.state.XmlColumnStates;

public class XmlDiff extends Diff {
    
    protected Map<String, Object> parseData(String data) throws Exception {
        Map<String, Object> parsedData = new LinkedHashMap<String, Object>();
        if (Strings.isNullOrEmpty(data)) {
            return ImmutableMap.copyOf(parsedData);
        }
        XmlMapper xmlMapper = new XmlMapper();
        XmlColumnStates state = xmlMapper.readValue(data, XmlColumnStates.class);
        for (XmlColumnState columnState : state.getColumnStates()) {
            String columnStateValue = Objects.isNull(columnState.getValue()) ? Common.NULL_AS_STRING : columnState.getValue();
            parsedData.put(columnState.getName(), columnStateValue);
        }
        return ImmutableMap.copyOf(parsedData);
    }

}
