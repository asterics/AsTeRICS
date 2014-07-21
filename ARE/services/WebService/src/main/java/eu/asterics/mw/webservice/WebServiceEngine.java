package eu.asterics.mw.webservice;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
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










import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.*;

import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

public class WebServiceEngine {
	private static WebServiceEngine instance=null;
	
	public static final String PATH_REST = "/rest";
	private static final int PORT_REST = 8081;
	private static final URI BASE_URI_REST = URI.create("http://localhost:"+PORT_REST+PATH_REST);
	
	public static final String PATH_WS = "/ws";
	public static final String PATH_WS_ASTERICS_DATA="/astericsData";
	private static final int PORT_WS = 8080;
	private static final URI BASE_URI_WS = URI.create("http://localhost:"+PORT_WS+PATH_WS);
	private Logger logger=logger = AstericsErrorHandling.instance.getLogger();
	private HttpServer restServer=null; 
	private HttpServer wsServer=null;
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
		logger.fine("Starting grizzly HTTP-server, "+BASE_URI_REST);
		
        ClasspathResourceConfig rc = new ClasspathResourceConfig();
        rc.add(new ApplicationConfig());
        restServer = GrizzlyServerFactory.createHttpServer(BASE_URI_REST, rc);
        		
		wsServer = HttpServer.createSimpleServer("./data/webservice","0.0.0.0", PORT_WS);
		
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
		for (NetworkListener listener : wsServer.getListeners()) {
			logger.fine("listener: "+listener.getHost()+":"+listener.getPort());			
		    listener.registerAddOn(addon);
		}
		
		astericsApplication = new AstericsDataApplication();

		logger.fine("Registering Websocket URI: "+BASE_URI_WS+PATH_WS_ASTERICS_DATA);
		WebSocketEngine.getEngine().register(PATH_WS,PATH_WS_ASTERICS_DATA,astericsApplication);	
		restServer.start();
		wsServer.start();
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
		if(restServer!=null) {
			restServer.shutdownNow();
		}
		if(wsServer!=null) {
			wsServer.shutdownNow();
		}

	}
	
	//data handling
	public IRuntimeInputPort getRuntimeInputPort() {
		return astericsApplication;
	}	
}
