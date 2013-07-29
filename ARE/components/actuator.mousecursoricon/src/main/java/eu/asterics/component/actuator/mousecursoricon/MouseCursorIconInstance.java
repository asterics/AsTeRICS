

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

package eu.asterics.component.actuator.mousecursoricon;


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
 * <Describe purpose of this module>
 * 
 * 
 *  
 * @author <your name> [<your email address>]
 *         Date: 
 *         Time: 
 */
public class MouseCursorIconInstance extends AbstractRuntimeComponentInstance
{
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	String propIcon1name = "arrow";
	String propIcon2name = "arrow";
	String propIcon3name = "arrow";
	String propIcon4name = "arrow";
	String propIcon5name = "arrow"; 
	String propIcon6name = "arrow";
	String propIcon7name = "arrow";
	String propIcon8name = "arrow";
	String propIcon9name = "arrow";

	String curDir="C:\\AsTeRICS";
	
	// declare member variables here

    static      
    {
        System.loadLibrary("mouseicon");
    	AstericsErrorHandling.instance.getLogger().fine("Loading \"mouseicon.dll\" for mouse cursor icon modifications!");
    }
	native public int initCursor();
	native public int setCursor(String filename);
	native public int exitCursor();

     
   /**
    * The class constructor.  
    */
    public MouseCursorIconInstance()
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
		if ("iconName".equalsIgnoreCase(portID))
		{
			return ipIconName;
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
		if ("setIcon1".equalsIgnoreCase(eventPortID))
		{
			return elpSetIcon1;
		}
		if ("setIcon2".equalsIgnoreCase(eventPortID))
		{
			return elpSetIcon2;
		}
		if ("setIcon3".equalsIgnoreCase(eventPortID))
		{
			return elpSetIcon3;
		}
		if ("setIcon4".equalsIgnoreCase(eventPortID))
		{
			return elpSetIcon4;
		}
		if ("setIcon5".equalsIgnoreCase(eventPortID))
		{
			return elpSetIcon5;
		}
		if ("setIcon6".equalsIgnoreCase(eventPortID))
		{
			return elpSetIcon6;
		}
		if ("setIcon7".equalsIgnoreCase(eventPortID))
		{
			return elpSetIcon7;
		}
		if ("setIcon8".equalsIgnoreCase(eventPortID))
		{
			return elpSetIcon8;
		}
		if ("setIcon9".equalsIgnoreCase(eventPortID))
		{
			return elpSetIcon9;
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

        return null;
    }
		
    /**
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
		if ("icon1name".equalsIgnoreCase(propertyName))
		{
			return propIcon1name;
		}
		if ("icon2name".equalsIgnoreCase(propertyName))
		{
			return propIcon2name;
		}
		if ("icon3name".equalsIgnoreCase(propertyName))
		{
			return propIcon3name;
		}
		if ("icon4name".equalsIgnoreCase(propertyName))
		{
			return propIcon4name;
		}
		if ("icon5name".equalsIgnoreCase(propertyName))
		{
			return propIcon5name;
		}
		if ("icon6name".equalsIgnoreCase(propertyName))
		{
			return propIcon6name;
		}
		if ("icon7name".equalsIgnoreCase(propertyName))
		{
			return propIcon7name;
		}
		if ("icon8name".equalsIgnoreCase(propertyName))
		{
			return propIcon8name;
		}
		if ("icon9name".equalsIgnoreCase(propertyName))
		{
			return propIcon9name;
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
		if ("icon1name".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propIcon1name;
			propIcon1name = (String)newValue;
			return oldValue;
		}
		if ("icon2name".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propIcon2name;
			propIcon2name = (String)newValue;
			return oldValue;
		}
		if ("icon3name".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propIcon3name;
			propIcon3name = (String)newValue;
			return oldValue;
		}
		if ("icon4name".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propIcon4name;
			propIcon4name = (String)newValue;
			return oldValue;
		}
		if ("icon5name".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propIcon5name;
			propIcon5name = (String)newValue;
			return oldValue;
		}
		if ("icon6name".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propIcon6name;
			propIcon6name = (String)newValue;
			return oldValue;
		}
		if ("icon7name".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propIcon7name;
			propIcon7name = (String)newValue;
			return oldValue;
		}
		if ("icon8name".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propIcon8name;
			propIcon8name = (String)newValue;
			return oldValue;
		}
		if ("icon9name".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propIcon9name;
			propIcon9name = (String)newValue;
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipIconName  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			  //setCursor("C:\\data\\"+ConversionUtils.stringFromBytes(data)+".cur"); 
			setCursor("data\\cursors\\"+ConversionUtils.stringFromBytes(data)+".cur"); 
		}
	};


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpSetIcon1 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			  setCursor("data\\cursors\\"+propIcon1name+".cur"); 
		}
	}; 
	final IRuntimeEventListenerPort elpSetIcon2 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			  setCursor("data\\cursors\\"+propIcon2name+".cur"); 
		}
	};
	final IRuntimeEventListenerPort elpSetIcon3 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			  setCursor("data\\cursors\\"+propIcon3name+".cur"); 
		}
	};
	final IRuntimeEventListenerPort elpSetIcon4 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			  setCursor("data\\cursors\\"+propIcon4name+".cur"); 
		}
	};
	final IRuntimeEventListenerPort elpSetIcon5 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			  setCursor("data\\cursors\\"+propIcon5name+".cur"); 
		}
	};
	final IRuntimeEventListenerPort elpSetIcon6 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			  setCursor("data\\cursors\\"+propIcon6name+".cur"); 
		}
	};
	final IRuntimeEventListenerPort elpSetIcon7 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			  setCursor("data\\cursors\\"+propIcon7name+".cur"); 
		}
	};
	final IRuntimeEventListenerPort elpSetIcon8 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			  setCursor("data\\cursors\\"+propIcon8name+".cur"); 
		}  
	};
	final IRuntimeEventListenerPort elpSetIcon9 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			  setCursor("data\\cursors\\"+propIcon9name+".cur"); 
		}
	};   
  
	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  curDir = System.getProperty("user.dir");
          super.start();
          initCursor();
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
          exitCursor();
          super.stop();
      }
}