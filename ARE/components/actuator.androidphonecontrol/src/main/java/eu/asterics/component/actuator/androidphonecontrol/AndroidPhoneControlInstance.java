

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
 *       This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.actuator.androidphonecontrol;


import java.util.logging.Logger;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsThreadPool;

/**
 * 
 * Implements Android phone control plugin.
 *  
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Jun 04, 2012
 *         Time: 13:24:48 AM
 */
public class AndroidPhoneControlInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opRemotePhoneID = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opReceivedSMS = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opErrorNumber = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpIdleState = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpRingState = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpConnectedState = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpNewSMS = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpError = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	int propConnectionType = 0;
	String propIP = "localhost";
	int propPort = 21111;
	String propDefaultPhoneID = "-";
	
	String phoneNb="";
	String message="";
	
	// declare member variables here

	CommunicationManager cm=new CommunicationManager(opRemotePhoneID,opReceivedSMS,opErrorNumber,etpIdleState,etpRingState,etpConnectedState,etpNewSMS,etpError,this);
    
   /**
    * The class constructor.
    */
    public AndroidPhoneControlInstance()
    {
        // empty constructor
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
		if ("phoneID".equalsIgnoreCase(portID))
		{
			return ipPhoneID;
		}
		if ("SMSContent".equalsIgnoreCase(portID))
		{
			return ipSMSContent;
		}
		if ("action".equalsIgnoreCase(portID))
		{
			return ipAction;
		}

		return null;
	}

    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID)
	{
		if ("remotePhoneID".equalsIgnoreCase(portID))
		{
			return opRemotePhoneID;
		}
		if ("receivedSMS".equalsIgnoreCase(portID))
		{
			return opReceivedSMS;
		}
		if ("errorNumber".equalsIgnoreCase(portID))
		{
			return opErrorNumber;
		}

		return null;
	}

    /**
     * returns an Event Listener Port.
     * @param eventPortID   the name of the port
     * @return         the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
		if ("sendSMS".equalsIgnoreCase(eventPortID))
		{
			return elpSendSMS;
		}
		if ("makePhoneCall".equalsIgnoreCase(eventPortID))
		{
			return elpMakePhoneCall;
		}
		if ("acceptPhoneCall".equalsIgnoreCase(eventPortID))
		{
			return elpAcceptPhoneCall;
		}
		if ("dropPhoneCall".equalsIgnoreCase(eventPortID))
		{
			return elpDropPhoneCall;
		}

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
		if ("idleState".equalsIgnoreCase(eventPortID))
		{
			return etpIdleState;
		}
		if ("ringState".equalsIgnoreCase(eventPortID))
		{
			return etpRingState;
		}
		if ("connectedState".equalsIgnoreCase(eventPortID))
		{
			return etpConnectedState;
		}
		if ("newSMS".equalsIgnoreCase(eventPortID))
		{
			return etpNewSMS;
		}
		if ("error".equalsIgnoreCase(eventPortID))
		{
			return etpError;
		}

        return null;
    }
		
    /**
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
		if ("connectionType".equalsIgnoreCase(propertyName))
		{
			return propConnectionType;
		}
		if ("iP".equalsIgnoreCase(propertyName))
		{
			return propIP;
		}
		if ("port".equalsIgnoreCase(propertyName))
		{
			return propPort;
		}
		if ("defaultPhoneID".equalsIgnoreCase(propertyName))
		{
			return propDefaultPhoneID;
		}

        return null;
    }

    /**
     * sets a new value for the given property.
     * @param propertyName   the name of the property
     * @param newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
		if ("connectionType".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propConnectionType;
			propConnectionType = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("iP".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propIP;
			propIP = (String)newValue;
			return oldValue;
		}
		if ("port".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propPort;
			propPort = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("defaultPhoneID".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propDefaultPhoneID;
			propDefaultPhoneID = (String)newValue;
			phoneNb=propDefaultPhoneID;
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipPhoneID  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{ 
			phoneNb = ConversionUtils.stringFromBytes(data);  
		}
	};
	private final IRuntimeInputPort ipSMSContent  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{ 
			message = ConversionUtils.stringFromBytes(data);  
		}
	};
	private final IRuntimeInputPort ipAction  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			String text = ConversionUtils.stringFromBytes(data);
		      if(text.length()>=PHONE_COMMAND.length())
		      {
		        
		    	  String header = text.trim().substring(0,PHONE_COMMAND.length());
		    	  if(header.equalsIgnoreCase(PHONE_COMMAND))
		    	  {
		    		  String text2=text.trim().substring(PHONE_COMMAND.length());
			          processCommand(text2);
		    	  }
		    	/*if(text.startsWith(PHONE_COMMAND))
		        {
		          String text2=text.substring(PHONE_COMMAND.length());
		          processCommand(text2);
		        }*/
		        
		      }
		}
	};
	
	private final String PHONE_COMMAND="@PHONE:";
	private final String SMS_COMMAND="SMS";
	private final String CALL_COMMAND="CALL";
	private final String ACCEPT_COMMAND="ACCEPT";
	private final String DROP_COMMAND="DROP";
	private final String SET_SMS_COMMAND="SET_SMS";
	private final String SET_ID_COMMAND="SET_ID";
	  
	  /**
	   * Process text command.
	   * @param command text command
	   */
	  private void processCommand(String command)
	  {
	    command=command.trim();
	    
	    
	    int poss=command.indexOf(":");
	    	
	    	
	    if(poss>0)
	    {
	      String command2=command.substring(0,poss);
	      command2=command2.trim();
	 	
	      if(command2.equalsIgnoreCase(SMS_COMMAND))
	      {
	        String command3=command.substring(poss+1);
	    	command3=command3.trim();
	    			
	    	int poss2=command3.indexOf(",");
	    			
	    	if(poss2>0)
	    	{
	    	  String phoneID=command3.substring(0,poss2);
	    	  phoneID=phoneID.trim();
	    				
	    	  String command4=command3.substring(poss2+1);
	    	  command4 = command4.trim();
	    				
	    	  int poss3=command4.indexOf("\"");
	    	  int poss4=command4.lastIndexOf("\"");
	    				
	    	  String smsContent=command4.substring(poss3+1,poss4);
	    	  smsContent=smsContent.trim();
	    	
	    	  
	    	  if(!busy){
	    		  message=smsContent;
		    	  phoneNb=phoneID;
	    		  sentCommand=ProtocolService.Command.SendSMS;
	    		  AstericsThreadPool.instance.execute(delay);
			  }
	    	  
	    	}
	    			
	      }
	      else if(command2.equalsIgnoreCase(CALL_COMMAND))
	      {
	        String phoneID=command.substring(poss+1);
	    	phoneID=phoneID.trim();
	    	if(!busy)
			{
	    	    phoneNb=phoneID;
	    		sentCommand=ProtocolService.Command.Call;
				AstericsThreadPool.instance.execute(delay);
			}
	      }
	      else if(command2.equalsIgnoreCase(SET_SMS_COMMAND))
		  {
	    	  String commandData=command.substring(poss+1).trim();
	    	  if((commandData.charAt(0)=='\"')&&(commandData.charAt(commandData.length()-1)=='\"'))
			  {
			  
				  commandData=commandData.substring(1,commandData.length()-1);
				  if(commandData.length()>0)
				  {
					  if(!busy){
						  message=commandData;
					  }
				  }
			  }
		  }else if(command2.equalsIgnoreCase(SET_ID_COMMAND)){
			  String commandData=command.substring(poss+1);
			  if(commandData.length()>0)
			  {
				  if(!busy){
					  phoneNb=commandData;
				  }
				  
			  }
		  }
	    		
	    }
	    else
	    {
	      if(command.equalsIgnoreCase(ACCEPT_COMMAND))
	      {
	        if(!busy)
			{
				sentCommand=ProtocolService.Command.Accept;
				AstericsThreadPool.instance.execute(delay);
			}
	      }
	      else if(command.equalsIgnoreCase(DROP_COMMAND))
	      {
	        if(!busy)
			{
				sentCommand=ProtocolService.Command.Drop;
				AstericsThreadPool.instance.execute(delay);
			}
	     
	      }
	      else if(command.equalsIgnoreCase(SMS_COMMAND))
	      {
	    	  if(!busy){  
				  sentCommand=ProtocolService.Command.SendSMS;
				  AstericsThreadPool.instance.execute(delay);
			  }
	      }
	      else if(command.equalsIgnoreCase(CALL_COMMAND))
	      {
	    	  if(!busy){ 
				  sentCommand=ProtocolService.Command.Call;
				  AstericsThreadPool.instance.execute(delay);
			  }
	      }
	    	
	    }
	    
	  }
	  
	  private final char BEGIN_CHAR='<';
	  private final char END_CHAR='>';
	 
	  
	  /**
	   * Process text command from the second set.
	   * @param command text command
	   */
	  /*
	  private boolean processCommandFromSecondSet(String command)
	  {
		  String commandToCheck=command.trim();
		  
		  
		  if(commandToCheck.length()<2){
			  return false;
		  }
		  
		  char firstChar = commandToCheck.charAt(0);
		  char endChar=commandToCheck.charAt(commandToCheck.length()-1);
		  
		  if((firstChar!=BEGIN_CHAR)||(endChar!=END_CHAR))
		  {
			  return false;
		  }
		  
		  String processCommand=commandToCheck.substring(1, commandToCheck.length()-1);
		  
		  int index = processCommand.indexOf(':');
		  
		  if(index<1)
		  {
			  return false;
		  }
		  
		  
		  String commandHeader= processCommand.substring(0, index+1);
		  
		  commandHeader=commandHeader.trim();
		  
		  if(!commandHeader.equalsIgnoreCase(PHONE_COMMAND))
		  {
			  return false;
		  }
		  
		  String commandContent = processCommand.substring(index+1, processCommand.length());
		  
		  
		  int index2=commandContent.indexOf(':');
		  
		  String commandName="";
		  String commandData="";
		  
		  if(index2==0)
		  {
			  return false;
		  }
		  
		  if(index2>0)
		  {
			  commandName=commandContent.substring(0,index2);
			  commandName=commandName.trim();
			  commandData=commandContent.substring(index2+1);
			  commandData=commandData.trim();
		  }
		  else
		  {
			  commandName=commandContent.trim();
		  }
			
		  boolean result=false;
		  
		  
		  if(commandName.equalsIgnoreCase(SET_SMS_COMMAND))
		  {
			  if((commandData.charAt(0)=='\"')&&(commandData.charAt(commandData.length()-1)=='\"'))
			  {
			  
				  commandData=commandData.substring(1,commandData.length()-1);
				  if(commandData.length()>0)
				  {
					  if(!busy){
						  message=commandData;
					  }
					  result=true;
				  }
			  }
		  }else if(commandName.equalsIgnoreCase(SET_ID_COMMAND)){
			  if(commandData.length()>0)
			  {
				  if(!busy){
					  phoneNb=commandData;
				  }
				  
				  result=true;
			  }
		  }else if (commandName.equalsIgnoreCase(SMS_COMMAND)){
			  if(!busy){  
				  sentCommand=ProtocolService.Command.SendSMS;
				  AstericsThreadPool.instance.execute(delay);
			  }
			  result=true;
		  }else if (commandName.equalsIgnoreCase(CALL_COMMAND)){
			  if(!busy){ 
				  sentCommand=ProtocolService.Command.Call;
				  AstericsThreadPool.instance.execute(delay);
			  }
			  result=true;
		  }else if (commandName.equalsIgnoreCase(ACCEPT_COMMAND)){
			  if(!busy){ 	  
				  sentCommand=ProtocolService.Command.Accept;
				  AstericsThreadPool.instance.execute(delay);
			  }
			  result=true;
		  }else if (commandName.equalsIgnoreCase(DROP_COMMAND)){
			  if(!busy){ 
				  sentCommand=ProtocolService.Command.Drop;
				  AstericsThreadPool.instance.execute(delay);
			  }
			  result=true;
		  }
		  
		  return result;
		  
	  }*/


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpSendSMS = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if(!busy)
			{
				
				sentCommand=ProtocolService.Command.SendSMS;
				AstericsThreadPool.instance.execute(delay);
				//cm.addCommand(ProtocolService.Command.SendSMS,phoneNb,message);
			}
		}
	};
	final IRuntimeEventListenerPort elpMakePhoneCall = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if(!busy)
			{
				sentCommand=ProtocolService.Command.Call;
				AstericsThreadPool.instance.execute(delay);
				//cm.addCommand(ProtocolService.Command.Call,phoneNb,"");
			}
		}
	};
	final IRuntimeEventListenerPort elpAcceptPhoneCall = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if(!busy)
			{
				sentCommand=ProtocolService.Command.Accept;
				AstericsThreadPool.instance.execute(delay);
				//cm.addCommand(ProtocolService.Command.Accept,"","");
			}
		}
	};
	final IRuntimeEventListenerPort elpDropPhoneCall = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if(!busy)
			{
				sentCommand=ProtocolService.Command.Drop;
				AstericsThreadPool.instance.execute(delay);
				//cm.addCommand(ProtocolService.Command.Drop,"","");
			}
		}
	};

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  busy=false;
    	  super.start();
    	  boolean client=false;
    	  if(propConnectionType==1)
    	  {
    		  client=true;
    	  }
    	  cm.start(client,propIP,propPort);
          
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
          super.pause();
          cm.setPause(true);
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
          busy=false;
    	  super.resume();
          cm.setPause(false);
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
    	  super.stop();
    	  cm.stop();
          
      }
      
      ProtocolService.Command sentCommand=ProtocolService.Command.None;
      boolean busy=false;
      
      
      /**
  	   * Implements delay for command send.
  	   */
      private final Runnable delay = new Runnable(){
  		
    	/**
    	 * Thread method.
    	 */
  		@Override
  		public void run() {
  			//Call,Accept, Drop, SendSMS
  			busy=true;
  			try{
  				  Thread.sleep(300);
  			}catch (InterruptedException e) {}
  			
  			
  			if((sentCommand==ProtocolService.Command.Accept)||(sentCommand==ProtocolService.Command.Drop))
  			{
  				
  				cm.addCommand(sentCommand,"","");
  			}
  			else if(sentCommand==ProtocolService.Command.Call)
  			{
  				cm.addCommand(sentCommand,phoneNb,"");
  			}
  			else if(sentCommand==ProtocolService.Command.SendSMS)
  			{
  				cm.addCommand(sentCommand,phoneNb,message);
  			}
  			busy=false;
  		}
  		};
}