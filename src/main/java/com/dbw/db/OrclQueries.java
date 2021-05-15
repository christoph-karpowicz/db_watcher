package com.dbw.db;

import com.dbw.db.query.QueryHelper;

public class OrclQueries {

    public static final String SELECT_TABLE_COLUMNS =
        "SELECT COLUMN_NAME, DATA_TYPE FROM ALL_TAB_COLS WHERE TABLE_NAME = ? AND HIDDEN_COLUMN='NO' AND VIRTUAL_COLUMN='NO' ORDER BY COLUMN_ID";

    public static final String FIND_AUDIT_TABLE = "SELECT COUNT(*) AS \"" + Common.EXISTS + "\" FROM all_tables WHERE OWNER = ? AND TABLE_NAME = ?";
    
    public static final String CREATE_AUDIT_TABLE = 
        "CREATE TABLE %s (" +
            Common.COLNAME_ID + "            NUMBER(19, 0) PRIMARY KEY NOT NULL," +
            Common.COLNAME_TABLE_NAME + "    VARCHAR2(100 CHAR), " +
            Common.COLNAME_OLD_STATE + "     CLOB, " +
            Common.COLNAME_NEW_STATE + "     CLOB, " +
            Common.COLNAME_OPERATION + "     CHAR(1 CHAR) NOT NULL, " +
            Common.COLNAME_TIMESTAMP + "     TIMESTAMP(3) DEFAULT LOCALTIMESTAMP(3) NOT NULL" +
        ")";

    public static final String FIND_AUDIT_TRIGGER = "SELECT COUNT(*) AS \"" + Common.EXISTS + "\" from sys.all_triggers WHERE TRIGGER_NAME = ?";

    private static final String UPDATE_COL_LIST = QueryHelper.buildColumnNameList(
        Common.COLNAME_ID, 
        Common.COLNAME_TABLE_NAME, 
        Common.COLNAME_OLD_STATE, 
        Common.COLNAME_NEW_STATE, 
        Common.COLNAME_OPERATION
    );

    private static final String INSERT_COL_LIST = QueryHelper.buildColumnNameList(
        Common.COLNAME_ID, 
        Common.COLNAME_TABLE_NAME, 
        Common.COLNAME_NEW_STATE, 
        Common.COLNAME_OPERATION
    );

    private static final String DELETE_COL_LIST = QueryHelper.buildColumnNameList(
        Common.COLNAME_ID, 
        Common.COLNAME_TABLE_NAME, 
        Common.COLNAME_OLD_STATE, 
        Common.COLNAME_OPERATION
    );
    
    public static final String CREATE_AUDIT_TRIGGER = 
        "CREATE TRIGGER %s \n" +
        "AFTER INSERT OR UPDATE OR DELETE ON %s \n" +
        "FOR EACH ROW \n" +
        "DECLARE \n" +
        "v_operation CHAR(1) := \n" +
        "    case when updating then 'U' \n" +
        "        when deleting then 'D' \n" +
        "        else 'I' end; \n" +
        "v_next_id " + Common.DBW_AUDIT_TABLE_NAME + ".ID%%TYPE;\n" +
        "v_old_state CLOB;\n" +
        "v_new_state CLOB;\n" +
        "BEGIN \n" +
        "    IF updating OR deleting THEN\n" +
        "       v_old_state := %s;\n" +
        "    END IF;\n" +
        "    IF updating OR inserting THEN\n" +
        "       v_new_state := %s;\n" +
        "    END IF;\n" +
        "    SELECT COALESCE(MAX(ID), 0)+1 INTO v_next_id from \n" + Common.DBW_AUDIT_TABLE_NAME + ";\n" +
        "    IF updating THEN\n" +
        "        INSERT INTO \n" + Common.DBW_AUDIT_TABLE_NAME + "(\n" + UPDATE_COL_LIST + ")\n" +
        "            VALUES(v_next_id, '%s', v_old_state, v_new_state, v_operation);\n" +
        "    ELSIF inserting THEN\n" +
        "        INSERT INTO \n" + Common.DBW_AUDIT_TABLE_NAME + "(\n" + INSERT_COL_LIST + ")\n" +
        "            VALUES(v_next_id, '%s', v_new_state, v_operation);\n" +
        "    ELSE \n" +
        "        INSERT INTO \n" + Common.DBW_AUDIT_TABLE_NAME + "(\n" + DELETE_COL_LIST + ")\n" +
        "            VALUES(v_next_id, '%s', v_old_state, v_operation);\n" +
        "    END IF;\n" +
        "    EXCEPTION\n" +
        "        WHEN OTHERS THEN\n" +
        "           DBMS_OUTPUT.PUT_LINE(SQLCODE);\n" +
        "           DBMS_OUTPUT.PUT_LINE(SQLERRM);\n" +
        "END;";

    public static final String DROP_AUDIT_TRIGGER = "DROP TRIGGER %s";

    public static final String SELECT_AUDIT_TRIGGERS = 
        "SELECT TRIGGER_NAME AS \"item\" from sys.all_triggers WHERE TRIGGER_NAME LIKE '" + Common.DBW_PREFIX + "%" + Common.AUDIT_POSTFIX + "'";

    public static final String SELECT_AUDIT_TABLE_MAX_ID = "SELECT COALESCE(MAX(ID), 0) AS \"" + Common.MAX + "\" FROM " + Common.DBW_AUDIT_TABLE_NAME;

    public static final String SELECT_LATEST_WITH_SECONDS =
        "WITH latest_id AS " +
        "(SELECT min(da.ID) AS " + Common.COLNAME_ID + " FROM " + Common.DBW_AUDIT_TABLE_NAME + " da " +
        "WHERE da.\"TIMESTAMP\" > LOCALTIMESTAMP - NUMTODSINTERVAL(?, 'SECOND')) " +
        "SELECT COALESCE(max(da.ID), 0) AS " + Common.COLNAME_ID + " FROM " + Common.DBW_AUDIT_TABLE_NAME + " da " +
        "WHERE id < COALESCE((SELECT id FROM latest_id), (SELECT max(ID) + 1 FROM " + Common.DBW_AUDIT_TABLE_NAME + "))";

    public static final String SELECT_AUDIT_RECORDS = "SELECT * FROM " + Common.DBW_AUDIT_TABLE_NAME + " WHERE id > ?";

    public static final String COUNT_AUDIT_RECORDS = "SELECT COUNT(*) AS ROW_COUNT FROM " + Common.DBW_AUDIT_TABLE_NAME;

    public static final String DELETE_ALL_AUDIT_RECORDS = "DELETE FROM " + Common.DBW_AUDIT_TABLE_NAME;

    public static final String DELETE_FIRST_N_AUDIT_RECORDS = "DELETE FROM " + Common.DBW_AUDIT_TABLE_NAME + " WHERE ROWNUM <= ?";

}
