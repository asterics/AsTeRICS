package eu.asterics.mw.webservice;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketEngine;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.webservice.serverUtils.ServerRepository;

public class WebServiceEngine {
	private static WebServiceEngine instance = null;
	
	private HttpServer restServer = null;
	private HttpServer wsServer = null;
	
	private Logger logger = AstericsErrorHandling.instance.getLogger();
	private AstericsDataApplication astericsApplication = null;


	/**
	 * Method that returns the instance of this class, based on the Singleton
	 * Design pattern.
	 * 
	 * @return - The {@link WebServiceEngine} object of this class.
	 */
	public static WebServiceEngine getInstance() {
		if(instance == null) {			
			instance = new WebServiceEngine();
		}
		return instance;
	}
	
	
	/**
	 * Initialize Grizzly Webservers (Rest Server and WS Server) and Websockets
	 * 
	 * @param bc
	 * @throws IOException
	 */
	public void initGrizzlyHttpService(BundleContext bc) throws IOException {
		
		logger.fine("Starting grizzly HTTP-server, " + ServerRepository.BASE_URI_REST);
			
		ResourceConfig rc = new ResourceConfig();
        
		//REST SERVER CONFIGURATION
        rc.registerClasses(RestServer.class, SseFeature.class);
        rc.register(new ResponseFilter());
        restServer = GrizzlyHttpServerFactory.createHttpServer(ServerRepository.BASE_URI_REST, rc);
        		
        restServer.start();

        
        logger.fine("Starting grizzly WS-server, " + ServerRepository.BASE_URI_WS);
        
        
        //WEB SERVICE SERVER CONFIGURATION
		wsServer = HttpServer.createSimpleServer("./data/webservice","0.0.0.0", ServerRepository.PORT_WS);
		
		final WebSocketAddOn addon = new WebSocketAddOn();
		for (NetworkListener listener : wsServer.getListeners()) {
			logger.fine("listener: "+listener.getHost()+":"+listener.getPort());			
		    listener.registerAddOn(addon);
		}
		astericsApplication = new AstericsDataApplication();

		logger.fine("Registering Websocket URI: "+ServerRepository.BASE_URI_WS+ServerRepository.PATH_WS_ASTERICS_DATA);
		WebSocketEngine.getEngine().register(ServerRepository.PATH_WS,ServerRepository.PATH_WS_ASTERICS_DATA,astericsApplication);	
		
		wsServer.start();
	}
	
	
	/**
	 * This is just an example of how to use the OSGI HttpService lookup mechanism to register a URI
	 * 
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
		if(restServer != null) {
			restServer.shutdownNow();
		}
		if(wsServer != null) {
			wsServer.shutdownNow();
		}
	}
	
	
	//data handling
	public IRuntimeInputPort getRuntimeInputPort() {
		return astericsApplication;
	}	
	
}
