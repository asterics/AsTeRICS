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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
 * This thread pool is to perform diverse tasks, such as sending and receiving CIM data.
 * There is one special pool with size 1 for the execution of lifecycle tasks like starting, stopping modules. 
 * This one should also be used to interface hw to prevent multi-threading problems with not threadsafe libraries.  
 * @author mad
 *
 */
public class AstericsThreadPool {
	private static final String THREAD_POOL_SIZE = "ThreadPool.OtherTasks.size";
	private static final Logger logger=AstericsErrorHandling.instance.getLogger();
	
	private static final String ARE_MAIN = "AREMain";
	public static final AstericsThreadPool instance = new AstericsThreadPool();
	private ExecutorService pool;
	private ExecutorService AREMainExecutor = Executors
			.newSingleThreadExecutor(new ThreadFactory() {
				
				@Override
				public Thread newThread(Runnable arg0) {
					// TODO Auto-generated method stub
					return new Thread(arg0,ARE_MAIN);
				}
			});

	private AstericsThreadPool() {
		int poolSize=new Integer(AREProperties.instance.getProperty(THREAD_POOL_SIZE, "10"));
		logger.info("Creating ThreadPool.OtherTasks with size: "+poolSize);
		pool = Executors.newFixedThreadPool(poolSize);
		//pool = Executors.newCachedThreadPool();
		/*
		pool = new ThreadPoolExecutor(5, 20, 60,
		TimeUnit.SECONDS,
		new LinkedBlockingQueue<Runnable>(100));
		*/
	}

	public void execute(Runnable r) {
		pool.execute(r);
	}

	/**
	 * Executes the given Runnable by the Thread instance "AREMain", which is used to execute AREServices (start, stop models, ...). The method checks
	 * if it is already executed by AREMain. In such a case Runnable is called directly. The Runnable is executed in a blocking way.
	 * @param r
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void execAndWaitOnAREMainThread(Runnable r) throws InterruptedException,
			ExecutionException {
		//AstericsErrorHandling.instance.getLogger().fine("Current Thread: "+Thread.currentThread().getName()+", AREMain: "+ARE_MAIN);
		if(ARE_MAIN.equals(Thread.currentThread().getName())) {
//			AstericsErrorHandling.instance.getLogger().fine("already in AREMain Thread");
			//We are already executed by the AREMain Thread so just call the Runnable.run() method
			r.run();
		} else {
//			AstericsErrorHandling.instance.getLogger().fine("scheduling for AREMain Thread");
			//execute with AREMainExecuter and wait for response "blocked execution"
			AREMainExecutor.submit(r).get();
		}
	}

	/**
	 * Returns the instance of the AREMain thread ExecutorService.
	 * @return
	 */
	public ExecutorService getAREMainExecutor() {
		return AREMainExecutor;
	}
}
