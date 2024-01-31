

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

package eu.asterics.component.sensor.trackir;

import eu.asterics.component.sensor.trackir.jni.AbstractBridge;
import eu.asterics.component.sensor.trackir.jni.Bridge;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.utils.OSUtils;
import eu.asterics.mw.services.AstericsThreadPool;

/**
 * 
 * This Plugin provides an interface to the TrackIR 5 head tracker 
 * see https://www.trackir.com/
 * The TrackIR 5 can measure the head orientation in 6DOF (Yaw, Pitch, Roll, X, Y, Z)
 * This data can be utilized for mouse cursor controland other purposes.
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: 2024-01-20
 */
public class TrackIRInstance extends AbstractRuntimeComponentInstance
{
    private final OutputPort opYaw = new OutputPort();
    private final OutputPort opPitch = new OutputPort();	
	private final OutputPort opRoll = new OutputPort();	
	private final OutputPort opX = new OutputPort();	
	private final OutputPort opY = new OutputPort();	
	private final OutputPort opZ = new OutputPort();	
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 


    private final AbstractBridge bridge;

	boolean propAutostart = true;
	int propPollingInterval = 10;

	// declare member variables here

    private boolean running;
	private boolean sendingData = true;
  
    
   /**
    * The class constructor.
    */
    public TrackIRInstance()
    {		
        bridge = new Bridge(opYaw, opPitch, opRoll, opX, opY, opZ);
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
		if ("yaw".equalsIgnoreCase(portID))
		{
			return opYaw;
		}
		if ("pitch".equalsIgnoreCase(portID))
		{
			return opPitch;
		}
		if ("roll".equalsIgnoreCase(portID))
		{
			return opRoll;
		}
		if ("x".equalsIgnoreCase(portID))
		{
			return opX;
		}
		if ("y".equalsIgnoreCase(portID))
		{
			return opY;
		}
		if ("z".equalsIgnoreCase(portID))
		{
			return opZ;
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
		if ("start".equalsIgnoreCase(eventPortID))
		{
			return elpStart;
		}
		if ("stop".equalsIgnoreCase(eventPortID))
		{
			return elpStop;
		}
		if ("center".equalsIgnoreCase(eventPortID))
		{
			return elpCenter;
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

        return null;
    }
		
    /**
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
		if ("autostart".equalsIgnoreCase(propertyName))
		{
			return propAutostart;
		}
		if ("pollingInterval".equalsIgnoreCase(propertyName))
		{
			return propPollingInterval;
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
		if ("autostart".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAutostart;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propAutostart = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propAutostart = false;
			}
			return oldValue;
		}
		if ("pollingInterval".equalsIgnoreCase(propertyName))
		{
			
			final Object oldValue = propPollingInterval;
			propPollingInterval = Integer.parseInt(newValue.toString());
			return oldValue;
		}

        return null;
    }


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpStart = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendingData=true;
		}
	};
	final IRuntimeEventListenerPort elpStop = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendingData=false;
		}
	};
	final IRuntimeEventListenerPort elpCenter = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			bridge.centerCoordinates();
		}
	};

    /**
     * Output Port for mouse coordinate values.
     */
    public class OutputPort extends DefaultRuntimeOutputPort {
        public void sendData(int data) {
            super.sendData(ConversionUtils.intToByteArray(data));
        }
    }	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
        bridge.activate();
        super.start();
        running = true;
		sendingData=propAutostart;
		

        AstericsThreadPool.instance.execute(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    try {
							Thread.sleep(propPollingInterval);
							if (sendingData) {
								bridge.getUpdate();
							}
                        }
                    catch (InterruptedException e) {
                    }
                }
            }
        });		  
		  
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
		  sendingData=false;
          super.pause();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
		  sendingData=true;
          super.resume();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
		  bridge.deactivate();
          super.stop();
          running = false;
      }
}