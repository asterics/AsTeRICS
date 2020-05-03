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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.ResourceRegistry;
import eu.asterics.mw.services.ResourceRegistry.RES_TYPE;

public class AREProperties extends Properties {
    private static final String ARE_PROPERTIES_NAME = "areProperties";
    public static AREProperties instance = new AREProperties();
    static final String PROPERTY_FILENAME = ARE_PROPERTIES_NAME;
    private static Logger logger;
    private Map<String, String> propertyComments = new LinkedHashMap<String, String>();

    // Property key constants
    public static final String ARE_WEBSERVICE_PORT_REST_KEY = "ARE.webservice.port.REST";
    public static final String ARE_WEBSERVICE_PORT_WEBSOCKET_KEY = "ARE.webservice.port.websocket";
    public static final String ARE_WEBSERVICE_SSL_PORT_REST_KEY = "ARE.webservice.ssl.port.REST";
    public static final String ARE_WEBSERVICE_SSL_PORT_WEBSOCKET_KEY = "ARE.webservice.ssl.port.websocket";

    //Port conflict resolving
    public static final String ARE_PORT_CONFLICT_NR_TRIES_KEY = "ARE.port.conflict.nr.tries";
    public static final String ARE_PORT_CONFLICT_STEP_SIZE_KEY = "ARE.port.conflict.step.size";

    private AREProperties() {
        logger = AstericsErrorHandling.instance.getLogger();
        try {
            load(ResourceRegistry.getInstance().getResourceInputStream(ARE_PROPERTIES_NAME, RES_TYPE.ANY));
        } catch (MalformedURLException | URISyntaxException e) {
            logger.logp(Level.SEVERE, this.getClass().getName(), "AREProperties", "Could not load properties file: " + e.getMessage(), e);
        } catch (IOException e) {
            logger.fine("Properties file does not exist, using default values");
        }
    }

    /**
     * Returns the current property value and sets it to the default value if it was not contained in the Properties object before. This is done to ensure that
     * it will be saved to a file with {@see AREProperties#storeProperties()}
     * 
     * @see java.util.Properties#getProperty(java.lang.String, java.lang.String)
     */
    @Override
    public synchronized String getProperty(String key, String defaultValue) {
        String propValue = super.getProperty(key, defaultValue);
        // Store back current property value to ensure that it will be saved to a file with storeProperties.
        setProperty(key, propValue);
        return propValue;
    }

    /**
     * This method saves the given default value, if the property was not set. Additionally, the given propertyComment is registered for being stored right
     * before the property in the {{@link #PROPERTY_FILENAME} file when {{@link #storeProperties()} is called.
     *
     * @param key
     * @param defaultValue
     * @param propertyComment
     * @return
     */
    public void setDefaultPropertyValue(String key, String defaultValue, String propertyComment) {
        propertyComments.put(key, propertyComment);
        getProperty(key, defaultValue);
    }

    /**
     * This method saves the given default value, if the property was not set.
     *
     * @param key
     * @param defaultValue
     * @return
     */
    /*
     * public void setDefaultPropertyValue(String key, String defaultValue) { getProperty(key, defaultValue); }
     */

    /**
     * Saves the properties of this instance to the file {@see AREProperties#PROPERTY_FILENAME}.
     */
    public void storeProperties() {
        StringBuilder propertiesStringBuilder = new StringBuilder();
        propertiesStringBuilder.append("# ARE properties, generated at " + new Date(System.currentTimeMillis()) + "\n\n");

        // First add properties with comment with insertion order using the commentsMap (LinkedHashMap).
        for (Map.Entry<String, String> entry : propertyComments.entrySet()) {
            String key = entry.getKey();
            propertiesStringBuilder.append("# " + entry.getValue());
            propertiesStringBuilder.append("\n");

            propertiesStringBuilder.append(key + "=" + getProperty(key));
            propertiesStringBuilder.append("\n\n");
        }

        // Now add properties without comment which were not printed yet.
        propertiesStringBuilder.append("\n");
        Enumeration<?> propertiesNames = propertyNames();
        while (propertiesNames.hasMoreElements()) {
            String key = (String) propertiesNames.nextElement();
            if (!propertyComments.containsKey(key)) {
                propertiesStringBuilder.append(key + "=" + getProperty(key));
                propertiesStringBuilder.append("\n");
            }
        }

        try {
            ResourceRegistry.getInstance().storeResource(propertiesStringBuilder.toString(), ARE_PROPERTIES_NAME, RES_TYPE.ANY);
        } catch (URISyntaxException | IOException e) {
            logger.logp(Level.SEVERE, this.getClass().getName(), "storeProperties", "Could not store properties file: " + e.getMessage(), e);
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
