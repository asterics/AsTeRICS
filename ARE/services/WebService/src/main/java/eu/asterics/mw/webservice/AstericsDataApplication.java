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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.glassfish.grizzly.websockets.Broadcaster;
import org.glassfish.grizzly.websockets.OptimizedBroadcaster;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketApplication;

//import com.corundumstudio.socketio.SocketConfig;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * This class handles registers as a websocket listener and acts as a proxy for
 * AsTeRICS plugins. It receives and sends data from to the websocket by
 * emulating {@link IRuntimeInputPort} and {@link IRuntimeOutputPort}
 * 
 * @author mad
 *
 */
public class AstericsDataApplication extends WebSocketApplication {
    ScheduledExecutorService sendService = Executors.newScheduledThreadPool(1);
    // broadcasts to all websocket clients
    private final Broadcaster broadcaster = new OptimizedBroadcaster();

    // IRuntimeOutputPort that manages the plugin input port listeners that want
    // to receive the websocket data
    final IRuntimeOutputPort opOut = new DefaultRuntimeOutputPort();

    /**
     * Callback which is called if a text message arrives at the websocket.
     */
    @Override
    public void onMessage(WebSocket socket, String text) {
        // System.out.println("In onMessage: "+text);
        super.onMessage(socket, text);

        opOut.sendData(ConversionUtils.stringToBytes(text));
    }

    /**
     * Callback which is called if a client connects to the websocket.
     */
    @Override
    public void onConnect(final WebSocket socket) {
        // TODO Auto-generated method stub
        super.onConnect(socket);
        AstericsErrorHandling.instance.getLogger().fine("WebSocket onConnect");
    }

    /**
     * Returns the {@link IRuntimeOutputPort} port of the WebSocket service. Is
     * used to send data to AsTeRICS plugins.
     * 
     * @param portID
     * @return
     */
    public IRuntimeOutputPort getOutputPort(String portID) {
        return opOut;
    }

    /**
     * Returns the {@link IRuntimeInputPort} port of the WebSocket service. Is
     * used to receive data from AsTeRICS plugins.
     * 
     * @param portID
     * @return
     */
    public IRuntimeInputPort getInputPort(String portID) {
        return ipIn;
    }

    /**
     * The IRuntimeInputPort instance that forwards the incoming data from the
     * Asterics plugins to the websocket.
     */
    private final IRuntimeInputPort ipIn = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            String dataStr = ConversionUtils.stringFromBytes(data);
            // logger.fine("Sending value: "+dataStr);
            broadcaster.broadcast(getWebSockets(), dataStr);
        }
    };

}
