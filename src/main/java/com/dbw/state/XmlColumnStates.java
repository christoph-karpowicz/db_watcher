package com.dbw.state;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Getter;

@Getter
public class XmlColumnStates {
    @JacksonXmlElementWrapper(localName = XmlStateBuilder.XML_COLUMN_STATES_TAG)
    private List<XmlColumnState> columnStates;
}
