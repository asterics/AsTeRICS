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

import java.io.IOException;

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
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, cache-control");
        headers.add("Access-Control-Allow-Credentials", "true");
        headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");

    }

}