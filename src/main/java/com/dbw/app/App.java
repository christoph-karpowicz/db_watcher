package com.dbw.app;

import com.dbw.actions.ClearCacheAction;
import com.dbw.actions.DbAction;
import com.dbw.actions.DeleteFirstNRowsAction;
import com.dbw.actions.PurgeAction;
import com.dbw.cli.CLI;
import com.dbw.err.DbwException;
import com.dbw.err.UnrecoverableException;
import com.dbw.output.OutputManager;
import com.dbw.watcher.WatcherManager;
import com.google.common.base.Strings;
import com.google.inject.Inject;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

public class App {
    public static final short DEFAULT_RUN_INTERVAL = 500;

    @Inject
    private WatcherManager watcherManager;
    @Inject
    private OutputManager outputManager;

    public static CLI.ParsedOptions options;

    public static short getInterval() {
        return Optional.ofNullable(App.options.getInterval()).orElse(DEFAULT_RUN_INTERVAL);
    }

    public void init(String[] commandLineArgs) throws UnrecoverableException {
        AppInitializer appInitializer = ObjectCreator.create(AppInitializer.class);
        appInitializer.setCommandLineArgs(commandLineArgs);
        appInitializer.init();
    }

    public void start() throws DbwException {
        boolean shutdownAfter = executeActions();
        if (shutdownAfter) {
            shutdown();
            return;
        }
        addShutdownHook();
        startWatchers();
    }

    private boolean executeActions() throws DbwException {
        if (options.getClearCache()) {
            ClearCacheAction clearCacheAction = ObjectCreator.create(ClearCacheAction.class);
            Set<String> configPaths = watcherManager.getConfigPaths();
            clearCacheAction.setConfigPaths(configPaths);
            clearCacheAction.execute();
            return true;
        }
        String deleteFirstNRowsOption = options.getDeleteFirstNRows();
        if (!Strings.isNullOrEmpty(deleteFirstNRowsOption)) {
            DeleteFirstNRowsAction deleteFirstNRowsAction = ObjectCreator.create(DeleteFirstNRowsAction.class);
            deleteFirstNRowsAction.setNumberOfRowsToDelete(deleteFirstNRowsOption);
            deleteFirstNRowsAction.execute();
        }
        if (options.getPurge()) {
            DbAction purgeAction = ObjectCreator.create(PurgeAction.class);
            purgeAction.execute();
            return true;
        }
        return false;
    }

    private void startWatchers() throws UnrecoverableException {
        watcherManager.init();
        watcherManager.startAll();
        outputManager.pollAndOutput();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void shutdown() {
        try {
            watcherManager.terminateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
