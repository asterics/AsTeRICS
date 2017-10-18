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

package eu.asterics.mw.are;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.asterics.mw.services.AstericsErrorHandling;

public class AREProperties extends Properties {
    public static AREProperties instance = new AREProperties();
    static final String PROPERTY_FILENAME = "areProperties";
    private static Logger logger;

    private AREProperties() {
        logger = AstericsErrorHandling.instance.getLogger();
        try (FileInputStream in = new FileInputStream(PROPERTY_FILENAME);) {
            load(in);
        } catch (IOException e) {
            logger.info("The file " + PROPERTY_FILENAME + " does not exist, it will be generated automatically.");
        }
    }

    /**
     * Returns the current property value and sets it to the default value if it was not contained in the Properties object before. This is done to ensure that
     * it will be saved to a file with {@see AREProperties#storeProperties()}
     * 
     * @see java.util.Properties#getProperty(java.lang.String, java.lang.String)
     */
    @Override
    public String getProperty(String key, String defaultValue) {
        String propValue = super.getProperty(key, defaultValue);
        // Store back current property value to ensure that it will be saved to a file with storeProperties.
        setProperty(key, propValue);
        return propValue;
    }

    /**
     * Saves the properties of this instance to the file {@see AREProperties#PROPERTY_FILENAME}.
     */
    public void storeProperties() {
        try (FileOutputStream out = new FileOutputStream(PROPERTY_FILENAME);) {
            store(out, "ARE Properties");
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Checks if the given property key has the given expectedValue.
     * 
     * @param key
     * @param expectedValue
     * @return
     */
    public boolean checkProperty(String key, String expectedValue) {
        if (containsKey(key)) {
            return getProperty(key).equals(expectedValue);
        }
        return false;
    }
}
