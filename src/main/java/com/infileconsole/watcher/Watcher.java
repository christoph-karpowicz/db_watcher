package com.infileconsole.watcher;

import com.infileconsole.app.Dispatch;

public interface Watcher {
    public void init();
    public void setDispatch(Dispatch dispatch);
}