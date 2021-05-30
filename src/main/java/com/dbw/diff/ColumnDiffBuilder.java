package com.dbw.diff;

import com.dbw.output.OutputBuilder;
import com.dbw.util.StringUtils;

public class ColumnDiffBuilder implements OutputBuilder {

    public String build(StateColumn stateColumn) {
        StringBuilder builder = new StringBuilder();
        builder.append(StringUtils.multiplyNTimes(TableDiffBuilder.getMaxRowWidth() / 2, HR));
        builder.append(NEW_LINE);
        builder.append(stateColumn.getColumnName() + VERBOSE_DIFF_DIFF);
        builder.append(NEW_LINE);
        builder.append(VERBOSE_DIFF_BEFORE);
        builder.append(NEW_LINE);
        builder.append(stateColumn.getOldState());
        builder.append(NEW_LINE);
        builder.append(VERBOSE_DIFF_AFTER);
        builder.append(NEW_LINE);
        builder.append(stateColumn.getNewState());
        builder.append(NEW_LINE);
        return builder.toString();
    }

}
