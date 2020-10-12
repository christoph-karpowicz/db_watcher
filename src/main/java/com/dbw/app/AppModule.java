package com.dbw.app;

import com.dbw.diff.TableDiffBuilder;
import com.dbw.output.OutputBuilder;
import com.dbw.diff.DiffService;
import com.dbw.diff.StateDiffService;
import com.dbw.watcher.Watcher;
import com.dbw.watcher.AuditTableWatcher;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Watcher.class).to(AuditTableWatcher.class).in(Scopes.SINGLETON);
        bind(DiffService.class).to(StateDiffService.class).in(Scopes.SINGLETON);
        bind(OutputBuilder.class).to(TableDiffBuilder.class).in(Scopes.SINGLETON);
    }
}