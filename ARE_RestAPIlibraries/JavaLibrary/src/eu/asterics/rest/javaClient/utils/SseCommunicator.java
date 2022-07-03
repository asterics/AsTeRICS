package eu.asterics.rest.javaClient.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.media.sse.EventListener;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.SseFeature;

public class SseCommunicator {
	public static final String MODEL_STATE_CHANGED = "model_state_changed";
	public static final String MODEL_CHANGED = "model_changed";
	public static final String MODEL_EVENT = "model_event"; //TODO
	
	public Set<String> eventNames;
	private Map<String, EventSource> eventSourceMap;
	private WebTarget webTarget;
	
	@SuppressWarnings("unused")
	private SseCommunicator() {	}
	
	public SseCommunicator(String baseUrl) {
		this.eventNames = new HashSet<String>();
		eventNames.add(SseCommunicator.MODEL_STATE_CHANGED);
		eventNames.add(SseCommunicator.MODEL_CHANGED);
		eventNames.add(SseCommunicator.MODEL_EVENT); //NOT IMPLEMENTED BY ARE SERVER
		
		this.eventSourceMap = new HashMap<String, EventSource>();
		
		Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
		webTarget = client.target(baseUrl + "events/subscribe");
	}
	
	
	/**
	 * Opens a persistent connection with ARE and listens for Server Sent Events
	 * with the given eventName
	 * 
	 * @param eventName - the name of the event
	 * @param listener - the {@link EventListener} which defines the action to take when an event occurs
	 * (See Jersey-Sse documentation for EventListener implementation)
	 * 
	 * @return - true if the subscription was successful and false otherwise
	 */
	public boolean subscribe(String eventName, EventListener listener) {
		if (!eventNames.contains(eventName)) {
			return false;
		}
		
		EventSource eventSource = EventSource.target(webTarget).build();
		eventSource.register(listener, eventName);
		eventSource.open();
		
		eventSourceMap.put(eventName, eventSource);
		
		return true;
	}
	
	/**
	 * Closes the connection for Server Sent Events
	 * 
	 * @param eventName - the name of the event to close
	 * 
	 * @return - true if the unsubscription was successful and false otherwise
	 */
	public boolean unsubscribe(String eventName) {
		if (eventSourceMap.containsKey(eventName)) {
			eventSourceMap.get(eventName).close();
			return true;
		}
		else {
			return false;
		}
	}
}
