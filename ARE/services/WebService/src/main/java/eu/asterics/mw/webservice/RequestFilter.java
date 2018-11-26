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
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 * A class that filters the requests of the Rest Server and aborts the request if the origin is not allowed
 *
 * @author Benjamin Klaus (klaus@technikum-wien.at)
 *
 */
public class RequestFilter implements ContainerRequestFilter {

    public static final String ARE_REST_ALLOWED_ORIGINS = "ARE.REST.allowed.origins";

    static {
        AREProperties.instance.setDefaultPropertyValue(ARE_REST_ALLOWED_ORIGINS, "localhost,127.0.0.1,asterics.github.io,asterics-foundation.org", "Origins that are allowed to access the ARE REST API. Separate with comma (',').");
    }

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        String origin = null;
        if(request.getHeaders().get("origin") != null) {
            origin = request.getHeaders().get("origin").get(0);
        }

        if (!isOriginAllowed(origin)) {
            ResponseBuilder builder = Response.status(Response.Status.UNAUTHORIZED);
            request.abortWith(builder.build());
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
     * empty origins are allowed (everthing that does not come from a webbrowser)
     *
     * @param origin
     * @return
     */
    private boolean isOriginAllowed(String origin) {
        if(origin == null || origin.equals("")) {
            return true;
        }

        String allowedOriginsProperty = AREProperties.instance.getProperty(ARE_REST_ALLOWED_ORIGINS);
        List<String> allowedOrigins = Arrays.asList(allowedOriginsProperty.split(","));
        origin = origin.trim();
        origin = origin.replace("https://", "");
        origin = origin.replace("http://", "");
        if (origin.contains(":")) {
            origin = origin.substring(0, origin.indexOf(':')); // remove port
        }
        for (String testOrigin : allowedOrigins) {
            if (origin.equals(testOrigin) || origin.endsWith("." + testOrigin)) {
                return true;
            }
        }
        return false;
    }

}