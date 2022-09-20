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

package eu.asterics.AsTeRICSPhoneServer;

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
import java.util.Enumeration;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * 
 * This class manages the Internet connection. It can run the client or server connection depend on the settings.
 *  
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Jun 04, 2012
 *         Time: 13:24:48 AM
 */

public class ConnectionManager extends BroadcastReceiver implements Runnable {
	
	public static final String FinishService= "eu.asterics.AsTeRICSPhoneServer.action.FinishService";
	public static final String PhoneStateChange = "eu.asterics.AsTeRICSPhoneServer.action.PhoneStateChange";
	public static final String NewSMS="eu.asterics.AsTeRICSPhoneServer.action.NewSMS";
	
	AsTeRICSphoneService appContext;
	ProtocolService protocolService;
	PhoneManager phoneManager;
	
	/**
	 * The class constructor.
	 * @param context context of the Service class
	 */
	public ConnectionManager(AsTeRICSphoneService context){
		appContext=context;
	}
	
	/**
	 * Runs the PhoneManager class.
	 */
	public void registerReveiver()
	{
		phoneManager=new PhoneManager(appContext,this);
		IntentFilter filter = new IntentFilter();
    	filter.addAction("android.intent.action.PHONE_STATE");
    	filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
    	filter.addAction("android.provider.Telephony.SMS_RECEIVED");
    	
    	appContext.registerReceiver(phoneManager, filter);
	}
	
	private boolean finish=false;
	private Socket socket=null;
	private ServerSocket serverSocket=null;
	private boolean writeError=false;
	private boolean connected=false;
	
	private Lock sendLock = new ReentrantLock();
	int counter =0;
	
	/**
	 * Thread method that is used to start the connection and process received data.
	 */
	public void run() {
		
		connected=false;
		boolean exit=false;
		boolean serverResult=true;
		do {
		
			if(clientConnection==false){
				prepareServerSocket();
				
				if(finish){
					closeServerSocket();
					
					return;
				}
			}
		
		
			exit=false;
			writeError=false;
			boolean result = true;
			connected=false;
			
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
				counter=0;
				displayNotification("Phone connected");
				Log.d("ConnectionManager", "Phone connected");
				
				protocolService = new ProtocolService(socket);
				connected=true;
				do {
					try{
						sendLock.lock();
						exit=processData(protocolService);
					}finally{
						sendLock.unlock();
					}
					
					
					
					try{
						Thread.sleep(200);
					}catch (InterruptedException e) {}
					counter++;
					
					if(counter>=100)
					{
						sendStart(protocolService);
					}
					
					if(counter>=200)
					{
						Log.d("ConnectionManager", "Time out!!!");
						exit=true;
					}
				} while ((!finish)&&(!exit)&&(!writeError));
				connected=false;
				displayNotification("Phone disconnected");
				Log.d("ConnectionManager", "Phone Disconnected");
			}
			
			try {
				socket.close();
			} catch (IOException e) {
				Log.e("ConnectionManager", "error socket close");
			}catch(NullPointerException e ) {
				Log.e("ConnectionManager", "error socket close");
			}
			
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
		do{
			serverResult=prepareServerConnection();
			if(!serverResult){
				displayNotification("Error: Preparation of the connection failed !!!");
				Log.e("ConnectionManager", "Error: Preparation of the connection failed !!!");
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
			Log.e("ConnectionManager", "error server socket close");
		}
	}
	
	/**
	 * Process the data received from Internet connection.
	 * @param ps ProtocolService class object
	 * @return operation result
	 */
	private boolean processData(ProtocolService ps)
	{
		if(ps.checkHeaderAvailable())
		{
			ProtocolService.HeaderData hd =ps.decodeHeader();
			int dataSize=hd.getSize();
			ProtocolService.Command command=hd.getCommand();
			if(dataSize<0){
				return true;
			}
			
			counter=0;
			
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
							Thread.sleep(50);
						} catch (InterruptedException e) {
							
						}
					}
				}while((dataReady==false)&&(repeats>0));
			}
			
			if(command!=ProtocolService.Command.None){
				if(dataReady){
					byte[] data=ps.getData(dataSize);
					switch(command){
					case Call:
					{
						String phoneID = ps.decodeCallNumber(data);
						
						if(phoneID!=null){
							Log.d("ConnectionManager", "Action call to: " + phoneID);
							int result = phoneManager.makeCall(phoneID);
							sendRespond(ps,result);
						}else{
							Log.e("ConnectionManager", "Phone ID error ");
						}
						
						break;
					}
					case Accept:
					{
						Log.d("ConnectionManager", "Action accept ");
						int result=phoneManager.acceptCall();
						sendRespond(ps,result);
						break;
					}
					case Drop:
					{
						Log.d("ConnectionManager", "Action drop ");
						int result = phoneManager.dropCall();
						sendRespond(ps,result);
						break;
					}
					case SendSMS:
					{
						String phoneID = ps.decodeSMSNumber(data);
						String message = ps.decodeSMS(data);
						
						if(phoneID!=null&&message!=null)
						{
							Log.d("ConnectionManager", "Action send SMS to: " + phoneID + " " + message);
							int result=phoneManager.sendTestSMS(phoneID,message);
							sendRespond(ps,result);
						}else{
							Log.e("ConnectionManager", "SMS data error ");
						}
						break;
					}
					case Start:
					{
						Log.d("ConnectionManager", "Action start ");
						sendRespond(ps,0);
						//displayDebugNotification("ping",3);
						break;
					}
					}
				}
				else
				{
					Log.e("ConnectionManager", "No data for command !!!");
				}
						
			}
			else
			{
				Log.e("ConnectionManager", "None command !!!");
			}
		}
			
		return false;
	}
	
	/**
	 * Sends the start package
	 * @param ps ProtocolService class object
	 */
	private void sendStart(ProtocolService ps){
		try{
			sendLock.lock();
			boolean result = ps.sendStartPackage();
			if(!result){
				writeError=true;
			}
		}finally{
			sendLock.unlock();
		}
	}
	
	/**
	 * Sends the respond number.
	 * @param ps ProtocolService class object
	 * @param error error data
	 */
	private void sendRespond(ProtocolService ps,int error){
		try{
			sendLock.lock();
			boolean result = ps.sendResultPackage(error);
			if(!result){
				writeError=true;
			}
		}finally{
			sendLock.unlock();
		}
	}
	
	/**
	 * Sends the phone state.
	 * @param state state of the phone
	 * @param phoneNumber number of the remote phone.
	 */
	private void sendState(byte state, String phoneNumber){
		if(!connected)
		{
			//displayDebugNotification("Not connected",2);
			return;
		}
		
		
		try{
			sendLock.lock();
			Thread.sleep(300);
			Log.d("ConnectionManager", "Send state: " + state + " " + phoneNumber);
			
			boolean result = protocolService.sendCallStatePackage(state, phoneNumber);
			if(!result){
				writeError=true;
				//displayDebugNotification("Send fail",2);
			}
			else
			{
				//displayDebugNotification("Send OK",2);
			}
			
		}catch(Exception e)
		{
			//displayDebugNotification("Error 2",2);
		
		}finally{
			sendLock.unlock();
		
		}
	}
	
	/**
	 * Sends the new SMS
	 * @param phoneNumber number of the phone
	 * @param message SMS content
	 */
	private void sendNewSMS (String phoneNumber,String message){
		if(!connected)
		{
			return;
		}
		try{
			sendLock.lock();
			boolean result = protocolService.sendNewSMSPackage(phoneNumber, message);
			if(!result){
				writeError=true;
			}
		}finally{
			sendLock.unlock();
		
		}
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
		
		if(error){
			displayNotification("Error: Server init error !!!");
			Log.e("ConnectionManager", "Error: Server init error !!!");
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
				Log.d("ConnectionManager", "time out");
			} catch (IOException e) {
				error=true;
			}
		}while((finish==false)&&(timeOut==true));
		
		if(finish==true){
			return false;
		}
		
		if(error){
			displayNotification("Error: Server init error !!!");
			Log.e("ConnectionManager", "Error: Server init error !!!");
			return false;
		}else{
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
			
			connectedError=false;
			
			try {
				serverSocket=new ServerSocket(connectionPort);
				InetAddress ipx = serverSocket.getInetAddress();
				Log.d("ConnectionManager","connection ip: " + ipx.toString() + " " + getLocalIpAddress() + " port: " + connectionPort );
			} catch (IOException e) {
				connectedError=true;
				trial=trial-1;
			}
			
		}while((finish==false)&&(connectedError==true)&&(trial>0));
		
		if(finish==true){
			
			return false;
		}
		
		
		if(connectedError==true){
		
			return false;
		
		}else{
			
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
		
		do{
			/* del
			if(trial<3){
				//TODO sleep
			}*/
			connectedError=false;
			try {
				socket=new Socket();//(connectionIP,connectionPort);
				socket.connect(new InetSocketAddress(connectionIP, connectionPort), 5000);

			} catch (UnknownHostException e) {
				connectedError=true;
				Log.e("ConnectionManager", "Connection fail !!! " + e.getMessage());
				trial=trial-1;
			} catch(SocketTimeoutException e){
				connectedError=true;
				Log.e("ConnectionManager", "Connection to server time out " + e.getMessage());
				trial=3;
			} catch (IOException e) {
				connectedError=true;
				Log.e("ConnectionManager", "Connection fail !!! " + e.getMessage());
				trial=trial-1;
			}
		}while((finish==false)&&(connectedError==true)&&(trial>0));
		
		if(finish==true){
			return false;
		}
		
		
		if(connectedError==true){
		
			displayNotification("Error: Connection to server fail !!!");
			Log.e("ConnectionManager", "Connection to server fail !!!");
			return false;
		
		}else{
			return true;
		}
	}
	
	boolean clientConnection=false;
	String connectionIP="";
	int connectionPort=21111;
	boolean serverWorking=false;
	
	/**
	 * Starts the new connection.
	 * @param client if is set, the client connection will be started, otherwise the server connection will be started
	 * @param IP IP of the server for client connection
	 * @param port port of the server
	 */
	public void StartServer(boolean client, String IP,int port){
		if(serverWorking)
		{
			finish=true;
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {}
		}
		finish=false;
		clientConnection=client;
		connectionIP=IP;
		connectionPort=port;
		Thread t = new Thread(this);
		t.start();
	}
	
	/**
	 * Receives intents
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(FinishService)) {
			finish=true;
		}else if(intent.getAction().equals(PhoneStateChange)){
			int state = intent.getIntExtra(PhoneManager.StateName, 0);
			String phoneID =intent.getStringExtra(PhoneManager.NumberName);
			byte byteState=(byte)state;
			sendState(byteState,phoneID);
			
		}else if(intent.getAction().equals(NewSMS))
		{
			String phoneID =intent.getStringExtra(PhoneManager.NumberName);
			String content=intent.getStringExtra(PhoneManager.SMSContent);
			sendNewSMS(phoneID,content);
		}
	}
	
	/**
	 * Starts the connection close.
	 */
	public void stopServer(){
		appContext.unregisterReceiver(phoneManager);
		finish=true;
	}
	
	/**
	 * Displays notifications.
	 * @param msg message content
	 */
	private void displayNotification(String msg){
		NotificationManager manager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification(R.drawable.ic_launcher, msg, System.currentTimeMillis());

		// The PendingIntent will launch activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(appContext, 0, new Intent(appContext, ServerPreference.class), 0);

		notification.setLatestEventInfo(appContext, "AsTeRICS server", msg, contentIntent);

		manager.notify(1, notification);

	}
	
	
	/**
	 * Gets the IP address of the phone.
	 * @return IP of the phone
	 */
	public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("SocketTestActivity", ex.toString());
        }
        return null;
    }
	
	
	
	

}
