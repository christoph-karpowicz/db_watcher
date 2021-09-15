package com.dbw.db;

import com.dbw.cfg.Config;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@AllArgsConstructor
public class TableRegexFinder {
    private final Config config;
    private final Database db;

    public Set<String> findWatchedTables() throws SQLException {
        if (config.getSettings().getTableNamesRegex()) {
            List<String> allTables = db.selectAllTables();
            Set<String> excludeRegex = config.getTables()
                    .stream()
                    .filter(regex -> regex.charAt(0) == '~')
                    .map(regex -> regex.substring(1))
                    .collect(Collectors.toSet());
            Set<String> includeRegex = config.getTables()
                    .stream()
                    .filter(regex -> !excludeRegex.contains(regex))
                    .collect(Collectors.toSet());
            Set<String> exclude = findTableNameMatches(allTables, excludeRegex);
            Set<String> include = findTableNameMatches(allTables, includeRegex)
                    .stream()
                    .filter(tableName -> !tableName.equalsIgnoreCase(Common.DBW_AUDIT_TABLE_NAME))
                    .collect(Collectors.toSet());
            include.removeAll(exclude);
            return include;
        } else {
            return config.getTables();
        }
    }

    private Set<String> findTableNameMatches(List<String> allTables, Set<String> regexes) {
        Set<String> matches = Sets.newHashSet();
        for (String regex : regexes) {
            if (regex.length() == 0) {
                continue;
            }
            Pattern pattern = Pattern.compile(regex);
            for (String tableName : allTables) {
                Matcher matcher = pattern.matcher(tableName);
                if (matcher.matches()) {
                    matches.add(tableName);
                }
            }
        }
        return matches;
    }

}
