package de.verdox.vcore.concurrent;

public class CatchingRunnable implements Runnable{
    private final Runnable delegate;

    public CatchingRunnable(Runnable delegate) {
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try {
            delegate.run();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                System.out.println(stackTraceElement.toString());
            }
            throw e;
        }
    }
}
