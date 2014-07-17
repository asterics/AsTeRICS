package eu.asterics.mw.webservice;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import eu.asterics.mw.are.AsapiSupport;
import eu.asterics.mw.are.exceptions.AREAsapiException;
import eu.asterics.mw.services.AREServices;

import java.util.Date;

@Path("/pojo")
public class SimpleRESTPojo {
    @GET
    @Produces("text/plain")
    public String pojo() {
        return "pojo ok @ " + new Date().toString();
    }
}