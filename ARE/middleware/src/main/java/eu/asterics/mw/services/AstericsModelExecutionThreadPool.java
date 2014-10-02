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
 * The pool size can be increased in areProperties 
 * @author mad
 *
 */
public class AstericsModelExecutionThreadPool {
	public static int TASK_SUBMIT_TIMEOUT=7000;
	
	private static final String MODEL_EXECUTOR = "ModelExecutor";

	private static final String TASK_SUBMIT_TIMEOUT_PROPERTY = "ThreadPoolTasks.submitTimeout";	
	private static final String THREAD_POOL_SIZE = "ThreadPool."+MODEL_EXECUTOR+".size";
	private static final Logger logger=AstericsErrorHandling.instance.getLogger();
	

	public static final AstericsModelExecutionThreadPool instance = new AstericsModelExecutionThreadPool();
			
	//pool for the execution of model tasks: is identical with modelExecutorLifecycle in case of the single threaded approach.
	private ExecutorService pool;
	//is used as a fallbackpool to stop a hanging model in case of a single-threaded approach
	private ExecutorService fallbackPool;
	
	//This the executor for the single threaded approach
	private ExecutorService modelExecutorLifecycle = Executors
			.newSingleThreadExecutor(new ThreadFactory() {
				
				@Override
				public Thread newThread(Runnable arg0) {
					logger.fine("Creating Thread: "+MODEL_EXECUTOR);
					
					Thread newThread=Executors.defaultThreadFactory().newThread(arg0);
					newThread.setName(MODEL_EXECUTOR);
					return newThread;
				}
			});

	private AstericsModelExecutionThreadPool() {
		int TASK_SUBMIT_TIMEOUT=new Integer(AREProperties.instance.getProperty(TASK_SUBMIT_TIMEOUT_PROPERTY, "7000"));
		logger.info(TASK_SUBMIT_TIMEOUT_PROPERTY+"="+TASK_SUBMIT_TIMEOUT);		
		AREProperties.instance.setProperty(TASK_SUBMIT_TIMEOUT_PROPERTY, Integer.toString(TASK_SUBMIT_TIMEOUT));
		
		int poolSize=new Integer(AREProperties.instance.getProperty(THREAD_POOL_SIZE, "0"));
		logger.info(THREAD_POOL_SIZE+"="+poolSize);
		AREProperties.instance.setProperty(THREAD_POOL_SIZE,Integer.toString(poolSize));		
		
		pool=modelExecutorLifecycle;
		if(poolSize > 1) {
			logger.info(THREAD_POOL_SIZE+" > 1, creating thread pool");
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
		fallbackPool=Executors.newCachedThreadPool(new ThreadFactory() {
			private int threadNr=0;				
			@Override
			public Thread newThread(Runnable r) {
				String threadName=MODEL_EXECUTOR+"-Lifecycle-Fallback-"+threadNr++;
				logger.fine("Creating Thread: "+threadName);
				
				Thread newThread=Executors.defaultThreadFactory().newThread(r);
				newThread.setName(threadName);
				return newThread;					
			}
		});
		
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
	 * Executes the given Runnable either in the modelExecutorLifecycle thread (single threaded approach) or in a thread pool of userdefined size.
	 * @param r
	 */
	public void execute(Runnable r) {
		pool.execute(r);
	}

	/**
	 * Submits the given Callable and waits until completion either in the modelExecutorLifecycle thread (single threaded approach) or in a thread pool of userdefined size.
	 * @param r
	 */	
	public <V> V submit(Callable<V> r) throws InterruptedException, ExecutionException, TimeoutException {
		Future<V> f=pool.submit(r);
		try{
			return f.get(TASK_SUBMIT_TIMEOUT,TimeUnit.MILLISECONDS);
		}catch(TimeoutException e) {
			logger.warning("["+Thread.currentThread()+"]: Task execution timeouted, trying to cancel task");
			if(f!=null) {
				f.cancel(true);
			}
			throw(e);
		}		
	}

	/**
	 * Executes (waits for termination) the given Runnable by the Thread instance "ModelExecutor".
	 * If the execution hangs a timeout arises after TASK_SUBMIT_TIMEOUT. In such a case a fallback thread is used to execute the Runnable.
	 * @param r
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException 
	 */
	public void execAndWaitOnModelExecutorLifecycleThread(Runnable r) throws InterruptedException,
			ExecutionException, TimeoutException {
		
		//AstericsErrorHandling.instance.getLogger().fine("Current Thread: "+Thread.currentThread().getName()+", AREMain: "+ARE_MAIN);
		if(MODEL_EXECUTOR.equals(Thread.currentThread().getName())) {
			//We are already executed by the AREMain Thread so just call the Runnable.run() method
			r.run();
		} else {
			//execute with AREMainExecuter and wait for response "blocked execution"
			Future f=modelExecutorLifecycle.submit(r);
			try{				
				f.get(TASK_SUBMIT_TIMEOUT,TimeUnit.MILLISECONDS);
			}
			catch(TimeoutException e) {
				logger.warning("["+MODEL_EXECUTOR+"]: Task execution timeouted, using fallback thread pool");
			    fallbackPool.submit(r).get(TASK_SUBMIT_TIMEOUT,TimeUnit.MILLISECONDS);
			}			
		}
	}

	/**
	 * Returns the instance of the AREMain thread ExecutorService.
	 * @return
	 */
	public ExecutorService getModelExecutorLifecycleThread() {
		return modelExecutorLifecycle;
	}
}
