package com.dbw.db;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class DriverClassLoader extends URLClassLoader {

    public DriverClassLoader(URL[] urls) {
        super(urls);
    }

    public void addFile (String path) throws MalformedURLException {
        String urlPath = "jar:file://" + path + "!/";
        addURL (new URL(urlPath));
    }

}
