package com.dbw.state;

import com.dbw.db.Column;
import com.dbw.db.OrclSpec;
import com.dbw.log.Level;
import com.dbw.log.Logger;
import com.dbw.log.WarningMessages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XmlStateBuilder {

    public final static String XML_COLUMN_STATES_TAG = "dbw-css";
    public final static String XML_COLUMN_STATE_TAG = "dbw-cs";
    public final static String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    public final static String XML_COLUMN_STATE_NAME_ATTRIBUTE = "name";
    private final String XML_ROOT_TAG = "dbw-root";
    private final String PLSQL_TO_CHAR_FUNCTION_START = "TO_CHAR(";
    private final String RIGHT_PARENTHESIS = ")";
    private final String PLSQL_CAST_TO_VARCHAR_FUNCTION_START = "utl_raw.cast_to_varchar2(UTL_ENCODE.BASE64_ENCODE(";
    private final String PLSQL_CAST_TO_VARCHAR_FUNCTION_END = "))";
    private final String PLSQL_LOB_TO_VARCHAR_FUNCTION_START = "DBMS_LOB.substr(";
    private final String PLSQL_LOB_TO_VARCHAR_FUNCTION_END = ",4000)";
    private final String PLSQL_RAW_CAST_TO_VARCHAR_FUNCTION_START =
            "UTL_RAW.CAST_TO_VARCHAR2(UTL_ENCODE.BASE64_ENCODE(UTL_RAW.CAST_TO_RAW(UTL_RAW.CAST_TO_VARCHAR2(DBMS_LOB.SUBSTR(";
    private final String PLSQL_RAW_CAST_TO_VARCHAR_FUNCTION_END = ")))))";
    private final String PLSQL_STRING_CONCAT_OPERATOR = "||";

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
        List<String> stateConcat = new ArrayList<>();
        stateConcat.add(xmlRootTag.startTag());
        stateConcat.add(columnStatesTag.startTag());
        for (Column tableColumn : tableColumns) {
            if (isDataTypeSupported(tableColumn)) {
                String wrnMsg = String.format(WarningMessages.TRIGGER_UNSUPPORTED_DATA_TYPE, tableColumn.getDataType(), tableColumn.getName());
                Logger.log(Level.WARNING, wrnMsg);
                continue;
            }
            stateConcat.add(buildForColumn(statePrefix, tableColumn));
        }
        stateConcat.add(columnStatesTag.endTag());
        stateConcat.add(xmlRootTag.endTag());
        return String.join(PLSQL_STRING_CONCAT_OPERATOR, stateConcat);
    }

    private boolean isDataTypeSupported(Column tableColumn) {
        return Arrays.stream(OrclSpec.SUPPORTED_DATA_TYPES_REGEXP)
                .noneMatch(dataType -> tableColumn.getDataType().matches("^" + dataType + "$"));
    }

    private String buildForColumn(String statePrefix, Column tableColumn) {
        addNameAttributeToColumnStateTag(tableColumn.getName());

        String columnNameWithStatePrefix = statePrefix + tableColumn.getName();
        StringBuilder columnValueToStringInvocation = new StringBuilder();
        columnValueToStringInvocation
            .append(columnStateTag.startTag())
            .append(PLSQL_STRING_CONCAT_OPERATOR)
            .append(generateToVarcharFunctionCall(columnNameWithStatePrefix, tableColumn.getDataType()))
            .append(PLSQL_STRING_CONCAT_OPERATOR)
            .append(columnStateTag.endTag());

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
            case OrclSpec.TYPE_BLOB:
                toVarcharFunctionCall
                        .append(PLSQL_RAW_CAST_TO_VARCHAR_FUNCTION_START)
                        .append(columnNameWithStatePrefix)
                        .append(PLSQL_RAW_CAST_TO_VARCHAR_FUNCTION_END);
            break;
            case OrclSpec.TYPE_CLOB:
                toVarcharFunctionCall
                        .append(PLSQL_LOB_TO_VARCHAR_FUNCTION_START)
                        .append(columnNameWithStatePrefix)
                        .append(PLSQL_LOB_TO_VARCHAR_FUNCTION_END);
                break;
            case OrclSpec.TYPE_RAW:
                toVarcharFunctionCall
                        .append(PLSQL_CAST_TO_VARCHAR_FUNCTION_START)
                        .append(columnNameWithStatePrefix)
                        .append(PLSQL_CAST_TO_VARCHAR_FUNCTION_END);
                break;
            default:
                toVarcharFunctionCall
                        .append(PLSQL_TO_CHAR_FUNCTION_START)
                        .append(columnNameWithStatePrefix)
                        .append(RIGHT_PARENTHESIS);
        }
        return toVarcharFunctionCall.toString();
    }
    
}
