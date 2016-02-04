package eu.asterics.ape.main;

import static eu.asterics.ape.main.APEProperties.DEFAULT_APE_LOG_LEVEL;
import static eu.asterics.ape.main.APEProperties.P_APE_LOG_LEVEL;

import java.util.logging.*;

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * This class wraps logging and console outputs into single methods to make it easier to reuse output messages.
 * @author mad
 *
 */
public class Notifier {
	private static Logger logger=AstericsErrorHandling.instance.getLogger();

	public static void initLogger(String consoleLogLevel) {
		for(Handler handler : logger.getHandlers()) {
			if(handler instanceof ConsoleHandler) {
				if(consoleLogLevel!=null && !"".equals(consoleLogLevel)) {
					System.out.println("Setting logLevel to: "+consoleLogLevel);
					handler.setLevel(Level.parse(consoleLogLevel));
				}
				handler.setFormatter(new SimpleFormatter() {

					@Override
					public synchronized String format(LogRecord record) {
						return String.format("%1$s\n",record.getMessage());
					}

					
				});
			}
		}
	}
	
	public static void debug(String message, Exception e) {
		//System.out.println(message);
		if(e!=null) {
			logger.log(Level.FINE,message,e);
		} else {
			logger.log(Level.FINE,message);
		}		
	}
	
	public static void info(String message) {
		info(message,null);
	}
	
	public static void info(String message, Exception e) {
		//System.out.println(message);
		logger.info(message);
		if(e!=null) {
			logger.info(e.getMessage());
		}
	}
	
	public static void warning(String message, Exception e) {
		//System.out.println(message);
		if(e!=null) {
			logger.log(Level.WARNING,message,e);
		} else {
			logger.log(Level.WARNING,message);
		}
	}
	
	public static void error(String message, Exception e) {
		//System.err.println(message);
		if(e!=null) {
			logger.log(Level.SEVERE,message,e);
		} else {
			logger.log(Level.SEVERE,message);
		}
	}
	
	
}
