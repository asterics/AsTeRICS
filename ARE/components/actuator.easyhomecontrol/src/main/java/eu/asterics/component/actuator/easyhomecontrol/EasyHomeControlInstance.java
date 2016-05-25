

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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.actuator.easyhomecontrol;


import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

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
import eu.asterics.mw.services.AstericsModelExecutionThreadPool;
import eu.asterics.component.actuator.easyhomecontrol.FS20Utils;
import eu.asterics.component.actuator.easyhomecontrol.PCSDevice;

/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 *  
 * @author <your name> [<your email address>]
 *         Date: 
 *         Time: 
 */
public class EasyHomeControlInstance extends AbstractRuntimeComponentInstance
{
	static {
		System.loadLibrary("hidapi-jni");
	}
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	String propNameDevice1 = "1";
	String propNameDevice2 = "2";
	String propNameDevice3 = "3";
	String propNameDevice4 = "4";
	String propNameDevice5 = "5";
	String propNameDevice6 = "6";
	String propNameDevice7 = "7";
	String propNameDevice8 = "8";
	String propNameDevice9 = "9";
	String propNameDevice10 = "10";
	String propNameDevice11 = "11";
	String propNameDevice12 = "12";
	String propNameDevice13 = "13";
	String propNameDevice14 = "14";
	String propNameDevice15 = "15";
	int propNumberDevice1 = 1;
	int propNumberDevice2 = 2;
	int propNumberDevice3 = 3;
	int propNumberDevice4 = 4;
	int propNumberDevice5 = 5;
	int propNumberDevice6 = 6;
	int propNumberDevice7 = 7;
	int propNumberDevice8 = 8;
	int propNumberDevice9 = 9;
	int propNumberDevice10 = 10;
	int propNumberDevice11 = 11;
	int propNumberDevice12 = 12;
	int propNumberDevice13 = 13;
	int propNumberDevice14 = 14;
	int propNumberDevice15 = 15;

	// declare member variables here
	private PCSDevice pcs;
	private EasyHomeControlInstance instance = this;
	
	private Logger logger =	AstericsErrorHandling.instance.getLogger();
  
    
   /**
    * The class constructor.
    */
    public EasyHomeControlInstance()
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

		return null;
	}

    /**
     * returns an Event Listener Port.
     * @param eventPortID   the name of the port
     * @return         the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
		if ("onDevice1".equalsIgnoreCase(eventPortID))
		{
			return elpOnDevice1;
		}
		if ("onDevice2".equalsIgnoreCase(eventPortID))
		{
			return elpOnDevice2;
		}
		if ("onDevice3".equalsIgnoreCase(eventPortID))
		{
			return elpOnDevice3;
		}
		if ("onDevice4".equalsIgnoreCase(eventPortID))
		{
			return elpOnDevice4;
		}
		if ("onDevice5".equalsIgnoreCase(eventPortID))
		{
			return elpOnDevice5;
		}
		if ("onDevice6".equalsIgnoreCase(eventPortID))
		{
			return elpOnDevice6;
		}
		if ("onDevice7".equalsIgnoreCase(eventPortID))
		{
			return elpOnDevice7;
		}
		if ("onDevice8".equalsIgnoreCase(eventPortID))
		{
			return elpOnDevice8;
		}
		if ("onDevice9".equalsIgnoreCase(eventPortID))
		{
			return elpOnDevice9;
		}
		if ("onDevice10".equalsIgnoreCase(eventPortID))
		{
			return elpOnDevice10;
		}
		if ("onDevice11".equalsIgnoreCase(eventPortID))
		{
			return elpOnDevice11;
		}
		if ("onDevice12".equalsIgnoreCase(eventPortID))
		{
			return elpOnDevice12;
		}
		if ("onDevice13".equalsIgnoreCase(eventPortID))
		{
			return elpOnDevice13;
		}
		if ("onDevice14".equalsIgnoreCase(eventPortID))
		{
			return elpOnDevice14;
		}
		if ("onDevice15".equalsIgnoreCase(eventPortID))
		{
			return elpOnDevice15;
		}
		if ("offDevice1".equalsIgnoreCase(eventPortID))
		{
			return elpOffDevice1;
		}
		if ("offDevice2".equalsIgnoreCase(eventPortID))
		{
			return elpOffDevice2;
		}
		if ("offDevice3".equalsIgnoreCase(eventPortID))
		{
			return elpOffDevice3;
		}
		if ("offDevice4".equalsIgnoreCase(eventPortID))
		{
			return elpOffDevice4;
		}
		if ("offDevice5".equalsIgnoreCase(eventPortID))
		{
			return elpOffDevice5;
		}
		if ("offDevice6".equalsIgnoreCase(eventPortID))
		{
			return elpOffDevice6;
		}
		if ("offDevice7".equalsIgnoreCase(eventPortID))
		{
			return elpOffDevice7;
		}
		if ("offDevice8".equalsIgnoreCase(eventPortID))
		{
			return elpOffDevice8;
		}
		if ("offDevice9".equalsIgnoreCase(eventPortID))
		{
			return elpOffDevice9;
		}
		if ("offDevice10".equalsIgnoreCase(eventPortID))
		{
			return elpOffDevice10;
		}
		if ("offDevice11".equalsIgnoreCase(eventPortID))
		{
			return elpOffDevice11;
		}
		if ("offDevice12".equalsIgnoreCase(eventPortID))
		{
			return elpOffDevice12;
		}
		if ("offDevice13".equalsIgnoreCase(eventPortID))
		{
			return elpOffDevice13;
		}
		if ("offDevice14".equalsIgnoreCase(eventPortID))
		{
			return elpOffDevice14;
		}
		if ("offDevice15".equalsIgnoreCase(eventPortID))
		{
			return elpOffDevice15;
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
		if ("nameDevice1".equalsIgnoreCase(propertyName))
		{
			return propNameDevice1;
		}
		if ("nameDevice2".equalsIgnoreCase(propertyName))
		{
			return propNameDevice2;
		}
		if ("nameDevice3".equalsIgnoreCase(propertyName))
		{
			return propNameDevice3;
		}
		if ("nameDevice4".equalsIgnoreCase(propertyName))
		{
			return propNameDevice4;
		}
		if ("nameDevice5".equalsIgnoreCase(propertyName))
		{
			return propNameDevice5;
		}
		if ("nameDevice6".equalsIgnoreCase(propertyName))
		{
			return propNameDevice6;
		}
		if ("nameDevice7".equalsIgnoreCase(propertyName))
		{
			return propNameDevice7;
		}
		if ("nameDevice8".equalsIgnoreCase(propertyName))
		{
			return propNameDevice8;
		}
		if ("nameDevice9".equalsIgnoreCase(propertyName))
		{
			return propNameDevice9;
		}
		if ("nameDevice10".equalsIgnoreCase(propertyName))
		{
			return propNameDevice10;
		}
		if ("nameDevice11".equalsIgnoreCase(propertyName))
		{
			return propNameDevice11;
		}
		if ("nameDevice12".equalsIgnoreCase(propertyName))
		{
			return propNameDevice12;
		}
		if ("nameDevice13".equalsIgnoreCase(propertyName))
		{
			return propNameDevice13;
		}
		if ("nameDevice14".equalsIgnoreCase(propertyName))
		{
			return propNameDevice14;
		}
		if ("nameDevice15".equalsIgnoreCase(propertyName))
		{
			return propNameDevice15;
		}
		if ("numberDevice1".equalsIgnoreCase(propertyName))
		{
			return propNumberDevice1;
		}
		if ("numberDevice2".equalsIgnoreCase(propertyName))
		{
			return propNumberDevice2;
		}
		if ("numberDevice3".equalsIgnoreCase(propertyName))
		{
			return propNumberDevice3;
		}
		if ("numberDevice4".equalsIgnoreCase(propertyName))
		{
			return propNumberDevice4;
		}
		if ("numberDevice5".equalsIgnoreCase(propertyName))
		{
			return propNumberDevice5;
		}
		if ("numberDevice6".equalsIgnoreCase(propertyName))
		{
			return propNumberDevice6;
		}
		if ("numberDevice7".equalsIgnoreCase(propertyName))
		{
			return propNumberDevice7;
		}
		if ("numberDevice8".equalsIgnoreCase(propertyName))
		{
			return propNumberDevice8;
		}
		if ("numberDevice9".equalsIgnoreCase(propertyName))
		{
			return propNumberDevice9;
		}
		if ("numberDevice10".equalsIgnoreCase(propertyName))
		{
			return propNumberDevice10;
		}
		if ("numberDevice11".equalsIgnoreCase(propertyName))
		{
			return propNumberDevice11;
		}
		if ("numberDevice12".equalsIgnoreCase(propertyName))
		{
			return propNumberDevice12;
		}
		if ("numberDevice13".equalsIgnoreCase(propertyName))
		{
			return propNumberDevice13;
		}
		if ("numberDevice14".equalsIgnoreCase(propertyName))
		{
			return propNumberDevice14;
		}
		if ("numberDevice15".equalsIgnoreCase(propertyName))
		{
			return propNumberDevice15;
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
		if ("nameDevice1".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNameDevice1;
			propNameDevice1 = (String)newValue;
			return oldValue;
		}
		if ("nameDevice2".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNameDevice2;
			propNameDevice2 = (String)newValue;
			return oldValue;
		}
		if ("nameDevice3".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNameDevice3;
			propNameDevice3 = (String)newValue;
			return oldValue;
		}
		if ("nameDevice4".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNameDevice4;
			propNameDevice4 = (String)newValue;
			return oldValue;
		}
		if ("nameDevice5".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNameDevice5;
			propNameDevice5 = (String)newValue;
			return oldValue;
		}
		if ("nameDevice6".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNameDevice6;
			propNameDevice6 = (String)newValue;
			return oldValue;
		}
		if ("nameDevice7".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNameDevice7;
			propNameDevice7 = (String)newValue;
			return oldValue;
		}
		if ("nameDevice8".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNameDevice8;
			propNameDevice8 = (String)newValue;
			return oldValue;
		}
		if ("nameDevice9".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNameDevice9;
			propNameDevice9 = (String)newValue;
			return oldValue;
		}
		if ("nameDevice10".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNameDevice10;
			propNameDevice10 = (String)newValue;
			return oldValue;
		}
		if ("nameDevice11".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNameDevice11;
			propNameDevice11 = (String)newValue;
			return oldValue;
		}
		if ("nameDevice12".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNameDevice12;
			propNameDevice12 = (String)newValue;
			return oldValue;
		}
		if ("nameDevice13".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNameDevice13;
			propNameDevice13 = (String)newValue;
			return oldValue;
		}
		if ("nameDevice14".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNameDevice14;
			propNameDevice14 = (String)newValue;
			return oldValue;
		}
		if ("nameDevice15".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNameDevice15;
			propNameDevice15 = (String)newValue;
			return oldValue;
		}
		if ("numberDevice1".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNumberDevice1;
			propNumberDevice1 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("numberDevice2".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNumberDevice2;
			propNumberDevice2 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("numberDevice3".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNumberDevice3;
			propNumberDevice3 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("numberDevice4".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNumberDevice4;
			propNumberDevice4 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("numberDevice5".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNumberDevice5;
			propNumberDevice5 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("numberDevice6".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNumberDevice6;
			propNumberDevice6 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("numberDevice7".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNumberDevice7;
			propNumberDevice7 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("numberDevice8".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNumberDevice8;
			propNumberDevice8 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("numberDevice9".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNumberDevice9;
			propNumberDevice9 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("numberDevice10".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNumberDevice10;
			propNumberDevice10 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("numberDevice11".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNumberDevice11;
			propNumberDevice11 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("numberDevice12".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNumberDevice12;
			propNumberDevice12 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("numberDevice13".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNumberDevice13;
			propNumberDevice13 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("numberDevice14".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNumberDevice14;
			propNumberDevice14 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("numberDevice15".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNumberDevice15;
			propNumberDevice15 = Integer.parseInt(newValue.toString());
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
	final IRuntimeEventListenerPort elpOnDevice1 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice1,propNumberDevice1,FS20Utils.OnStep1);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOnDevice2 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice2,propNumberDevice2,FS20Utils.OnStep1);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOnDevice3 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice3,propNumberDevice3,FS20Utils.OnStep1);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOnDevice4 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice4,propNumberDevice4,FS20Utils.OnStep1);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOnDevice5 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice5,propNumberDevice5,FS20Utils.OnStep1);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOnDevice6 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice6,propNumberDevice6,FS20Utils.OnStep1);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOnDevice7 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice7,propNumberDevice7,FS20Utils.OnStep1);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOnDevice8 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice8,propNumberDevice8,FS20Utils.OnStep1);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOnDevice9 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice9,propNumberDevice9,FS20Utils.OnStep1);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOnDevice10 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice10,propNumberDevice10,FS20Utils.OnStep1);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOnDevice11 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice11,propNumberDevice11,FS20Utils.OnStep1);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOnDevice12 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice12,propNumberDevice12,FS20Utils.OnStep1);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOnDevice13 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice13,propNumberDevice13,FS20Utils.OnStep1);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOnDevice14 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice14,propNumberDevice14,FS20Utils.OnStep1);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOnDevice15 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice15,propNumberDevice15,FS20Utils.OnStep1);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOffDevice1 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice1,propNumberDevice1,FS20Utils.Off);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOffDevice2 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice2,propNumberDevice2,FS20Utils.Off);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOffDevice3 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice3,propNumberDevice3,FS20Utils.Off);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOffDevice4 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice4,propNumberDevice4,FS20Utils.Off);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOffDevice5 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice5,propNumberDevice5,FS20Utils.Off);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOffDevice6 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice6,propNumberDevice6,FS20Utils.Off);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOffDevice7 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice7,propNumberDevice7,FS20Utils.Off);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOffDevice8 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice8,propNumberDevice8,FS20Utils.Off);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOffDevice9 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice9,propNumberDevice9,FS20Utils.Off);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOffDevice10 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice10,propNumberDevice10,FS20Utils.Off);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOffDevice11 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice11,propNumberDevice11,FS20Utils.Off);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOffDevice12 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice12,propNumberDevice12,FS20Utils.Off);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOffDevice13 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice13,propNumberDevice13,FS20Utils.Off);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOffDevice14 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice14,propNumberDevice14,FS20Utils.Off);
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpOffDevice15 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendDataToEasyHome(propNumberDevice15,propNumberDevice15,FS20Utils.Off);
				 // insert event handling here 
		}
	};

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {

          super.start();
          openDevice();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
          super.pause();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
          super.resume();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
    	  //closeDevice();
          super.stop();
          
      }
      
      private void openDevice() {
  		Runnable runnable = new Runnable() {
  			@Override
  			public void run() {
  				String curThread = Thread.currentThread().getName();
  				logger.fine("[" + curThread + "]" + "Trying to open device");

  				if (pcs != null) {
  					logger.fine("[" + curThread + "]"
  							+ "PCSDevice already open");
  					closeDevice();
  				}

  				pcs = new PCSDevice();
  				if (!pcs.open()) {
  					logger.warning("["+ curThread+ "]"
  								   + "Could not open/find FS20 PCS Device. Please verify that the FS20 Transceiver is connected to a USB port.");
  					AstericsErrorHandling.instance
  							.reportError(
  									EasyHomeControlInstance.this,
  									"Could not open/find FS20 PCS Device. Please verify that the FS20 Transceiver is connected to a USB port.");
  					pcs = null;
  					return;
  				}
  			}
  		};

  		try {
  			AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(runnable);
  		} catch (InterruptedException | ExecutionException | TimeoutException e) {
  			logger.warning("Could not execute openDevice in ModelExecutor thread: "
  					+ e.getMessage());
  		}

  	}
      
      private void closeDevice() {
  		Runnable runnable = new Runnable() {
  			@Override
  			public void run() {
  				String curThread = Thread.currentThread().getName();
  				logger.fine("[" + curThread + "]" + "Trying to close device");

  				if (pcs != null) {
  					if (!pcs.close()) {
  						AstericsErrorHandling.instance.reportInfo(
  								EasyHomeControlInstance.this, "[" + curThread + "]"
  										+ "Could not close PCS Device");
  						return;
  					}
  					// Set to null anyway
  					pcs = null;
  				}
  			}
  		};
  		try {
  			AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(runnable);
  		} catch (InterruptedException | ExecutionException | TimeoutException e) {
  			logger.warning("Could not execute closeDevice in ModelExecutor thread: "
  					+ e.getMessage());
  		}

  	}
      
      private void sendDataToEasyHome(final int houseCode, final int addr,final int command) {
  		Runnable runnable = new Runnable() {
  			@Override
  			public void run() {

  				String curThread = Thread.currentThread().getName();
  				logger.fine("[" + curThread + "]" + "Sending data to EasyHome...");

  				if (pcs != null) {
  					pcs.send(houseCode, addr, command);
  				}
  			}
  		};

  		try {
  			AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(runnable);
  		} catch (InterruptedException | ExecutionException | TimeoutException e) {
  			logger.warning("Could not execute sendDataEasyHome in ModelExecutor thread: "
  					+ e.getMessage());
  		}
  	}
}