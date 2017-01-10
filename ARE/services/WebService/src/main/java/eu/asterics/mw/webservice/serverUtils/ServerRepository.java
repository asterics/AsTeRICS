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

package eu.asterics.mw.webservice.serverUtils;

import java.net.URI;
import java.util.ArrayList;

/**
 * The repository used by the Grizzly servers, holding static information.
 * 
 * @author Marios Komodromos (mkomod05@cs.ucy.ac.cy)
 *
 */
public class ServerRepository {

    // Rest Server configuration
    public static final String PATH_REST = "/rest";
    public static final int PORT_REST = 8081;
    public static final URI BASE_URI_REST = URI.create("http://0.0.0.0:" + PORT_REST + PATH_REST);

    // Web Service Server configuration
    public static final String PATH_WS = "/ws";
    public static final String PATH_WS_ASTERICS_DATA = "/astericsData";
    public static final int PORT_WS = 8082;
    public static final URI BASE_URI_WS = URI.create("http://localhost:" + PORT_WS + PATH_WS);

    // a list with the functions of the Restful API
    public static final ArrayList<RestFunction> restFunctions = new ArrayList<RestFunction>() {
        {
            add(new RestFunction("GET", "/runtime/model", "", "text/xml", "",
                    "Retrieves the currently deployed model in XML"));

            add(new RestFunction("PUT", "/runtime/model", "text/xml", "text/plain", "model in xml",
                    "Deploys the model given as a parameter"));

            add(new RestFunction("PUT", "/runtime/model/{filename}", "", "text/plain", "",
                    "Deploys the model contained in the given filename"));

            add(new RestFunction("PUT", "/runtime/model/state/start", "", "text/plain", "", "Starts the model"));

            add(new RestFunction("PUT", "/runtime/model/state/stop", "", "text/plain", "", "Stops the model"));

            add(new RestFunction("PUT", "/runtime/model/state/pause", "", "text/plain", "", "Pauses the model"));

            add(new RestFunction("GET", "/runtime/model/state", "", "text/plain", "",
                    "Returns the state of the deployed model"));

            add(new RestFunction("PUT", "/runtime/model/autorun/{filename}", "", "text/plain", "",
                    "Deploys and starts the model contained in the given filename"));

            add(new RestFunction("GET", "/runtime/model/components/ids", "", "application/json", "",
                    "Retrieves all the components in the deployed model"));

            add(new RestFunction("GET", "/runtime/model/components/{componentId}", "", "application/json", "",
                    "Retrieves all the property keys of the component with the given id"));

            add(new RestFunction("GET", "/runtime/model/components/{componentId}/{componentKey}", "", "text/plain", "",
                    "Retrieves property value of a specific component, in the currently deployed model"));

            add(new RestFunction("PUT", "/runtime/model/components/{componentId}/{componentKey}", "text/plain",
                    "text/plain", "property value",
                    "Changes a property value of a specific component,  in the currently deployed model"));

            add(new RestFunction("GET", "/storage/models/{filename}", "", "text/xml", "",
                    "Retrieves an xml representation of a model in a specific file"));

            add(new RestFunction("POST", "/storage/models/{filename}", "text/xml", "text/plain", "model in xml",
                    "Stores a model in the given filename"));

            add(new RestFunction("DELETE", "/storage/models/{filename}", "", "text/plain", "",
                    "Deletes the model with the given filename"));

            add(new RestFunction("GET", "/storage/models/names", "", "application/json", "",
                    "Retrieves a list with all the model that are saved in the ARE repository"));

            add(new RestFunction("GET", "/restfunctions", "", "text/plain", "",
                    "Retrieves a list with all the available rest functions"));

            add(new RestFunction("GET", "/storage/components/descriptors/xml", "", "text/xml", "",
                    "Returns an xml string containing the descriptors of the created components with some modifications in order to be used by the webACS"));

            add(new RestFunction("GET", "/storage/components/descriptors/json", "", "text/xml", "",
                    "Retrieves the exact content of the component descriptors contained in the ARE repository"));

            add(new RestFunction("GET", "/events/subscribe", "", "", "",
                    "Opens a persistent connection with ARE to use it for Server Sent Events"));

            add(new RestFunction("PUT", "/runtime/model/components/input/{componentId}/{inputKey}", "text/plain",
                    "text/plain", "input value", "Sets an input port of a component to a given value"));

          add( new RestFunction("GET", "/runtime/model/name", "", "text/plain", "",
                  "Returns the name of the currently deployed model") );
        }
    };

}
