package com.dbw.db;

public class OrclQueries {

    public static final String SELECT_TABLE_COLUMNS = "SELECT COLUMN_NAME, DATA_TYPE FROM ALL_TAB_COLS WHERE TABLE_NAME = ? ORDER BY COLUMN_NAME";

    public static final String SELECT_TABLE_NAMES = "SELECT * FROM all_tables WHERE OWNER = ?";

    public static final String FIND_AUDIT_TABLE = "SELECT COUNT(*) AS \"exists\" FROM all_tables WHERE OWNER = ? AND TABLE_NAME = ?";
    
    public static final String CREATE_AUDIT_TABLE = 
        "CREATE TABLE %s (" +
            "id NUMBER(10) PRIMARY KEY NOT NULL," +
            "table_name    VARCHAR2(100), " +
            "old_state     VARCHAR2(" + Orcl.STATE_COLUMN_MAX_LENGTH + "), " +
            "new_state     VARCHAR2(" + Orcl.STATE_COLUMN_MAX_LENGTH + "), " +
            "operation     CHAR(1) NOT NULL, " +
            "timestamp     DATE DEFAULT sysdate NOT NULL" +
        ")";

    public static final String FIND_AUDIT_TRIGGER = "SELECT COUNT(*) AS \"exists\" from sys.all_triggers WHERE TRIGGER_NAME = ?";

    public static final String CREATE_AUDIT_TRIGGER = 
        "CREATE TRIGGER DBW_%s_AUDIT " +
        "AFTER INSERT OR UPDATE OR DELETE ON %s " +
        "FOR EACH ROW " +
        "DECLARE " +
        "v_operation VARCHAR2(6) := " +
        "    case when updating then 'U' " +
        "        when deleting then 'D' " +
        "        else 'I' end; " +
        "next_id DBW_AUDIT.ID%%TYPE;" +
        "v_old_state VARCHAR2(2000);" +
        "v_new_state VARCHAR2(2000);" +
        "BEGIN " +
        "    SELECT COALESCE(MAX(ID), 0)+1 INTO next_id from DBW_AUDIT;" +
        "    IF updating THEN" +
        "        v_old_state := %s;" +
        "        v_new_state := %s;" +
        "        INSERT INTO DBW_AUDIT(id, table_name, old_state, new_state, operation)" +
        "            VALUES(next_id, '%s', v_old_state, v_new_state, v_operation);" +
        "    ELSIF inserting THEN" +
        "       v_new_state := %s;" +
        "       INSERT INTO DBW_AUDIT(id, table_name, new_state, operation)" +
        "            VALUES(next_id, '%s', v_new_state, v_operation);" +
        "    ELSE " +
        "        v_old_state := %s;" +
        "        INSERT INTO DBW_AUDIT(id, table_name, old_state, operation)" +
        "            VALUES(next_id, '%s', v_old_state, v_operation);" +
        "    END IF;" +
        "END;";

    public static final String DROP_AUDIT_TRIGGER = "DROP TRIGGER DBW_%S_AUDIT";

    public static final String SELECT_AUDIT_TRIGGERS = "SELECT TRIGGER_NAME AS \"item\" from sys.all_triggers WHERE TRIGGER_NAME LIKE 'DBW_%_AUDIT'";

    public static final String SELECT_AUDIT_TABLE_MAX_ID = "SELECT MAX(ID) AS \"max\" FROM DBW_AUDIT";

    public static final String SELECT_AUDIT_RECORDS = "SELECT * FROM DBW_AUDIT WHERE id > ?";

}