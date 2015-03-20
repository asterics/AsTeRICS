package eu.asterics.mw.webservice;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

public class ResponseFilter implements ContainerResponseFilter {
	
    @Override
    public ContainerResponse filter(ContainerRequest request,
            ContainerResponse response) {

    	String bodyContent = "";
    	String errorMessage = "";
    	boolean error = false;
    	
    	try {

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
    		//do nothing??
    	}
    	
        response.getHttpHeaders().add("Access-Control-Allow-Origin", "*");
        response.getHttpHeaders().add("Access-Control-Allow-Headers",
                "origin, content-type, accept, authorization");
        response.getHttpHeaders().add("Access-Control-Allow-Credentials", "true");
        response.getHttpHeaders().add("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");

        return response;
    }
}