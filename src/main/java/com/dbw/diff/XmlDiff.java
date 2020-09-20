package com.dbw.diff;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import com.dbw.state.XmlColumnStates;

public class XmlDiff extends Diff {
    
    protected Map<String, Object> parseData(String data) {
        Map<String, Object> parsedData = new HashMap<String, Object>();
        try {
            XmlMapper xmlMapper = new XmlMapper();
            XmlColumnStates state = xmlMapper.readValue(data, XmlColumnStates.class);
            System.out.println(state.getColumnState());
        } catch (Exception e) {
            parsedData = new HashMap<String, Object>();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        return parsedData;
    }

}
