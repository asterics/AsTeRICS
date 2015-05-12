package eu.asterics.mw.webservice;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

/**
 * A class that filters the responses of the Rest Server by adding specific headers
 * in order to overcome CORS problems.
 * 
 * @author Marios Komodromos (mkomod05@cs.ucy.ac.cy)
 *
 */
public class ResponseFilter implements ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext request,
			ContainerResponseContext response) throws IOException {

    	String bodyContent = "";
    	String errorMessage = "";
    	boolean error = false;
    	
    	try {//identify that an error occured in the RestServer class

	    	bodyContent = response.getEntity().toString();
	    	if (bodyContent.startsWith("error:")) {
	    		error = true;
	    		errorMessage = bodyContent.substring(6);
	    	}
	    	else if (bodyContent.startsWith("<error>")) {
	    		error = true;
	    		errorMessage = bodyContent.substring(7, bodyContent.length()-8);
	    	}
	    	else if (bodyContent.startsWith("{'error':")) {
	    		error = true;
	    		errorMessage = bodyContent.substring(9, bodyContent.length()-2);
	    	}
	    	
	    	if (error) {
	    		response.setStatus(500);
	    		response.setEntity(errorMessage);
	    	}
    	
    	} catch (Exception ex) {
    		//doing nothing
    		//could write to the logger to report the server error
    	}
    	
		MultivaluedMap<String, Object> headers = response.getHeaders();
		
		headers.add("Access-Control-Allow-Origin", "*");
		headers.add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
		headers.add("Access-Control-Allow-Credentials", "true");
		headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
		
	}
	
}