package com.dbw.cfg;

import com.dbw.err.UnrecoverableException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.Set;

@Getter
@Setter
public class Config {
    private String path;
    @Setter(AccessLevel.NONE)
    private DatabaseConfig database;
    @Setter(AccessLevel.NONE)
    private SettingsConfig settings;
    @Setter(AccessLevel.NONE)
    private Set<String> tables;
    private boolean changed;
    private String checkSum;

    public Optional<Integer> getOperationsMinimum() {
        return Optional.ofNullable(settings).map(SettingsConfig::getOperationsMinimum);
    }

    public Optional<Integer> getOperationsLimit() {
        return Optional.ofNullable(settings).map(SettingsConfig::getOperationsLimit);
    }

    public boolean areOperationsSettingsPresent() {
        return settings != null && settings.getOperationsMinimum() != null && settings.getOperationsLimit() != null;
    }

    public void validate() throws UnrecoverableException {
        ConfigValidator.ValidationResult result =
                ConfigValidator.isAuditTableOnWatchList()
                .and(ConfigValidator.isDbTypeKnown())
                .and(ConfigValidator.areOperationsSettingsBothDeclared())
                .and(ConfigValidator.areOperationsSettingsGtZero())
                .and(ConfigValidator.isOperationsLimitGtMinimum())
                .apply(this);
        if (!result.equals(ConfigValidator.ValidationResult.SUCCESS)) {
            throw new UnrecoverableException("ConfigValidationException", String.format(result.msg, path));
        }
    }
}
