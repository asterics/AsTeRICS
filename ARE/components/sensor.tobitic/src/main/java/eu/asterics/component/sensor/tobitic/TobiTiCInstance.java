

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

package eu.asterics.component.sensor.tobitic;


import java.io.*;
import java.util.logging.Logger;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;


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
import eu.asterics.mw.services.AstericsThreadPool;
import eu.asterics.mw.services.IRemoteConnectionListener;
import eu.asterics.mw.services.RemoteConnectionManager;

/**
 * 
 * This module implements a simple intrface to the TOBI TiC
 * see http://files.mtvl.org/projects/tobicore/doc/libtobiic.pdf
 * http://www.bcistandards.org/softwarestandards/tic
 * 
 * up to five class values (names given as properties) 
 * are presented at the plugin's output ports
 *  
 * @author chris veigl [veigl@technikum-wien.at]
 *         Date: Oct. 30, 2012
 *         Time: 
 */
public class TobiTiCInstance extends AbstractRuntimeComponentInstance
{
	public final int NUMBER_OF_LABELS = 5;

	public final IRuntimeOutputPort [] opValue = new DefaultRuntimeOutputPort[NUMBER_OF_LABELS];
	public String[] propClassLabel= new String[NUMBER_OF_LABELS];
	int propTcpPort = 52000;

	// declare member variables here

	private boolean connectionEstablished = false;
	private IRemoteConnectionListener connectionListener = null;

	private TobiTiCInstance instance = this;

    // SAX-EventHandler erstellen
    DefaultHandler handler = new SAXLesen();

    // Inhalt mit dem Default-Parser parsen
    SAXParser saxParser;

    
   /**
    * The class constructor.
    */
    public TobiTiCInstance()
    {
		for (int i = 0; i < NUMBER_OF_LABELS; i++)
		{
			propClassLabel[i]="label"+i;
			opValue[i]=new DefaultRuntimeOutputPort();

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
		for (int i = 0; i < NUMBER_OF_LABELS; i++)
		{
			s = "value" + (i+1);
			if (s.equalsIgnoreCase(portID))
			{
				return opValue[i];
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
		if ("tcpPort".equalsIgnoreCase(propertyName))
		{
			return propTcpPort;
		}
    	String s;
		for (int i = 0; i < NUMBER_OF_LABELS; i++)
		{
			s = "classLabel" + (i+1);
			if (s.equalsIgnoreCase(propertyName))
			{
				return propClassLabel[i];
			}
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
		if ("tcpPort".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propTcpPort;
			propTcpPort = Integer.parseInt(newValue.toString());
			return oldValue;
		}
    	String s;
		for (int i = 0; i < NUMBER_OF_LABELS; i++)
		{
			s = "classLabel" + (i+1);
			if (s.equalsIgnoreCase(propertyName))
			{
				final Object oldValue = propClassLabel[i];
				propClassLabel[i] = (String)newValue;
				return oldValue;
			}
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

          super.start();
    	  if (!openConnection(propTcpPort))
    	  {
    		  AstericsErrorHandling.instance.reportInfo(this, "TiC connection not available ");
    	  } 
    	  else
    	  { 
  			   AstericsErrorHandling.instance.reportInfo(this,String.format("ReUsing old Connection to TiC"));

    	  }

          
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
			AstericsErrorHandling.instance.reportInfo(this,
					String.format("Closing Connection to TiC"));

	        RemoteConnectionManager.instance.closeConnection(Integer.toString(propTcpPort));
	        connectionEstablished = false;
          super.stop();
      }
      
      
  	/**
    	 * Opens the connection to the Tobi TiC application
    	 * @param tcpPort the TCP port to connect to
    	 * @return true if there is already a connection available on this TCP port,
    	 * false otherwise which will result in the connection being opened and
    	 * handled through the connection listener
    	 */
    	private boolean openConnection(int tcpPort)
    	{
        	connectionListener = new TobiTicConnectionListener();
        	if (RemoteConnectionManager.instance.requestConnection(
        			Integer.toString(tcpPort), connectionListener))
        	{
        		// connection already existed 
        		connectionEstablished = true;
        		connectionListener.connectionEstablished();
        		return true;
        	}
        	else
        	{
        		// no existing connection, user should wait for connection 
        		// established message from listener
        		return false;
        	}
    	}    
    	
  	/**
  	 * Closes the connection
  	 */
  	void closeConnection()
  	{
  		if (connectionEstablished)
  		{
  			AstericsErrorHandling.instance.reportInfo(instance,
  					String.format("Closing Connection to TiC Interface"));

  	        RemoteConnectionManager.instance
          	.closeConnection(Integer.toString(propTcpPort));
  	        connectionEstablished = false;
  		}
  	}  	
      
      
  	/**
  	 * Implementation of the remote connection listener interface for the 
  	 * communication with the Tobi Tic interface.
  	 * This class handles all incoming packets as well as the set up 
  	 * and tear down of the connection.
  	 * 
  	 */
  	class TobiTicConnectionListener implements IRemoteConnectionListener
  	{
  		/**
  		 * Constructs the listener
  		 * @param owner the SpeechProcessor component instance owning the listener
  		 */
  		public TobiTicConnectionListener()
  		{
  		}
  		
  		/**
  		 * Called once a connection is set up on a TCP port.
  		 */
  		public void connectionEstablished()
  		{
  			AstericsErrorHandling.instance.reportInfo(instance, "Connection established");
  			
  			connectionEstablished = true;
  			// boolean ret = RemoteConnectionManager.instance.writeData(Integer.toString(tcpPort), message.getBytes());
  			
  			input="";
  	        try {
  	            saxParser = SAXParserFactory.newInstance().newSAXParser();
//  	            saxParser.parse(new File("xml_file.xml"), handler);
  	        } catch (ParserConfigurationException pe) {
  	            pe.printStackTrace();
  	        } catch (SAXException se) {
  	            se.printStackTrace();
  	        }

  			
  		}
  		
		String input="";
  		
  		/**
  		 * Called when the SpeechProcessor sends data. Handles all incoming packets
  		 */
  		public void dataReceived(byte [] data)
  		{
  			
  			String act_input = new String(data); //.trim();

/*			System.out.flush();
            System.out.println("\ngot input:"+act_input);
			System.out.flush();
*/

  			input+=act_input;

  			int delimiterPos = input.indexOf("</tobiic>");
  			
  			if (delimiterPos>-1)   // one complete message available
  			{
  	  			String oneMessage = input.substring (0,delimiterPos+9); 
  				input = input.substring (delimiterPos+9);

  	            System.out.println("\ngot message:\n-------------\n"+oneMessage);
  	            System.out.println("\nrest:\n--------------\n"+input);
  				
	  	        try {
	  	        	/*
	  	        	input =	 
	  	        	"<tobiic version=\"0.1.1.0\" frame=\"1\">\n"+
	            		" <classifier name=\"smr\" description=\"SMR classifier\" vtype=\"prob\" ltype=\"biosig\">\n"+
	            			"  <class label=\"0x769\">0.743132</class>\n"+
	            			"  <class label=\"0x770\">0.256868</class>\n"+
	            	" </classifier>\n"+
	            	" </tobiic>";
	  	        	 */
	            	// System.out.println("Tic Interface data: \n"+input);
	  	            saxParser.parse(new InputSource( new StringReader( oneMessage )), handler);
	  	            	  	            
	  	        } catch (SAXException se) {
	  	            se.printStackTrace();
	  	        } catch (IOException ie) {
	  	            ie.printStackTrace();
	  	        }   
	  	        // input="";
  			}  			
  		}
  		
  		/**
  		 * Called when connection is lost
  		 */
  		public void connectionLost()
  		{
  			AstericsErrorHandling.instance.reportInfo(instance, 
  			"Connection lost");
  			connectionEstablished = false;
  		}
  		
  		/**
  		 * Called after connection has been closed
  		 */
  		public void connectionClosed()
  		{
  			AstericsErrorHandling.instance.reportInfo(instance, 
  			"Connection closed");
  			
  			connectionEstablished = false;
  		}
  		
  	}      

  	
  	public class SAXLesen extends DefaultHandler {
  	    private StringBuffer textBuffer = null;
		int sendToPort=0;

  	    public void startDocument() throws SAXException {
  	    //    System.out.println("SAX start document ");
  	    }

  	    public void endDocument() throws SAXException {   
  	    //    System.out.println("SAX end document ");
  	    }

  	    public void startElement(String namespaceURI, String localName,
  	            String qName, Attributes attrs) throws SAXException {
  	        flushBufferContent();
  	        String eName = ("".equals(localName)) ? qName : localName;
  	        System.out.println("Element: "+eName);

  	        sendToPort=0;
  	        if (attrs != null) {
  	            for (int i = 0; i < attrs.getLength(); i++) {
  	                String aName = attrs.getLocalName(i);
  	                if ("".equals(aName))
  	                    aName = attrs.getQName(i);
  	    	        System.out.println("Attribut: "+aName+ ", Value: "+ attrs.getValue(i));
  	    	        if (aName.equalsIgnoreCase("label"))
  	    	        {
	  	    	        for (int t=0; t< NUMBER_OF_LABELS; t++)
	  	    	        {
	  	    	        	if ((attrs.getValue(i)).equalsIgnoreCase(propClassLabel[t]))
	  	    	        	{
	  	    	        		sendToPort=t+1;
	  	    	        	}
	  	    	        }
  	    	        }
  	            }
  	        }
  	    }

  	    public void endElement(String namespaceURI, String localName, String qName)
  	            throws SAXException {
  	        flushBufferContent();
  	        String eName = ("".equals(localName)) ? qName : localName;
 	        System.out.println("End Element: "+eName);

  	    }

  	    // Erzeugt einen String aus den Char-Arrays und liest
  	    // diesen in einen StringBuffer ein
  	    public void characters(char[] buf, int offset, int len) throws SAXException {
  	        String s = new String(buf, offset, len);
  	        if (textBuffer == null)
  	            textBuffer = new StringBuffer(s);
  	        else
  	            textBuffer.append(s);
  	    }

  	    private void flushBufferContent() throws SAXException {
  	        if (textBuffer == null)
  	            return;
 	        System.out.println("In buffer: "+ textBuffer.toString());
 	        if (sendToPort>0)
 	        {
 	        	try 
 	        	{
 	        		double val = Double.parseDouble(textBuffer.toString());
 	    	        System.out.println("Sending "+val+" to Port "+sendToPort);
 	        		opValue[sendToPort-1].sendData(ConversionUtils.doubleToBytes(val));
 	        	}
 	        	catch (Exception e) {};
 	        }
  	        textBuffer = null;
  	    }
  	} 
  	
}