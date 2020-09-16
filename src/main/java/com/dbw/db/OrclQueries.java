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
        "CREATE TRIGGER dbw_%s_audit" +
        " AFTER INSERT OR UPDATE OR DELETE ON %s" +
        " FOR EACH ROW EXECUTE PROCEDURE dbw_audit_func()";

    public static final String DROP_AUDIT_TRIGGER = "DROP TRIGGER DBW_%S_AUDIT";

    public static final String SELECT_AUDIT_TRIGGERS = "SELECT TRIGGER_NAME AS \"item\" from sys.all_triggers WHERE TRIGGER_NAME LIKE 'DBW_%_AUDIT'";

    public static final String SELECT_AUDIT_TABLE_MAX_ID = "SELECT MAX(ID) FROM DBW_AUDIT";

    public static final String SELECT_AUDIT_RECORDS = "SELECT * FROM DBW_AUDIT WHERE id > ?";

}