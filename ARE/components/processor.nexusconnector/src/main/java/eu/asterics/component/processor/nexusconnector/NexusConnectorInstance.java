/*
Copyright 2016 OCAD University

Licensed under the New BSD license.
*/

package eu.asterics.component.processor.nexusconnector;

import java.util.logging.Logger;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import eu.asterics.component.processor.nexusconnector.NexusConnectorInputPort;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.container.jdk.client.JdkClientContainer;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * This plugin provides a bidirectional connection to an instance of
 * the GPII Nexus.
 *
 * @author Simon Bates [sbates@ocadu.ca]
 */
public class NexusConnectorInstance extends AbstractRuntimeComponentInstance {

    private final String PROP_NEXUS_HOSTNAME = "nexusHostname";
    private final String PROP_NEXUS_PORT = "nexusPort";
    private final String PROP_NEXUS_COMPONENT_PATH = "nexusComponentPath";

    // Input Ports

    private final IRuntimeInputPort ipIn1d = new NexusConnectorInputPort(this, "inputs.in1d", NexusConnectorInputPort.InputType.DOUBLE);
    private final IRuntimeInputPort ipIn2d = new NexusConnectorInputPort(this, "inputs.in2d", NexusConnectorInputPort.InputType.DOUBLE);
    private final IRuntimeInputPort ipIn3d = new NexusConnectorInputPort(this, "inputs.in3d", NexusConnectorInputPort.InputType.DOUBLE);
    private final IRuntimeInputPort ipIn4d = new NexusConnectorInputPort(this, "inputs.in4d", NexusConnectorInputPort.InputType.DOUBLE);
    private final IRuntimeInputPort ipIn5s = new NexusConnectorInputPort(this, "inputs.in5s", NexusConnectorInputPort.InputType.STRING);
    private final IRuntimeInputPort ipIn6s = new NexusConnectorInputPort(this, "inputs.in6s", NexusConnectorInputPort.InputType.STRING);
    private final IRuntimeInputPort ipIn7s = new NexusConnectorInputPort(this, "inputs.in7s", NexusConnectorInputPort.InputType.STRING);
    private final IRuntimeInputPort ipIn8s = new NexusConnectorInputPort(this, "inputs.in8s", NexusConnectorInputPort.InputType.STRING);

    // Output Ports

    private final StatefulDoubleOutputPort opOut1d = new StatefulDoubleOutputPort();
    private final StatefulDoubleOutputPort opOut2d = new StatefulDoubleOutputPort();
    private final StatefulDoubleOutputPort opOut3d = new StatefulDoubleOutputPort();
    private final StatefulDoubleOutputPort opOut4d = new StatefulDoubleOutputPort();
    private final StatefulStringOutputPort opOut5s = new StatefulStringOutputPort();
    private final StatefulStringOutputPort opOut6s = new StatefulStringOutputPort();
    private final StatefulStringOutputPort opOut7s = new StatefulStringOutputPort();
    private final StatefulStringOutputPort opOut8s = new StatefulStringOutputPort();

    // Properties

    private String propNexusHostname = "localhost";
    private int propNexusPort = 9081;
    private String propNexusComponentPath = "nexus.asterics";

    // WebSocket connection

    private ClientEndpointConfig nexusClientConfig = ClientEndpointConfig.Builder.create().build();
    private ClientManager nexusClient = ClientManager.createClient(JdkClientContainer.class.getName());
    private Session nexusSession;
    private RemoteEndpoint.Basic nexusEndpoint;

    public NexusConnectorInstance() {
        // Empty constructor
    }

    /**
     * Returns an Input Port.
     * @param portID    The name of the port
     * @return          The input port or null if not found
     */
    @Override
    public IRuntimeInputPort getInputPort(String portID) {
        if ("in1d".equalsIgnoreCase(portID)) {
            return ipIn1d;
        } else if ("in2d".equalsIgnoreCase(portID)) {
            return ipIn2d;
        } else if ("in3d".equalsIgnoreCase(portID)) {
            return ipIn3d;
        } else if ("in4d".equalsIgnoreCase(portID)) {
            return ipIn4d;
        } else if ("in5s".equalsIgnoreCase(portID)) {
            return ipIn5s;
        } else if ("in6s".equalsIgnoreCase(portID)) {
            return ipIn6s;
        } else if ("in7s".equalsIgnoreCase(portID)) {
            return ipIn7s;
        } else if ("in8s".equalsIgnoreCase(portID)) {
            return ipIn8s;
        }
        return null;
    }

    /**
     * Returns an Output Port.
     * @param portID    The name of the port
     * @return          The output port or null if not found
     */
    @Override
    public IRuntimeOutputPort getOutputPort(String portID) {
        if ("out1d".equalsIgnoreCase(portID)) {
            return opOut1d.getPort();
        } else if ("out2d".equalsIgnoreCase(portID)) {
            return opOut2d.getPort();
        } else if ("out3d".equalsIgnoreCase(portID)) {
            return opOut3d.getPort();
        } else if ("out4d".equalsIgnoreCase(portID)) {
            return opOut4d.getPort();
        } else if ("out5s".equalsIgnoreCase(portID)) {
            return opOut5s.getPort();
        } else if ("out6s".equalsIgnoreCase(portID)) {
            return opOut6s.getPort();
        } else if ("out7s".equalsIgnoreCase(portID)) {
            return opOut7s.getPort();
        } else if ("out8s".equalsIgnoreCase(portID)) {
            return opOut8s.getPort();
        }
        return null;
    }

    /**
     * Returns an Event Listener Port.
     * @param eventPortID   The name of the port
     * @return              The EventListener port or null if not found
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        return null;
    }

    /**
     * Returns an Event Triggerer Port.
     * @param eventPortID   The name of the port
     * @return              The EventTriggerer port or null if not found
     */
    @Override
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {
        return null;
    }

    /**
     * Returns the value of the requested property.
     * @param propertyName  The name of the property
     * @return              The property value or null if not found
     */
    @Override
    public Object getRuntimePropertyValue(String propertyName) {
        if (PROP_NEXUS_HOSTNAME.equalsIgnoreCase(propertyName)) {
            return propNexusHostname;
        } else if (PROP_NEXUS_PORT.equalsIgnoreCase(propertyName)) {
            return propNexusPort;
        } else if (PROP_NEXUS_COMPONENT_PATH.equalsIgnoreCase(propertyName)) {
            return propNexusComponentPath;
        }
        return null;
    }

    /**
     * Sets a new value for the specified property.
     * @param propertyName  The name of the property
     * @param newValue      The new value for the property
     */
    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        if (PROP_NEXUS_HOSTNAME.equalsIgnoreCase(propertyName)) {
            final Object oldValue = propNexusHostname;
            propNexusHostname = newValue.toString();
            return oldValue;
        } else if (PROP_NEXUS_PORT.equalsIgnoreCase(propertyName)) {
            final Object oldValue = propNexusPort;
            propNexusPort = Integer.parseInt(newValue.toString());
            return oldValue;
        } else if (PROP_NEXUS_COMPONENT_PATH.equalsIgnoreCase(propertyName)) {
            final Object oldValue = propNexusComponentPath;
            propNexusComponentPath = newValue.toString();
            return oldValue;
        }
        return null;
    }

    /**
     * Called when the model is started.
     */
    @Override
    public void start() {
        super.start();

        try {
            String nexusUriPath = "/bindModel/" + propNexusComponentPath + "/connector";
            URI nexusUri = new URI("ws", null, propNexusHostname, propNexusPort, nexusUriPath, null, null);
            nexusSession = nexusClient.connectToServer(new Endpoint() {
                    @Override
                    public void onOpen(Session session, EndpointConfig config) {
                        session.addMessageHandler(new NexusConnectorMessageHandler(NexusConnectorInstance.this));
                        nexusEndpoint = session.getBasicRemote();
                    }
                }, nexusClientConfig, nexusUri);
        } catch (URISyntaxException e) {
            // TODO: Proper Exception handling
            throw new RuntimeException(e);
        } catch (DeploymentException e) {
            // TODO: Proper Exception handling
            throw new RuntimeException(e);
        } catch (IOException e) {
            // TODO: Proper Exception handling
            throw new RuntimeException(e);
        }
    }

    /**
     * Called when the model is paused.
     */
    @Override
    public void pause() {
        super.pause();
    }

    /**
     * Called when the model is resumed.
     */
    @Override
    public void resume() {
        super.resume();
    }

    /**
     * Called when the model is stopped.
     */
    @Override
    public void stop() {
        super.stop();

        try {
            nexusSession.close();
        } catch (IOException e) {
            // TODO: Proper Exception handling
            throw new RuntimeException(e);
        }
    }

    public void sendNexusChangeMessage(String path, double value) {
        sendNexusChangeMessage(path, JsonValue.valueOf(value));
    }

    public void sendNexusChangeMessage(String path, String value) {
        sendNexusChangeMessage(path, JsonValue.valueOf(value));
    }

    public void onNexusMessage(String message) {
        JsonValue changeMessage = Json.parse(message);
        if (changeMessage.isObject()) {
            JsonValue outputs = changeMessage.asObject().get("outputs");
            if (outputs.isObject()) {
                updateDoubleOutputPort("out1d", outputs, opOut1d);
                updateDoubleOutputPort("out2d", outputs, opOut2d);
                updateDoubleOutputPort("out3d", outputs, opOut3d);
                updateDoubleOutputPort("out4d", outputs, opOut4d);
                updateStringOutputPort("out5s", outputs, opOut5s);
                updateStringOutputPort("out6s", outputs, opOut6s);
                updateStringOutputPort("out7s", outputs, opOut7s);
                updateStringOutputPort("out8s", outputs, opOut8s);
            }
        }
    }

    private void sendNexusChangeMessage(String path, JsonValue value) {
        JsonObject message = Json.object().add("path", path).add("value", value);
        try {
            nexusEndpoint.sendText(message.toString());
        } catch (IOException e) {
            // TODO: Proper Exception handling
            throw new RuntimeException(e);
        }
    }

    private void updateDoubleOutputPort(String path, JsonValue outputs, StatefulDoubleOutputPort port) {
        JsonValue newValue = outputs.asObject().get(path);
        if (newValue.isNumber()) {
            port.update(newValue.asDouble());
        }
    }

    private void updateStringOutputPort(String path, JsonValue outputs, StatefulStringOutputPort port) {
        JsonValue newValue = outputs.asObject().get(path);
        if (newValue.isString()) {
            port.update(newValue.asString());
        }
    }

}
