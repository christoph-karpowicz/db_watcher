package com.dbw.watcher;

import com.dbw.err.UnrecoverableException;
import com.dbw.log.ErrorMessages;
import com.dbw.log.Level;
import com.dbw.log.Logger;
import com.google.inject.Singleton;

import java.util.Hashtable;
import java.util.List;

@Singleton
public class WatcherCheckIn extends Hashtable<Watcher, Boolean> {
    public void init(List<Watcher> watchers) {
        watchers.forEach(this::checkOut);
    }

    public synchronized void checkIn(Watcher watcher) {
        this.put(watcher, Boolean.TRUE);
        try {
            this.wait();
        } catch (InterruptedException e) {
            new UnrecoverableException("WatcherCheckIn", ErrorMessages.WATCHER_INTERRUPT).handle();
        }
    }

    public synchronized void checkOutAll() {
        for (Watcher watcher : this.keySet()) {
            checkOut(watcher);
        }
        this.notifyAll();
    }

    private void checkOut(Watcher watcher) {
        this.put(watcher, Boolean.FALSE);
    }

    public boolean areAllCheckedIn() {
        return this.values().stream().allMatch(Boolean.TRUE::equals);
    }
}
