package eu.asterics.mw.are;

import java.util.logging.Logger;

import eu.asterics.mw.model.runtime.IRuntimeComponentInstance;
import eu.asterics.mw.services.AstericsErrorHandling;

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

public class DefaultComponentFactory {
    private Class clazz;
    // private boolean isSingleton;
    private Logger logger = null;
    // private IRuntimeComponentInstance cachedInstance;

    /**
     * Factory that creates all the components of a specified AsTeRICS plugin.
     * 
     * @param clazz
     *            the class to be created
     * @param isSingleton
     *            obsolete definition whether factory should create a singleton
     *            or not. Factory now always creates new instances of class,
     *            singleton components are checked by the model creation process
     *            in ARE.
     */
    public DefaultComponentFactory(Class clazz, boolean isSingleton) {
        logger = AstericsErrorHandling.instance.getLogger();
        this.clazz = clazz;
        // this.isSingleton = isSingleton;
        /*
         * try{ cachedInstance = (IRuntimeComponentInstance)
         * clazz.newInstance(); }catch (InstantiationException ie) {
         * logger.warning(this.getClass().getName()+".DefaultComponentFactory:"
         * +" Could not instantiate object -> \n" +ie.getMessage()); throw new
         * RuntimeException(ie); } catch (IllegalAccessException iae) {
         * logger.warning(this.getClass().getName()+".DefaultComponentFactory:"
         * +" Could not instantiate object -> \n" +iae.getMessage()); throw new
         * RuntimeException(iae); }
         */
    }

    /**
     * Generates a new instance of a component
     * 
     * @return the instance of the component
     */
    public IRuntimeComponentInstance getInstance() {
        /*
         * if (isSingleton) return this.cachedInstance; else
         */
        try {
            return (IRuntimeComponentInstance) clazz.newInstance();
        } catch (InstantiationException ie) {
            logger.warning(this.getClass().getName() + ".getInsance: Could not instantiate " + "object -> \n"
                    + ie.getMessage());
            throw new RuntimeException(ie);
        } catch (IllegalAccessException iae) {
            logger.warning(this.getClass().getName() + ".getInsance: Could not instantiate " + "object -> \n"
                    + iae.getMessage());
            throw new RuntimeException(iae);
        }
    }

}
