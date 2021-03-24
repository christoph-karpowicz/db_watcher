package com.dbw.diff;

import com.dbw.db.AuditRecord;
import com.dbw.log.ErrorMessages;
import com.google.common.base.Strings;

import java.util.function.Function;

import static com.dbw.db.Operation.*;

public interface StateDataValidator extends Function<AuditRecord, StateDataValidator.ValidationResult> {
    enum ValidationResult {
        SUCCESS,
        OLD_IS_NULL_OR_EMPTY,
        NEW_IS_NULL_OR_EMPTY;

        public String getErrorMessage(AuditRecord auditRecord) {
            String state;
            if (this.equals(OLD_IS_NULL_OR_EMPTY)) {
                state = "old";
            } else {
                state = "new";
            }
            return String.format(ErrorMessages.STATE_VALIDATION_NULL_OR_EMPTY, auditRecord.getId(), state);
        }
    }

    static StateDataValidator isOldStateNullOrEmpty() {
        return auditRecord ->
                auditRecord.getOperation().in(DELETE, UPDATE) && Strings.isNullOrEmpty(auditRecord.getOldData()) ?
                ValidationResult.OLD_IS_NULL_OR_EMPTY : ValidationResult.SUCCESS;
    }

    static StateDataValidator isNewStateNullOrEmpty() {
        return auditRecord ->
                auditRecord.getOperation().in(INSERT, UPDATE) && Strings.isNullOrEmpty(auditRecord.getNewData()) ?
                        ValidationResult.NEW_IS_NULL_OR_EMPTY : ValidationResult.SUCCESS;
    }

    default StateDataValidator and(StateDataValidator other) {
        return auditRecord -> {
            StateDataValidator.ValidationResult result = this.apply(auditRecord);
            return result.equals(StateDataValidator.ValidationResult.SUCCESS) ? other.apply(auditRecord) : result;
        };
    }
}
