
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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */


package eu.asterics.component.sensor.facetrackerLK.jni; 

import eu.asterics.component.sensor.facetrackerLK.FacetrackerLKInstance;
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
	private FacetrackerLKInstance owner = null;
	
    static        
    {  
        System.loadLibrary("facetrackerLK");
        AstericsErrorHandling.instance.getLogger().fine("Loading \"facetrackerLK.dll\" ... ok!");
        
        // using opencv231
        System.loadLibrary("tbb");
        AstericsErrorHandling.instance.getLogger().fine("Loading \"tbb.dll\" ... ok!");
    }
 
    public Bridge( FacetrackerLKInstance owner)
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

    native public void showCameraSettings();    
    native public void initFace();
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
    private void report_callback(
            final int level,
            final String message)
    { 
    	switch (level) {
    		case 0:     	AstericsErrorHandling.instance.getLogger().fine(message); break;
    		case 1:     	AstericsErrorHandling.instance.getLogger().warning(message); break;
    		case 2:     	AstericsErrorHandling.instance.getLogger().severe(message); break;
    	}
    }
  
    /**
     * This method is called back from the native code on demand. The passed
     * arguments correspond to the cartesian coordinates of two points,
     * point1 and point2.
     *
     * @param point1_x the x of point 1 (range is [0, Short.MAX_VALUE])
     * @param point1_y the y of point 1 (range is [0, Short.MAX_VALUE])
     * @param point2_x the x of point 2 (range is [0, Short.MAX_VALUE])
     * @param point2_y the y of point 2 (range is [0, Short.MAX_VALUE])
     */
    synchronized private void newCoordinates_callback(final int point1_x,
        final int point1_y, final int point2_x, final int point2_y)
    {
    	owner.newCoordinates_callback(point1_x, point1_y, point2_x, point2_y);
    }
}