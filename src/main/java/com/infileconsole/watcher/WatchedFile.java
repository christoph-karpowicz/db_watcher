package com.infileconsole.watcher;

import java.io.File;

public class WatchedFile extends File {
    private String id;
    
    public WatchedFile(String path) {
        super(path);
    }

    public String getId() {
        return id;
    }
    
    public void setId() {
        id = WatchedFileService.setId(this.toPath()).get();
    }
}