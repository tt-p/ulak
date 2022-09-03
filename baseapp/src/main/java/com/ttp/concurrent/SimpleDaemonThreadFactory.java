package com.ttp.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleDaemonThreadFactory implements ThreadFactory {

    private final AtomicInteger threadCount;

    public SimpleDaemonThreadFactory() {
        threadCount = new AtomicInteger(0);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, "DaemonThread-%d".formatted(threadCount.getAndIncrement()));
        thread.setDaemon(true);
        return thread;
    }
}