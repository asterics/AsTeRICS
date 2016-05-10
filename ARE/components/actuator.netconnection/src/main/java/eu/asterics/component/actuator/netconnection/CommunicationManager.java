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

package eu.asterics.component.actuator.netconnection;


import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;
import eu.asterics.mw.model.runtime.IRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.data.ConversionUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;


/**
 * 
 * This class manages the Internet connection. It can run the client or server connection depend on the settings.
 *  
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Jun 04, 2012
 *         Time: 13:24:48 AM
 */
public class CommunicationManager {

	private Lock lock = new ReentrantLock();
	
	final int maxServerSessions=10;
	final int shortWait =5;
	final int longWait=250;
	final int dataWait=50;
	final int errorWait=50;
	final int serverWait=500;
	
	int timeCounter=0;
	
	boolean writeError=false;
	boolean outputReady=false;
	List<ServerConnection> serverSessions = new ArrayList<ServerConnection>();
	
	ProtocolService protocolService = null;//new ProtocolService();
	
	private int sessionID=0;
	
	private boolean connected=false;
	
	IRuntimeEventTriggererPort[] eventPorts=null;
	IRuntimeOutputPort[] integerPorts=null;
	IRuntimeOutputPort[] doublePorts=null;
	IRuntimeOutputPort[] stringPorts=null;
	
	
	/**
	 * The class constructor.
	 * @param eventPorts Array of the event ports
	 * @param integerPorts Array of the integer ports
	 * @param doublePorts Array of the double ports
	 * @param stringPorts Array of the string ports
	 */
	public CommunicationManager(IRuntimeEventTriggererPort[] eventPorts,IRuntimeOutputPort[] integerPorts,IRuntimeOutputPort[] doublePorts,IRuntimeOutputPort[] stringPorts)
	{
		this.integerPorts=integerPorts;
		this.doublePorts=doublePorts;
		this.stringPorts=stringPorts;
		this.eventPorts=eventPorts;
		
	}
	
	
	private boolean finish=false;
	Socket socket=null;
	ServerSocket serverSocket=null;
	ServerConnection serverConnection=null;
	
	private boolean clientConnection=false;
	private String IP="localhost";
	private int port = 21111;
	private boolean multisession=false;
	
	/**
	 * Starts communication work.
	 * @param clientConnection defines connection mode
	 * @param IP IP of the remote server
	 * @param port TCP port of the connection
	 * @param multisession if it is set to true, the server will handle up to maxServerSessions client connections
	 */
	public void start(boolean clientConnection,String IP,int port, boolean multisession)
	{
		this.clientConnection=clientConnection;
		this.IP=IP;
		this.port=port;
		finish=false;
		pause=false;
		this.multisession=multisession;
		
		connected=false;
		
		if(serverConnection!=null)
		{
			serverConnection.stopNow();
			serverConnection=null;
		}
		
		if(serverSessions.size()>0)
		{
			try
			{
				lock.lock();
				for(int i=0;i<serverSessions.size();i++)
				{
					serverSessions.get(i).stopNow();
				}
			
				serverSessions.clear();
			}finally
			{
				lock.unlock();
			}
		}
		
		if(clientConnection)
		{
			AstericsThreadPool.instance.execute(clientThread);
		}
		else
		{
			AstericsThreadPool.instance.execute(serverThread);
		}
		//AstericsThreadPool.instance.execute(this);
		//AstericsThreadPool.instance.execute(sender);
	}
	
	/**
	 * Stops work.
	 */
	public void stop()
	{
		finish=true;
	}
	
	private boolean pause=false;
	
	/**
	 * Sets or resets the pause state.
	 * @param pause pause state
	 */
	public void setPause(boolean pause)
	{
		this.pause=pause;
	}
	
	/**
	 * Removes the server connection with client.
	 * @param connection server connection to remove
	 * @param id of the server connection
	 */
	private synchronized void removeServerConnection(ServerConnection connection, int id)
	{
		if(multisession)
		{
			try
			{
				lock.lock();
				serverSessions.remove(connection);
			}
			finally
			{
				lock.unlock();
			}
		}
		else
		{
			
			serverConnection=null;
		}
	}
	
	
	
	/**
     * This method sends the command, if the plugin works as the client. 
     * @param command defines value type
     * @param port defines the port of the remote receiver.
     * @param doubleData double value
     * @param integerData integer value
     * @param stringData string value
     * @return result
     */
	private synchronized boolean sendClientCommand (Command command,int port,double doubleData,int integerData,String stringData)
	{
		if(connected)
		{
			SendError result =protocolService.SendCommand(command,port,doubleData,integerData,stringData);
			if(result==SendError.SendError)
			{
				writeError=true;
			}
			
			
			if(result==SendError.OK)
			{
				timeCounter=0;
				return true;
			}else{
				return false;
			}
		
		}
		
		return false;
	}
	
	/**
     * This method sends the command to the remote receiver. 
     * @param command defines value type
     * @param port defines the port of the remote receiver.
     * @param doubleData double value
     * @param integerData integer value
     * @param stringData string value
     * @return result
     */
	public synchronized boolean sendCommand(Command command,int port,double doubleData,int integerData,String stringData)
	{
		
		boolean result =false;
		boolean connectedToRemote=false;
		
		if(pause)
		{
			return false;
		}
		
		//AstericsErrorHandling.instance.getLogger().fine("Data to send: " + command + " port: " + port + " " + doubleData + " " + integerData + " " + stringData);
		
		if(clientConnection)
		{
			if(connected)
			{
				connectedToRemote=true;
				result=sendClientCommand(command, port, doubleData, integerData, stringData);
			}
		} 
		else
		{
			if(multisession)
			{
				result =true;
				boolean sent=false;
				for (int i=0;i<serverSessions.size();i++)
				{
					ServerConnection connection=serverSessions.get(i);
					
					
					if(connection.isConnected())
					{
						sent=true;
						connectedToRemote=true;
						result=result&&connection.sendCommand(command, port, doubleData, integerData, stringData);
					}
						
				}
				
				if(!sent)
				{
					result=false;
				}
			}
			else
			{
				if(serverConnection!=null)
				{
					if(serverConnection.isConnected())
					{
						connectedToRemote=true;
						result=serverConnection.sendCommand(command, port, doubleData, integerData, stringData);
					}
					
				}
			}
		}
		
		if(!result)
		{
			if(connectedToRemote)
			{
				AstericsErrorHandling.instance.getLogger().warning("There are problems during send command");
			}
		}
		
		return result;
	}
	
	/**
     * This method decodes the command data and executes commands.
     * @param ps ProtocolService object
     * @param command the command type
     * @param port command port number
     * @param data the command data
     * @return result
     */
	private synchronized boolean processCommand(ProtocolService ps,Command command,int port, byte[] data)
	{
		if(pause)
		{
			return false;
		}
		switch(command)
		{
		case Event:
		{
			if((port<1)||(port>eventPorts.length)){
				AstericsErrorHandling.instance.getLogger().warning("Wrong event port: " + port);
			}else{
				eventPorts[port-1].raiseEvent();
			}
							
			break;
		}
		case Integer:
		{
			if((data!=null)&&(data.length>0))
			{
				int integerValue = ps.getInteger(data);
			
				if((port<1)||(port>integerPorts.length)){
					AstericsErrorHandling.instance.getLogger().warning("Wrong integer port: " + port);
				}else{
				//AstericsErrorHandling.instance.getLogger().fine("integer value: " + integerValue);
					integerPorts[port-1].sendData(ConversionUtils.intToBytes(integerValue));
				}
			}
			
			break;
		}
		case Double:
		{
			if((data!=null)&&(data.length>0))
			{
				double doubleValue=ps.getDouble(data);
			
				if((port<1)||(port>doublePorts.length)){
					AstericsErrorHandling.instance.getLogger().warning("Wrong double port: " + port);
				}else{
				//AstericsErrorHandling.instance.getLogger().fine("double value: " + doubleValue);
					doublePorts[port-1].sendData(ConversionUtils.doubleToBytes(doubleValue));
				}
			}
			
			break;
		}
		case String:
		{
			
			if((data!=null)&&(data.length>0)){
				String string=ps.getString(data);
			
			
				if((port<1)||(port>stringPorts.length)){
					AstericsErrorHandling.instance.getLogger().warning("Wrong string port: " + port);
				}else{
					//AstericsErrorHandling.instance.getLogger().fine("string value: " + string);
					stringPorts[port-1].sendData(ConversionUtils.stringToBytes(string));
				}
			}
			else
			{
				stringPorts[port-1].sendData(ConversionUtils.stringToBytes(""));
			}
			
			
			break;
			
			
		}
		}
		
		return true;
	}
	
	/**
     * This method reads data from the remote sender.
     * @param ps ProtocolService object
     * @return error
     */
	private boolean readData(ProtocolService ps)
	{
		
		if(ps.checkHeaderAvailable())
		{
			HeaderInfo header = ps.getHeader();
			
			int dataSize=header.getDataSize();
			int port=header.getPort();
			Command command=header.getCommand(); 
			
			//AstericsErrorHandling.instance.getLogger().fine("Receive data; command: " + command + " port: " + port + " data size:" + dataSize);
			
			if(dataSize<0||port<0)
			{
				AstericsErrorHandling.instance.getLogger().warning("Wrong data");
				return true;
			}
			
			boolean dataReady=false;
			
			if(dataSize==0)
			{
				dataReady=true;
			}
			else
			{
				int repeats=6;
				
				do{
					dataReady=ps.checkDataAvailable(dataSize);
					repeats=repeats-1;
					if(finish)
					{
						repeats=0;
					}
					
					if(repeats<5)
					{
						try {
							Thread.sleep(dataWait);
						} catch (InterruptedException e) {
							
						}
					}
				}while((dataReady==false)&&(repeats>0));
			}
			
			/*if((command!=Command.None)&&(dataReady==true))
			{
		
			}*/
			
			if(dataReady)
			{
				byte[] data=null;
				if(dataSize>0)
				{
					data=ps.getData(dataSize);
				}
				
				processCommand(ps,command,port,data);
			}
		}
		
		return false;
	}
	
	private boolean serverSocketCreated=false;
	
	/**
     * Closes the server socket
     */
	private void closeServerSocket()
	{
		try {
			serverSocket.close();
			serverSocketCreated=false;
		} catch (IOException e) {
			AstericsErrorHandling.instance.getLogger().warning("Error server socket close");
		}
	}
	
	/**
     * This method creates the server socket.
     * @param oneTrial if is set there will be only one trial of creating socket.
     * @return result
     */
	private boolean createServerSocket(boolean oneTrial)
	{
		boolean connectionError=false;
		do{
			
			try {
				serverSocket=new ServerSocket(port);
				serverSocketCreated=true;
				InetAddress ipx = serverSocket.getInetAddress();
				AstericsErrorHandling.instance.getLogger().fine("connection ip: " + ipx.toString() + /*" " + getLocalIpAddress() +*/ " port: " + port);
			} catch (IOException e) {
				AstericsErrorHandling.instance.getLogger().warning("Server Thread error");
				connectionError=true;
					
			}
				
			if(!connectionError){
				try {
					serverSocket.setSoTimeout(5000);
				} catch (SocketException e) {
					closeServerSocket();
					connectionError=true;
					AstericsErrorHandling.instance.getLogger().warning("Server Thread error");
				}
			}
			
			if(connectionError==true)
			{
				try{
					Thread.sleep(errorWait);
				}catch (InterruptedException e) {}
			}
			
		}while((!finish)&&(connectionError==true)&&(!oneTrial));
		
		if(connectionError)
		{
			return false;
		}
		
		return true;
	}
	
	/**
     * Implements the main server thread.
     */
	private final Runnable serverThread = new Runnable(){
		/**
		 * The server method.
		 */
		@Override
		public void run() {
			
			
			boolean connectionError=false;
			
			createServerSocket(false);
			
			if(finish)
			{
				closeServerSocket();
				
				return;
			}
			
			boolean timeOut=false;
			boolean connected=false;
						
			if(!connectionError)
			{
			
				if(multisession){
				
					do
					{
						timeOut=false;
						if(serverSessions.size()>=maxServerSessions)
						{
							try {
								Thread.sleep(serverWait);
							} catch (InterruptedException e) {}
							if(serverSocketCreated)
							{
								closeServerSocket();
							}
						}
						else
						{
							if (serverSocketCreated) {
								try {
									Socket socket = serverSocket.accept();
									ServerConnection serverConnection = new ServerConnection(
											socket, ++sessionID);
									serverSessions.add(serverConnection);
									AstericsThreadPool.instance.execute(serverConnection);
									AstericsErrorHandling.instance.getLogger().fine("Server connected with client. ID: " + sessionID);
								} catch (InterruptedIOException e) {
									timeOut = true;
									//AstericsErrorHandling.instance.getLogger().warning("Server timeout");
								} catch (IOException e) {
									connectionError = true;
									AstericsErrorHandling.instance.getLogger().warning("Server Thread error");
								}
								if (connectionError) {
									try {
										Thread.sleep(serverWait);
									} catch (InterruptedException e) {
									}
								}
							}
							else
							{
								boolean result=createServerSocket(true);
								if(!result)
								{
									try {
										Thread.sleep(serverWait);
									} catch (InterruptedException e) {
									}
								}
							}
						}
						
					}while((finish==false));
				
				}else{
					/*if(serverConnection!=null)
					{
					}*/
					
					do
					{
						timeOut=false;
						if(serverConnection!=null)  //TODO
						{
							try {
								Thread.sleep(serverWait);
							} catch (InterruptedException e) {}
							
							if(serverSocketCreated){
								closeServerSocket();
							}
						}
						else
						{

							if(!serverSocketCreated){
								
								boolean result=createServerSocket(true);
								if(!result)
								{
									try {
										Thread.sleep(serverWait);
									} catch (InterruptedException e) {
									}
								}
								
							}else{
							
								try {
									Socket socket=serverSocket.accept();
									serverConnection=new ServerConnection(socket,0);
									AstericsThreadPool.instance.execute(serverConnection);
									AstericsErrorHandling.instance.getLogger().fine("Server connected with client");
									
								}catch (InterruptedIOException e) {
									timeOut=true;
									//AstericsErrorHandling.instance.getLogger().warning("Server timeout");
								} catch (IOException e) {
									connectionError=true;
									AstericsErrorHandling.instance.getLogger().warning("Server Thread error");
								}
								
								if(connectionError)
								{
									try {
										Thread.sleep(serverWait);
									} catch (InterruptedException e) {}
								}
							}
						}
						
					}while((finish==false));
					
				}
			}
			
			closeServerSocket();
			
			
		}
		
	};
	
	/**
     * Implements the client thread.
     */
	private final Runnable clientThread = new Runnable(){
		/**
		 * The client method.
		 */
		@Override
		public void run() {
			
			do{
				connected=false;
				boolean connectedError=false;
				
				do{
					connectedError=false;
					try {
						socket=new Socket();//(connectionIP,connectionPort);
						socket.connect(new InetSocketAddress(IP, port), 5000);
						AstericsErrorHandling.instance.getLogger().fine("Client Thread connected: " + IP + " : " + port);		
					} catch (UnknownHostException e) {
						connectedError=true;
						AstericsErrorHandling.instance.getLogger().warning("Connection fail ! " + e.getMessage());
					} catch(SocketTimeoutException e){
						connectedError=true;
						//AstericsErrorHandling.instance.getLogger().warning("time out " + e.getMessage());
					} catch (IOException e) {
						connectedError=true;
						AstericsErrorHandling.instance.getLogger().warning("Connection fail !! " + e.getMessage());
					}
				}while((finish==false)&&(connectedError==true));
				
				if(finish)
				{
					//socket close
					return;
				}
				
				connectedError=false;
				
				DataInputStream inputStream=null;
				DataOutputStream outputStream=null;
				
				try{
					inputStream = new DataInputStream(socket.getInputStream());
					outputStream = new DataOutputStream(socket.getOutputStream());
				}
				catch(IOException e){
					AstericsErrorHandling.instance.getLogger().warning("Stream error: "+e.getMessage());
					connectedError=true;
				}
				
				if(connectedError){
					try {
						socket.close();
					} catch (IOException e) {
						AstericsErrorHandling.instance.getLogger().warning("Error socket close");
					}catch(NullPointerException e ) {
						AstericsErrorHandling.instance.getLogger().warning("Error socket close null");
					}
					
					continue;
				}
				
				protocolService =new ProtocolService(inputStream,outputStream);
			
				
				boolean exit=false;
				connected=true;
				writeError=false;
				do {
					
					exit=readData(protocolService);
					
				
					
					if(protocolService.getHeaderReceived())
					{
						timeCounter=0;
						if(shortWait>0)
						{
							try{
								Thread.sleep(shortWait);
							}catch (InterruptedException e) {}
						}
					}
					else
					{
						timeCounter++;
						try{
							Thread.sleep(longWait);
						}catch (InterruptedException e) {}
					}
					
					
					if(timeCounter>35)
					{
						timeCounter=0;
						sendClientCommand(Command.Action,0,0,0,"");
					}
					
				} while ((!finish)&&(!exit)&&(!writeError));
				
				if(!finish)
				{
					AstericsErrorHandling.instance.getLogger().warning("Client Disconnected");
				}
				else
				{
					AstericsErrorHandling.instance.getLogger().fine("Client Disconnected");
				}
								
				connected=false;
				
				try {
					socket.close();
				} catch (IOException e) {
					AstericsErrorHandling.instance.getLogger().warning("Error socket close");
				}catch(NullPointerException e ) {
					AstericsErrorHandling.instance.getLogger().warning("Error socket close null");
				}
				
		
				
			}while(!finish);
			
			
		}
		
	};
	
	/**
     * Stores server connections
     */
	private class ServerConnection implements Runnable {
		
		private Socket socket=null;
		private int id=0;
		private ProtocolService ps=null;
		boolean writeError=false;
		int timeCounter=0;
		private boolean connected=false;
		
		DataInputStream inputStream=null;
		DataOutputStream outputStream=null;
		
		boolean endNow=false;
		
		int getID() {return id;}
		
		/**
		 * The class constructor.
		 * @param socket the socket of the connection
		 * @param id the connection id
		 */
		public ServerConnection(Socket socket,int id)
		{
			this.socket=socket;
			this.id=id;
			connected=false;
			endNow=false;
			
			try{
				inputStream = new DataInputStream(socket.getInputStream());
				outputStream = new DataOutputStream(socket.getOutputStream());
			}
			catch(IOException e){
				AstericsErrorHandling.instance.getLogger().warning("Stream error: "+e.getMessage());
			}
			
			ps=new ProtocolService(inputStream,outputStream);
		}
		
		/**
	     * This method sends the command to the remote receiver. 
	     * @param command defines value type
	     * @param port defines the port of the remote receiver.
	     * @param doubleData double value
	     * @param integerData integer value
	     * @param stringData string value
	     * @return result
	     */
		public synchronized boolean sendCommand(Command command,int port,double doubleData,int integerData,String stringData)
		{
			
			if(connected)
			{
				SendError result =ps.SendCommand(command,port,doubleData,integerData,stringData);
				if(result==SendError.SendError)
				{
					writeError=true;
				}
				
				
				if(result==SendError.OK)
				{
					timeCounter=0;
					return true;
				}else{
					return false;
				}
			
			}
			
			return false;
		}
		
		
		
		/**
	     * Returns true if the object is connected to the remote receiver. 
	     * @return true if the object is in the connected state
	     */
		public boolean isConnected()
		{
			return connected;
		}
		
		/**
	     * Stops the connection.
	     */
		public void stopNow()
		{
			endNow=true;
		}
		
		/**
	     * The server connection main thread.
	     */
		@Override
		public void run() {
			
			boolean exit=false;
			
			
			writeError=false;
			connected=true;
			endNow=false;
			
			do {
				
				exit=readData(ps);
				
				
				if(ps.getHeaderReceived())
				{
					timeCounter=0;
					if(shortWait>0)
					{
						try{
							Thread.sleep(shortWait);
						}catch (InterruptedException e) {}
					}
				}
				else
				{
					timeCounter++;
					try{
						Thread.sleep(longWait);
					}catch (InterruptedException e) {}
				}
				
				
				if(timeCounter>30)
				{
					timeCounter=0;
					sendCommand(Command.Action,0,0,0,"");
				}
				
				/*if((finish==true)||(exit==true)||(writeError==true))
				{
		
				}*/
				
			} while ((!finish)&&(!exit)&&(!writeError)&&(!endNow));
			
			if(!finish&&!endNow)
			{
				AstericsErrorHandling.instance.getLogger().warning("Server disconnected, id: "+ id);
			}else{
				AstericsErrorHandling.instance.getLogger().fine("Server disconnected, id: " + id);
			}
			
			connected=false;
			
			try {
				socket.close();
			} catch (IOException e) {
				AstericsErrorHandling.instance.getLogger().warning("Error socket close");
			}catch(NullPointerException e ) {
				AstericsErrorHandling.instance.getLogger().warning("Error socket close null");
			}
			
			
			if(!endNow)
			{
				removeServerConnection(this,id);
			}
		}
		
	}
	
	

}
