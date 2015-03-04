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
 * This thread pool is used to perform the execution of a deployed model.
 * The default approach is a single threaded approach, this means that start, stop, setProperty, sendData and receiveEvent are all executed within the same single thread.
 * @deprecated: The multi-threaded approach is deprecated. If used, there is no guarantee for thread synchronization and data integrity when calling start, stop, setProperty, sendData or receiveEvent.  
 * @author mad
 *
 */
public class AstericsModelExecutionThreadPool {
	//The default submit timeout for a task when called blocking with execAndWaitOnModelExecutorLifecycleThread(...).
	public static int TASK_SUBMIT_TIMEOUT=20000;
	/* Set default value to 1, because only in the single threaded approach there is deterministic execution of data propagation and event notification */
	private static final int DEFAULT_POOL_SIZE = 1;
	
	private static final String MODEL_EXECUTOR = "ModelExecutor";
	private static final String MODEL_EXECUTOR_LIFECYCLE = MODEL_EXECUTOR+"-Lifecycle";

	private static final String TASK_SUBMIT_TIMEOUT_PROPERTY = "ThreadPoolTasks.submitTimeout";	
	private static final String THREAD_POOL_SIZE = "ThreadPool."+MODEL_EXECUTOR+".size";
	private static final Logger logger=AstericsErrorHandling.instance.getLogger();
	

	public static final AstericsModelExecutionThreadPool instance = new AstericsModelExecutionThreadPool();
			
	//pool for the execution of model tasks: is identical with modelExecutorLifecycle in case of the single threaded approach.
	private ExecutorService pool;
	//stores last nr. of fallback thread
	private static int fallbackNr=0;
	private ThreadFactory fallbackThreadFactory=new ThreadFactory() {				
		@Override
		public Thread newThread(Runnable r) {
			String threadName=MODEL_EXECUTOR_LIFECYCLE+"-Fallback-"+fallbackNr++;
			logger.warning("ModelExecutor ["+Thread.currentThread().getName()+"]: Switching to fallbackPool with Thread: "+threadName);

			Thread newThread=Executors.defaultThreadFactory().newThread(r);
			newThread.setName(threadName);
			return newThread;					
		}
	};
	
	//This the executor for the single threaded approach
	private ExecutorService modelExecutorLifecycle = Executors
			.newSingleThreadExecutor(new ThreadFactory() {
				
				@Override
				public Thread newThread(Runnable arg0) {
					logger.fine("Creating Thread: "+MODEL_EXECUTOR_LIFECYCLE);
					
					Thread newThread=Executors.defaultThreadFactory().newThread(arg0);
					newThread.setName(MODEL_EXECUTOR_LIFECYCLE);
					return newThread;
				}
			});

	private AstericsModelExecutionThreadPool() {
		TASK_SUBMIT_TIMEOUT=new Integer(AREProperties.instance.getProperty(TASK_SUBMIT_TIMEOUT_PROPERTY, String.valueOf(TASK_SUBMIT_TIMEOUT)));
		logger.info(TASK_SUBMIT_TIMEOUT_PROPERTY+"="+TASK_SUBMIT_TIMEOUT);		
		AREProperties.instance.setProperty(TASK_SUBMIT_TIMEOUT_PROPERTY, Integer.toString(TASK_SUBMIT_TIMEOUT));
		
		int poolSize=new Integer(AREProperties.instance.getProperty(THREAD_POOL_SIZE, Integer.toString(DEFAULT_POOL_SIZE)));
		logger.info(THREAD_POOL_SIZE+"="+poolSize);
		AREProperties.instance.setProperty(THREAD_POOL_SIZE,Integer.toString(poolSize));		
		
		pool=modelExecutorLifecycle;
		if(poolSize > 1) {
			logger.warning("\n\nWARNING: Multi-threaded mode ist DEPRECATED! - "+THREAD_POOL_SIZE+" > 1, creating thread pool\n\n");
			pool=Executors.newFixedThreadPool(poolSize, new ThreadFactory() {
				private int threadNr=0;				
				@Override
				public Thread newThread(Runnable r) {
					// TODO Auto-generated method stub
					String threadName=MODEL_EXECUTOR+"-"+threadNr++;
					logger.fine("Creating Thread: "+threadName);

					Thread newThread=Executors.defaultThreadFactory().newThread(r);
					newThread.setName(threadName);
					return newThread;					
				}
			});
		} else {
			logger.info(THREAD_POOL_SIZE+" <= 1, using single threaded model execution approach");
		}
		
		//pool = Executors.newCachedThreadPool();
		/*
		pool = new ThreadPoolExecutor(5, 20, 60,
		TimeUnit.SECONDS,
		new LinkedBlockingQueue<Runnable>(100));
		*/
	}
	
	public static AstericsModelExecutionThreadPool getInstance() {
		return instance;
	}	

	/**
	 * Executes (non-blocking) the given Runnable either in the modelExecutorLifecycle thread (single threaded approach) or in a thread pool of userdefined size.
	 * @param r
	 */
	public void execute(Runnable r) {
		pool.execute(r);
	}

	/**
	 * Submits (blocking) the given Callable and waits until completion either in the modelExecutorLifecycle thread (single threaded approach) or in a thread pool of userdefined size.
	 * @param r
	 */	
	public <V> V submit(Callable<V> r) throws InterruptedException, ExecutionException, TimeoutException {
		Future<V> f=pool.submit(r);
		try{
			return f.get(TASK_SUBMIT_TIMEOUT,TimeUnit.MILLISECONDS);
		}catch(TimeoutException e) {
			logger.warning("["+Thread.currentThread().getName()+"]: Task execution timeouted, trying to cancel task");
			if(f!=null) {
				f.cancel(true);
			}
			throw(e);
		}		
	}

	/**
	 * Executes (blocking, waits for termination) the given Runnable by the Thread instance "ModelExecutor".
	 * If the execution hangs a timeout arises after TASK_SUBMIT_TIMEOUT.
	 * @param r
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException 
	 */
	public void execAndWaitOnModelExecutorLifecycleThread(Runnable r) throws InterruptedException,
			ExecutionException, TimeoutException {
		try {
			execAndWaitOnModelExecutorLifecycleThread(Executors.callable(r));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(e instanceof InterruptedException) {
				throw (InterruptedException)e;
			} else if(e instanceof ExecutionException) {
				throw (ExecutionException)e;
			} else if (e instanceof TimeoutException) {
				throw (TimeoutException)e;
			} else {
				logger.warning("Exception occurred: "+e.getClass()+", message: "+e.getMessage());
			}
		}
	}
	
	/**
	 * Executes (blocking, waits for termination) the given Callable by the Thread instance "ModelExecutor".
	 * If the execution hangs a timeout arises after TASK_SUBMIT_TIMEOUT.
	 * @param c
	 * @throws Exception
	 */
	public void execAndWaitOnModelExecutorLifecycleThread(Callable c) throws Exception {
		//if (MODEL_EXECUTOR.equals(Thread.currentThread().getName())) {
		if (Thread.currentThread().getName().startsWith(MODEL_EXECUTOR_LIFECYCLE)) {
			// We are already executed by the ModelExecutor Thread so just call the
			// Runnable.run() method
			AstericsErrorHandling.instance.getLogger().fine(
					"ModelExecutor: Current thread: "
							+ Thread.currentThread().getName()
							+ ", running r.run() in this thread");
			c.call();
		} else {
			// Execute with ModelExecutor and wait for response
			// "blocked execution"
			AstericsErrorHandling.instance.getLogger().fine(
					"ModelExecutor: Current thread: "
							+ Thread.currentThread().getName()
							+ ", Submitting on modelExecutorLifecycle thread");
			Future f = modelExecutorLifecycle.submit(c);
			try {
				f.get(TASK_SUBMIT_TIMEOUT, TimeUnit.MILLISECONDS);
			} catch (TimeoutException e) {				
				logger.warning("[" + MODEL_EXECUTOR_LIFECYCLE
						+ "]: Task execution timeouted");
				switchToFallbackPoolInternal();
				throw e;
			}
		}
	}
	
	/**
	 * Creates a new thread pool of size 1 and exchanges (currently hanging) thread pools (modelExecutorLifecycle, pool) 
	 * for model execution with the new instance.
	 */	
	private void switchToFallbackPoolInternal() {
		//Each time an execution timeouts the caller can switch to a new threadpool, to not risk a hanging ARE
		//NOTE: keep it of size one to ensure that the models are executed thread safe!!
		//is used as a fallbackpool to stop a hanging model in case of a single-threaded approach		

		//Synchronize to ensure that both modelExecutorLifecycle and pool get the same instance.
		synchronized(fallbackThreadFactory) {
			ExecutorService fallbackPool=Executors.newFixedThreadPool(1, fallbackThreadFactory);
			modelExecutorLifecycle=fallbackPool;
			pool=fallbackPool;
		}		
	}
	
	/**
	 * Creates a new thread pool of size 1 and exchanges (currently hanging) thread pools (modelExecutorLifecycle, pool) 
	 * for model execution with the new instance.
	 */
	public void switchToFallbackPool() {		
		//logger.warning("ModelExecutor ["+Thread.currentThread()+"]: Switching to fallbackPool: DISABLED");
		
		//Don't use manual switching any more, use automatic switchToFallbackPoolInternal() after a timeout in execAndWaitOnModelExecutorLifecycleThread instead
	}
}
