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

import java.util.prefs.Preferences;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.ListPreference;
import android.util.Log;

/**
 * 
 * Phone server service class.
 *  
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Jun 04, 2012
 *         Time: 13:24:48 AM
 */
public class AsTeRICSphoneService extends Service /*implements Runnable*/ {

	public static final String StartService = "eu.asterics.AsTeRICSPhoneServer.action.StartService";
	//public static final String StopService = "eu.asterics.AsTeRICSPhoneServer.action.StopService";
	
	public static final String clientConnection = "eu.asterics.AsTeRICSPhoneServer.action.ClientConnection";
	public static final String ip = "eu.asterics.AsTeRICSPhoneServer.action.IP";
	public static final String port = "eu.asterics.AsTeRICSPhoneServer.action.port";
	public static final String additionalPrefereceFile ="eu.asterics.AsTeRICSPhoneServer.addpref.xml";
	
	ConnectionManager connectionManager;//=new ConnectionManager(this);
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private boolean finish=false;
	private boolean client=false;
	private int portNb=21111;
	private String ipAddress="";
	Intent lastIntent=null;
	/**
	 * Starts service command.
	 */
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		lastIntent=intent;
		
		if(intent==null)
		{
			Log.d("AsTeRICSService", "Service is restarted");
			lastIntent=restartServer();
			//return START_STICKY  ;
		}
		
		
		if(lastIntent.hasExtra(StartService))
		{
			boolean action = lastIntent.getBooleanExtra(StartService, false);
		
			if(action){
				client=lastIntent.getBooleanExtra(clientConnection, false);
			
				if(client)
				{
					ipAddress=lastIntent.getStringExtra(ip);
				}
				else
				{
					Log.d("AsTeRICSphoneService","Server Connection");
				}
			
				portNb=lastIntent.getIntExtra(port, 21111);
			
				connectionManager.StartServer(client, ipAddress, portNb);
				finish=false;
				//Thread t = new Thread(this);
				//t.start();
			}else{
				finish=true;
				Log.d("AsTeRICSphoneService","Service stopped");
				connectionManager.stopServer();
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {}
				stopSelf();
			}
		}
		
		return START_STICKY  ;

	}
	
	/**
	 * Called by the system when the service is first created.
	 */
    @Override
    public void onCreate() {
    	
    	connectionManager=new ConnectionManager(this);
		
    	IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectionManager.PhoneStateChange);
		filter.addAction(ConnectionManager.NewSMS);
		
		try
		{
			registerReceiver(connectionManager, filter);
		}
		catch(Exception e)
		{
			Log.e("AsTeRICSService", e.getMessage());
		}
    	connectionManager.registerReveiver();
    }
    
    
    @Override
    public void onDestroy()
    {
    	unregisterReceiver(connectionManager);
    }
    
    /*
    @Override
    public void onLowMemory()
    {
    	
    }
    
    
    @Override
    public void onRebind(Intent intent)
    {
    	
    }*/
    
    
    //private int notificationNumber=0;
    
    /**
	 * Displays notification.
	 * @param msg message to display.
	 */
    private void displayNotification(String msg)
	{
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification(R.drawable.ic_launcher, msg, System.currentTimeMillis());

		// The PendingIntent will launch activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, ServerPreference.class), 0);

		notification.setLatestEventInfo(this, "AsTeRICS server", msg, contentIntent);
		//notificationNumber++;

		manager.notify(1, notification);

	}
    
    /**
	 * It reads data of the last intent and generates new intent with these data.
	 * @return new intent with data of the last intent
	 */
    private Intent restartServer()
    {
    	SharedPreferences additionalPreferenceSet = getSharedPreferences(AsTeRICSphoneService.additionalPrefereceFile,MODE_WORLD_WRITEABLE  ); 
    	
    	
    	Intent intent = new Intent();
    	
    	if(additionalPreferenceSet.contains(AsTeRICSphoneService.StartService))
    	{
    		intent.putExtra(AsTeRICSphoneService.StartService, additionalPreferenceSet.getBoolean(AsTeRICSphoneService.StartService, false));
    	}
    	
    	if(additionalPreferenceSet.contains(AsTeRICSphoneService.clientConnection))
    	{
    		intent.putExtra(AsTeRICSphoneService.clientConnection, additionalPreferenceSet.getBoolean(AsTeRICSphoneService.clientConnection, false));
    	}
    	
    	
    	if(additionalPreferenceSet.contains(AsTeRICSphoneService.ip))
    	{
    		intent.putExtra(AsTeRICSphoneService.ip, additionalPreferenceSet.getString(AsTeRICSphoneService.ip, "localhost"));
    	}
    	
    	if(additionalPreferenceSet.contains(AsTeRICSphoneService.port))
    	{
    		intent.putExtra(AsTeRICSphoneService.port, additionalPreferenceSet.getInt(AsTeRICSphoneService.port, 21111));
    	}
    	
    	
    	return intent;
    	
    }


}
