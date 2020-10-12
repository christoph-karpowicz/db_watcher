package com.dbw.diff;

import java.util.List;

import com.dbw.db.Operation;
import com.dbw.output.OutputBuilder;
import com.google.inject.Singleton;

@Singleton
public class TableDiffBuilder implements OutputBuilder {
    private final short MAX_COL_LENGTH = 17;

    private StringBuilder builder;

    public void init() {
        builder = new StringBuilder();
    }

    public void build(List<StateColumn> stateColumns, Operation dbOperation) {
        addHorizontalBorders(stateColumns);
        addColumnHeaders(stateColumns);
        switch (dbOperation) {
            case UPDATE:
                buildUpdate(stateColumns);
                break;
        }
        addHorizontalBorders(stateColumns);
    }

    private void addHorizontalBorders(List<StateColumn> stateColumns) {
        stateColumns.forEach(stateColumn -> {
            builder.append(PADDING);
            builder.append(stateColumn.hasDiff() ? DIFF_HORIZONTAL_BORDER : PADDING);
            String border = "";
            String filler = stateColumn.hasDiff() ? DIFF_HORIZONTAL_BORDER : PADDING;
            int maxLength = MAX_COL_LENGTH < stateColumn.getMaxLength() ? MAX_COL_LENGTH : stateColumn.getMaxLength();
            for (short i = 0; i < maxLength; i++) {
                border += filler;
            }
            builder.append(border);
            builder.append(stateColumn.hasDiff() ? DIFF_HORIZONTAL_BORDER : PADDING);
            builder.append(PADDING);
        });
        appendNewLine();
    }

    private void addColumnHeaders(List<StateColumn> stateColumns) {
        stateColumns.forEach(stateColumn -> {
            append(stateColumn, stateColumn.getColumnName(), HEADER_UNDERLINE_PADDING);
        });
        appendNewLine();
    }

    private void buildUpdate(List<StateColumn> stateColumns) {
        stateColumns.forEach(stateColumn -> {
            append(stateColumn, stateColumn.getOldState(), PADDING);
        });
        appendNewLine();
        stateColumns.forEach(stateColumn -> {
            append(stateColumn, stateColumn.getNewState(), PADDING);
        });
        appendNewLine();
    }

    private void append(StateColumn stateColumn, String value, String padding) {
        builder.append(PADDING);
        builder.append(stateColumn.hasDiff() ? DIFF_VERTICAL_BORDER : PADDING);
        int maxLength = MAX_COL_LENGTH < stateColumn.getMaxLength() ? MAX_COL_LENGTH : stateColumn.getMaxLength();
        int substringLength = maxLength - ELLIPSIS.length();
        int valueLength = value.length();
        String finalValue = value;
        if (valueLength > maxLength) {
            finalValue = value.substring(0, substringLength) + ELLIPSIS;
            stateColumn.setCut(true);
        } else if (valueLength < maxLength) {
            int lengthDiff = substringLength + ELLIPSIS.length() - valueLength;
            for (short i = 0; i < lengthDiff; i++) {
                finalValue += padding;
            }
        }
        builder.append(finalValue);
        builder.append(stateColumn.hasDiff() ? DIFF_VERTICAL_BORDER : PADDING);
        builder.append(PADDING);
    }

    private void appendNewLine() {
        builder.append(NEW_LINE);
    }

    @Override
    public String toString() {
        return builder.toString();
    }
    
}
