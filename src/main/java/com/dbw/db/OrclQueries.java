package com.dbw.db;

public class OrclQueries {

    public static final String SELECT_TABLE_COLUMNS = "SELECT COLUMN_NAME, DATA_TYPE FROM ALL_TAB_COLS WHERE TABLE_NAME = ? AND HIDDEN_COLUMN='NO' AND VIRTUAL_COLUMN='NO' ORDER BY COLUMN_ID";

    public static final String SELECT_TABLE_NAMES = "SELECT * FROM all_tables WHERE OWNER = ?";

    public static final String FIND_AUDIT_TABLE = "SELECT COUNT(*) AS \"" + Common.EXISTS + "\" FROM all_tables WHERE OWNER = ? AND TABLE_NAME = ?";
    
    public static final String CREATE_AUDIT_TABLE = 
        "CREATE TABLE %s (" +
            Common.COLNAME_ID + "            NUMBER(19, 0) PRIMARY KEY NOT NULL," +
            Common.COLNAME_TABLE_NAME + "    VARCHAR2(100 CHAR), " +
            Common.COLNAME_OLD_STATE + "     CLOB, " +
            Common.COLNAME_NEW_STATE + "     CLOB, " +
            Common.COLNAME_OPERATION + "     CHAR(1 CHAR) NOT NULL, " +
            Common.COLNAME_TIMESTAMP + "     TIMESTAMP(6) DEFAULT sysdate NOT NULL" +
        ")";

    public static final String FIND_AUDIT_TRIGGER = "SELECT COUNT(*) AS \"" + Common.EXISTS + "\" from sys.all_triggers WHERE TRIGGER_NAME = ?";

    private static final String UPDATE_COL_LIST = QueryBuilder.buildColumNameList(
        Common.COLNAME_ID, 
        Common.COLNAME_TABLE_NAME, 
        Common.COLNAME_OLD_STATE, 
        Common.COLNAME_NEW_STATE, 
        Common.COLNAME_OPERATION
    );

    private static final String INSERT_COL_LIST = QueryBuilder.buildColumNameList(
        Common.COLNAME_ID, 
        Common.COLNAME_TABLE_NAME, 
        Common.COLNAME_NEW_STATE, 
        Common.COLNAME_OPERATION
    );

    private static final String DELETE_COL_LIST = QueryBuilder.buildColumNameList(
        Common.COLNAME_ID, 
        Common.COLNAME_TABLE_NAME, 
        Common.COLNAME_OLD_STATE, 
        Common.COLNAME_OPERATION
    );
    
    public static final String CREATE_AUDIT_TRIGGER = 
        "CREATE TRIGGER %s " +
        "AFTER INSERT OR UPDATE OR DELETE ON %s " +
        "FOR EACH ROW " +
        "DECLARE " +
        "v_operation VARCHAR2(6) := " +
        "    case when updating then 'U' " +
        "        when deleting then 'D' " +
        "        else 'I' end; " +
        "next_id " + Common.DBW_AUDIT_TABLE_NAME + ".ID%%TYPE;" +
        "v_old_state CLOB;" +
        "v_new_state CLOB;" +
        "BEGIN " +
        "    SELECT COALESCE(MAX(ID), 0)+1 INTO next_id from " + Common.DBW_AUDIT_TABLE_NAME + ";" +
        "    IF updating THEN" +
        "        v_old_state := %s;" +
        "        v_new_state := %s;" +
        "        INSERT INTO " + Common.DBW_AUDIT_TABLE_NAME + "(" + UPDATE_COL_LIST + ")" +
        "            VALUES(next_id, '%s', v_old_state, v_new_state, v_operation);" +
        "    ELSIF inserting THEN" +
        "        v_new_state := %s;" +
        "        INSERT INTO " + Common.DBW_AUDIT_TABLE_NAME + "(" + INSERT_COL_LIST + ")" +
        "            VALUES(next_id, '%s', v_new_state, v_operation);" +
        "    ELSE " +
        "        v_old_state := %s;" +
        "        INSERT INTO " + Common.DBW_AUDIT_TABLE_NAME + "(" + DELETE_COL_LIST + ")" +
        "            VALUES(next_id, '%s', v_old_state, v_operation);" +
        "    END IF;" +
        "END;";

    public static final String DROP_AUDIT_TRIGGER = "DROP TRIGGER %s";

    public static final String SELECT_AUDIT_TRIGGERS = 
        "SELECT TRIGGER_NAME AS \"item\" from sys.all_triggers WHERE TRIGGER_NAME LIKE '" + Common.DBW_PREFIX + "%" + Common.AUDIT_POSTFIX + "'";

    public static final String SELECT_AUDIT_TABLE_MAX_ID = "SELECT MAX(ID) AS \"" + Common.MAX + "\" FROM " + Common.DBW_AUDIT_TABLE_NAME;

    public static final String SELECT_AUDIT_RECORDS = "SELECT * FROM " + Common.DBW_AUDIT_TABLE_NAME + " WHERE id > ?";

}