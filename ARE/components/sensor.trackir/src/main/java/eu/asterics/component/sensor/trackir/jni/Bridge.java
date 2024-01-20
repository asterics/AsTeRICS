
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

package eu.asterics.component.sensor.trackir.jni;

import eu.asterics.component.sensor.trackir.TrackIRInstance;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Java JNI brdige for interfacing C++ code for the trackir plugin
 * 
 * @author Chris Veigl [veigl@technikum-wien.at] Date: Mar 1, 2011 Time: 3:35:00
 *         PM
 */
public class Bridge extends AbstractBridge {
    /**
     * Statically load the native library
     */
    static {
        System.loadLibrary("trackirbridge");
        AstericsErrorHandling.instance.getLogger().fine("Loading \"trackirbridge.dll\" ... ok!");
    }

    public Bridge(final TrackIRInstance.OutputPort opYaw, final TrackIRInstance.OutputPort opPitch,final TrackIRInstance.OutputPort opRoll, 
				  final TrackIRInstance.OutputPort opX, final TrackIRInstance.OutputPort opY,final TrackIRInstance.OutputPort opZ) {
    	AstericsErrorHandling.instance.getLogger().fine("Initializing native windows Bridge for TrackIR capturing...");
    	
		// get the output port instances
        this.opYaw = opYaw;
        this.opPitch = opPitch;
        this.opRoll = opRoll;
        this.opX = opX;
        this.opY = opY;
        this.opZ = opZ;
    }

    /**
     * Activates the underlying native code/hardware.
     *
     * @return 0 if everything was OK, a negative number corresponding to an
     *         error code otherwise
     */
    native public int activate();

    /**
     * Deactivates the underlying native code/hardware.
     *
     * @return 0 if everything was OK, a negative number corresponding to an
     *         error code otherwise
     */
    native public int deactivate();

    /**
     * request an update of tracking data
     *
     * @return 0 if everything was OK, error code otherwise
     */
    native public int getUpdate();

    /**
     * request centering coordinates
     *
     * @return 0 if everything was OK, error code otherwise
     */
    native public int centerCoordinates();
		
}