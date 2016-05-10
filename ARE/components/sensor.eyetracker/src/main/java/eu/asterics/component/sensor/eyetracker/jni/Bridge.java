
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
 *    This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */


package eu.asterics.component.sensor.eyetracker.jni; 

import eu.asterics.component.sensor.eyetracker.EyetrackerInstance;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 *   Java JNI bridge for interfacing C++ code for the facetracker_lk plugin
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: Mar 1, 2011
 *         Time: 3:35:00 PM
 */
public class Bridge                          
{   
    /** 
     * Statically load the native library 
     */
    static   
    {   
    	//Same for both versions
        System.loadLibrary("tbb");
        AstericsErrorHandling.instance.getLogger().fine("Loading \"tbb.dll\" ... ok!");
        
        System.loadLibrary("eyetracker");
        AstericsErrorHandling.instance.getLogger().fine("Loading \"eyetracker.dll\" ... ok!");
        
    }
 
 //   private final EyetrackerInstance.OutputPort x;
 //   private final EyetrackerInstance.OutputPort y;
    private final EyetrackerInstance owner;
    
    public Bridge(final EyetrackerInstance owner)
    {
        this.owner = owner;
    } 
  
    /**
     * Activates the underlying native code/hardware.
     *
     * @return 0 if everything was OK, a negative number corresponding to an
     * error code otherwise
     */  
    native public int activate();   

    /**
     * Deactivates the underlying native code/hardware.
     *
     * @return 0 if everything was OK, a negative number corresponding to an
     * error code otherwise
     */
    native public int deactivate();

    /**
     * Gets the value of the named property.
     *
     * @param key the name of the property to be accessed
     * @return the value of the named property
     */
    native public String getProperty(String key);

    /**
     * Sets the named property to the defined value.
     *
     * @param key the name of the property to be accessed
     * @param value the value to be assigned to the named property
     * @return the value previously assigned to the named property
     */
    native public String setProperty(String key, final String value);


    native public void calibrate();
    native public void showCameraSettings();
    native public void setDisplayPosition(int x, int y, int w, int h);
    native public void saveCameraProfile(String filename);   
    native public void loadCameraProfile(String filename);

    
    /**
     * This method is called back from the native code on demand to signify an
     * internal error. The first argument corresponds to an error code and the
     * second argument corresponds to a textual description of the error.
     *
     * @param errorCode an error code
     * @param message a textual description of the error
     */ 
    private void errorReport_callback(
            final int errorCode,
            final String message)
    {
    	AstericsErrorHandling.instance.getLogger().warning(errorCode + ": " + message);
    }
  
    /**
     * This method is called back from the native code on demand. The passed
     * arguments correspond to x/y position of the tracked round object (e.g. the pupil)
     *
     * @param x_location the x eye location (range is [0, Short.MAX_VALUE])
     * @param y_location the y eye location (range is [0, Short.MAX_VALUE])
     */
    synchronized private void newCoordinates_callback(final int x_location, final int y_location)
    {
    	owner.eyeLocationCallback (x_location,y_location); 
//    		x.sendData(x_location); 
//    		y.sendData(y_location);        
//    	} 
    }
}