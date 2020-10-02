package com.dbw.state;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public class XmlColumnStates {
    @JacksonXmlElementWrapper(localName = XmlStateBuilder.XML_COLUMN_STATES_TAG)
    private List<XmlColumnState> columnStates;

    public List<XmlColumnState> getColumnStates() {
        return columnStates;
    }

    public void setColumnStates(List<XmlColumnState> columnStates) {
        this.columnStates = columnStates;
    }
}
