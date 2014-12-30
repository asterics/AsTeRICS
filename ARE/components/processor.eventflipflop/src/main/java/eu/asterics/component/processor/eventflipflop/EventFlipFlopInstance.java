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

package eu.asterics.component.processor.eventflipflop;

import eu.asterics.mw.data.*;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import java.awt.event.InputEvent;
import java.io.*;
import java.net.*;
//import java.util.logging.Logger;
import eu.asterics.mw.services.AstericsErrorHandling;


/**
 * This plugin implements a simple event flip-flop. An event on the event listener
 * triggers the event trigger one, the next received event triggers event trigger two, 
 * the next triggers one once again, etc.
 * 
 * 
 * @author Roland Ossmann [ro@ki-i.at]
 *         Date: Mar 30, 2011
 *         Time: 11:08:01 AM
 */
public class EventFlipFlopInstance extends AbstractRuntimeComponentInstance
{
    private final DefaultRuntimeEventTriggererPort etpOut1  = new DefaultRuntimeEventTriggererPort();    
    private final DefaultRuntimeEventTriggererPort etpOut2  = new DefaultRuntimeEventTriggererPort(); 
    private boolean status = false;    
		
	
    public EventFlipFlopInstance()
    {
        // empty constructor - needed for OSGi service factory operations
    }

    /**
     * Standard method from framework
     * @param portID
     * @return
     */
    public IRuntimeInputPort getInputPort(String portID)
    {
        return null;
    }

    /**
     * Standard method from framework
     * @param portID
     * @return
     */
    public IRuntimeOutputPort getOutputPort(String portID)
    {
        return null;
    }
    
    /**
     * Standard method from framework
     * @param eventPortID
     * @return
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {

        if("event-in".equalsIgnoreCase(eventPortID))
        {
            return elpTriggerEvent;
        }
        if("selectOut1".equalsIgnoreCase(eventPortID))
        {
            return elpSelectOut1;
        }
        if("selectOut2".equalsIgnoreCase(eventPortID))
        {
            return elpSelectOut2;
        }
        return null;
    }

	/**
	 * Standard method from framework
	 * @param propertyName
	 * @return
	 */
    public Object getRuntimePropertyValue(String propertyName)
    {
        return null;
    }

    /**
     * Standard method from framework
     * @param propertyName
     * @param newValue
     * @return
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
        return null;
    }
 

    /**
     * Standard method from framework
     */
    final IRuntimeEventListenerPort elpTriggerEvent = new IRuntimeEventListenerPort()
    {
    	 public void receiveEvent(final String data)
    	 {
			if (status) {
				etpOut2.raiseEvent();
			} else {
				etpOut1.raiseEvent();
			}
			status = !status; 
    	 }
    };    

    final IRuntimeEventListenerPort elpSelectOut1 = new IRuntimeEventListenerPort()
    {
    	 public void receiveEvent(final String data)
    	 {
    		 status=false; 
    	 }
    };    

    final IRuntimeEventListenerPort elpSelectOut2 = new IRuntimeEventListenerPort()
    {
    	 public void receiveEvent(final String data)
    	 {
    		 status=true; 
    	 }
    };    
    /**
     * Standard method from framework
     * @param eventPortID
     * @return
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
        if("event-out1".equalsIgnoreCase(eventPortID))
        {
            return etpOut1;
        }
        else if("event-out2".equalsIgnoreCase(eventPortID))
        {
            return etpOut2;
        }
        return null;
    }
    
    /**
     * Standard method from framework
     */
    @Override
    public void start()
    {
    	status=false;
        super.start();
        AstericsErrorHandling.instance.reportInfo(this, "EventFlipFlop Instance started");
    }

    /**
     * Standard method from framework
     */
    @Override
    public void stop()
    {
        super.stop();
        AstericsErrorHandling.instance.reportInfo(this, "EventFlipFlop Instance stopped");
    }
}