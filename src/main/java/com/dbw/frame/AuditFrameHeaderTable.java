package com.dbw.frame;

import com.dbw.output.OutputBuilder;
import com.dbw.util.StringUtils;
import com.google.common.collect.Lists;

import java.util.List;

public class AuditFrameHeaderTable implements OutputBuilder {
    private final short PADDING_AND_BORDER_LENGTH = 3;

    private final List<List<String>> table;
    protected final StringBuilder sb;
    private int rowWidth;

    public AuditFrameHeaderTable() {
        this.table = Lists.newArrayList();
        this.sb = new StringBuilder();
    }

    public void addRow(List<String> row) {
        table.add(row);
    }

    public void calculateRowWidth() {
        int width = 0;
        for (short i = 0; i < table.get(0).size(); i++) {
            width += getColumnWidth(i) + PADDING_AND_BORDER_LENGTH;
        }
        rowWidth = width;
    }

    public String build() {
        for (short i = 0; i < table.size(); i++) {
            for (short j = 0; j < table.get(i).size(); j++) {
                String value = table.get(i).get(j);
                int columnWidth = getColumnWidth(j);
                sb.append(PADDING);
                sb.append(getWithPadding(value, columnWidth));
                sb.append(PADDING);
                sb.append(VERTICAL_BORDER);
            }
            sb.append(NEW_LINE);
            if (rowWidth == 0) {
                rowWidth = sb.length();
            }
            if (i != table.size() - 1) {
                buildTableHorizontalBorder(VERTICAL_BORDER, rowWidth);
                sb.append(NEW_LINE);
            }
        }
        buildTableHorizontalBorder(EDGE_BORDER, rowWidth);
        sb.append(NEW_LINE);
        return sb.toString();
    }

    private int getColumnWidth(int index) {
        return table
                .stream()
                .map(row -> row.get(index).length())
                .reduce(0, Math::max);
    }

    public int getRowWidth() {
        return rowWidth;
    }

    private void buildTableHorizontalBorder(String edge, int rowWidth) {
        sb.append(StringUtils.multiplyNTimes(rowWidth - 1, HORIZONTAL_BORDER));
        sb.append(edge);
    }

    private String getWithPadding(String val, int columnWidth) {
        return val + StringUtils.multiplyNTimes(columnWidth - val.length(), PADDING);
    }
}
