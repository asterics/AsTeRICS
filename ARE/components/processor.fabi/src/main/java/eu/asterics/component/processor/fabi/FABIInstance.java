

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

package eu.asterics.component.processor.fabi;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import eu.asterics.mw.cimcommunication.CIMPortController;
import eu.asterics.mw.cimcommunication.CIMPortManager;
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
 *         Time: 
 */
public class FABIInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opList = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opID = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	double propStepsize = 3;
	int propCOMPort = 1;
	CIMPortController portController = null;
	private InputStream in = null;
	private OutputStream out = null;
	private boolean running = false;
	String message = "";
	String receivedMessage = "";
	boolean errorEEPROM = false;
	private static boolean messageReceived = false;
	private static int timeout = 0;
	
	String strSlotSaveName, strSlotLoadName, strText, strKey;
	Integer intButtonMode, intMoveMouseX, intMoveMouseY;
	boolean listReceiver = false;
	Thread readThread = null;

  
    
   /**
    * The class constructor.
    */
    public FABIInstance()
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
		if ("buttonMode".equalsIgnoreCase(portID))
		{
			return ipButtonMode;
		}
		if ("slotSaveName".equalsIgnoreCase(portID))
		{
			return ipSlotSaveName;
		}
		if ("slotLoadName".equalsIgnoreCase(portID))
		{
			return ipSlotLoadName;
		}
		if ("moveMouseX".equalsIgnoreCase(portID))
		{
			return ipMoveMouseX;
		}
		if ("moveMouseY".equalsIgnoreCase(portID))
		{
			return ipMoveMouseY;
		}
		if ("text".equalsIgnoreCase(portID))
		{
			return ipText;
		}
		if ("key".equalsIgnoreCase(portID))
		{
			return ipKey;
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
		if ("list".equalsIgnoreCase(portID))
		{
			return opList;
		}
		if ("iD".equalsIgnoreCase(portID))
		{
			return opID;
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
		if ("iD".equalsIgnoreCase(eventPortID))
		{
			return elpID;
		}
		if ("buttonMode".equalsIgnoreCase(eventPortID))
		{
			return elpButtonMode;
		}
		if ("clickLeft".equalsIgnoreCase(eventPortID))
		{
			return elpClickLeft;
		}
		if ("clickRight".equalsIgnoreCase(eventPortID))
		{
			return elpClickRight;
		}
		if ("clickDoubleLeft".equalsIgnoreCase(eventPortID))
		{
			return elpClickDoubleLeft;
		}
		if ("clickMiddle".equalsIgnoreCase(eventPortID))
		{
			return elpClickMiddle;
		}
		if ("pressLeft".equalsIgnoreCase(eventPortID))
		{
			return elpPressLeft;
		}
		if ("pressRight".equalsIgnoreCase(eventPortID))
		{
			return elpPressRight;
		}
		if ("pressMiddle".equalsIgnoreCase(eventPortID))
		{
			return elpPressMiddle;
		}
		if ("releaseLeft".equalsIgnoreCase(eventPortID))
		{
			return elpReleaseLeft;
		}
		if ("releaseRight".equalsIgnoreCase(eventPortID))
		{
			return elpReleaseRight;
		}
		if ("releaseMiddle".equalsIgnoreCase(eventPortID))
		{
			return elpReleaseMiddle;
		}
		if ("wheelUp".equalsIgnoreCase(eventPortID))
		{
			return elpWheelUp;
		}
		if ("wheelDown".equalsIgnoreCase(eventPortID))
		{
			return elpWheelDown;
		}
		if ("moveMouseX".equalsIgnoreCase(eventPortID))
		{
			return elpMoveMouseX;
		}
		if ("moveMouseY".equalsIgnoreCase(eventPortID))
		{
			return elpMoveMouseY;
		}
		if ("keyWrite".equalsIgnoreCase(eventPortID))
		{
			return elpKeyWrite;
		}
		if ("keyPress".equalsIgnoreCase(eventPortID))
		{
			return elpKeyPress;
		}
		if ("keyRelease".equalsIgnoreCase(eventPortID))
		{
			return elpKeyRelease;
		}
		if ("keyReleaseAll".equalsIgnoreCase(eventPortID))
		{
			return elpKeyReleaseAll;
		}
		if ("save".equalsIgnoreCase(eventPortID))
		{
			return elpSave;
		}
		if ("load".equalsIgnoreCase(eventPortID))
		{
			return elpLoad;
		}
		if ("list".equalsIgnoreCase(eventPortID))
		{
			return elpList;
		}
		if ("next".equalsIgnoreCase(eventPortID))
		{
			return elpNext;
		}
		if ("clear".equalsIgnoreCase(eventPortID))
		{
			return elpClear;
		}
		if ("idle".equalsIgnoreCase(eventPortID))
		{
			return elpIdle;
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
		if ("stepsize".equalsIgnoreCase(propertyName))
		{
			return propStepsize;
		}
		if ("COMPort".equalsIgnoreCase(propertyName))
		{
			return propCOMPort;
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
		if ("stepsize".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propStepsize;
			propStepsize = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("COMPort".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propCOMPort;
			propCOMPort = Integer.parseInt(newValue.toString());
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipButtonMode  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
			intButtonMode = ConversionUtils.intFromBytes(data);
				 // myVar = ConversionUtils.intFromBytes(data); 
		}
	};
	private final IRuntimeInputPort ipSlotSaveName  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
			strSlotSaveName = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data); 
		}
	};
	private final IRuntimeInputPort ipSlotLoadName  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
			strSlotLoadName = ConversionUtils.stringFromBytes(data); 
			// = ConversionUtils.intFromBytes(data); 
		}
	};
	private final IRuntimeInputPort ipMoveMouseX  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
			intMoveMouseX = ConversionUtils.intFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data); 
		}
	};
	private final IRuntimeInputPort ipMoveMouseY  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
			intMoveMouseY = ConversionUtils.intFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data); 
		}
	};
	private final IRuntimeInputPort ipText  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
			strText = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data); 
		}
	};
	private final IRuntimeInputPort ipKey  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
			strKey = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data); 
		}
	};


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpID = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			messageReceived = false;
			SetCommand("AT ID");
			String id = checkMessageReceived();
			opID.sendData(ConversionUtils.stringToBytes(id));
		}
	};
	final IRuntimeEventListenerPort elpButtonMode = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT BM " + intButtonMode);
		}
	};
	final IRuntimeEventListenerPort elpClickLeft = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT CL");
		}
	};
	final IRuntimeEventListenerPort elpClickRight = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT CR"); 
		}
	};
	final IRuntimeEventListenerPort elpClickDoubleLeft = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT CD");
		}
	};
	final IRuntimeEventListenerPort elpClickMiddle = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT CM");
		}
	};
	final IRuntimeEventListenerPort elpPressLeft = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT PL");
		}
	};
	final IRuntimeEventListenerPort elpPressRight = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT PR"); 
		}
	};
	final IRuntimeEventListenerPort elpPressMiddle = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT PM");
		}
	};
	final IRuntimeEventListenerPort elpReleaseLeft = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT RL");
		}
	};
	final IRuntimeEventListenerPort elpReleaseRight = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT RR"); 
		}
	};
	final IRuntimeEventListenerPort elpReleaseMiddle = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT RM");
		}
	};
	final IRuntimeEventListenerPort elpWheelUp = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT WU");
		}
	};
	final IRuntimeEventListenerPort elpWheelDown = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT WD"); 
		}
	};
	final IRuntimeEventListenerPort elpMoveMouseX = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT MX " + intMoveMouseX); 
		}
	};
	final IRuntimeEventListenerPort elpMoveMouseY = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT MY " + intMoveMouseY);
		}
	};
	final IRuntimeEventListenerPort elpKeyWrite = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT KW " + strText);
		}
	};
	final IRuntimeEventListenerPort elpKeyPress = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT KP " + strKey);
		}
	};
	final IRuntimeEventListenerPort elpKeyRelease = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT KR " + strKey);
		}
	};
	final IRuntimeEventListenerPort elpKeyReleaseAll = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT RA");
		}
	};
	final IRuntimeEventListenerPort elpSave = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT SA " + strSlotSaveName);
		}
	};
	final IRuntimeEventListenerPort elpLoad = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT LO " + strSlotLoadName);
		}
	};
	final IRuntimeEventListenerPort elpList = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			listReceiver = true;
			messageReceived = false;
			SetCommand("AT LI");
			
			String helper = checkMessageListReceived();
			String list = "";
			while(receivedMessage != null)
			{
				list = helper;
				helper = checkMessageListReceived();
			}
			opList.sendData(ConversionUtils.stringToBytes(list));
			listReceiver = false;
		}
	};
	final IRuntimeEventListenerPort elpNext = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT NE");
		}
	};
	final IRuntimeEventListenerPort elpClear = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT DE");
		}
	};
	final IRuntimeEventListenerPort elpIdle = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			SetCommand("AT NC");
		}
	};

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
  		if (!openCOMPort()) {
			return;
		}
  		
  		SetCommand("AT WS " + (int) propStepsize);
          super.start();
      }
      
	  	private boolean openCOMPort() {
			portController = CIMPortManager.getInstance().getRawConnection(
					"COM" + propCOMPort, 9600, true);
	
			if (portController == null) {
				System.out
						.println("Fabi: Could not construct raw port controller, please verify that the COM port is valid.");
				return false;
			} else {
				System.out.println("COM" + propCOMPort + " Port open!");
				in = portController.getInputStream();
				out = portController.getOutputStream();
				readThread = new Thread(new Runnable() {
					@Override
					public void run() {
						running = true;
						while (running) {
	
							try {
								if(in.available() > 0)
								{									
									if (in.available() > 0) {
										handlePacketReceived((byte)in.read());
									}
									else
									{
										Thread.sleep(1);
									}
								}
							} catch (IOException io) {
								io.printStackTrace();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				});
	
				readThread.start();
			}
			return true;
		}
	  	
		public void handlePacketReceived(byte data) {
			System.out.print((char) data);
			message += (char) data;

			if ((char) data == '\n') {
				receivedMessage = message;
				messageReceived = true;
				if(!listReceiver)
					message = "";
			}

		}
		
		private String checkMessageReceived() {
			if (messageReceived) {
				messageReceived = false;
				return null;
			}

			while (!messageReceived) {
				timeout++;
				if (timeout == 50) {
					System.out.println("no data within 0.5 seconds received");
					timeout = 0;
					messageReceived = true;
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			timeout = 0;
			messageReceived = false;
			return receivedMessage;
		}
		
		private String checkMessageListReceived() {
			while (!messageReceived) {
				timeout++;
				if (timeout == 50) {
					System.out.println("no data within 0.5 seconds received");
					timeout = 0;
					messageReceived = true;
					receivedMessage = null;
					message = "";
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			timeout = 0;
			messageReceived = false;
			return receivedMessage;
		}
		
		
		public void SetCommand(String command) {
			if (portController == null)
				return;
			
			System.out.println(command);
			try {
				out.write(ConversionUtils.stringToBytes(command + "\n"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
  		if(portController!=null)
  		{
  			CIMPortManager.getInstance().closeRawConnection("COM" + propCOMPort);
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
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
  		if(portController!=null)
  		{
  			CIMPortManager.getInstance().closeRawConnection("COM" + propCOMPort);
  		}
		if(readThread != null)		
		{
			running = false;
			try {
				readThread.join(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        super.stop();
      }
}