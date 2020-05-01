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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketEngine;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.osgi.framework.BundleContext;

import eu.asterics.mw.services.*;
import eu.asterics.mw.services.ResourceRegistry;
import eu.asterics.mw.services.ResourceRegistry.RES_TYPE;
import eu.asterics.mw.webservice.serverUtils.ServerRepository;

/**
 * This class initializes the web services of the AsTeRICS framework. This includes an http-server, a REST interface and a websocket.
 * 
 * @author mad
 *
 */
public class WebServiceEngine {
    private static WebServiceEngine instance = null;

    private List<HttpServer> httpServers = new ArrayList<HttpServer>();

    private Logger logger = AstericsErrorHandling.instance.getLogger();
    private AstericsDataApplication astericsApplication = null;
    private static String KEYSTORE_LOC;
    private static final String KEYSTORE_PASS = "asterics";
    private SSLContextConfigurator sslCon;

    /**
     * Method that returns the instance of this class, based on the Singleton Design pattern.
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
     * @throws URISyntaxException
     */
    public void initGrizzlyHttpService(BundleContext bc) throws IOException, URISyntaxException {
        int NR_TRIES_PORT=3;
        int PORT_STEP_SIZE=5;
        boolean success=false;

        for(int i=0;i<NR_TRIES_PORT && !success;i++) {        
            int portRest=ServerRepository.getInstance().getPortREST()+i*PORT_STEP_SIZE;
            int portRestSSL=ServerRepository.getInstance().getSSLPortREST()+i*PORT_STEP_SIZE;
            int portWebsocket=ServerRepository.getInstance().getPortWebsocket()+i*PORT_STEP_SIZE;

            ServerRepository.getInstance().setPortREST(portRest);
            ServerRepository.getInstance().setSSLPortREST(portRestSSL);
            ServerRepository.getInstance().setPortWebsocket(portWebsocket);

            try{
                KEYSTORE_LOC = ResourceRegistry.getInstance().toString(ResourceRegistry.getInstance().getResource("keystore/keystore_server", RES_TYPE.PROFILE));
                sslCon = new SSLContextConfigurator();
                sslCon.setKeyStoreFile(KEYSTORE_LOC);
                sslCon.setKeyStorePass(KEYSTORE_PASS);

                HttpServer httpServer = null;

                if (ServerRepository.getInstance().isRESTEnabled()) {
                    httpServer = initWebAndRESTService(ServerRepository.getInstance().getBaseUriREST(), ServerRepository.getInstance().getPortREST(), false);
                    httpServer.start();
                    httpServers.add(httpServer);
                }

                if (ServerRepository.getInstance().isSSLRESTEnabled()) {
                    httpServer = initWebAndRESTService(ServerRepository.getInstance().getBaseUriSSLREST(), ServerRepository.getInstance().getSSLPortREST(), true);
                    httpServer.start();
                    httpServers.add(httpServer);
                }

                if (ServerRepository.getInstance().isWebsocketEnabled()) {
                    httpServer = initWebsocketService(ServerRepository.getInstance().getBaseUriWebsocket(), ServerRepository.getInstance().getPortWebsocket(), false);
                    httpServer.start();
                    httpServers.add(httpServer);
                }

                // Could not get the SSL Websocket functioning, to be debugged for the next release
                // if (ServerRepository.getInstance().isSSLWebsocketEnabled()) {
                // httpServer = initWebsocketService(ServerRepository.getInstance().getBaseUriSSLWebsocket(), ServerRepository.getInstance().getSSLPortWebsocket(),
                // true);
                // httpServer.start();
                // httpServers.add(httpServer);
                // }

                //in case of success, break the loop!!
                success=true;
                AREServices.instance.setRESTPort(portRest);

                logger.fine("REST and Websocket ports successfully registered.");
            }catch(Exception e) {
                logger.fine("Port registration failed for ports ["+portRest+","+portRestSSL+","+portWebsocket+"] "+e.getMessage());
            }
        }
    }

    private HttpServer initWebAndRESTService(URI baseURIREST, int portREST, boolean useSSL) throws URISyntaxException, IOException {
        logger.fine("Starting REST API at " + baseURIREST);

        ResourceConfig rc = new ResourceConfig();

        // REST SERVER CONFIGURATION
        rc.registerClasses(RestServer.class, SseResource.class, SseFeature.class);
        rc.register(new RequestFilter());
        rc.register(new ResponseFilter());

        HttpServer restServer;
        if (useSSL) {
            restServer = GrizzlyHttpServerFactory.createHttpServer(baseURIREST, rc, true,
                    new SSLEngineConfigurator(sslCon).setClientMode(false).setNeedClientAuth(false));
        } else {
            restServer = GrizzlyHttpServerFactory.createHttpServer(baseURIREST, rc);
        }

        // Normal Web server configuration (document root)
        String docRoot = ResourceRegistry.getInstance().toString(ResourceRegistry.getInstance().getResource(RES_TYPE.WEB_DOCUMENT_ROOT));
        logger.info("Registering webserver document root at " + docRoot);
        restServer.getServerConfiguration().addHttpHandler(new StaticHttpHandler(docRoot), "/");
        /*
         * //Code to register data and models folder as virtual subpaths? restServer.getServerConfiguration().addHttpHandler(new
         * StaticHttpHandler(ResourceRegistry.getInstance().toString(ResourceRegistry.getInstance().getResource("/",RES_TYPE.MODEL))), "models/");
         * restServer.getServerConfiguration().addHttpHandler(new
         * StaticHttpHandler(ResourceRegistry.getInstance().toString(ResourceRegistry.getInstance().getResource("/",RES_TYPE.DATA))), "data/"); /
         **/

        for (NetworkListener l : restServer.getListeners()) {
            // Otherwise we would have to restart the ARE in case of a file modification within the document root.
            l.getFileCache().setEnabled(false);
        }

        return restServer;
    }

    private HttpServer initWebsocketService(URI baseURIWS, int portWS, boolean useSSL) throws URISyntaxException, IOException {
        // Websocket configuration
        logger.fine("Initializing Websocket... ");
        String docRoot = ResourceRegistry.getInstance().toString(ResourceRegistry.getInstance().getResource(RES_TYPE.WEB_DOCUMENT_ROOT));
        HttpServer wsServer = HttpServer.createSimpleServer(docRoot, "0.0.0.0", portWS);

        WebSocketAddOn addon = new WebSocketAddOn();
        for (NetworkListener listener : wsServer.getListeners()) {
            logger.fine("listener: " + listener.getHost() + ":" + listener.getPort());
            listener.registerAddOn(addon);
            /*
             * Actually this should work according to https://www.programcreek.com/java-api-examples/?api=org.glassfish.grizzly.ssl.SSLEngineConfigurator but
             * could not get a connection to the wss:// websocket
             */

            if (useSSL) {
                logger.fine("Setting up secure WSS for wsBaseURI: " + baseURIWS);
                listener.setSecure(true);
                listener.setSSLEngineConfig(new SSLEngineConfigurator(sslCon).setClientMode(false).setNeedClientAuth(false));
            }
        }
        astericsApplication = new AstericsDataApplication();

        logger.fine("Registering Websocket URI: " + baseURIWS + ServerRepository.PATH_WEBSOCKET_ASTERICS_DATA);
        WebSocketEngine.getEngine().register(ServerRepository.PATH_WEBSOCKET, ServerRepository.PATH_WEBSOCKET_ASTERICS_DATA, astericsApplication);

        return wsServer;
    }

    /**
     * Returns the instance of the AsTeRICS websocket application containing IRuntimInputPort and IRuntimeOutputPort instances.
     * 
     * @return
     */
    public AstericsDataApplication getAstericsApplication() {
        return astericsApplication;
    }

    public void stop() {
        for (HttpServer server : httpServers) {
            server.shutdownNow();
        }
    }
}
