package eu.asterics.mw.webservice;

import java.io.IOException;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.glassfish.jersey.media.sse.SseFeature;

import eu.asterics.mw.webservice.serverUtils.ServerEvent;

@Singleton
@Path("events")
public class SseResource {
	
	private static SseBroadcaster broadcaster = new SseBroadcaster();
	
	/**
	 * Static method that broadcast an event to clients who were subscribed to the
	 * SSE mechanism. This method will send events to a specific cluster of
	 * clients, specified by the eventName parameter.
	 * 
	 * @param eventTitle - the name of the event
	 * @param eventMessage - the message of the event
	 * 
	 * @return
	 */
	public static String broadcastEvent(ServerEvent eventType, String eventMessage) {
		
        OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();

        eventBuilder.name(eventType.toString());
        eventBuilder.mediaType(MediaType.TEXT_PLAIN_TYPE);
        eventBuilder.data(String.class, eventMessage);
        OutboundEvent event = eventBuilder.build();
 
        broadcaster.broadcast(event);
 
        return eventMessage;
	}
	
	
	/**
	 * Static method that broadcast an event to everyone who was subscribed to the
	 * SSE mechanism. This method does not specifies a specific cluster of clients
	 * so the generic cluster is used.
	 * 
	 * NOTE: A message sent to the generic cluster will not be received from clients
	 * who are subscribed to specific clusters.
	 * 
	 * @param eventMessage
	 * @return
	 */
	public static String broadcastEvent(String eventMessage) {
		
        OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();

        eventBuilder.mediaType(MediaType.TEXT_PLAIN_TYPE);
        eventBuilder.data(String.class, eventMessage);
        OutboundEvent event = eventBuilder.build();
 
        broadcaster.broadcast(event);
 
        return eventMessage;
	}
	
	
	@Path("subscribe")
    @GET
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput subscribe() {
        final EventOutput eventOutput = new EventOutput();
        
        SseResource.broadcaster.add(eventOutput);
        
        return eventOutput;
    }

}