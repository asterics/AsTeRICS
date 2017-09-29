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

package eu.asterics.mw.webservice;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
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
import eu.asterics.mw.are.DeploymentManager;
import eu.asterics.mw.are.exceptions.AREAsapiException;
import eu.asterics.mw.model.DataType;
import eu.asterics.mw.model.deployment.IChannel;
import eu.asterics.mw.model.deployment.IEventChannel;
import eu.asterics.mw.model.deployment.IPort;
import eu.asterics.mw.model.deployment.IRuntimeModel;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.webservice.serverUtils.AstericsAPIEncoding;
import eu.asterics.mw.webservice.serverUtils.ObjectTransformation;
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

    @Path("/restfunctions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getRestFunctions() {
        String JSONresponse = ObjectTransformation.objectToJSON(ServerRepository.restFunctions);
        if (JSONresponse.equals("")) {
            JSONresponse = "{'error':'Could not retrieve the rest function signatures (Object serialization failure)'}";
        }

        return JSONresponse;
    }

    /**********************
     * Runtime resources
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
            response = "<error>" + errorMessage + "</error>";
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
    public String runModel(@PathParam("state") String state) {
        String response;
        String errorMessage = "";

        String currentState = asapiSupport.getModelState();
        try {
            if (state.equals("start")) {
                if (currentState.equals("started")) {
                    response = "Model was already started";
                } else {
                    asapiSupport.runModel();
                    response = "Model started";
                }
            } else if (state.equals("stop")) {
                if (currentState.equals("stopped")) {
                    response = "Model was already stopped";
                } else {
                    asapiSupport.stopModel();
                    response = "Model stopped";
                }
            } else if (state.equals("pause")) {
                if (currentState.equals("paused")) {
                    response = "Model was already paused";
                } else {
                    asapiSupport.pauseModel();
                    response = "Model paused";
                }
            } else {
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
            errorMessage = "Could not retrieve model components" + " (" + e.getMessage() + ")";
            response = "{'error':'" + errorMessage + "'}";
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
            logger.fine(response + "\n\n\n");
            if (response.equals("")) {
                response = "{'error':'Couldn't retrieve component property keys (Object serialization failure)'}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Could not retrieve property keys from " + decodedId + " (" + e.getMessage() + ")";
            response = "{'error':'" + errorMessage + "'}";
        }

        return response;
    }

    @Path("/runtime/model/components/{componentId}/{componentKey}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getRuntimeComponentProperty(@PathParam("componentId") String componentId,
            @PathParam("componentKey") String componentKey) {
        String response;
        String errorMessage = "";
        String decodedId = "", decodedKey = "";

        try {
            decodedId = astericsAPIEncoding.decodeString(componentId);
            decodedKey = astericsAPIEncoding.decodeString(componentKey);
            response = asapiSupport.getComponentProperty(decodedId, decodedKey);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Couldn't retrieve '" + componentKey + "' property from '" + componentId + "' ("
                    + e.getMessage() + ")";
            response = "error:" + errorMessage;
        }

        return response;
    }

    @Path("/runtime/model/components/{componentId}/{componentKey}")
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String setRuntimeComponentProperty(String value, @PathParam("componentId") String componentId,
            @PathParam("componentKey") String componentKey) {
        String response;
        String errorMessage = "";
        String decodedId = "", decodedKey = "";

        try {
            decodedId = astericsAPIEncoding.decodeString(componentId);
            decodedKey = astericsAPIEncoding.decodeString(componentKey);
            response = asapiSupport.setComponentProperty(decodedId, decodedKey, value);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Couldn't set '" + value + "' value to '" + decodedKey + "' from '" + decodedId + "' ("
                    + e.getMessage() + ")";
            response = "error:" + errorMessage;
        }

        return response;
    }
    

    @Path("/runtime/model/components/properties")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String setRuntimeComponentProperties(String bodyContent) {
        String response = "";
        String errorMessage = "";
        Set<String> changedValues = new HashSet<String>();
        
        try {
            Map<String, Map<String, String>> propertyMap = new HashMap<String, Map<String, String>>();
            propertyMap = (Map<String, Map<String, String>>) ObjectTransformation.JSONToObject(bodyContent, Map.class);

            for (String componentId: propertyMap.keySet()) {
            	Map<String, String> componentPropertyMap = propertyMap.get(componentId);
	            for (String componentKey: componentPropertyMap.keySet()) {
	            	String newValue = componentPropertyMap.get(componentKey);
	            	try {
	            		asapiSupport.setComponentProperty(componentId, componentKey, newValue);
	            		changedValues.add(componentId+"#"+componentKey);
	            	}
	            	catch (Exception ex) {
	            		ex.printStackTrace();
	            	}
	            }
            }

        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Couldn't change the component(s) value(s) (" + e.getMessage() + ")";
            response = "error:" + errorMessage;
        }

        try {
        	return ObjectTransformation.objectToJSON(changedValues);
        }
        catch (Exception ex) {
        	return "";
        }
    }

    
    @Path("/runtime/model/components/{componentId}/ports/input/ids")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getComponentInputPortIds(@PathParam("componentId") String componentId) {
        String response;
        String errorMessage = "";

        try {
            componentId = astericsAPIEncoding.decodeString(componentId);

            final IRuntimeModel currentRuntimeModel = DeploymentManager.instance.getCurrentRuntimeModel();
            String[] inputPorts = currentRuntimeModel.getComponentInputPorts(componentId);

            response = ObjectTransformation.objectToJSON(inputPorts);
            if (response.equals("")) {
                response = "{'error':'Could not retrieve the component input port ids'}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Could not retrieve the component input port ids (" + e.getMessage() + ")";
            response = "{'error':'" + errorMessage + "'}";
        }

        return response;
    }

    @Path("/runtime/model/components/{componentId}/ports/output/ids")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getComponentOutputPorts(@PathParam("componentId") String componentId) {
        String response;
        String errorMessage = "";

        try {
            componentId = astericsAPIEncoding.decodeString(componentId);

            final IRuntimeModel currentRuntimeModel = DeploymentManager.instance.getCurrentRuntimeModel();
            String[] inputPorts = currentRuntimeModel.getComponentOutputPorts(componentId);

            response = ObjectTransformation.objectToJSON(inputPorts);
            if (response.equals("")) {
                response = "{'error':'Could not retrieve the component output port ids'}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Could not retrieve the component output port ids (" + e.getMessage() + ")";
            response = "{'error':'" + errorMessage + "'}";
        }

        return response;
    }

    @Path("/runtime/model/components/{componentId}/ports/{portId}/datatype")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getPortDatatype(@PathParam("componentId") String componentId, @PathParam("portId") String portId) {
        String response;
        String errorMessage = "";

        try {
            componentId = astericsAPIEncoding.decodeString(componentId);
            portId = astericsAPIEncoding.decodeString(portId);

            final IRuntimeModel currentRuntimeModel = DeploymentManager.instance.getCurrentRuntimeModel();
            IPort port = currentRuntimeModel.getPort(componentId, portId);
            DataType dataType = port.getPortDataType();

            response = dataType + "";
        } catch (Exception e) {
            errorMessage = "Could not retrieve the component output port datatype (" + e.getMessage() + ")";
            response = "{'error':'" + errorMessage + "'}";
        }

        return response;
    }

    @Path("/runtime/model/components/{componentId}/channels/event/ids")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getComponentEventChannelsIds(@PathParam("componentId") String componentId) {
        String response = "";
        String errorMessage = "";

        try {
            componentId = astericsAPIEncoding.decodeString(componentId);

            final IRuntimeModel currentRuntimeModel = DeploymentManager.instance.getCurrentRuntimeModel();
            List<String> eventChannelIds = new ArrayList<String>();

            Set<IEventChannel> eventChannels = currentRuntimeModel.getEventChannels();
            for (IEventChannel eventChannel : eventChannels) {
                if ((eventChannel.getSources()[0].getComponentInstanceID().equals(componentId))
                        || (eventChannel.getTargets()[0].getComponentInstanceID().equals(componentId))) {
                    eventChannelIds.add(eventChannel.getChannelID());
                }
            }

            response = ObjectTransformation.objectToJSON(eventChannelIds);
            if (response.equals("")) {
                response = "{'error':'Could not retrieve the event channel ids'}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Could not retrieve the event channel ids (" + e.getMessage() + ")";
            response = "{'error':'" + errorMessage + "'}";
        }

        return response;
    }

    @Path("/runtime/model/components/{componentId}/{portId}/channels/data/ids")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getComponentDataChannelsIds(@PathParam("componentId") String componentId,
            @PathParam("portId") String portId) {
        String response = "";
        String errorMessage = "";

        try {
            componentId = astericsAPIEncoding.decodeString(componentId);
            portId = astericsAPIEncoding.decodeString(portId);

            final IRuntimeModel currentRuntimeModel = DeploymentManager.instance.getCurrentRuntimeModel();
            Map<String, List<String>> dataChannelIds = new HashMap<>();

            Set<IChannel> dataChannels = currentRuntimeModel.getChannels();
            for (IChannel dataChannel : dataChannels) {
                String targetComponent = dataChannel.getTarget().getComponentInstanceID();
                String sourceComponent = dataChannel.getSource().getComponentInstanceID();
                String targetPort = dataChannel.getTarget().getPortID();
                String sourcePort = dataChannel.getSource().getPortID();
                if (targetComponent.equals(componentId) && targetPort.equals(portId)) {
                    List<String> sourceComponentAndPort = Arrays.asList(sourceComponent, sourcePort);
                    dataChannelIds.put(dataChannel.getChannelID(), sourceComponentAndPort);
                } else if (sourceComponent.equals(componentId) && sourcePort.equals(portId)) {
                    List<String> targetComponentAndPort = Arrays.asList(targetComponent, targetPort);
                    dataChannelIds.put(dataChannel.getChannelID(), targetComponentAndPort);
                }
            }

            response = ObjectTransformation.objectToJSON(dataChannelIds);
            if (response.equals("")) {
                response = "{'error':'Could not retrieve the data channel ids'}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Could not retrieve the data channel ids (" + e.getMessage() + ")";
            response = "{'error':'" + errorMessage + "'}";
        }

        return response;
    }

    @Path("/runtime/model/channels/event/{channelId}/source")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getEventChannelSource(@PathParam("channelId") String channelId) {
        String response = "";
        String errorMessage = "";

        try {
            channelId = astericsAPIEncoding.decodeString(channelId);

            final IRuntimeModel currentRuntimeModel = DeploymentManager.instance.getCurrentRuntimeModel();
            Map<String, String> sourceInfo = new HashMap<String, String>();

            Set<IEventChannel> eventChannels = currentRuntimeModel.getEventChannels();
            for (IEventChannel eventChannel : eventChannels) {
                if (eventChannel.getChannelID().equals(channelId)) {
                    sourceInfo.put("component", eventChannel.getSources()[0].getComponentInstanceID());
                    sourceInfo.put("eventPort", eventChannel.getSources()[0].getEventPortID());
                }
            }

            response = ObjectTransformation.objectToJSON(sourceInfo);
            if (response.equals("")) {
                response = "{'error':'Could not retrieve the source of the event channel " + channelId + "'}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Could not retrieve the source of the event channel " + channelId + " (" + e.getMessage()
                    + ")";
            response = "{'error':'" + errorMessage + "'}";
        }

        return response;
    }

    @Path("/runtime/model/channels/event/{channelId}/target")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getEventChannelTarget(@PathParam("channelId") String channelId) {
        String response = "";
        String errorMessage = "";

        try {
            channelId = astericsAPIEncoding.decodeString(channelId);

            final IRuntimeModel currentRuntimeModel = DeploymentManager.instance.getCurrentRuntimeModel();
            Map<String, String> sourceInfo = new HashMap<String, String>();

            Set<IEventChannel> eventChannels = currentRuntimeModel.getEventChannels();
            for (IEventChannel eventChannel : eventChannels) {
                if (eventChannel.getChannelID().equals(channelId)) {
                    sourceInfo.put("component", eventChannel.getTargets()[0].getComponentInstanceID());
                    sourceInfo.put("eventPort", eventChannel.getTargets()[0].getEventPortID());
                }
            }

            response = ObjectTransformation.objectToJSON(sourceInfo);
            if (response.equals("")) {
                response = "{'error':'Could not retrieve the target of the event channel " + channelId + "'}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Could not retrieve the target of the event channel " + channelId + " (" + e.getMessage()
                    + ")";
            response = "{'error':'" + errorMessage + "'}";
        }

        return response;
    }

    @Path("/runtime/model/channels/event/ids")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getEventChannelsIds() {
        String response = "";
        String errorMessage = "";

        try {
            final IRuntimeModel currentRuntimeModel = DeploymentManager.instance.getCurrentRuntimeModel();
            List<String> eventChannelIds = new ArrayList<String>();

            Set<IEventChannel> eventChannels = currentRuntimeModel.getEventChannels();
            for (IEventChannel eventChannel : eventChannels) {
                eventChannelIds.add(eventChannel.getChannelID());
            }

            response = ObjectTransformation.objectToJSON(eventChannelIds);
            if (response.equals("")) {
                response = "{'error':'Could not retrieve the event channel ids'}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Could not retrieve the event channel ids (" + e.getMessage() + ")";
            response = "{'error':'" + errorMessage + "'}";
        }

        return response;
    }

    @Path("/runtime/model/channels/data/{channelId}/source")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getDataChannelSource(@PathParam("channelId") String channelId) {
        String response = "";
        String errorMessage = "";

        try {
            channelId = astericsAPIEncoding.decodeString(channelId);

            final IRuntimeModel currentRuntimeModel = DeploymentManager.instance.getCurrentRuntimeModel();
            Map<String, String> sourceInfo = new HashMap<String, String>();

            Set<IChannel> dataChannels = currentRuntimeModel.getChannels();
            for (IChannel dataChannel : dataChannels) {
                if (dataChannel.getChannelID().equals(channelId)) {
                    sourceInfo.put("component", dataChannel.getSource().getComponentInstanceID());
                    sourceInfo.put("eventPort", dataChannel.getSource().getPortID());
                }
            }

            response = ObjectTransformation.objectToJSON(sourceInfo);
            if (response.equals("")) {
                response = "{'error':'Could not retrieve the source of the data channel " + channelId + "'}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Could not retrieve the source of the data channel " + channelId + " (" + e.getMessage()
                    + ")";
            response = "{'error':'" + errorMessage + "'}";
        }

        return response;
    }

    @Path("/runtime/model/channels/data/{channelId}/target")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getDataChannelTarget(@PathParam("channelId") String channelId) {
        String response = "";
        String errorMessage = "";

        try {
            channelId = astericsAPIEncoding.decodeString(channelId);

            final IRuntimeModel currentRuntimeModel = DeploymentManager.instance.getCurrentRuntimeModel();
            Map<String, String> sourceInfo = new HashMap<String, String>();

            Set<IChannel> dataChannels = currentRuntimeModel.getChannels();
            for (IChannel dataChannel : dataChannels) {
                if (dataChannel.getChannelID().equals(channelId)) {
                    sourceInfo.put("component", dataChannel.getTarget().getComponentInstanceID());
                    sourceInfo.put("eventPort", dataChannel.getTarget().getPortID());
                }
            }

            response = ObjectTransformation.objectToJSON(sourceInfo);
            if (response.equals("")) {
                response = "{'error':'Could not retrieve the target of the data channel " + channelId + "'}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Could not retrieve the target of the data channel " + channelId + " (" + e.getMessage()
                    + ")";
            response = "{'error':'" + errorMessage + "'}";
        }

        return response;
    }

    @Path("/runtime/model/channels/data/ids")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getDataChannelsIds() {
        String response = "";
        String errorMessage = "";

        try {
            final IRuntimeModel currentRuntimeModel = DeploymentManager.instance.getCurrentRuntimeModel();
            List<String> dataChannelIds = new ArrayList<String>();

            Set<IChannel> dataChannels = currentRuntimeModel.getChannels();
            for (IChannel dataChannel : dataChannels) {
                dataChannelIds.add(dataChannel.getChannelID());
            }

            response = ObjectTransformation.objectToJSON(dataChannelIds);
            if (response.equals("")) {
                response = "{'error':'Could not retrieve the data channel ids'}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Could not retrieve the data channel ids (" + e.getMessage() + ")";
            response = "{'error':'" + errorMessage + "'}";
        }

        return response;
    }

    /*************************************
     * Storage/ARE-repository resources
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
            response = "<error>" + errorMessage + "</error>";
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

        if ((modelInXML == "") || (modelInXML == null)) {
            errorMessage = "invalid parameters";
            response = "error:" + errorMessage;
        }

        try {
            decodedFilepath = astericsAPIEncoding.decodeString(filepath);

            asapiSupport.storeModel(modelInXML, decodedFilepath);
            response = "Model stored";
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Could not store the model" + decodedFilepath + "' (" + e.getMessage() + ")";
            response = "error:" + errorMessage;
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
            } else {
                response = "Could not delete the model (Please check if the given filepath is correct)";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Could not delete the model " + decodedFilepath + "' (" + e.getMessage() + ")";
            response = "error:" + errorMessage;
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

            response = ObjectTransformation.objectToJSON(array);
            if (response.equals("")) {
                response = "{'error':'Could not retrieve the stored models (Object serialization failure)'}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Could not retrieve the stored models" + " (" + e.getMessage() + ")";
            response = "{'error':'" + errorMessage + "'}";
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
                response = "{'error':'" + errorMessage + "'}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Couldn't retrieve the components collection" + "' (" + e.getMessage() + ")";
            response = "{'error':'" + errorMessage + "'}";
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
                response = "{'error':'Could not retrieve the components descriptors (Object serialization failure)'}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Could not retrieve the components descriptors (" + e.getMessage() + ")";
            response = "{'error':'" + errorMessage + "'}";
        }

        return response;
    }

    @Path("/runtime/model/components/input/{componentId}/{inputKey}")
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String sendDataToInputPort(String value, @PathParam("componentId") String componentId,
            @PathParam("inputKey") String componentKey) {
        String response;
        String errorMessage = "";
        String decodedId = "", decodedKey = "";

        try {
            decodedId = astericsAPIEncoding.decodeString(componentId);
            decodedKey = astericsAPIEncoding.decodeString(componentKey);
            logger.info(MessageFormat.format("sending data <{0}> to {1}-{2}", value, decodedId, decodedKey));
            asapiSupport.sendData(decodedId, decodedKey, value.getBytes());
            response = "success";
        } catch (Exception e) {
            logger.log(Level.WARNING, "could not send data!", e);
            errorMessage = "Couldn't set '" + value + "' value to '" + decodedKey + "' from '" + decodedId + "' ("
                    + e.getMessage() + ")";
            response = "error:" + errorMessage;
        }

        return response;
    }
}
