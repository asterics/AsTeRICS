/*
Copyright 2016 OCAD University

Licensed under the New BSD license.
*/

package eu.asterics.component.processor.nexusconnector;


import java.util.logging.Logger;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.ws.WebSocket;
import com.ning.http.client.ws.WebSocketUpgradeHandler;
import com.ning.http.client.ws.WebSocketTextListener;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.providers.grizzly.GrizzlyAsyncHttpProvider;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 *  
 * @author <your name> [<your email address>]
 *         Date: 
 *         Time: 
 */
public class NexusConnectorInstance extends AbstractRuntimeComponentInstance
{
    // Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();


    // declare member variables here
    AsyncHttpClientConfig nexusClientConfig = new AsyncHttpClientConfig.Builder().build();
    AsyncHttpClient nexusClient = new AsyncHttpClient(new GrizzlyAsyncHttpProvider(nexusClientConfig), nexusClientConfig);
    WebSocket nexusWebSocket;

    /**
     * The class constructor.
     */
    public NexusConnectorInstance() {
        // empty constructor
    }

    /**
     * returns an Input Port.
     * @param portID   the name of the port
     * @return         the input port or null if not found
     */
    public IRuntimeInputPort getInputPort(String portID) {
        if ("A".equalsIgnoreCase(portID)) {
            return ipA;
        } else if ("B".equalsIgnoreCase(portID)) {
            return ipB;
        } else if ("C".equalsIgnoreCase(portID)) {
            return ipC;
        } else if ("D".equalsIgnoreCase(portID)) {
            return ipD;
        }
        return null;
    }

    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID) {
        if ("outStrOne".equalsIgnoreCase(portID)) {
            return opStrOne;
        }
        return null;
    }

    /**
     * returns an Event Listener Port.
     * @param eventPortID   the name of the port
     * @return         the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {
        return null;
    }

    /**
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName) {
        return null;
    }

    /**
     * sets a new value for the given property.
     * @param propertyName   the name of the property
     * @param newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        return null;
    }

    /**
     * Input Ports
     */
    private final IRuntimeInputPort ipA = new DefaultRuntimeInputPort() {
        public void receiveData(byte[] data) {
            double val = ConversionUtils.doubleFromBytes(data);
            nexusWebSocket.sendMessage("{ \"path\": \"inputs.a\", \"value\": " + val + " }");
        }
    };

    private final IRuntimeInputPort ipB = new DefaultRuntimeInputPort() {
        public void receiveData(byte[] data) {
            double val = ConversionUtils.doubleFromBytes(data);
            nexusWebSocket.sendMessage("{ \"path\": \"inputs.b\", \"value\": " + val + " }");
        }
    };

    private final IRuntimeInputPort ipC = new DefaultRuntimeInputPort() {
        public void receiveData(byte[] data) {
            double val = ConversionUtils.doubleFromBytes(data);
            nexusWebSocket.sendMessage("{ \"path\": \"inputs.c\", \"value\": " + val + " }");
        }
    };

    private final IRuntimeInputPort ipD = new DefaultRuntimeInputPort() {
        public void receiveData(byte[] data) {
            double val = ConversionUtils.doubleFromBytes(data);
            nexusWebSocket.sendMessage("{ \"path\": \"inputs.d\", \"value\": " + val + " }");
        }
    };

    /**
     * Output Ports
     */
    private String opStrOneValue = null;
    private final IRuntimeOutputPort opStrOne = new DefaultRuntimeOutputPort();


    /**
     * Event Listerner Ports.
     */


    /**
     * called when model is started.
     */
    @Override
    public void start() {
        super.start();
        System.out.println("NexusConnector START");

        ListenableFuture<WebSocket> f = nexusClient.prepareGet("ws://localhost:9081/bindModel/nexus.asterics/connector")
            .execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(new WebSocketTextListener() {
                    @Override
                    public void onMessage(String message) {
                        JsonValue changeMessage = Json.parse(message);
                        if (changeMessage.isObject()) {
                            JsonValue outputs = changeMessage.asObject().get("outputs");
                            if (outputs.isObject()) {
                                JsonValue outStrOneChangeValue = outputs.asObject().get("outStrOne");
                                if (outStrOneChangeValue.isString()) {
                                    String outStrOneChangeValueString  = outStrOneChangeValue.asString();
                                    if (opStrOneValue == null || !opStrOneValue.equals(outStrOneChangeValueString)) {
                                        System.out.println("NexusConnector OUT STR ONE VALUE CHANGE");
                                        opStrOneValue = outStrOneChangeValueString;
                                        opStrOne.sendData(ConversionUtils.stringToBytes(opStrOneValue));
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onOpen(WebSocket websocket) {
                    }

                    @Override
                    public void onClose(WebSocket websocket) {
                    }

                    @Override
                    public void onError(Throwable t) {
                    }
            }).build());

        try {
            nexusWebSocket = f.get();
        } catch (CancellationException e) {
            // TODO: Proper Exception handling
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            // TODO: Proper Exception handling
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            // TODO: Do something with the InterruptedException
        }
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        super.pause();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        super.resume();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        super.stop();
        System.out.println("NexusConnector STOP");
    }
}
