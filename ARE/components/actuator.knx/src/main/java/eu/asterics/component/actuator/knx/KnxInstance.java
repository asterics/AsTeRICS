

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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.actuator.knx;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.StringTokenizer;
import java.util.logging.Logger;


import java.net.UnknownHostException;
import tuwien.auto.calimero.CloseEvent;
import tuwien.auto.calimero.FrameEvent;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.datapoint.CommandDP;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.knxnetip.KNXnetIPConnection;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.event.NetworkLinkListener;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl; 

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;

/* 
 * <Describe purpose of this module>
 * 
 * 
 *  
 * @author <your name> [<your email address>]
 *         Date: 
 *         Time: 
 */
public class KnxInstance extends AbstractRuntimeComponentInstance
{
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 
	private KNXNetworkLinkIP netLinkIp = null;
	private ProcessCommunicator pc = null;
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	// Properties
	private boolean propNAT = true;
	private String propLocalIP = new String();
	private String propKnxNetIP = new String();
	private String propGroupAddress1 = new String();
	private String propDataType1 = new String();
	private String propDataValue1 = new String();
	private String propGroupAddress2 = new String();
	private String propDataType2 = new String();
	private String propDataValue2 = new String();
	private String propGroupAddress3 = new String();
	private String propDataType3 = new String();
	private String propDataValue3 = new String();
	private String propGroupAddress4 = new String();
	private String propDataType4 = new String();
	private String propDataValue4 = new String();
	private String propGroupAddress5 = new String();
	private String propDataType5 = new String();
	private String propDataValue5 = new String();
	private String propGroupAddress6 = new String();
	private String propDataType6 = new String();
	private String propDataValue6 = new String();
	
	private String propGroupAddressSlider1 = new String();
	private String propGroupAddressSlider2 = new String();
	private String propGroupAddressSlider3 = new String();
	private String propGroupAddressSlider4 = new String();
	private String propGroupAddressSlider5 = new String();
	private String propGroupAddressSlider6 = new String();
	// declare member variables here

	// Event Listener Ports
	private final String ELP_SEND1 	= "send1";
	private final String ELP_SEND2 	= "send2";
	private final String ELP_SEND3 	= "send3";
	private final String ELP_SEND4 	= "send4";
	private final String ELP_SEND5 	= "send5";		
	private final String ELP_SEND6 	= "send6";
	
	private String propGroupAddressTrigger1 = new String();
	private String propGroupAddressTrigger2 = new String();
	private String propGroupAddressTrigger3 = new String();
	private String propGroupAddressTrigger4 = new String();
	private String propGroupAddressTrigger5 = new String();
	private String propGroupAddressTrigger6 = new String();
	
	final IRuntimeEventTriggererPort runtimeEventTriggererPort1 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort runtimeEventTriggererPort2 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort runtimeEventTriggererPort3 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort runtimeEventTriggererPort4 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort runtimeEventTriggererPort5 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort runtimeEventTriggererPort6 = new DefaultRuntimeEventTriggererPort();
	
    
	
	private double in1=0,in2=0,in3=0,in4=0,in5=0,in6=0;
   /**
    * The class constructor.
    */
    public KnxInstance()
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
		if ("command".equalsIgnoreCase(portID))
		{
			return ipCommand;
		}
		if ("slider1".equalsIgnoreCase(portID))
		{
			return ipSlider1;
		}
		if ("slider2".equalsIgnoreCase(portID))
		{
			return ipSlider2;
		}
		if ("slider3".equalsIgnoreCase(portID))
		{
			return ipSlider3;
		}
		if ("slider4".equalsIgnoreCase(portID))
		{
			return ipSlider4;
		}
		if ("slider5".equalsIgnoreCase(portID))
		{
			return ipSlider5;
		}
		if ("slider6".equalsIgnoreCase(portID))
		{
			return ipSlider6;
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

		return null;
	}

    /**
     * returns an Event Listener Port.
     * @param eventPortID   the name of the port
     * @return         the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
		if ("send1".equalsIgnoreCase(eventPortID))
		{
			return elpSend1;
		}
		if ("send2".equalsIgnoreCase(eventPortID))
		{
			return elpSend2;
		}
		if ("send3".equalsIgnoreCase(eventPortID))
		{
			return elpSend3;
		}
		if ("send4".equalsIgnoreCase(eventPortID))
		{
			return elpSend4;
		}
		if ("send5".equalsIgnoreCase(eventPortID))
		{
			return elpSend5;
		}
		if ("send6".equalsIgnoreCase(eventPortID))
		{
			return elpSend6;
		}

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    
    

    @Override
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
        if("event_out_1".equalsIgnoreCase(eventPortID))
        {
            return runtimeEventTriggererPort1;
        }
        else if("event_out_2".equalsIgnoreCase(eventPortID))
        {
            return runtimeEventTriggererPort2;
        }
        else if("event_out_3".equalsIgnoreCase(eventPortID))
        {
            return runtimeEventTriggererPort3;
        }
        else if("event_out_4".equalsIgnoreCase(eventPortID))
        {
            return runtimeEventTriggererPort4;
        }
        else if("event_out_5".equalsIgnoreCase(eventPortID))
        {
            return runtimeEventTriggererPort5;
        }
        else if("event_out_6".equalsIgnoreCase(eventPortID))
        {
            return runtimeEventTriggererPort6;
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
    	if("localIP".equalsIgnoreCase(propertyName))
        {
            return propLocalIP;
        }
        else if("KNXNetIP".equalsIgnoreCase(propertyName))
        {
        	return propKnxNetIP;
        }
        else if("NAT".equalsIgnoreCase(propertyName))
        {
        	return propNAT;
        }
        else if("groupAddress1".equalsIgnoreCase(propertyName))
        {
        	return propGroupAddress1;
        }
        else if("dataType1".equalsIgnoreCase(propertyName))
        {
        	return propDataType1;
        }
        else if("dataValue1".equalsIgnoreCase(propertyName))
        {
        	return propDataValue1;
        }
        else if("groupAddress2".equalsIgnoreCase(propertyName))
        {
        	return propGroupAddress2;
        }
        else if("dataType2".equalsIgnoreCase(propertyName))
        {
        	return propDataType2;
        }
        else if("dataValue2".equalsIgnoreCase(propertyName))
        {
        	return propDataValue2;
        }
        else if("groupAddress3".equalsIgnoreCase(propertyName))
        {
        	return propGroupAddress3;
        }
        else if("dataType3".equalsIgnoreCase(propertyName))
        {
        	return propDataType3;
        }
        else if("dataValue3".equalsIgnoreCase(propertyName))
        {
        	return propDataValue3;
        }
        else if("groupAddress4".equalsIgnoreCase(propertyName))
        {
        	return propGroupAddress4;
        }
        else if("dataType4".equalsIgnoreCase(propertyName))
        {
        	return propDataType4;
        }
        else if("dataValue4".equalsIgnoreCase(propertyName))
        {
        	return propDataValue4;
        }
        else if("groupAddress5".equalsIgnoreCase(propertyName))
        {
        	return propGroupAddress5;
        }
        else if("dataType5".equalsIgnoreCase(propertyName))
        {
        	return propDataType5;
        }
        else if("dataValue5".equalsIgnoreCase(propertyName))
        {
        	return propDataValue5;
        }
        else if("groupAddress6".equalsIgnoreCase(propertyName))
        {
        	return propGroupAddress6;
        }
        else if("dataType6".equalsIgnoreCase(propertyName))
        {
        	return propDataType6;
        }
        else if("dataValue6".equalsIgnoreCase(propertyName))
        {
        	return propDataValue6;
        }
        else if("groupAddressTrigger1".equalsIgnoreCase(propertyName))
        {
        	return propGroupAddressTrigger1;
        }
        else if("groupAddressTrigger2".equalsIgnoreCase(propertyName))
        {
        	return propGroupAddressTrigger2;
        }
        else if("groupAddressTrigger3".equalsIgnoreCase(propertyName))
        {
        	return propGroupAddressTrigger3;
        }
        else if("groupAddressTrigger4".equalsIgnoreCase(propertyName))
        {
        	return propGroupAddressTrigger4;
        }
        else if("groupAddressTrigger5".equalsIgnoreCase(propertyName))
        {
        	return propGroupAddressTrigger5;
        }
        else if("groupAddressTrigger6".equalsIgnoreCase(propertyName))
        {
        	return propGroupAddressTrigger6;
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
    	Object oldValue = null;
        if("localIP".equalsIgnoreCase(propertyName)) {
            oldValue = propLocalIP;
            propLocalIP = newValue.toString();
        }
        else if("KNXNetIP".equalsIgnoreCase(propertyName)) {
        	oldValue = propKnxNetIP;
        	propKnxNetIP = newValue.toString();
        }
        else if("NAT".equalsIgnoreCase(propertyName)) {
        	oldValue = propNAT;
        	if("true".equalsIgnoreCase((String)newValue)) {
                propNAT = true;
            }
            else if("false".equalsIgnoreCase((String)newValue)) {
                propNAT = false;
            }
        }
        else if("groupAddress1".equalsIgnoreCase(propertyName)) {
        	oldValue = propGroupAddress1;
        	propGroupAddress1 = newValue.toString();
        }
        else if("dataType1".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataType1;
        	propDataType1 = newValue.toString();
        }
        else if("dataValue1".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataValue1;
        	propDataValue1 = newValue.toString();
        }
        else if("groupAddress2".equalsIgnoreCase(propertyName)) {
        	oldValue = propGroupAddress2;
        	propGroupAddress2 = newValue.toString();
        }
        else if("dataType2".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataType2;
        	propDataType2 = newValue.toString();
        }
        else if("dataValue2".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataValue2;
        	propDataValue2 = newValue.toString();
        }
        else if("groupAddress3".equalsIgnoreCase(propertyName)) {
        	oldValue = propGroupAddress3;
        	propGroupAddress3 = newValue.toString();
        }
        else if("dataType3".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataType3;
        	propDataType3 = newValue.toString();
        }
        else if("dataValue3".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataValue3;
        	propDataValue3 = newValue.toString();
        }
        else if("groupAddress4".equalsIgnoreCase(propertyName)) {
        	oldValue = propGroupAddress4;
        	propGroupAddress4 = newValue.toString();
        }
        else if("dataType4".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataType4;
        	propDataType4 = newValue.toString();
        }
        else if("dataValue4".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataValue4;
        	propDataValue4 = newValue.toString();
        }
        else if("groupAddress5".equalsIgnoreCase(propertyName)) {
        	oldValue = propGroupAddress5;
        	propGroupAddress5 = newValue.toString();
        }
        else if("dataType5".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataType5;
        	propDataType5 = newValue.toString();
        }
        else if("dataValue5".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataValue5;
        	propDataValue5 = newValue.toString();
        }
        else if("groupAddress6".equalsIgnoreCase(propertyName)) {
        	oldValue = propGroupAddress6;
        	propGroupAddress6 = newValue.toString();
        }
        else if("dataType6".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataType6;
        	propDataType6 = newValue.toString();
        }
        else if("dataValue6".equalsIgnoreCase(propertyName)) {
        	oldValue = propDataValue6;
        	propDataValue6 = newValue.toString();
        }
        else if("groupAddressSlider1".equalsIgnoreCase(propertyName)) {
        	oldValue = propGroupAddressSlider1;
        	propGroupAddressSlider1 = newValue.toString();
        }
        else if("groupAddressSlider2".equalsIgnoreCase(propertyName)) {
        	oldValue = propGroupAddressSlider2;
        	propGroupAddressSlider2 = newValue.toString();
        }
        else if("groupAddressSlider3".equalsIgnoreCase(propertyName)) {
        	oldValue = propGroupAddressSlider3;
        	propGroupAddressSlider3 = newValue.toString();
        }
        else if("groupAddressSlider4".equalsIgnoreCase(propertyName)) {
        	oldValue = propGroupAddressSlider4;
        	propGroupAddressSlider4 = newValue.toString();
        }
        else if("groupAddressSlider5".equalsIgnoreCase(propertyName)) {
        	oldValue = propGroupAddressSlider5;
        	propGroupAddressSlider5 = newValue.toString();
        }
        else if("groupAddressSlider6".equalsIgnoreCase(propertyName)) {
        	oldValue = propGroupAddressSlider6;
        	propGroupAddressSlider6 = newValue.toString();
        }
        else if("groupAddressTrigger1".equalsIgnoreCase(propertyName)) {
        	oldValue = propGroupAddressTrigger1;
        	propGroupAddressTrigger1 = newValue.toString();
        }
        else if("groupAddressTrigger2".equalsIgnoreCase(propertyName)) {
        	oldValue = propGroupAddressTrigger2;
        	propGroupAddressTrigger2 = newValue.toString();
        }
        else if("groupAddressTrigger3".equalsIgnoreCase(propertyName)) {
        	oldValue = propGroupAddressTrigger3;
        	propGroupAddressTrigger3 = newValue.toString();
        }
        else if("groupAddressTrigger4".equalsIgnoreCase(propertyName)) {
        	oldValue = propGroupAddressTrigger4;
        	propGroupAddressTrigger4 = newValue.toString();
        }
        else if("groupAddressTrigger5".equalsIgnoreCase(propertyName)) {
        	oldValue = propGroupAddressTrigger5;
        	propGroupAddressTrigger5 = newValue.toString();
        }
        else if("groupAddressTrigger6".equalsIgnoreCase(propertyName)) {
        	oldValue = propGroupAddressTrigger6;
        	propGroupAddressTrigger6 = newValue.toString();
        }
        
        return oldValue;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipCommand  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			String text = ConversionUtils.stringFromBytes(data);
	    	// Logger.getAnonymousLogger().info("KNX received: " + text);

    		if (text.startsWith("@KNX:")) {  			
				try {
					
					StringTokenizer st = new StringTokenizer(text.substring(5)," ,#");
			    	// Logger.getAnonymousLogger().info("Tokenizing: " + text.substring(5));
					sendKNX(st.nextToken(),st.nextToken(),st.nextToken());
				} catch (Exception e) {
					Logger.getAnonymousLogger().severe(e.toString());
				}
    		}
		}
	};
	private final IRuntimeInputPort ipSlider1  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			in1=ConversionUtils.doubleFromBytes(data);
            sendKNX(propGroupAddressSlider1,new String("int"),Integer.toString((int)(in1)));
		}
	};
	private final IRuntimeInputPort ipSlider2  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			in2=ConversionUtils.doubleFromBytes(data);
            sendKNX(propGroupAddressSlider2,new String("int"),Integer.toString((int)(in2)));
		}
	};
	private final IRuntimeInputPort ipSlider3  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			in3=ConversionUtils.doubleFromBytes(data);
            sendKNX(propGroupAddressSlider3,new String("int"),Integer.toString((int)(in3)));
		}
	};
	private final IRuntimeInputPort ipSlider4  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			in4=ConversionUtils.doubleFromBytes(data);
            sendKNX(propGroupAddressSlider4,new String("int"),Integer.toString((int)(in4)));
		}
	};
	private final IRuntimeInputPort ipSlider5  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			in5=ConversionUtils.doubleFromBytes(data);
            sendKNX(propGroupAddressSlider5,new String("int"),Integer.toString((int)(in5)));
		}
	};
	private final IRuntimeInputPort ipSlider6  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			in6=ConversionUtils.doubleFromBytes(data);
            sendKNX(propGroupAddressSlider6,new String("int"),Integer.toString((int)(in6)));
		}
	};


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpSend1 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendKNX(propGroupAddress1,propDataType1,propDataValue1);
		}
	};
	final IRuntimeEventListenerPort elpSend2 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendKNX(propGroupAddress2,propDataType2,propDataValue2);
		}
	};
	final IRuntimeEventListenerPort elpSend3 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendKNX(propGroupAddress3,propDataType3,propDataValue3);
		}
	};
	final IRuntimeEventListenerPort elpSend4 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendKNX(propGroupAddress4,propDataType4,propDataValue4);
		}
	};
	final IRuntimeEventListenerPort elpSend5 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendKNX(propGroupAddress5,propDataType5,propDataValue5);
		}
	};
	final IRuntimeEventListenerPort elpSend6 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			sendKNX(propGroupAddress6,propDataType6,propDataValue6);
		}
	};

	
	
	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  openKNXconnection();
          super.start();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
          super.pause();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
          super.resume();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
    	  closeKNXconnection();
          super.stop();
      }

      /**
       * opens a connection to a KNX/IP router via the Calimero library.
       */
      private void openKNXconnection() {
	   		try {
	   			netLinkIp = new KNXNetworkLinkIP(KNXNetworkLinkIP.TUNNEL, 
	   					new InetSocketAddress(InetAddress.getByName(propLocalIP), 0),
	   					new InetSocketAddress(InetAddress.getByName(propKnxNetIP),KNXnetIPConnection.IP_PORT),
	   					propNAT, new TPSettings(false));
	   			pc = new ProcessCommunicatorImpl(netLinkIp);
	   			Logger.getAnonymousLogger().info("KNX connection opened");
	   			
	   			
	   			netLinkIp.addLinkListener(new NetworkLinkListener() {
					@Override
					public void confirmation(FrameEvent arg0) {
					}

					@Override
					public void indication(FrameEvent arg0) {
					//	System.out.println("srcadress " + arg0.getSource());
					//	System.out.println(arg0.getSource().getClass());
					//	System.out.println("targetadresse "
					//			+ ((tuwien.auto.calimero.cemi.CEMILData)arg0.getFrame()).getDestination());
						
						GroupAddress derandere = new GroupAddress (2821);
						GroupAddress bewegungsmelder= new GroupAddress (2307);
						GroupAddress bewegungsmelder2= new GroupAddress (2307);
					//	if (derandere.equals(bewegungsmelder)){System.out.println("yes ");}
						try {
						if(((tuwien.auto.calimero.cemi.CEMILData)arg0.getFrame()).getDestination().equals(new GroupAddress(propGroupAddressTrigger1))){
							System.out.println("matching address 1 ");
							runtimeEventTriggererPort1.raiseEvent();
						}
						if(((tuwien.auto.calimero.cemi.CEMILData)arg0.getFrame()).getDestination().equals(new GroupAddress(propGroupAddressTrigger2))){
							System.out.println("matching address 2 ");
							runtimeEventTriggererPort2.raiseEvent();
						}
						if(((tuwien.auto.calimero.cemi.CEMILData)arg0.getFrame()).getDestination().equals(new GroupAddress(propGroupAddressTrigger3))){
							System.out.println("matching address 3 ");
							runtimeEventTriggererPort3.raiseEvent();
						}
						if(((tuwien.auto.calimero.cemi.CEMILData)arg0.getFrame()).getDestination().equals(new GroupAddress(propGroupAddressTrigger4))){
							System.out.println("matching address 4 ");
							runtimeEventTriggererPort4.raiseEvent();
						}
						if(((tuwien.auto.calimero.cemi.CEMILData)arg0.getFrame()).getDestination().equals(new GroupAddress(propGroupAddressTrigger5))){
							System.out.println("matching address 5 ");
							runtimeEventTriggererPort5.raiseEvent();
						}
						if(((tuwien.auto.calimero.cemi.CEMILData)arg0.getFrame()).getDestination().equals(new GroupAddress(propGroupAddressTrigger6))){
							System.out.println("matching address 6 ");
							runtimeEventTriggererPort6.raiseEvent();
						}
						} catch (Exception e) {
			   				Logger.getAnonymousLogger().severe(e.toString());
			   			}
					}

					@Override
					public void linkClosed(CloseEvent arg0) {
					}
				});

	   		} catch (Exception e) {
	   			netLinkIp = null;
	   			Logger.getAnonymousLogger().severe(e.toString());
	   		}
       }
      
      /**
       * closes a connection to a KNX/IP router.
       */
       private void closeKNXconnection () {
   		if (netLinkIp != null) {
   			netLinkIp.close();
   			Logger.getAnonymousLogger().info("KNX connection closed");
   		}
       }
       
       /**
        * sends a group/type/value command  to a KNX/IP router via the Calimero library.
        */
       private void sendKNX(String groupaddress, String type, String value) {

   		if (netLinkIp != null) {
   			try {
   		    	if (type.equalsIgnoreCase("string")) {
   					pc.write(new GroupAddress(groupaddress), value);	
   				}
   				else if (type.equalsIgnoreCase("boolean")) {
   					if (value.equalsIgnoreCase("1")) {
   						pc.write(new GroupAddress(groupaddress), true);
   					}
   					else if (value.equalsIgnoreCase("0")) {
   						pc.write(new GroupAddress(groupaddress), false);
   					}
   				}
   				else if (type.equalsIgnoreCase("int")) {
   					Integer i = new Integer(value);
   					pc.write(new GroupAddress(groupaddress), i, ProcessCommunicator.UNSCALED);
   				}
   				else if (type.equalsIgnoreCase("float")) {
   					Float f = new Float(value);
   					pc.write(new GroupAddress(groupaddress), f);
   				}
   		
   		    	Logger.getAnonymousLogger().info("sent value: " + value + " type: " + type + " to " + groupaddress);
   		    	
   			} catch (Exception e) {
   				Logger.getAnonymousLogger().severe(e.toString());
   			}
   		}
   		else {
   			Logger.getAnonymousLogger().severe("KNX connection not open");
   		}   	
       }
      
      
}