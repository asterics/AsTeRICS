


package eu.asterics.component.sensor.eyetracker.jni;

import eu.asterics.component.sensor.eyetracker.EyetrackerInstance;
import eu.asterics.component.sensor.eyetracker.POSIT;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;

public class BridgePOSIT {	
	
	private EyetrackerInstance owner;
	public POSIT positObj;

    /**  
     * Statically load the native library
     */
    static   
    {   
    	//Same for both versions
        System.loadLibrary("tbb");
        AstericsErrorHandling.instance.getLogger().fine("Loading \"tbb.dll\" ... ok!");
        
        System.loadLibrary("posit");
        AstericsErrorHandling.instance.getLogger().fine("Loading \"posit.dll\" ... ok!");
        
    }
    
    public BridgePOSIT(final EyetrackerInstance owner)
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
     * This method sends the blob coordinates to the native C++ code for pose
     * estimation 
     * @param pt1x X-coordinate of point 1
     * @param pt1y Y-coordinate of point 1
     * @param pt2x X-coordinate of point 2
     * @param pt2y Y-coordinate of point 2
     * @param pt3x X-coordinate of point 3
     * @param pt3y Y-coordinate of point 3
     * @param pt4x X-coordinate of point 4
     * @param pt4y Y-coordinate of point 4
     * @return 
     */    
    public native int runPOSIT(int pt1x, int pt1y,
					    		int pt2x, int pt2y,
					    		int pt3x, int pt3y,
					    		int pt4x, int pt4y );
    
    
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
     * arguments correspond to x/y/z rotation of the tracked object (IR-LED array)
     *
     * @param x_rotation of the object (4-Byte float value, units: rad)
     * @param y_rotation of the object (4-Byte float value, units: rad)
     * @param z_rotation of the object (4-Byte float value, units: rad)
     */
    synchronized private void newRotationVector_callback(final float x_rotation, final float y_rotation, final float z_rotation)
    { 
    	//AstericsErrorHandling.instance.reportDebugInfo(this.owner, String.format("x: %f, y: %f, z: %f", x_rotation, y_rotation, z_rotation));
    	positObj.writeRvec(x_rotation, y_rotation, z_rotation);
    }
    
    /**
     * This method is called back from the native code on demand. The passed
     * arguments correspond to x/y/z translation of the tracked object (IR-LED array)
     *
     * @param x_translation of the object (4-Byte float value, units: mm)
     * @param y_translation of the object (4-Byte float value, units: mm)
     * @param z_translation of the object (4-Byte float value, units: mm)
     */
    synchronized private void newTranslationVector_callback(final float x_translation, final float y_translation, final float z_translation)
    {
    	//AstericsErrorHandling.instance.reportDebugInfo(this.owner, String.format("x: %f, y: %f, z: %f", x_translation, y_translation, z_translation));
    	positObj.writeTvec(x_translation, y_translation, z_translation);
    }
    
    /**
     * opens / closes the Information Window
     * @return
     */
    public native int togglePoseInfoWindow();
    
    /** 
     * starts the evaluation in full-screen
     * @param ResX i.e. 1920
     * @param ResY i.e. 1080
     * @return
     */
    public native int startEval(int ResX, int ResY);
	
    /**
     * callback from native code to stop sending the eye coordinates
     */
    synchronized private void stopSendEyeCoordinates_callback()
    {
    	positObj.stopSendEyeCoordinates();
    }
    
    /**
     * sends the eye coordinates to the native code
     * @param rawX w/o compensation
     * @param rawY w/o compensation
     * @param actx w/ compensation
     * @param acty w/ compensation
     * @return
     */
    public native int sendEvalParams(int rawX, int rawY, int actX, int actY);
    
    
}
