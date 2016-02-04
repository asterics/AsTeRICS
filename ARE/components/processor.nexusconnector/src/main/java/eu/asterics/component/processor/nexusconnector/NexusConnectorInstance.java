

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
 *       This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
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
        if ("myInPort".equalsIgnoreCase(portID)) {
            return ipMyInPort;
        }
        return null;
    }

    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID) {
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
     * Input Ports for receiving values.
     */
    private final IRuntimeInputPort ipMyInPort = new DefaultRuntimeInputPort() {
        public void receiveData(byte[] data) {
            // insert data reception handling here, e.g.:
            // myVar = ConversionUtils.doubleFromBytes(data);
            // myVar = ConversionUtils.stringFromBytes(data);
            // myVar = ConversionUtils.intFromBytes(data);
            double val = ConversionUtils.doubleFromBytes(data);
            // System.out.println("NexusConnector IN = " + val);
            nexusWebSocket.sendMessage("{ \"path\": \"\", \"value\": " + val + " }");
        }
    };


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

        ListenableFuture<WebSocket> f = nexusClient.prepareGet("ws://localhost:9081/bindModel/example1/a")
            .execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(new WebSocketTextListener() {
                    @Override
                    public void onMessage(String message) {
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
            // TODO: Do something with the Exception
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
