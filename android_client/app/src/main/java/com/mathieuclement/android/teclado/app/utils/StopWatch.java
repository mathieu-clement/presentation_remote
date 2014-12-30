/*
Copyright (c) 2014, Mathieu Clément
BSD 3-Clause License
*/

package com.mathieuclement.android.teclado.app.utils;

import org.joda.time.Duration;
import org.joda.time.Instant;

public class StopWatch {
    private Instant startInstant;
    private boolean running = false;
    private boolean started = false;
    private Duration totalDuration = new Duration(0);

    public void start() {
        this.startInstant = new Instant();
        this.running = true;
        this.started = true;
    }


    public void reset() {
        this.running = false;
        this.started = false;
        totalDuration = new Duration(0);
    }

    public void pause() {
        this.running = false;
        totalDuration = totalDuration.plus(new Duration(startInstant, new Instant()));
    }

    public void resume() {
        if(!isStarted()) throw new IllegalStateException("Stopwatch cannot be resumed because it wasn't started.");
        start();
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isStarted() {
        return started;
    }

    public Duration getElapsedDuration() {
        return new Duration(startInstant, new Instant()).plus(totalDuration);
    }
}