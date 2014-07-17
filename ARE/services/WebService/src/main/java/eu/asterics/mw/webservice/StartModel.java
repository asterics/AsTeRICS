package eu.asterics.mw.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import eu.asterics.mw.are.exceptions.AREAsapiException;
import eu.asterics.mw.services.AREServices;

@Path("/model/start")
public class StartModel {

    @GET
    @Produces("text/plain")
    public String startModel() {    	
    	try {
			AREServices.instance.runModel();
		} catch (AREAsapiException e) {
			return e.toString();
		}
    	return "Model started";
    }

}
