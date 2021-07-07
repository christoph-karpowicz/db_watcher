package com.dbw.state;

import com.dbw.db.Column;
import com.dbw.db.OrclSpec;
import com.dbw.db.Plsql;
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
        return String.join(Plsql.PLSQL_STRING_CONCAT_OPERATOR, stateConcat);
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
            .append(Plsql.PLSQL_STRING_CONCAT_OPERATOR)
            .append(generateToVarcharFunctionCall(columnNameWithStatePrefix, tableColumn.getDataType()))
            .append(Plsql.PLSQL_STRING_CONCAT_OPERATOR)
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
        boolean isDefault = false;
        switch (dataType) {
            case OrclSpec.TYPE_BLOB:
                toVarcharFunctionCall
                        .append(Plsql.PLSQL_RAW_CAST_TO_VARCHAR_FUNCTION_START)
                        .append(columnNameWithStatePrefix)
                        .append(Plsql.PLSQL_RAW_CAST_TO_VARCHAR_FUNCTION_END);
                break;
            case OrclSpec.TYPE_CLOB:
                toVarcharFunctionCall
                        .append(Plsql.PLSQL_LOB_TO_VARCHAR_FUNCTION_START)
                        .append(columnNameWithStatePrefix)
                        .append(Plsql.PLSQL_LOB_TO_VARCHAR_FUNCTION_END);
                break;
            case OrclSpec.TYPE_RAW:
                toVarcharFunctionCall
                        .append(Plsql.PLSQL_CAST_TO_VARCHAR_FUNCTION_START)
                        .append(columnNameWithStatePrefix)
                        .append(Plsql.PLSQL_CAST_TO_VARCHAR_FUNCTION_END);
                break;
            default:
                toVarcharFunctionCall
                        .append(Plsql.PLSQL_TO_CHAR_FUNCTION_START)
                        .append(columnNameWithStatePrefix)
                        .append(Plsql.RIGHT_PARENTHESIS);
                isDefault = true;
        }

        if (!isDefault) {
            toVarcharFunctionCall
                    .insert(0, Plsql.PLSQL_CASE_THEN)
                    .insert(0, Plsql.PLSQL_IS_NOT_NULL)
                    .insert(0, columnNameWithStatePrefix)
                    .insert(0, Plsql.PLSQL_CASE_START);
            toVarcharFunctionCall
                    .append(Plsql.PLSQL_CASE_ELSE)
                    .append(Plsql.PLSQL_NULL)
                    .append(Plsql.PLSQL_CASE_END);
        }
        return toVarcharFunctionCall.toString();
    }
    
}
