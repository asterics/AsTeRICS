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
 * This class handles registers as a websocket listener and acts as a proxy for AsTeRICS plugins. It receives and sends data from to the websocket by emulating 
 * {@link IRuntimeInputPort} and {@link IRuntimeOutputPort}
 * @author mad
 *
 */
public class AstericsDataApplication extends WebSocketApplication {
	ScheduledExecutorService sendService=Executors.newScheduledThreadPool(1);
    // broadcasts to all websocket clients
    private final Broadcaster broadcaster = new OptimizedBroadcaster();
    
    //IRuntimeOutputPort that manages the plugin input port listeners that want to receive the websocket data
	final IRuntimeOutputPort opOut = new DefaultRuntimeOutputPort();

	/**
	 * Callback which is called if a text message arrives at the websocket.
	 */
	@Override
	public void onMessage(WebSocket socket, String text) {
		//System.out.println("In onMessage: "+text);
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
	 * Returns the {@link IRuntimeOutputPort} port of the WebSocket service. Is used to send data to AsTeRICS plugins.
	 * @param portID
	 * @return
	 */
    public IRuntimeOutputPort getOutputPort(String portID)
    {
        return opOut;
    }

    /**
     * Returns the {@link IRuntimeInputPort} port of the WebSocket service. Is used to receive data from AsTeRICS plugins.
     * @param portID
     * @return
     */
    public IRuntimeInputPort getInputPort(String portID)
    {
		return ipIn;
	}
	
    /**
     * The IRuntimeInputPort instance that forwards the incoming data from the Asterics plugins to the websocket.
     */
	private final IRuntimeInputPort ipIn  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			String dataStr=ConversionUtils.stringFromBytes(data);		
			//logger.fine("Sending value: "+dataStr);
			broadcaster.broadcast(getWebSockets(), dataStr);
		}
	};


}
