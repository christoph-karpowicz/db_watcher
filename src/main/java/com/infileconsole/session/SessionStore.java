package com.infileconsole.session;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import com.google.inject.Singleton;

@Singleton
public class SessionStore implements Store {
    private Session activeSession;
    private BlockingQueue<Session> sessionQueue = new SynchronousQueue<Session>();
}