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

package eu.asterics.mw.webservice;

import eu.asterics.mw.are.AREProperties;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

/**
 * A class that filters the responses of the Rest Server by adding specific
 * headers in order to overcome CORS problems.
 *
 * @author Marios Komodromos (mkomod05@cs.ucy.ac.cy)
 *
 */
public class ResponseFilter implements ContainerResponseFilter {

    public static final String ARE_REST_ALLOWED_ORIGINS = "ARE.REST.allowed.origins";

    static {
        AREProperties.instance.setDefaultPropertyValue(ARE_REST_ALLOWED_ORIGINS, "localhost,127.0.0.1,asterics.github.io,asterics-foundation.org", "Origins that are allowed to access the ARE REST API. Separate with comma (',').");
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {

        String bodyContent = "";
        String errorMessage = "";
        boolean error = false;

        try {// identify that an error occurred in the RestServer class

            bodyContent = response.getEntity().toString();
            if (bodyContent.startsWith("error:")) {
                error = true;
                errorMessage = bodyContent.substring(6);
            } else if (bodyContent.startsWith("<error>")) {
                error = true;
                errorMessage = bodyContent.substring(7, bodyContent.length() - 8);
            } else if (bodyContent.startsWith("{'error':")) {
                error = true;
                errorMessage = bodyContent.substring(10, bodyContent.length() - 2);
            }

            if (error) {
                response.setStatus(500);
                response.setEntity(errorMessage);
            }

        } catch (Exception ex) {
            // doing nothing
            // could write to the logger to report the server error
        }

        MultivaluedMap<String, Object> headers = response.getHeaders();

        // add headers to overcome CORS problems
        String origin = request.getHeaders().get("origin").get(0);
        if (isOriginAllowed(origin)) {
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
            headers.add("Access-Control-Allow-Credentials", "true");
            headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        }
    }

    /**
     * returns true if the given origin is allowed, specified by property value "ARE.REST.allowed.origins"
     * subdomains are also allowed.
     * e.g. allowed origin is "asterics-foundation.org"
     * => "asterics-foundation.org" is allowed
     * => "test.asterics-foundation.org" is allowed
     * => "test.asterics-foundation.at" is not allowed
     *
     * @param origin
     * @return
     */
    private boolean isOriginAllowed(String origin) {
        String allowedOriginsProperty = AREProperties.instance.getProperty(ARE_REST_ALLOWED_ORIGINS);
        List<String> allowedOrigins = Arrays.asList(allowedOriginsProperty.split(","));
        origin = origin.trim();
        origin = origin.replace("https://", "");
        origin = origin.replace("http://", "");
        if (origin.contains(":")) {
            origin = origin.substring(0, origin.indexOf(':')); // remove port
        }
        for (String testOrigin : allowedOrigins) {
            if (origin.startsWith(testOrigin) || origin.endsWith("." + testOrigin)) {
                return true;
            }
        }
        return false;
    }

}