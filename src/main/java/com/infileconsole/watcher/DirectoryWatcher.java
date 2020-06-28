package com.infileconsole.watcher;

import java.io.IOException;
import java.nio.file.*;

public class DirectoryWatcher implements Runnable {
    private String dirName;
    
    public DirectoryWatcher(String dirName) {
        this.dirName = dirName;
    }

    @Override
    public void run() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(dirName);
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
            boolean poll = true;
            while (poll) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    System.out.println("Event kind : " + event.kind() + " - File : " + event.context());
                }
                poll = key.reset();
            }
        } catch(IOException e) {
            System.out.println(e.getMessage());
        } catch(InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}