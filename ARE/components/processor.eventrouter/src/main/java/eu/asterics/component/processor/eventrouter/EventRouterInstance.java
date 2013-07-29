

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

package eu.asterics.component.processor.eventrouter;


import java.util.logging.Logger;
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
 * Routed incoming events to one of 8 event trigger ports
 * 
 * 
 *  
 * @author Chris Veigl
 *         Date: 2.7.2013
 */
public class EventRouterInstance extends AbstractRuntimeComponentInstance
{
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpEventOut1 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEventOut2 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEventOut3 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEventOut4 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEventOut5 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEventOut6 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEventOut7 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEventOut8 = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();


	// declare member variables here

	private int actPort=1;
    
   /**
    * The class constructor.
    */
    public EventRouterInstance()
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
		if ("eventIn".equalsIgnoreCase(eventPortID))
		{
			return elpEventIn;
		}
		if ("select1".equalsIgnoreCase(eventPortID))
		{
			return elpSelect1;
		}
		if ("select2".equalsIgnoreCase(eventPortID))
		{
			return elpSelect2;
		}
		if ("select3".equalsIgnoreCase(eventPortID))
		{
			return elpSelect3;
		}
		if ("select4".equalsIgnoreCase(eventPortID))
		{
			return elpSelect4;
		}
		if ("select5".equalsIgnoreCase(eventPortID))
		{
			return elpSelect5;
		}
		if ("select6".equalsIgnoreCase(eventPortID))
		{
			return elpSelect6;
		}
		if ("select7".equalsIgnoreCase(eventPortID))
		{
			return elpSelect7;
		}
		if ("select8".equalsIgnoreCase(eventPortID))
		{
			return elpSelect8;
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
		if ("eventOut1".equalsIgnoreCase(eventPortID))
		{
			return etpEventOut1;
		}
		if ("eventOut2".equalsIgnoreCase(eventPortID))
		{
			return etpEventOut2;
		}
		if ("eventOut3".equalsIgnoreCase(eventPortID))
		{
			return etpEventOut3;
		}
		if ("eventOut4".equalsIgnoreCase(eventPortID))
		{
			return etpEventOut4;
		}
		if ("eventOut5".equalsIgnoreCase(eventPortID))
		{
			return etpEventOut5;
		}
		if ("eventOut6".equalsIgnoreCase(eventPortID))
		{
			return etpEventOut6;
		}
		if ("eventOut7".equalsIgnoreCase(eventPortID))
		{
			return etpEventOut7;
		}
		if ("eventOut8".equalsIgnoreCase(eventPortID))
		{
			return etpEventOut8;
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


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpEventIn = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				switch (actPort) {
				case 1: etpEventOut1.raiseEvent(); break;
				case 2: etpEventOut2.raiseEvent(); break;
				case 3: etpEventOut3.raiseEvent(); break;
				case 4: etpEventOut4.raiseEvent(); break;
				case 5: etpEventOut5.raiseEvent(); break;
				case 6: etpEventOut6.raiseEvent(); break;
				case 7: etpEventOut7.raiseEvent(); break;
				case 8: etpEventOut8.raiseEvent(); break;
				}
		}
	};
	final IRuntimeEventListenerPort elpSelect1 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				actPort=1; 
		}
	};
	final IRuntimeEventListenerPort elpSelect2 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			actPort=2; 
		}
	};
	final IRuntimeEventListenerPort elpSelect3 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			actPort=3; 
		}
	};
	final IRuntimeEventListenerPort elpSelect4 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			actPort=4; 
		}
	};
	final IRuntimeEventListenerPort elpSelect5 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			actPort=5; 
		}
	};
	final IRuntimeEventListenerPort elpSelect6 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			actPort=6; 
		}
	};
	final IRuntimeEventListenerPort elpSelect7 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			actPort=7; 
		}
	};
	final IRuntimeEventListenerPort elpSelect8 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			actPort=8; 
		}
	};

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {

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

          super.stop();
      }
}