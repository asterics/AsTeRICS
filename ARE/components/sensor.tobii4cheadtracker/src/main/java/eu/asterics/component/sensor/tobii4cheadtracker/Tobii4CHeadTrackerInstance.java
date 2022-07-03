

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

package eu.asterics.component.sensor.tobii4cheadtracker;


import java.util.logging.Logger;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;

/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 *  
 * @author <your name> [<your email address>]
 *         Date: 
 */
public class Tobii4CHeadTrackerInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opHeadRotX = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opHeadRotY = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opHeadRotZ = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opHeadPosX = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opHeadPosY = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opHeadPosZ = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpUserPresenceStatusAway = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpUserPresenceStatusPresent = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpCalibrationStarted = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpCalibrationFinished = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpDisplayAreaChanged = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpPowerSaveStateTrue = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpPowerSaveStateFalse = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpDeviceOff = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpDeviceOn = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	boolean propEnabled = true;

	// declare member variables here
	Bridge bridge=new Bridge(this);
  
    
   /**
    * The class constructor.
    */
    public Tobii4CHeadTrackerInstance()
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
		if ("headRotX".equalsIgnoreCase(portID))
		{
			return opHeadRotX;
		}
		if ("headRotY".equalsIgnoreCase(portID))
		{
			return opHeadRotY;
		}
		if ("headRotZ".equalsIgnoreCase(portID))
		{
			return opHeadRotZ;
		}
		if ("headPosX".equalsIgnoreCase(portID))
		{
			return opHeadPosX;
		}
		if ("headPosY".equalsIgnoreCase(portID))
		{
			return opHeadPosY;
		}
		if ("headPosZ".equalsIgnoreCase(portID))
		{
			return opHeadPosZ;
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
		if ("activate".equalsIgnoreCase(eventPortID))
		{
			return elpActivate;
		}
		if ("deactivate".equalsIgnoreCase(eventPortID))
		{
			return elpDeactivate;
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
		if ("userPresenceStatusAway".equalsIgnoreCase(eventPortID))
		{
			return etpUserPresenceStatusAway;
		}
		if ("userPresenceStatusPresent".equalsIgnoreCase(eventPortID))
		{
			return etpUserPresenceStatusPresent;
		}
		if ("calibrationStarted".equalsIgnoreCase(eventPortID))
		{
			return etpCalibrationStarted;
		}
		if ("calibrationFinished".equalsIgnoreCase(eventPortID))
		{
			return etpCalibrationFinished;
		}
		if ("displayAreaChanged".equalsIgnoreCase(eventPortID))
		{
			return etpDisplayAreaChanged;
		}
		if ("powerSaveStateTrue".equalsIgnoreCase(eventPortID))
		{
			return etpPowerSaveStateTrue;
		}
		if ("powerSaveStateFalse".equalsIgnoreCase(eventPortID))
		{
			return etpPowerSaveStateFalse;
		}
		if ("deviceOff".equalsIgnoreCase(eventPortID))
		{
			return etpDeviceOff;
		}
		if ("deviceOn".equalsIgnoreCase(eventPortID))
		{
			return etpDeviceOn;
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
		if ("enabled".equalsIgnoreCase(propertyName))
		{
			return propEnabled;
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
		if ("enabled".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propEnabled;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propEnabled = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propEnabled = false;
			}
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpActivate = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
            try {
                bridge.startTracking();
            } catch (Exception e) {
                //This is triggered through a model execution, so maybe we should not propagate the exception here but just log it.
                AstericsErrorHandling.instance.reportError(Tobii4CHeadTrackerInstance.this,e.getMessage());
                //throw new RuntimeException(e);
            }
		}
	};
	final IRuntimeEventListenerPort elpDeactivate = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
	          try {
	              bridge.stopTracking();
	          } catch (Exception e) {
	              //This is triggered through a model execution, so maybe we should not propagate the exception here but just log it.
	              AstericsErrorHandling.instance.reportError(Tobii4CHeadTrackerInstance.this,e.getMessage());
	              //throw new RuntimeException(e);
	          }

		}
	};

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {          
          super.start();

          //Ensure that native libs are loaded, will only do it once if it already happened before.
          bridge.loadNativeLibs();
          
          //Start tracking: The activation of the tracker should be blocking, the main_loop waiting for callbacks not.
          if(propEnabled) {
              try {
                  bridge.startTracking();
              } catch (Exception e) {
                  throw new RuntimeException(e);
              }
          }
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
          try {
              bridge.stopTracking();
          } catch (Exception e) {
              throw new RuntimeException(e);
          }
          
          super.pause();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
          super.resume();
          //Start tracking: The activation of the tracker should be blocking, the main_loop waiting for callbacks not.
          if(propEnabled) {
              try {
                  bridge.startTracking();
              } catch (Exception e) {
                  throw new RuntimeException(e);
              }
          }
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
          try {
              bridge.stopTracking();
          } catch (Exception e) {
              throw new RuntimeException(e);
          }

          super.stop();
      }
}