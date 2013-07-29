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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.actuator.phonecontrol;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Implements the Windows Mobile phone plugin
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Feb 11, 2011
 *         Time: 4:27:47 PM
 */
public class PhoneControlInstance extends AbstractRuntimeComponentInstance
{
  private final String IP_PHONE_ID="phoneID";
  private final String IP_SMS_CONTENT ="SMSContent";
  private final String IP_COMMAND="command";
    
  private final String OP_REMOTE_PHONE_ID="remotePhoneID";
  private final String OP_RECEIVED_SMS="receivedSMS";
  private final String OP_ERROR_NUMBER="errorNumber";
    
  private final String ELP_SEND_SMS="sendSMS";
  private final String ELP_MAKE_PHONE_CALL ="makePhoneCall";
  private final String ELP_ACCEPT_PHONE_CALL ="acceptPhoneCall";
  private final String ELP_DROP_PHONE_CALL ="dropPhoneCall";
  private final String ELP_RECONNECT ="reconnect";
    
  private final String ETP_IDLE_STATE ="idleState";
  private final String ETP_RING_STATE ="ringState";
  private final String ETP_CONNECTED_STATE ="connectedState";
  private final String ETP_NEW_SMS ="newSMS";
  private final String ETP_ERROR ="error";
    
  private final String PROP_DEFAULT_PHONE_ID="defaultPhoneID";
  private final String PROP_BLUETOOTH_PHONE_NAME="bluetoothPhoneName";
  private final String PROP_PORT="port";

  private final OutputPort opReceivedSMS = new OutputPort();
  private final OutputPort opRemotePhoneID = new OutputPort();
  private final OutputErrorPort opErrorNumber = new OutputErrorPort();
    
  private IRuntimeInputPort ipSMSContent = new IpSMSContent();
  private IRuntimeInputPort ipPhoneID = new IpPhoneID();
  private IRuntimeInputPort ipCommand = new IpCommand();
    
  final IRuntimeEventTriggererPort etpIdleState = new DefaultRuntimeEventTriggererPort();
  final IRuntimeEventTriggererPort etpRingState = new DefaultRuntimeEventTriggererPort();
  final IRuntimeEventTriggererPort etpConnectedState = new DefaultRuntimeEventTriggererPort();
  final IRuntimeEventTriggererPort etpNewSMS = new DefaultRuntimeEventTriggererPort();
  final IRuntimeEventTriggererPort etpError = new DefaultRuntimeEventTriggererPort();
    
  private final PhoneControlBridge bridge = new PhoneControlBridge(opReceivedSMS,opRemotePhoneID,opErrorNumber,etpIdleState,etpRingState,etpConnectedState,etpNewSMS,etpError);

  /**
   * The class constructor.
   */
  public PhoneControlInstance()
  {
	  
  }

  /**
   * Returns an Output Port.
   * @param portID   the name of the port
   * @return         the output port
   */ 
  public IRuntimeOutputPort getOutputPort(String portID)
  {
    if(OP_RECEIVED_SMS.equalsIgnoreCase(portID))
    {
      return opReceivedSMS;
    }
    else if(OP_REMOTE_PHONE_ID.equalsIgnoreCase(portID))
    {
      return opRemotePhoneID;
    }
    else if(OP_ERROR_NUMBER.equalsIgnoreCase(portID))
    {
      return opErrorNumber;
    }
        
      return null;
  }
  
  /**
   * Implements default output port.
   */
  public class OutputPort extends DefaultRuntimeOutputPort
  {
    public void sendData(String data)
    {
      super.sendData(ConversionUtils.stringToBytes(data));
    }
  }
  
  /**
   * Implements error output port.
   */  
  public class OutputErrorPort extends DefaultRuntimeOutputPort
  {
    public void sendData(int data)
    {
      super.sendData(ConversionUtils.intToByteArray(data));
    }
  }
   
  /**
   * Returns an Input Port.
   * @param portID   the name of the port
   * @return         the input port or null if not found
   */
  public IRuntimeInputPort getInputPort(String portID)
  {
    if(IP_SMS_CONTENT.equalsIgnoreCase(portID))
    {
      return ipSMSContent;
    }
    else if(IP_PHONE_ID.equalsIgnoreCase(portID))
    {
      return ipPhoneID;
    }
    else if(IP_COMMAND.equalsIgnoreCase(portID))
    {
      return ipCommand;
    }
        
    return null;
        
  }
  
  /**
   * Implements input port for SMS content.
   */  
  private class IpSMSContent extends DefaultRuntimeInputPort
  {
    public void receiveData(byte[] data)
    {
      String text = ConversionUtils.stringFromBytes(data);
      if(text.length()>1)
      {
        bridge.setSMSContent(text);
      }
    }

  }
  
  /**
   * Implements input port for Phone ID.
   */  
  private class IpPhoneID extends DefaultRuntimeInputPort
  {
    public void receiveData(byte[] data)
    {
      String text = ConversionUtils.stringFromBytes(data);
      if(text.length()>1)
      {
        bridge.setPhoneID(text);
      }
          
    }
  }
    
  private final String PHONE_COMMAND="@PHONE:";
  
  /**
   * Implements input port for commands.
   */  
  private class IpCommand extends DefaultRuntimeInputPort
  {
    public void receiveData(byte[] data)
    {
      String text = ConversionUtils.stringFromBytes(data);
      if(text.length()>1)
      {
        if(text.startsWith(PHONE_COMMAND))
        {
          String text2=text.substring(PHONE_COMMAND.length());
          processCommand(text2);
        }
      }
          
    }
  }
    
  private final String SMS_COMMAND="SMS";
  private final String CALL_COMMAND="CALL";
  private final String ACCEPT_COMMAND="ACCEPT";
  private final String DROP_COMMAND="DROP";
  private final String RECONNECT_COMMAND="RECONNECT";
  
  /**
   * Process text command.
   * @param command text command
   */
  private void processCommand(String command)
  {
    command=command.trim();
    int poss=command.indexOf(":");
    	
    boolean commandAccepted=false;
    	
    	
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
    				
    	  String message=command4.substring(poss3+1,poss4);
    	  message=message.trim();
    				
    	  commandAccepted=true;
    	  bridge.setPhoneID(phoneID);
    	  bridge.setSMSContent(message);
    	  bridge.sendSMS();
    	}
    			
      }
      else if(command2.equalsIgnoreCase(CALL_COMMAND))
      {
        String phoneID=command.substring(poss+1);
    	phoneID=phoneID.trim();
    	commandAccepted=true;
    	bridge.setPhoneID(phoneID);
    	bridge.makePhoneCall();
      }
    		
    }
    else
    {
      if(command.equalsIgnoreCase(ACCEPT_COMMAND))
      {
        commandAccepted=true;
    	bridge.accept();
      }
      else if(command.equalsIgnoreCase(DROP_COMMAND))
      {
        commandAccepted=true;
    	bridge.drop();
      }
      else if(command.equalsIgnoreCase(RECONNECT_COMMAND))
      {
        commandAccepted=true;
    	bridge.reconnect();
      }
    }
    	
  }
  
  /**
   * Returns the value of the given property.
   * @param propertyName   the name of the property
   * @return               the property value or null if not found
   */
  public Object getRuntimePropertyValue(String propertyName)
  {
    if(PROP_BLUETOOTH_PHONE_NAME.equalsIgnoreCase(propertyName))
    {
      return bridge.getPhoneName();
    }
    else if(PROP_PORT.equalsIgnoreCase(propertyName))
    {
      return bridge.getPhonePort();
    }
    else if(PROP_DEFAULT_PHONE_ID.equalsIgnoreCase(propertyName))
    {
      return bridge.getDefaultPhoneID();
    }
    return null;
  }

  /**
   * Sets a new value for the given property.
   * @param propertyName   the name of the property
   * @param newValue       the desired property value
   * @return old property  value
   */
  public Object setRuntimePropertyValue(String propertyName, Object newValue)
  {
    if(PROP_BLUETOOTH_PHONE_NAME.equalsIgnoreCase(propertyName))
    {
      final String oldValue = bridge.getPhoneName();
           
      bridge.setPhoneName((String)newValue);
            
      return oldValue;
    }
    else if(PROP_PORT.equalsIgnoreCase(propertyName))
    {
      final Integer oldValue = bridge.getPhonePort();

      int readInterval = Integer.parseInt((String) newValue);
            
      bridge.setPhonePort(readInterval);
        	
      return oldValue;
    }
    else if(PROP_DEFAULT_PHONE_ID.equalsIgnoreCase(propertyName))
    {
      final String oldValue = bridge.getDefaultPhoneID();
           
      bridge.setDefaultPhoneID((String)newValue);
            
      return oldValue;
    }
        

    return null;  
  }
  
  /**
   * Returns an Event Listener Port.
   * @param eventPortID   the name of the port
   * @return         the event listener port or null if not found
   */
  @Override
  public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
  {
    if(ELP_SEND_SMS.equalsIgnoreCase(eventPortID))
    {
      return elpSendSMS;
    }
    else if(ELP_MAKE_PHONE_CALL.equalsIgnoreCase(eventPortID))
    {
      return elpMakePhoneCall;
    }
    else if(ELP_ACCEPT_PHONE_CALL.equalsIgnoreCase(eventPortID))
    {
      return elpAcceptPhoneCall;
    }
    else if(ELP_DROP_PHONE_CALL.equalsIgnoreCase(eventPortID))
    {
      return elpDropPhoneCall;
    }
    else if(ELP_RECONNECT.equalsIgnoreCase(eventPortID))
    {
      return elpReconnect;
    }
        
    return null;
        
  }
  
  /**
   * Implements send SMS event port
   */ 
  final IRuntimeEventListenerPort elpSendSMS 	= new IRuntimeEventListenerPort()
  {
    @Override 
    public void receiveEvent(String data)
    {
      bridge.sendSMS();
    }
  };
  
  /**
   * Implements make phone call event port
   */ 
  final IRuntimeEventListenerPort elpMakePhoneCall 	= new IRuntimeEventListenerPort()
  {
    @Override 
    public void receiveEvent(String data)
    {
      bridge.makePhoneCall();
    }
  };
  
  /**
   * Implements accept phone call event port
   */ 
  final IRuntimeEventListenerPort elpAcceptPhoneCall 	= new IRuntimeEventListenerPort()
  {
    @Override 
    public void receiveEvent(String data)
    {
      bridge.accept();
    }
  };
  
  /**
   * Implements drop phone call event port
   */ 
  final IRuntimeEventListenerPort elpDropPhoneCall 	= new IRuntimeEventListenerPort()
  {
    @Override 
    public void receiveEvent(String data)
    {
      bridge.drop();
    }
  };
  
  /**
   * Implements reconnect event port
   */
  final IRuntimeEventListenerPort elpReconnect 	= new IRuntimeEventListenerPort()
  {
    @Override 
    public void receiveEvent(String data)
    {
      bridge.reconnect();
    }
  };
    
  /**
   * Returns an Event Trigger Port.
   * @param eventPortID   the name of the port
   * @return         the Event Trigger port or null if not found
   */  
  @Override
  public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
  {
    if(ETP_IDLE_STATE.equalsIgnoreCase(eventPortID))
    {
      return etpIdleState;
    }
    else if(ETP_RING_STATE.equalsIgnoreCase(eventPortID))
    {
      return etpRingState;
    }
    else if(ETP_CONNECTED_STATE.equalsIgnoreCase(eventPortID))
    {
      return etpConnectedState;
    }
    else if(ETP_NEW_SMS.equalsIgnoreCase(eventPortID))
    {
      return etpNewSMS;
    }
    else if(ETP_ERROR.equalsIgnoreCase(eventPortID))
    {
      return etpError;
    }

    return null;
  }
    
    
  /**
   * Called when model is started.
   */
  @Override
  public void start()
  {
    bridge.start();
    super.start();
  }

  /**
   * Called when model is paused.
   */
  @Override
  public void pause()
  {
    bridge.stop();
    super.pause();
  }

  /**
   * Called when model is resumed
   */
  @Override
  public void resume()
  {
    bridge.start();
    super.resume();
  }

  /**
   * Called when model is stopped.
   */
  @Override
  public void stop()
  {
    bridge.stop();
    super.stop();
  }
    
}