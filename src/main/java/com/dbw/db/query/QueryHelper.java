package com.dbw.db.query;

import com.dbw.db.Common;

public class QueryHelper {

    public static String buildColumnNameList(String... columnNames) {
        return String.join(Common.COMMA_DELIMITER, columnNames);
    }

    public static String buildAuditTriggerName(String tableName) {
        return String.format(Common.DBW_PREFIX + "%s" + Common.AUDIT_POSTFIX, tableName);
    }
    
}
