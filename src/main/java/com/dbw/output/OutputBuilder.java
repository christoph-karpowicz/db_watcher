package com.dbw.output;

public interface OutputBuilder {
    String HR = "-";
    String DOT = ".";
    String ELLIPSIS = "...";
    String PADDING = " ";
    String HEADER_UNDERLINE_PADDING = "_";
    String VERTICAL_BORDER = "|";
    String HORIZONTAL_BORDER = "-";
    String EDGE_BORDER = "+";
    String NEW_LINE = "\n";
    String FRAME_HEADER_NO = "No.";
    String FRAME_HEADER_DB_TYPE = "DB type";
    String FRAME_HEADER_DB_NAME = "DB name";
    String FRAME_HEADER_TABLE = "Table";
    String FRAME_HEADER_OPERATION = "Operation";
    String FRAME_HEADER_TIMESTAMP = "Timestamp";
    String FRAME_HEADER_QUERY = "Query: ";
    String VERBOSE_DIFF_DIFF = " diff: ";
    String VERBOSE_DIFF_BEFORE = "Before: ";
    String VERBOSE_DIFF_AFTER = "After: ";
    String OLD_STATE_PREFIX = "-";
    String NEW_STATE_PREFIX = "+";
}
