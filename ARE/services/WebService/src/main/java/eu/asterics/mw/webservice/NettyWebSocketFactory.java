package eu.asterics.mw.webservice;

import org.osgi.framework.BundleContext;

// import com.corundumstudio.socketio.Configuration;
// import com.corundumstudio.socketio.SocketIOClient;
// import com.corundumstudio.socketio.SocketIOServer;
// import com.corundumstudio.socketio.listener.ConnectListener;
/*
public class NettyWebSocketFactory {
	private SocketIOServer sioServer;

	private void initNettySocketIO(BundleContext bc) {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);
      
        System.out.println("init netty SocketIOServer");

        sioServer = new SocketIOServer(config);
        
        sioServer.addConnectListener(new ConnectListener() {
			
			@Override
			public void onConnect(SocketIOClient client) {
				System.out.println("Client connect: "+client.getRemoteAddress().toString());
				// TODO Auto-generated method stub
				client.sendEvent("updateData", "12X");
			}
		});
        sioServer.start();
	}

}
*/