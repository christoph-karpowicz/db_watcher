package com.dbw.diff;

import java.util.List;

import com.dbw.db.Operation;
import com.google.inject.Singleton;

@Singleton
public class DiffBuilder implements Builder {
    private final short MAX_COL_LENGTH = 17;
    private final String ELLIPSIS = "...";
    private final String PADDING = " ";
    private final String HEADER_UNDERLINE_PADDING = "_";
    private final String DIFF_VERTICAL_BORDER = "|";
    private final String DIFF_HORIZONTAL_BORDER = "-";
    private final String NEW_LINE = "\n";

    private StringBuilder builder;

    public void init() {
        builder = new StringBuilder();
    }

    public void build(List<StateColumn> statePairs, Operation dbOperation) {
        addHorizontalBorders(statePairs);
        addColumnHeaders(statePairs);
        switch (dbOperation) {
            case UPDATE:
                buildUpdate(statePairs);
                break;
        }
        addHorizontalBorders(statePairs);
    }

    private void addHorizontalBorders(List<StateColumn> statePairs) {
        statePairs.forEach(pair -> {
            builder.append(PADDING);
            builder.append(pair.hasDiff() ? DIFF_HORIZONTAL_BORDER : PADDING);
            String border = "";
            String filler = pair.hasDiff() ? DIFF_HORIZONTAL_BORDER : PADDING;
            int maxLength = MAX_COL_LENGTH < pair.getMaxLength() ? MAX_COL_LENGTH : pair.getMaxLength();
            for (short i = 0; i < maxLength; i++) {
                border += filler;
            }
            builder.append(border);
            builder.append(pair.hasDiff() ? DIFF_HORIZONTAL_BORDER : PADDING);
            builder.append(PADDING);
        });
        appendNewLine();
    }

    private void addColumnHeaders(List<StateColumn> statePairs) {
        statePairs.forEach(pair -> {
            append(pair.getColumnName(), pair.getMaxLength(), pair.hasDiff(), HEADER_UNDERLINE_PADDING);
        });
        appendNewLine();
    }

    private void buildUpdate(List<StateColumn> statePairs) {
        statePairs.forEach(pair -> {
            append(pair.getOldState(), pair.getMaxLength(), pair.hasDiff(), PADDING);
        });
        appendNewLine();
        statePairs.forEach(pair -> {
            append(pair.getNewState(), pair.getMaxLength(), pair.hasDiff(), PADDING);
        });
        appendNewLine();
    }

    private void append(String value, int columnLength, boolean hasDiff, String padding) {
        builder.append(PADDING);
        builder.append(hasDiff ? DIFF_VERTICAL_BORDER : PADDING);
        int maxLength = MAX_COL_LENGTH < columnLength ? MAX_COL_LENGTH : columnLength;
        int substringLength = maxLength - ELLIPSIS.length();
        int valueLength = value.length();
        String finalValue = value;
        if (valueLength > maxLength) {
            finalValue = value.substring(0, substringLength) + ELLIPSIS;
        } else if (valueLength < maxLength) {
            int lengthDiff = substringLength + ELLIPSIS.length() - valueLength;
            for (short i = 0; i < lengthDiff; i++) {
                finalValue += padding;
            }
        }
        builder.append(finalValue);
        builder.append(hasDiff ? DIFF_VERTICAL_BORDER : PADDING);
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
