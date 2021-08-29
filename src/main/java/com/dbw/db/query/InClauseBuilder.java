package com.dbw.db.query;

import com.dbw.db.Common;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class InClauseBuilder {
    private final String AND = "AND ";
    private final String IN_START = " IN (";
    private final String IN_END = ")";

    private final String columnName;
    private final Set<String> values;

    public String build() {
        List<String> parts = Lists.newArrayList();
        parts.add(AND);
        parts.add(columnName);
        parts.add(IN_START);
        parts.add(getValuesWithQuotes());
        parts.add(IN_END);
        return String.join("", parts);
    }

    private String getValuesWithQuotes() {
        Set<String> valuesWithQuotes = values.stream()
                .map(value -> Common.SINGLE_QUOTE + value + Common.SINGLE_QUOTE)
                .collect(Collectors.toSet());
        return String.join(Common.COMMA_DELIMITER, valuesWithQuotes);
    }
}
