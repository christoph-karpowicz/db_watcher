package com.dbw.actions;

import com.dbw.cache.Cache;
import com.dbw.cli.CLIStrings;
import com.dbw.log.Level;
import com.dbw.log.Logger;
import com.dbw.log.SuccessMessages;
import com.dbw.log.WarningMessages;
import com.google.inject.Inject;

import java.util.Set;

public class ClearCacheAction {
    @Inject
    private Cache cache;

    private Set<String> configPaths;

    public void setConfigPaths(Set<String> configPath) {
        this.configPaths = configPath;
    }

    public void execute() {
        configPaths.forEach(path -> {
            if (!cache.isConfigPresent(path)) {
                String warnMsg = String.format(WarningMessages.CLEAR_CACHE_NOT_FOUND, path);
                Logger.log(Level.WARNING, warnMsg);
                return;
            }
            cache.removeConfig(path);
            String successMsg = String.format(SuccessMessages.CLEAR_CACHE_SUCCESS, path);
            Logger.log(Level.INFO, successMsg);
        });
    }
}
