package com.dbw.diff;

import com.google.inject.Singleton;

@Singleton
public class DiffBuilder implements Buildable {

    private StringBuilder builder;

    public void init() {
        builder = new StringBuilder();
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
