package com.ttp.concurrent;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class HoldTerminateRunnable implements Runnable {

    private final AtomicBoolean isRunning;
    private final AtomicBoolean isTerminated;

    public HoldTerminateRunnable(AtomicBoolean isRunning, AtomicBoolean isTerminated) {
        this.isRunning = isRunning;
        this.isTerminated = isTerminated;
    }

    protected abstract void operate();

    protected  void hold() { }

    protected abstract void terminate();

    @Override
    public final void run() {
        if (isTerminated.get()) {
            terminate();
        }
        if (isRunning.get()) {
            operate();
        } else {
            hold();
        }
    }
}
