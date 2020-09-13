package com.dbw.db;

public class OrclQueries {

    public static final String SELECT_TABLE_COLUMNS = "SELECT * FROM ALL_TAB_COLS WHERE TABLE_NAME = ? ORDER BY COLUMN_NAME;";

    public static final String SELECT_TABLE_NAMES = "SELECT * FROM all_tables WHERE OWNER = ?;";

    public static final String FIND_AUDIT_TABLE = "SELECT COUNT(*) AS \"exists\" FROM all_tables WHERE OWNER = ? AND TABLE_NAME = ?;";
    
    public static final String CREATE_AUDIT_TABLE = 
        "CREATE TABLE %s (" +
            "id NUMBER(10) PRIMARY KEY NOT NULL," +
            "table_name    VARCHAR2(100), " +
            "old_state     VARCHAR2(2000), " +
            "new_state     VARCHAR2(2000), " +
            "operation     VARCHAR2(6) NOT NULL, " +
            "timestamp     DATE DEFAULT sysdate NOT NULL" +
        ");";

    public static final String FIND_AUDIT_FUNCTION = 
        "SELECT EXISTS (" +
        "    SELECT FROM pg_proc " +
        "    WHERE  proname = 'dbw_audit_func'" +
        ");";

    public static final String FIND_AUDIT_TRIGGER = 
        "SELECT EXISTS (" +
        "    SELECT FROM pg_trigger " +
        "    WHERE  NOT tgisinternal" +
        "    AND    tgname = ?" +
        ");";

    public static final String CREATE_AUDIT_TRIGGER = 
        "CREATE TRIGGER dbw_%s_audit" +
        " AFTER INSERT OR UPDATE OR DELETE ON %s" +
        " FOR EACH ROW EXECUTE PROCEDURE dbw_audit_func();";

    public static final String DROP_AUDIT_TRIGGER = "DROP TRIGGER IF EXISTS dbw_%s_audit ON %s;";

    public static final String SELECT_AUDIT_TRIGGERS = "SELECT tgname AS item FROM pg_trigger WHERE NOT tgisinternal AND tgname LIKE 'dbw_%_audit'";

    public static final String SELECT_AUDIT_TABLE_MAX_ID = "SELECT MAX(id) FROM dbw_audit;";

    public static final String SELECT_AUDIT_RECORDS = "SELECT * FROM dbw_audit WHERE id > ?;";

}