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

package eu.asterics.ape.main;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * This class wraps logging and console outputs into single methods to make it
 * easier to reuse output messages.
 * 
 * @author mad
 *
 */
public class Notifier {
    private static Logger logger = AstericsErrorHandling.instance.getLogger();

    public static void initLogger(String consoleLogLevel) {
        for (Handler handler : logger.getHandlers()) {
            if (handler instanceof ConsoleHandler) {
                if (consoleLogLevel != null && !"".equals(consoleLogLevel)) {
                    System.out.println("Setting logLevel to: " + consoleLogLevel);
                    handler.setLevel(Level.parse(consoleLogLevel));
                }
                handler.setFormatter(new SimpleFormatter() {

                    @Override
                    public synchronized String format(LogRecord record) {
                        return String.format("%1$s: %2$s\n", record.getLevel(), record.getMessage());
                    }

                });
            }
        }
    }

    public static void debug(String message, Exception e) {
        // System.out.println(message);
        if (e != null) {
            logger.log(Level.FINE, message, e);
        } else {
            logger.log(Level.FINE, message);
        }
    }

    public static void info(String message) {
        info(message, null);
    }

    public static void info(String message, Exception e) {
        // System.out.println(message);
        logger.info(message);
        if (e != null) {
            logger.info(e.getMessage());
        }
    }

    public static void warning(String message, Exception e) {
        // System.out.println(message);
        if (e != null) {
            logger.log(Level.WARNING, message, e);
        } else {
            logger.log(Level.WARNING, message);
        }
    }

    public static void error(String message, Exception e) {
        // System.err.println(message);
        if (e != null) {
            logger.log(Level.SEVERE, message, e);
        } else {
            logger.log(Level.SEVERE, message);
        }
    }

}
