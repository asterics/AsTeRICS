
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
 
package eu.asterics.component.sensor.wiimote.jni;

import eu.asterics.component.sensor.wiimote.WiiMoteInstance;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.data.ConversionUtils;

/**
 *   Java JNI bridge for interfacing C++ code for the WiiMote plugin
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: Oct 25, 2011
 *         Time: 8:35:00 PM
 */  
public class Bridge
{
    /**
     * Statically load the native library
     */
    static   
    {   
        System.loadLibrary("WiiInterface");
    	AstericsErrorHandling.instance.getLogger().fine("Loading \"WiiInterface.dll\" for Wiimote... ok!");
   
    }
  
    private final WiiMoteInstance owner;
    private int buttonstate=0;
 
    public Bridge(final WiiMoteInstance owner)
    {
        this.owner= owner;
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
    	AstericsErrorHandling.instance.getLogger().fine(errorCode + ": " + message);
    } 
     
  
    /**
     * This method is called back from the native code on demand. The passed
     * arguments correspond to the current joystick values
     *
     * @param pitch (integer)
     * @param roll (integer)
     * @param point1x (integer)
     * @param point1y(integer)
     * @param point2x (integer)
     * @param point2y(integer)
     * @param nunx (integer)
     * @param nuny (integer)
     * @param battery  (integer)
     * @param buttons (integer)
     */
    private void newValues_callback( int pitch, int roll, int point1x, int point1y, int point2x, int point2y,
    		 int nunx,  int nuny, int battery, int buttons)
    {     	
    	owner.opPitch.sendData(ConversionUtils.doubleToBytes((double)pitch));       
    	owner.opRoll.sendData(ConversionUtils.doubleToBytes((double)roll));       
    	owner.opPoint1X.sendData(ConversionUtils.doubleToBytes((double)point1x));       
    	owner.opPoint1Y.sendData(ConversionUtils.doubleToBytes((double)point1y));       
    	owner.opPoint2X.sendData(ConversionUtils.doubleToBytes((double)point2x));       
    	owner.opPoint2Y.sendData(ConversionUtils.doubleToBytes((double)point2y));       
    	owner.opNunX.sendData(ConversionUtils.doubleToBytes((double)nunx));       
    	owner.opNunY.sendData(ConversionUtils.doubleToBytes((double)nuny));       
    	owner.opBattery.sendData(ConversionUtils.intToBytes(battery));       
    	  
    	if (buttonstate!=buttons)
    	{        		 
    	    if (((buttonstate & 0x08) == 0) && ((buttons & 0x08) != 0)) 
	    	       owner.etpPressedUp.raiseEvent();
    	    else if (((buttonstate & 0x08) != 0) && ((buttons & 0x08) == 0)) 
	 	           owner.etpReleasedUp.raiseEvent();
    	    if (((buttonstate & 0x04) == 0) && ((buttons & 0x04) != 0)) 
	    	       owner.etpPressedDown.raiseEvent();
    	    else if (((buttonstate & 0x04) != 0) && ((buttons & 0x04) == 0)) 
	 	           owner.etpReleasedDown.raiseEvent();
    	    if (((buttonstate & 0x01) == 0) && ((buttons & 0x01) != 0)) 
	    	       owner.etpPressedLeft.raiseEvent();
    	    else if (((buttonstate & 0x01) != 0) && ((buttons & 0x01) == 0)) 
	 	           owner.etpReleasedLeft.raiseEvent();
    	    if (((buttonstate & 0x02) == 0) && ((buttons & 0x02) != 0)) 
	    	       owner.etpPressedRight.raiseEvent();
    	    else if (((buttonstate & 0x02) != 0) && ((buttons & 0x02) == 0)) 
	 	           owner.etpReleasedRight.raiseEvent();
    	    if (((buttonstate & 0x0800) == 0) && ((buttons & 0x0800) != 0)) 
	    	       owner.etpPressedA.raiseEvent();
    	    else if (((buttonstate & 0x0800) != 0) && ((buttons & 0x0800) == 0)) 
	 	           owner.etpReleasedA.raiseEvent();
    	    if (((buttonstate & 0x0400) == 0) && ((buttons & 0x0400) != 0)) 
	    	       owner.etpPressedB.raiseEvent();
    	    else if (((buttonstate & 0x0400) != 0) && ((buttons & 0x0400) == 0)) 
	 	           owner.etpReleasedB.raiseEvent();
    	    if (((buttonstate & 0x0200) == 0) && ((buttons & 0x0200) != 0)) 
	    	       owner.etpPressed1.raiseEvent();
    	    else if (((buttonstate & 0x0200) != 0) && ((buttons & 0x0200) == 0)) 
	 	           owner.etpReleased1.raiseEvent();
    	    if (((buttonstate & 0x0100) == 0) && ((buttons & 0x0100) != 0)) 
	    	       owner.etpPressed2.raiseEvent();
    	    else if (((buttonstate & 0x0100) != 0) && ((buttons & 0x0100) == 0)) 
	 	           owner.etpReleased2.raiseEvent();
    	    if (((buttonstate & 0x0010) == 0) && ((buttons & 0x0010) != 0)) 
	    	       owner.etpPressedPlus.raiseEvent();
    	    else if (((buttonstate & 0x0010) != 0) && ((buttons & 0x0010) == 0)) 
	 	           owner.etpReleasedPlus.raiseEvent();
    	    if (((buttonstate & 0x1000) == 0) && ((buttons & 0x1000) != 0)) 
	    	       owner.etpPressedMinus.raiseEvent();
    	    else if (((buttonstate & 0x1000) != 0) && ((buttons & 0x1000) == 0)) 
	 	           owner.etpReleasedMinus.raiseEvent();
    	    if (((buttonstate & 0x8000) == 0) && ((buttons & 0x8000) != 0)) 
	    	       owner.etpPressedHome.raiseEvent();
    	    else if (((buttonstate & 0x8000) != 0) && ((buttons & 0x8000) == 0)) 
	 	           owner.etpReleasedHome.raiseEvent();
    	    if (((buttonstate & 0x10000) == 0) && ((buttons & 0x10000) != 0)) 
	    	       owner.etpPressedNunchuckC.raiseEvent();
    	    else if (((buttonstate & 0x10000) != 0) && ((buttons & 0x10000) == 0)) 
	 	           owner.etpReleasedNunchuckC.raiseEvent();
    	    if (((buttonstate & 0x20000) == 0) && ((buttons & 0x20000) != 0)) 
	    	       owner.etpPressedNunchuckZ.raiseEvent();
    	    else if (((buttonstate & 0x20000) != 0) && ((buttons & 0x20000) == 0)) 
	 	           owner.etpReleasedNunchuckZ.raiseEvent();

    	
    	   	buttonstate=buttons; 
    	}
    	 
    } 
   
   
  
}