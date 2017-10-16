package eu.asterics.mw.webservice;

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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

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
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketEngine;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.webservice.serverUtils.ServerRepository;

/**
 * This class initializes the web services of the AsTeRICS framework. This
 * includes an http-server, a REST interface and a websocket.
 * 
 * @author mad
 *
 */
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
        if (instance == null) {
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

        logger.fine("Starting REST API at " + ServerRepository.getInstance().getBaseUriREST());

        ResourceConfig rc = new ResourceConfig();

        // REST SERVER CONFIGURATION
        rc.registerClasses(RestServer.class, SseResource.class, SseFeature.class);
        rc.register(new ResponseFilter());
        restServer = GrizzlyHttpServerFactory.createHttpServer(ServerRepository.getInstance().getBaseUriREST(), rc);
        restServer.getServerConfiguration().addHttpHandler(new StaticHttpHandler("./data/webservice"), "/");
        for (NetworkListener l : restServer.getListeners()) {
            l.getFileCache().setEnabled(false);
        }

        restServer.start();

        logger.fine("Initializing Websocket... ");

        // WEB SERVICE SERVER CONFIGURATION
        wsServer = HttpServer.createSimpleServer("./data/webservice", "0.0.0.0", ServerRepository.getInstance().getPortWS());

        final WebSocketAddOn addon = new WebSocketAddOn();
        for (NetworkListener listener : wsServer.getListeners()) {
            logger.fine("listener: " + listener.getHost() + ":" + listener.getPort());
            listener.registerAddOn(addon);
        }
        astericsApplication = new AstericsDataApplication();

        logger.fine(
                "Registering Websocket URI: " + ServerRepository.getInstance().getBaseUriWS() + ServerRepository.PATH_WS_ASTERICS_DATA);
        WebSocketEngine.getEngine().register(ServerRepository.PATH_WS, ServerRepository.PATH_WS_ASTERICS_DATA,
                astericsApplication);

        wsServer.start();
    }

    /**
     * Returns the instance of the AsTeRICS websocket application containing
     * IRuntimInputPort and IRuntimeOutputPort instances.
     * 
     * @return
     */
    public AstericsDataApplication getAstericsApplication() {
        return astericsApplication;
    }

    /**
     * This is just an example of how to use the OSGI HttpService lookup
     * mechanism to register a URI
     * 
     * @param bc
     * @throws NamespaceException
     * @throws ServletException
     */
    private void initHttpService(BundleContext bc) throws NamespaceException, ServletException {
        ServiceReference sr = bc.getServiceReference(HttpService.class.getName());
        System.out.println("sr: " + sr);
        if (sr != null) {
            HttpService http = (HttpService) bc.getService(sr);
            System.out.println("http: " + http);
            if (http != null) {
                http.registerResources("/", "./data/webservice", null);
                http.registerServlet("/time", new HttpServlet() {

                    @Override
                    protected void service(HttpServletRequest req, HttpServletResponse resp)
                            throws ServletException, IOException {
                        final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz",
                                Locale.US);
                        final String date = format.format(new Date(System.currentTimeMillis()));
                        resp.setContentType("text/plain");
                        resp.setContentLength(date.length());
                        resp.getWriter().write(date);

                        // super.service(req, resp);
                    }

                }, null, null);

            }
        }
    }

    public void stop() {
        // TODO Auto-generated method stub
        if (restServer != null) {
            restServer.shutdownNow();
        }
        if (wsServer != null) {
            wsServer.shutdownNow();
        }
    }
}
