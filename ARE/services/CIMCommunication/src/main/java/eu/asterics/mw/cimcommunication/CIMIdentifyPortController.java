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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */

package eu.asterics.mw.cimcommunication;

import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.TooManyListenersException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

class CIMIdentifyPortController extends CIMPortController 
implements Runnable {

	// local definitions
	enum SearchState 
	{
		SEARCH_START, 
		DETECTING_CIM, 
		FOUND_CIM, 
		DETECTING_ZIGBEE,  
		FOUND_ZIGBEE
	}

	private static final long PACKET_DETECT_TIMEOUT = 3000;

	BlockingQueue<Byte> dataSource = null;
	SerialPort port;
	long timeOfCreation;
	final int BAUD_RATE = 115200;
	private boolean threadRunning = true;
	private boolean threadEnded = false;
	private boolean identifiedCIM = false;
	private boolean connectionLost;

	private String name;

	private InputStream inputStream;
	private SerialPortEventListener eventListener;
	private long lastPacketSent;    


	public String getName() {
		return name;
	}


	/**
	 * Creates the port controller from the COM port identifier for an 
	 * available COM port.
	 * @param portIdentifier
	 * @throws CIMException 
	 */
	CIMIdentifyPortController(CommPortIdentifier portIdentifier) throws CIMException
	{
		super(portIdentifier.getName());
		name = "Identify" + portIdentifier.getName();

		dataSource = new LinkedBlockingQueue<Byte>(); 

		try {     
			port = (SerialPort) portIdentifier.open(
					this.getClass().getName() + comPortName, 2000);

			logger.fine(this.getClass().getName()+".CIMIdentifyPortController:" 
					+" Opened serial port " + comPortName);
			port.setSerialPortParams(BAUD_RATE, SerialPort.DATABITS_8, 
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			inputStream = port.getInputStream();   
			eventListener = new CIMPortEventListener(inputStream, dataSource);
			port.addEventListener(eventListener);
			port.notifyOnDataAvailable(true);
			timeOfCreation = System.currentTimeMillis();
		} 
		catch (UnsupportedCommOperationException ucoe) 
		{     
			logger.severe(this.getClass().getName()+"." +
					"CIMSerialPortController: Could not set serial port " +
					"parameters -> \n" + ucoe.getMessage());
			port.close();
			throw new CIMException();
		} 
		catch (PortInUseException piue) 
		{
			logger.warning(this.getClass().getName()+"." +
					"CIMSerialPortController: "+
					String.format("Port %s already in use", comPortName)+
					" -> \n" + piue.getMessage());
			throw new CIMException();
		}   
		catch (IOException ioe)
		{
			logger.severe(this.getClass().getName()+"." +
					"CIMSerialPortController: Could not get input stream" +
					" -> \n" + ioe.getMessage());
			port.close();
			throw new CIMException();
		} 
		catch (TooManyListenersException tmle) 
		{
			logger.warning(this.getClass().getName()+"." +
					"CIMSerialPortController: "+
					String.format("Too many listeners on port %s", comPortName)+ 
					" -> \n" + tmle.getMessage());
			throw new CIMException();
		}
	}


	@Override
	void closePort() {
		threadRunning = false;
		while (!threadEnded) Thread.yield();
		
		
	}

	/**
	 * The main packet receive loop for a COM port. This permanently reads data
	 * from the blocking queue between the serial port and the serial port 
	 * controller which detaches the serial receiver thread from the packet 
	 * parsing task. Also performs the auto detection of the CIM. 
	 */
	@Override
	public void run() 
	{
		SearchState searchState =  SearchState.SEARCH_START;
		CIMProtocolPacket packet = null;

		connectionLost=false;

		AstericsThreadPool.instance.execute(
			new Runnable()
			{

				@Override
				public void run() {
					logger.fine(this.getClass().getName()+".run: Identify packet " +
							"injector thread started for " +
					"port " + comPortName + "\n");

					while (threadRunning)
					{
						
				    	try
				        {
				        	Thread.sleep(500);
				        }
				        catch (InterruptedException e)
				        {  	e.printStackTrace();   }
				    	
				    	sendPacket(null, 
				    			CIMProtocolPacket.FEATURE_UNIQUE_SERIAL_NUMBER, 
				    			CIMProtocolPacket.COMMAND_REQUEST_READ_FEATURE, false);
					}
					logger.fine(this.getClass().getName()+
							".run: injector thread ended on port " 
								+ comPortName +"\n");
					
				}
				
			}
		);
		

		// near endless loop
		while (threadRunning)
		{
			try {
				// wait for next byte in queue
				Byte b = dataSource.poll(1000L, TimeUnit.MILLISECONDS);
				if (System.currentTimeMillis() - timeOfCreation > PACKET_DETECT_TIMEOUT)
				{
					logger.fine(this.getClass().getName()+".run: Did not " +
							"receive identifiable CIM protocol packet during " +
					"identification phase on port " + comPortName + "\n");
					threadRunning = false;
				}

				if (b != null)
				{
//                	System.out.println(String.format("Port: " + comPortName + " Recv: 0x%2x ('%c')", b, b));					
					switch (searchState)
					{
					case SEARCH_START:
						if (b == '@')
						{
							searchState = SearchState.DETECTING_CIM;
						}
						break;
					case DETECTING_CIM:
						if (b == 'T')
						{
							// found the packet header, start parsing the packet content
							searchState = SearchState.FOUND_CIM;
							packet = new CIMProtocolPacket();
//							System.out.println("Created new packet");
						}
						else
						{
							searchState = SearchState.SEARCH_START;
						}

						break;
					case FOUND_CIM:
						synchronized (this)
						{
							if (packet.parsePacket(b)) 
							{
								if (packet.getFeatureAddress() == 
									CIMProtocolPacket.FEATURE_UNIQUE_SERIAL_NUMBER &&
									(packet.getData() != null)	)
								{
									// finished parsing the packet
									short cimId = packet.getAreCimID();
									long uid = 0;
									byte [] data = packet.getData(); 
									for (int i = 3; i >= 0 ; i--)
										uid = (uid << 8) | (((int)data[i]) & 0xff);
	
									CIMUniqueIdentifier cuid 
									= new CIMUniqueIdentifier(cimId, uid);
									
									logger.fine(this.getClass().getName()+ 
											".run: Thread " + comPortName + 
											": CIM identified as " + cuid.toString());
									
									
									if (cimId == 0xa01) 
									{
										CIMWirelessHubPortController ctrl = new CIMWirelessHubPortController(comPortName, port, (CIMPortEventListener) eventListener);
										ctrl.cuid = cuid;
										ctrl.setNextExpectedIncomingSerialNumber((byte) (packet.getSerialNumber() + 1));
										CIMPortManager.getInstance().addWirelessConnection(ctrl);
										AstericsThreadPool.instance.execute(ctrl);
									}
									else
									{
										CIMSerialPortController ctrl = 
											new CIMSerialPortController(comPortName, port, (CIMPortEventListener) eventListener);
										ctrl.cuid = cuid;
										ctrl.setNextExpectedIncomingSerialNumber((byte) (packet.getSerialNumber() + 1));
		
										CIMPortManager.getInstance().addConnection(cuid, ctrl);
										AstericsThreadPool.instance.execute(ctrl);
									}
									threadRunning = false;
									identifiedCIM = true;
								}
								searchState = SearchState.SEARCH_START;
							}
							break;
						}
					}
				} // if (b != null)
				Thread.yield();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.fine(this.getClass().getName()+".run: Identifier thread " 
				+ comPortName +	" main loop ended, cleaning up \n");

		if (!identifiedCIM)
		{
			// thread ends, clean up
			port.notifyOnDataAvailable(false);
			port.removeEventListener();
			synchronized (eventHandlers)  
			{
				eventHandlers.clear();
			}
			try 
			{
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}		
			port.close();
		}

		logger.fine(this.getClass().getName()+".run: Identifier thread on " +
				"serial port " + comPortName + " ended \n");

		threadEnded = true;
	}

	@Override
	synchronized byte sendPacket(byte[] data, short featureAddress,
			short requestCode, boolean crc) {
		byte ret = -1;
		if (threadRunning)
		{
			CIMProtocolPacket packet = new CIMProtocolPacket();
			packet.useCrc(crc);
			packet.setAreCimID(areVersion);
			packet.setSerialNumber(serialNumber);
			packet.setFeatureAddress(featureAddress);
			packet.setRequestReplyCode(requestCode);
			packet.setData(data);

			try {
				port.getOutputStream().write(packet.toBytes());
				port.getOutputStream().flush();
				port.getOutputStream().close();
			} 
			catch (IOException ioe) 
			{
				if (connectionLost == false)
				{
					logger.severe(this.getClass().getName()+".sendPacket: could " +
							"not send packet #" + serialNumber + ", " + 
							packet.toString() + " on port " + comPortName 
							 + " (if port related to Windows Bluetooth stack, " 
							 + "ignore error -> \n" + ioe.getMessage());
					connectionLost=true;
				}
				return -1;
			}
			catch (NullPointerException npe) 
			{
				logger.severe(this.getClass().getName()+".sendPacket: " +
						"NullPointerException trying to send packet #" + 
						serialNumber + " -> \n" + npe.getMessage());
				return -1;
			}

			lastPacketSent = System.currentTimeMillis();

			ret = serialNumber;
			if (serialNumber == 127)
			{
				serialNumber = 0;
			}
			else
			{
				serialNumber++;
			}
		}
		else
		{
			logger.warning(this.getClass().getName()+".sendPacket: " +
			"sendPacket called while thread was set to end \n");
		}
		return ret;
	}

}
