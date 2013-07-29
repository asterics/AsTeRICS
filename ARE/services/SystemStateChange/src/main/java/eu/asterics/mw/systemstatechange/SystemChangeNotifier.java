

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

package eu.asterics.mw.systemstatechange;

 
import java.util.logging.Logger;
import java.util.Vector;


import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsThreadPool;

/**
 * 
 * This module provides listeners for the low level system events
 * USB device attach/detach and system supend/wakeup
 * 
 *  
 * @author Chris Veigl
 */

public class SystemChangeNotifier 
{ 
	public static SystemChangeNotifier instance = null; 
	
    static      
    {
        System.loadLibrary("systemevent");
    	AstericsErrorHandling.instance.getLogger().fine("Loading \"systemevent.dll\" for lowlevel event notifications... ok!");
    	instance = new SystemChangeNotifier();
    }
 
	// declare member variables here
	native public int systemEventInit();
	native public int systemEventExit();
 
	final int  EVENT_REQUEST_SLEEP = 0;
	final int  EVENT_SLEEP=1;
	final int  EVENT_WAKE=2;
	final int  EVENT_USB_ATTACH=10; 
	final int  EVENT_USB_DETACH=11;
	
	Object mutex = new Object();
	
    Vector<SystemChangeListener> listeners = new Vector<SystemChangeListener>();   
    
   /**
    * The class constructor. This initaliases the USB event polling and starts
    * the polling thread.
    */
    private SystemChangeNotifier()
    {
		systemEventInit();
    }
    
    
    synchronized private void systemEventCallback(final int eventType)
    {
    	switch (eventType) {
    		case EVENT_REQUEST_SLEEP: 
    				AstericsErrorHandling.instance.getLogger().fine("systemEventCallback received:  REQUEST_SLEEP"); 
 				   for (SystemChangeListener l : listeners)
				   {
					   l.systemSleepRequested();
				   }
    			break;
    		
    		case EVENT_SLEEP: 
    				AstericsErrorHandling.instance.getLogger().fine("systemEventCallback received: ENTER_SLEEP"); 
  				   for (SystemChangeListener l : listeners)
				   {
					   l.systemSleep();
				   }
    			break;
    			
    		case EVENT_WAKE: 
    				AstericsErrorHandling.instance.getLogger().fine("systemEventCallback received: WAKE_UP"); 
   				   for (SystemChangeListener l : listeners)
				   {
					   l.systemResume();
				   }
    			break;
    		
    		case EVENT_USB_ATTACH:  
				   AstericsErrorHandling.instance.getLogger().fine("systemEventCallback received: USB_ATTACH");
				   for (SystemChangeListener l : listeners)
				   {
					   l.usbDevicesAttached();
				   }
				 break;
				   
    		case EVENT_USB_DETACH: 
				   AstericsErrorHandling.instance.getLogger().fine("Usb devices detached");
				   for (SystemChangeListener l : listeners)
				   {
					   l.usbDevicesRemoved();
				   }
				 break;
    	}
    }
    
    
    /**
     * 
     * @param listener
     */
    public void addListener(SystemChangeListener listener)
    {
    	synchronized(mutex)
    	{
		   for (SystemChangeListener l : listeners)
		   {
			   if (listener.equals(l))
			   {
				   return; 
			   }
		   } 
		   listeners.add(listener);
    	}
    }

    public void removeListener(SystemChangeListener listener)
    {
    	synchronized(mutex)
    	{
		   for (SystemChangeListener l : listeners)
		   {
			   if (listener.equals(l))
			   {
				   listeners.remove(l);
				   return;
			   }
		   }
    	}
    }
    
    public void clearListeners()
    {
    	synchronized(mutex)
    	{
    		listeners.clear();
    	}
    }
	
}