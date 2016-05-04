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
 *     This project has been partly funded by the European Commission,
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */

package eu.asterics.mw.model.runtime.impl;

import eu.asterics.mw.are.DeploymentManager;
import eu.asterics.mw.model.deployment.IComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsModelExecutionThreadPool;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * Date: 1/7/11
 * Time: 2:01 PM
 */
public class DefaultRuntimeEventTriggererPort implements IRuntimeEventTriggererPort
{
	private static final Logger logger=AstericsErrorHandling.instance.getLogger();
    private static final String UNIQUE_ID_DELIM = ":";

	private final Map<String, IRuntimeEventListenerPort> eventListeners = new LinkedHashMap<String, IRuntimeEventListenerPort>();

    private String channelID = null;

    @Override
    public void setEventChannelID(String eventChannelID)
    {
        this.channelID = eventChannelID;
    }

    @Override
	public void raiseEvent() {
    	AstericsModelExecutionThreadPool.instance.execute(new Runnable() {
    		public void run() {
    			//MULTI-THREADED: Remove comments if you want to reenable multi-threaded execution approach.
    			//syncing on eventListeners ensures that a deployed model is not changed during event notification.
    			//logger.fine("Synchronizing on eventListeners, curThread: "+Thread.currentThread().getName()+", eventListeners: "+eventListeners.entrySet());
    			//synchronized (eventListeners) 
    			{
    				for (Map.Entry<String, IRuntimeEventListenerPort> elem : eventListeners.entrySet()) {

    					IRuntimeEventListenerPort eventListenerPort = elem.getValue();
    					if (eventListenerPort == null)
    						continue;

    					String targetComponentId = getTargetComponentId(elem.getKey());
    					//block event propagation if model and component is not up and running
    					//Disabled for now, because some important events are lost at startup.
    					/*
								if(!DeploymentManager.instance.isComponentRunning(targetComponentId)) {
									//logger.warning("Event could not be notified, target component not running: targetComponentId: "+targetComponentId);
									//System.out.println("E: "+targetComponentId);
									//return;
								}
    					 */

    					IComponentInstance targetComponent = DeploymentManager.instance
    							.getCurrentRuntimeModel()
    							.getComponentInstance(targetComponentId);
    					if (targetComponent != null) {									
    						//MULTI-THREADED: Remove comments if you want to reenable multi-threaded execution approach.
    						//We have to synchronize using the target component, because the component can be considered a black box, that must
    						//ensure data integrity. The data propagation, event notification, start, (stop), set Property should all synchronize on targetComponent.	
    						//logger.fine("Synchronizing on targetComponentId: "+targetComponentId);
    						//synchronized (targetComponent) 
    						{
    							eventListenerPort.receiveEvent(channelID);
    						}
    					} else {
    						logger.warning("Event could not be notified, target component not found: targetComponentId: "+targetComponentId);    							
    					}
    				}
    			}
    		}
    	});
	}

    @Override
    public void addEventListener(String targetComponentID, String eventPortID, IRuntimeEventListenerPort eventListenerPort)
    {
		//MULTI-THREADED: Remove comments if you want to reenable multi-threaded execution approach.
		//syncing on eventListeners ensures that a deployed model is not changed during event notification.
        //synchronized (eventListeners)
        {
            eventListeners.put(getUniqueID(targetComponentID, eventPortID), eventListenerPort);
        }
    }

    @Override
    public void removeEventListener(String targetComponentID, String eventPortID)
    {
		//MULTI-THREADED: Remove comments if you want to reenable multi-threaded execution approach.
		//syncing on eventListeners ensures that a deployed model is not changed during event notification.    	
        //synchronized (eventListeners)
        {
            eventListeners.remove(getUniqueID(targetComponentID, eventPortID));
        }
    }

    private String getUniqueID(final String targetComponentID, final String eventPortID)
    {
        return targetComponentID + UNIQUE_ID_DELIM + eventPortID;
    }
    
    /**
     * Split uniqueID to its elements
     * @param uniqueId
     * @return
     */
    private String getTargetComponentId(String uniqueId) {
    	return uniqueId.substring(0, uniqueId.indexOf(UNIQUE_ID_DELIM));
    }
}