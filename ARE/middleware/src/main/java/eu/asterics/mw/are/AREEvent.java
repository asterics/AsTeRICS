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
package eu.asterics.mw.are;

public enum AREEvent {
	PRE_DEPLOY_EVENT ("pre_deploy_event"),
	POST_DEPLOY_EVENT ("post_deploy_event"),
	PRE_START_EVENT ("pre_start_event"),
	POST_START_EVENT ("post_start_event"),
	PRE_STOP_EVENT ("pre_stop_event"),
	POST_STOP_EVENT ("post_stop_event"),
	PRE_PAUSE_EVENT ("pre_pause_event"),
	POST_PAUSE_EVENT ("post_pause_event"),
	PRE_RESUME_EVENT ("pre_resume_event"),
	POST_RESUME_EVENT ("post_resume_event"),
	PRE_BUNDLES_EVENT ("pre_bundles_event"),
	POST_BUNDLES_EVENT ("post_bundles_event");
	
	private final String eventType;
	
    private AREEvent(String eventType) {
    	this.eventType = eventType;
    }
    
    public boolean equalsName(String otherType){
        return (otherType == null)? false:eventType.equals(otherType);
    }

    public String toString(){
       return eventType;
    }
    
}
