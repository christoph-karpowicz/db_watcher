package com.dbw.output;

import java.util.Collections;

public interface OutputBuilder {
    String HR = "-";
    String ELLIPSIS = "...";
    String PADDING = " ";
    String HEADER_UNDERLINE_PADDING = "_";
    String DIFF_VERTICAL_BORDER = "|";
    String DIFF_HORIZONTAL_BORDER = "-";
    String DIFF_EDGE_BORDER = "+";
    String NEW_LINE = "\n";
    String FRAME_HEADER_ID = "ID: ";
    String FRAME_HEADER_TABLE = "Table: ";
    String FRAME_HEADER_OPERATION = "Operation: ";
    String FRAME_HEADER_TIMESTAMP = "Timestamp: ";
    String VERBOSE_DIFF_DIFF = " diff: ";
    String VERBOSE_DIFF_BEFORE = "Before: ";
    String VERBOSE_DIFF_AFTER = "After: ";
}
