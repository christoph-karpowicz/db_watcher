package com.dbw.frame;

import com.dbw.app.ObjectCreator;
import com.dbw.db.AuditRecord;
import com.dbw.db.Database;
import com.dbw.diff.Diff;
import com.dbw.diff.StateColumn;
import com.dbw.diff.StateDiffService;
import com.dbw.err.RecoverableException;
import com.google.inject.Inject;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AuditFrame {
    @Getter @Setter
    private AuditRecord auditRecord;
    private Diff diff;
    @Setter
    private Database db;
    private List<StateColumn> stateColumns;
    private String timeSincePrevious;
    @Setter
    private int frameNo;

    @Inject
    private StateDiffService diffService;

    public void createDiff() throws RecoverableException, SQLException {
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
        builder.setDbConfig(db.getConfig());
        builder.setAuditRecord(auditRecord);
        builder.setStateColumns(stateColumns);
        builder.setTimeSincePrevious(timeSincePrevious);
        builder.setFrameNo(frameNo);
        builder.init();
        builder.build();
        return builder.toString();
    }

}
