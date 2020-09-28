package com.dbw.state;

import java.util.ArrayList;
import java.util.List;

import com.dbw.db.Column;

public class XmlStateBuilder {

    private final String XML_DECLARATION = "'<?xml version=\"1.0\" encoding=\"UTF-8\"?>'";
    private final String XML_ROOT_TAG = "XmlColumnStates";
    private final String XML_COLUMN_STATES_TAG = "columnStates";
    private final String PLSQL_STRING_CONCAT_OPERATOR = " || ";

    public XmlStateBuilder() {

    }

    public String build(String statePrefix, Column[] tableColumns) {
        List<String> stateConcat = new ArrayList<String>();
        stateConcat.add(XML_DECLARATION);
        stateConcat.add(XML_ROOT_START_TAG);
        stateConcat.add(XML_COLUMN_STATES_START_TAG);
        for (Column tableColumn : tableColumns) {
            StringBuilder columnValueToStringInvocation = new StringBuilder();
            columnValueToStringInvocation
                .append("'<columnState name=\"" + tableColumn.getName() + "\">' || ")
                .append("TO_CHAR(:")
                .append(statePrefix)
                .append(".")
                .append(tableColumn.getName())
                .append(")")
                .append(" || '</columnState>'");
            stateConcat.add(columnValueToStringInvocation.toString());
        }
        stateConcat.add(XML_ROOT_END_TAG);
        stateConcat.add(XML_COLUMN_STATES_END_TAG);
        return String.join(" || ", stateConcat);
    }
    
}
