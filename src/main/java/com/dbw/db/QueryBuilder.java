package com.dbw.db;

public class QueryBuilder {

    public static String buildColumNameList(String... columnNames) {
        return String.join(Common.COMMA_DELIMITER, columnNames);
    }

    public static String buildAuditTriggerName(String tableName) {
        return String.format(Common.DBW_PREFIX + "%s" + Common.AUDIT_POSTFIX, tableName);
    }
    
}
