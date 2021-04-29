package com.dbw.cfg;

import com.dbw.err.UnrecoverableException;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class Config {
    private String path;
    private DatabaseConfig database;
    private SettingsConfig settings;
    private Set<String> tables;
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

    public SettingsConfig getSettings() {
        return settings;
    }

    public Optional<Integer> getOperationsMinimum() {
        return Optional.ofNullable(settings).map(SettingsConfig::getOperationsMinimum);
    }

    public Optional<Integer> getOperationsLimit() {
        return Optional.ofNullable(settings).map(SettingsConfig::getOperationsLimit);
    }

    public boolean areOperationsSettingsPresent() {
        return settings != null && settings.getOperationsMinimum() != null && settings.getOperationsLimit() != null;
    }

    public Set<String> getTables() {
        return Collections.unmodifiableSet(tables);
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
                .and(ConfigValidator.areOperationsSettingsBothDeclared())
                .and(ConfigValidator.areOperationsSettingsGtThanZero())
                .apply(this);
        if (!result.equals(ConfigValidator.ValidationResult.SUCCESS)) {
            throw new UnrecoverableException("ConfigValidationException", String.format(result.msg, path));
        }
    }
}
