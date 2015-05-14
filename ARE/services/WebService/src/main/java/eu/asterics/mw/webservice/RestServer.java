package eu.asterics.mw.webservice;


import java.util.Arrays;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.asterics.mw.are.AsapiSupport;
import eu.asterics.mw.are.exceptions.AREAsapiException;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.webservice.serverUtils.ObjectTransformation;
import eu.asterics.mw.webservice.serverUtils.ServerRepository;


/**
 * The implementation of the Rest Server class.
 * 
 * @author Marios Komodromos (mkomod05@cs.ucy.ac.cy)
 *
 */
@Path("/server")
public class RestServer {
	AsapiSupport as = new AsapiSupport();
	//private Logger logger = AstericsErrorHandling.instance.getLogger();
	
	@Path("/restfunctions")
	@GET
	@Produces (MediaType.APPLICATION_JSON)
	public String getRestFunctions() {
		String JSONresponse = ObjectTransformation.objectToJSON(ServerRepository.restFunctions);
		
		return JSONresponse;
	}
	
	
	@Path("/runtime/model")
    @GET
    @Produces(MediaType.TEXT_XML)
    public String getModel() {
		String response = null;
		String errorMessage = "";
		
		try {
			response = as.getModel();
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Couldn't retrieve the model";
			response = "<error>"+errorMessage+"</error>";
		}
		
    	return response;
    }
	
	

	@Path("/runtime/model")
	@PUT
	@Consumes(MediaType.TEXT_XML)
	@Produces(MediaType.TEXT_PLAIN)
    public String deployModel(String modelInXML) {
		String response;
		String errorMessage = "";
		
		try {
			as.deployModel(modelInXML);
			response = "true";
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Couldn't deploy the given model";
			response = "error:" + errorMessage;
		}
		
		return response;
    }
	

	@Path("/runtime/model/{filename}")
    @PUT
	@Produces(MediaType.TEXT_PLAIN)
    public String deployFile(@PathParam("filename") String filename) {  
		String response;
		String errorMessage = "";
		
		try {
			as.deployFile(filename);
			response = "true";
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Couldn't deploy the model from file " + filename;
			response = "error:" + errorMessage;
		}
		
		return response;
    }
	

	@Path("/runtime/model/state/{state}")
    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String runModel(@PathParam("state")String state) {
		String response;
		String errorMessage = "";
		
		try {
			if (state.equals("start")) {
				as.runModel();
				response = "true";
			} 
			else if (state.equals("stop")) {
				as.stopModel();
				response = "true";
			}
			else if (state.equals("pause")) {
				as.pauseModel();
				response = "true";
			}
			else {
				errorMessage = "Unknown state";
				response = "error:" + errorMessage;
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Could not " + state + " the model";
			response = "error:" + errorMessage;
		}

    	return response;
    }
	
	
	@Path("/runtime/model/state")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getModelState() {
		String response;
		String errorMessage = "";
		
		try {
			response = as.getModelState();
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Could not retrieve the state of the runtime model";
			response = "error:" + errorMessage;
		}

    	return response;
    }
	

	@Path("/runtime/model/autorun/{filename}")
    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String autorun(@PathParam("filename") String filename) {
		String response;
		String errorMessage = "";
		
		try {
			as.autostart(filename);
			response = "true";
		} catch (AREAsapiException e) {
			e.printStackTrace();
			errorMessage = "Could not autostart " + filename;
			response = "error:" + errorMessage;
		}

    	return response;
    }
	
	
	@Path("/runtime/model/components")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getComponents() {
		String response;
		String errorMessage = "";
		
		try {
			String[] array = as.getComponents();
			
			response = ObjectTransformation.objectToJSON(Arrays.asList(array));
		} catch (Exception ex) {
			ex.printStackTrace();
			errorMessage = "Couldn't retrieve model components";
			response = "{'error':'"+errorMessage+"'}";
		}
		
    	return response;
    }
	
	
	@Path("/runtime/model/components/{componentId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getComponentPropertyKeys(@PathParam("componentId") String componentId) {
		String response;
		String errorMessage = "";
		
		try {
			String[] array = as.getComponentPropertyKeys(componentId);
			
			response = ObjectTransformation.objectToJSON(Arrays.asList(array));
		} catch (Exception ex) {
			ex.printStackTrace();
			errorMessage = "Couldn't retrieve property keys from " + componentId;
			response = "{'error':'"+errorMessage+"'}";
		}
		
    	return response;
    }
	
	
	@Path("/runtime/model/components/{componentId}/{componentKey}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getComponentProperty(@PathParam("componentId") String componentId, @PathParam("componentKey") String componentKey) {
		String response;
		String errorMessage = "";
		
		try {
			response = as.getComponentProperty(componentId, componentKey);
		} catch (Exception ex) {
			ex.printStackTrace();
			errorMessage = "Couldn't retrieve "+ componentKey + " property from " + componentId;
			response = "error:"+errorMessage;
		}
		
    	return response;
    }
	
	
	@Path("/runtime/model/components/{componentId}/{componentKey}")
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String setComponentProperty(String value, @PathParam("componentId") String componentId, @PathParam("componentKey") String componentKey) {
		String response;
		String errorMessage = "";
		try {
			response = as.setComponentProperty(componentId, componentKey, value);
		} catch (Exception ex) {
			ex.printStackTrace();
			errorMessage = "Couldn't set " + value + " value to " + componentKey + " from " + componentId;
			response = "error:"+errorMessage;
		}
		
    	return response;
    }
	
	

	
	
	
	

	@Path("/storage/models/{filename}")
    @GET
    @Produces(MediaType.TEXT_XML)
    public String getModelFromFile(@PathParam("filename") String filename) {
		String response = null;
		String errorMessage;
		
		try {
			response = as.getModelFromFile(filename);
		} catch (Exception e) {
			errorMessage = "Couldn't retrieve the model from " + filename;
			response = "<error>"+errorMessage+"</error>";
		}
		
    	return response;
    }
	
	
	@Path("/storage/models/{filename}")
    @POST
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String storeModel(@PathParam("filename") String filename, String modelInXML) {  
		String response;
		String errorMessage;
		
		if ( (modelInXML == "") || (modelInXML == null) ) {
			errorMessage = "invalid parameters";
			response = "error:"+errorMessage;
		}
		
		try {
			as.storeModel(modelInXML, filename);
			response = "true";
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Could not store the model";
			response = "error:"+errorMessage;
		}
		
		return response;
    }
	
	
	@Path("/storage/models/{filename}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteModelFile(@PathParam("filename") String filename) {  
		String response;
		String errorMessage;
		
		try {
			boolean b = as.deleteModelFile(filename);
			response = b + "";
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Could not delete the model";
			response = "error:"+errorMessage;
		}
		
		return response;
    }
	
	
	@Path("/storage/models")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String listAllStoredModels() {    	
		String response = null;
		String errorMessage;
		
		try {
			String[] array = as.listAllStoredModels();

			response = ObjectTransformation.objectToJSON(Arrays.asList(array));
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Couldn't retrieve the stored models";
			response = "{'error':'"+errorMessage+"'}";
		}
		
		return response;
    }

}
