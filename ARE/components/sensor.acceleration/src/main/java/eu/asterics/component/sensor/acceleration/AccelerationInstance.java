
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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */


package eu.asterics.component.sensor.acceleration;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import eu.asterics.mw.cimcommunication.CIMEvent;
import eu.asterics.mw.cimcommunication.CIMEventHandler;
import eu.asterics.mw.cimcommunication.CIMEventPacketReceived;
import eu.asterics.mw.cimcommunication.CIMPortController;
import eu.asterics.mw.cimcommunication.CIMPortManager;
import eu.asterics.mw.cimcommunication.CIMProtocolPacket;
import eu.asterics.mw.cimcommunication.CIMWirelessDataEvent;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 *   Implements the acceleration plugin, which provides data from the
 *   acceleration CIM sensor module at 3 output ports (X, Y, Z)
 *  
 *   @author Christoph Veigl [veigl@technikum-wien.at]
 *         Date: Apr 28, 2011
 *         Time: 11:44:00 AM
 */
public class AccelerationInstance extends AbstractRuntimeComponentInstance implements CIMEventHandler
{
	private static final String WIRELESS_ACC_STRING = "WIRELESS_ACC";

	private static final String WIRED_ACC_STRING = "WIRED_ACC";

	private CIMPortController port = null;

	private final String KEY_PROPERTY_AUTOSTART 		= "autoStart";
	private final String KEY_PROPERTY_DESCRETE_STEPS 	= "descreteSteps";
	private final String KEY_PROPERTY_UDPATE_FREQUENCY 	= "updateFrequency";
	private final String KEY_PROPERTY_ACCELERATION_RANGE = "accelerationRange";
	private final String KEY_PROPERTY_UNIQUE_ID = "uniqueID";

	private static final short ACC_FEATURE_FREQUENCY   	= 0x61;
	private static final short ACC_FEATURE_RANGE 	  	= 0x62;
	private static final short ACC_FEATURE_INPUT_DATA 	= 0x63;
	private static final short ACC_FEATURE_AUTOSEND	  	= 0x64;

	private final short ACC_CIM_ID 			= 0x0501;
	private final short WIRELESS_ACC_CIM_ID 			= 0x0d01;
	private final short NUMBER_OF_PORTS		= 4;

	final OutputPort [] outputPorts = new OutputPort[NUMBER_OF_PORTS];    

	private boolean propAutoStart = true;
	private short propDescreteSteps = 0;
	private short propUpdateFrequency = 0;
	private short propAccelerationRange = 0;
    private String 	propUniqueID = "not used";
    private short 	propDefaultCIMID = ACC_CIM_ID;
    
	private boolean wirelessCIM = false;
	private boolean calibrateNow = false;
	private int calX=0;
	private int calY=0;
	private int calZ=0;
	boolean threadActive = false;

	/**
	 * The class constructor.
	 */
	public AccelerationInstance()
	{
		// create the output ports
		for (int i = 0; i < NUMBER_OF_PORTS; i++)
		{
			outputPorts[i] = new OutputPort();
		}
	}

	/**
	 * returns an Output Port.
	 * @param portID   the name of the port
	 * @return         the output port or null if not found
	 */
	public IRuntimeOutputPort getOutputPort(String portID)
	{
		if("xAcc".equalsIgnoreCase(portID))
		{
			return outputPorts[0];
		}
		else if("yAcc".equalsIgnoreCase(portID))
		{
			return outputPorts[1];
		}
		else if("zAcc".equalsIgnoreCase(portID))
		{
			return outputPorts[2];
		}
		else if("total".equalsIgnoreCase(portID))
		{
			return outputPorts[3];
		}

		return null;
	}

	/**
	 * returns an Event Listener Port.
	 * @param portID   the name of the port
	 * @return         the event listener port or null if not found
	 */
	public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
	{
		if("start".equalsIgnoreCase(eventPortID))
		{
			return elpStart;
		}
		else if("stop".equalsIgnoreCase(eventPortID))
		{
			return elpStop;
		}
		else if("calibrate".equalsIgnoreCase(eventPortID))
		{
			return elpCalibrate;
		}
		return null;
	}

	/**
	 * Returns a List of available CIM unique IDs
	 * @return list of string with CIM IDs
	 */
	public synchronized List<String> getRuntimePropertyList(String key) 
	{
		List<String> res = new ArrayList<String>(); 
		if (key.compareToIgnoreCase("uniqueID")==0)
		{
			res.add("not used");
			String s;
			// get wired accelerator in CIMs
			Vector<Long> ids=CIMPortManager.getInstance()
				.getUniqueIdentifiersofCIMs(ACC_CIM_ID);
			if (ids != null)
			{ 
				for (Long l : ids)
				{
					//s = String.format("0x%x-0x%x", ACC_CIM_ID, l);
					s = String.format(WIRED_ACC_STRING+"-0x%x", l);
					res.add(s);
					System.out.println(ACC_CIM_ID+" found unique ID: "+s);
				}
			}
								
			// get wireless digital in CIMs
			ids=CIMPortManager.getInstance()
				.getUniqueIdentifiersofWirelessCIMs(WIRELESS_ACC_CIM_ID);
			if (ids != null)
			{ 
				for (Long l : ids)
				{
					s = String.format(WIRELESS_ACC_STRING+"-0x%x", l);
					res.add(s);
					System.out.println(WIRELESS_ACC_CIM_ID+" found wireless unique ID: "+s);
				}
			}
		}
		return res;
	} 	

	/**
	 * returns the value of the given property.
	 * @param propertyName   the name of the property
	 * @return               the property value or null if not found
	 */
	public synchronized Object getRuntimePropertyValue(String propertyName)
	{
		if(KEY_PROPERTY_AUTOSTART.equalsIgnoreCase(propertyName))
		{
			return propAutoStart;
		}
		else if(KEY_PROPERTY_DESCRETE_STEPS.equalsIgnoreCase(propertyName))
		{
			return propDescreteSteps;
		}
		else if(KEY_PROPERTY_UDPATE_FREQUENCY.equalsIgnoreCase(propertyName))
		{
			return propUpdateFrequency;
		}
		else if(KEY_PROPERTY_ACCELERATION_RANGE.equalsIgnoreCase(propertyName))
		{
			return propAccelerationRange;
		}
    	else if(KEY_PROPERTY_UNIQUE_ID.equalsIgnoreCase(propertyName))
        {
            return propUniqueID;
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
		if (KEY_PROPERTY_AUTOSTART.equalsIgnoreCase(propertyName))
		{
			boolean propertyValue = false;

			if("true".equalsIgnoreCase((String)newValue))
				propAutoStart = true;
			else if("false".equalsIgnoreCase((String)newValue))
				propAutoStart = false;
			else
				AstericsErrorHandling.instance.reportInfo(this, "Invalid property value for " + propertyName + ": " + newValue);

		}
		else if(KEY_PROPERTY_DESCRETE_STEPS.equalsIgnoreCase(propertyName))
		{
			try
			{
				propDescreteSteps = Short.parseShort(newValue.toString());
				AstericsErrorHandling.instance.reportInfo(this, String.format("Setting DescreteSteps to %d", propDescreteSteps));
			}
			catch (NumberFormatException nfe)
			{
				AstericsErrorHandling.instance.reportInfo(this, "Invalid property value for " + propertyName + ": " + newValue);
			}        
		}
		else if(KEY_PROPERTY_UDPATE_FREQUENCY.equalsIgnoreCase(propertyName))
		{
			try
			{
				propUpdateFrequency = Short.parseShort(newValue.toString());
				AstericsErrorHandling.instance.reportInfo(this, String.format("Setting UpdateFrequency to %d", propUpdateFrequency));
			}
			catch (NumberFormatException nfe)
			{
				AstericsErrorHandling.instance.reportInfo(this, "Invalid property value for " + propertyName + ": " + newValue);
			}        
		}
		else if(KEY_PROPERTY_ACCELERATION_RANGE.equalsIgnoreCase(propertyName))
		{
			try
			{
				propAccelerationRange = Short.parseShort(newValue.toString());
				AstericsErrorHandling.instance.reportInfo(this, String.format("Setting AccelerationRange to %d", propAccelerationRange));
			}
			catch (NumberFormatException nfe)
			{
				AstericsErrorHandling.instance.reportInfo(this, "Invalid property value for " + propertyName + ": " + newValue);
			}        
		}
    	else if(KEY_PROPERTY_UNIQUE_ID.equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propUniqueID;
			propUniqueID = (String)newValue;
			
			CIMPortController tempPort = openCIM(propDefaultCIMID, propUniqueID);

			if (tempPort != null)
			{
				port=tempPort;
				if (    (!wirelessCIM) &&
						(!propUniqueID.equals("")) && 
						(!propUniqueID.equals("not used")))
				{
						byte [] leBytes = { (byte) 0x20 }; 
						CIMPortManager.getInstance().sendPacket  (port, leBytes, 
							  (short) 0x77, 
							  CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
						try { Thread.sleep (1000); }  catch (InterruptedException e) {}
						byte [] lebytes = { (byte) 0 };
						CIMPortManager.getInstance().sendPacket  (port, lebytes, 
							  (short) 0x77, 
							  CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);

				}
			}
			return oldValue;
		}

		return null;
	}

	/**
	 * called when model is started.
	 */
	@Override
	public synchronized void start()
	{
		/*
		CIMPortController port = CIMPortManager.getInstance().getConnection(ACC_CIM_ID);
		if (port == null )
		{
			port = CIMPortManager.getInstance()
				.getWirelessConnection(WIRELESS_ACC_CIM_ID, 0xa000003);
			if (port != null)
				wirelessCIM = true;
		}
		else
		{
			wirelessCIM = false;
		}*/
    	if (port == null)
    	{
    		port = openCIM(propDefaultCIMID, propUniqueID);
    	}
		
		if (port != null )
		{
			port.addEventListener(this);
			if (!wirelessCIM)
			{
				byte [] data = { (byte)propUpdateFrequency };
				CIMPortManager.getInstance().sendPacket(port, data, ACC_FEATURE_FREQUENCY, 
						CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
				data[0]=(byte)propAccelerationRange;
				CIMPortManager.getInstance().sendPacket(port, data, ACC_FEATURE_RANGE, 
						CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
				if (propAutoStart == true)
					data[0]=(byte)1;
				else data[0]=(byte)0;
				CIMPortManager.getInstance().sendPacket(port, data, ACC_FEATURE_AUTOSEND, 
						CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
			}
		}
		else
		{
       		AstericsErrorHandling.instance.reportError(this, "Could not find Acceleration Module. Please verify that the CIM Module is connected to an USB Port and that the driver is installed.");
		}
		super.start();
		AstericsErrorHandling.instance.reportInfo(this, "AccelerationInstance started");
	}

	/**
	 * called when model is paused.
	 */
	@Override
	public synchronized void pause()
	{
		super.pause();
	}

	/**
	 * called when model is resumed.
	 */
	@Override
	public synchronized void resume()
	{
		super.resume();
	}

	/**
	 * called when model is stopped.
	 */
	@Override
	public synchronized void stop()
	{
		super.stop();
		CIMPortController port = CIMPortManager.getInstance().getConnection(ACC_CIM_ID);
		if (port != null )
		{
			if (!wirelessCIM)
			{
				byte [] data = { (byte)0 };
				CIMPortManager.getInstance().sendPacket(port, data, ACC_FEATURE_AUTOSEND, 
						CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
			}
			port.removeEventListener(this);
		}
		AstericsErrorHandling.instance.reportInfo(this, "AccelerationInstance stopped");
	}

    /**
     * Opens a CIM controller for a certain unique ID 
     * @param cimId the CIM type ID of the CIM 
     * @param uniqueID the unique ID of the CIM as a string
     * @return the CIM controller for the corresponding CIM, null if not present
     */
	private CIMPortController openCIM(short cimID, String uniqueID)
	{
		if ("not used".equalsIgnoreCase(propUniqueID) || (propUniqueID==""))
		{
		    return (CIMPortManager.getInstance().getConnection(cimID));
		}
		else
		{
			try {
				//short id = Short.decode(propUniqueID.substring(0, propUniqueID.indexOf('-')));
				short id = ACC_CIM_ID;
				String cimString = propUniqueID.substring(0,
						propUniqueID.indexOf('-'));

				if(WIRED_ACC_STRING.equals(cimString)) {
					id = ACC_CIM_ID;
				} else if(WIRELESS_ACC_STRING.equals(cimString)){
					id = WIRELESS_ACC_CIM_ID;
				}

				long  uid = Long.decode(propUniqueID.substring(propUniqueID.indexOf('-') + 1));
				System.out.println(String.format("Trying to get: id %x uid %x", id, uid));
				if (id == (short) WIRELESS_ACC_CIM_ID)
				{
					wirelessCIM = true;
					return (CIMPortManager.getInstance().getWirelessConnection(id, uid));
				}
				wirelessCIM = false;
				return (CIMPortManager.getInstance().getConnection(id, uid));
			} catch (Exception e) {
				return null;
			}
		}
	}

	private void handleAccInputValuePacket(byte [] b)
	{
//		AstericsErrorHandling.instance.reportInfo(this, "handleAdcInputValuePacket start");

		int x=0,y=0,z=0,total=0;
		int offset = 0;
		if (wirelessCIM)
			offset = -1;

		x =  ((int) b[offset + 1]) & 0xff;
		x =	x | ((((int) b[offset + 2]) & 0xff) << 8);
		if (x>32767) x=x-65536;
		if (calibrateNow) calX=x;
		total=x*x;
		x-=calX;
		if (propDescreteSteps > 0) x=(x*propDescreteSteps)/16384;

		y =  ((int) b[offset + 3]) & 0xff;
		y =	y | ((((int) b[offset + 4]) & 0xff) << 8);
		if (y>32767) y=y-65536;
		if (calibrateNow) calY=y;
		total+=y*y;
		y-=calY;
		if (propDescreteSteps > 0) y=(y*propDescreteSteps)/16384;

		z =  ((int) b[offset + 5]) & 0xff;
		z =	z | ((((int) b[offset + 6]) & 0xff) << 8);
		if (z>32767) z=z-65536;
		if (calibrateNow) calZ=z;
		total+=z*z;
		z-=calZ;
		if (propDescreteSteps > 0) z=(z*propDescreteSteps)/16384;


		total = (int) Math.sqrt(total);
		switch (propAccelerationRange) {
		case 1: total*= 1.5; break;
		case 2: total*= 2; break;
		case 3: total*= 3; break;
		case 4: total*= 3; break;
		case 8: total*= 8; break;
		case 16: total*= 16; break;
		}

//		AstericsErrorHandling.instance.reportInfo(this, 
//				String.format("x: %x, y: %x, z: %x", x, y, z));
		
		outputPorts[0].sendData(x);												
		outputPorts[1].sendData(y);												
		outputPorts[2].sendData(z);												
		outputPorts[3].sendData(total);
		calibrateNow = false;
	}

	public synchronized void handlePacketReceived(CIMEvent e)
	{
		//        Logger.getAnonymousLogger().info("handlePacketReceived start");

		if (wirelessCIM && (e instanceof CIMWirelessDataEvent))
		{
			CIMWirelessDataEvent ev = (CIMWirelessDataEvent) e;
			handleAccInputValuePacket(ev.data);
		}
		else if(e instanceof CIMEventPacketReceived)
		{
			CIMEventPacketReceived ev = (CIMEventPacketReceived) e;
			CIMProtocolPacket packet = ev.packet;
	
			if (packet.getFeatureAddress() == ACC_FEATURE_INPUT_DATA)
			{
				handleAccInputValuePacket(packet.getData());
			}
		}
	}

	public void handlePacketError(CIMEvent e)
	{
		AstericsErrorHandling.instance.reportInfo(this, "Faulty packet received");
	}

	public class OutputPort extends DefaultRuntimeOutputPort
	{
		public void sendData(int data)
		{      	
			super.sendData(ConversionUtils.intToByteArray(data));
		}
	}

	final IRuntimeEventListenerPort elpStart = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (!wirelessCIM)
			{
				byte [] bytes = { (byte)1 };
				CIMPortManager.getInstance().sendPacket(port, bytes, ACC_FEATURE_AUTOSEND, 
						CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
			}
		}
	};

	final IRuntimeEventListenerPort elpStop = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (!wirelessCIM)
			{
				byte [] bytes = { (byte)0 };
				CIMPortManager.getInstance().sendPacket(port, bytes, ACC_FEATURE_AUTOSEND, 
						CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
			}
		}
	};
	final IRuntimeEventListenerPort elpCalibrate = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			calibrateNow = true;
		}
	};

}