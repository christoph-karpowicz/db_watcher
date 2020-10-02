package com.dbw.diff;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.ImmutableMap;
import com.dbw.state.XmlColumnState;
import com.dbw.state.XmlColumnStates;

public class XmlDiff extends Diff {
    
    protected Map<String, Object> parseData(String data) {
        Map<String, Object> parsedData = new HashMap<String, Object>();
        try {
            XmlMapper xmlMapper = new XmlMapper();
            XmlColumnStates state = xmlMapper.readValue(data, XmlColumnStates.class);
            for (XmlColumnState columnState : state.getColumnStates()) {
                parsedData.put(columnState.getName(), columnState.getValue());
            }
        } catch (Exception e) {
            parsedData = new HashMap<String, Object>();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        return ImmutableMap.copyOf(parsedData);
    }

}
