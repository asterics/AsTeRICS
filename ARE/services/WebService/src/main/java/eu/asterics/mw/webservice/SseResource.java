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

package eu.asterics.mw.webservice;


import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.glassfish.jersey.media.sse.SseFeature;

import eu.asterics.mw.are.AREEvent;
import eu.asterics.mw.webservice.serverUtils.ServerEventType;

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
	public static String broadcastEvent(ServerEventType eventType, String eventMessage) {
		
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