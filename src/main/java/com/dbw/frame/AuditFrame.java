package com.dbw.frame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dbw.db.AuditRecord;
import com.dbw.db.Database;
import com.dbw.db.Operation;
import com.dbw.diff.Diff;
import com.dbw.diff.StateColumn;
import com.dbw.diff.StateDiffService;
import com.google.inject.Inject;

public class AuditFrame {
    private AuditRecord auditRecord;
    private String diff;
    private List<StateColumn> stateColumns;

    @Inject
    private StateDiffService diffService;

    public void setAuditRecord(AuditRecord auditRecord) {
        this.auditRecord = auditRecord;
    }

    public void findDiff(Database db) {
        Diff diffObject = diffService.createDiff(db, auditRecord);
        createStateColumns(diffObject.getOldState(), diffObject.getNewState());
        diff = diffService.findDiff(stateColumns, auditRecord);
    }

    private void createStateColumns(Map<String, Object> oldState, Map<String, Object> newState) {
        stateColumns = new ArrayList<StateColumn>();
        Set<String> columnNames = oldState.keySet();
        for (String columnName : columnNames) {
            String oldStateValue = (String)oldState.get(columnName);
            String newStateValue = (String)newState.get(columnName);
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
        StringBuilder builder = new StringBuilder();
        builder.append("--------------------------");
        builder.append("\nID: " + auditRecord.getId());
        builder.append("\nTable: " + auditRecord.getTableName());
        builder.append("\nOperation: " + Operation.valueOfSymbol(auditRecord.getOperation()));
        builder.append("\nTimestamp: " + auditRecord.getTimestamp());
        builder.append("\n");
        builder.append(diff);
        return builder.toString();
    }
}
