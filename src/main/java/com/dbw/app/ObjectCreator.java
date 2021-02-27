package com.dbw.app;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ObjectCreator {
    private static final Injector injector = Guice.createInjector(new AppModule());

    public static <T> T create(Class<T> clazz) {
        return injector.getInstance(clazz);
    }
    
}
