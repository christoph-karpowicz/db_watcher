package com.dbw.frame;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.dbw.app.ObjectCreator;
import com.dbw.db.AuditRecord;
import com.dbw.db.Database;
import com.dbw.diff.Diff;
import com.dbw.diff.StateDiffService;
import com.dbw.err.StateDataProcessingException;
import com.dbw.diff.StateColumn;
import com.google.inject.Inject;

public class AuditFrame {
    private AuditRecord auditRecord;
    private Diff diff;
    private Database db;
    private List<StateColumn> stateColumns;
    private String timeSincePrevious;

    @Inject
    private StateDiffService diffService;

    public AuditRecord getAuditRecord() {
        return auditRecord;
    }

    public void setAuditRecord(AuditRecord auditRecord) {
        this.auditRecord = auditRecord;
    }

    public void setDb(Database db) {
        this.db = db;
    }

    public void createDiff() throws StateDataProcessingException, SQLException {
        diff = diffService.createDiff(db, auditRecord);
    }

    public void createStateColumns() {
        stateColumns = new ArrayList<>();
        Set<String> columnNames = diff.getStateColumnNames(auditRecord.getOperation());
        for (String columnName : columnNames) {
            String oldStateValue = diffService.stateValueToString(diff.getOldState().get(columnName));
            String newStateValue = diffService.stateValueToString(diff.getNewState().get(columnName));
            StateColumn statePair = new StateColumn(columnName, oldStateValue, newStateValue);
            statePair.compare();
            stateColumns.add(statePair);
        }
        setDiffStates(stateColumns);
    }

    private void setDiffStates(List<StateColumn> stateColumns) {
        this.stateColumns = stateColumns;
    }

    public void setTimeSincePrevious(String timeSincePrevious) {
        this.timeSincePrevious = timeSincePrevious;
    }

    @Override
    public String toString() {
        AuditFrameBuilder builder = ObjectCreator.create(AuditFrameBuilder.class);
        builder.setDbConfig(db.getDbConfig());
        builder.setAuditRecord(auditRecord);
        builder.setStateColumns(stateColumns);
        builder.setTimeSincePrevious(timeSincePrevious);
        builder.init();
        builder.build();
        return builder.toString();
    }

}
