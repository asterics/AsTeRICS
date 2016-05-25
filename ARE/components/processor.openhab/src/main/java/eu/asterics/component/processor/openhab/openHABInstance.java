

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

package eu.asterics.component.processor.openhab;

 
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * 
 * interfaces to the openHAB home automation suite
 * The interface is handled via the REST API of openHAB
 * (see: https://github.com/openhab/openhab/wiki/REST-API)
 *  
 * @author Benjamin Aigner[aignerb@technikum-wien.at]
 *         Date: 27.07.2015
 *         Time: 00:07 AM
 */
public class openHABInstance extends AbstractRuntimeComponentInstance
{
	/**
	 * Using this hostname to connect to openHAB
	 * e.g.: http://localhost:8080
	 * 
	 * REST API: http://localhost:8080/rest
	 */
	static private String hostname;
	
	/** This port will be used to access openHAB (default: 8080 for non-HTTPS, 8443 for HTTPS) */
	String port = "8443";
	
	/** If a authentication is configured, use this username for HTTP authentication
	 * WARNING: if the username is given, this component WILL authenticate, even if no username is necessary! */
	String username = "";
	
	/** Password, corresponding to username */
	String password = "";
	
	/** Use lazy certificate check for HTTPS
	 * If this is set to true, the SSL certificate check will be bypassed */
	boolean lazyCertificate = true;
	
	/** protocol to be connected with, either http or https */
	String protocol = "https";
	
	/** update rate to fetch all necessary items [ms] */
	int updateRate = 1000;
	
	/** item name for fetching data, output port item1 */
	String item1out = "";
	/** item name for fetching data, output port item2 */
	String item2out = "";
	/** item name for fetching data, output port item3 */
	String item3out = "";
	/** item name for fetching data, output port item4 */
	String item4out = "";
	/** item name for fetching data, output port item5 */
	String item5out = "";
	/** item name for fetching data, output port item6 */
	String item6out = "";
	
	/** item name for setting a state, input port item1 */
	String item1in = "";
	/** item name for setting a state, input port item2 */
	String item2in = "";
	/** item name for setting a state, input port item3 */
	String item3in = "";
	/** item name for setting a state, input port item4 */
	String item4in = "";
	/** item name for setting a state, input port item5 */
	String item5in = "";
	/** item name for setting a state, input port item6 */
	String item6in = "";
	
	/** item name for event triggering, event output port item1 */
	String item1event = "";
	/** item name for event triggering, event output port item2 */
	String item2event = "";
	/** item name for event triggering, event output port item3 */
	String item3event = "";
	/** item name for event triggering, event output port item4 */
	String item4event = "";
	/** item name for event triggering, event output port item5 */
	String item5event = "";
	/** item name for event triggering, event output port item6 */
	String item6event = "";
	
	

	/** item state for event triggering, event output port item1 */
	String item1state = null;
	/** item state for event triggering, event output port item2 */
	String item2state = null;
	/** item state for event triggering, event output port item3 */
	String item3state = null;
	/** item state for event triggering, event output port item4 */
	String item4state = null;
	/** item state for event triggering, event output port item5 */
	String item5state = null;
	/** item state for event triggering, event output port item6 */
	String item6state = null;
	
	/** all available items of the selected openHAB instance */
	List<String> items;
	
	
	//output ports
	public final IRuntimeOutputPort opItem1 =  new DefaultRuntimeOutputPort();
	public final IRuntimeOutputPort opItem2 =  new DefaultRuntimeOutputPort();
	public final IRuntimeOutputPort opItem3 =  new DefaultRuntimeOutputPort();
	public final IRuntimeOutputPort opItem4 =  new DefaultRuntimeOutputPort();
	public final IRuntimeOutputPort opItem5 =  new DefaultRuntimeOutputPort();
	public final IRuntimeOutputPort opItem6 =  new DefaultRuntimeOutputPort();
	
	//event trigger ports
	public final IRuntimeEventTriggererPort etpItem1 = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpItem2 = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpItem3 = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpItem4 = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpItem5 = new DefaultRuntimeEventTriggererPort();
	public final IRuntimeEventTriggererPort etpItem6 = new DefaultRuntimeEventTriggererPort();
	
	//tick generator for fetching item state
	private final TickGenerator tg = new TickGenerator(this);
	
   /**
    * The class constructor.
    */
    public openHABInstance()
    {
    }

    /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
		if ("item1in".equalsIgnoreCase(portID))
		{
			return ipItem1;
		}
		if ("item2in".equalsIgnoreCase(portID))
		{
			return ipItem2;
		}
		if ("item3in".equalsIgnoreCase(portID))
		{
			return ipItem3;
		}
		if ("item4in".equalsIgnoreCase(portID))
		{
			return ipItem4;
		}
		if ("item5in".equalsIgnoreCase(portID))
		{
			return ipItem5;
		}
		if ("item6in".equalsIgnoreCase(portID))
		{
			return ipItem6;
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
    	if ("item1".equalsIgnoreCase(portID))
		{
			return opItem1;
		}
		if ("item2".equalsIgnoreCase(portID))
		{
			return opItem2;
		}
		if ("item3".equalsIgnoreCase(portID))
		{
			return opItem3;
		}
		if ("item4".equalsIgnoreCase(portID))
		{
			return opItem4;
		}
		if ("item5".equalsIgnoreCase(portID))
		{
			return opItem5;
		}
		if ("item6".equalsIgnoreCase(portID))
		{
			return opItem6;
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
    	if("item1changed".equalsIgnoreCase(eventPortID)) {
    		return etpItem1;
    	}
    	if("item2changed".equalsIgnoreCase(eventPortID)) {
    		return etpItem2;
    	}

    	if("item3changed".equalsIgnoreCase(eventPortID)) {
    		return etpItem3;
    	}

    	if("item4changed".equalsIgnoreCase(eventPortID)) {
    		return etpItem4;
    	}

    	if("item5changed".equalsIgnoreCase(eventPortID)) {
    		return etpItem5;
    	}

    	if("item6changed".equalsIgnoreCase(eventPortID)) {
    		return etpItem6;
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
    	// general properties
		if ("updaterate".equalsIgnoreCase(propertyName))
		{
			return updateRate;
		}
		if ("hostname".equalsIgnoreCase(propertyName))
		{
			return hostname;
		}
		if ("port".equalsIgnoreCase(propertyName))
		{
			return port;
		}
		if ("protocol".equalsIgnoreCase(propertyName))
		{
			return protocol;
		}
		if ("lazyCertificates".equalsIgnoreCase(propertyName))
		{
			return lazyCertificate;
		}
		if ("username".equalsIgnoreCase(propertyName))
		{
			return username;
		}
		if ("password".equalsIgnoreCase(propertyName))
		{
			return password;
		}
		//Properties related to input ports
		if ("item1in".equalsIgnoreCase(propertyName))
		{
			return item1in;
		}
		if ("item2in".equalsIgnoreCase(propertyName))
		{
			return item2in;
		}
		if ("item3in".equalsIgnoreCase(propertyName))
		{
			return item3in;
		}
		if ("item4in".equalsIgnoreCase(propertyName))
		{
			return item4in;
		}
		if ("item5in".equalsIgnoreCase(propertyName))
		{
			return item5in;
		}
		if ("item6in".equalsIgnoreCase(propertyName))
		{
			return item6in;
		}
		//Properties related to output ports
		if ("item1out".equalsIgnoreCase(propertyName))
		{
			return item1out;
		}
		if ("item2out".equalsIgnoreCase(propertyName))
		{
			return item2out;
		}
		if ("item3out".equalsIgnoreCase(propertyName))
		{
			return item3out;
		}
		if ("item4out".equalsIgnoreCase(propertyName))
		{
			return item4out;
		}
		if ("item5out".equalsIgnoreCase(propertyName))
		{
			return item5out;
		}
		if ("item6out".equalsIgnoreCase(propertyName))
		{
			return item6out;
		}
		//Properties related to event ports
		if ("item1event".equalsIgnoreCase(propertyName))
		{
			return item1event;
		}
		if ("item2event".equalsIgnoreCase(propertyName))
		{
			return item2event;
		}
		if ("item3event".equalsIgnoreCase(propertyName))
		{
			return item3event;
		}
		if ("item4event".equalsIgnoreCase(propertyName))
		{
			return item4event;
		}
		if ("item5event".equalsIgnoreCase(propertyName))
		{
			return item5event;
		}
		if ("item6event".equalsIgnoreCase(propertyName))
		{
			return item6event;
		}
        return null;
    }

    /**
     * sets a new value for the given property.
     * @param[in] propertyName   the name of the property
     * @param[in] newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
    	AstericsErrorHandling.instance.reportDebugInfo(null, "SetRuntimeProperty: " + propertyName + " " + newValue);
    	//general properties
		if ("updaterate".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = updateRate;
			updateRate = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("hostname".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = hostname;
			hostname = newValue.toString();
			return oldValue;
		}
		if ("port".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = port;
			port = newValue.toString();
			return oldValue;
		}
		if ("username".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = username;
			username = newValue.toString();
			return oldValue;
		}
		if ("password".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = password;
			password = newValue.toString();
			return oldValue;
		}
		if ("protocol".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = protocol;
			protocol = newValue.toString();
			return oldValue;
		}
		if ("lazyCertificates".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = lazyCertificate;
			if("true".equalsIgnoreCase((String) newValue))
			{
				lazyCertificate = true;
			} else {
				lazyCertificate = false;
			}
			return oldValue;
		}
		//input port properties
		if ("item1in".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = item1in;
			item1in = newValue.toString();
			return oldValue;
		}
		if ("item2in".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = item2in;
			item2in = newValue.toString();
			return oldValue;
		}
		if ("item3in".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = item3in;
			item3in = newValue.toString();
			return oldValue;
		}
		if ("item4in".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = item4in;
			item4in = newValue.toString();
			return oldValue;
		}
		if ("item5in".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = item5in;
			item5in = newValue.toString();
			return oldValue;
		}
		if ("item6in".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = item6in;
			item6in = newValue.toString();
			return oldValue;
		}
		//output port properties
		if ("item1out".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = item1out;
			item1out = newValue.toString();
			return oldValue;
		}
		if ("item2out".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = item2out;
			item2out = newValue.toString();
			return oldValue;
		}
		if ("item3out".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = item3out;
			item3out = newValue.toString();
			return oldValue;
		}
		if ("item4out".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = item4out;
			item4out = newValue.toString();
			return oldValue;
		}
		if ("item5out".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = item5out;
			item5out = newValue.toString();
			return oldValue;
		}
		if ("item6out".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = item6out;
			item6out = newValue.toString();
			return oldValue;
		}
		//event port properties
		if ("item1event".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = item1event;
			item1event = newValue.toString();
			return oldValue;
		}
		if ("item2event".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = item2event;
			item2event = newValue.toString();
			return oldValue;
		}
		if ("item3event".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = item3event;
			item3event = newValue.toString();
			return oldValue;
		}
		if ("item4event".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = item4event;
			item4event = newValue.toString();
			return oldValue;
		}
		if ("item5event".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = item5event;
			item5event = newValue.toString();
			return oldValue;
		}
		if ("item6event".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = item6event;
			item6event = newValue.toString();
			return oldValue;
		}

        return null;
    }


	/**
     * Input Ports for receiving values.
   */
	private final IRuntimeInputPort ipItem1  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			String state = ConversionUtils.stringFromBytes(data);
			if(item1in != "") {
				setItemState(item1in,state);
				AstericsErrorHandling.instance.reportDebugInfo(null, "new state for item1("+ item1in + "):" + state);
			}
		}

	};
	/**
     * Input Ports for receiving values.
   */
	private final IRuntimeInputPort ipItem2  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			String state = ConversionUtils.stringFromBytes(data);
			if(item2in != "") {
				setItemState(item2in,state);
				AstericsErrorHandling.instance.reportDebugInfo(null, "new state for item2("+ item2in + "):" + state);
			}
		}

	};
	/**
     * Input Ports for receiving values.
   */
	private final IRuntimeInputPort ipItem3  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			String state = ConversionUtils.stringFromBytes(data);
			if(item3in != "") {
				setItemState(item3in,state);
				AstericsErrorHandling.instance.reportDebugInfo(null, "new state for item3("+ item3in + "):" + state);
			}
		}

	};
	/**
     * Input Ports for receiving values.
   */
	private final IRuntimeInputPort ipItem4  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			String state = ConversionUtils.stringFromBytes(data);
			if(item4in != "") {
				setItemState(item4in,state);
				AstericsErrorHandling.instance.reportDebugInfo(null, "new state for item4("+ item4in + "):" + state);
			}
		}

	};
	/**
     * Input Ports for receiving values.
   */
	private final IRuntimeInputPort ipItem5  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			String state = ConversionUtils.stringFromBytes(data);
			if(item5in != "") {
				setItemState(item5in,state);
				AstericsErrorHandling.instance.reportDebugInfo(null, "new state for item5("+ item5in + "):" + state);
			}
		}

	};

	/**
     * Input Ports for receiving values.
   */
	private final IRuntimeInputPort ipItem6  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			String state = ConversionUtils.stringFromBytes(data);
			if(item6in != "") {
				setItemState(item6in,state);
				AstericsErrorHandling.instance.reportDebugInfo(null, "new state for item6("+ item6in + "):" + state);
			}
		}

	};
	
	/**
     * Input Ports for receiving values, action string
   */
	private final IRuntimeInputPort ipActionString  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			String state = ConversionUtils.stringFromBytes(data);
			AstericsErrorHandling.instance.reportError(null, "ActionString not implemented yet...");
		}

	};
     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  AstericsErrorHandling.instance.reportDebugInfo(this, "Connecting to openHAB:");
    	  AstericsErrorHandling.instance.reportDebugInfo(this, "Host: " + hostname + ":" + port);
    	  AstericsErrorHandling.instance.reportDebugInfo(this, "Username: " + username);
    	  AstericsErrorHandling.instance.reportDebugInfo(this, "Password: " + password);
    	  AstericsErrorHandling.instance.reportDebugInfo(this, "Protocol: " + protocol);
    	  AstericsErrorHandling.instance.reportDebugInfo(this, "Using lazyCertificates: " + lazyCertificate);
    	  //get all available items
    	  this.items = getItems();

    	  if(items.isEmpty())
    	  {
    		  AstericsErrorHandling.instance.reportError(this, "Could not find openHAB on host "+ openHABInstance.hostname +" Please verify that openHAB is running and there is no firewall related issue");
    		  return;
    	  }
          super.start();
          tg.start();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
          super.pause();
          tg.stop();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
          super.resume();
          tg.start();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
          super.stop();
          tg.stop();
      }
      
      public List<String> getSitemaps() {
  		return getList(protocol+"://"+hostname+":"+port,"sitemap");
  	}
  	

  	public List<String> getItems() {
  		return getList(protocol+"://"+hostname+":"+port,"item");
  	}
  	
  	public String getItemState(String item) {
  		try {
  			AstericsErrorHandling.instance.reportDebugInfo(this, "Get item (name: " + item + ": " + protocol+"://"+hostname+":"+ port + "/rest/items/" + item + "/state");
  			return httpGet(protocol+"://"+hostname+":"+ port + "/rest/items/" + item + "/state");
  		} catch (KeyManagementException e) {
  			tg.stop();
  			AstericsErrorHandling.instance.reportError(this, "KeyManagement exception, try to use lazyCertificate option (property)");
  		} catch (NoSuchAlgorithmException e) {
  			tg.stop();
  			AstericsErrorHandling.instance.reportError(this, "Algortihm exception, please contact the AsTeRICS team");
  		} catch (IOException e) {
  			tg.stop();
  			//catch a wrong item name
  			if(e.getMessage().equalsIgnoreCase("Not Found"))
  			{
  				AstericsErrorHandling.instance.reportError(this, "Item name '" + item + "' not found, please update your model (HTTP 404)");
  			} else {
  				AstericsErrorHandling.instance.reportError(this, "Can't connect/transmit to openHAB instance, please check for a running openHAB and try to use it via the browser (username/password may be wrong),\n message: " + e.getMessage());
  			}
  		}
  		return "";
  	}
  	
  	public String setItemState(String item, String state) {
  		
  		//http://localhost:8080/CMD?Temperature_FF_Office=12.3
  		try {
  			AstericsErrorHandling.instance.reportDebugInfo(this, "Set item (name: " + item + ",state: " + state + "):" + protocol+"://"+hostname+":"+ port + "/CMD?" + item + "=" + state);
  			return httpGet(protocol+"://"+hostname+":"+ port + "/CMD?" + item + "=" + state);
  		} catch (KeyManagementException e) {
  			tg.stop();
  			AstericsErrorHandling.instance.reportError(this, "KeyManagement exception, try to use lazyCertificate option (property)");
  		} catch (NoSuchAlgorithmException e) {
  			tg.stop();
  			AstericsErrorHandling.instance.reportError(this, "Algortihm exception, please contact the AsTeRICS team");
  		} catch (IOException e) {
  			tg.stop();
  			if(e.getMessage().equalsIgnoreCase("Not Found"))
  			{
  				AstericsErrorHandling.instance.reportError(this, "Item name '" + item +"' not found, please update your model (HTTP 404)");
  			} else {
  				AstericsErrorHandling.instance.reportError(this, "Can't connect/transmit to openHAB instance, please check for a running openHAB and try to use it via the browser (username/password may be wrong),\n message: " + e.getMessage());
  			}
  		}
  		return "";
  	}
  	
  	public List<String> getList(String hostname,String type) {
  		List<String> response = new ArrayList<String>();
  		
  		try {
  			//create DOM object
  			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
  			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
  			//read the XML response via httpGet, REST api and determine type via parameter
  			AstericsErrorHandling.instance.reportDebugInfo(this, "Get list (type: " + type + ": " + hostname + "/rest/" + type + "s");
  			InputSource is = new InputSource( new StringReader( httpGet(hostname + "/rest/" + type + "s") ) );
  			Document doc = dBuilder.parse(is);
  			
  			NodeList nodes = doc.getElementsByTagName(type);
  				
  			//parse all nodes, and extract item/sitemap name
  			for(int i = 0; i< nodes.getLength(); i++)
  			{
  				Node item = nodes.item(i);
  				Element element = (Element) item;
  				String name = element.getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue();
  				//add to list
  				response.add(name);
  			}
  		} catch (SAXException e) {
  			tg.stop();
  			AstericsErrorHandling.instance.reportError(this, "SAX exception, unable to parse the openHAB data, maybe a transmission error?");
  		} catch (IOException e) {
  			tg.stop();
  			AstericsErrorHandling.instance.reportError(this, "Can't connect/transmit to openHAB instance, please check for a running openHAB and try to use it via the browser (username/password may be wrong),\n message: " + e.getMessage());
  		} catch (ParserConfigurationException e) {
  			tg.stop();
  			AstericsErrorHandling.instance.reportError(this, "Parser exception, unable to parse the openHAB data, maybe a transmission error?");
  		} catch (KeyManagementException e) {
  			tg.stop();
  			AstericsErrorHandling.instance.reportError(this, "KeyManagement exception, try to use lazyCertificate option (property)");
  		} catch (NoSuchAlgorithmException e) {
  			tg.stop();
  			AstericsErrorHandling.instance.reportError(this, "Algortihm exception, please contact the AsTeRICS team");
  		}
  		
  		return response;
  	}
  	
  	public String httpGet(String urlStr) throws IOException, KeyManagementException, NoSuchAlgorithmException {
  		//if we wan't to ignore any certificate errors (not recommended!!!!), we need to do additional stuff here
  		//Based on http://www.rgagnon.com/javadetails/java-fix-certificate-problem-in-HTTPS.html
  		if(lazyCertificate == true) {
  			// Install the all-trusting host verifier
  		    HttpsURLConnection.setDefaultHostnameVerifier(hostnameValid);
  		    
  		    TrustManager[] trustAllCerts = new TrustManager[] {
  		    	       new X509TrustManager() {
  		    	          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
  		    	            return null;
  		    	          }

  		    	          public void checkClientTrusted(X509Certificate[] certs, String authType) {  }

  		    	          public void checkServerTrusted(X509Certificate[] certs, String authType) {  }

  		    	       }
  		    	    };
  		    
  		    SSLContext sc = SSLContext.getInstance("SSL");
  		    sc.init(null, trustAllCerts, new java.security.SecureRandom());
  		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
  		}
  		System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
  		  URL url = new URL(urlStr);
  		  HttpURLConnection conn =
  		      (HttpURLConnection) url.openConnection();
  		  
  		  //check for an username, if given, authenticate via HTTP BASIC
  		  if(this.username.length() != 0) {
  			  String userPassword = username + ":" + password;
  			  String passphraseEncoded = MyBase64.encode(userPassword.getBytes());
  			  
  			  conn.setRequestProperty("Authorization", "Basic " + passphraseEncoded);
  			  conn.connect();
  		  }
  		  
  		  if (conn.getResponseCode() != 200) {
  		    throw new IOException(conn.getResponseMessage());
  		  }

  		  // Buffer the result into a string
  		  BufferedReader rd = new BufferedReader(
  		      new InputStreamReader(conn.getInputStream()));
  		  StringBuilder sb = new StringBuilder();
  		  String line;
  		  while ((line = rd.readLine()) != null) {
  		    sb.append(line);
  		  }
  		  rd.close();

  		  conn.disconnect();
  		  return sb.toString();
  		}
  	
  	/**
  	 * From http://stackoverflow.com/questions/469695/decode-base64-data-in-java
  	 * There is no open base64 class in openJDK 7 (it will be in 8)
  	 * @author GeorgeK
  	 *
  	 */
  	public static class MyBase64 {

  	    private final static char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

  	    private static int[]  toInt   = new int[128];

  	    static {
  	        for(int i=0; i< ALPHABET.length; i++){
  	            toInt[ALPHABET[i]]= i;
  	        }
  	    }

  	    /**
  	     * Translates the specified byte array into Base64 string.
  	     *
  	     * @param buf the byte array (not null)
  	     * @return the translated Base64 string (not null)
  	     */
  	    public static String encode(byte[] buf){
  	        int size = buf.length;
  	        char[] ar = new char[((size + 2) / 3) * 4];
  	        int a = 0;
  	        int i=0;
  	        while(i < size){
  	            byte b0 = buf[i++];
  	            byte b1 = (i < size) ? buf[i++] : 0;
  	            byte b2 = (i < size) ? buf[i++] : 0;

  	            int mask = 0x3F;
  	            ar[a++] = ALPHABET[(b0 >> 2) & mask];
  	            ar[a++] = ALPHABET[((b0 << 4) | ((b1 & 0xFF) >> 4)) & mask];
  	            ar[a++] = ALPHABET[((b1 << 2) | ((b2 & 0xFF) >> 6)) & mask];
  	            ar[a++] = ALPHABET[b2 & mask];
  	        }
  	        switch(size % 3){
  	            case 1: ar[--a]  = '=';
  	            case 2: ar[--a]  = '=';
  	        }
  	        return new String(ar);
  	    }

  	    /**
  	     * Translates the specified Base64 string into a byte array.
  	     *
  	     * @param s the Base64 string (not null)
  	     * @return the byte array (not null)
  	     */
  	    public static byte[] decode(String s){
  	        int delta = s.endsWith( "==" ) ? 2 : s.endsWith( "=" ) ? 1 : 0;
  	        byte[] buffer = new byte[s.length()*3/4 - delta];
  	        int mask = 0xFF;
  	        int index = 0;
  	        for(int i=0; i< s.length(); i+=4){
  	            int c0 = toInt[s.charAt( i )];
  	            int c1 = toInt[s.charAt( i + 1)];
  	            buffer[index++]= (byte)(((c0 << 2) | (c1 >> 4)) & mask);
  	            if(index >= buffer.length){
  	                return buffer;
  	            }
  	            int c2 = toInt[s.charAt( i + 2)];
  	            buffer[index++]= (byte)(((c1 << 4) | (c2 >> 2)) & mask);
  	            if(index >= buffer.length){
  	                return buffer;
  	            }
  	            int c3 = toInt[s.charAt( i + 3 )];
  	            buffer[index++]= (byte)(((c2 << 6) | c3) & mask);
  	        }
  	        return buffer;
  	    } 

  	}
  	
  	//Create a verifier for our hostname
  	static HostnameVerifier hostnameValid = new HostnameVerifier() {
          public boolean verify(String hostnameToVerify, SSLSession session) {
          	if(hostnameToVerify.equals(hostname)) {
          		return true;
          	} else {
          		return false;
          	}
          }
  	};

  	/** callback method to fetch the item state in a regular period (defined by updateRate) */
	public void fetchState() {
		String s;
		AstericsErrorHandling.instance.reportDebugInfo(this, "Fetching data, updateRate: " + updateRate);
		//if set, all 6 output ports are fetched from openHAB
		if(item1out != "")
		{
			s= getItemState(item1out);
			opItem1.sendData(s.getBytes());
			AstericsErrorHandling.instance.reportDebugInfo(this, "Item1 state: " + s);
		}
		if(item2out != "")
		{
			s= getItemState(item2out);
			opItem2.sendData(s.getBytes());
			AstericsErrorHandling.instance.reportDebugInfo(this, "Item2 state: " + s);
		}
		if(item3out != "")
		{
			s= getItemState(item3out);
			opItem3.sendData(s.getBytes());
			AstericsErrorHandling.instance.reportDebugInfo(this, "Item3 state: " + s);
		}
		if(item4out != "")
		{
			s= getItemState(item4out);
			opItem4.sendData(s.getBytes());
			AstericsErrorHandling.instance.reportDebugInfo(this, "Item4 state: " + s);
		}
		if(item5out != "")
		{
			s= getItemState(item5out);
			opItem5.sendData(s.getBytes());
			AstericsErrorHandling.instance.reportDebugInfo(this, "Item5 state: " + s);
		}
		if(item6out != "")
		{
			s= getItemState(item6out);
			opItem6.sendData(s.getBytes());
			AstericsErrorHandling.instance.reportDebugInfo(this, "Item6 state: " + s);
		}
		
		//check for a changed item state, to raise events
		if(item1event != "")
		{
			s = getItemState(item1event);
			if(item1state == null)
			{
				AstericsErrorHandling.instance.reportDebugInfo(this, "Item1Event initial state: " + s);
				item1state = s;
			} else {
				if(!s.equals(item1state))
				{
					AstericsErrorHandling.instance.reportDebugInfo(this, "Item1Event raise event state: " + s);
					etpItem1.raiseEvent();
					item1state = s;
				}
			}
		}
		if(item2event != "")
		{
			s = getItemState(item2event);
			if(item2state == null)
			{
				AstericsErrorHandling.instance.reportDebugInfo(this, "Item2Event initial state: " + s);
				item2state = s;
			} else {
				if(!s.equals(item2state))
				{
					AstericsErrorHandling.instance.reportDebugInfo(this, "Item2Event raise event state: " + s);
					etpItem2.raiseEvent();
					item2state = s;
				}
			}
		}
		if(item3event != "")
		{
			s = getItemState(item3event);
			if(item3state == null)
			{
				AstericsErrorHandling.instance.reportDebugInfo(this, "Item3Event initial state: " + s);
				item3state = s;
			} else {
				if(!s.equals(item3state))
				{
					AstericsErrorHandling.instance.reportDebugInfo(this, "Item3Event raise event state: " + s);
					etpItem3.raiseEvent();
					item3state = s;
				}
			}
		}
		if(item4event != "")
		{
			s = getItemState(item4event);
			if(item4state == null)
			{
				AstericsErrorHandling.instance.reportDebugInfo(this, "Item4Event initial state: " + s);
				item4state = s;
			} else {
				if(!s.equals(item4state))
				{
					AstericsErrorHandling.instance.reportDebugInfo(this, "Item4Event raise event state: " + s);
					etpItem4.raiseEvent();
					item4state = s;
				}
			}
		}
		if(item5event != "")
		{
			s = getItemState(item5event);
			if(item5state == null)
			{
				AstericsErrorHandling.instance.reportDebugInfo(this, "Item5Event initial state: " + s);
				item5state = s;
			} else {
				if(!s.equals(item5state))
				{
					AstericsErrorHandling.instance.reportDebugInfo(this, "Item5Event raise event state: " + s);
					etpItem5.raiseEvent();
					item5state = s;
				}
			}
		}
		if(item6event != "")
		{
			s = getItemState(item6event);
			if(item6state == null)
			{
				AstericsErrorHandling.instance.reportDebugInfo(this, "Item6Event initial state: " + s);
				item6state = s;
			} else {
				if(!s.equals(item6state))
				{
					AstericsErrorHandling.instance.reportDebugInfo(this, "Item6Event raise event state: " + s);
					etpItem6.raiseEvent();
					item6state = s;
				}
			}
		}

	}
}