package com.dbw.cfg;

import com.dbw.err.UnrecoverableException;

import java.util.Collections;
import java.util.List;

public class Config {
    public static final String DEFAULT_SCHEMA = "public";

    private String path;
    private DatabaseConfig database;
    private List<String> tables;
    private boolean changed;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public DatabaseConfig getDatabase() {
        return database;
    }

    public List<String> getTables() {
        return Collections.unmodifiableList(tables);
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public void validate() throws UnrecoverableException {
        ConfigValidator.ValidationResult result =
                ConfigValidator.isAuditTableOnWatchList()
                .and(ConfigValidator.isDbTypeKnown())
                .apply(this);
        if (!result.equals(ConfigValidator.ValidationResult.SUCCESS)) {
            throw new UnrecoverableException("ConfigValidationException", String.format(result.msg, path));
        }
    }
}
