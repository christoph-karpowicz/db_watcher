package com.dbw.state;

import java.util.ArrayList;
import java.util.List;

import com.dbw.db.Column;

public class XmlStateBuilder {

    public final static String XML_COLUMN_STATES_TAG = "columnStates";
    private final String XML_DECLARATION = "'<?xml version=\"1.0\" encoding=\"UTF-8\"?>'";
    private final String XML_ROOT_TAG = "XmlColumnStates";
    private final String XML_COLUMN_STATE_TAG = "columnState";
    private final String XML_COLUMN_STATE_NAME_ATTRIBUTE = "name";
    private final String PLSQL_TO_CHAR_FUNCTION_START = "TO_CHAR(:";
    private final String PLSQL_TO_CHAR_FUNCTION_END = ")";
    private final String PLSQL_STRING_CONCAT_OPERATOR = " || ";

    XmlStateTag xmlRootTag;
    XmlStateTag columnStatesTag;
    XmlStateTag columnStateTag;

    public XmlStateBuilder() {
        prepareTags();
    }

    private void prepareTags() {
        xmlRootTag = new XmlStateTag(XML_ROOT_TAG);
        columnStatesTag = new XmlStateTag(XML_COLUMN_STATES_TAG);
        columnStateTag = new XmlStateTag(XML_COLUMN_STATE_TAG);
    }

    public String build(String statePrefix, Column[] tableColumns) {
        List<String> stateConcat = new ArrayList<String>();
        stateConcat.add(XML_DECLARATION);
        stateConcat.add(xmlRootTag.start());
        stateConcat.add(columnStatesTag.start());
        for (Column tableColumn : tableColumns) {
            stateConcat.add(buildForColumn(statePrefix, tableColumn));
        }
        stateConcat.add(columnStatesTag.end());
        stateConcat.add(xmlRootTag.end());
        return String.join(PLSQL_STRING_CONCAT_OPERATOR, stateConcat);
    }

    private String buildForColumn(String statePrefix, Column tableColumn) {
        addNameAttributeToColumnStateTag(tableColumn.getName());

        StringBuilder columnValueToStringInvocation = new StringBuilder();
        columnValueToStringInvocation
            .append(columnStateTag.start())
            .append(PLSQL_STRING_CONCAT_OPERATOR)
            .append(PLSQL_TO_CHAR_FUNCTION_START)
            .append(statePrefix)
            .append(tableColumn.getName())
            .append(PLSQL_TO_CHAR_FUNCTION_END)
            .append(PLSQL_STRING_CONCAT_OPERATOR)
            .append(columnStateTag.end());

        return columnValueToStringInvocation.toString();
    }

    private void addNameAttributeToColumnStateTag(String value) {
        XmlStateTagAttribute columnStateNameAttribute = new XmlStateTagAttribute(XML_COLUMN_STATE_NAME_ATTRIBUTE, value);
        columnStateTag.resetAttributes();
        columnStateTag.addAttribute(columnStateNameAttribute);
    }
    
}
