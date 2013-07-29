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
 *     This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.sensor.razorimu;


import eu.asterics.mw.cimcommunication.*;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;


//import gnu.io.*;
import java.util.*;
import java.util.logging.*;



/**
 * RazorImuInstance reads data streams from the 9DOF Razor IMU in PitchYawRoll 
 * Mode and sends the data to the respective port
 * 
 * @author Christoph Weiss [weissch@technikum-wien.at]
 *         Date: Nov 29, 2010
 *         Time: 10:22:08 AM
 */
public class RazorImuInstance extends AbstractRuntimeComponentInstance 
	implements CIMEventHandler
{

	private final OutputPort opPitchPort = new OutputPort();
	private final OutputPort opYawPort   = new OutputPort();
	private final OutputPort opRollPort  = new OutputPort();

	final String comPortPropertyName = "comPort";
	final String baudRatePropertyName = "baudRate";  
	final String operationModePropertyName = "operationMode";  

	enum OperationMode 
	{
		PitchYawRoll
	};

	OperationMode propOperationMode = OperationMode.PitchYawRoll;

	String propComPortName = "COM11";
	int propBaudRate = 57600;

	CIMPortController portController = null;

	ArrayDeque<Byte> inputQueue = new ArrayDeque<Byte>();

	/**
	 * Constructs the instance
	 */
	public RazorImuInstance()
	{
	}

	/**
	 * Returns the requested output of the component
	 * @param portID the name of the output port
	 * @return the requested port instance for the IDs "pitch","yaw" and "roll",
	 * null if non existant
	 */
	public IRuntimeOutputPort getOutputPort(String portID)
	{

		if("pitch".equalsIgnoreCase(portID))
		{
			return opPitchPort;
		}
		else if("yaw".equalsIgnoreCase(portID))
		{
			return opYawPort;
		}
		else if("roll".equalsIgnoreCase(portID))
		{
			return opRollPort;
		}
		return null;
	}

	/**
	 * Returns the values of the properties of the component 
	 * @param propertyName the name of the requested property
	 * @return the value of the property as an Object
	 * 
	 */
	@Override
	public Object getRuntimePropertyValue(String propertyName) {
		if(comPortPropertyName.equalsIgnoreCase(propertyName))
		{
			return this.propComPortName;
		}
		else if(baudRatePropertyName.equalsIgnoreCase(propertyName))
		{
			return this.propBaudRate;
		}
		else if (operationModePropertyName.equalsIgnoreCase(propertyName))
		{
			switch (propOperationMode)
			{
			case PitchYawRoll:
				return new String("PitchYawRoll");

			}
		}
		return null;
	}

	/**
	 * Sets a new value to a specified property
	 *  
	 * @param propertyName the name of the requested property
	 * @param newValue the value to be set for the property
	 * @return the old value of the property as an Object
	 */
	@Override
	public Object setRuntimePropertyValue(String propertyName, Object newValue) {
		if(comPortPropertyName.equalsIgnoreCase(propertyName))
		{
			final String oldValue = this.propComPortName;
			CIMPortManager.getInstance().closeRawConnection(propComPortName);
			propComPortName = (String) newValue;
			AstericsErrorHandling.instance.reportInfo(this, "Set COM port attribute to:" + propComPortName);            
			return oldValue;
		}
		else if(baudRatePropertyName.equalsIgnoreCase(propertyName))
		{
			final Integer oldValue = this.propBaudRate;

			propBaudRate = Integer.parseInt((String) newValue);
			CIMPortManager.getInstance().getRawConnection(propComPortName, propBaudRate);
			AstericsErrorHandling.instance.reportInfo(this, "Set COM port baudrate to:" + propBaudRate);            
			return oldValue;
		}
		else if (operationModePropertyName.equalsIgnoreCase(propertyName))
		{
			if ( ((String) newValue).equalsIgnoreCase("PitchYawRoll"))
			{
				propOperationMode = OperationMode.PitchYawRoll;
			}
		}
		return null;
	}


	/**
	 * An output port implementation which can send double values
	 * @author weissch
	 *
	 */
	public class OutputPort extends DefaultRuntimeOutputPort
	{
		/**
		 * Sends data to the connected input port 
		 * @param data a double value to be sent
		 * 
		 */
		public void sendData(double data)
		{
			//TODO change this to a more useful conversion
			super.sendData(ConversionUtils.doubleToBytes(data));
		}
	}

	/**
	 * Parses a packet that the IMU is sending. Reads out the values for pitch,
	 * yaw and roll and transfers them on the corresponding output ports
	 */
	void parsePacket()
	{
		double [] values = new double[3];
		//		 byte [] garbage = new byte[100];
		//		 int dataSkipped = 0;

		try
		{
			while (inputQueue.removeFirst() != '!') ;

			Logger.getAnonymousLogger().finest("Found start of packet, no bytes skipped");

			if (inputQueue.removeFirst() == 'A' && inputQueue.removeFirst() == 'N' && 
					inputQueue.removeFirst() == 'G' && inputQueue.removeFirst() == ':')
			{
	        	Logger.getAnonymousLogger().finest("ANG sequence detected");            		
				int j = 0, f = 0;
				for (int i = 0 ; i < 3; i++)
				{
					byte [] array = new byte[10];
					boolean floatBoundaryFound = false;
					while (!floatBoundaryFound)
					{
						array[j] =  inputQueue.removeFirst();
						if ((array[j] == ',') || (array[j] == '\n'))
						{
							array[j] = 0;
							values[f] = Double.parseDouble(new String(array));
							Logger.getAnonymousLogger().info("Parsed float value[" + f + "]:" + values[f]);
							floatBoundaryFound = true;

						}
						else 
						{
							Logger.getAnonymousLogger().info("Read character: " + (char) array[j]);
							j++;
						}
					}
					j = 0;
					f++;
				}
			}
			else
			{
				Logger.getAnonymousLogger().warning("Parse error");
				return;
			}

			Logger.getAnonymousLogger().fine("Read values: pitch " + values[0] +",yaw: " + values[1] + ",roll: " + values[2]);

			opPitchPort.sendData(values[0]);
			opYawPort.sendData(values[1]);
			opRollPort.sendData(values[2]);

		} 
		catch (Exception e)
		{
			// happens when Deque does not contain full packet yet
		}
	}

	/**
	 * Called by the raw port controller if data is available
	 * @param ev a CIMEvent which can be ignored as it is only needed due to the
	 * interface specification
	 */
	public void handlePacketReceived(CIMEvent ev)
	{
		byte data;		

		switch (propOperationMode)
		{
		case PitchYawRoll:
			CIMEventRawPacket rp = (CIMEventRawPacket ) ev;
			data = rp.b;
			inputQueue.addLast(data);
			if (data == '\n')
			{
				//	            		Logger.getAnonymousLogger().info("Packet end detected, parsing ...");            		
				parsePacket();
			}
			break;
		}
	}

	/**
	 * Method stub, needed by CIM listener interface 
	 */
	public void handlePacketError(CIMEvent e)
	{

	}

	/**
	 * Starts the component, opens the raw port controller for the COM port and
	 * adds itself as an event listener
	 */
	@Override
	public void start()
	{
		portController = CIMPortManager.getInstance()
			.getRawConnection(propComPortName, propBaudRate);
		if (portController == null)
		{
			AstericsErrorHandling.instance.reportError(this, 
					"Could not open port for connection to device");
		}
		portController.addEventListener(this);

		super.start();
	}

	/**
	 * Releases the port controller and pauses the component
	 */
	@Override
	public void pause()
	{
		if (portController != null)
		{
			portController.removeEventListener(this);
			CIMPortManager.getInstance().closeRawConnection(propComPortName);
			portController = null;
			AstericsErrorHandling.instance.reportInfo(this, "Raw port controller closed");
		}
		super.pause();
	}

	/**
	 * Obtains the port controller and resumes data transfer
	 */
	@Override
	public void resume()
	{
		portController = CIMPortManager.getInstance().getRawConnection(propComPortName, propBaudRate);
		if (portController == null)
		{
			AstericsErrorHandling.instance.reportError(this, "Could not construct raw port controller");
		}
		portController.addEventListener(this);

		super.resume();	    	
	}

	/**
	 * Stops the component and closes the raw port controller
	 */
	@Override
	public void stop()
	{
		super.stop();
		if (portController != null)
		{
			portController.removeEventListener(this);
			CIMPortManager.getInstance().closeRawConnection(propComPortName);
			portController = null;
			AstericsErrorHandling.instance.reportInfo(this, "RazorImu connection closed");
		}
	}
}
