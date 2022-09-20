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


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.util.Log;

/**
 * 
 * Preference manager class.
 *  
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Jun 04, 2012
 *         Time: 13:24:48 AM
 */
public class ServerPreference extends PreferenceActivity implements OnPreferenceClickListener,OnPreferenceChangeListener {
	
	/**
	 * Called when the activity is starting.
	 */
	@Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            /*
            if(savedInstanceState==null)
            {
            	Log.d("ServerPreference", "Bundle null");
            }
            else
            {
            	Log.d("ServerPreference", "Bundle not null");
            }*/
            
            addPreferencesFromResource(R.xml.preferences);
            
            Preference startServer = (Preference) findPreference("cbEnable");
            startServer.setOnPreferenceClickListener(this);
            
            Preference connectionTypeList = (Preference) findPreference("lpConnectionType");
            connectionTypeList.setOnPreferenceChangeListener(this);
            
            //connectionManager=new ConnectionManager(getApplicationContext());
            ListPreference list     = (ListPreference)findPreference("lpConnectionType");
			SharedPreferences preferences = getSharedPreferences("preferences.xml", Activity.MODE_WORLD_READABLE );
			EditTextPreference ipPreference = (EditTextPreference) findPreference("etIP");
			if(list.getValue().equals(preferences.getString("lpConnectionType", list.getEntryValues()[0].toString())))
			{
				
				
				ipPreference.setEnabled(false);
			}
			else
			{
				ipPreference.setEnabled(true);
			}
            Log.d("ServerPreference", "Preferece dialog created ");
	}
	
	Intent phoneActivity=null;
	ConnectionManager connectionManager=null;

	/**
	 * Called when a Preference has been clicked.
	 */
	public boolean onPreferenceClick(Preference preference) {
		CheckBoxPreference startServer = (CheckBoxPreference) findPreference("cbEnable");
		
		PreferenceCategory connectionSettings =(PreferenceCategory) findPreference("pcConnection");
		
		if(startServer.isChecked())
		{
			//Log.d("ServerPreference", "is checked");
			connectionSettings.setEnabled(false);
			
			/*
			phoneActivity = new Intent(this, AsTeRICSService.class);
			phoneActivity.putExtra(AsTeRICSService.Finish_operation, false);
			startService(phoneActivity);*/
			/*connectionManager.StartServer();*/
			
			/*
			phoneActivity = new Intent(this, AsTeRICSphoneService.class);
			phoneActivity.putExtra(AsTeRICSphoneService.StartService, true);
			startService(phoneActivity);*/
			sendIntent(true);
			
		}
		else
		{
			//Log.d("ServerPreference", "is not checked");
			
			connectionSettings.setEnabled(true);
			ListPreference list     = (ListPreference)findPreference("lpConnectionType");
			SharedPreferences preferences = getSharedPreferences("preferences.xml", Activity.MODE_WORLD_READABLE);
			EditTextPreference ipPreference = (EditTextPreference) findPreference("etIP");
			if(list.getValue().equals(preferences.getString("lpConnectionType", list.getEntryValues()[0].toString())))
			{
				
				
				ipPreference.setEnabled(false);
			}
			else
			{
				ipPreference.setEnabled(true);
			}
			
			/*
			if(phoneActivity!=null)
			{
				Log.d("ServerPreference", "finishing !!!");
				phoneActivity.putExtra(AsTeRICSService.Finish_operation, true);
			}*/
			//Log.d("ServerPreference", "finishing !!!");
			//connectionManager.stopServer();
			/*
			phoneActivity = new Intent(this, AsTeRICSphoneService.class);
			phoneActivity.putExtra(AsTeRICSphoneService.StartService, false);
			startService(phoneActivity);*/
			sendIntent(false);
		}
		
		return false;
	}


	/**
	 * Called when a Preference has been changed by the user.
	 */
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		
		try
		{
			if(arg0.hasKey())
			{
				if(arg0.getKey().equals("lpConnectionType"))
				{
					String value=(String)arg1;
				
					//Log.d("ServerPreference", "value: "+value);
				
					EditTextPreference ipPreference = (EditTextPreference) findPreference("etIP");
				
					if(value.equals("server"))
					{
						ipPreference.setEnabled(false);
					}
					else
					{
						ipPreference.setEnabled(true);
					}
				}
			}
		}
		catch(Exception e)
		{
			Log.e("ServerPreference", "Exception: " + e.getMessage());
		}
		return true;
	}
	
	/**
	 * Sends item to the service
	 * @param enable defines if the phone service should started or stopped.
	 */
	private void sendIntent(boolean enable)
	{
		phoneActivity = new Intent(this, AsTeRICSphoneService.class);
		phoneActivity.putExtra(AsTeRICSphoneService.StartService, enable);
		
		SharedPreferences additionalPreferenceSet = getSharedPreferences(AsTeRICSphoneService.additionalPrefereceFile,MODE_WORLD_READABLE ); 
		SharedPreferences.Editor editor= additionalPreferenceSet.edit();
		editor.putBoolean(AsTeRICSphoneService.StartService, enable);
		editor.commit();
		
		if(enable)
		{
			//SharedPreferences preferences = getSharedPreferences("preferences.xml", Activity.MODE_PRIVATE);
			ListPreference list     = (ListPreference)findPreference("lpConnectionType");
			
			String connection =list.getValue();//preferences.getString("lpConnectionType","other");
			if(connection.equalsIgnoreCase("server"))
			{
				phoneActivity.putExtra(AsTeRICSphoneService.clientConnection, false);
				editor.putBoolean(AsTeRICSphoneService.clientConnection, false);
			}
			else
			{
				if(connection.equalsIgnoreCase("client"))
				{
					phoneActivity.putExtra(AsTeRICSphoneService.clientConnection, true);
					editor.putBoolean(AsTeRICSphoneService.clientConnection, true);
					EditTextPreference etIP = (EditTextPreference)findPreference("etIP");
					//String ip = preferences.getString("etIP", "none");
					String ip = etIP.getText();
					/*
					if(ip.equalsIgnoreCase("none"))
					{
						Log.e("ServerPreference", "ip connection recognize fail !!!");
					}
					else
					{
						Log.d("ServerPreference", "ip: " + ip);
					}*/
					phoneActivity.putExtra(AsTeRICSphoneService.ip, ip);
					editor.putString(AsTeRICSphoneService.ip, ip);
				}
				else
				{
					Log.e("ServerPreference", "client connection recognize fail !!! " + connection);
				}
			}
				
			EditTextPreference etPort = (EditTextPreference)findPreference("etPort");
			String port = etPort.getText();
				
			Log.d("ServerPreference", "port: " + port);
				
				
			boolean hasport=true;
				
			int portNumber=21111;

			try
			{
				portNumber= Integer.parseInt(port);
			}
			catch(NumberFormatException e)
			{
				hasport=false;
			}
				
			if(hasport)
			{
				phoneActivity.putExtra(AsTeRICSphoneService.port, portNumber);
				editor.putInt(AsTeRICSphoneService.port, portNumber);
				
			}
			else
			{
				Log.e("ServerPreference", "port connection recognize fail !!!");
			}
			
		}
		
		editor.commit();
		startService(phoneActivity);
	}
	
	
}
