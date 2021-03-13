package com.dbw.actions;

import com.dbw.err.DbwException;

public interface DbAction {
    void execute() throws DbwException;
}
