package com.dbw.db;

public class OrclSpec {
    public final static String NEW_STATE_PREFIX = ":NEW.";
    public final static String OLD_STATE_PREFIX = ":OLD.";
    public final static String TYPE_CLOB = "CLOB";
    public final static String TYPE_BLOB = "BLOB";
    public final static String TYPE_RAW = "RAW";
    public final static String[] SUPPORTED_DATA_TYPES_REGEXP = new String[]{
            TYPE_CLOB, TYPE_BLOB, "NCLOB", "VARCHAR2?", "NVARCHAR2", "NUMBER", "FLOAT", "DATE", /*"URITYPE",*/
            "BINARY_FLOAT", "BINARY_DOUBLE", "TIMESTAMP.*", TYPE_RAW, "NCHAR", "CHAR", /*"BFILE",*/
            "DATE", "INTERVAL.*"
    };
}
