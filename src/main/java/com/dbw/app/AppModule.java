package com.dbw.app;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        // bind(Watchable.class).to(DirectoryTreeWatcher.class).in(Scopes.SINGLETON);
        // bind(Store.class).to(SessionStore.class).in(Scopes.SINGLETON);
    }
}