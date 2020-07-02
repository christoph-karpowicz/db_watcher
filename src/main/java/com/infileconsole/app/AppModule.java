package com.infileconsole.app;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.infileconsole.session.SessionStore;
import com.infileconsole.session.Store;
import com.infileconsole.watcher.DirectoryTreeWatcher;
import com.infileconsole.watcher.Watcher;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Watcher.class).to(DirectoryTreeWatcher.class).in(Scopes.SINGLETON);
        bind(Store.class).to(SessionStore.class).in(Scopes.SINGLETON);
    }
}