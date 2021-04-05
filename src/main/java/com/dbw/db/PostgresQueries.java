package com.dbw.db;

public class PostgresQueries {

    public static final String FIND_AUDIT_TABLE = 
        "SELECT " + Common.EXISTS + " (" +
        "    SELECT FROM information_schema.tables " +
        "    WHERE  table_schema = ?" +
        "    AND    table_name   = ?" +
        ");";

    public static final String FIND_TABLE_COLUMNS = 
        "SELECT column_name as \"item\" FROM information_schema.columns" +
        "    WHERE  table_schema = ?" +
        "    AND    table_name   = ?" +
        "    ORDER BY ordinal_position;";
    
    public static final String CREATE_AUDIT_TABLE = 
        "CREATE TABLE %s (" +
            Postgres.COLUMN_NAMES[0] + "     SERIAL PRIMARY KEY NOT NULL," +
            Postgres.COLUMN_NAMES[1] + "     VARCHAR(100), " +
            Postgres.COLUMN_NAMES[2] + "     TEXT, " +
            Postgres.COLUMN_NAMES[3] + "     TEXT, " +
            Postgres.COLUMN_NAMES[4] + "     CHAR(1) NOT NULL, " +
            Postgres.COLUMN_NAMES[5] + "     TEXT, " +
            Postgres.COLUMN_NAMES[6] + "     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL" +
        ")";

    public static final String FIND_AUDIT_FUNCTION = 
        "SELECT " + Common.EXISTS + " (" +
        "    SELECT FROM pg_proc " +
        "    WHERE  proname = '" + Common.DBW_AUDIT_FUNC_NAME.toLowerCase() + "'" +
        ");";

    private static final String UPDATE_COL_LIST = QueryBuilder.buildColumNameList(
        Common.COLNAME_OLD_STATE, 
        Common.COLNAME_NEW_STATE, 
        Common.COLNAME_TABLE_NAME, 
        Common.COLNAME_OPERATION, 
        Common.COLNAME_QUERY
    );

    private static final String DELETE_COL_LIST = QueryBuilder.buildColumNameList(
        Common.COLNAME_OLD_STATE, 
        Common.COLNAME_TABLE_NAME, 
        Common.COLNAME_OPERATION, 
        Common.COLNAME_QUERY
    );

    private static final String INSERT_COL_LIST = QueryBuilder.buildColumNameList(
        Common.COLNAME_NEW_STATE, 
        Common.COLNAME_TABLE_NAME, 
        Common.COLNAME_OPERATION, 
        Common.COLNAME_QUERY
    );

    public static final String CREATE_AUDIT_FUNCTION = 
        "CREATE FUNCTION " + Common.DBW_AUDIT_FUNC_NAME.toLowerCase() + "() RETURNS trigger AS " +
        "$$" +
        "DECLARE" +
        "    v_old TEXT;" +
        "    v_new TEXT;" +
        "BEGIN" +
        "    IF (TG_OP = 'UPDATE') THEN" +
        "        v_old := ROW_TO_JSON(ROW(OLD.*));" +
        "        v_new := ROW_TO_JSON(ROW(NEW.*));" +
        "        INSERT INTO " + Common.DBW_AUDIT_TABLE_NAME + "(" + UPDATE_COL_LIST + ") " +
        "            VALUES (v_old, v_new, TG_TABLE_NAME::TEXT , 'U', current_query());" +
        "        RETURN NEW;" +
        "    ELSIF (TG_OP = 'DELETE') THEN" +
        "        v_old := ROW_TO_JSON(ROW(OLD.*));" +
        "        INSERT INTO " + Common.DBW_AUDIT_TABLE_NAME + "(" + DELETE_COL_LIST + ")" +
        "            VALUES (v_old, TG_TABLE_NAME::TEXT, 'D', current_query());" +
        "        RETURN OLD;" +
        "    ELSIF (TG_OP = 'INSERT') THEN" +
        "        v_new := ROW_TO_JSON(ROW(NEW.*));" +
        "        INSERT INTO " + Common.DBW_AUDIT_TABLE_NAME + "(" + INSERT_COL_LIST + ")" +
        "            VALUES (v_new, TG_TABLE_NAME::TEXT, 'I', current_query());" +
        "        RETURN NEW;" +
        "    END IF;" +
        "    RETURN NEW;" +
        "END;" +
        "$$" +
        "LANGUAGE plpgsql;";

    public static final String DROP_AUDIT_FUNCTION = "DROP FUNCTION " + Common.DBW_AUDIT_FUNC_NAME.toLowerCase() + ";";

    public static final String FIND_AUDIT_TRIGGER = 
        "SELECT " + Common.EXISTS + " (" +
        "    SELECT FROM pg_trigger " +
        "    WHERE  NOT tgisinternal" +
        "    AND    tgname = ?" +
        ");";

    public static final String CREATE_AUDIT_TRIGGER = 
        "CREATE TRIGGER %s" +
        " AFTER INSERT OR UPDATE OR DELETE ON %s" +
        " FOR EACH ROW EXECUTE PROCEDURE " + Common.DBW_AUDIT_FUNC_NAME.toLowerCase() + "();";

    public static final String DROP_AUDIT_TRIGGER = "DROP TRIGGER %s ON %s;";

    public static final String SELECT_AUDIT_TRIGGERS = "SELECT tgname AS item FROM pg_trigger WHERE NOT tgisinternal AND tgname LIKE '" + Common.DBW_PREFIX + "%" + Common.AUDIT_POSTFIX + "'";

    public static final String SELECT_AUDIT_TABLE_MAX_ID = "SELECT MAX(id) FROM " + Common.DBW_AUDIT_TABLE_NAME + ";";

    public static final String SELECT_LATEST_WITH_SECONDS =
        "WITH latest_id AS " +
        "(SELECT min(id) as " + Common.COLNAME_ID + " FROM " + Common.DBW_AUDIT_TABLE_NAME + " da " +
        "WHERE da.\"timestamp\" > now() - make_interval(0,0,0,0,0,0,?)) " +
        "SELECT COALESCE(max(id), 0) AS " + Common.COLNAME_ID + " FROM " + Common.DBW_AUDIT_TABLE_NAME + " da " +
        "WHERE id < COALESCE((SELECT id FROM latest_id), (SELECT max(id) + 1 FROM " + Common.DBW_AUDIT_TABLE_NAME + "))";

    public static final String SELECT_AUDIT_RECORDS = "SELECT * FROM " + Common.DBW_AUDIT_TABLE_NAME + " WHERE id > ?;";

    public static final String COUNT_AUDIT_RECORDS = "SELECT COUNT(*) AS \"ROW_COUNT\" FROM " + Common.DBW_AUDIT_TABLE_NAME;

    public static final String DELETE_ALL_AUDIT_RECORDS = "DELETE FROM " + Common.DBW_AUDIT_TABLE_NAME;

    public static final String DELETE_FIRST_N_AUDIT_RECORDS =
        "DELETE FROM " + Common.DBW_AUDIT_TABLE_NAME +
        " WHERE id = " +
        "any(array(SELECT id FROM " + Common.DBW_AUDIT_TABLE_NAME + " ORDER BY timestamp LIMIT ?))";

}
