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
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.mw.webservice.serverUtils;

/**
 * An enumeration class listing the event types that the server can send to the
 * subscribers.
 * 
 * @author Marios Komodrmos (mkomod05@cs.ucy.ac.cy)
 *
 */
public enum ServerEventType {
    MODEL_STATE_CHANGED ("model_state_changed"),
    MODEL_CHANGED ("model_changed"),
    MODEL_EVENT ("model_event"); //TODO
    
    private final String eventType;       

    private ServerEventType(String eventType) {
    	this.eventType = eventType;
    }

    public boolean equalsName(String otherType){
        return (otherType == null)? false:eventType.equals(otherType);
    }

    public String toString(){
       return eventType;
    }

}