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
    private final String PLSQL_TO_CHAR_FUNCTION_START = "TO_CHAR(";
    private final String RIGHT_PARENTHESIS = ")";
    private final String PLSQL_LOB_TO_VARCHAR_FUNCTION_START = "DBMS_LOB.substr(";
    private final String PLSQL_LOB_TO_VARCHAR_FUNCTION_END = ", 4000)";
    private final String PLSQL_RAW_CAST_TO_VARCHAR_FUNCTION_START = "UTL_RAW.CAST_TO_VARCHAR2(UTL_ENCODE.BASE64_ENCODE(UTL_RAW.CAST_TO_RAW(UTL_RAW.CAST_TO_VARCHAR2(DBMS_LOB.SUBSTR(";
    private final String PLSQL_RAW_CAST_TO_VARCHAR_FUNCTION_END = ")))))";
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

        String columnNameWithStatePrefix = statePrefix + tableColumn.getName();
        StringBuilder columnValueToStringInvocation = new StringBuilder();
        columnValueToStringInvocation
            .append(columnStateTag.start())
            .append(PLSQL_STRING_CONCAT_OPERATOR)
            .append(generateToVarcharFunctionCall(columnNameWithStatePrefix, tableColumn.getDataType()))
            .append(PLSQL_STRING_CONCAT_OPERATOR)
            .append(columnStateTag.end());

        return columnValueToStringInvocation.toString();
    }

    private void addNameAttributeToColumnStateTag(String value) {
        XmlStateTagAttribute columnStateNameAttribute = new XmlStateTagAttribute(XML_COLUMN_STATE_NAME_ATTRIBUTE, value);
        columnStateTag.resetAttributes();
        columnStateTag.addAttribute(columnStateNameAttribute);
    }

    private String generateToVarcharFunctionCall(String columnNameWithStatePrefix, String dataType) {
        StringBuilder toVarcharFunctionCall = new StringBuilder();

        switch (dataType) {
            case "CLOB":
                toVarcharFunctionCall.append(PLSQL_LOB_TO_VARCHAR_FUNCTION_START)
                    .append(columnNameWithStatePrefix)
                    .append(PLSQL_LOB_TO_VARCHAR_FUNCTION_END);
                break;
            case "BLOB":
                toVarcharFunctionCall.append(PLSQL_RAW_CAST_TO_VARCHAR_FUNCTION_START)
                    .append(columnNameWithStatePrefix)
                    .append(PLSQL_RAW_CAST_TO_VARCHAR_FUNCTION_END);
                break;
            default:
                toVarcharFunctionCall.append(PLSQL_TO_CHAR_FUNCTION_START)
                    .append(columnNameWithStatePrefix)
                    .append(RIGHT_PARENTHESIS);
        }
        return toVarcharFunctionCall.toString();
    }
    
}
