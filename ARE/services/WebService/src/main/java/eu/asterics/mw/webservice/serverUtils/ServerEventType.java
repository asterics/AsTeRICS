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