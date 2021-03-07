package com.dbw.diff;

import com.dbw.app.App;
import com.dbw.db.Operation;
import com.dbw.output.OutputBuilder;
import com.google.inject.Singleton;

import java.util.List;
import java.util.Objects;

@Singleton
public class TableDiffBuilder implements OutputBuilder {
    public static final short DEFAULT_MAX_COL_WIDTH = 17;
    public static final short DEFAULT_MAX_ROW_WIDTH = 120;

    private StringBuilder builder;

    public static short getMaxColumnWidth() {
        return Objects.isNull(App.options.getMaxColumnWidth()) ? DEFAULT_MAX_COL_WIDTH : App.options.getMaxColumnWidth();
    }

    public static short getMaxRowWidth() {
        return Objects.isNull(App.options.getMaxRowWidth()) ? DEFAULT_MAX_ROW_WIDTH : App.options.getMaxRowWidth();
    }
    
    public void init() {
        builder = new StringBuilder();
    }

    public void build(List<List<StateColumn>> stateRows, Operation dbOperation) {
        for (List<StateColumn> stateColumns : stateRows) {
            addHorizontalBorders(stateColumns);
            addColumnHeaders(stateColumns);
            switch (dbOperation) {
                case UPDATE:
                    buildUpdate(stateColumns);
                    break;
                case INSERT:
                    buildInsert(stateColumns);
                    break;
                case DELETE:
                    buildDelete(stateColumns);
                    break;
            }
            addHorizontalBorders(stateColumns);
        }
    }

    private void addHorizontalBorders(List<StateColumn> stateColumns) {
        stateColumns.forEach(stateColumn -> {
            builder.append(PADDING);
            builder.append(stateColumn.hasDiff() ? DIFF_EDGE_BORDER : PADDING);
            String filler = stateColumn.hasDiff() ? DIFF_HORIZONTAL_BORDER : PADDING;
            int maxWidth = getFinalMaxColumnWidth(stateColumn);
            for (short i = 0; i < maxWidth; i++) {
                builder.append(filler);
            }
            builder.append(stateColumn.hasDiff() ? DIFF_EDGE_BORDER : PADDING);
            builder.append(PADDING);
        });
        builder.append(NEW_LINE);
    }

    private void addColumnHeaders(List<StateColumn> stateColumns) {
        stateColumns.forEach(stateColumn -> {
            append(stateColumn, stateColumn.getColumnName(), HEADER_UNDERLINE_PADDING, false);
        });
        builder.append(NEW_LINE);
    }

    private void buildUpdate(List<StateColumn> stateColumns) {
        stateColumns.forEach(stateColumn -> {
            append(stateColumn, stateColumn.getOldState(), PADDING, false);
        });
        builder.append(NEW_LINE);
        stateColumns.forEach(stateColumn -> {
            append(stateColumn, stateColumn.getNewState(), PADDING, true);
        });
        builder.append(NEW_LINE);
    }

    private void buildInsert(List<StateColumn> stateColumns) {
        stateColumns.forEach(stateColumn -> {
            append(stateColumn, stateColumn.getNewState(), PADDING, false);
        });
        builder.append(NEW_LINE);
    }

    private void buildDelete(List<StateColumn> stateColumns) {
        stateColumns.forEach(stateColumn -> {
            append(stateColumn, stateColumn.getOldState(), PADDING, false);
        });
        builder.append(NEW_LINE);
    }
    
    private void append(StateColumn stateColumn, String value, String padding, boolean onlyIfDiff) {
        builder.append(PADDING);
        builder.append(stateColumn.hasDiff() ? VERTICAL_BORDER : PADDING);
        int maxWidth = getFinalMaxColumnWidth(stateColumn);
        int substringLength = maxWidth - ELLIPSIS.length();
        String initialFinalValue = (onlyIfDiff && !stateColumn.hasDiff()) ? "" : value;
        StringBuilder finalValueBuilder = new StringBuilder(initialFinalValue);
        int valueLength = finalValueBuilder.length();
        if (valueLength > maxWidth) {
            finalValueBuilder.setLength(0);
            finalValueBuilder.append(value, 0, substringLength);
            finalValueBuilder.append(ELLIPSIS);
            stateColumn.setCut(true);
        } else if (valueLength < maxWidth) {
            int lengthDiff = substringLength + ELLIPSIS.length() - valueLength;
            for (short i = 0; i < lengthDiff; i++) {
                finalValueBuilder.append(padding);
            }
        }
        builder.append(finalValueBuilder.toString());
        builder.append(stateColumn.hasDiff() ? VERTICAL_BORDER : PADDING);
        builder.append(PADDING);
    }

    private int getFinalMaxColumnWidth(StateColumn stateColumn) {
        return TableDiffBuilder.getMaxColumnWidth() < stateColumn.getMaxWidth() ? TableDiffBuilder.getMaxColumnWidth() : stateColumn.getMaxWidth();
    }

    @Override
    public String toString() {
        return builder.toString();
    }
    
}
