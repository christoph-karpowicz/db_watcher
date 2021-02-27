package com.dbw.app;

import com.dbw.err.DbwException;

public class Dbw {

    public static App app;
    
    public static void main(String[] args) {
        app = ObjectCreator.create(App.class);
        try {
            app.init(args);
            app.start();
        } catch (DbwException e) {
            e.handle();
        }
    }
}
