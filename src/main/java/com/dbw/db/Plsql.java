package com.dbw.db;

public class Plsql {
    public final static String PLSQL_CASE_START = "(CASE WHEN ";
    public final static String PLSQL_CASE_THEN = " THEN ";
    public final static String PLSQL_CASE_ELSE = " ELSE ";
    public final static String PLSQL_CASE_END = " END)";
    public final static String PLSQL_NULL = " 'null' ";
    public final static String PLSQL_IS_NOT_NULL = " IS NOT NULL ";
    public final static String PLSQL_TO_CHAR_FUNCTION_START = "TO_CHAR(";
    public final static String RIGHT_PARENTHESIS = ")";
    public final static String PLSQL_CAST_TO_VARCHAR_FUNCTION_START = "utl_raw.cast_to_varchar2(UTL_ENCODE.BASE64_ENCODE(";
    public final static String PLSQL_CAST_TO_VARCHAR_FUNCTION_END = "))";
    public final static String PLSQL_LOB_TO_VARCHAR_FUNCTION_START = "DBMS_LOB.substr(";
    public final static String PLSQL_LOB_TO_VARCHAR_FUNCTION_END = ",4000)";
    public final static String PLSQL_RAW_CAST_TO_VARCHAR_FUNCTION_START =
            "UTL_RAW.CAST_TO_VARCHAR2(UTL_ENCODE.BASE64_ENCODE(UTL_RAW.CAST_TO_RAW(UTL_RAW.CAST_TO_VARCHAR2(DBMS_LOB.SUBSTR(";
    public final static String PLSQL_RAW_CAST_TO_VARCHAR_FUNCTION_END = ")))))";
    public final static String PLSQL_STRING_CONCAT_OPERATOR = "||";
}
