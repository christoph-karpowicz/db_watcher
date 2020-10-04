package com.dbw.diff;

import com.dbw.db.Operation;
import com.google.inject.Singleton;

@Singleton
public class DiffBuilder implements Builder {

    private StringBuilder builder;

    public void init() {
        builder = new StringBuilder();
    }

    public void build(Operation dbOperation) {
        switch (dbOperation) {
            case UPDATE:
                // for (Object value : diff.getOldState().values()) {
                //     diffBuilder.append(value);
                // }
                // for (Object value : diff.getNewState().values()) {
                //     diffBuilder.append(value);
                // }
                break;
        }
    }

    public void append(Object value) {
        String valueAsString = value.toString();
        if (valueAsString.length() > 10) {
            valueAsString = valueAsString.substring(0, 10) + "...";
        }
        builder.append(valueAsString);
        builder.append("   ");
    }

    @Override
    public String toString() {
        return builder.toString();
    }
    
}
