

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

package com.starlab.component.actuator.flickeringlightstimulator;

import eu.asterics.mw.cimcommunication.*;


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
import java.io.*;
import java.util.*;
/**
 * 
 * This module allows to configure and start/stop the stimulation
 * of the Flickering Light Panels. 
 * 
 *  
 * @author Laura Dubreuil Vall [laura.dubreuil@starlab.es]
 *         Date: 30/08/2012
 *         Time: 15:01
 */
public class FlickeringLightStimulatorInstance extends AbstractRuntimeComponentInstance implements CIMEventHandler
{
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();
	
	final IRuntimeEventTriggererPort stimPeriodFinishedEtpPort = new DefaultRuntimeEventTriggererPort();

	int propFrequencyPanel1 = 1;
	int propFrequencyPanel2 = 1;
	int propFrequencyPanel3 = 1;
	int propFrequencyPanel4 = 1;
	
	int propDcPanel1 = 50;
	int propDcPanel2 = 50;
	int propDcPanel3 = 50;
	int propDcPanel4 = 50;
	
	int propIntPanel1 = 100;
	int propIntPanel2 = 100;
	int propIntPanel3 = 100;
	int propIntPanel4 = 100;
	
	boolean propRed = true;
	boolean propBlue = true;
	boolean propGreen = true;
	
	int propMsec = 3000;
	
	String propComPort = "COM7";

	private CIMPortController port = null;
	private int checksumPanel1;
	private int checksumPanel2;
	private int checksumPanel3;
	private int checksumPanel4;	
	private String buffer = "";
    
   /**
    * The class constructor.
    */
    public FlickeringLightStimulatorInstance()
    {
   	
        
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
		if ("panel1".equalsIgnoreCase(portID))
		{
			return ipPanel1;
		}
		if ("panel2".equalsIgnoreCase(portID))
		{
			return ipPanel2;
		}
		if ("panel3".equalsIgnoreCase(portID))
		{
			return ipPanel3;
		}
		if ("panel4".equalsIgnoreCase(portID))
		{
			return ipPanel4;
		}

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
		if ("startStim".equalsIgnoreCase(eventPortID))
		{
			return startElpPort;
		}
		
		if ("stopStim".equalsIgnoreCase(eventPortID))
		{
			return stopElpPort;
		}
		if ("startStimPeriod".equalsIgnoreCase(eventPortID))
		{
			return startStimPeriodElpPort;
		}
		if ("updateConfiguration".equalsIgnoreCase(eventPortID))
		{
			return updateConfigurationElpPort;
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

		if ("stimPeriodFinished".equalsIgnoreCase(eventPortID))
		{
			return stimPeriodFinishedEtpPort;
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
		if ("frequencyPanel1".equalsIgnoreCase(propertyName))
		{
			return propFrequencyPanel1;
		}
		if ("frequencyPanel2".equalsIgnoreCase(propertyName))
		{
			return propFrequencyPanel2;
		}
		if ("frequencyPanel3".equalsIgnoreCase(propertyName))
		{
			return propFrequencyPanel3;
		}
		if ("frequencyPanel4".equalsIgnoreCase(propertyName))
		{
			return propFrequencyPanel4;
		}
		if ("dcPanel1".equalsIgnoreCase(propertyName))
		{
			return propDcPanel1;
		}
		if ("dcPanel2".equalsIgnoreCase(propertyName))
		{
			return propDcPanel2;
		}
		if ("dcPanel3".equalsIgnoreCase(propertyName))
		{
			return propDcPanel3;
		}
		if ("dcPanel4".equalsIgnoreCase(propertyName))
		{
			return propDcPanel4;
		}
		if ("intPanel1".equalsIgnoreCase(propertyName))
		{
			return propIntPanel1;
		}
		if ("intPanel2".equalsIgnoreCase(propertyName))
		{
			return propIntPanel2;
		}
		if ("intPanel3".equalsIgnoreCase(propertyName))
		{
			return propIntPanel3;
		}
		if ("intPanel4".equalsIgnoreCase(propertyName))
		{
			return propIntPanel4;
		}
		if ("red".equalsIgnoreCase(propertyName))
		{
			return propRed;
		}
		if ("blue".equalsIgnoreCase(propertyName))
		{
			return propBlue;
		}
		if ("green".equalsIgnoreCase(propertyName))
		{
			return propGreen;
		}
		if ("comPort".equalsIgnoreCase(propertyName))
		{
			return propComPort;
		}
		if ("msec".equalsIgnoreCase(propertyName))
		{
			return propMsec;
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
		if ("frequencyPanel1".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFrequencyPanel1;
			propFrequencyPanel1 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("frequencyPanel2".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFrequencyPanel2;
			propFrequencyPanel2 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("frequencyPanel3".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFrequencyPanel3;
			propFrequencyPanel3 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("frequencyPanel4".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFrequencyPanel4;
			propFrequencyPanel4 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("dcPanel1".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propDcPanel1;
			propDcPanel1 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("dcPanel2".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propDcPanel2;
			propDcPanel2 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("dcPanel3".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propDcPanel3;
			propDcPanel3 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("dcPanel4".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propDcPanel4;
			propDcPanel4 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("intPanel1".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propIntPanel1;
			propIntPanel1 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("intPanel2".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propIntPanel2;
			propIntPanel2 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("intPanel3".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propIntPanel3;
			propIntPanel3 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("intPanel4".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propIntPanel4;
			propIntPanel4 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("red".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propRed;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propRed = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propRed = false;
			}
			return oldValue;
		}
		if ("blue".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propBlue;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propBlue = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propBlue = false;
			}
			return oldValue;
		}
		if ("green".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propGreen;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propGreen = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propGreen = false;
			}
			return oldValue;
		}
		if ("comPort".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propComPort;
			propComPort = (String)newValue;
			return oldValue;
		}
		if ("msec".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propMsec;
			propMsec = Integer.parseInt(newValue.toString());
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipPanel1  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{			
			propFrequencyPanel1 = ConversionUtils.intFromBytes(data);
		}
	};
	private final IRuntimeInputPort ipPanel2  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			propFrequencyPanel2 = ConversionUtils.intFromBytes(data);
		}
	};
	private final IRuntimeInputPort ipPanel3  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			propFrequencyPanel3 = ConversionUtils.intFromBytes(data);
		}
	};
	private final IRuntimeInputPort ipPanel4  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			propFrequencyPanel4 = ConversionUtils.intFromBytes(data);
		}
	};
	
	//Start stimulation
	void startStimulation()
	{
		short trash = 0x00;
		byte c = 0x00;
		byte [] end = { c }; 
		
		byte[] start_stimulation = ConversionUtils.stringToBytes("Start_Stimulation");
		CIMPortManager.getInstance().sendPacket(port, start_stimulation, trash, trash, false);
		CIMPortManager.getInstance().sendPacket(port, end, trash, trash, false);
	}
		
	//Send protocol
	void send_data(int frequency_b, int dc_b, int intensity_b, int checksum_b)
	{
		short trash = 0x00;
		byte c = 0x00;
		byte [] end = { c }; 
		byte [] frequency = ConversionUtils.stringToBytes(Integer.toString(frequency_b*2));
		byte [] dc = ConversionUtils.stringToBytes(Integer.toString(dc_b));
		byte [] intensity = ConversionUtils.stringToBytes(Integer.toString(intensity_b));
		byte [] checksum = ConversionUtils.stringToBytes(Integer.toString(checksum_b));
		
		CIMPortManager.getInstance().sendPacket(port, frequency, trash, trash, false);
		CIMPortManager.getInstance().sendPacket(port, end, trash, trash, false);

		CIMPortManager.getInstance().sendPacket(port, dc, trash, trash, false);
		CIMPortManager.getInstance().sendPacket(port, end, trash, trash, false);
		
		CIMPortManager.getInstance().sendPacket(port, intensity, trash, trash, false);
		CIMPortManager.getInstance().sendPacket(port, end, trash, trash, false);

		CIMPortManager.getInstance().sendPacket(port, checksum, trash, trash, false);
		CIMPortManager.getInstance().sendPacket(port, end, trash, trash, false);
	}
	
	//Set panel configuration
	void setConfiguration()
	{		
		short trash = 0x00;
		byte c = 0x00;
		byte [] end = { c }; 
		String col;
		
		byte[] set_panel_configuration = ConversionUtils.stringToBytes("Set_Panel_Configuration");		
		CIMPortManager.getInstance().sendPacket(port, set_panel_configuration, trash, trash, false);
		CIMPortManager.getInstance().sendPacket(port, end, trash, trash, false);		
		
		checksumPanel1 = calc_checksum(propFrequencyPanel1*2, propDcPanel1, propIntPanel1);
		send_data(propFrequencyPanel1, propDcPanel1, propIntPanel1, checksumPanel1); //Panel 1
		
		checksumPanel2 = calc_checksum(propFrequencyPanel2*2, propDcPanel2, propIntPanel2);
		send_data(propFrequencyPanel2, propDcPanel2, propIntPanel2, checksumPanel2); //Panel 2
		
		checksumPanel3 = calc_checksum(propFrequencyPanel3*2, propDcPanel3, propIntPanel3);
		send_data(propFrequencyPanel3, propDcPanel3, propIntPanel3, checksumPanel3); //Panel 3
		
		checksumPanel4 = calc_checksum(propFrequencyPanel4*2, propDcPanel4, propIntPanel4);
		send_data(propFrequencyPanel4, propDcPanel4, propIntPanel4, checksumPanel4); //Panel 4	
		
		if (propGreen == true)
			col = "1";
		else
			col = "0";
		
		if (propRed == true)
			col = col + "1";
		else
			col = col + "0";
		
		if (propBlue == true)
			col = col + "1";
		else
			col = col + "0";
		
		byte [] color = ConversionUtils.stringToBytes(col);		
		CIMPortManager.getInstance().sendPacket(port, color, trash, trash, false);
		CIMPortManager.getInstance().sendPacket(port, end, trash, trash, false);
	}

	
	// Stop stimulation
	void stopStimulation()
	{
		short trash = 0x00;
		byte c = 0;
		byte [] end = { c }; 
		byte[] stop_stimulation = ConversionUtils.stringToBytes("Stop_Stimulation");
		
		CIMPortManager.getInstance().sendPacket(port, stop_stimulation, trash, trash, false);
		CIMPortManager.getInstance().sendPacket(port, end, trash, trash, false);
	}
	
	void startStimPeriod()
	{
		startStimulation();
    	try {
    		  Thread.sleep(propMsec);
    		} catch (InterruptedException ie) {
    		    //Handle exception
    		}
    	stopStimulation();
    	stimPeriodFinishedEtpPort.raiseEvent();   			
	}


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort updateConfigurationElpPort = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			 setConfiguration();
		}
	};
	
	final IRuntimeEventListenerPort startElpPort = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			 startStimulation();
		}
	};
		
	final IRuntimeEventListenerPort stopElpPort = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{			 
			stopStimulation();
		}
	};
	
	final IRuntimeEventListenerPort startStimPeriodElpPort = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{			 
			startStimPeriod();
		}
	};
	

	// function to build checksum out of 3 bytes of data 
	int calc_checksum(int data1, int data2, int data3)
	{
		int checksum = 0;
		int i = 0;
		int n = 0;
		
		for (i = 0x01, checksum=0, n = 0; n < 8; i = i << 1, n++)	// shift data
		{
			  if ((i & data1) == i) checksum++;
			  if ((i & data2) == i) checksum++;
			  if ((i & data3) == i) checksum++;
		}
		return checksum;	// return number of '1's of the 3 strings
	}

	@Override
	public void handlePacketReceived(CIMEvent e)
	{
		CIMEventRawPacket eventRawPacket = (CIMEventRawPacket)e;
		
		byte[] data = { eventRawPacket.b };
		
		String dataString = ConversionUtils.stringFromBytes(data);
		
	}
	
	@Override
	public void handlePacketError(CIMEvent e)
	{
		AstericsErrorHandling.instance.reportError(this, "handle error");
		/*CIMEventErrorPacketFault EventErrorPacketFault = (CIMEventErrorPacketFault)e;
		
		AstericsErrorHandling.instance.reportError(this, EventErrorPacketFault.toString());
		
		CIMEventErrorPacketLost EventErrorPacketLost = (CIMEventErrorPacketLost)e;
		
		AstericsErrorHandling.instance.reportError(this, EventErrorPacketLost.toString());*/
				
	}

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {

    	  	short trash = 0x00;
    		byte c = 0x00;
    		byte [] end = { c }; 
      	  	port = CIMPortManager.getInstance().getRawConnection(propComPort,9600); //COM Port and baud rate
      	  	port.addEventListener(this);
      	  	
          	if (port != null)
          	{
          		super.start();
      	    	AstericsErrorHandling.instance.reportInfo(this, "Flickering Light Stimulator started");
      	    	setConfiguration();
      			/*byte[] idle = ConversionUtils.stringToBytes("idle");
      			CIMPortManager.getInstance().sendPacket(port, idle, trash, trash, false);
      			CIMPortManager.getInstance().sendPacket(port, end, trash, trash, false);*/

      	    	//statusRequest();
      	    	/*setConfiguration();
      	    	startStimulation();
      	    	try {
    	    		  Thread.sleep(5000);
    	    		} catch (InterruptedException ie) {
    	    		    //Handle exception
    	    		}
      	    	stopStimulation();*/
      			
      			/*byte[] status_request = ConversionUtils.stringToBytes("Status_Request");  			
      			
      			CIMPortManager.getInstance().sendPacket(port, status_request, trash, trash, false);
      			CIMPortManager.getInstance().sendPacket(port, end, trash, trash, false);*/
          	}
          	else
          	{
          		AstericsErrorHandling.instance.reportError(this, "Could not find COM port");
          	}
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
      	if (port != null)
      	{
      		stopStimulation();
      	}
          super.stop();
      }
        
      
}