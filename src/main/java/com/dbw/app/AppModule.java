package com.dbw.app;

import com.google.inject.AbstractModule;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        // bind(Watcher.class).to(AuditTableWatcher.class).in(Scopes.SINGLETON);
    }
}