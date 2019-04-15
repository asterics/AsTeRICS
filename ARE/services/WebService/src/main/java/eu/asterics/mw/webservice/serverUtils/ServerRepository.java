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
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.mw.webservice.serverUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import eu.asterics.mw.are.AREProperties;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.webservice.RestServer;
import eu.asterics.mw.webservice.SseResource;
import eu.asterics.mw.webservice.serverUtils.RestFunction.Description;

/**
 * The repository used by the Grizzly servers, holding static information.
 * 
 * @author Marios Komodromos (mkomod05@cs.ucy.ac.cy)
 *
 */
public class ServerRepository {
    // Singleton instance
    private static ServerRepository instance;

    // Rest Server configuration
    public static final String ARE_WEBSERVICE_PORT_REST_KEY = "ARE.webservice.port.REST";
    public static final String PATH_REST = "/rest";
    public static final int DEFAULT_PORT_REST = 8081;

    // Web Socket Server configuration
    public static final String PATH_WEBSOCKET = "/ws";
    public static final String PATH_WEBSOCKET_ASTERICS_DATA = "/astericsData";
    public static final String ARE_WEBSERVICE_PORT_WEBSOCKET_KEY = "ARE.webservice.port.websocket";
    public static final int DEFAULT_PORT_WEBSOCKET = 8082;

    // member variables holding property values
    private int portREST = DEFAULT_PORT_REST;
    private int portWebsocket = DEFAULT_PORT_WEBSOCKET;

    /**
     * Private ctor used for initializing the class.
     */
    private ServerRepository() {
        // init ports and paths with property values
        try {
            portREST = Integer.parseInt(AREProperties.instance.getProperty(ARE_WEBSERVICE_PORT_REST_KEY, String.valueOf(DEFAULT_PORT_REST)));
        } catch (NumberFormatException e) {
            AstericsErrorHandling.instance.getLogger().logp(Level.WARNING, this.getClass().getName(), "ServerRepository()",
                    "Configured port for REST service invalid: " + e.getMessage(), e);
        }
        // init ports and paths with property values
        try {
            portWebsocket = Integer.parseInt(AREProperties.instance.getProperty(ARE_WEBSERVICE_PORT_WEBSOCKET_KEY, String.valueOf(DEFAULT_PORT_WEBSOCKET)));
        } catch (NumberFormatException e) {
            AstericsErrorHandling.instance.getLogger().logp(Level.WARNING, this.getClass().getName(), "ServerRepository()",
                    "Configured port for Websocket service invalid: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the baseURI for the REST API.
     *
     * @return the baseUriRest
     */
    public URI getBaseUriREST() {
        return URI.create("http://0.0.0.0:" + getPortREST() + PATH_REST);
    }

    /**
     * Returns the baseURI for the websocket functionality. The actual websocket channels must be subpaths of it, e.g.
     * {@link ServerRepository#PATH_WEBSOCKET_ASTERICS_DATA}
     *
     * @return the baseUriWs
     */
    public URI getBaseUriWebsocket() {
        return URI.create("http://0.0.0.0:" + getPortWebsocket() + PATH_WEBSOCKET);
    }

    /**
     * Returns the configured port number for the REST API.
     *
     * @return
     */
    public int getPortREST() {
        return portREST;
    }

    /**
     * Returns the configured port number for the webserver, which should be the same as the one for the REST API.
     */
    public int getPortWebserver() {
        return getPortREST();
    }

    /**
     * Returns the configured port number for the Websocket functionality.
     *
     * @return
     */
    public int getPortWebsocket() {
        return portWebsocket;
    }

    /**
     * Returns a singleton instance of the ServerRepository class
     *
     * @return
     */
    public static ServerRepository getInstance() {
        if (instance == null) {
            instance = new ServerRepository();
        }
        return instance;
    }

    /**
     * This method generates a list of {@link RestFunction} entries for the given class object.
     * 
     * @param restClass
     * @return
     */
    private ArrayList<RestFunction> createListOfRestFunctions(Class restClass) {
        ArrayList<RestFunction> restFunctions = new ArrayList<RestFunction>();
        Method[] allMethods = restClass.getDeclaredMethods();
        for (Method method : allMethods) {
            if (Modifier.isPublic(method.getModifiers())) {
                System.out.println(method);
                // use the method
                RestFunction restFunction = new RestFunction();
                for (Annotation annotation : method.getDeclaredAnnotations()) {
                    if (annotation instanceof Path) {
                        restFunction.setPath(((javax.ws.rs.Path) annotation).value());
                    } else if (annotation instanceof Produces) {
                        restFunction.setProduces(Arrays.toString(((javax.ws.rs.Produces) annotation).value()));
                    } else if (annotation instanceof Consumes) {
                        restFunction.setConsumes(Arrays.toString(((javax.ws.rs.Consumes) annotation).value()));
                    } else if (annotation instanceof GET) {
                        restFunction.setHttpRequestType("GET");
                    } else if (annotation instanceof PUT) {
                        restFunction.setHttpRequestType("PUT");
                    } else if (annotation instanceof POST) {
                        restFunction.setHttpRequestType("POST");
                    } else if (annotation instanceof DELETE) {
                        restFunction.setHttpRequestType("DELETE");
                    } else if (annotation instanceof Description) {
                        restFunction.setDescription(((Description) annotation).value());
                    }
                    System.out.println("annotType: " + annotation.annotationType() + ", string: " + annotation);
                }
                List<String> pathParams = new ArrayList<String>();
                for (Annotation[] annotations : method.getParameterAnnotations()) {
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof PathParam) {
                            pathParams.add(((PathParam) annotation).value());
                        }
                    }
                }
                restFunction.setBodyParameter(pathParams.toString());

                if ("".equals(restFunction.getPath()) || "".equals(restFunction.getHttpRequestType())) {
                    // If there is not @Path or no @HTTRequestType it cannot be a REST function, so skip it.
                    continue;
                }
                restFunctions.add(restFunction);
            }
        }
        return restFunctions;
    }

    /**
     * Generates a list of {@link RestFunction} entries with all REST functions of the ARE.
     * 
     * @return
     */
    public ArrayList<RestFunction> createListOfRestFunctions() {
        ArrayList<RestFunction> restFunctions = createListOfRestFunctions(RestServer.class);
        restFunctions.addAll(createListOfRestFunctions(SseResource.class));
        return restFunctions;
    }
}
