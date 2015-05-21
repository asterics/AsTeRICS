package eu.asterics.mw.webservice.serverUtils;

/**
 * An enumeration class listing the event types that the server can send to the
 * subscribers.
 * 
 * @author Marios Komodrmos (mkomod05@cs.ucy.ac.cy)
 *
 */
public enum ServerEvent {
    MODEL_STATE_CHANGED ("ModelStateChanged"),
    MODEL_CHANGED ("ModelChanged"),
    REPOSITORY_CHANGED ("RepositoryChanged");
    
    private final String eventType;       

    private ServerEvent(String s) {
    	eventType = s;
    }

    public boolean equalsName(String otherType){
        return (otherType == null)? false:eventType.equals(otherType);
    }

    public String toString(){
       return eventType;
    }

}
