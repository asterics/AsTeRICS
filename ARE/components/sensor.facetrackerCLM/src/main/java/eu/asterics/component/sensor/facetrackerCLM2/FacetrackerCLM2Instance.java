

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
 *       This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.sensor.facetrackerCLM2;

import java.awt.Dimension;
import java.awt.Point;

import eu.asterics.component.sensor.facetrackerCLM2.jni.*;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsModelExecutionThreadPool;

/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 *  
 * @author <Andrea Carbone> [<carbone@isir.upmc.fr>]
 *         Date: 
 *         Time: 
 */
public class FacetrackerCLM2Instance extends AbstractRuntimeComponentInstance
{
	// Roll, Pitch, Yaw
	final IRuntimeOutputPort opRoll	= new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opPitch = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opYaw = new DefaultRuntimeOutputPort();
	// Pos
	final IRuntimeOutputPort opPosX = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opPosY = new DefaultRuntimeOutputPort();
	//Scale
	final IRuntimeOutputPort opScale = new DefaultRuntimeOutputPort();
	
	final IRuntimeOutputPort opEyeLeft = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opEyeRight = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	public final IRuntimeEventTriggererPort etpEyebrowsRaised = 
		new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();
	
//	final IRuntimeEventTriggererPort etpEtpEyebrowsRaised =
//			new DefaultRuntimeEventTriggererPort();
	
	
	int propCameraIndex = 0;
	int propCameraRes =1;
	String modelName;

	// declare member variables here
	//private  GUI gui = null;
 
	// Native runtime
	private final FacetrackerCLM2Bridge bridge =
		new FacetrackerCLM2Bridge(this);
    
   /**
    * The class constructor.
    */  
    public FacetrackerCLM2Instance()
    {
        // empty constructor
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
		if ("Roll".equalsIgnoreCase(portID))
		{
			return opRoll;
		}   	
		if ("Pitch".equalsIgnoreCase(portID))
		{
			return opPitch;
		}
		if ("Yaw".equalsIgnoreCase(portID))
		{
			return opYaw;
		}
		if ("PosX".equalsIgnoreCase(portID))
		{
			return opPosX;
		}
		if ("PosY".equalsIgnoreCase(portID))
		{
			return opPosY;
		}
		if ("Scale".equalsIgnoreCase(portID))
		{
			return opScale;
		}
		if ("EyeLeft".equalsIgnoreCase(portID))
		{
			return opEyeLeft;
		}
		if ("EyeRight".equalsIgnoreCase(portID))
		{
			return opEyeRight;
		}
		return null;
	}

    /**
     * returns an Event Listener Port.
     * @param eventPortID   the name of the port
     * @return         the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
		if ("reset".equalsIgnoreCase(eventPortID))
		{
			return elpReset;
		}
        if("showCameraSettings".equalsIgnoreCase(eventPortID))
        {
            return elpShowCameraSettings;
        } 
        if("setReferencePose".equalsIgnoreCase(eventPortID))
        {
            return elpSetReferencePose;
        }         
        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
		if ("EyebrowsRaised".equalsIgnoreCase(eventPortID))
		{
			return etpEyebrowsRaised;
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
		if ("cameraSelection".equalsIgnoreCase(propertyName))
		{
			return bridge.getProperty(propertyName);
		}
		else if ("cameraResolution".equalsIgnoreCase(propertyName))
		{
			return bridge.getProperty(propertyName);
		}
		else if ("modelName".equalsIgnoreCase(propertyName))
		{
			return bridge.getProperty(propertyName);
		}
		else if ("cameraDisplayUpdate".equalsIgnoreCase(propertyName))
		{
			return bridge.getProperty(propertyName);
		}
        return null;
    }

    /**
     * sets a new value for the given property.
     * @param propertyName   the name of the property
     * @param newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
    	
		if ("cameraSelection".equalsIgnoreCase(propertyName))
		{
			propCameraIndex = Integer.parseInt(newValue.toString());
			final Object oldValue=bridge.setProperty(propertyName, newValue.toString());
			return oldValue;
		}
		
		if ("cameraResolution".equalsIgnoreCase(propertyName))
		{
			propCameraRes = Integer.parseInt(newValue.toString());
			final Object oldValue=bridge.setProperty(propertyName, newValue.toString());
			return oldValue;
		}

		if ("cameraDisplayUpdate".equalsIgnoreCase(propertyName))
		{
			//final Object oldValue = propCameraIndex;
			propCameraRes = Integer.parseInt(newValue.toString());
			final Object oldValue=bridge.setProperty(propertyName, newValue.toString());
			return oldValue;
		}
		if ("modelName".equalsIgnoreCase(propertyName))
		{
			//final Object oldValue = propCameraIndex;
			modelName = newValue.toString();// Integer.parseInt(newValue.toString());
			final Object oldValue=bridge.setProperty(propertyName, newValue.toString());
			return oldValue;
		}		
        return null;
    }

     /**
      * Input Ports for receiving values.
      */


	/**
	 * Event Listener Ports.
	 */
	final IRuntimeEventListenerPort elpReset = new IRuntimeEventListenerPort()
	{
		@Override 
		public void receiveEvent(final String data)
		{
			// insert event handling here 
			bridge.reset();
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
	 * Event Listener Port for Camera Settings Window.
	 */
    final IRuntimeEventListenerPort elpSetReferencePose 	= new IRuntimeEventListenerPort()
    {
    	@Override 
    	public void receiveEvent(String data)
    	 {
    		bridge.setReferencePose();
    	 }
    };
    
    public void newValuesCallback(
			final double roll  
		, 	final double pitch
		, 	final double yaw
		,	final double posx
		,	final double posy
		,	final double scale
		,	final int eyeLeftState
		,	final int eyeRightState)
    {
    	// TODO Auto-generated method stub
    	opRoll.sendData(ConversionUtils.doubleToBytes(roll));
    	opPitch.sendData(ConversionUtils.doubleToBytes(pitch));
    	opYaw.sendData(ConversionUtils.doubleToBytes(yaw));

    	opPosX.sendData(ConversionUtils.doubleToBytes(posx));
    	opPosY.sendData(ConversionUtils.doubleToBytes(posy));

    	opScale.sendData(ConversionUtils.doubleToBytes(scale));

    	opEyeLeft.sendData(ConversionUtils.intToBytes(eyeLeftState));
    	opEyeRight.sendData(ConversionUtils.intToBytes(eyeRightState));
    }
// 
//    /**
//     * This method is called back from the native code on demand to signify an
//     * internal error. The first argument corresponds to an error code and the
//     * second argument corresponds to a textual description of the error.
//     *
//     * @param level an error code
//     * @param message a textual description of the error
//     */
//    private void report_callback(
//            final int level,
//            final String message)
//    { 
//    	switch (level) {
//    		case 0:     	AstericsErrorHandling.instance.getLogger().fine(message); break;
//    		case 1:     	AstericsErrorHandling.instance.getLogger().warning(message); break;
//    		case 2:     	AstericsErrorHandling.instance.getLogger().severe(message); break;
//    	}
//    }
    

    
     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  AstericsErrorHandling.instance.reportInfo(this, "CLM Instance::start()");
			//gui = new GUI(this,AREServices.instance.getAvailableSpace(this));
			//AREServices.instance.displayPanel(gui, this, true);
  		if (bridge.activate() == 0)
			AstericsErrorHandling.instance.reportError(this, "Could not init CLM Facetracker");
		else 
		{
			AstericsErrorHandling.instance.reportInfo(this, "CLM Facetracker activated");
  			Point pos = AREServices.instance.getComponentPosition(this);
  			Dimension d = AREServices.instance.getAvailableSpace(this);
		   // System.out.println("LK window position:"+ pos.x +"/"+ pos.y+" Size:"+d.width+"/"+d.height);  
		   bridge.setDisplayPosition(pos.x,pos.y,d.width,d.height);
		}

		super.start();
		
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
    	  bridge.suspend();
          super.pause();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
    	  bridge.resume();
          super.resume();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
    	  bridge.deactivate();
			//AREServices.instance.displayPanel(gui, this, false);
          super.stop();
      }
}