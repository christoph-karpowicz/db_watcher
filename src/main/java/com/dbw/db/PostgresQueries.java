package com.dbw.db;

public class PostgresQueries {

    public static final String FIND_AUDIT_TABLE = 
        "SELECT EXISTS (" +
        "    SELECT FROM information_schema.tables " +
        "    WHERE  table_schema = ?" +
        "    AND    table_name   = ?" +
        ");";
    
    public static final String CREATE_AUDIT_TABLE = 
        "CREATE TABLE %s (" +
            "id            SERIAL PRIMARY KEY NOT NULL," +
            "table_name    VARCHAR(100), " +
            "old_state     TEXT, " +
            "new_state     TEXT, " +
            "operation     VARCHAR(6) NOT NULL, " +
            "query         TEXT, " +
            "timestamp     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL" +
        ")";

    public static final String FIND_AUDIT_FUNCTION = 
        "SELECT EXISTS (" +
        "    SELECT FROM pg_proc " +
        "    WHERE  proname = 'dbw_audit_func'" +
        ");";

    public static final String CREATE_AUDIT_FUNCTION = 
        "CREATE FUNCTION dbw_audit_func() RETURNS trigger AS " +
        "$$" +
        "DECLARE" +
        "    v_old TEXT;" +
        "    v_new TEXT;" +
        "BEGIN" +
        "    IF (TG_OP = 'UPDATE') THEN" +
        "        v_old := ROW_TO_JSON(ROW(OLD.*));" +
        "        v_new := ROW_TO_JSON(ROW(NEW.*));" +
        "        INSERT INTO dbw_audit(old_state, new_state, table_name, operation, query) " +
        "            VALUES (v_old, v_new, TG_TABLE_NAME::TEXT , TG_OP, current_query());" +
        "        RETURN NEW;" +
        "    ELSIF (TG_OP = 'DELETE') THEN" +
        "        v_old := ROW_TO_JSON(ROW(OLD.*));" +
        "        INSERT INTO dbw_audit(old_state, table_name, operation, query)" +
        "            VALUES (v_old, TG_TABLE_NAME::TEXT, TG_OP, current_query());" +
        "        RETURN OLD;" +
        "    ELSIF (TG_OP = 'INSERT') THEN" +
        "        v_new := ROW_TO_JSON(ROW(NEW.*));" +
        "        INSERT INTO dbw_audit(new_state, table_name, operation, query)" +
        "            VALUES (v_new, TG_TABLE_NAME::TEXT, TG_OP, current_query());" +
        "        RETURN NEW;" +
        "    END IF;" +
        "    RETURN NEW;" +
        "END;" +
        "$$" +
        "LANGUAGE plpgsql;";

    public static final String DROP_AUDIT_FUNCTION = "DROP FUNCTION IF EXISTS dbw_audit_func;";

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