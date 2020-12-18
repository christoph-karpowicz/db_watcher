package com.dbw.app;

import com.dbw.err.DbwException;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Dbw {

    public static App app;
    
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppModule());
        app = injector.getInstance(App.class);
        try {
            app.init(args);
            app.start();
        } catch (DbwException e) {
            e.handle();
        }
    }
}
