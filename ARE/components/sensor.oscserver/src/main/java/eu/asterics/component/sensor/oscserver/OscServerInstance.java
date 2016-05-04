

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

package eu.asterics.component.sensor.oscserver;

import de.sciss.net.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.logging.Logger;

import de.sciss.net.OSCChannel;
import de.sciss.net.OSCListener;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCServer;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;

/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 *  
 * @author <your name> [<your email address>]
 *         Date: 
 *         Time: 
 */
public class OscServerInstance extends AbstractRuntimeComponentInstance
{
	public final int NUMBER_OF_OUTPORTS = 12;
	private final String OP_CH_PREFIX ="cH";
	
	
//	final IRuntimeOutputPort opCH1 = new DefaultRuntimeOutputPort();
//	final IRuntimeOutputPort opCH2 = new DefaultRuntimeOutputPort();
//	final IRuntimeOutputPort opCH3 = new DefaultRuntimeOutputPort();
//	final IRuntimeOutputPort opCH4 = new DefaultRuntimeOutputPort();
//	final IRuntimeOutputPort opCH5 = new DefaultRuntimeOutputPort();
//	final IRuntimeOutputPort opCH6 = new DefaultRuntimeOutputPort();
//	final IRuntimeOutputPort opCH7 = new DefaultRuntimeOutputPort();
//	final IRuntimeOutputPort opCH8 = new DefaultRuntimeOutputPort();
//	final IRuntimeOutputPort opCH9 = new DefaultRuntimeOutputPort();
//	final IRuntimeOutputPort opCH10 = new DefaultRuntimeOutputPort();
//	final IRuntimeOutputPort opCH11 = new DefaultRuntimeOutputPort();
//	final IRuntimeOutputPort opCH12 = new DefaultRuntimeOutputPort();
	
	public final IRuntimeOutputPort [] opCH = new DefaultRuntimeOutputPort[NUMBER_OF_OUTPORTS];
	
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	int propPort = 57000;
	String propAddressCH1 = "/Channel1";
	String propAddressCH2 = "/Channel2";
	String propAddressCH3 = "/Channel3";
	String propAddressCH4 = "/Channel4";
	String propAddressCH5 = "/Channel5";
	String propAddressCH6 = "/Channel6";
	String propAddressCH7 = "/Channel7";
	String propAddressCH8 = "/Channel8";
	String propAddressCH9 = "/Channel9";
	String propAddressCH10 = "/Channel10";
	String propAddressCH11 = "/Channel11";
	String propAddressCH12 = "/Channel12";
	int propArgNrCH1 = 1;
	int propArgNrCH2 = 2;
	int propArgNrCH3 = 3;
	int propArgNrCH4 = 4;
	int propArgNrCH5 = 5;
	int propArgNrCH6 = 6;
	int propArgNrCH7 = 7;
	int propArgNrCH8 = 8;
	int propArgNrCH9 = 9;
	int propArgNrCH10 = 10;
	int propArgNrCH11 = 11;
	int propArgNrCH12 = 12;
	

	// declare member variables here

	static OscServerInstance instance;
    
	Object sync = new Object ();
	OSCServer c;
	
	
   /**
    * The class constructor.
    */
    public OscServerInstance()
    {
        // empty constructor
    	instance = this;
    	
    	for (int i = 0; i < NUMBER_OF_OUTPORTS; i++)
		{
			opCH[i]=new DefaultRuntimeOutputPort();
		}
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {

		return null;
	}

    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID)
	{
    	
    	String s;
		for (int i = 0; i < NUMBER_OF_OUTPORTS; i++)
		{
			s = OP_CH_PREFIX + i;
			if (s.equalsIgnoreCase(portID))
			{
				return opCH[i];
			}
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

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {

        return null;
    }
		
    /**
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
		if ("port".equalsIgnoreCase(propertyName))
		{
			return propPort;
		}
		if ("addressCH1".equalsIgnoreCase(propertyName))
		{
			return propAddressCH1;
		}
		if ("addressCH2".equalsIgnoreCase(propertyName))
		{
			return propAddressCH2;
		}
		if ("addressCH3".equalsIgnoreCase(propertyName))
		{
			return propAddressCH3;
		}
		if ("addressCH4".equalsIgnoreCase(propertyName))
		{
			return propAddressCH4;
		}
		if ("addressCH5".equalsIgnoreCase(propertyName))
		{
			return propAddressCH5;
		}
		if ("addressCH6".equalsIgnoreCase(propertyName))
		{
			return propAddressCH6;
		}
		if ("addressCH7".equalsIgnoreCase(propertyName))
		{
			return propAddressCH7;
		}
		if ("addressCH8".equalsIgnoreCase(propertyName))
		{
			return propAddressCH8;
		}
		if ("addressCH9".equalsIgnoreCase(propertyName))
		{
			return propAddressCH9;
		}
		if ("addressCH10".equalsIgnoreCase(propertyName))
		{
			return propAddressCH10;
		}
		if ("addressCH11".equalsIgnoreCase(propertyName))
		{
			return propAddressCH11;
		}
		if ("addressCH12".equalsIgnoreCase(propertyName))
		{
			return propAddressCH12;
		}
		if ("argNrCH1".equalsIgnoreCase(propertyName))
		{
			return propArgNrCH1;
		}
		if ("argNrCH2".equalsIgnoreCase(propertyName))
		{
			return propArgNrCH2;
		}
		if ("argNrCH3".equalsIgnoreCase(propertyName))
		{
			return propArgNrCH3;
		}
		if ("argNrCH4".equalsIgnoreCase(propertyName))
		{
			return propArgNrCH4;
		}
		if ("argNrCH5".equalsIgnoreCase(propertyName))
		{
			return propArgNrCH5;
		}
		if ("argNrCH6".equalsIgnoreCase(propertyName))
		{
			return propArgNrCH6;
		}
		if ("argNrCH7".equalsIgnoreCase(propertyName))
		{
			return propArgNrCH7;
		}
		if ("argNrCH8".equalsIgnoreCase(propertyName))
		{
			return propArgNrCH8;
		}
		if ("argNrCH9".equalsIgnoreCase(propertyName))
		{
			return propArgNrCH9;
		}
		if ("argNrCH10".equalsIgnoreCase(propertyName))
		{
			return propArgNrCH10;
		}
		if ("argNrCH11".equalsIgnoreCase(propertyName))
		{
			return propArgNrCH11;
		}
		if ("argNrCH12".equalsIgnoreCase(propertyName))
		{
			return propArgNrCH12;
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
		if ("port".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propPort;
			propPort = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("addressCH1".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAddressCH1;
			propAddressCH1 = (String)newValue;
			return oldValue;
		}
		if ("addressCH2".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAddressCH2;
			propAddressCH2 = (String)newValue;
			return oldValue;
		}
		if ("addressCH3".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAddressCH3;
			propAddressCH3 = (String)newValue;
			return oldValue;
		}
		if ("addressCH4".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAddressCH4;
			propAddressCH4 = (String)newValue;
			return oldValue;
		}
		if ("addressCH5".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAddressCH5;
			propAddressCH5 = (String)newValue;
			return oldValue;
		}
		if ("addressCH6".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAddressCH6;
			propAddressCH6 = (String)newValue;
			return oldValue;
		}
		if ("addressCH7".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAddressCH7;
			propAddressCH7 = (String)newValue;
			return oldValue;
		}
		if ("addressCH8".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAddressCH8;
			propAddressCH8 = (String)newValue;
			return oldValue;
		}
		if ("addressCH9".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAddressCH9;
			propAddressCH9 = (String)newValue;
			return oldValue;
		}
		if ("addressCH10".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAddressCH10;
			propAddressCH10 = (String)newValue;
			return oldValue;
		}
		if ("addressCH11".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAddressCH11;
			propAddressCH11 = (String)newValue;
			return oldValue;
		}
		if ("addressCH12".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAddressCH12;
			propAddressCH12 = (String)newValue;
			return oldValue;
		}
		if ("argNrCH1".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propArgNrCH1;
			propArgNrCH1 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("argNrCH2".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propArgNrCH2;
			propArgNrCH2 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("argNrCH3".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propArgNrCH3;
			propArgNrCH3 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("argNrCH4".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propArgNrCH4;
			propArgNrCH4 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("argNrCH5".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propArgNrCH5;
			propArgNrCH5 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("argNrCH6".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propArgNrCH6;
			propArgNrCH6 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("argNrCH7".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propArgNrCH7;
			propArgNrCH7 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("argNrCH8".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propArgNrCH8;
			propArgNrCH8 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("argNrCH9".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propArgNrCH9;
			propArgNrCH9 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("argNrCH10".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propArgNrCH10;
			propArgNrCH10 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("argNrCH11".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propArgNrCH11;
			propArgNrCH11 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("argNrCH12".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propArgNrCH12;
			propArgNrCH12 = Integer.parseInt(newValue.toString());
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */


     /**
      * Event Listerner Ports.
      */

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  server( OSCChannel.UDP );
          super.start();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
    	  try
    	  {
    		  OscServerInstance.instance.c.stop();
    	  }
    	  catch(IOException e1)
    	  {}
          super.pause();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
    	  try
    	  {
    		  OscServerInstance.instance.c.start();
    	  }
    	  catch(IOException e1)
    	  {}
          super.resume();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
    	  OscServerInstance.instance.c.dispose();
          super.stop();
      }
      
      

      /**
       *	This opens a server listening at port see Parameter. Recognized
       *	messages are <code>/pause</code>, <code>/quit</code>, <code>/dumpOSC</code>.
       *	See <code>NetUtil_Tests.rtf</code> for a way to check the server.
       *
       *	@param	protocol	<code>UDP</code> or <code>TCP</code>
       */
      public static void server( String protocol )
      {
    	  
//      	postln( "NetUtilTest.server( \"" + protocol + "\" )\n" );
//      	postln( "listening at port 57110. recognized commands: /pause, /quit, /dumpOSC" );
      	
      	try {
      		AstericsErrorHandling.instance.reportInfo(OscServerInstance.instance, "start server");
      		OscServerInstance.instance.c = OSCServer.newUsing( protocol, OscServerInstance.instance.propPort );
      		//AstericsErrorHandling.instance.reportInfo(OscServerInstance.instance, String.format( "server addr "+localAddress ));
      		AstericsErrorHandling.instance.reportInfo(OscServerInstance.instance, String.valueOf(InetAddress.getLocalHost().getHostAddress()));
      		//AstericsErrorHandling.instance.reportInfo(OscServerInstance.instance, String.valueOf(localAddress);
      	}
      	catch( IOException e1 ) 
      	{
      		e1.printStackTrace();
      		return; 
      	}
      	//AstericsErrorHandling.instance.reportInfo(OscServerInstance.instance, "add Listener");      	
      	OscServerInstance.instance.c.addOSCListener( new OSCListener() 
      	{
      		public void messageReceived( OSCMessage m, SocketAddress addr, long time )
      		{
      			
      			//int i=0;
      			//int occupiedports = OscServerInstance.instance.propArgOfOscStream2  + OscServerInstance.instance.propArgOfOscStream1+ OscServerInstance.instance.propArgOfOscStream3;
      			
      			//AstericsErrorHandling.instance.reportInfo(OscServerInstance.instance, String.format("get from" +addr));
      			
      			if(m.getName().contains("foo"))
      				{
      				//AstericsErrorHandling.instance.reportInfo(OscServerInstance.instance, "get_foo");
      					
      			} if(m.getName().equals(OscServerInstance.instance.propAddressCH1)){
      				//AstericsErrorHandling.instance.reportInfo(OscServerInstance.instance, "Stream1");

      				OscServerInstance.instance.opCH[1].sendData( ConversionUtils.doubleToBytes((float) m.getArg(OscServerInstance.instance.propArgNrCH1)));
      				
      				
      				
      			}if(m.getName().equals(OscServerInstance.instance.propAddressCH2)){
      				//AstericsErrorHandling.instance.reportInfo(OscServerInstance.instance, "Stream2");

      				
      					OscServerInstance.instance.opCH[2].sendData( ConversionUtils.doubleToBytes((float) m.getArg(OscServerInstance.instance.propArgNrCH2)));
      					
      			}if(m.getName().equals(OscServerInstance.instance.propAddressCH3)){
      				//AstericsErrorHandling.instance.reportInfo(OscServerInstance.instance, "Stream3");

      				
      					OscServerInstance.instance.opCH[3].sendData( ConversionUtils.doubleToBytes((float) m.getArg(OscServerInstance.instance.propArgNrCH3)));
      				
      			}if(m.getName().equals(OscServerInstance.instance.propAddressCH4)){
      				//AstericsErrorHandling.instance.reportInfo(OscServerInstance.instance, "Stream4");
      				
      					OscServerInstance.instance.opCH[4].sendData( ConversionUtils.doubleToBytes((float) m.getArg(OscServerInstance.instance.propArgNrCH4)));
      			
      				
  					
      			}if(m.getName().equals(OscServerInstance.instance.propAddressCH5)){
      				//AstericsErrorHandling.instance.reportInfo(OscServerInstance.instance, "Stream5");
      				
      					OscServerInstance.instance.opCH[5].sendData( ConversionUtils.doubleToBytes((float) m.getArg(OscServerInstance.instance.propArgNrCH5)));
      					  		      				      				
      				//OscServerInstance.instance.opCH[OscServerInstance.instance.propSetOutCH5].sendData( ConversionUtils.doubleToBytes((float) m.getArg(OscServerInstance.instance.propArgNrCH5)));
  					
      			}if(m.getName().equals(OscServerInstance.instance.propAddressCH6)){
      				
      					OscServerInstance.instance.opCH[6].sendData( ConversionUtils.doubleToBytes((float) m.getArg(OscServerInstance.instance.propArgNrCH6)));
      					
      			}if(m.getName().equals(OscServerInstance.instance.propAddressCH7)){
      				//AstericsErrorHandling.instance.reportInfo(OscServerInstance.instance, "Stream7");
      				
      					OscServerInstance.instance.opCH[7].sendData( ConversionUtils.doubleToBytes((float) m.getArg(OscServerInstance.instance.propArgNrCH7)));
      					
      			}if(m.getName().equals(OscServerInstance.instance.propAddressCH8)){
      				//AstericsErrorHandling.instance.reportInfo(OscServerInstance.instance, "Stream2");
      				
      					OscServerInstance.instance.opCH[8].sendData( ConversionUtils.doubleToBytes((float) m.getArg(OscServerInstance.instance.propArgNrCH8)));
      					
      			}if(m.getName().equals(OscServerInstance.instance.propAddressCH9)){
      				//AstericsErrorHandling.instance.reportInfo(OscServerInstance.instance, "Stream9");
      				
      					OscServerInstance.instance.opCH[9].sendData( ConversionUtils.stringToBytes( (String) m.getArg(OscServerInstance.instance.propArgNrCH9)));
      				
      			}if(m.getName().equals(OscServerInstance.instance.propAddressCH10)){
      				//AstericsErrorHandling.instance.reportInfo(OscServerInstance.instance, "Stream2");
      				
      					OscServerInstance.instance.opCH[10].sendData( ConversionUtils.stringToBytes( (String) m.getArg(OscServerInstance.instance.propArgNrCH10)));
      				
      			}if(m.getName().equals(OscServerInstance.instance.propAddressCH11)){
      				//AstericsErrorHandling.instance.reportInfo(OscServerInstance.instance, "Stream2");
      				
      					OscServerInstance.instance.opCH[11].sendData( ConversionUtils.stringToBytes( (String) m.getArg(OscServerInstance.instance.propArgNrCH11)));
      					
      			}if(m.getName().equals(OscServerInstance.instance.propAddressCH12)){
      				
      					OscServerInstance.instance.opCH[12].sendData( ConversionUtils.stringToBytes( (String) m.getArg(OscServerInstance.instance.propArgNrCH12)));
   			}
      			
      		}	
      			
      	});
      	try 
      	{
      		OscServerInstance.instance.c.start();
      	}
      	catch( IOException e3 ) 
      	{}
  	  AstericsErrorHandling.instance.reportInfo(OscServerInstance.instance, "foo");
      	
      }
      
      
      
}