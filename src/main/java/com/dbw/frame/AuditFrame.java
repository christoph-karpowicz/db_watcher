package com.dbw.frame;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.dbw.app.ObjectCreator;
import com.dbw.db.AuditRecord;
import com.dbw.db.Operation;
import com.dbw.diff.Diff;
import com.dbw.diff.StateDiffService;
import com.dbw.diff.StateColumn;
import com.google.inject.Inject;

public class AuditFrame {
    private AuditRecord auditRecord;
    private Diff diff;
    private Class<?> dbClass;
    private List<StateColumn> stateColumns;

    @Inject
    private StateDiffService diffService;

    public void setAuditRecord(AuditRecord auditRecord) {
        this.auditRecord = auditRecord;
    }

    public void setDbClass(Class<?> dbClass) {
        this.dbClass = dbClass;
    }

    public void createDiff() {
        diff = diffService.createDiff(dbClass, auditRecord);
    }

    public void createStateColumns() {
        stateColumns = new ArrayList<StateColumn>();
        Set<String> columnNames = diff.getStateColumnNames(Operation.valueOfSymbol(auditRecord.getOperation()));
        for (String columnName : columnNames) {
            String oldStateValue = (String)diff.getOldState().get(columnName);
            String newStateValue = (String)diff.getNewState().get(columnName);
            StateColumn statePair = new StateColumn(columnName, oldStateValue, newStateValue);
            statePair.compare();
            stateColumns.add(statePair);
        }
        setDiffStates(stateColumns);
    }

    private void setDiffStates(List<StateColumn> stateColumns) {
        this.stateColumns = stateColumns;
    }

    @Override
    public String toString() {
        AuditFrameBuilder builder = ObjectCreator.create(AuditFrameBuilder.class);
        builder.setAuditRecord(auditRecord);
        builder.setStateColumns(stateColumns);
        builder.init();
        builder.build();
        return builder.toString();
    }

}
