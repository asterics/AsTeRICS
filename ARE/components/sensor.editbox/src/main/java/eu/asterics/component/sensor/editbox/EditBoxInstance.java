

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

package eu.asterics.component.sensor.editbox;


import java.util.logging.Logger;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;

/**
 * 
 * The Edit Box component 
 * 
 * 
 *  
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Dec 19, 2011
 *         Time: 12:31:41 AM
 */
public class EditBoxInstance extends AbstractRuntimeComponentInstance
{
	 final OutputPort opOutput = new OutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();
	private final String OP_OUTPUT="output";
	private final String PROP_CAPTION ="caption";
	private final String PROP_DEFAULT="default";
	private final String PROP_TEXT_COLOR="textColor";
	private final String PROP_BACKGROUND_COLOR="backgroundColor";
	private final String PROP_INSERT_ACTION="insertAction";
	private final String PROP_SEND_DEFAULT_VALUE="sendDefaultValue";
	private final String ELP_CLEAR="clear";
	private final String ELP_SEND="send";
	
	String propCaption = "Edit Box";
	String propDefault = "";
	int propTextColor=0;
	int propBackgroundColor=11;
	int propInsertAction=0;
	boolean propSendDefaultValue = false;
    public boolean propDisplayGUI=true;
	
	// declare member variables here
	private  GUI gui = null;
	private boolean guiReady=false;
    
   /**
    * The class constructor.
    */
    public EditBoxInstance()
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
		if (OP_OUTPUT.equalsIgnoreCase(portID))
		{
			return opOutput;
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
      if(ELP_CLEAR.equalsIgnoreCase(eventPortID))
      {
        return elpClear;
      }
      else if (ELP_SEND.equalsIgnoreCase(eventPortID)) 
      {
        return elpSend;
      }
	  
      else
      {
        return null;
      }
    }
    
    /**
     * Input event port.
     */
    final IRuntimeEventListenerPort elpClear 	= new IRuntimeEventListenerPort()
    {
      @Override 
      public void receiveEvent(String data)
      {
    	  if(guiReady)
    	  {
    		  gui.setText("");
    	  }
      }
    };    

	/**
     * Input event port.
     */
    final IRuntimeEventListenerPort elpSend	= new IRuntimeEventListenerPort()
    {
      @Override 
      public void receiveEvent(String data)
      {
    	  if(guiReady)
    	  {
    		  opOutput.sendData(gui.getText());
    	  }
		  
      }
    };    

	
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
		if (PROP_CAPTION.equalsIgnoreCase(propertyName))
		{
			return propCaption;
		}
		if (PROP_DEFAULT.equalsIgnoreCase(propertyName))
		{
			return propDefault;
		}
		if (PROP_TEXT_COLOR.equalsIgnoreCase(propertyName))
		{
			return propTextColor;
		}
		if (PROP_BACKGROUND_COLOR.equalsIgnoreCase(propertyName))
		{
			return propBackgroundColor;
		}
		if (PROP_INSERT_ACTION.equalsIgnoreCase(propertyName))
		{
			return propInsertAction;
		}
		if (PROP_SEND_DEFAULT_VALUE.equalsIgnoreCase(propertyName))
		{
			return propSendDefaultValue;
		}
    	if("displayGUI".equalsIgnoreCase(propertyName))
        {
            return propDisplayGUI;
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
		if (PROP_TEXT_COLOR.equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propTextColor;
			propTextColor = Integer.parseInt((String) newValue);
			if((propTextColor<0)||(propTextColor>12))
			{
				propTextColor=0;
			}
			return oldValue;
		}
		if (PROP_BACKGROUND_COLOR.equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propBackgroundColor;
			propBackgroundColor = Integer.parseInt((String) newValue);
			if((propBackgroundColor<0)||(propBackgroundColor>12))
			{
				propBackgroundColor=11;
			}
			return oldValue;
		}
		if (PROP_INSERT_ACTION.equalsIgnoreCase(propertyName))
		{
			final Object oldValue =propInsertAction;
			propInsertAction = Integer.parseInt((String) newValue);
			if((propInsertAction<0)||(propInsertAction>2))
			{
				propInsertAction=0;
			}
			return oldValue;
		}
		if (PROP_SEND_DEFAULT_VALUE.equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propSendDefaultValue;
			propSendDefaultValue = Boolean.parseBoolean((String) newValue);
			return oldValue;
		}
    	if("displayGUI".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propDisplayGUI;

            if("true".equalsIgnoreCase((String)newValue))
            {
            	propDisplayGUI = true;
            }
            else if("false".equalsIgnoreCase((String)newValue))
            {
            	propDisplayGUI = false;
            }
            return oldValue;
        }    	

		return null;
    }

    /**
     * Returns the text color.
     * @return   text color
     */
    int getTextColor()
    {
  	  return propTextColor;
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
      * called when model is started.
      */
      @Override
      public void start()
      {
			gui = new GUI(this,AREServices.instance.getAvailableSpace(this));
			if (propDisplayGUI) AREServices.instance.displayPanel(gui, this, true);
			guiReady=true;
			if (propSendDefaultValue) 
			{
				if(guiReady)
				{
					opOutput.sendData(gui.getText());
				}
			}
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
			guiReady=false;
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
       * Returns the default text.
       * @return   default text
       */
      String getDefaultText()
      {
        return propDefault;
      }
      
      /**
       * Returns the insert action parameter.
       * @return   insert action parameter
       */
      int getInsertAction()
      {
        return propInsertAction;
      }
      
	  
      /**
       * Plugin output port.
       */
      class OutputPort extends DefaultRuntimeOutputPort
      {
        public void sendData(String data)
        {
          super.sendData(ConversionUtils.stringToBytes(data));
        }
      }
    
}