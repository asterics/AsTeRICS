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

import java.net.URI;
import java.util.Properties;

/**
 * This class provides property keys and manages the property values of a
 * running APE instance. It can be instantiated with an instance of a Property
 * object providing default values for each property.
 * 
 * @author mad
 *
 */
public class APEProperties extends Properties {
    public static final String P_APE_BASE_URI = "APE.baseURI";
    public static final String P_ARE_BASE_URI = "ARE.baseURI";
    public static final String P_APE_MODELS = "APE.models";
    public static final String P_APE_PROJECT_DIR = "APE.projectDir";
    public static final String P_APE_BUILD_DIR = "APE.buildDir";
    public static final String P_APE_DATA_COPY_MODE = "APE.dataCopyMode";
    public static final String P_APE_WEB_COPY_MODE = "APE.webCopyMode";
    public static final String P_APE_LOG_LEVEL = "APE.logLevel";
    public static final String P_APE_BUILD_MODE = "APE.buildMode";    

    public static final String APE_PROP_PREFIX = "APE.";
    public static final String ARE_PROP_PREFIX = "ARE.";
    public static final String FX_PROP_PREFIX = "fx.";

    public static String DEFAULT_PROJECT_DIR = "defProjectDir/";
    public static String DEFAULT_BUILD_DIR = "build/";
    public static String DEFAULT_APE_LOG_LEVEL = "INFO";
    public static APE_BUILD_MODE DEFAULT_APE_BUILD_MODE = APE_BUILD_MODE.DEVEL;
    public static APE_WEB_COPY_MODE DEFAULT_WEB_COPY_MODE=APE_WEB_COPY_MODE.ALL;

    public static URI APE_BASE_URI = null;
    // public static URI APE_PROJECT_DIR_URI=null;
    // public static URI APE_PROP_FILE_BASE_URI=null;

    /**
     * Allowed modes for property {@link APEProperties#P_APE_DATA_COPY_MODE}
     * 
     * @author mad
     *
     */
    public enum APE_DATA_COPY_MODE {
        ALL, FOLDER, SINGLE, NONE
    }
    
    /**
     * Allowed build modes.
     * 
     * devel: Optimization of the workflow for development: no clean of build; only copies jars, if build/merged folder is empty; if copying jar, copy all jars instead of just referenced ones.
     * release: clean, APE-copy with referenced jars only
     * @author mad
     *
     */
    public enum APE_BUILD_MODE {
    	DEVEL, RELEASE
    }
    
    public enum APE_WEB_COPY_MODE {
        ALL,NONE
    }

    public APEProperties() {
    }

    public APEProperties(Properties defaults) {
        super(defaults);
    }

}
