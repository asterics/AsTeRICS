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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.actuator.androidphonecontrol;


import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.data.ConversionUtils;

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
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.osgi.framework.debug.Debug;

/**
 * 
 * This class manages the Internet connection. It can run the client or server connection depend on the settings.
 *  
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Jun 04, 2012
 *         Time: 13:24:48 AM
 */
public class CommunicationManager implements Runnable {

	ProtocolService protocolService;
	boolean writeError=false;
	boolean outputReady=false;
	
	final IRuntimeOutputPort opRemotePhoneID;
	final IRuntimeOutputPort opReceivedSMS;
	final IRuntimeOutputPort opErrorNumber;

	final IRuntimeEventTriggererPort etpIdleState;
	final IRuntimeEventTriggererPort etpRingState;
	final IRuntimeEventTriggererPort etpConnectedState;
	final IRuntimeEventTriggererPort etpNewSMS;
	final IRuntimeEventTriggererPort etpError;
	AbstractRuntimeComponentInstance owner;
	
	/**
	 * The class constructor.
	 * @param opRemotePhoneID phone ID output port
	 * @param opReceivedSMS SMS output port
	 * @param opErrorNumber error output port
	 * @param etpIdleState idle state event output port
	 * @param etpRingState phone ring state event output port
	 * @param etpConnectedState connected event output port
	 * @param etpNewSMS new sms event output port
	 * @param etpError error event output port
	 */
	public CommunicationManager(IRuntimeOutputPort opRemotePhoneID,IRuntimeOutputPort opReceivedSMS,IRuntimeOutputPort opErrorNumber,IRuntimeEventTriggererPort etpIdleState,IRuntimeEventTriggererPort etpRingState,IRuntimeEventTriggererPort etpConnectedState,IRuntimeEventTriggererPort etpNewSMS,IRuntimeEventTriggererPort etpError,AbstractRuntimeComponentInstance owner)
	{
		this.opRemotePhoneID=opRemotePhoneID;
		this.opReceivedSMS=opReceivedSMS;
		this.opErrorNumber=opErrorNumber;
		
		this.etpIdleState=etpIdleState;
		this.etpRingState=etpRingState;
		this.etpConnectedState=etpConnectedState;
		this.etpNewSMS=etpNewSMS;
		this.etpError=etpError;
		this.owner=owner;
	}
	
	/**
	 * Thread method that is used to start the connection and process received data.
	 */
	@Override
	public void run() {
		
		boolean exit=false;
		boolean serverResult=true;
		outputReady=false;
		do{
		
			if(clientConnection==false)
			{
				prepareServerSocket();
			
				if(finish)
				{
					closeServerSocket();
				
					return;
				}
			}
		
		
			outputReady=false;
			exit=false;
			writeError=false;
			
			boolean result = true;
			
			if (clientConnection) {
				result = connectToServer();
			} else {
				result = waitForConnection();
			}
			
			if(clientConnection==false)
			{
				closeServerSocket();
			}
			
			if ((!result)&&(!finish)) {
				exit =true;
				continue;
			}
			
			
			
			if (!finish) {
				AstericsErrorHandling.instance.getLogger().fine("Phone connected");
				AstericsErrorHandling.instance.reportOk(owner, "Phone connected");
 
				
				//AstericsErrorHandling.instance.setStatusObject("OK", "", "Phone connected");
				
				protocolService = new ProtocolService(socket);
				outputReady=true;
				
				
				do {
					
					exit=processData();
					
					try{
						Thread.sleep(200);
					}catch (InterruptedException e) {}
					
					/*if((finish==true)||(exit==true)||(writeError==true))
					{
			
					}*/
					
				} while ((!finish)&&(!exit)&&(!writeError));
				outputReady=false;
				
				if(exit||writeError)
				{
					AstericsErrorHandling.instance.reportError(owner, "Phone disconnected");
				}
			
				AstericsErrorHandling.instance.getLogger().fine("Phone disconnected");
			}
			
			
			try {
				socket.close();
			} catch (IOException e) {
				AstericsErrorHandling.instance.getLogger().warning("Error socket close");
			}catch(NullPointerException e ) {
				AstericsErrorHandling.instance.getLogger().warning("Error socket close null");
			}
			
			
			
			//AstericsErrorHandling.instance.getLogger().fine("finish");
			
			
			if(clientConnection==false)
			{
				if(serverSocketActive)
				{
					closeServerSocket();
				}
			}
			
			
		} while (!finish);
		
		
		
		
		

	}

	private boolean serverSocketActive=false;
	
	/**
	 * Prepares server socket.
	 */
	private void prepareServerSocket() {
		boolean serverResult;
		do
		{
			serverResult=prepareServerConnection();
			if(!serverResult)
			{
				AstericsErrorHandling.instance.getLogger().warning("Error: Preparation of the connection failed !!!");
			}
			else
			{
				serverSocketActive=true;
			}
		}while((!serverResult)&&(!finish));
	}

	/**
	 * Closes server socket.
	 */
	private void closeServerSocket() {
		try {
			serverSocket.close();
			serverSocketActive=false;
		} catch (IOException e) {
			AstericsErrorHandling.instance.getLogger().warning("Error server socket close");
		}
	}
	
	private boolean finish=false;
	Socket socket=null;
	ServerSocket serverSocket=null;
	
	private boolean clientConnection=false;
	private String IP="localhost";
	private int port = 21111;
	
	/**
	 * Starts communication work.
	 * @param clientConnection defines connection mode
	 * @param IP IP of the remote server
	 * @param port TCP port of the connection
	 */
	public void start(boolean clientConnection,String IP,int port)
	{
		this.clientConnection=clientConnection;
		this.IP=IP;
		this.port=port;
		finish=false;
		pause=false;
		AstericsThreadPool.instance.execute(this);
		AstericsThreadPool.instance.execute(sender);
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
	 * In the server connection, waits for the remote clients.
	 * @return operation result
	 */
	private boolean waitForConnection(){
		
		boolean error=false;
		try {
			serverSocket.setSoTimeout(5000);
		} catch (SocketException e) {
			error=true;
		}
		
		if(error)
		{
			AstericsErrorHandling.instance.getLogger().warning("Error: Server init error !!!");
			return false;
		}
		
		boolean timeOut=false;
		error=false;
		
		do{
			timeOut=false;
			try {
				socket=serverSocket.accept();
			}catch (InterruptedIOException e) {
				timeOut=true;
				//AstericsErrorHandling.instance.getLogger().warning("Server timeout");
			} catch (IOException e) {
				error=true;
			}
		}while((finish==false)&&(timeOut==true));
		
		
		if(finish==true)
		{
			return false;
		}
		
		if(error)
		{
			AstericsErrorHandling.instance.getLogger().warning("Error: Server init error !!!");
			return false;
		}else
		{
			return true;
		}
	}
	
	/**
	 * Runs the server connection.
	 * @return operation result.
	 */
	private boolean prepareServerConnection(){
		int trial=3;
		boolean connectedError=false;
		
		do
		{
			/*del
			if(trial<3)
			{
				//TODO sleep
			}*/
			connectedError=false;
			///Log.d("ConnectionManager","connection port: " + connectionPort);
			try {
				serverSocket=new ServerSocket(port);
				InetAddress ipx = serverSocket.getInetAddress();
				AstericsErrorHandling.instance.getLogger().fine("connection ip: " + ipx.toString() + /*" " + getLocalIpAddress() +*/ " port: " + port);
			} catch (IOException e) {
				connectedError=true;
				trial=trial-1;
			}
			
		}while((finish==false)&&(connectedError==true)&&(trial>0));
		
		if(finish==true)
		{
			return false;
		}
		
		
		if(connectedError==true){
		
			
			return false;
		
		}
		else
		{
			return true;
			
		}
		
	}
	
	/**
	 * Connects to the server, for the client connection.
	 * @return operation result.
	 */
	private boolean connectToServer() {
		int trial=3;
		boolean connectedError=false;
		
		do
		{
			/* del
			if(trial<3)
			{
				//TODO sleep
			}*/
			connectedError=false;
			try {
				socket=new Socket();//(connectionIP,connectionPort);
				socket.connect(new InetSocketAddress(IP, port), 5000);

			} catch (UnknownHostException e) {
				connectedError=true;
				AstericsErrorHandling.instance.getLogger().warning("Connection fail ! " + e.getMessage());
				trial=trial-1;
			} catch(SocketTimeoutException e){
				connectedError=true;
				//AstericsErrorHandling.instance.getLogger().warning("time out " + e.getMessage());
				trial=3;
			} catch (IOException e) {
				connectedError=true;
				AstericsErrorHandling.instance.getLogger().warning("Connection fail !! " + e.getMessage());
				trial=trial-1;
			}
		}while((finish==false)&&(connectedError==true)&&(trial>0));
		
		if(finish==true)
		{
			return false;
		}
		
		
		if(connectedError==true){
		
			AstericsErrorHandling.instance.getLogger().warning("Connection fail !!!");
			return false;
		
		}
		else
		{
			return true;
			
		}
	}
	
	/**
	 * Process the data received from Internet connection.
	 * @return operation result
	 */
	private boolean processData()
	{
		if(protocolService.checkHeaderAvailable())
		{
			ProtocolService.HeaderData hd = protocolService.decodeHeader();
			int dataSize=hd.getSize();
			ProtocolService.Command command=hd.getCommand();
			if(dataSize<0)
			{
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
					dataReady=protocolService.checkDataAvailable(dataSize);
					repeats=repeats-1;
					if(finish)
					{
						repeats=0;
					}
					
					if(repeats<5)
					{
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							
						}
					}
				}while((dataReady==false)&&(repeats>0));
			}
			
			if(command!=ProtocolService.Command.None)
			{
				if(dataReady)
				{
					
					switch(command)
					{
					case Start:
					{
						packageCounter=0;
						break;
					}
					case Result:
					{
						int result=protocolService.decodeResult(null);
						//AstericsErrorHandling.instance.getLogger().fine("Receive Result: " + result);
						if(result>0)
						{
							AstericsErrorHandling.instance.getLogger().warning("Receive Result: " + result);
							opErrorNumber.sendData(ConversionUtils.intToBytes(result));
							etpError.raiseEvent();
							
						}
						
						packageCounter=0;
						break;
					}
					case NewSMS:
					{
						byte[] data=protocolService.getData(dataSize);
						String phoneID = protocolService.decodeSMSNumber(data);
						String message = protocolService.decodeSMS(data);
						AstericsErrorHandling.instance.getLogger().fine("New sms: " + phoneID + " " + message);
						
						opRemotePhoneID.sendData(ConversionUtils.stringToBytes(phoneID));
						opReceivedSMS.sendData(ConversionUtils.stringToBytes(message));
						etpNewSMS.raiseEvent();
						
						packageCounter=0;
						break;
					}
					case CallState:
					{
						byte[] data=protocolService.getData(dataSize);
						int state=protocolService.decodeState(data);
						String phoneID=protocolService.decodeStatePhone(data);
						AstericsErrorHandling.instance.getLogger().fine("State: " + state + " " + phoneID);
						
						switch(state)
						{
						case 1:
							etpIdleState.raiseEvent();
							break;
						case 2:
							etpRingState.raiseEvent();
							if(phoneID.length()>0)
							{
								opRemotePhoneID.sendData(ConversionUtils.stringToBytes(phoneID));
							}
							break;
						case 3:
							etpConnectedState.raiseEvent();
							if(phoneID.length()>0)
							{
								opRemotePhoneID.sendData(ConversionUtils.stringToBytes(phoneID));
							}
							break;
						}
						
						packageCounter=0;
						break;
					}
					}
				}
						
			}
		}
			
		return false;
	}
	
	/**
	 * Sends the command to the remote phone.
	 * @param command command sent to the remote phone
	 */
	private void sendCommand(CommandOrder command)
	{
		
		if(command==null)
		{
			return;
		}
		
		if(pause)
		{
			return;
		}
		
		if(!outputReady)
		{
			return;
		}
		
		
		boolean result=true;
		
		
		try
		{
			sendLock.lock();
			switch(command.getCommand())
			{
			case Call:
				AstericsErrorHandling.instance.getLogger().fine("Call: " +command.getPhoneID() );
				result=protocolService.sendCallPackage(command.getPhoneID());
				break;
			case Accept:
				AstericsErrorHandling.instance.getLogger().fine("Accept Call");
				result=protocolService.sendAcceptPackage();
				break;
			case Drop: 
				AstericsErrorHandling.instance.getLogger().fine("Drop Call");
				result=protocolService.sendDropPackage();
				break;
			case SendSMS:
				AstericsErrorHandling.instance.getLogger().fine("Send SMS: " + command.getPhoneID() + " "+ command.getMessage());
				result=protocolService.sendSendSMSPackage(command.getPhoneID(),command.getMessage());
				break;
			case Start:
				//AstericsErrorHandling.instance.getLogger().fine("Send start");
				result=protocolService.sendStartPackage();
				break;
			}
		}finally
		{
			sendLock.unlock();
		
		}
		
		if(!result)
		{
			writeError=true;
			AstericsErrorHandling.instance.getLogger().fine("Send Error");
		}
	}
	
	private Lock lock = new ReentrantLock();
	private Lock sendLock = new ReentrantLock();
	
	Queue<CommandOrder> queue = new java.util.LinkedList<CommandOrder>();
	
	/**
	 * Adds the command to the queue.
	 * @param command command
	 * @param phoneID phone ID parameter
	 * @param message SMS content parameter
	 */
	public void addCommand(ProtocolService.Command command,String phoneID,String message)
	{
		try
		{
			CommandOrder co=new CommandOrder(command,phoneID,message);
			lock.lock();
			queue.add(co);
		}
		finally
		{
			lock.unlock();
		}
	}
	
	private int packageCounter=0;
	
	/**
	 * The timer which sends subsequence command from queue to the remote phone.
	 */
	private final Runnable sender = new Runnable(){
		
		/**
		 * The timer method.
		 */
		@Override
		public void run() {
			packageCounter=0;
			do
			{
				boolean empty=false;
				CommandOrder commandOrder=null;
				try
				{
					lock.lock();
					if(queue.size()>0)
					{
						commandOrder=queue.poll();
					}
					else
					{
						empty=true;
					}
					
				}
				finally
				{
					lock.unlock();
				}
				
				if(empty)
				{
					try{
						Thread.sleep(300);
					
					}catch (InterruptedException e) {}
					
					if(outputReady)
					{
						packageCounter++;
					}
					else
					{
						packageCounter=0;
					}
					
					if(packageCounter>40)
					{
						if(outputReady)
						{
							CommandOrder command=new CommandOrder (ProtocolService.Command.Start,"","");
							sendCommand(command);
						}
						packageCounter=0;
					}
				}
				else
				{
					if(outputReady)
					{
						//AstericsErrorHandling.instance.getLogger().fine("run: " + commandOrder.getCommand());
						sendCommand(commandOrder);
					}
					packageCounter=0;
				}
			}
			while(!finish);
		}
		
	};
	

}
