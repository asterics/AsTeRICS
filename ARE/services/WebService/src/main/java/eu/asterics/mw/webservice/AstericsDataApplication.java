package eu.asterics.mw.webservice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.glassfish.grizzly.websockets.Broadcaster;
import org.glassfish.grizzly.websockets.OptimizedBroadcaster;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketApplication;

import sun.swing.StringUIClientPropertyKey;

//import com.corundumstudio.socketio.SocketConfig;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.services.AstericsErrorHandling;


public class AstericsDataApplication extends WebSocketApplication implements IRuntimeInputPort {
	ScheduledExecutorService sendService=Executors.newScheduledThreadPool(1);
    // initialize optimized broadcaster
    private final Broadcaster broadcaster = new OptimizedBroadcaster();
	private Logger logger=logger = AstericsErrorHandling.instance.getLogger();

	@Override
	public void onMessage(WebSocket socket, String text) {
		// TODO Auto-generated method stub
		
		System.out.println("In onMessage ");
		super.onMessage(socket, text);
	}
	

	@Override
	public void onConnect(final WebSocket socket) {
		// TODO Auto-generated method stub
		super.onConnect(socket);
		System.out.println("in onConnect");
		//String msg="data: updateData\n"+"args: [12X]\n\n";
		
		
		/*
		sendService.submit(new Runnable() {
			int counter=0;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(int i=0;i<10000;i++)
				socket.send(i +"");
			
			}
		});
		*/
	}


	@Override
	public synchronized void receiveData(byte[] data) {
		String dataStr=String.valueOf(ConversionUtils.doubleFromBytes(data));		
		//logger.fine("Sending value: "+dataStr);
		broadcaster.broadcast(getWebSockets(), dataStr);
	}


	@Override
	public void startBuffering(AbstractRuntimeComponentInstance c, String portID) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void stopBuffering(AbstractRuntimeComponentInstance c, String portID) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean isBuffered() {
		// TODO Auto-generated method stub
		return false;
	}
	

}
