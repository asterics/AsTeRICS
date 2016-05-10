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

package eu.asterics.component.sensor.openBCI;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;


//import eu.asterics.component.sensor.acceleration.AccelerationInstance.OutputPort;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.cimcommunication.*;

import java.util.*;
import java.util.logging.*;

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
public class openBCIInstance extends AbstractRuntimeComponentInstance 
{
	private final OutputPort opChannel1 = new OutputPort();
	private final OutputPort opChannel2 = new OutputPort();
	private final OutputPort opChannel3 = new OutputPort();
	private final OutputPort opChannel4 = new OutputPort();
	private final OutputPort opChannel5 = new OutputPort();
	private final OutputPort opChannel6 = new OutputPort();
	private final OutputPort opChannel7 = new OutputPort();
	private final OutputPort opChannel8 = new OutputPort();
	private final OutputPort opAccX = new OutputPort();
	private final OutputPort opAccY = new OutputPort();
	private final OutputPort opAccZ = new OutputPort();
	
	
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	private InputStream in = null;
	private OutputStream out = null;
	private Thread readerThread = null;
	private boolean running = false;
	String propComPort = "COM4";
	int propBaudRate = 115200;
	int state=0;
	byte [] array = new byte[50];
	int [] value = new int[8];
	int i = 0;
	
	
	// declare member variables here
	CIMPortController portController = null;
   /**
    * The class constructor.
    */
    public openBCIInstance()
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
		if ("Channel1".equalsIgnoreCase(portID))
			return opChannel1;
		if ("Channel2".equalsIgnoreCase(portID))
			return opChannel2;
		if ("Channel3".equalsIgnoreCase(portID))
			return opChannel3;
		if ("Channel4".equalsIgnoreCase(portID))
			return opChannel4;
		if ("Channel5".equalsIgnoreCase(portID))
			return opChannel5;
		if ("Channel6".equalsIgnoreCase(portID))
			return opChannel6;
		if ("Channel7".equalsIgnoreCase(portID))
			return opChannel7;
		if ("Channel8".equalsIgnoreCase(portID))
			return opChannel8;
		if ("AccX".equalsIgnoreCase(portID))
			return opAccX;
		if ("AccY".equalsIgnoreCase(portID))
			return opAccY;
		if ("AccZ".equalsIgnoreCase(portID))
			return opAccZ;

		return null;
	}

    /**
     * returns an Event Listener Port.
     * @param eventPortID   the name of the port
     * @return         the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {

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
		if ("comPort".equalsIgnoreCase(propertyName))
		{
			return propComPort;
		}
		if ("baudRate".equalsIgnoreCase(propertyName))
		{
			return propBaudRate;
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
		if ("comPort".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propComPort;
			propComPort = newValue.toString();
			return oldValue;
		}
		if ("baudRate".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propBaudRate;
			propBaudRate = Integer.parseInt(newValue.toString());
			return oldValue;
		}

        return null;
    }


	
    
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
	 * Parses a packet in P2-Format. 
	 
	void parsePacket()
	{
		try
		{
			int x = 2;
			for (int k = 0 ; k < 6; k++)
			{
				value[k]=(((int) array[x+1]) & 0xff) | ((((int) array[x]) & 0xff) << 8);
				
				// value[k] =(int)( (array[x] << 8) + array[x+1]);
	            x++;
	            x++;
			}

		 // System.out.println("Read values: Channel1 " + value[0] +",Channel2: " + value[1]+",Channel3: " + value[2]+",Channel4: " + value[3]+",Channel5: " + value[4]+",Channel6: " + value[5]);
		 opChannel1.sendData(value[0]-512);
		 opChannel2.sendData(value[1]-512);
		 opChannel3.sendData(value[2]-512);
		 opChannel4.sendData(value[3]-512);
		 opChannel5.sendData(value[4]-512);
		 opChannel6.sendData(value[5]-512);
		 opChannel7.sendData(value[5]-512);
		 opChannel8.sendData(value[5]-512);
		
		} 
		catch (Exception e)
		{
			// happens when Deque does not contain full packet yet
		}
	}
*/
    
    
	/**
	 * Called by the raw port controller if data is available
	 * @param ev a CIMEvent which can be ignored as it is only needed due to the
	 * interface specification

		  Packet Parser for OpenBCI:

		  https://github.com/OpenBCI/OpenBCI/wiki/Data-Format-for-OpenBCI-V3

		  3-byte signed integers are stored in 'big endian' format, MSB first.
		  The 3-byte values are converted to 4-byte signed longs during the unpacking.

		  Accelerometer data are 2-byte signed ints.  Also converted to signed longs.

		  Start Indicator: 0xA0
		  Frame number   : 1 byte  (advances on each packet)
		  Channel N data : 3 bytes per channel (repeats for ch 1-8, or ch 1-16)
		  ...
		  A_channel X    : 2 bytes (note these may also be instead, optional user data,
		  A_channel Y    : 2 bytes  replacing accelerometer values.)
		  A_channel Z    : 2 bytes
		  End Indcator:    0xC0
		
	 * 
	 */

    static int syncloss=0;
	static int bytecounter=0;
	static int channelcounter=0;
	static int tempval=0;
	static int framenumber=0;

    
    public void handlePacketReceived(byte actbyte)
	{
		//System.out.println("val = " + Integer.toHexString(0x000000ff & data) +" hex");


			switch (state) {

				// To better sync up when lost, look for two byte sequence.  It has happened
				// previously, that when data contains lots of A0's, sync could not reestablish.
				//
				case 0: if ((0x000000ff & actbyte) == 0xC0)			// look for end indicator
							state++;
						break;

				case 1:	if ((0x000000ff & actbyte) == 0xA0)		    // look for start indicator next
							state++;
						else
							state = 0;
						break;

				case 2:	if ((0x000000ff & actbyte) != framenumber) {
							syncloss++;
							// but go ahead and parse it anyway, 
						}
						framenumber = actbyte + 1;		// next expected frame number
						if (framenumber==256) framenumber=0;
						bytecounter=0;
						channelcounter=0;
						tempval=0;
						state++;
						break;

				case 3: // get channel values 
						tempval |= ((0x000000ff & actbyte) << (16 - (bytecounter*8)));		// big endian
						bytecounter++;
						if (bytecounter==3) {
							if ((tempval & 0x00800000) > 0) {
								tempval |= 0xFF000000;
							} else {
								tempval &= 0x00FFFFFF;
							}
							
							switch (channelcounter) {
								case 0: opChannel1.sendData(tempval);break;
								case 1: opChannel2.sendData(tempval);break;
								case 2: opChannel3.sendData(tempval);break;
								case 3: opChannel4.sendData(tempval);break;
								case 4: opChannel5.sendData(tempval);break;
								case 5: opChannel6.sendData(tempval);break;
								case 6: opChannel7.sendData(tempval);break;
								case 7: opChannel8.sendData(tempval);break;
							}
							
							channelcounter++;
							if (channelcounter==8) {  // all channels arrived !
								state++;
								bytecounter=0;
								tempval=0;
							}
							else { bytecounter=0; tempval=0; }
						}
						break;

				case 4: // get accelerometer XYZ
						tempval |= ((0xff & actbyte) << (8 - (bytecounter*8)));		// big endian
						bytecounter++;
						if (bytecounter==2) {
							if ((tempval & 0x00008000) > 0) {
								tempval |= 0xFFFF0000;
							} else {
								tempval &= 0x0000FFFF;
							}
							switch (channelcounter) {
								case 8: opAccX.sendData(tempval);break;
								case 9: opAccY.sendData(tempval);break;
								case 10: opAccZ.sendData(tempval);break;
							}
							channelcounter++;
							if (channelcounter==(11)) {  // all channels arrived !
								state++;
								bytecounter=0;
								channelcounter=0;
								tempval=0;
							}
							else { bytecounter=0; tempval=0; }
						}
						break;

				case 5: if ((0x000000ff & actbyte) == 0xC0)     // if correct end delimiter found:
						{
							state = 1;
						}
						else
						{
							syncloss++;
							state = 0;	// resync
						}
						break;

				default: state=0;  // resync
			}
		
	}	

	
     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  portController = CIMPortManager.getInstance().getRawConnection(propComPort,propBaudRate,true);
    	  
    	  if (portController == null) {
    		 AstericsErrorHandling.instance.reportError(this, 
    		 "openBCI-plugin: Could not construct raw port controller, please verify that the COM port is valid.");
    	  } else {
    		  in = portController.getInputStream();
    		  out = portController.getOutputStream();
    		  readerThread = new Thread(new Runnable() {

				@Override
				public void run() {
					running = true;
					while (running) {

						try { 
							if (in.available() > 0) {
								handlePacketReceived((byte) in.read());
							} else {
								Thread.sleep(10);
							}
						} catch (InterruptedException ie) {
							ie.printStackTrace();
						} catch (IOException io) {
							io.printStackTrace();
						}
					
					}
				}
    			  
    		  });
    		  readerThread.start();
    		  
          	try {
         		// out.write('f');
        		out.write('b');
   
        	} catch (Exception e) {};
    	  }
    	  super.start();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {	  
    	if (portController != null) {
  			CIMPortManager.getInstance().closeRawConnection(propComPort);
  			portController = null;
  			AstericsErrorHandling.instance.reportInfo(this, "openBCI raw port controller closed");
  		}
    	running = false;
        super.pause();
        
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
    	portController = CIMPortManager.getInstance().getRawConnection(propComPort, propBaudRate,true);
  		if (portController == null) {
  			AstericsErrorHandling.instance.reportError(this, "Could not construct openBCI raw port controller, please make sure that the COM port is valid.");
  		}
  		readerThread.start();
  		super.resume();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
        super.stop();
        if (portController != null) {
        	
        	try {
        		out.write('s');
        	} catch (Exception e) {};

        	CIMPortManager.getInstance().closeRawConnection(propComPort);
  			portController = null;
  			AstericsErrorHandling.instance.reportInfo(this, "openBCI connection closed");
  			running = false;
        }
      }
}