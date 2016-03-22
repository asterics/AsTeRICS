package eu.asterics.mw.webservice;


import java.util.Arrays;
import java.util.List;
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
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.webservice.serverUtils.AstericsAPIEncoding;
import eu.asterics.mw.webservice.serverUtils.ObjectTransformation;
import eu.asterics.mw.webservice.serverUtils.ServerAREEventListener;
import eu.asterics.mw.webservice.serverUtils.ServerRepository;


/**
 * The implementation of the Rest Server class.
 * 
 * @author Marios Komodromos (mkomod05@cs.ucy.ac.cy)
 *
 */
@Path("/")
public class RestServer {
	private AsapiSupport asapiSupport = new AsapiSupport();
	private Logger logger = AstericsErrorHandling.instance.getLogger();
	private AstericsAPIEncoding astericsAPIEncoding = new AstericsAPIEncoding();
	
    static {
    	ServerAREEventListener eventListener = new ServerAREEventListener();
    	AREServices.instance.registerAREEventListener(eventListener);
    }

	
	
	@Path("/restfunctions")
	@GET
	@Produces (MediaType.APPLICATION_JSON)
	public String getRestFunctions() {
		String JSONresponse = ObjectTransformation.objectToJSON(ServerRepository.restFunctions);
		if (JSONresponse.equals("")) {
			JSONresponse = "{'error':'Couldn't retrieve the rest function signatures (Object serialization failure)'}";
		}
		
		return JSONresponse;
	}
	
	
	/**********************
	 *	Runtime resources 
	 **********************/
	
	@Path("/runtime/model")
    @GET
    @Produces(MediaType.TEXT_XML)
    public String getModel() {
		String response = null;
		String errorMessage = "";
		
		try {
			response = asapiSupport.getModel();
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "No model available for download";
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
			asapiSupport.deployModel(modelInXML);
			response = "Model deployed";
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Couldn't deploy the given model" + " (" + e.getMessage() + ")";
			response = "error:" + errorMessage;
		}
		
		return response;
    }
	

	@Path("/runtime/model/{filepath}")
    @PUT
	@Produces(MediaType.TEXT_PLAIN)
    public String deployFile(@PathParam("filepath") String filepath) {  
		String response;
		String errorMessage = "";
		String decodedFilepath = "";
		
		try {
			decodedFilepath = astericsAPIEncoding.decodeString(filepath);
			
			asapiSupport.deployFile(decodedFilepath);
			response = "'" + decodedFilepath + "'" + " model deployed";
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Couldn't deploy the model from file '" + decodedFilepath + "' (" + e.getMessage() + ")";
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
		
		String currentState = asapiSupport.getModelState();
		try {
			if (state.equals("start")) {
				if (currentState.equals("started")) {
					response = "Model was already started";
				}
				else {
					asapiSupport.runModel();
					response = "Model started";
				}
			} 
			else if (state.equals("stop")) {
				if (currentState.equals("stopped")) {
					response = "Model was already stopped";
				}
				else {
					asapiSupport.stopModel();
					response = "Model stopped";
				}
			}
			else if (state.equals("pause")) {
				if (currentState.equals("paused")) {
					response = "Model was already paused";
				}
				else {
					asapiSupport.pauseModel();
					response = "Model paused";
				}
			}
			else {
				errorMessage = "Unknown state passed as a parameter";
				response = "error:" + errorMessage;
			}
		} catch (Exception e) {
			errorMessage = "Could not " + state + " the model" + " (" + e.getMessage() + ")";
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
			response = asapiSupport.getModelState();
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Could not retrieve the state of the runtime model" + " (" + e.getMessage() + ")";
			response = "error:" + errorMessage;
		}

    	return response;
    }
	

	@Path("/runtime/model/autorun/{filepath}")
    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String autorun(@PathParam("filepath") String filepath) {
		String response;
		String errorMessage = "";
		String decodedFilepath = "";
		
		try {
			decodedFilepath = astericsAPIEncoding.decodeString(filepath);
			
			asapiSupport.autostart(decodedFilepath);
			response = decodedFilepath + " deployed and started";
		} catch (AREAsapiException e) {
			e.printStackTrace();
			errorMessage = "Could not autostart '" + decodedFilepath + "' (" + e.getMessage() + ")";
			response = "error:" + errorMessage;
		}

    	return response;
    }
	
	
	@Path("/runtime/model/components/ids")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getRuntimeComponentIds() {
		String response = "";
		String errorMessage = "";
		
		try {
			String[] array = asapiSupport.getComponents();
			
			response = ObjectTransformation.objectToJSON(Arrays.asList(array));
			if (response.equals("")) {
				response = "{'error':'Couldn't retrieve model components (Object serialization failure)'}";
			}
		} catch (Exception e) { 
			e.printStackTrace();
			errorMessage = "Couldn't retrieve model components" + " (" + e.getMessage() + ")";
			response = "{'error':'"+errorMessage+"'}";
		}
		
    	return response;
    }
	
	
	@Path("/runtime/model/components/{componentId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getRuntimeComponentPropertyKeys(@PathParam("componentId") String componentId) {
		String response;
		String errorMessage = "";
		String decodedId = "";
		
		try {
			decodedId = astericsAPIEncoding.decodeString(componentId);
			String[] array = asapiSupport.getComponentPropertyKeys(decodedId);
			
			response = ObjectTransformation.objectToJSON(Arrays.asList(array));
			if (response.equals("")) {
				response = "{'error':'Couldn't retrieve component property keys (Object serialization failure)'}";
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Couldn't retrieve property keys from '" + decodedId + "' (" + e.getMessage() + ")";
			response = "{'error':'"+errorMessage+"'}";
		}
		
    	return response;
    }
	
	
	@Path("/runtime/model/components/{componentId}/{componentKey}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getRuntimeComponentProperty(@PathParam("componentId") String componentId, @PathParam("componentKey") String componentKey) {
		String response;
		String errorMessage = "";
		String decodedId = "", decodedKey = "";
		
		try {
			decodedId = astericsAPIEncoding.decodeString(componentId);
			decodedKey = astericsAPIEncoding.decodeString(componentKey);
			response = asapiSupport.getComponentProperty(decodedId, decodedKey);
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Couldn't retrieve '"+ componentKey + "' property from '" + componentId + "' (" + e.getMessage() + ")";
			response = "error:"+errorMessage;
		}
		
    	return response;
    }
	
	
	@Path("/runtime/model/components/{componentId}/{componentKey}")
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String setRuntimeComponentProperty(String value, @PathParam("componentId") String componentId, @PathParam("componentKey") String componentKey) {
		String response;
		String errorMessage = "";
		String decodedId = "", decodedKey = "";
		
		try {
			decodedId = astericsAPIEncoding.decodeString(componentId);
			decodedKey = astericsAPIEncoding.decodeString(componentKey);
			response = asapiSupport.setComponentProperty(decodedId, decodedKey, value);
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Couldn't set '" + value + "' value to '" + decodedKey + "' from '" + decodedId + "' (" + e.getMessage() + ")";
			response = "error:"+errorMessage;
		}
		
    	return response;
    }
	
	
	
	
	/*************************************
	 *	Storage/ARE-repository resources
	 *************************************/
	
	@Path("/storage/models/{filepath}")
    @GET
    @Produces(MediaType.TEXT_XML)
    public String getModelFromFile(@PathParam("filepath") String filepath) {
		String response = null;
		String errorMessage;
		String decodedFilepath = "";
		
		try {
			decodedFilepath = astericsAPIEncoding.decodeString(filepath);
			
			response = asapiSupport.getModelFromFile(decodedFilepath);
		} catch (Exception e) {
			errorMessage = "Couldn't retrieve the model from '" + decodedFilepath + "' (" + e.getMessage() + ")";
			response = "<error>"+errorMessage+"</error>";
		}
		
    	return response;
    }
	
	
	@Path("/storage/models/{filepath}")
    @POST
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String storeModel(@PathParam("filepath") String filepath, String modelInXML) {  
		String response;
		String errorMessage;
		String decodedFilepath = "";
		
		if ( (modelInXML == "") || (modelInXML == null) ) {
			errorMessage = "invalid parameters";
			response = "error:"+errorMessage;
		}
		
		try {
			decodedFilepath = astericsAPIEncoding.decodeString(filepath);
			
			asapiSupport.storeModel(modelInXML, decodedFilepath);
			response = "Model stored";
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Could not store the model" + decodedFilepath + "' (" + e.getMessage() + ")";
			response = "error:"+errorMessage;
		}
		
		return response;
    }
	
	
	@Path("/storage/models/{filepath}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteModelFile(@PathParam("filepath") String filepath) {  
		String response;
		String errorMessage;
		String decodedFilepath = "";
		
		try {
			decodedFilepath = astericsAPIEncoding.decodeString(filepath);
			
			boolean isDeleted = asapiSupport.deleteModelFile(decodedFilepath);
			if (isDeleted) {
				response = "Model deleted";
			} 
			else {
				response = "Could not delete the model (Please check if the given filepath is correct)";
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Could not delete the model " + decodedFilepath + "' (" + e.getMessage() + ")";
			response = "error:"+errorMessage;
		}
		
		return response;
    }
	
	
	@Path("/storage/models/names")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String listAllStoredModels() {    	
		String response = null;
		String errorMessage;
		
		try {
			String[] array = asapiSupport.listAllStoredModels();

			response = ObjectTransformation.objectToJSON(Arrays.asList(array));
			if (response.equals("")) {
				response = "{'error':'Couldn't retrieve the stored models (Object serialization failure)'}";
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Couldn't retrieve the stored models" + "' (" + e.getMessage() + ")";
			response = "{'error':'"+errorMessage+"'}";
		}
		
		return response;
    }
	

	@Path("/storage/components/descriptors/xml")
	@GET
	@Produces(MediaType.TEXT_XML)
	public String getComponentDescriptorsAsXml() {
		String response = null;
		String errorMessage;

		try {
			response = asapiSupport.getComponentDescriptorsAsXml();
			if (response == null) {
				errorMessage = "Couldn't retrieve the components collection";
				response = "{'error':'"+errorMessage+"'}";
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Couldn't retrieve the components collection" + "' (" + e.getMessage() + ")";
			response = "{'error':'"+errorMessage+"'}";
		}
		
		return response;
	}
	
	
	@Path("/storage/components/descriptors/json")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getComponentDescriptorsAsJSON() {
		String response = null;
		String errorMessage;

		try {
			List<String> array = asapiSupport.getBundelDescriptors();
			response = ObjectTransformation.objectToJSON(Arrays.asList(array));
			if (response.equals("")) {
				response = "{'error':'Couldn't retrieve the components descriptors (Object serialization failure)'}";
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Couldn't retrieve the components descriptors";
			response = "{'error':'"+errorMessage+"'}";
		}
		
		return response;
	}
	
}

