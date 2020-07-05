package com.infileconsole.app;

import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.google.inject.Inject;
import com.infileconsole.eval.Evaluable;
import com.infileconsole.eval.Evaluator;
import com.infileconsole.watcher.DirectoryTreeWatcher;
import com.infileconsole.watcher.WatchedFile;

public class Dispatch {
    private Path root;
    private boolean closeSignal;
    private BlockingQueue<Evaluable> evalQueue;
    
    @Inject
    private DirectoryTreeWatcher watcher;

    public void init() {
        this.closeSignal = false;
        evalQueue = new ArrayBlockingQueue<Evaluable>(5);

        watcher.setDispatch(this);
        watcher.init();
        watcher.startDirectoryWatchers();

        while (!closeSignal) {
            if (evalQueue.size() > 0) {
                // System.out.println(evalQueue);
                Evaluable evaluator = evalQueue.poll();
                Thread evaluatorThread = new Thread((Evaluator)evaluator, "evaluator");
                evaluatorThread.start();
            }
        }
    }

    public void queueFileEval(WatchedFile file) {
        Evaluable evaluator = new Evaluator(file);
        evalQueue.add(evaluator);
    }

    public Path getRoot() {
        return root;
    }

    public void setRoot(Path root) {
        this.root = root;
    }

    public void sendCloseSignal() {
        closeSignal = true;
    }
}