package eu.asterics.mw.webservice.serverUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import javax.ws.rs.PUT;

/**
 * The repository used by the Grizzly servers, holding static information.
 * 
 * @author Marios Komodromos (mkomod05@cs.ucy.ac.cy)
 *
 */
public class ServerRepository {
	
	//Rest Server configuration
	public static final String PATH_REST = "/rest";
	public static final int PORT_REST = 8081;
	public static final URI BASE_URI_REST = URI.create("http://0.0.0.0:"+PORT_REST+PATH_REST);
	
	
	//Web Service Server configuration
	public static final String PATH_WS = "/ws";
	public static final String PATH_WS_ASTERICS_DATA="/astericsData";
	public static final int PORT_WS = 8082;
	public static final URI BASE_URI_WS = URI.create("http://localhost:"+PORT_WS+PATH_WS);
	
	
	//a list with the functions of the Restful API
	public static final ArrayList<RestFunction> restFunctions = new ArrayList<RestFunction>() {
		{
			add( new RestFunction("GET", "/runtime/model", "", "text/xml", "",
					"Retrieves the currently deployed model in XML" ) );
			
			add( new RestFunction("PUT", "/runtime/model", "text/xml", "text/plain", "model in xml", 
					"Deploys the model given as a parameter") );
			
			add( new RestFunction("PUT", "/runtime/model/{filename}", "", "text/plain", "",
					"Deploys the model contained in the given filename") );
			
			add( new RestFunction("PUT", "/runtime/model/state/start", "", "text/plain", "",
					"Starts the model") );
			
			add( new RestFunction("PUT", "/runtime/model/state/stop", "", "text/plain", "",
					"Stops the model") );
			
			add( new RestFunction("PUT", "/runtime/model/state/pause", "", "text/plain", "",
					"Pauses the model") );
			
			add( new RestFunction("GET", "/runtime/model/state", "", "text/plain", "",
					"Returns the state of the deployed model") );
			
			add( new RestFunction("PUT", "/runtime/model/autorun/{filename}", "", "text/plain", "",
					"Deploys and starts the model contained in the given filename") );
			
			add( new RestFunction("GET", "/runtime/model/components", "", "application/json", "",
					"Retrieves all the components in the deployed model") );
			
			add( new RestFunction("GET", "/runtime/model/components/{componentId}", "", "application/json", "",
					"Retrieves all the property keys of the component with the given id") );
			
			add( new RestFunction("GET", "/runtime/model/components/{componentId}/{componentKey}", "", "text/plain", "",
					"Retrieves property value of a specific component, in the currently deployed model") );
			
			add( new RestFunction("PUT", "/runtime/model/components/{componentId}/{componentKey}", "text/plain", "text/plain", "property value",
					"Changes a property value of a specific component,  in the currently deployed model") );
			
			add( new RestFunction("GET", "/storage/models/{filename}", "", "text/xml", "",
					"Retrieves an xml representation of a model in a specific file") );
			
			add( new RestFunction("POST", "/storage/models/{filename}", "text/xml", "text/plain", "model in xml",
					"Stores a model in the given filename") );
			
			add( new RestFunction("DELETE", "/storage/models/{filename}", "", "text/plain", "",
					"Deletes the model with the given filename") );
			
			add( new RestFunction("GET", "/storage/models", "", "application/json", "",
					"Retrieves a list with all the model that are saved in the ARE repository") );
			
			add( new RestFunction("GET", "/restfunctions", "", "text/plain", "",
					"Retrieves a list with all the available rest functions") );
			
			add( new RestFunction("GET", "/storage/components/collection", "", "text/xml", "",
					"Returns an xml string containing the descriptors of the created components") );
			
			add( new RestFunction("GET", "/storage/components", "", "text/xml", "",
					"Returns a list with all the component descriptors contained in the ARE repository") );
			
			add( new RestFunction("GET", "/events/subscribe", "", "", "",
					"Opens a persistent connection with ARE to use it for Server Sent Events") );
		}
	};
	
	
	

}
