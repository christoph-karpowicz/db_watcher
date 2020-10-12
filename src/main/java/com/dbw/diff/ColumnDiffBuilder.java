package com.dbw.diff;

import java.util.List;

import com.dbw.db.Operation;
import com.dbw.output.OutputBuilder;
import com.google.inject.Singleton;

@Singleton
public class ColumnDiffBuilder implements OutputBuilder {
    private StringBuilder builder;

    public void init() {
        builder = new StringBuilder();
    }

    public void build(List<StateColumn> stateColumns, Operation dbOperation) {
        
    }

    @Override
    public String toString() {
        return builder.toString();
    }
    
}
