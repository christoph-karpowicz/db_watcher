package com.dbw.state;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class XmlColumnStates {
    @JacksonXmlElementWrapper(localName = "columnStates")
    private List<String> columnState;

    public List<String> getColumnState() {
        return columnState;
    }

    public void setColumnState(List<String> columnState) {
        this.columnState = columnState;
    }
}
