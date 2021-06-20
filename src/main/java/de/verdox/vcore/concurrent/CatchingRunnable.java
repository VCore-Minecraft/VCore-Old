/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.concurrent;

/**
 * Runnable that prints exceptions thrown
 */
public class CatchingRunnable implements Runnable{
    private final Runnable delegate;

    public CatchingRunnable(Runnable delegate) {
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try {
            delegate.run();
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                System.out.println(stackTraceElement.toString());
            }
            throw e;
        }
    }
}
