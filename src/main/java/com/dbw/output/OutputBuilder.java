package com.dbw.output;

public interface OutputBuilder {
    String HR = "-";
    String DOT = ".";
    String ELLIPSIS = "...";
    String PADDING = " ";
    String HEADER_UNDERLINE_PADDING = "_";
    String VERTICAL_BORDER = "|";
    String DIFF_HORIZONTAL_BORDER = "-";
    String DIFF_EDGE_BORDER = "+";
    String NEW_LINE = "\n";
    String FRAME_HEADER_ID = "ID: ";
    String FRAME_HEADER_DB = "Database: ";
    String FRAME_HEADER_DB_TYPE = "    type: ";
    String FRAME_HEADER_DB_NAME = "    name: ";
    String FRAME_HEADER_TABLE = "Table: ";
    String FRAME_HEADER_OPERATION = "Operation: ";
    String FRAME_HEADER_TIMESTAMP = "Timestamp: ";
    String VERBOSE_DIFF_DIFF = " diff: ";
    String VERBOSE_DIFF_BEFORE = "Before: ";
    String VERBOSE_DIFF_AFTER = "After: ";
    String SECONDS_SYMBOL = "s";
}
