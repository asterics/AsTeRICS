package eu.asterics.rest.javaClient;

import eu.asterics.rest.javaClient.serialization.ObjectTransformation;
import eu.asterics.rest.javaClient.serialization.RestFunction;
import eu.asterics.rest.javaClient.utils.AstericsAPIEncoding;
import eu.asterics.rest.javaClient.utils.HttpCommunicator;
import eu.asterics.rest.javaClient.utils.HttpResponse;
import eu.asterics.rest.javaClient.utils.SseCommunicator;
import org.codehaus.jackson.type.TypeReference;
import org.glassfish.jersey.media.sse.EventListener;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that provides methods to communicate with the AsTeRICS Runtime Enviroment (ARE)
 *
 * @author Marios Komodromos
 *
 */
public class ARECommunicator {
	private HttpCommunicator httpCommunicator;
	private SseCommunicator sseCommunicator;
	private AstericsAPIEncoding astericsAPIEncoding;

	@SuppressWarnings("unused")
	private ARECommunicator() { }

	public ARECommunicator(String baseUrl) {
		httpCommunicator = new HttpCommunicator(baseUrl);
		sseCommunicator = new SseCommunicator(baseUrl);
		astericsAPIEncoding = new AstericsAPIEncoding();
	}

	/**
	 * Retrieves the currently deployed model in XML format
	 *
	 * @return - the model in XML format
	 * @throws Exception
	 */
	public String downloadDeployedModel() throws Exception {

		try {
			HttpResponse httpResponse = httpCommunicator.getRequest("/runtime/model",
					HttpCommunicator.DATATYPE_TEXT_XML);
			return httpResponse.getBody();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Retrieves an XML representation of a model in a specific file
	 * given as a parameter
	 *
	 * @param filepath - the filename that holds the model
	 *
	 * @return - The XML representation of the model
	 * @throws Exception
	 */
	public String downloadModelFromFile(String filepath) throws Exception {
		try {
			String encodedFilepath = astericsAPIEncoding.encodeString(filepath);
			HttpResponse httpResponse = httpCommunicator.getRequest("/storage/models/" + encodedFilepath,
					HttpCommunicator.DATATYPE_TEXT_XML);
			return httpResponse.getBody();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Deploys the model (XML format) given as a parameter
	 *
	 * @param modelInXML - XML describing an AsTeRICS model
	 *
	 * @return - a string informing if the deployment was successful
	 * @throws Exception
	 */
	public String uploadModel(String modelInXML) throws Exception {
		try {
			HttpResponse httpResponse = httpCommunicator.putRequest("/runtime/model",
					HttpCommunicator.DATATYPE_TEXT_XML, HttpCommunicator.DATATYPE_TEXT_PLAIN,
					modelInXML);
			return httpResponse.getBody();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Deploys and runs the model contained in the file
	 * given as a parameter
	 *
	 * @param filepath - the name of the file
	 *
	 * @return - a string informing if the autorun was successful
	 * @throws Exception
	 */
	public String autorun(String filepath) throws Exception {
		try {
			String encodedFilepath = astericsAPIEncoding.encodeString(filepath);
			HttpResponse httpResponse = httpCommunicator.putRequest("/runtime/model/autorun/" + encodedFilepath,
					HttpCommunicator.DATATYPE_TEXT_PLAIN);
			return httpResponse.getBody();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Pauses the running model
	 *
	 * @return - a string informing if the pause method was successful
	 * @throws Exception
	 */
	public String pauseModel() throws Exception {
		try {
			HttpResponse httpResponse = httpCommunicator.putRequest("/runtime/model/state/pause",
					HttpCommunicator.DATATYPE_TEXT_PLAIN);
			return httpResponse.getBody();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Starts the deployed model
	 *
	 * @return - a string informing if the start method was executed successfully
	 * @throws Exception
	 */
	public String startModel() throws Exception {
		try {
			HttpResponse httpResponse = httpCommunicator.putRequest("/runtime/model/state/start",
					HttpCommunicator.DATATYPE_TEXT_PLAIN);
			return httpResponse.getBody();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Stops the deployed model
	 *
	 * @return - a string informing if the stop method was executed successfully
	 * @throws Exception
	 */
	public String stopModel() throws Exception {
		try {
			HttpResponse httpResponse = httpCommunicator.putRequest("/runtime/model/state/stop",
					HttpCommunicator.DATATYPE_TEXT_PLAIN);
			return httpResponse.getBody();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Retrieves model state (STARTED, STOPPED, PAUSED)
	 *
	 * @return - the model state
	 * @throws Exception
	 */
	public String getModelState() throws Exception {
		try {
			HttpResponse httpResponse = httpCommunicator.getRequest("/runtime/model/state",
					HttpCommunicator.DATATYPE_TEXT_PLAIN);
			return httpResponse.getBody();
		} catch (Exception e) {
			throw e;
		}
	}

    /**
     * Retrieves current model name (=ID containing path and timestamp-suffix)
     *
     * @return the model name
     * @throws Exception
     */
    public String getModelName() throws Exception {
        HttpResponse httpResponse = httpCommunicator.getRequest("/runtime/model/name", HttpCommunicator.DATATYPE_TEXT_PLAIN);
        return httpResponse.getBody();
    }


	/**
	 * Stores a model in the given filename, to the ARE repository
	 *
	 * @param filepath - the filename that will contain the model
	 *
	 * @param modelInXML - the XML representation of the model
	 *
	 * @return - a string informing if the store operation was successful
	 * @throws Exception
	 */
	public String storeModel(String filepath, String modelInXML) throws Exception {
		try {
			String encodedFilepath = astericsAPIEncoding.encodeString(filepath);
			HttpResponse httpResponse = httpCommunicator.postRequest("/storage/models/" + encodedFilepath, null, null,
					HttpCommunicator.DATATYPE_TEXT_XML, HttpCommunicator.DATATYPE_TEXT_PLAIN,
					modelInXML);
			return httpResponse.getBody();
		} catch (Exception e) {
			throw e;
		}
	}

    /**
     * Stores data in the given filepath, to the ARE/data folder
     *
     * @param filepath - the filepath to store the data. can contain path + filename or only filename
     * @param data     - the data to save
     * @return - a string informing if the store operation was successful
     * @throws Exception
     */
    public String storeData(String filepath, String data) throws Exception {
        String encodedFilepath = astericsAPIEncoding.encodeString(filepath);
        HttpResponse httpResponse = httpCommunicator.postRequest("/storage/data/" + encodedFilepath, null, null,
                HttpCommunicator.DATATYPE_TEXT_PLAIN, HttpCommunicator.DATATYPE_TEXT_PLAIN,
                data);
        return httpResponse.getBody();
    }

	/**
	 * Stores webapp data in the given filepath, to the ARE/web/webapps/<webappId>/data folder
	 *
	 * @param filepath - the filepath to store the data. can contain path + filename or only filename
	 * @param data     - the data to save
	 * @return - a string informing if the store operation was successful
	 * @throws Exception
	 */
	public String storeWebappData(String webappId, String filepath, String data) throws Exception {
		String encodedWebappId = astericsAPIEncoding.encodeString(webappId);
		String encodedFilepath = astericsAPIEncoding.encodeString(filepath);
		HttpResponse httpResponse = httpCommunicator.postRequest("/storage/webapps/" + encodedWebappId + "/" + encodedFilepath, null, null,
				HttpCommunicator.DATATYPE_TEXT_PLAIN, HttpCommunicator.DATATYPE_TEXT_PLAIN,
				data);
		return httpResponse.getBody();
	}

	/**
	 * Retrieves webapp data from the given filepath, from the ARE/web/webapps/<webappId>/data folder
	 *
	 * @param filepath - the filepath to retrieve the data. can contain path + filename or only filename
	 * @return - the data saved at the given filepath
	 * @throws Exception
	 */
	public String getWebappData(String webappId, String filepath) throws Exception {
		String encodedWebappId = astericsAPIEncoding.encodeString(webappId);
		String encodedFilepath = astericsAPIEncoding.encodeString(filepath);
		HttpResponse httpResponse = httpCommunicator.getRequest("/storage/webapps/" + encodedWebappId + "/" + encodedFilepath, HttpCommunicator.DATATYPE_TEXT_PLAIN);
		return httpResponse.getBody();
	}

	/**
	 * Deploys a model contained in the given file
	 *
	 * @param filepath - the name of the file
	 *
	 * @return - a string informing if the model was deployed successfully
	 * @throws Exception
	 */
	public String deployModelFromFile(String filepath) throws Exception {
		try {
			String encodedFilepath = astericsAPIEncoding.encodeString(filepath);
			HttpResponse httpResponse = httpCommunicator.putRequest("/runtime/model/" + encodedFilepath,
					HttpCommunicator.DATATYPE_TEXT_PLAIN);
			return httpResponse.getBody();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Deletes the model with the given file name from the ARE repository
	 *
	 * @param filepath - the name of the model
	 *
	 * @return - a string informing if the file was deleted
	 * @throws Exception
	 */
	public String deleteModelFromFile(String filepath) throws Exception {
		try {
			String encodedFilepath = astericsAPIEncoding.encodeString(filepath);
			HttpResponse httpResponse = httpCommunicator.deleteRequest("/storage/models/"+encodedFilepath,
					HttpCommunicator.DATATYPE_TEXT_PLAIN);
			return httpResponse.getBody();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Retrieves all the components ids contained in the currently deployed model
	 *
	 * @return - An array of Strings containing the component ids of the components
	 * @throws Exception
	 */
	public String[] getRuntimeComponentIds() throws Exception {
		try {
			HttpResponse httpResponse = httpCommunicator.getRequest("/runtime/model/components/ids",
					HttpCommunicator.DATATYPE_APPLICATION_JSON);

			List<String> list = (List<String>) ObjectTransformation.JSONToObject(httpResponse.getBody(), List.class);

			return (String[]) list.toArray(new String[list.size()]);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Retrieves a list with all the models that are stored in the ARE repository
	 *
	 * @return - An array of Strings containing the names of the models
	 * @throws Exception
	 */
	public String[] listStoredModels() throws Exception {
		try {
			HttpResponse httpResponse = httpCommunicator.getRequest("/storage/models/names",
					HttpCommunicator.DATATYPE_APPLICATION_JSON);

			List<String> list = (List<String>) ObjectTransformation.JSONToObject(httpResponse.getBody(), List.class);

			return (String[]) list.toArray(new String[list.size()]);
		} catch (Exception e) {
			throw e;
		}
	}


	/**
	 * Retrieves all property keys of the component with the given componentId in the
	 * currently deployed model
	 *
	 * @param componentId - the component id
	 *
	 * @return - An array of Strings containing the property keys of the component
	 * @throws Exception
	 */
	public String[] getRuntimeComponentPropertyKeys(String componentId) throws Exception {
		try {
			String encodedId = astericsAPIEncoding.encodeString(componentId);
			HttpResponse httpResponse = httpCommunicator.getRequest("/runtime/model/components/"+encodedId,
					HttpCommunicator.DATATYPE_APPLICATION_JSON);

			List<String> list = (List<String>) ObjectTransformation.JSONToObject(httpResponse.getBody(), List.class);

			return (String[]) list.toArray(new String[list.size()]);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Retrieves property value of a specific component, in the currently deployed model
	 *
	 * @param componentId - the component id
	 * @param propertyKey - the property key
	 *
	 * @return - the value that corresponds to the given property key
	 * @throws Exception
	 */
	public String getRuntimeComponentProperty(String componentId, String propertyKey) throws Exception {
		try {
			String encodedId = astericsAPIEncoding.encodeString(componentId);
			String encodedKey = astericsAPIEncoding.encodeString(propertyKey);
			HttpResponse httpResponse = httpCommunicator.getRequest("/runtime/model/components/"+encodedId+"/"+encodedKey,
					HttpCommunicator.DATATYPE_TEXT_PLAIN);
			return httpResponse.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
    /**
     * Retrieves the dynamic property value of a specific component, in the currently deployed model
     *
     * @param componentId - the component id
     * @param propertyKey - the property key
     *
     * @return - the value that corresponds to the given property key
     * @throws Exception
     */
    public String[] getRuntimeComponentPropertyDynamic(String componentId, String propertyKey) throws Exception {
        try {
            String encodedId = astericsAPIEncoding.encodeString(componentId);
            String encodedKey = astericsAPIEncoding.encodeString(propertyKey);
            HttpResponse httpResponse = httpCommunicator.getRequest("/runtime/model/components/"+encodedId+"/"+encodedKey+"/dynamicproperty",
                    HttpCommunicator.DATATYPE_APPLICATION_JSON);
            
            List<String> list = (List<String>) ObjectTransformation.JSONToObject(httpResponse.getBody(), List.class);

            return (String[]) list.toArray(new String[list.size()]);
        } catch (Exception e) {
            throw e;
        }
    }	

	/**
	 * Changes a property value of a specific component, in the currently deployed model
	 *
	 * @param componentId - the component id
	 * @param propertyKey - the property key
	 * @param value - the new value
	 *
	 * @return - the previous value
	 * @throws Exception
	 */
	public String setRuntimeComponentProperty(String componentId, String propertyKey, String value) throws Exception {
		try {
			String encodedId = astericsAPIEncoding.encodeString(componentId);
			String encodedKey = astericsAPIEncoding.encodeString(propertyKey);
			HttpResponse httpResponse = httpCommunicator.putRequest("/runtime/model/components/"+encodedId+"/"+encodedKey,
					HttpCommunicator.DATATYPE_TEXT_PLAIN, HttpCommunicator.DATATYPE_TEXT_PLAIN,
					value);
			return httpResponse.getBody();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * sends data to a input port of a component of the currently deployed model
	 *
	 * @param componentId id of the component to send data to
	 * @param portId port of the component to send data to
	 * @param value data to send
	 * @return
	 * @throws Exception
	 */
	public String sendDataToInputPort(String componentId, String portId, String value) throws Exception {
		String encodedId = astericsAPIEncoding.encodeString(componentId);
		String encodedPortId = astericsAPIEncoding.encodeString(portId);
		String url = MessageFormat.format("/runtime/model/components/{0}/ports/{1}/data", encodedId, encodedPortId);
		HttpResponse httpResponse = httpCommunicator.putRequest(url,
				HttpCommunicator.DATATYPE_TEXT_PLAIN, HttpCommunicator.DATATYPE_TEXT_PLAIN,
				value);
		return httpResponse != null ? httpResponse.getBody() : null;
	}

	/**
	 * sends data to a input port of a component of the currently deployed model
	 *
	 * @param componentId id of the component to send data to
	 * @param eventPortId port of the component to send data to
	 * @return
	 * @throws Exception
	 */
	public String triggerEvent(String componentId, String eventPortId) throws Exception {
		String encodedId = astericsAPIEncoding.encodeString(componentId);
		String encodedPortId = astericsAPIEncoding.encodeString(eventPortId);
		String url = MessageFormat.format("/runtime/model/components/{0}/events/{1}", encodedId, encodedPortId);
		HttpResponse httpResponse = httpCommunicator.putRequest(url,
				HttpCommunicator.DATATYPE_TEXT_PLAIN);
		return httpResponse != null ? httpResponse.getBody() : null;
	}

	/**
	 * Returns a list with all the available rest functions provided via this API
	 *
	 * @return - an ArrayList of RestFunction objects that describe the available functions
	 * @throws Exception
	 */
	public ArrayList<RestFunction> getRestFunctions() throws Exception {
		try {
			HttpResponse httpResponse = httpCommunicator.getRequest("/restfunctions",
					HttpCommunicator.DATATYPE_APPLICATION_JSON);

			ArrayList<RestFunction> functions = (ArrayList<RestFunction>) ObjectTransformation.JSONToObject(httpResponse.getBody(),
					new TypeReference<ArrayList<RestFunction>>(){});

			return functions;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 *
	 *
	 * @return - Returns an xml string containing the descriptors of the created components with some modifications in order to be used by the webACS
	 * @throws Exception
	 */
	public String getComponentDescriptorsAsXml() throws Exception {
		try {
			HttpResponse httpResponse = httpCommunicator.getRequest("/storage/components/descriptors/xml",
					HttpCommunicator.DATATYPE_TEXT_XML);

			return httpResponse.getBody();
		} catch (Exception e) {
			throw e;
		}
	}


	/**
	 *
	 * @return - Retrieves the exact content of the components descriptors contained in the ARE repository
	 * @throws Exception
	 */
	public List<String> getComponentDescriptorsAsJSON() throws Exception {
		try {
			HttpResponse httpResponse = httpCommunicator.getRequest("/storage/components/descriptors/json",
					HttpCommunicator.DATATYPE_APPLICATION_JSON);

			List<String> descriptors = (List<String>) ObjectTransformation.JSONToObject(httpResponse.getBody(), List.class);

			return descriptors;
		} catch (Exception e) {
			throw e;
		}
	}


	/**
	 * Opens a persistent connection with ARE and listens for Server Sent Events
	 * with the given eventName
	 *
	 * @param eventName - the name of the event
	 * @param listener - the {@link EventListener} which defines the action to take when an event occurs
	 * (See Jersey-Sse documentation for EventListener implementation)
	 *
	 * @return - true if the subscription was successful and false otherwise
	 */
	public boolean subscribe(String eventName, EventListener listener) {
		return sseCommunicator.subscribe(eventName, listener);
	}

	/**
	 * Closes the connection for Server Sent Events
	 *
	 * @param eventName - the name of the event to close
	 *
	 * @return - true if the unsubscription was successful and false otherwise
	 */
	public boolean unsubscribe(String eventName) {
		return sseCommunicator.unsubscribe(eventName);
	}

}
