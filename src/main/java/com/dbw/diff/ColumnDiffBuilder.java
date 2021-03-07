package com.dbw.diff;

import com.dbw.output.OutputBuilder;
import com.dbw.util.StringUtils;
import com.google.inject.Singleton;

import java.util.Collections;

@Singleton
public class ColumnDiffBuilder implements OutputBuilder {
    private StringBuilder builder;

    public void init() {
        builder = new StringBuilder();
    }

    public void build(StateColumn stateColumn) {
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
    }

    @Override
    public String toString() {
        return builder.toString();
    }

}
