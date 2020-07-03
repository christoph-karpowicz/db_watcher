package com.infileconsole.watcher;

import java.io.IOException;
import java.nio.file.*;
import java.util.logging.Logger;

import com.infileconsole.app.Dispatch;

public class DirectoryWatcher implements Runnable {
    private Path path;
    private Dispatch dispatch;
    private Logger logger;
    
    public DirectoryWatcher(Path path, Dispatch dispatch) {
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
                for (WatchEvent<?> event : key.pollEvents()) {
                    Path filePath = Paths.get(path.toString() + "/" + event.context());
                    logger.info(filePath.toString() + " modified.");
                    dispatch.queueFileEval(filePath);
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