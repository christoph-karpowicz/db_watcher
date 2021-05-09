package com.dbw.diff;

import com.dbw.app.App;
import com.dbw.db.Operation;
import com.dbw.output.OutputBuilder;
import com.dbw.util.StringUtils;
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
            builder.append(stateColumn.hasDiff() ? EDGE_BORDER : PADDING);
            String filler = stateColumn.hasDiff() ? HORIZONTAL_BORDER : PADDING;
            int maxWidth = getFinalMaxColumnWidth(stateColumn);
            for (short i = 0; i < maxWidth; i++) {
                builder.append(filler);
            }
            builder.append(stateColumn.hasDiff() ? EDGE_BORDER : PADDING);
            builder.append(PADDING);
        });
        builder.append(NEW_LINE);
    }

    private void addColumnHeaders(List<StateColumn> stateColumns) {
        stateColumns.forEach(stateColumn ->
                appendHeader(stateColumn, stateColumn.getColumnName()));
        builder.append(NEW_LINE);
    }

    private void buildUpdate(List<StateColumn> stateColumns) {
        stateColumns.forEach(stateColumn ->
                appendOldState(stateColumn, stateColumn.getOldState()));
        builder.append(NEW_LINE);
        stateColumns.forEach(stateColumn ->
                appendNewState(stateColumn, stateColumn.getNewState()));
        builder.append(NEW_LINE);
    }

    private void buildInsert(List<StateColumn> stateColumns) {
        stateColumns.forEach(stateColumn ->
                appendOldState(stateColumn, stateColumn.getNewState()));
        builder.append(NEW_LINE);
    }

    private void buildDelete(List<StateColumn> stateColumns) {
        stateColumns.forEach(stateColumn ->
                appendOldState(stateColumn, stateColumn.getOldState()));
        builder.append(NEW_LINE);
    }

    private void appendHeader(StateColumn stateColumn, String value) {
        builder.append(PADDING);
        append(stateColumn, value, HEADER_UNDERLINE_PADDING, false);
    }

    private void appendOldState(StateColumn stateColumn, String value) {
        builder.append(stateColumn.hasDiff() ? OLD_STATE_PREFIX : PADDING);
        append(stateColumn, value, PADDING, false);
    }

    private void appendNewState(StateColumn stateColumn, String value) {
        builder.append(stateColumn.hasDiff() ? NEW_STATE_PREFIX : PADDING);
        append(stateColumn, value, PADDING, true);
    }
    
    private void append(StateColumn stateColumn, String value, String padding, boolean isUpdate) {
        builder.append(stateColumn.hasDiff() ? VERTICAL_BORDER : PADDING);
        String initialValue = (isUpdate && !stateColumn.hasDiff()) ? "" : value;
        builder.append(getFittedValue(stateColumn, initialValue, padding));
        builder.append(stateColumn.hasDiff() ? VERTICAL_BORDER : PADDING);
        builder.append(PADDING);
    }

    private String getFittedValue(StateColumn stateColumn, String value, String padding) {
        int maxWidth = getFinalMaxColumnWidth(stateColumn);
        int substringLength = maxWidth - ELLIPSIS.length();
        int valueLength = value.length();
        String finalValue = value;
        if (valueLength > maxWidth) {
            finalValue = getTruncatedValue(value, substringLength);
            stateColumn.setCut(true);
        } else if (valueLength < maxWidth) {
            finalValue = getPaddingFilledValue(value, substringLength, padding);
        }
        return finalValue;
    }

    private String getTruncatedValue(String value, int substringLength) {
        return String.join("", value.substring(0, substringLength), ELLIPSIS);
    }

    private String getPaddingFilledValue(String value, int substringLength, String padding) {
        int lengthDiff = substringLength + ELLIPSIS.length() - value.length();
        String multipliedPadding = StringUtils.multiplyNTimes(lengthDiff, padding);
        return String.join("", value, multipliedPadding);
    }

    private int getFinalMaxColumnWidth(StateColumn stateColumn) {
        return TableDiffBuilder.getMaxColumnWidth() < stateColumn.getMaxWidth() ?
                TableDiffBuilder.getMaxColumnWidth() : stateColumn.getMaxWidth();
    }

    @Override
    public String toString() {
        return builder.toString();
    }
    
}
