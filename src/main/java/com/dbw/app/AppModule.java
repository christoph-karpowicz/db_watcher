package com.dbw.app;

import com.dbw.diff.Buildable;
import com.dbw.diff.DiffBuilder;
import com.dbw.diff.DiffService;
import com.dbw.diff.Differable;
import com.dbw.watcher.Watchable;
import com.dbw.watcher.Watcher;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Watchable.class).to(Watcher.class).in(Scopes.SINGLETON);
        bind(Differable.class).to(DiffService.class).in(Scopes.SINGLETON);
        bind(Buildable.class).to(DiffBuilder.class).in(Scopes.SINGLETON);
    }
}