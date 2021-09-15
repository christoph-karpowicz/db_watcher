package com.dbw.db.query;

import com.dbw.db.Common;
import com.dbw.util.StringUtils;

public class QueryHelper {

    public static String buildColumnNameList(String... columnNames) {
        return String.join(Common.COMMA_DELIMITER, columnNames);
    }

    public static String buildAuditTriggerName(String tableName) {
        String tableNameHash = StringUtils.createShortHash(tableName);
        return Common.DBW_PREFIX + tableNameHash;
    }
    
}
