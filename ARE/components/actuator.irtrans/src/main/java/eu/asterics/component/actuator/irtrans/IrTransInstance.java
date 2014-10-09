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

package eu.asterics.component.actuator.irtrans;

import eu.asterics.mw.data.*;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import java.awt.event.InputEvent;
import java.io.*;
import java.net.*;
//import java.util.logging.Logger;

import eu.asterics.mw.services.AstericsErrorHandling;


/**
 * Component to send commands to the IR-transmitter of the company irtrans (www.irtrans.de)
 * via the ethernet.
 * 
 * 
 * @author Roland Ossmann [ro@ki-i.at]
 *         Date: Feb 08, 2011
 *         Time: 11:08:01 AM
 */
public class IrTransInstance extends AbstractRuntimeComponentInstance
{		
	private final int NUMBER_OF_COMMANDS = 24;
	private final String KEY_PROPERTY_COMMAND = "send";
	private final String KEY_PROPERTY_EVENT = "sendprop";
	
	// prestring will be part of every command, which will be sent
    private String propPrestring="";
    private String propHostname="";
    private String propPort="";

    private IRuntimeInputPort ipAction = new InputPort1();			
		private final IRuntimeOutputPort opOutput = new DefaultRuntimeOutputPort();
		private Thread readerThread;
		private DatagramSocket socketIn = null;

    final EventListenerPortSendString[] elpRuntimeEventListenerCmds = new EventListenerPortSendString[NUMBER_OF_COMMANDS];    

    public IrTransInstance()
    {
    	for (int i = 0; i < NUMBER_OF_COMMANDS; i++)
		{
    		elpRuntimeEventListenerCmds[i] = new EventListenerPortSendString();
		}
    }

    /**
     * Standard method from framework
     * @param portID
     * @return
     */
    public IRuntimeInputPort getInputPort(String portID)
    {
        if("action".equalsIgnoreCase(portID))
        {
            return ipAction;
        }
        else
        {
            return null;
        }
    }

    /**
     * Standard method from framework
     * @param portID
     * @return
     */
    public IRuntimeOutputPort getOutputPort(String portID)
    {
        if("output".equalsIgnoreCase(portID))
        {
            return opOutput;
        }
        else
        {
            return null;
        }
    }
    
    /**
     * Standard method from framework
     * @param portID
     * @return
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {

    	for (int i = 0; i < NUMBER_OF_COMMANDS; i++)
		{
			String s = KEY_PROPERTY_EVENT + (i + 1);
			if (s.equalsIgnoreCase(eventPortID))
			{
	            return elpRuntimeEventListenerCmds[i];
			}
		}
        return null;
        
    }

    /**
     * Standard method from framework
     * @param portID
     * @return
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
    	//Logger.getAnonymousLogger().info("IRTrans, getRuntimePropertyValue");
    	//System.out.println("IRTrans, getRuntimePropertyValue");

        if("prestring".equalsIgnoreCase(propertyName))
        {
            return propPrestring;
        }
        else if("hostname".equalsIgnoreCase(propertyName))
        {
            return propHostname;
        }
        else if("port".equalsIgnoreCase(propertyName))
        {
            return propPort;
        }
        else
        {
            for (int i = 0; i < NUMBER_OF_COMMANDS; i++)
           	{
            		String s = KEY_PROPERTY_COMMAND + (i + 1);
            		if (s.equalsIgnoreCase(propertyName))
            		{
            			return (elpRuntimeEventListenerCmds[i].stringToSend);
            		}
           	}
        }
        return null;
    }

    /**
     * Standard method from framework
     * @param portID
     * @return
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
    	
        if("prestring".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propPrestring;
            propPrestring = (String)newValue;
            return oldValue;
        }
        else if("hostname".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propHostname;
            propHostname = (String)newValue;
            return oldValue;
        }
        else if("port".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propPort;
            propPort = (String)newValue;
            return oldValue;
        }
        else
        {
            for (int i = 0; i < NUMBER_OF_COMMANDS; i++)
           	{
        		String s = KEY_PROPERTY_COMMAND + (i + 1);
        		if (s.equalsIgnoreCase(propertyName))
        		{
                    final Object oldValue = elpRuntimeEventListenerCmds[i].stringToSend;
                    elpRuntimeEventListenerCmds[i].stringToSend=(String)newValue;
        			return oldValue;
        		}
           	}
        }        	
        return null;
    }
    
    /**
     * Standard method from framework
     * @param portID
     * @return
     */ 
    private class InputPort1 extends DefaultRuntimeInputPort
    {
        public void receiveData(byte[] data)
        {
            String text = ConversionUtils.stringFromBytes(data);
    		if (text.startsWith("@IRTRANS:")) {  			
	    		sendString((text.substring(9).trim())); 
    		}
            
        }

    }
    
    /**
     * Standard method from framework
     * @param portID
     * @return
     */
    class EventListenerPortSendString implements IRuntimeEventListenerPort
    {
    	 String stringToSend="";
    	 public void receiveEvent(final String data)
    	 {
	    	sendString(propPrestring + stringToSend);
    	 }
    };    

    /**
     * Creating the string, which will be sent as a UDP package.
     * This string contains the host and port, the prestring (which contains the selected remote)
     * and the command itself (defined in the properties)
     * @param stringToSend The string to be sent
     */
    private void sendString(String stringToSend) {
		 int intPort;
		 InetAddress address;
		 byte[] sendBuf;
		 		 
		 DatagramSocket socketIn = null;
		 DatagramSocket socketOut = null;
		 DatagramPacket packet;
		 
		 try {
    		socketOut = new DatagramSocket();
    		
    		//String sendString = prestring + eventSendString; 
    		sendBuf = stringToSend.getBytes();
    		 
    		address = InetAddress.getByName(propHostname);
    		intPort = Integer.parseInt(propPort);
    		packet = new DatagramPacket(sendBuf, sendBuf.length, address, intPort);
    		socketOut.send(packet);
    	    AstericsErrorHandling.instance.reportInfo(this, "IRTrans sent data: "+stringToSend);
    	    socketOut.close();
    		
    	    // This block just receives the answers, not required
    	    /*
    	    Thread.sleep(100);
    	    socketIn = new DatagramSocket(21000);
    		byte[] buf = new byte[256];
    		packet = new DatagramPacket(buf, buf.length);
    		socketIn.receive(packet);
    		String received = new String(packet.getData(), 0, packet.getLength());
    		System.out.println("IRTrans, received: " + received);
    		socketIn.close();
    		*/
    		
		 } catch (Exception e) {
			 AstericsErrorHandling.instance.reportError(this,"The IRTrans - Plugin could not send data. Please verify that the IRTrans Module is connected and installed, and that the IP-address is correctly specified.");
			 AstericsErrorHandling.instance.reportInfo(this,e.toString());
		 }   	
    }

		private void receiveCommand () {
			readerThread = new Thread(new Runnable() {
			

				@Override
				public void run() {
					try {
						socketIn = new DatagramSocket(21000);
		    		byte[] buf = new byte[256];
		    		DatagramPacket packet = new DatagramPacket(buf, buf.length);
						while (readerThread.isInterrupted() == false) {
				    		socketIn.receive(packet);
				    		String received = new String(packet.getData(), 0, packet.getLength());
				    		opOutput.sendData(ConversionUtils.stringToBytes(received));
				    		System.out.println("IRTrans, received: " + received);
						}
		        socketIn.close();
					} catch (Exception e) {
		    		//e.printStackTrace();
					}   	
				}
   		});
			readerThread.start();
		}
    
    /**
     * Standard method from framework
     * @param portID
     * @return
     */
    @Override
    public void start()
    {
        super.start();
        receiveCommand();
        AstericsErrorHandling.instance.reportInfo(this, "IRTransmitter Instance started");
    }

    /**
     * Standard method from framework
     * @param portID
     * @return
     */
    @Override
    public void stop()
    {
        readerThread.interrupt();
        if(socketIn!=null) {
        	try{
        		socketIn.close();
        	}catch(Exception e) {        		
        	}
        }
        socketIn=null;
        AstericsErrorHandling.instance.reportInfo(this, "IRTransmitter Instance stopped");
        super.stop();
    }
}