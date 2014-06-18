package eu.asterics.mw.webservice;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketApplication;
import org.glassfish.grizzly.websockets.WebSocketEngine;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

public class WebServiceEngine {
	private static WebServiceEngine instance=null;
	
	private static final int PORT = 8080;
	private Logger logger=logger = AstericsErrorHandling.instance.getLogger();
	private HttpServer server=null; 
	private AstericsDataApplication astericsApplication=null;


	public static WebServiceEngine getInstance() {
		if(instance == null) {			
			instance=new WebServiceEngine();
		}
		return instance;
	}
	
	/**
	 * Initialize Grizzly Webserver and Websockets
	 * @param bc
	 * @throws IOException
	 */
	public void initGrizzlyHttpService(BundleContext bc) throws IOException {
		logger.fine("Starting grizzly HTTP-server, URI: http://localhost:"+PORT);
		server = HttpServer.createSimpleServer("./data/webservice","0.0.0.0", PORT);
		
		//NetworkListener listener=new NetworkListener("ext net", "192.168.0.100", PORT);
		
				
		/*//This is just to test how to add a HttpHandler
		server.getServerConfiguration().addHttpHandler(
			    new HttpHandler() {
			        public void service(Request request, Response response) throws Exception {
			            final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
			            final String date = format.format(new Date(System.currentTimeMillis()));
			            response.setContentType("text/plain");
			            response.setContentLength(date.length());
			            response.getWriter().write(date);
			        }
			    },
			    "/time");
			    */
		
		
		final WebSocketAddOn addon = new WebSocketAddOn();
		for (NetworkListener listener : server.getListeners()) {
			logger.fine("listener: "+listener.getHost()+":"+listener.getPort());
		    listener.registerAddOn(addon);
		}
		
		astericsApplication = new AstericsDataApplication();

		logger.fine("Registering Websocket URI: ws://localhost:" +PORT+"/ws/ws");
		WebSocketEngine.getEngine().register("/ws","/ws",astericsApplication);	
		server.start();
	}
	
	/**
	 * This is just an example of how to use the OSGI HttpService lookup mechanism to register a URI
	 * @param bc
	 * @throws NamespaceException
	 * @throws ServletException
	 */
	private void initHttpService(BundleContext bc) throws NamespaceException, ServletException {
		ServiceReference sr  =  bc.getServiceReference(HttpService.class.getName());
		System.out.println("sr: "+sr);
		if (sr != null) {
			HttpService http = (HttpService) bc.getService(sr);
			System.out.println("http: "+http);
			if (http != null) {
				http.registerResources("/", "./data/webservice",null);
				http.registerServlet("/time",new HttpServlet() {

					@Override
					protected void service(HttpServletRequest req,
							HttpServletResponse resp) throws ServletException,
							IOException {
			            final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
			            final String date = format.format(new Date(System.currentTimeMillis()));
			            resp.setContentType("text/plain");
			            resp.setContentLength(date.length());
			            resp.getWriter().write(date);						

						//super.service(req, resp);
					}
				 
					
				 } ,null,null);
				
			}
		}	
	}

	public void stop() {
		// TODO Auto-generated method stub
		if(server!=null) {
			server.shutdownNow();
		}
	}
	
	//data handling
	public IRuntimeInputPort getRuntimeInputPort() {
		return astericsApplication;
	}	
}
