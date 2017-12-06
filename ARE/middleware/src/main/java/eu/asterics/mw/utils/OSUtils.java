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

package eu.asterics.mw.utils;

/**
 * Helper class to find OS ARE is running on.
 * 
 * @author Martin Deinhofer [deinhofe@technikum-wien.at] Date: May 28, 2015
 */
public class OSUtils {
    public static final String LINUX = "linux";
    public static final String MACOSX = "macosx";
    public static final String WINDOWS = "windows";
    private static String OS = System.getProperty("os.name").toLowerCase();

    /**
     * Is the ARE running on any windows?
     * 
     * @return true: yes
     */
    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    /**
     * Is the ARE running on an Mac OS X?
     * 
     * @return true: yes
     */
    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    /**
     * Is the ARE runningn on Linux or Unix?
     * 
     * @return true: yes
     */
    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
    }

    /**
     * Returns the operating system name according to AsTeRICS convention.
     * 
     * @return
     */
    public static String getOsName() {
        if (isWindows()) {
            return WINDOWS;
        } else if (isMac()) {
            return MACOSX;
        } else if (isUnix()) {
            return LINUX;
        }
        return "unknown";
    }
}
