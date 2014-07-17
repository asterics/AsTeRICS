package eu.asterics.mw.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import eu.asterics.mw.are.exceptions.AREAsapiException;
import eu.asterics.mw.services.AREServices;

@Path("/model/stop")
public class StopModel {    
    @GET
    @Produces("text/plain")
    public String stopModel() {    	

		AREServices.instance.stopModel();
    	return "Model stopped";
    }
}
