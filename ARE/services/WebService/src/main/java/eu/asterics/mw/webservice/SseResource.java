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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.asterics.mw.jnativehook.NativeHookServices;
import eu.asterics.mw.webservice.serverUtils.*;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.glassfish.jersey.media.sse.SseFeature;

import eu.asterics.mw.are.DeploymentManager;
import eu.asterics.mw.model.deployment.IChannel;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;

@Singleton
@Path("/")
public class SseResource {
    private static Logger logger = AstericsErrorHandling.instance.getLogger();
    private AstericsAPIEncoding astericsAPIEncoding = new AstericsAPIEncoding();

    // Client broadcasters
    private static SseBroadcaster deploymentBroadcaster = new SseBroadcaster();
    private static SseBroadcaster keyboardPressedBroadcaster = new SseBroadcaster();
    private static SseBroadcaster keyboardReleasedBroadcaster = new SseBroadcaster();
    private static SseBroadcaster modelStateBroadcaster = new SseBroadcaster();
    private static SseBroadcaster eventChannelBroadcaster = new SseBroadcaster();
    private static SseBroadcaster propertyChangeBroadcaster = new SseBroadcaster();
    private static Map<String, HashSet<EventOutput>> dataChannelBroadcaster = new HashMap<String, HashSet<EventOutput>>();

    // System listeners
    private static AREEventListener eventListener;
    private static RuntimeListener runtimeListener;
    private static AREKeyboardListener keyboardListener;

    public SseResource() {
        // create and register listeners
        SseResource.eventListener = new AREEventListener();
        SseResource.runtimeListener = new RuntimeListener();
        SseResource.keyboardListener = new AREKeyboardListener();

        AREServices.instance.registerAREEventListener(eventListener);
        AREServices.instance.registerRuntimeDataListener(runtimeListener);
        NativeHookServices.getInstance().registerAREKeyboardListener(keyboardListener);

        initializeDataChannelListeners();
    }

    public static void initializeDataChannelListeners() {

        // close EventOutput objects
        try {
            for (HashSet<EventOutput> eventOutputs : dataChannelBroadcaster.values()) {
                for (EventOutput eventOutput : eventOutputs) {
                    eventOutput.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // remove the client broadcaster entries
        dataChannelBroadcaster.clear();

        // remove the old data channel requests from the listener
        runtimeListener.clearDataChannelList();

        // initialize accordint to the new model
        if (DeploymentManager.instance.getCurrentRuntimeModel() != null) {
            for (IChannel channel : DeploymentManager.instance.getCurrentRuntimeModel().getChannels()) {
                dataChannelBroadcaster.put(channel.getChannelID(), new HashSet<EventOutput>());
            }
        }
    }

    /***********************************
     * SUBSCRIBERS - start
     **********************************/

    @Path("/runtime/deployment/listener")
    @GET
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput subscribe_AREDeploymentEvents() {
        final EventOutput eventOutput = new EventOutput();

        SseResource.deploymentBroadcaster.add(eventOutput);

        return eventOutput;
    }

    @Path("/runtime/keyboard/pressed/listener")
    @GET
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput subscribe_keyboardPressedEvents() {
        final EventOutput eventOutput = new EventOutput();

        SseResource.keyboardPressedBroadcaster.add(eventOutput);

        return eventOutput;
    }

    @Path("/runtime/keyboard/released/listener")
    @GET
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput subscribe_keyboardReleasedEvents() {
        final EventOutput eventOutput = new EventOutput();

        SseResource.keyboardReleasedBroadcaster.add(eventOutput);

        return eventOutput;
    }

    @Path("/runtime/model/state/listener")
    @GET
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput subscribe_modelStateEvents() {
        final EventOutput eventOutput = new EventOutput();

        SseResource.modelStateBroadcaster.add(eventOutput);

        return eventOutput;
    }

    @Path("/runtime/model/channels/event/listener")
    @GET
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput subscribe_eventChannelsEvents() {
        final EventOutput eventOutput = new EventOutput();

        SseResource.eventChannelBroadcaster.add(eventOutput);

        return eventOutput;
    }

    @Path("/runtime/model/channels/data/{channelId}/listener")
    @GET
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput subscribe_dataChannelsEvents(@PathParam("channelId") String channelId) {
        final EventOutput eventOutput = new EventOutput();

        try {
            String decodedId = astericsAPIEncoding.decodeString(channelId);
            runtimeListener.openDataChannel(decodedId);

            HashSet<EventOutput> eventOutputs = SseResource.dataChannelBroadcaster.get(decodedId);
            if (eventOutputs != null) {
                eventOutputs.add(eventOutput);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return eventOutput;
    }

    @Path("/runtime/model/components/properties/listener")
    @GET
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput subscribe_propertyChangeEvents() {
        final EventOutput eventOutput = new EventOutput();

        SseResource.propertyChangeBroadcaster.add(eventOutput);

        return eventOutput;
    }

    /***********************************
     * SUBSCRIBERS - end
     **********************************/

    /***********************************
     * BROADCASTER METHODS - start
     **********************************/

    /**
     * Static method that broadcasts an event to clients who were subscribed to deployment events.
     * 
     * @param eventMessage
     *            - the message of the event
     * 
     * @return
     */
    public static String broadcastDeploymentEvent(String eventMessage) {

        OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();

        eventBuilder.name("event");
        eventBuilder.mediaType(MediaType.TEXT_PLAIN_TYPE);
        eventBuilder.data(String.class, eventMessage);
        OutboundEvent event = eventBuilder.build();

        deploymentBroadcaster.broadcast(event);

        return eventMessage;
    }

    /**
     * Static method that broadcasts an event to clients who were subscribed to keyboard pressed events.
     *
     * @param eventMessage JSON formatted native key event that was triggered
     *
     * @return the json that was sent to the SSE subscribers
     */
    public static String broadcastKeyboardPressedEvent(String eventMessage) {
        return broadcastEvent(keyboardPressedBroadcaster, eventMessage, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Static method that broadcasts an event to clients who were subscribed to keyboard released events.
     *
     * @param eventMessage JSON formatted native key event that was triggered
     *
     * @return the json that was sent to the SSE subscribers
     */
    public static String broadcastKeyboardReleasedEvent(String eventMessage) {
        return broadcastEvent(keyboardReleasedBroadcaster, eventMessage, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Static method that broadcasts an event to clients who were subscribed to model state events.
     * 
     * @param eventMessage
     *            - the message of the event
     * 
     * @return
     */
    public static String broadcastModelStateEvent(String eventMessage) {

        OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();

        eventBuilder.name("event");
        eventBuilder.mediaType(MediaType.TEXT_PLAIN_TYPE);
        eventBuilder.data(String.class, eventMessage);
        OutboundEvent event = eventBuilder.build();

        SseResource.modelStateBroadcaster.broadcast(event);

        return eventMessage;
    }

    /**
     * Static method that broadcasts an event to clients who were subscribed to eventChannel events.
     * 
     * @param channelId
     *            - the id of the channel
     * @param targetComponentId
     *            - the component that received the event
     * @return
     */
    public static String broadcastEventChannelEvent(String channelId, String targetComponentId) {

        OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();

        Map<String, String> eventMap = new HashMap<String, String>();
        eventMap.put("channelId", channelId);
        eventMap.put("targetComponentId", targetComponentId);

        String eventMessage = ObjectTransformation.objectToJSON(eventMap);

        eventBuilder.name("event");
        eventBuilder.mediaType(MediaType.TEXT_PLAIN_TYPE);
        eventBuilder.data(String.class, eventMessage);
        OutboundEvent event = eventBuilder.build();

        SseResource.eventChannelBroadcaster.broadcast(event);

        return eventMessage;
    }

    /**
     * Static method that broadcasts an event to clients who were subscribed to dataChannel events.
     * 
     * @param channelId
     *            - the id of the channel
     * @param data
     *            - the data transmitted through the channel
     * @return
     */
    public static String broadcastDataChannelEvent(String channelId, String data) {

        OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();

        Map<String, String> eventMap = new HashMap<String, String>();
        eventMap.put("channelId", channelId);
        eventMap.put("data", data);

        String eventMessage = ObjectTransformation.objectToJSON(eventMap);

        eventBuilder.name("event");
        eventBuilder.mediaType(MediaType.TEXT_PLAIN_TYPE);
        eventBuilder.data(String.class, eventMessage);
        OutboundEvent event = eventBuilder.build();

        Iterator<EventOutput> iterator = dataChannelBroadcaster.get(channelId).iterator();
        while (iterator.hasNext()) {
            EventOutput eventOutput = iterator.next();

            try {
                if (eventOutput.isClosed()) {

                    iterator.remove();
                    if (SseResource.dataChannelBroadcaster.get(channelId).isEmpty()) {
                        SseResource.runtimeListener.closeDataChannel(channelId);
                    }
                } else {
                    eventOutput.write(event);
                }
            } catch (Exception e) {
                // Exception may be thrown if the 'isClosed()' library method is
                // not yet updated but the
                // SSE connection was closed by the client.
                // The connection will close in the next data transmission.
            }
        }

        return "";
    }

    /**
     * Static method that broadcasts an event to clients who were subscribed to property changes events
     * 
     * @param componentId
     *            - the id of the component that changed it's property
     * @param componentKey
     *            - the key of the property
     * @param newValue
     *            - the new value of the property
     * @return
     */
    public static String broadcastPropertyChangedEvent(String componentId, String componentKey, String newValue) {

        OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();

        Map<String, String> eventMap = new HashMap<String, String>();
        eventMap.put("componentId", componentId);
        eventMap.put("componentKey", componentKey);
        eventMap.put("newValue", newValue);

        String eventMessage = ObjectTransformation.objectToJSON(eventMap);

        eventBuilder.name("event");
        eventBuilder.mediaType(MediaType.TEXT_PLAIN_TYPE);
        eventBuilder.data(String.class, eventMessage);
        OutboundEvent event = eventBuilder.build();

        SseResource.propertyChangeBroadcaster.broadcast(event);

        return eventMessage;
    }

    /***********************************
     * BROADCASTER METHODS - end
     **********************************/


    private static String broadcastEvent(SseBroadcaster broadcaster, String eventMessage, MediaType eventType) {
        OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
        eventBuilder.name("event");
        eventBuilder.mediaType(eventType);
        eventBuilder.data(String.class, eventMessage);
        OutboundEvent event = eventBuilder.build();
        broadcaster.broadcast(event);
        return eventMessage;
    }

}