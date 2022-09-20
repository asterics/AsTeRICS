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
 *     This project has been partly funded by the European Commission,
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */

package eu.asterics.mw.services;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Logger;

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
 *     This project has been partly funded by the European Commission,
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */

/**
 * This thread pool is to perform diverse tasks, such as sending and receiving
 * CIM data, reading or writing to a socket, triggering timed tasks,.... The
 * thread pool is a cached thread pool with no maximum number of threads (
 * {@link http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Executors.html#newCachedThreadPool%28java.util.concurrent.ThreadFactory%29}
 * ). If you want to perform a model lifecycle task like starting, stopping,
 * pausing or resuming or want to execute ensure that a certain task is executed
 * in the model executor thread, don't use this class but use @see
 * AstericsModelExecutionThreadPool.
 * 
 * @author mad
 *
 */
public class AstericsThreadPool {
    private static final Logger logger = AstericsErrorHandling.instance.getLogger();

    public static final AstericsThreadPool instance = new AstericsThreadPool();
    protected static final String THREAD_POOL_PREFIX = "OtherTasks";
    private ExecutorService pool;
    private Set<String> runningNamedTasks = Collections.synchronizedSet(new HashSet<String>());


    private AstericsThreadPool() {
        pool = Executors.newCachedThreadPool(new ThreadFactory() {
            private int threadNr = 0;

            @Override
            public Thread newThread(Runnable r) {
                // TODO Auto-generated method stub
                String threadName = THREAD_POOL_PREFIX + "-" + threadNr++;
                logger.fine("Creating Thread: " + threadName);

                Thread newThread = Executors.defaultThreadFactory().newThread(r);
                newThread.setName(threadName);
                return newThread;
            }
        });

        /*
         * pool = new ThreadPoolExecutor(5, 20, 60, TimeUnit.SECONDS, new
         * LinkedBlockingQueue<Runnable>(100));
         */
    }

    public static AstericsThreadPool getInstance() {
        return instance;
    }

    /**
     * Executes (non-blocking) the given Runnable in the thread pool and returns
     * a {@link Future} instance for the task. The Future can be used to get
     * return values, receive Exceptions of the task or cancel the task.
     * 
     * @param r
     * @return {@link Future}: The Future object of the task.
     */
    public Future<?> execute(Runnable r) {
        // pool.execute(r);
        return pool.submit(r);
    }

    /**
     * Executes (non-blocking) the given Runnable in the thread pool and returns
     * a {@link Future} instance for the task. The Future can be used to get
     * return values, receive Exceptions of the task or cancel the task.
     *
     * @param r
     * @param taskId an ID associated to the given task
     * @return {@link Future}: The Future object of the task.
     */
    public Future<?> execute(final Runnable r, final String taskId) {
        runningNamedTasks.add(taskId);
        Future future = pool.submit(new Runnable() {
            @Override
            public void run() {
                r.run();
                runningNamedTasks.remove(taskId);
            }
        });
        return future;
    }

    /**
     * returns all ids of running and not finished tasks started with {@link AstericsThreadPool#execute(Runnable, String)}
     * or {@link AstericsThreadPool#execute(Callable, String)}
     * @return
     */
    public Set<String> getRunningTaskIds() {
        return new HashSet<>(runningNamedTasks);
    }

    /**
     * returns all ids of running and not finished tasks started with {@link AstericsThreadPool#execute(Runnable, String)}
     * or {@link AstericsThreadPool#execute(Callable, String)}, where the taskIdOfTask.contains(containedInTaskId)
     * @param containedInTaskId the taskIds to search for
     * @return
     */
    public Set<String> getRunningTaskIds(String containedInTaskId) {
        Set<String> returnSet = new HashSet<>();
        for (String taskId : runningNamedTasks) {
            if (taskId.contains(containedInTaskId)) {
                returnSet.add(taskId);
            }
        }
        return returnSet;
    }

    /**
     * returns a formatted String containing all running and not finished tasks started with {@link AstericsThreadPool#execute(Runnable, String)}
     * or {@link AstericsThreadPool#execute(Callable, String)}, where the taskIdOfTask.contains(containedInTaskId)
     * @param containedInTaskId the taskIds to search for
     * @return the formatted string, or an empty string if no running taskIds for the search string were found
     */
    public String getFormattedRunningNamedTasks(String containedInTaskId) {
        StringBuffer buffer = new StringBuffer();
        Set<String> runningTasks = getRunningTaskIds(containedInTaskId);
        if (!runningTasks.isEmpty()) {
            buffer.append(MessageFormat.format("running named tasks where taskId contains <{0}>:\n", containedInTaskId));
            for (String taskId : runningTasks) {
                buffer.append("\t" + taskId + "\n");
            }
        }
        return buffer.toString();
    }

    /**
     * Executes (non-blocking) the given {@link Callable} in the thread pool and
     * returns a {@link Future} instance for the task. The Future can be used to
     * get return values, receive Exceptions of the task or cancel the task.
     *
     * @param <V>
     * @param c
     * @return
     */
    public <V> Future<V> execute(Callable<V> c) {
        return pool.submit(c);
    }

    /**
     * Executes (non-blocking) the given {@link Callable} in the thread pool and
     * returns a {@link Future} instance for the task. The Future can be used to
     * get return values, receive Exceptions of the task or cancel the task.
     *
     * @param <V>
     * @param c
     * @param taskId
     * @return
     */
    public <V> Future<V> execute(final Callable<V> c, final String taskId) {
        runningNamedTasks.add(taskId);
        Future<V> future = pool.submit(new Callable<V>() {
            @Override
            public V call() throws Exception {
                V returnValue = c.call();
                runningNamedTasks.remove(taskId);
                return returnValue;
            }
        });
        return future;
    }

    /**
     * Executes (blocking) the given Callable in the thread pool.
     * 
     * @param r
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public <V> V submit(Callable<V> r) throws InterruptedException, ExecutionException, TimeoutException {
        Future<V> f = pool.submit(r);
        try {
            return f.get(AstericsModelExecutionThreadPool.TASK_SUBMIT_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            logger.warning("[" + Thread.currentThread() + "]: Task execution timeouted, trying to cancel task");
            if (f != null) {
                f.cancel(true);
            }
            throw (e);
        }
    }
}
