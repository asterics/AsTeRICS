package eu.asterics.mw.services;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

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



/**
 * Responsible for managing connections with external components
 * @author Costas
 *
 */
public class RemoteConnectionManager {


	public static final RemoteConnectionManager instance = 
		new RemoteConnectionManager();
	private static HashMap<String, SenderThread> portsToSenderThread = 
		new HashMap<String, SenderThread>();
	private static HashMap<String, SetupThread> portsToSetupThread = 
		new HashMap<String, SetupThread>();

	private static HashMap<String, Socket> portsToSockets = 
		new HashMap<String, Socket>();
	private final AstericsThreadPool threadPool = AstericsThreadPool.instance;
	
	private Logger logger = null;
	
	/**
	 * Constructs the remote connection manager and stores a reference to the 
	 * logger.
	 */
	private RemoteConnectionManager()
	{
		super();
		logger = AstericsErrorHandling.instance.getLogger();
	}
	
	/**
	 * This function will initiate a socket connection to a specified port 
	 * number and registers the connection listener with it. If a connection 
	 * exists the new connection listener is registered with it.
	 * @param port the port number as a String
	 * @param iRemoteConListener the listener implementation to be informed 
	 * about incoming data
	 * @returns false if no connection is available caller should wait for 
	 * feedback from connection listener, true if connection is available and 
	 * new listener has been registered.
	 */
	public synchronized boolean requestConnection (String port, IRemoteConnectionListener 
			iRemoteConListener)
	{
		SetupThread setupT = RemoteConnectionManager.portsToSetupThread.get(port);
		if (setupT == null)
		{
			setupT = new SetupThread (port, iRemoteConListener);
			threadPool.execute(setupT);
			return false;
		}
		else
		{
			SenderThread sthread=RemoteConnectionManager.portsToSenderThread.get(port);
			setupT.setConnectionListener(iRemoteConListener);
			sthread.setConnectionListener(iRemoteConListener);
		}
		return true;
	}
	
	/**
	 * This function sends the specified byte array to the sender thread. Return 
	 * from function does not mean that data has been sent.   
	 * @param port port number as a String
	 * @param data byte array holding the data to be sent
	 * @returns true if data could be added to queue to sender thread, 
	 * false if failed.
	 * 
	 */
	public boolean writeData(String port, byte[] data)
	{
		SenderThread sthread=RemoteConnectionManager.portsToSenderThread.get(port);
		
		if (sthread != null)
		{
			return sthread.writeData(data);
		}
		return false;
	}
	
	/**
	 * Closes connection on specified port and causes all related threads to 
	 * end.
	 * @param port port number as a String
	 */
	public void closeConnection(String port)
	{
		Socket socket=RemoteConnectionManager.portsToSockets.remove(port);
		if (socket != null)
		{
			try {
				socket.close();
				RemoteConnectionManager.portsToSenderThread.remove(port);
				RemoteConnectionManager.portsToSetupThread.remove(port);
			} catch (IOException e) {
				logger.warning(this.getClass().getName()+".closeConnection: " +
						"Socket connection on port "+port+" cannot be closed -> \n"+
						e.getMessage());
			}
		}
		else
		{
			logger.warning(this.getClass().getName()+".closeConnection: " +
					"Socket on port "+port+" does not exist,ignoring close request");
		}
	}

	/**
	 * The setup thread waits for an incoming connection on the specified
	 * socket. Upon connection the thread will set up the necessary streams,
	 * create a sender thread and then become the thread to receive incoming
	 * data from the other socket connection
	 * @author weissch
	 *
	 */
	private class SetupThread implements Runnable {

		ServerSocket proxyServerSocket=null;
		
		private String port;
		private IRemoteConnectionListener IRemoteConListener;
		DataInputStream proxyInputStream;

		/**
		 * Constructs the thread, stores port number and the connected listener
		 * interface
		 * @param port
		 * @param IRemoteConListener
		 */
		public SetupThread (String port, IRemoteConnectionListener 
				IRemoteConListener)
		{
			this.port=port;
			this.IRemoteConListener=IRemoteConListener;

		}
		
		/**
		 * Replaces the attached connection listenere
		 * @param IRemoteConListener
		 */
		public synchronized void setConnectionListener( IRemoteConnectionListener 
				IRemoteConListener)
		{
			this.IRemoteConListener=IRemoteConListener;
		}
		
		/**
		 * Sets up the connection and then listens for incoming data
		 */
		@Override
		public void run() {
			Socket proxyClientSocket=null;
			try {
				proxyServerSocket = new ServerSocket(Integer.valueOf(port));
				proxyClientSocket = proxyServerSocket.accept();
				//Remote Client connected
				
				//Store the port and socket in a mapping
				RemoteConnectionManager.portsToSockets.
											put(port, proxyClientSocket);
				

				//Start the sender thread
				SenderThread senderT = new SenderThread (proxyClientSocket, 
															IRemoteConListener);
				threadPool.execute(senderT);
				RemoteConnectionManager.portsToSenderThread.put(port, senderT);
				RemoteConnectionManager.portsToSetupThread.put(port, this);
				
				proxyServerSocket.close();
				
				//now read the remote client data
				proxyInputStream = 
					new DataInputStream(proxyClientSocket.getInputStream());
				byte[] buffer = new byte[256];

				IRemoteConListener.connectionEstablished();
				
				int len=0;
				while((len=proxyInputStream.read(buffer)) != -1)
				{
					if (len != 0)
					{
						// String act_input = new String(buf);
						//System.out.flush();
			            // System.out.println("\ngot input:"+act_input);
						//System.out.flush();
						
						IRemoteConListener.dataReceived(Arrays.copyOf(buffer, len));	
//						Arrays.fill(buffer, (byte) 0);
					}
				}

				IRemoteConListener.connectionClosed();
			} catch (IOException e) {
				logger.warning(this.getClass().getName()+".run: " +
						"Socket connection on port "+port+" closed -> \n"+
						e.getMessage());
				IRemoteConListener.connectionLost();
			}
		}
	}

	/**
	 * The sender thread reads from a blocking queue and transfers data to the
	 * other end of the connection if available.
	 * @author weissch
	 *
	 */
	private class SenderThread implements Runnable {

		LinkedBlockingQueue<byte []> outgoingCommandQueue = 
											new LinkedBlockingQueue<byte []>();
		private IRemoteConnectionListener 
		IRemoteConListener;
		private OutputStream os;
		boolean threadRunning=true;
		
		/**
		 * Retrieves streams from socket and sets up sender thread
		 * @param socket
		 * @param IRemoteConListener
		 */
		public SenderThread (Socket socket, IRemoteConnectionListener 
				IRemoteConListener)
		{
			this.IRemoteConListener=IRemoteConListener;
			try
			{
				os = socket.getOutputStream();
			}
			catch (IOException e)
			{
				logger.warning(this.getClass().getName()+".SenderThread: " +
						"Connection closed -> \n"+e.getMessage());
				IRemoteConListener.connectionClosed();
			}
		}
		
		/**
		 * Replaces connected listener interface
		 * @param IRemoteConListener
		 */
		public synchronized void setConnectionListener( IRemoteConnectionListener 
				IRemoteConListener)
		{
			this.IRemoteConListener=IRemoteConListener;
		}
		
		/**
		 * Places data to be transferred in to outgoing command queue
		 * @param data
		 * @return
		 */
		public boolean writeData(byte[] data)
		{
			return outgoingCommandQueue.add(data);
		}
		
		
		/**
		 * Waits for data in command queue and transfer data if available
		 */
		@Override
		public void run() {
			while (threadRunning)
			{
				try
				{
					byte [] data = outgoingCommandQueue.take();
					os.write(data);
				}
				catch (InterruptedException e)
				{
					logger.warning(this.getClass().getName()+".run: " +
							"Socket connection closed -> \n"+
							e.getMessage());
					IRemoteConListener.connectionLost();
					
					threadRunning = false;
				}
				catch (IOException e)
				{
					logger.warning(this.getClass().getName()+".run: " +
							"Socket connection closed -> \n"+
							e.getMessage());
					IRemoteConListener.connectionClosed();
					threadRunning = false;
				}
			}
		}

	}
}
