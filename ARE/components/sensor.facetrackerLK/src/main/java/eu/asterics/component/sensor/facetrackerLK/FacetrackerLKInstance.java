
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

 
package eu.asterics.component.sensor.facetrackerLK; 


import java.awt.Dimension;
import java.awt.Point;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.component.sensor.facetrackerLK.jni.Bridge;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 *   Implements the facetracker_lk plugin, which uses OpenCV 
 *   and a combination of the HaarCascade detection and the LukasKanade
 *   Optical Flow tracking to deliver nose- and chin position changes
 *   at the output ports
 *  
 *   This plugin uses a JNI bridge to the OpenCV tracking routines.
 *   
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: Oct 10, 2010
 *         Time: 2:35:00 PM 
 */
public class FacetrackerLKInstance extends AbstractRuntimeComponentInstance
{
    private final OutputPort opNoseX = new OutputPort();
    private final OutputPort opNoseY = new OutputPort();
    private final OutputPort opChinX = new OutputPort();
    private final OutputPort opChinY = new OutputPort();
  
    String propCameraProfile ="";

    
    private final Bridge bridge = new Bridge(opNoseX, opNoseY, opChinX, opChinY);
 
	/**
	 * The class constructor.
	 */
   public FacetrackerLKInstance()
    {
        // empty constructor - needed for OSGi service factory operations
    }
        
	/**
	 * returns an Input Port.
	 * @param portID   the name of the port
	 * @return         the input port or null if not found
	 */
   public IRuntimeInputPort getInputPort(String portID)
    {
        return null; 
    }
   
	/**
	 * returns an Output Port.
	 * @param portID   the name of the port
	 * @return         the output port or null if not found
	 */	 
    public IRuntimeOutputPort getOutputPort(String portID)
    {
        if("noseX".equalsIgnoreCase(portID))
        {
            return opNoseX;
        }
        else if("noseY".equalsIgnoreCase(portID))
        {
            return opNoseY;
        }
        else if("chinX".equalsIgnoreCase(portID))
        {
            return opChinX;
        }
        else if("chinY".equalsIgnoreCase(portID))
        {
            return opChinY;
        }

        return null;
    }

	/**
	 * returns the value of the given property.
	 * @param propertyName   the name of the property
	 * @return               the property value or null if not found
	 */
    public Object getRuntimePropertyValue(String propertyName)
    {
        if("cameraProfile".equalsIgnoreCase(propertyName))
		{
			return propCameraProfile;
		}
        else return bridge.getProperty(propertyName);
    }
 
	/**
	 * sets a new value for the given property.
	 * @param propertyName   the name of the property
	 * @param newValue       the desired property value or null if not found
	 */
   public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
        if("cameraProfile".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propCameraProfile;
			propCameraProfile = (String)newValue;
			return oldValue;
		}
        else
        {
		    final String oldValue = bridge.getProperty(propertyName);
	        bridge.setProperty(propertyName, newValue.toString());
	        return oldValue;
        }
    }
   
	/**
	 * returns an Event Listener Port 
	 * @param enventPortID   the name of the event listener port
	 * @return       the event listener port or null if not found
	 */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {

        if("init".equalsIgnoreCase(eventPortID))
        {
            return initEvent;
        }        
        if("showCameraSettings".equalsIgnoreCase(eventPortID))
        {
            return elpShowCameraSettings;
        }       
        if("saveProfile".equalsIgnoreCase(eventPortID))
        {
            return elpSaveProfile;
        }                
        return null;
    }
    
	/**
	 * Output Ports for feature coordinates.
	 */
    public class OutputPort extends DefaultRuntimeOutputPort
    {
        public void sendData(int data)
        {
            super.sendData(ConversionUtils.intToByteArray(data));
        }
    }

	/**
	 * Event Listener Port for face position initialisation.
	 */
    final IRuntimeEventListenerPort initEvent 	= new IRuntimeEventListenerPort()
    {
    	@Override 
    	public void receiveEvent(String data)
    	 {
    		bridge.initFace();
    	 }
    };
 
	/**
	 * Event Listener Port for Camera Settings Window.
	 */
    final IRuntimeEventListenerPort elpShowCameraSettings 	= new IRuntimeEventListenerPort()
    {
    	@Override 
    	public void receiveEvent(String data)
    	 {
    		bridge.showCameraSettings();
    	 }
    };
    
	/**
	 * Event Listener Port for save profile.
	 */
    final IRuntimeEventListenerPort elpSaveProfile 	= new IRuntimeEventListenerPort()
    {
    	@Override 
    	public void receiveEvent(String data)
    	 {
    		if (propCameraProfile!="")
    		  bridge.saveCameraProfile(propCameraProfile+".yml");
    	 }
    };

    
    
    @Override
    public void start()
    {
        if (bridge.activate() == 0)
        {
     	    AstericsErrorHandling.instance.reportError(this, "Could not init Webcam");
        }
     	else
     	{
     	   AstericsErrorHandling.instance.reportDebugInfo(this, "Webcam Facetracker activated");
		   if (propCameraProfile != "")
			   bridge.loadCameraProfile(propCameraProfile+".yml");

     	   Point pos = AREServices.instance.getComponentPosition(this);
     	   Dimension d = AREServices.instance.getAvailableSpace(this);
		   // System.out.println("LK window position:"+ pos.x +"/"+ pos.y+" Size:"+d.width+"/"+d.height);  
		   bridge.setDisplayPosition(pos.x,pos.y,d.width,d.height);
     	} 
        super.start();
    }
 
   
    @Override
    public void pause()
    {
        bridge.deactivate();
        super.pause();
    }

    @Override
    public void resume()
    {
        if (bridge.activate() == 0)
      	   AstericsErrorHandling.instance.reportError(this, "Could not init Webcam");
      	else
      	   AstericsErrorHandling.instance.reportDebugInfo(this, "Webcam Facetracker activated");
        super.resume();
    }

    @Override
    public void stop()
    {
        bridge.deactivate();
        super.stop();
    }

}