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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 *
 */

package eu.asterics.mw.services;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import eu.asterics.mw.are.AREProperties;

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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 *
 */

/**
 * This thread pool is to perform diverse tasks, such as sending and receiving CIM data, reading or writing to a socket, triggering timed tasks,....
 * The thread pool is a cached thread pool with no maximum number of threads ({@link http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Executors.html#newCachedThreadPool%28java.util.concurrent.ThreadFactory%29}). 
 * If you want to perform a model lifecycle task like starting, stopping, pausing or resuming or want to execute ensure that a certain task is executed
 * in the model executor thread, don't use this class but use @see AstericsModelExecutionThreadPool.
 *  @author mad
 *
 */
public class AstericsThreadPool {	
	private static final Logger logger=AstericsErrorHandling.instance.getLogger();
	
	public static final AstericsThreadPool instance = new AstericsThreadPool();
	protected static final String THREAD_POOL_PREFIX = "OtherTasks";
	private ExecutorService pool;

	private AstericsThreadPool() {
		pool = Executors.newCachedThreadPool(new ThreadFactory() {
			private int threadNr=0;				
			@Override
			public Thread newThread(Runnable r) {
				// TODO Auto-generated method stub
				String threadName=THREAD_POOL_PREFIX+"-"+threadNr++;
				logger.fine("Creating Thread: "+threadName);

				Thread newThread=Executors.defaultThreadFactory().newThread(r);
				newThread.setName(threadName);
				return newThread;					
			}
		});

		/*
		pool = new ThreadPoolExecutor(5, 20, 60,
		TimeUnit.SECONDS,
		new LinkedBlockingQueue<Runnable>(100));
		*/
	}
	
	public static AstericsThreadPool getInstance() {
		return instance;
	}
	
	/**
	 * Executes (non-blocking) the given Runnable in the thread pool and returns a {@link Future} instance for the task. 
	 * The Future can be used to get return values, receive Exceptions of the task or cancel the task. 
	 * @param r
	 * @return {@link Future}: The Future object of the task.
	 */
	public Future<?> execute(Runnable r) {
		//pool.execute(r);
		return pool.submit(r);
	}
	
	/**
	 * Executes (non-blocking) the given {@link Callable} in the thread pool and returns a {@link Future} instance for the task. 
	 * The Future can be used to get return values, receive Exceptions of the task or cancel the task. 
	 * @param <V>
	 * @param c
	 * @return
	 */
	public <V> Future<V> execute(Callable<V> c) {
		return pool.submit(c);
	}
		
	/**
	 * Executes (blocking) the given Callable in the thread pool.
	 * @param r
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	public <V> V submit(Callable<V> r) throws InterruptedException, ExecutionException, TimeoutException {
		Future<V> f=pool.submit(r);
		try{
			return f.get(AstericsModelExecutionThreadPool.TASK_SUBMIT_TIMEOUT,TimeUnit.MILLISECONDS);
		}catch(TimeoutException e) {
			logger.warning("["+Thread.currentThread()+"]: Task execution timeouted, trying to cancel task");
			if(f!=null) {
				f.cancel(true);
			}
			throw(e);
		}		
	}
}
