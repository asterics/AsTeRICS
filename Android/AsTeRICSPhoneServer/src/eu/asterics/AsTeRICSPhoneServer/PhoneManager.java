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

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

/**
 * 
 * This calls manages phone and SMS functions.
 *  
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Jun 04, 2012
 *         Time: 13:24:48 AM
 */
public class PhoneManager extends BroadcastReceiver {
	
	public static final int StateIdle=1;
	public static final int StateRinging=2;
	public static final int StateOffHook=3;
	public static final int StateOutgoing=4;
	
	public static final String StateName = "State";
	public static final String NumberName="Number";
	public static final String SMSContent="Content";
	
	private int currentState=0;
	Context phoneCallContext=null;
	/**
	 * This method is used to receive phone and SMS intents.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.PHONE_STATE")) { 
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            Log.d("PhoneManager","PhoneStateReceiver: call State=" + state);
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            	currentState=StateIdle;
            	sendStateIntent(context,currentState,"");
            	Log.d("PhoneManager","PhoneStateReceiver: Idle");
            } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) { 
                
            	String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                currentState=StateRinging;
                sendStateIntent(context,currentState,incomingNumber);
                Log.d("PhoneManager","PhoneStateReceiver: Incoming call " + incomingNumber);
                phoneCallContext=context;
 
            } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            	currentState=StateOffHook;
            	sendStateIntent(context,currentState,"");
            	Log.d("PhoneManager","PhoneStateReceiver: Offhook");
            	phoneCallContext=context;
            	
            }
        } else if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) { 
            
           
            String outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            currentState=StateOutgoing;
            sendStateIntent(context,StateRinging,outgoingNumber);
            Log.d("PhoneManager","PhoneStateReceiver: Outgoing call " + outgoingNumber);
            phoneCallContext=context;
            
           
        }else if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
        	Bundle bundle = intent.getExtras();
        	if (bundle != null) 
        	{
        		Object[] pdus = (Object[])bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++)
                {
                	messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                	Log.d("PhoneManager", "message :" + messages[i].getDisplayOriginatingAddress() + " " + messages[i].getDisplayMessageBody());
                	newSMS(context, messages[i].getDisplayOriginatingAddress(), messages[i].getDisplayMessageBody());

                }
        	}
        } 
        else {
            //Logger.e("PhoneStateReceiver **unexpected intent.action=" + intent.getAction());
        	Log.e("PhoneManager","PhoneStateReceiver: unexpected intent.action=" + intent.getAction());
        }

	}
	
	/**
	 * Sends phone state change to the ConnectionManager object.
	 * @param state the new phone state
	 * @param number number of the remote phone 
	 */
	private void sendStateIntent(Context context,int state, String number)
	{
		Intent intent=new Intent();
		intent.setAction(ConnectionManager.PhoneStateChange);
		intent.putExtra(StateName, state);
		intent.putExtra(NumberName, number);
		context.sendBroadcast(intent);
		//owner.sendState((byte)state, number);
	
	}
	
	/**
	 * Sends information about the new SMS to the ConnectionManager.
	 * @param phoneNumber sender number
	 * @param message SMS content
	 */
	private void newSMS(Context context,String phoneNumber, String message)
	{
		Intent intent=new Intent();
		intent.setAction(ConnectionManager.NewSMS);
		intent.putExtra(NumberName, phoneNumber);
		intent.putExtra(SMSContent, message);
		context.sendBroadcast(intent);
	}
	
	private AsTeRICSphoneService appContext;
	private ConnectionManager owner;
	
	/**
	 * The class constructor.
	 * @param contex the service context
	 * @param connectionManager the ConnectionManager owner object
	 */
	public PhoneManager(AsTeRICSphoneService context, ConnectionManager connectionManager){
		appContext=context;
		owner=connectionManager;
		getPhoneState();
		phoneCallContext=null;
	}
	
	
	/**
	 * Drops the call.
	 * @return operation result.
	 */
	public int dropCall()
	{
		if((currentState==StateRinging)||(currentState==StateOffHook))
		{
			Context context = phoneCallContext;
			if(context==null)
			{
				context=appContext;
			}
			
			
			if(killCall(context))
			{
				return 0;
			}
			else
			{
				return 1;

			}
			
		}
		else if(currentState==StateOutgoing){
			setResultData(null);
			return 0;
		}
		else
		{
			return 1;
		}
	}
	
	/**
	 * Makes the call.
	 * @param phoneID the number of the remote phone
	 * @return operation result.
	 */
	public int makeCall(String phoneID)
	{
		if(currentState==StateIdle)
		{
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:"+phoneID));
			callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			appContext.startActivity(callIntent);
			return 0;
		}
		else
		{
			return 1;
		}
		
	}
	
	/**
	 * Accepts the incoming call.
	 * @return operation result.
	 */
	public int acceptCall()
	{
		Log.d("PhoneManager","accept Init");
		if(currentState==StateRinging)
		{
			Context context = phoneCallContext;
			if(context==null)
			{
				context=appContext;
			}
			
			/*if(answerCall(context))
			{
				return 0;
			}
			else*/
			{
				int result=answerCallBluetoothEmulation(context);
				//int result=answerCallAdb(context);
				return result;
			}
		}
		
		return 1;
	}
	
	
	/**
	 * Gets the current phone state.
	 */
	private void getPhoneState()
	{
		TelephonyManager telephone = (TelephonyManager)appContext.getSystemService(Context.TELEPHONY_SERVICE);
		int state = telephone.getCallState();
		switch(state)
		{
		case TelephonyManager.CALL_STATE_IDLE:
			currentState=1;
			Log.d("PhoneManager","Start state: idle");
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			Log.d("PhoneManager","Start state: offhook");
			currentState=3;
			break;
		case TelephonyManager.CALL_STATE_RINGING:
			Log.d("PhoneManager","Start state: ringing");
			currentState=2;
			break;
		default:
			Log.e("PhoneManager","No begin: state");
		}
	}
	
	/**
	 * Sends SMS.
	 * @param phoneID the ID of the receiver phone
	 * @param message the message content
	 * @return operation result
	 */
	public int sendTestSMS(String phoneID,String message)
	{
		if(currentState==StateIdle)
		{
			try
			{
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(phoneID, null, message, null, null);
				Log.d("PhoneManager","SMS is sent");
			}
			catch(Exception e)
			{
				Log.e("PhoneManager","SMS is not sent !!!");
			}
		
			return 0;
		}
		else
		{
			return 1;
		}
		
	}
	
	/**
	 * Kills the call.
	 * @param context context
	 * @return operation result
	 */
	private boolean killCall(Context context) {
	    try {
	       
	       TelephonyManager telephonyManager =
	          (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	 
	      
	       Class classTelephony = Class.forName(telephonyManager.getClass().getName());
	       Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");
	 
	      
	       methodGetITelephony.setAccessible(true);
	 
	     
	       Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);
	 
	      
	       Class telephonyInterfaceClass =  
	           Class.forName(telephonyInterface.getClass().getName());
	       Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
	 
	       
	       methodEndCall.invoke(telephonyInterface);
	 
	   } catch (Exception ex) { 
		   Log.e("PhoneManager","Kill call" + ex.toString());
	      return false;
	   }
	   return true;
	}
	
	/**
	 * Accept the call.
	 * @param context context
	 * @return operation result
	 */
	private boolean answerCall(Context context)
	{
		try {
		      
		       TelephonyManager telephonyManager =
		          (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		 
		   
		       Class classTelephony = Class.forName(telephonyManager.getClass().getName());
		       Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");
		 
		      
		       methodGetITelephony.setAccessible(true);
		 
		      
		       Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);
		 
		    
		       Class telephonyInterfaceClass =  
		           Class.forName(telephonyInterface.getClass().getName());
		       Method answerCall = telephonyInterfaceClass.getDeclaredMethod("answerRingingCall");
		 
		    
		       answerCall.invoke(telephonyInterface);
		 
		   } catch (Exception ex) { 
			   Log.e("PhoneManager","answerCall: " + ex.toString());
		      return false;
		   }
		   return true;
		
		
	}
	
	/**
	 * Emulates headset keys to accept the call.
	 * @param context context
	 * @return operation result
	 */
	private int answerCallAdb(Context contex)
	{
		final Runtime r = Runtime.getRuntime();
	    try {
	 
	    	//Process process = r.exec("am start -n com.android.phone/.InCallScreen -a android.intent.action.ANSWER");
	    	//Process process = r.exec(new String[]{"su","-c","input keyevent 5"});
	    	Process process = r.exec(new String[]{"input keyevent 5"});
	        InputStream stream = process.getErrorStream();
	        Log.d("PhoneManager", "Process Error Stream: " +stream.toString());
	        Log.d("PhoneManager","Sending shell command to Answer Call");
	    } catch (Exception e) {
	    	Log.d("PhoneManager", "Stack Trace: " + e.getStackTrace().toString());
	        e.printStackTrace();
	    }
		return 0;
	}
	
	/**
	 * Emulates headset keys to accept the call.
	 * @param context context
	 * @return operation result
	 */
	private int answerCallBluetoothEmulation(Context context)
	{
		try
		{
			
			AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
			boolean headsetOn = audioManager.isWiredHeadsetOn();
			boolean errorPlug=false;
			/*
			boolean btHeadsetOn1 = audioManager.isBluetoothA2dpOn();
			boolean btHeadsetOn2 = audioManager.isBluetoothScoOn();
			boolean speaker = audioManager.isSpeakerphoneOn();
			//audioManager.
			
			Log.d("PhoneManager", "headset: " + headsetOn + " b1: " +btHeadsetOn1 + " b2: " +  btHeadsetOn2 + " speaker: " + speaker);*/
			Log.d("PhoneManager","ACCEPT start");
			if(headsetOn==false)
			{
				try
				{
					Intent headSetUnPluggedintent = new Intent(Intent.ACTION_HEADSET_PLUG);
					headSetUnPluggedintent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
					headSetUnPluggedintent.putExtra("state", 1); // 0 = unplugged  1 = Headset with microphone 2 = Headset without microphone
					headSetUnPluggedintent.putExtra("name", "Headset");
					context.sendOrderedBroadcast(headSetUnPluggedintent, null);
				}
				catch(Exception ex2)
				{
					errorPlug=true;
					Log.d("PhoneManager","Error Plug");
				}
			}
			
			
			Log.d("PhoneManager","try to accept 1");
			Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);          
			buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
			context.sendOrderedBroadcast(buttonDown, "android.permission.CALL_PRIVILEGED");
				
			Log.d("PhoneManager","try to accept 2");
			
			Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);            
			buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
			context.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
				
			Log.d("PhoneManager","try to accept 3");
			
			
			if(headsetOn==false)
			{
				if(errorPlug==false)
				{
					Intent headSetUnPluggedintent2 = new Intent(Intent.ACTION_HEADSET_PLUG);
					headSetUnPluggedintent2.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
					headSetUnPluggedintent2.putExtra("state", 0); // 0 = unplugged  1 = Headset with microphone 2 = Headset without microphone
					headSetUnPluggedintent2.putExtra("name", "Headset");
					context.sendOrderedBroadcast(headSetUnPluggedintent2, null);
				
				}
			}
			else
			{
				Log.d("PhoneManager","No unplug");
			}
			
			

		}catch(Exception ex)
		{
			Log.e("PhoneManager","Answer call " + ex.toString());
			return 1;
		}
		
		return 0;
	}

	/*
	private void detectBTdevices(Context context)
	{
		BluetoothAdapter mBluetoothAdapter = null;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Log.d("PhoneManager", "detect start");
		
		if(mBluetoothAdapter != null)
		{
			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
			if (pairedDevices.size() != 0)
			{
				// the device has paired devices
				for (BluetoothDevice device : pairedDevices)
				{
					Log.d("PhoneManager", "device name: " + device.getName());
					Log.d("PhoneManager", "device address: " + device.getAddress());
					Log.d("PhoneManager", "device bond state: " + device.getBondState());
					Log.d("PhoneManager", "device class: " + device.getBluetoothClass().getDeviceClass());
					Log.d("PhoneManager", "device class2: " + device.getBluetoothClass().getMajorDeviceClass());
				}
			}
			else
			{
				// no paired devices
				Log.d("PhoneManager", "no paired devices");
			}
		}
		else
		{
			Log.d("PhoneManager", "detect error");
		}
	}*/
	
	

}
