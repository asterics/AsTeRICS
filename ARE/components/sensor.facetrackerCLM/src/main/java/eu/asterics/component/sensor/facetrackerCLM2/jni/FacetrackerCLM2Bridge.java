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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */
package eu.asterics.component.sensor.facetrackerCLM2.jni;

import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsModelExecutionThreadPool;
import eu.asterics.component.sensor.facetrackerCLM2.FacetrackerCLM2Instance;
/**
 * @author Andrea Carbone
 *
 */
public class FacetrackerCLM2Bridge {

	static 
	{
		AstericsErrorHandling.instance.getLogger().fine("Loading CLM DLLS !");
		System.loadLibrary("tbb");
		AstericsErrorHandling.instance.getLogger().fine("Loading \"tbb.dll\" ... ok!");
		System.loadLibrary("sensor.facetrackerCLM");
		AstericsErrorHandling.instance.getLogger().fine("Loading \"sensor.facetrackerCLM.dll\" ... ok!");
	}

	
	private final FacetrackerCLM2Instance owner;
	
	public FacetrackerCLM2Bridge(
			final FacetrackerCLM2Instance instance) {
		this.owner=instance;
	}

	native public int activate();

	native public int suspend();

	native public int resume();

	native public int deactivate();
	
	native public int showCameraSettings();
	
	native public int setReferencePose();
	
	native public void setDisplayPosition(int x, int y, int w, int h);
	
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
    
    native public void reset();
    

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
     * 
     */
    synchronized private void newValuesCallback(
    				final double roll  
    			, 	final double pitch
    			, 	final double yaw
    			,	final double posx
    			,	final double posy
    			,	final double scale
    			,	final int eyeLeftState
    			, 	final int eyeRightState)
        {
	    	AstericsModelExecutionThreadPool.instance.execute(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
		    		owner.newValuesCallback(roll, pitch, yaw, posx, posy, scale, eyeLeftState, eyeRightState);				
				}
			});
        }
    
    /**
     * 
     */
    synchronized public void raiseGestureSurpriseEvt()
    {	
    	//report_callback(0, "JNI >> etpEyebrowsRaised.raiseEvent();");
    	owner.etpEyebrowsRaised.raiseEvent();
    }
}
