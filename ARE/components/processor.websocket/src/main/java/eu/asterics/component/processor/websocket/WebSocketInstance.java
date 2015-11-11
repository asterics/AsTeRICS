

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

package eu.asterics.component.processor.websocket;


import eu.asterics.mw.are.DeploymentManager;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.webservice.WebServiceEngine;

/**
 * This plugin connects to the WebServiceEngine and sends data to it or receives data from it.
 *    
 * @author Martin Deinhofer [martin.deinhofer@technikum-wien.at]
 *         Date: 20140618
 *         
 */
public class WebSocketInstance extends AbstractRuntimeComponentInstance
{
	private static final String OP_OUT_A = "outA";
	private static final String IP_IN_A = "inA";
	private static final String IP_FROM_WEB_SOCKET="ipFromWebSocket";
	final IRuntimeOutputPort opOutA = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpReconnect = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	// declare member variables here
  
    
   /**
    * The class constructor.
    */
    public WebSocketInstance()
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
		if (IP_IN_A.equalsIgnoreCase(portID))
		{
			return ipInA;
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
		if (OP_OUT_A.equalsIgnoreCase(portID))
		{
			return opOutA;
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
		if ("event1".equalsIgnoreCase(eventPortID))
		{
			return elpEvent1;
		}
		if ("reset".equalsIgnoreCase(eventPortID))
		{
			return elpReset;
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
		if ("reconnect".equalsIgnoreCase(eventPortID))
		{
			return etpReconnect;
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

        return null;
    }

    /**
     * sets a new value for the given property.
     * @param propertyName   the name of the property
     * @param newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipInA  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			//The WebsocketEngine provides the AstericsDataApplication instance which emulates an IRuntimeInputPort
			WebServiceEngine.getInstance().getAstericsApplication().getInputPort("").receiveData(data);
		}
	};

	/**
	 * Internal input port that receives the data from the websocket which emulates an IRuntimeOutputPort.
	 */
	private final IRuntimeInputPort ipFromWebSocket  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			//forward the data to the output port.
			opOutA.sendData(data);
		}
	};

	
     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpEvent1 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpReset = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 // insert event handling here 
		}
	};

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
          super.start();
          register();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
          super.pause();
          deRegister();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
          super.resume();
          register();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
          super.stop();
          deRegister();
      }
      
      /**
       * Registers this instance as an input port receiving data from the WebSocket
       */
      private void register() {
    	  String componentInstanceId=DeploymentManager.instance.getComponentInstanceIDFromComponentInstance(this);
    	  WebServiceEngine.getInstance().getAstericsApplication().getOutputPort("").addInputPortEndpoint(componentInstanceId, IP_FROM_WEB_SOCKET, ipFromWebSocket, "");
      }
      
      /**
       * Deregisters this instance from receiving websocket data.
       */
      private void deRegister() {
    	  String componentInstanceId=DeploymentManager.instance.getComponentInstanceIDFromComponentInstance(this);
    	  WebServiceEngine.getInstance().getAstericsApplication().getOutputPort("").removeInputPortEndpoint(componentInstanceId, IP_FROM_WEB_SOCKET);
      }
}