package com.dbw.output;

import java.util.Collections;

public interface OutputBuilder {
    public final String SM_HR = "-------------";
    public final String LG_HR = String.join("", Collections.nCopies(7, SM_HR));
    public final String ELLIPSIS = "...";
    public final String PADDING = " ";
    public final String HEADER_UNDERLINE_PADDING = "_";
    public final String DIFF_VERTICAL_BORDER = "|";
    public final String DIFF_HORIZONTAL_BORDER = "-";
    public final String NEW_LINE = "\n";
    public final String FRAME_HEADER_ID = "ID: ";
    public final String FRAME_HEADER_TABLE = "Table: ";
    public final String FRAME_HEADER_OPERATION = "Operation: ";
    public final String FRAME_HEADER_TIMESTAMP = "Timestamp: ";
    public final String VERBOSE_DIFF_DIFF = " diff: ";
    public final String VERBOSE_DIFF_BEFORE = "Before: ";
    public final String VERBOSE_DIFF_AFTER = "After: ";
}
