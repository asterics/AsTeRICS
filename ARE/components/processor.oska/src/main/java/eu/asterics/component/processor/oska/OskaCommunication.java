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
package eu.asterics.component.processor.oska;

import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;
import eu.asterics.mw.services.IRemoteConnectionListener;
import eu.asterics.mw.services.RemoteConnectionManager;
/**
 * OskaCommunication performs all the actions that are directly connected to 
 * the OSKA third party application. This includes opening and closing the 
 * connection, sending and receiving packets and providing methods to the other
 * classes of the package to expose the features of OSKA available throught the
 * TCP connection
 * @author Christoph Weiss [weissch@technikum-wien.at]
 *
 */
class OskaCommunication 
{
	/**
	 * Number of ms to wait after sending data to OSKA to ensure proper handling
	 * of commands
	 */
	private static final int OSKA_AFTER_SENT_WAIT = 100;

	/**
	 * Number of seconds to wait between last sending activity and sending of 
	 * keep alive packets 
	 */
	private static final long OSKA_KEEP_ALIVE_INTERVAL = 60;
	
	/**
	 * If true will append double quotes to strings sent to OSKA
	 */
	boolean useDoubleQuotes = true;
	
	private boolean connectionEstablished = false;
	private OskaConnectionListener connectionListener = null;
	Runnable keepAlive = null;
	int lastActivity = 0;
	
	int tcpPort = 0;

	/**
	 * Opens the connection to the OSKA application
	 * @param tcpPort the TCP port to connect to
	 * @return true if there is already a connection available on this TCP port,
	 * false otherwise which will result in the connection being opened and
	 * handled through the OSKA connection listener
	 */
	boolean openConnection(int tcpPort)
	{
		this.tcpPort = tcpPort;
    	connectionListener = new OskaConnectionListener();
    	if (RemoteConnectionManager.instance.requestConnection(
    			Integer.toString(tcpPort), connectionListener))
    	{
    		// connection to OSKA already existed 
    		connectionEstablished = true;
    		connectionListener.connectionEstablished();
    		return true;
    	}
    	else
    	{
    		// no existing OSKA connection, user should wait for connection 
    		// established message from listener
    		
    		return false;
    	}
	}
	
	/**
	 * Closes the connection to OSKA
	 */
	void closeConnection()
	{
		if (connectionEstablished)
		{
	        RemoteConnectionManager.instance
        	.closeConnection(Integer.toString(tcpPort));
	        connectionEstablished = false;
		}
	}
	
	class OskaConnectionKeepAliveThread implements Runnable 
	{
		@Override
		public void run() 
		{
			while (connectionEstablished)
			{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {	}
				
				if (OSKA_KEEP_ALIVE_INTERVAL <= lastActivity)
				{
					
					sendToOska("SIZE");
				}
				lastActivity++;
			}
			AstericsErrorHandling.instance.reportDebugInfo(OskaInstance.instance, 
					  "OskaConnectionKeepAliveThread ended");
		}
		
	};
	
	/**
	 * Implementation of the remote connection listener interface for the 
	 * communication with OSKA. This class handles all incoming packets as well
	 * as the set up and tear down of the connection.
	 * 
	 * @author weissch
	 *
	 */
	class OskaConnectionListener implements IRemoteConnectionListener
	{
		/**
		 * Constructs the listener
		 * @param owner the OSKA component instance owning the listener
		 */
		public OskaConnectionListener()
		{
		}
		
		/**
		 * Called once a connection is set up on a TCP port. Initiates the OSKA
		 * player, loads a keyboard file and positions the player window.
		 */
		public void connectionEstablished()
		{
			AstericsErrorHandling.instance.reportInfo(OskaInstance.instance, 
					String.format("Connection established via OSKA " +
							"connection listener"));
			connectionEstablished = true;

			try {
				Thread.sleep(250);  // increase if problems with initialization !
			} catch (InterruptedException e) { 	}
			
			OskaInstance.instance.initializeOska();
			if (keepAlive == null)
			{
				keepAlive = new OskaConnectionKeepAliveThread();
				AstericsThreadPool.instance.execute(keepAlive);
			}
		}
		
		/**
		 * Called when OSKA sends data to OSKA component. Handles all incoming
		 * packets. Special handling for SIZE and KEYBOARDLOADED command
		 */
		public void dataReceived(byte [] data)
		{
			AstericsErrorHandling.instance.getLogger().fine(
					String.format("OSKA connection listener: data: %s", 
							new String(data)));
			String input = new String(data).trim();
			
			OskaInstance.instance.commandManager.handleCommand(input);
			
		}
		
		/**
		 * Called when connection is lost
		 */
		public void connectionLost()
		{
			connectionEstablished = false;
			AstericsErrorHandling.instance.reportInfo(OskaInstance.instance, 
			  String.format("Connection has been lost claims OSKA connection " +
			  		"listener"));
		}
		
		/**
		 * Called after connection has been closed
		 */
		public void connectionClosed()
		{
			connectionEstablished = false;
			AstericsErrorHandling.instance.reportInfo(OskaInstance.instance, 
			  String.format("Connection closed via OSKA connection listener"));
		}
	}
	
	/**
	 * Sends message to OSKA. Will embed it between double quotes if the field 
	 * useDoubleQuotes holds true
	 * @param message the command to be sent to OSKA
	 * @return true if message could be sent, false otherwise
	 */
	boolean sendToOska(String message)
	{
		boolean ret = false;
		if (connectionEstablished)
		{
			if (useDoubleQuotes)
			{
				StringBuffer cmd = new StringBuffer();
				cmd.append("\"");
				cmd.append(message);
				cmd.append("\"");
				AstericsErrorHandling.instance.reportDebugInfo(
						OskaInstance.instance, 
						String.format("Sending to port %d: %s", tcpPort, 
								cmd.toString()));
				ret = RemoteConnectionManager.instance.writeData(
						Integer.toString(tcpPort), cmd.toString().getBytes());
			}
			else
				AstericsErrorHandling.instance.reportDebugInfo(
						OskaInstance.instance, 
						String.format("Sending to port %d: %s", tcpPort, 
								message));
				ret = RemoteConnectionManager.instance.writeData(
					Integer.toString(tcpPort), message.getBytes());
			try
			{
				Thread.sleep(OSKA_AFTER_SENT_WAIT);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
			lastActivity = 0;
		}
		return ret;
	}

	/**
	 * Sends press command with coordinates to OSKA
	 * @return true if successful, false otherwise
	 */
	boolean sendPressCommand() {
		//TODO change this back to simple press
//		return sendToOska(OskaInstance.instance
//				.highlighter.generatePressCommand());
		return sendToOska("Press");
	}
	
	/**
	 * Sets title on OSKA's title bar
	 * @return true if successful, false otherwise
	 */
	boolean setTitle(String title)
	{
		if (!title.trim().equals(""))
			return sendToOska("Title:" + title);
		return false;
	}

	/**
	 * Loads keyboard on OSKA
	 * @return true if successful, false otherwise
	 */
	boolean loadKeyboard(String kbdPath)
	{
		if (!kbdPath.trim().equals(""))
		{
			AstericsErrorHandling.instance.reportInfo(OskaInstance.instance, 
					String.format("Loading kbd: %s", kbdPath));
			
			return sendToOska("LoadKeyboard:" + kbdPath );
		}
		return false;
	}
	
	/**
	 * Sets the internal scanning speed of OSKA
	 * @return true if successful, false otherwise
	 */
	boolean setInternalScanSpeed(int speed)
	{
		return sendToOska("SetSpeed:" + speed);
	}
	
	/**
	 * Sets the position of the OSKA application
	 * @return true if successful, false otherwise
	 */
	boolean setPosition(int x, int y)
	{
		return sendToOska("Position:" + x + "," + y);
	}

	/**
	 * Sends close command to OSKA
	 * @return true if successful, false otherwise
	 */
	void closeOska() {
        sendToOska("Close");
		try {
			Thread.sleep(500);  // increase if problems with shutdown
		} catch (InterruptedException e) { 	}

	}

	boolean setSize(int width, int height) {
		return sendToOska("Size:" + width + "," + height);
	}
}
