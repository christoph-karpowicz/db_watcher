package com.infileconsole.watcher;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.logging.Logger;

import com.infileconsole.app.Dispatch;

public class DirectoryWatcher implements Runnable {
    private final Dispatch dispatch;
    private final Path path;
    private final Logger logger;
    
    public DirectoryWatcher(Dispatch dispatch, Path path) {
        this.path = path;
        this.dispatch = dispatch;
        this.logger = Logger.getGlobal();
    }

    @Override
    public void run() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            boolean poll = true;
            while (poll) {
                WatchKey key = watchService.take();
                List<WatchEvent<?>> events = key.pollEvents();
                for (WatchEvent<?> event : events) {
                    WatchedFile file = new WatchedFile(path.toString() + "/" + event.context());
                    if (!WatchedFileService.getId(file.toPath()).isPresent()) {
                        file.setId();
                    }
                    System.out.println(file.getId());
                    logger.info(file.toString() + " modified.");
                    dispatch.queueFileEval(file);
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