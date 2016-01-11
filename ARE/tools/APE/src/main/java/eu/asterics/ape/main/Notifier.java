package eu.asterics.ape.main;

import java.util.logging.Level;
import java.util.logging.Logger;

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * This class wraps logging and console outputs into single methods to make it easier to reuse output messages.
 * @author mad
 *
 */
public class Notifier {
	private static Logger logger=AstericsErrorHandling.instance.getLogger();

	public Notifier() {
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
		//System.out.println(message);
		logger.info(message);
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
