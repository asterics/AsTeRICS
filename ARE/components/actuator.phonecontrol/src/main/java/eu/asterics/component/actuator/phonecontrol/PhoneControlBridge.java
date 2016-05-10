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
 *    This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.actuator.phonecontrol;

import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;


/**
 * Interfaces the phone library for the phone plugin
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Feb 11, 2011
 *         Time: 4:27:47 PM
 */
public class PhoneControlBridge implements Runnable
{
  /**
   * Statically load the native library
   */
  static   
  {  
    System.loadLibrary("PhoneLibrary");    
    AstericsErrorHandling.instance.getLogger().fine("Loading \"PhoneLibrary.dll\" ... ok!");
        
    System.loadLibrary("PhoneBridge");
    AstericsErrorHandling.instance.getLogger().fine("Loading \"PhoneBridge.dll\" ... ok!");
  
  }


  private boolean busy;
  String phoneName;
  int phonePort;
  boolean connectionEstablished=false;
    
  private final PhoneControlInstance.OutputPort opReceivedSMS;
  private final PhoneControlInstance.OutputPort phoneIDOut;
  private final PhoneControlInstance.OutputErrorPort opErrorNumber;
    
  final IRuntimeEventTriggererPort etpIdleState;
  final IRuntimeEventTriggererPort etpRingState;
  final IRuntimeEventTriggererPort etpConnectedState;
  final IRuntimeEventTriggererPort etpNewSMS;
  final IRuntimeEventTriggererPort etpError;
  
  /**
   * The class constructor.
   * 
   * @param opReceivedSMS SMS content output port
   * @param phoneIDOut phone ID output port
   * @param opRrrorNumber error number output port
   * @param etpIdleState phone idle output event port
   * @param etpRingState phone ring output event port
   * @param etpConnectedState phone connected output event port
   * @param etpNewSMS new SMS available output event port
   * @param etpError error output event port
   */
  public PhoneControlBridge(final PhoneControlInstance.OutputPort opReceivedSMS,
                  final PhoneControlInstance.OutputPort phoneIDOut,
                  final PhoneControlInstance.OutputErrorPort opErrorNumber,
                  final IRuntimeEventTriggererPort etpIdleState,
                  final IRuntimeEventTriggererPort etpRingState,
                  final IRuntimeEventTriggererPort etpConnectedState,
                  final IRuntimeEventTriggererPort etpNewSMS,
                  final IRuntimeEventTriggererPort etpError)
  {
    this.opReceivedSMS = opReceivedSMS;
    this.phoneIDOut = phoneIDOut;
    this.opErrorNumber = opErrorNumber;
        
    this.etpIdleState=etpIdleState;
    this.etpRingState=etpRingState;
    this.etpConnectedState=etpConnectedState;
    this.etpNewSMS=etpNewSMS;
    this.etpError=etpError;
        
    this.phoneID=defaultPhoneID;
        //t=new Thread(this);
    busy=false;
		//t.start();
  }
    
  /**
   * Gets phone port
   * 
   * @return phone port
   */
  public int getPhonePort()
  {
    return phonePort;
  }
  
  /**
   * Sets phone port
   * 
   * @param phone port
   */
  public void setPhonePort(int port)
  {
    phonePort=port;
  }
  
  /**
   * Gets phone name
   * 
   * @return phone name
   */
  public String getPhoneName()
  {
    return phoneName;
  }
  
  /**
   * Sets phone name
   * 
   * @param phone name
   */
  public void setPhoneName(String name)
  {
    phoneName=name;
  }
    
  private final int Phone_Not_Found = -2012;
  
  /**
   * Starts the class.
   */
  public void start()
  {
    	
    AstericsErrorHandling.instance.getLogger().fine("Phone initializing, search for the phone ...");
    connectionEstablished=false;
    	
    if(phoneName.length()>0)
    {
      AstericsErrorHandling.instance.getLogger().fine("activate: " + phoneName + "port: " + Integer.toString(phonePort));
      int result = activate(phoneName, phonePort);
      AstericsErrorHandling.instance.getLogger().fine("result: " +Integer.toString(result));
      if(result<0)
      {
        if(result==Phone_Not_Found)
    	{
    	  AstericsErrorHandling.instance.getLogger().warning("Phone not found");
    	}
    	else
    	{
    	  AstericsErrorHandling.instance.getLogger().warning("Phone initializing error: "+Integer.toString(result) );
    	}
      }
    }
    else
    {
      AstericsErrorHandling.instance.getLogger().warning("Error No phone name!!!");
    }
    	
  }
  
  /**
   * Stops the class
   */  
  public void stop()
  { 	
    AstericsErrorHandling.instance.getLogger().fine("Stop Phone... ");
    int result=deactivate();
    if(result<0)
    {
      AstericsErrorHandling.instance.getLogger().warning("Phone stop Error!");
    }
    AstericsErrorHandling.instance.getLogger().fine("Stop Phone OK ");
  }
    
  /**
   * Thread function used for make phone calls and send SMS.
   */   
  public void run()
  {
    try{
			//t.sleep(500);
      Thread.sleep(500);
	}catch (InterruptedException e) {}
    	
	int result=0;
	if(actionType==ActionType.Send_SMS)
	{	
	  if((phoneID.length()>0)&&(SMSContent.length()>0))
	  {
	    result = sendSMS(phoneID,SMSContent);
	  }
	}
	else if(actionType==ActionType.Make_Phone_Call)
	{
	  if(phoneID.length()>0)
	  {
	    result = makePhoneCall(phoneID);
	  }
	}
		
    if(result<0)
	{
	  opErrorNumber.sendData(result);
	  etpError.raiseEvent();
	}
		
	busy=false;
	actionType=ActionType.None;
  }
    
  enum ActionType {Send_SMS,Make_Phone_Call,None}
    
  private ActionType actionType;
  private String defaultPhoneID="";
  private String phoneID="";
  private String SMSContent="";
  
  /**
   * Makes phone call.
   */   
  public void makePhoneCall()
  {
    int result=0;
    	
    if(connectionEstablished==false)
    {
      return;
    }
    	
    if(busy)
    {
      return;
    }
    busy=true;
    actionType=ActionType.Make_Phone_Call;
    	
    AstericsThreadPool.instance.execute(this);
    	/*
    	result = makePhoneCall(phoneID);
    	if(result<0)
		{
			opRrrorNumber.sendData(result);
			etpError.raiseEvent();
		}*/
  }
  
  /**
   * Sends SMS.
   */ 
  public void sendSMS()
  {
    int result=0;
    	
    if(connectionEstablished==false)
    {
      return;
    }
    	
    if(busy)
    {
      return;
    }
    busy=true;
    actionType=ActionType.Send_SMS;
    	
    	//t=new Thread(this);
    	//t.start();
    AstericsThreadPool.instance.execute(this);
    	
    	/*
    	result = sendSMS(phoneID,SMSContent);
    	if(result<0)
		{
			opRrrorNumber.sendData(result);
			etpError.raiseEvent();
		}*/
  }
  
  /**
   * Accepts phone calls.
   */
  public void accept()
  {
    if(connectionEstablished==false)
    {
      return;
    }
    	
    int result=0;
    result=acceptCall ();
    if(result<0)
	{
	  opErrorNumber.sendData(result);
	  etpError.raiseEvent();
	}
  }
    
  /**
   * Drops phone calls.
   */
  public void drop()
  {
    if(connectionEstablished==false)
    {
      return;
    }
    	
    int result=0;
    result=dropCall ();
    if(result<0)
	{
      opErrorNumber.sendData(result);
	  etpError.raiseEvent();
	}
  }
    
  private final int Library_No_Initialized = -2;
  
  /**
   * Reconnects the phone.
   */
  public void reconnect()
  {
    int result=0;
    result=reconnectPhone(phoneName, phonePort);
    if(result<0)
	{
	  if(result==Library_No_Initialized)
	  {
	    result = activate(phoneName, phonePort);
		if(result<0)
		{
		  AstericsErrorHandling.instance.getLogger().warning("Phone reconnect error: " + Integer.toString(result));
		  opErrorNumber.sendData(result);
		  etpError.raiseEvent();
		 }
	   }
	   else
	   {
	     AstericsErrorHandling.instance.getLogger().warning("Phone reconnect error: " + Integer.toString(result));
		 opErrorNumber.sendData(result);
		 etpError.raiseEvent();
	   }
	 }
   }
  
  /**
   * Sets the default phone name.
   * 
   * @param phone name
   */
  public void setDefaultPhoneID(String phoneID)
  {
    defaultPhoneID=phoneID;
    this.phoneID=defaultPhoneID;
  }
  
  /**
   * Gets the default phone ID.
   * 
   * @return phone name
   */
  public String getDefaultPhoneID()
  {
    return defaultPhoneID;
  }
  
  /**
   * Sets phone ID.
   * 
   * @param phone ID
   */
  public void setPhoneID(String phoneID)
  {
    this.phoneID=phoneID;
    	
  }
  
  /**
   * Sets SMS content.
   * 
   * @param SMS content
   */
  public void setSMSContent(String SMSContent)
  {
    this.SMSContent=SMSContent;
  }
    
  /**
   * Activates the underlying native code/hardware.
   *
   * @param phoneBTName the Bluetooth name of the phone to be accessed
   * @param portNumber the phone port Number
   * @return 1 if everything was OK, a negative number corresponding to an
   * error code otherwise
   */
  native public int activate(String phoneBTName, int portNumber);
    
  /**
   * Reconnects the phone.
   *
   * @param phoneBTName the Bluetooth name of the phone to be accessed
   * @param portNumber the phone port Number
   * @return 1 if everything was OK, a negative number corresponding to an
   * error code otherwise
   */
  native public int reconnectPhone(String phoneBTName, int portNumber);

  /**
   * Deactivates the underlying native code/hardware.
   *
   * @return 1 if everything was OK, a negative number corresponding to an
   * error code otherwise
   */
  native public int deactivate();
    
  /**
   * Accept incoming call.
   *
   * @return 1 if everything was OK, a negative number corresponding to an
   * error code otherwise
   */
  native public int acceptCall ();
    
  /**
   * Drop incoming call or cancel call.
   *
   * @return 1 if everything was OK, a negative number corresponding to an
   * error code otherwise
   */
  native public int dropCall ();
    
  /**
   * Make phone call.
   *
   * @param phoneID is the recipient phone ID
   * @return 1 if everything was OK, a negative number corresponding to an
   * error code otherwise
   */
  native public int makePhoneCall (String phoneID);
    
  /**
   * Send SMS.
   *
   * @param phoneID is the recipient phone ID
   * @param content is the message content
   * @return 1 if everything was OK, a negative number corresponding to an
   * error code otherwise
   */
  native public int sendSMS (String phoneID, String content);
   
  /**
   * This method is called back from the native code when there is a new SMS
   *
   * @param phoneID is the sender phone ID
   * @param content is the message content
   */
  private void newSMS_callback(String phoneID, String content)
  {
    phoneIDOut.sendData(phoneID);
    opReceivedSMS.sendData(content);
    etpNewSMS.raiseEvent();
    	
  }
    
  /**
   * This method is called back from the native code when there is a new SMS
   *
   * @param phoneState is the current phone state
   * @param phoneID is the caller phone ID
   */
  private void phoneStateChange_callback(final int phoneState, String phoneID)
  {
    if(phoneState==1)
    {
      etpIdleState.raiseEvent();
    }
    else if(phoneState==2)
    {
      phoneIDOut.sendData(phoneID);
      etpRingState.raiseEvent();
    }
    else if(phoneState==3)
    {
      phoneIDOut.sendData(phoneID);
      etpConnectedState.raiseEvent();
    }
    	
  }
    
  /**
   * This method is called to return result of connection establish.   
   *
   * @param result is the connection establish result
   */
  private void connectionState_callback(int result)
  {
    if(result>0)
    {
      connectionEstablished=true;
      AstericsErrorHandling.instance.getLogger().fine("Phone connected !!!");
    }
    else
    {
      AstericsErrorHandling.instance.getLogger().warning("Phone not connected: " + Integer.toString(result));
    }
    	
  }
      
}