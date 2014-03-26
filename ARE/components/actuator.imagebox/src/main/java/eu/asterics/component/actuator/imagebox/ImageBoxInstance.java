

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

package eu.asterics.component.actuator.imagebox;


import java.util.logging.Logger;
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
import eu.asterics.mw.services.AREServices;

/**
 * 
 * Implement GUI component which can display image.
 *  
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Jan 17, 2012
 *         Time: 12:31:41 AM
 */
public class ImageBoxInstance extends AbstractRuntimeComponentInstance
{
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 
    
	private final String PROP_CAPTION ="caption";
	private final String PROP_DEFAULT="default";
	private final String PROP_BACKGROUND_COLOR="backgroundColor";
	private final String ETP_CLICKED = "clicked";
	private final String ELP_CLEAR="clear";
	private final String IP_INPUT="input";
	private Logger logger = null;
	
	final IRuntimeEventTriggererPort etpClicked = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	String propCaption = "Image Box";
	String propDefault = "";
	int propBackgroundColor =  11;

	// declare member variables here
	private  GUI gui = null;
  
    
   /**
    * The class constructor.
    */
    public ImageBoxInstance()
    {
        // empty constructor
		logger = AstericsErrorHandling.instance.getLogger();
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
		if (IP_INPUT.equalsIgnoreCase(portID))
		{
			return ipInput;
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
		if (ELP_CLEAR.equalsIgnoreCase(eventPortID))
		{
			return elpClear;
		}
		
        return null;
    }
    
    /**
     * Input event port.
     */
    final IRuntimeEventListenerPort elpClear 	= new IRuntimeEventListenerPort()
    {
      @Override 
      public void receiveEvent(String data)
      {
    	  gui.setPicturePath("");
      }
    };    


    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
		if (ETP_CLICKED.equalsIgnoreCase(eventPortID))
		{
			return etpClicked;
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
		if (PROP_CAPTION.equalsIgnoreCase(propertyName))
		{
			return propCaption;
		}
		if (PROP_DEFAULT.equalsIgnoreCase(propertyName))
		{
			return propDefault;
		}
		if (PROP_BACKGROUND_COLOR.equalsIgnoreCase(propertyName))
		{
			return propBackgroundColor;
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
		if (PROP_CAPTION.equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propCaption;
			propCaption = (String)newValue;
			return oldValue;
		}
		if (PROP_DEFAULT.equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propDefault;
			propDefault = (String)newValue;
			return oldValue;
		}
		if (PROP_BACKGROUND_COLOR.equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propBackgroundColor;
			propBackgroundColor = Integer.parseInt(newValue.toString());
			if((propBackgroundColor<0)||(propBackgroundColor>12))
			{
				propBackgroundColor=11;
			}
			return oldValue;
		}
		

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipInput  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data) {
			if (gui == null) {
				logger.warning(this.getClass().getName()
						+ "."
						+ "receiveData: got data although gui not initialized, model running?");
				return;
			}

			String newImagePath = ConversionUtils.stringFromBytes(data);
			gui.setPicturePath(newImagePath);
		}
		
	};


	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  gui = new GUI(this,AREServices.instance.getAvailableSpace(this));
		  AREServices.instance.displayPanel(gui, this, true);
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
    	  AREServices.instance.displayPanel(gui, this, false);
    	  gui=null;
          super.stop();
      }
      
      
      /**
       * Returns the plugin caption.
       * @return   plugin caption
       */
      String getCaption()
      {
        return propCaption;
      }
      
      /**
       * Returns the background color.
       * @return   background color
       */
      int getBackgroundColor()
      {
    	  return propBackgroundColor;
      }
      
      /**
       * Returns the default picture path
       * @return   default picture path
       */
      String getDefaultPicturePath()
      {
    	  return propDefault;
      }
      

}