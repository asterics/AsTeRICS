/*
 *    AsTeRICS - Assistive Technology Rapid Integration and Construction Set
 * 
 * 
 *        d8888      88888888888       8888888b.  8888888 .d8888b.   .d8888b. 
 *       d88888          888           888   Y88b   888  d88P  Y88b d88P  Y88b
 *      d88P888          888           888    888   888  888    888 Y88b.     
 *     d88P 888 .d8888b  888   .d88b.  888   d88P   888  888         "Y888b.  
 *    d88P  888 88K      888  d8P  Y8b 8888888P"    888  888            "Y88b.
 *   d88P   888 "Y8888b. 888  88888888 888 T88b     888  888    888       "888
 *  d8888888888      X88 888  Y8b.     888  T88b    888  Y88b  d88P Y88b  d88P
 * d88P     888  88888P' 888   "Y8888  888   T88b 8888888 "Y8888P"   "Y8888P" 
 *
 *
 *                    homepage: http://www.asterics.org 
 *
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

/*
This code is based on the example of how to block/consume keyboard events: https://github.com/kwhat/jnativehook/wiki/Usage#consuming-events-unsupported
*/
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

    @Override
    public void shutdown() {
        isRunning = false;
    }

    @Override
    public List<Runnable> shutdownNow() {
        return new ArrayList<Runnable>(0);
    }

    @Override
    public boolean isShutdown() {
        return !isRunning;
    }

    @Override
    public boolean isTerminated() {
        return !isRunning;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return true;
    }

    @Override
    public void execute(Runnable r) {
        r.run();
    }
}
