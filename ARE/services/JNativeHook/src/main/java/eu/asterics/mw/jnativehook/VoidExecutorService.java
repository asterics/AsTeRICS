package eu.asterics.mw.jnativehook;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

public class VoidExecutorService extends AbstractExecutorService {
    private boolean isRunning;

    public VoidExecutorService() {
        isRunning = true;
    }

    public void shutdown() {
        isRunning = false;
    }

    public List<Runnable> shutdownNow() {
        return new ArrayList<Runnable>(0);
    }

    public boolean isShutdown() {
        return !isRunning;
    }

    public boolean isTerminated() {
        return !isRunning;
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return true;
    }

    public void execute(Runnable r) {
        r.run();
    }
}
