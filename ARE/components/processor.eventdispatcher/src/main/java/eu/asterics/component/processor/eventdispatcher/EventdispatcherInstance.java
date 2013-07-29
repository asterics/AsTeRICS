
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

package eu.asterics.component.processor.eventdispatcher;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;

import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import java.util.*;
import java.util.logging.*;


/**
 *   Implements the event displatcher plugin, which compares an
 *   input string with up to 10 string templates and generates a
 *   dedicated event if a match is found.
 *     
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: Apr 23, 2011
 *         Time: 06:35:00 PM
 */
public class EventdispatcherInstance extends AbstractRuntimeComponentInstance
{
	private final int NUMBER_OF_COMMANDS = 10;
	private final String KEY_PROPERTY_COMMAND = "command";
	private final String KEY_PROPERTY_EVENT = "recognizedCommand";
    	
    private IRuntimeInputPort cmdPort = new CmdPort();
    final IRuntimeEventTriggererPort [] etpRecognizedCommand = new DefaultRuntimeEventTriggererPort[NUMBER_OF_COMMANDS];    

    private String[] commands = {"one","two","three","four","five","six","seven","eight","nine","ten"};
    
 
    /**
     * The class constructor, instantiates the event trigger ports
     */
    public EventdispatcherInstance()
    {
    	for (int i = 0; i < NUMBER_OF_COMMANDS; i++)
		{
    		etpRecognizedCommand[i] = new DefaultRuntimeEventTriggererPort();
		}
    }


    /**
     * returns an Input Port.
     * @param portID   the name of the port
     * @return         the input port or null if not found
     */
   public IRuntimeInputPort getInputPort(String portID)
    {
        if("cmd".equalsIgnoreCase(portID))
        {
            return cmdPort;
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
     * returns an Event Trigger Port.
     * @param eventPortID   the name of the port
     * @return         the Event Trigger port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
    	for (int i = 0; i < NUMBER_OF_COMMANDS; i++)
		{
			String s = KEY_PROPERTY_EVENT + (i + 1);
			if (s.equalsIgnoreCase(eventPortID))
			{
	            return etpRecognizedCommand[i];
			}
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
        for (int i = 0; i < NUMBER_OF_COMMANDS; i++)
       	{
        		String s = KEY_PROPERTY_COMMAND + (i + 1);
        		if (s.equalsIgnoreCase(propertyName))
        		{
        			return (commands[i]);
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
       	for (int i = 0; i < NUMBER_OF_COMMANDS; i++)
   		{
    			String s = KEY_PROPERTY_COMMAND + (i + 1);
    			if (s.equalsIgnoreCase(propertyName))
    			{
    				commands[i] = (String)newValue;
    	        	// Logger.getAnonymousLogger().info(String.format("Setting command %d to %s", i+1, newValue));
    			}
   		}
        return null;
    }



    /**
     * Input Port for receiving string command,
     *  raises event of matching command slot.
     */       
    private class CmdPort extends DefaultRuntimeInputPort
    {
        public void receiveData(byte[] data)
        {
            String cmd = ConversionUtils.stringFromBytes(data);
            
       		for (int i=0;i<NUMBER_OF_COMMANDS;i++)
       		{
        		    if (commands[i].equalsIgnoreCase(cmd))
        		    {
        			  etpRecognizedCommand[i].raiseEvent();
        		    }
       		}
        }

		
    }
    
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