package com.dbw.cfg;

import com.dbw.db.Common;
import com.dbw.db.DatabaseType;
import com.dbw.log.ErrorMessages;

import java.util.function.Function;

interface ConfigValidator extends Function<Config, ConfigValidator.ValidationResult> {
    enum ValidationResult {
        SUCCESS(""),
        UNKNOWN_DB_TYPE(ErrorMessages.CFG_UNKNOWN_DB_TYPE),
        AUDIT_TABLE_WATCH_ATTEMPT(ErrorMessages.CFG_AUDIT_TABLE_WATCH_ATTEMPT),
        CFG_BOTH_OP_SETTINGS_NOT_DECLARED(ErrorMessages.CFG_BOTH_OP_SETTINGS_NOT_DECLARED),
        CFG_OP_SETTINGS_LT_ZERO(ErrorMessages.CFG_OP_SETTINGS_LT_ZERO);

        public final String msg;

        ValidationResult(String msg) {
            this.msg = msg;
        }
    }

    static ConfigValidator isDbTypeKnown() {
        return config ->
                DatabaseType.getTypeList().contains(config.getDatabase().getType().trim().toLowerCase()) ?
                        ValidationResult.SUCCESS : ValidationResult.UNKNOWN_DB_TYPE;
    }

    static ConfigValidator isAuditTableOnWatchList() {
        return config ->
                config.getTables().contains(Common.DBW_AUDIT_TABLE_NAME.trim()) ||
                config.getTables().contains(Common.DBW_AUDIT_TABLE_NAME.trim().toLowerCase()) ?
                        ValidationResult.AUDIT_TABLE_WATCH_ATTEMPT : ValidationResult.SUCCESS;
    }

    static ConfigValidator areOperationsSettingsBothDeclared() {
        return config ->
                (!config.getOperationsMinimum().isPresent() && !config.getOperationsLimit().isPresent()) ||
                        ((config.getOperationsMinimum().isPresent() || config.getOperationsLimit().isPresent()) &&
                        config.getOperationsMinimum().isPresent() && config.getOperationsLimit().isPresent()) ?
                        ValidationResult.SUCCESS : ValidationResult.CFG_BOTH_OP_SETTINGS_NOT_DECLARED;
    }

    static ConfigValidator areOperationsSettingsGtThanZero() {
        return config ->
                config.areOperationsSettingsPresent() &&
                        ((config.getOperationsMinimum().isPresent() && config.getOperationsMinimum().get() < 0) ||
                        (config.getOperationsLimit().isPresent() && config.getOperationsLimit().get() < 0)) ?
                        ValidationResult.CFG_OP_SETTINGS_LT_ZERO : ValidationResult.SUCCESS;
    }

    default ConfigValidator and(ConfigValidator other) {
        return config -> {
            ValidationResult result = this.apply(config);
            return result.equals(ValidationResult.SUCCESS) ? other.apply(config) : result;
        };
    }
}
