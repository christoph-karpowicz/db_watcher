package com.dbw.diff;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.dbw.state.XmlColumnState;
import com.dbw.state.XmlColumnStates;

public class XmlDiff extends Diff {
    
    protected Map<String, Object> parseData(String data) {
        Map<String, Object> parsedData = new LinkedHashMap<String, Object>();
        if (Strings.isNullOrEmpty(data)) {
            return ImmutableMap.copyOf(parsedData);
        }

        try {
            XmlMapper xmlMapper = new XmlMapper();
            XmlColumnStates state = xmlMapper.readValue(data, XmlColumnStates.class);
            for (XmlColumnState columnState : state.getColumnStates()) {
                parsedData.put(columnState.getName(), columnState.getValue());
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            e.printStackTrace();
        }
        return ImmutableMap.copyOf(parsedData);
    }

}
