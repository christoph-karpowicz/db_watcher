package com.dbw.db.query;

import com.dbw.app.App;
import com.dbw.db.Common;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class SelectAuditRecordsQueryBuilder {
    private final String base;

    public String build() {
        List<String> parts = Lists.newArrayList();
        parts.add(base);
        if (App.options.getTables().isPresent()) {
            String inClause = new InClauseBuilder(Common.COLNAME_TABLE_NAME, App.options.getTables().get()).build();
            parts.add(inClause);
        }
        return String.join(Common.SPACE_DELIMITER, parts);
    }
}
