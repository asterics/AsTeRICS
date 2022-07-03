package eu.asterics.mw.are;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;

import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import org.osgi.framework.BundleException;

import eu.asterics.mw.are.asapi.StatusObject;
import eu.asterics.mw.are.exceptions.AREAsapiException;
import eu.asterics.mw.are.exceptions.BundleManagementException;
import eu.asterics.mw.are.exceptions.ParseException;
import eu.asterics.mw.are.parsers.DefaultDeploymentModelParser;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.DataType;
import eu.asterics.mw.model.bundle.IComponentType;
import eu.asterics.mw.model.deployment.IChannel;
import eu.asterics.mw.model.deployment.IComponentInstance;
import eu.asterics.mw.model.deployment.IInputPort;
import eu.asterics.mw.model.deployment.IOutputPort;
import eu.asterics.mw.model.deployment.IRuntimeModel;
import eu.asterics.mw.model.deployment.impl.DefaultChannel;
import eu.asterics.mw.model.deployment.impl.DefaultComponentInstance;
import eu.asterics.mw.model.deployment.impl.ModelState;
import eu.asterics.mw.model.runtime.IRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsModelExecutionThreadPool;
import eu.asterics.mw.services.ResourceRegistry;
import eu.asterics.mw.services.ResourceRegistry.RES_TYPE;

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
 *     This project has been partly funded by the European Commission,
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */

/**
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 * @author Costas Kakousis [kakousis@cs.ucy.ac.cy]
 * 
 *         This class implements the actual functionality of the AsapiServer Interface methods. The methods of this class are called by the corresponding
 *         methods in the AsapiSupport class. Date: Aug 25, 2010 Time: 11:35:35 AM
 */
public class AsapiSupport {
    private final ComponentRepository componentRepository = ComponentRepository.instance;

    private Logger logger = null;
    private DocumentBuilder builder;

    public static final String DEFAULT_MODEL_URL = "/default_model.xml";
    public static final String AUTO_START_MODEL = "autostart.acs";

    public AsapiSupport() {
        logger = AstericsErrorHandling.instance.getLogger();
    }

    /**
     * Returns an array containing all the available (i.e., installed) component types. These are encoded as strings, representing the absolute class name (in
     * Java) of the corresponding implementation.
     *
     * @return an array containing all available component types
     * @throws AREAsapiException
     */
    public String[] getAvailableComponentTypes() throws AREAsapiException {
        // MAD: It seems that this method is not used by the ACS and is actually redundant to getBundleDescriptors
        throw new AREAsapiException("Method not implemented: getAvailableComponentTypes()");
        /*
         * try { return AstericsModelExecutionThreadPool.instance .execAndWaitOnModelExecutorLifecycleThread(new Callable<String[]>() {
         * 
         * @Override public String[] call() throws Exception {
         * 
         * // The method name indicates available (all // components for the platform) but // componentRepository.getInstalledComponentTypes() // returns the
         * currently installed ones, maybe // should remove this method. final Set<IComponentType> componentTypeSet = componentRepository
         * .getInstalledComponentTypes();
         * 
         * if (componentTypeSet.size() == 0) { logger.fine(this.getClass().getName() + ".getAvailableComponentTypes:" + " No installed component types found!");
         * } final String[] componentTypes = new String[componentTypeSet.size()];
         * 
         * int counter = 0; for (final IComponentType componentType : componentTypeSet) { componentTypes[counter++] = componentType.getID(); }
         * 
         * System.out.println("\n\nin getAvailableComponentTypes: \n\n"); return componentTypes; } });
         * 
         * } catch (Exception e) { logger.severe("Error in fetching installed componentType of ComponentRepository: " + e.getMessage()); } return new String[0];
         */
    }

    /**
     * Returns an xml String containing the component collection (bundle descriptors of every AsTeRiCS component). This function reads the file
     * {@link BundleManager#COMPONENT_COLLECTION_CACHE_FILE_URI} and returns its contents.
     *
     * @return an xml string containing all the bundle descriptors (some parts of the descriptor are removed) and null if an error has occurred.
     * @throws AREAsapiException
     */
    public String getComponentDescriptorsAsXml() throws AREAsapiException {
        try {
            return AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Callable<String>() {

                @Override
                public String call() throws Exception {
                    logger.fine("getComponentDescriptorsAsXml()");
                    String response = "";
                    try {
                        response = ResourceRegistry.getInstance().getResourceContentAsString(BundleManager.COMPONENT_COLLECTION_CACHE_FILE_URI.toURL().openStream());
                    } catch (IOException e) {
                        logger.severe("Error reading cached component collection from file <" + BundleManager.COMPONENT_COLLECTION_CACHE_FILE_URI
                                + ">, reason: " + e.getMessage());
                        // We must rethrow the exception, because we could not read the file.
                        // @todo think about falling back to providing file without cache.
                        throw new AREAsapiException(e.getMessage());
                    }
                    return response;
                }
            });
        } catch (Exception e) {
            logger.severe("Error in fetching bundle descriptors: " + e.getMessage());
            throw new AREAsapiException(e.getMessage());
        }
    }

    /**
     * Returns a string encoding the currently deployed model in XML. If there is no model deployed, then an empty one is returned.
     *
     * @return a string encoding the currently deployed model in XML or an empty string if there is no model deployed
     * @throws AREAsapiException
     */
    public String getModel() throws AREAsapiException {
        try {
            return AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return DeploymentManager.instance.getCurrentRuntimeModelAsXMLString();
                }
            });
        } catch (Exception e) {
            logger.logp(Level.SEVERE, this.getClass().getCanonicalName(), "getModel()", e.getMessage(), e);
            throw (new AREAsapiException(e.getMessage()));
        }
    }

    /**
     * Returns the name of the currently deployed model. This is the attribute "modelName" from the XML, containing the full path at creation time, filename and
     * creation timestamp. Therefore it is an ID of the model.
     *
     * @return the name (ID) of the currently deployed model or an empty string, if no model is deployed
     */
    public String getCurrentModelName() {
        IRuntimeModel currentRuntimeModel = DeploymentManager.instance.getCurrentRuntimeModel();
        if (currentRuntimeModel == null) {
            return "";
        }
        return currentRuntimeModel.getModelName();
    }

    /**
     * Returns a string encoding of the model defined in the filename given as argument. If there is no model, an empty string is returned.
     *
     * @param filename
     *            the name of the file to be checked
     * @return a string encoding of the model defined in the filename
     * @throws AREAsapiException
     *             if could not get model from file
     */
    public String getModelFromFile(String filename) throws AREAsapiException {
        try {
            return ResourceRegistry.getInstance().getResourceContentAsString(filename, RES_TYPE.MODEL);
        } catch (URISyntaxException | IOException e) {
            logger.logp(Level.SEVERE, this.getClass().getCanonicalName(), "getModelFromFile(String filename)", e.getMessage(), e);
            throw new AREAsapiException(e.getMessage());
        }
    }

    /**
     * Returns the state of the current runtime model.
     *
     * @return - The state of the runtime model. See {@link ModelState} class for the available states.
     */
    public String getModelState() {
        try {
            ModelState modelState = DeploymentManager.instance.getCurrentRuntimeModel().getState();
            return modelState.toString();
        } catch (NullPointerException e) {
            return ModelState.STOPPED.toString();
        }
    }

    /**
     * Deploys the model encoded in the specified string into the ARE. An exception is thrown if the specified string is either not well-defined XML, or not
     * well defined ASAPI model encoding, or if a validation error occurred after reading the model.
     *
     * @param modelInXML
     *            a string representation in XML of the model to be deployed
     * @throws AREAsapiException
     *             if the specified string is either not well-defined XML, or not well defined ASAPI model encoding, or if a validation error occurred after
     *             reading the model
     */
    public void deployModel(final String modelInXML) throws AREAsapiException {
        try {
            AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    // Stop running model first if there is one
                    if (DeploymentManager.instance.getCurrentRuntimeModel() != null) {
                        logger.fine("Before Deploying model, trying to stop old before.");
                        stopModel();
                        DeploymentManager.instance.undeployModel();
                    }

                    AREServices.instance.deployModelInternal(modelInXML);
                    return null;
                }
            });
        } catch (ParseException e4) {
            DeploymentManager.instance.undeployModel();
            DeploymentManager.instance.setStatus(AREStatus.FATAL_ERROR);
            AstericsErrorHandling.instance.setStatusObject(AREStatus.FATAL_ERROR.toString(), "", "Deployment Error");
            logger.logp(Level.SEVERE, this.getClass().getCanonicalName(), "deployModel(String)", e4.getMessage(), e4);
            throw (new AREAsapiException(
                    "Model could not be parsed or is not compatible with installed components.\nTry to convert the model file by opening and resaving it with the AsTeRICS Configuration Suite (ACS)"));
        } catch (Throwable t) {
            DeploymentManager.instance.undeployModel();
            DeploymentManager.instance.setStatus(AREStatus.FATAL_ERROR);
            AstericsErrorHandling.instance.setStatusObject(AREStatus.FATAL_ERROR.toString(), "", "Deployment Error");
            logger.logp(Level.SEVERE, this.getClass().getCanonicalName(), "deployModel(String)", t.getMessage(), t);
            throw (new AREAsapiException("Model could not be deployed."));
        }

    }

    /**
     * Retrieves the descriptors of AsTeRiCS bundles as a Java List. This function is used by the WebACS when 'Download Component Collection' is clicked.
     * Internally the contents of {{@link #getComponentDescriptorsAsXml()} is added as first element of the List.
     *
     * @return A {@link List} of {@link String} which contains all the bundle descriptors.
     *
     * @throws AREAsapiException
     */
    public List<String> getBundleDescriptors() throws AREAsapiException {
        try {
            return AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Callable<List<String>>() {

                @Override
                public List<String> call() throws Exception {
                    logger.fine("getBundelDescriptors()");
                    List<String> res = new ArrayList<String>();
                    res.add(getComponentDescriptorsAsXml());
                    return res;
                }
            });
        } catch (Exception e) {
            logger.logp(Level.SEVERE, this.getClass().getCanonicalName(), "getBundelDescriptors()", e.getMessage(), e);
            throw new AREAsapiException(e.getMessage());
        }
    }

    /**
     * Deploys a new empty model into the ARE. In essence, this is equivalent to creating an empty model and deploying it using {@link #deployModel(String)}.
     * This results to freeing all resources in the ARE (i.e., if a previous model reserved any).
     *
     * @throws AREAsapiException
     */
    public void newModel() throws AREAsapiException {
        throw new AREAsapiException("The ASAPI function is not supported: newModel()");
    }

    /**
     * It starts or resumes the execution of the model.
     *
     * @throws AREAsapiException
     *             if an exception occurs while validating and starting the deployed model.
     */
    public void runModel() throws AREAsapiException {
        AREServices.instance.runModel();
    }

    /**
     * Briefly stops the execution of the model. Its main difference from the {@link #stopModel()} method is that it does not reset the components (e.g., the
     * buffers are not cleared).
     *
     * @throws AREAsapiException
     *             if the deployed model is not started already, or if the execution cannot be paused
     */
    public void pauseModel() throws AREAsapiException {
        AREServices.instance.pauseModel();
    }

    /**
     * Stops the execution of the model. Unlike the {@link #pauseModel()} method, this one resets the components, which means that when the model is started
     * again it starts from scratch (i.e., with a new state).
     *
     * @throws AREAsapiException
     *             if the deployed model is not started already, or if the execution cannot be stopped
     */
    public void stopModel() throws AREAsapiException {
        // Delegate to AREServices
        AREServices.instance.stopModel();
    }

    /**
     * Returns an array that includes all existing component instances in the model (even multiple instances of the same component type).
     *
     * @return an array of all the IDs of the existing component instances
     */
    public String[] getComponents() {
        String[] components = DeploymentManager.instance.getCurrentRuntimeModel().getComponentInstancesIDs();
        if (components != null) {
            logger.fine(this.getClass().getName() + ".getComponents: OK\n");
            return components;
        } else {
            logger.warning(this.getClass().getName() + ".getComponents: Failed\n");
            return null;
        }

    }

    /**
     * Returns an array containing the IDs of all the channels that include the specified component instance either as a source or target.
     *
     * @param componentID
     *            the ID of the specified component instance
     * @return an array containing the IDs of all the channels which include the specified component instance
     */
    public String[] getChannels(final String componentID) {
        String[] channels = DeploymentManager.instance.getCurrentRuntimeModel().getChannelsIDs(componentID);
        if (channels != null) {
            logger.fine(this.getClass().getName() + ".getChannels: OK \n");
            return channels;
        } else {
            logger.warning(this.getClass().getName() + ".getChannels: Failed \n");
            return null;
        }
    }

    /**
     * Used to create a new instance of the specified component type, with the assigned ID. Throws an exception if the specified component type is not
     * available, or if the specified ID is already defined.
     *
     * @param componentID
     *            the unique ID to be assigned to the new component instance
     * @param componentType
     *            describes the component type of the component to be instantiated
     * @throws AREAsapiException
     *             if the specified component type is not available, or if the specified ID is already defined
     */
    public void insertComponent(final String componentID, final String componentType) throws AREAsapiException {
        // Should also be called with AstericsModelExecutorThreadPool

        Set<IComponentType> availableComponentTypes = componentRepository.getInstalledComponentTypes();
        boolean isAvailable = false;

        for (IComponentType ct : availableComponentTypes) {
            if (ct.getType().equals(componentType)) {
                isAvailable = true;
                break;
            }

        }
        if (isAvailable) {
            boolean alreadyDefined = false;
            Set<IComponentInstance> componentInstances = DeploymentManager.instance.getCurrentRuntimeModel().getComponentInstances();

            for (IComponentInstance ci : componentInstances) {
                if (ci.getInstanceID().equals(componentID)) {
                    alreadyDefined = true;
                    break;
                }

            }
            if (alreadyDefined) {
                throw new AREAsapiException("Already defined component ID: " + componentID);
            }
            // TODO All OK, insert component
            DefaultComponentInstance newInstance = new DefaultComponentInstance(componentID, componentType, "", new LinkedHashSet<IInputPort>(),
                    new LinkedHashSet<IOutputPort>(), new LinkedHashMap<String, Object>(), new Point(0, 0),
                    // false,
                    null, new LinkedHashSet<IInputPort>());
            DeploymentManager.instance.getCurrentRuntimeModel().insertComponent(newInstance);
        } else {
            logger.warning(this.getClass().getName() + ".insertComponent: " + "not available component type -> " + componentType + " \n");
            throw new AREAsapiException("Not available component type: " + componentType);
        }
    }

    /**
     * Used to delete the instance of the component that is specified by the given ID. Throws an exception if the specified component ID is not defined.
     *
     * @param componentID
     *            the ID of the component to be removed
     * @throws AREAsapiException
     *             if the specified component ID is not defined
     */
    public void removeComponent(final String componentID) throws AREAsapiException {
        // Should also be called with AstericsModelExecutorThreadPool

        IComponentInstance componentInstance = DeploymentManager.instance.getCurrentRuntimeModel().getComponentInstance(componentID);

        if (componentInstance == null) {
            logger.warning(this.getClass().getName() + ".removeComponent: " + "component " + componentID + " missing \n");
            throw new AREAsapiException("Component " + componentID + "missing");
        }

        try {
            DeploymentManager.instance.removeComponent(componentID);
        } catch (BundleManagementException e1) {
            logger.warning(this.getClass().getName() + ".removeComponent: " + "Failed -> \n" + e1.getMessage() + " \n");
            throw new AREAsapiException(e1.getMessage());
        } catch (BundleException e2) {
            logger.warning(this.getClass().getName() + ".removeComponent: " + "Failed -> \n" + e2.getMessage() + " \n");
            throw new AREAsapiException(e2.getMessage());
        }
        DeploymentManager.instance.getCurrentRuntimeModel().removeComponentInstance(componentID);
    }

    /**
     * Returns an array containing the IDs of all the ports (i.e., includes both input and output ones) of the specified component instance. An exception is
     * thrown if the specified component instance is not defined.
     *
     * @param componentID
     *            the ID of the specified component instance
     * @return an array (non empty) containing the IDs of all the ports of the specified component instance
     * @throws AREAsapiException
     *             if the specified component instance is not defined
     */
    public String[] getAllPorts(final String componentID) throws AREAsapiException {
        String[] ports = DeploymentManager.instance.getCurrentRuntimeModel().getComponentPorts(componentID);

        if (ports != null) {
            logger.fine(this.getClass().getName() + ".getAllPorts: OK \n");
            return ports;
        } else {
            logger.warning(this.getClass().getName() + ".getAllPorts: Failed \n");
            return null;
        }
    }

    /**
     * Returns an array containing the IDs of all the input ports of the specified component instance. An exception is thrown if the specified component
     * instance is not defined.
     *
     * @param componentID
     *            the ID of the specified component instance
     * @return an array (possibly empty) containing the IDs of all the input ports of the specified component instance
     * @throws AREAsapiException
     *             if the specified component instance is not defined
     */
    public String[] getInputPorts(final String componentID) throws AREAsapiException {
        String[] inputPorts = DeploymentManager.instance.getCurrentRuntimeModel().getComponentInputPorts(componentID);

        if (inputPorts != null) {
            logger.fine(this.getClass().getName() + ".getInputPorts: OK \n");
            return inputPorts;
        } else {
            logger.warning(this.getClass().getName() + ".getInputPorts: Failed \n");
            return null;
        }
    }

    /**
     * Returns an array containing the IDs of all the output ports of the specified component instance. An exception is thrown if the specified component
     * instance is not defined.
     *
     * @param componentID
     *            the ID of the specified component instance
     * @return an array (possibly empty) containing the IDs of all the output ports of the specified component instance
     * @throws AREAsapiException
     *             if the specified component instance is not defined
     */
    public String[] getOutputPorts(final String componentID) throws AREAsapiException {
        String[] outPorts = DeploymentManager.instance.getCurrentRuntimeModel().getComponentOutputPorts(componentID);

        if (outPorts != null) {
            logger.fine(this.getClass().getName() + ".getOutputPorts: OK\n");
            return outPorts;
        } else {
            logger.warning(this.getClass().getName() + ".getOutputPorts: Failed\n");
            return null;
        }
    }

    /**
     * Creates a channel between the specified source and target components and ports. Throws an exception if the specified ID is already defined, or the
     * specified component or port IDs is not found, or if the data types of the ports do not match. Also, an exception is thrown if there is already a channel
     * connected to the specified input port (only one channel is allowed per input port).
     *
     * @param channelID
     *            the ID to be assigned to the formed channel
     * @param sourceComponentID
     *            the ID of the source component
     * @param sourcePortID
     *            the ID of the source port
     * @param targetComponentID
     *            the ID of the target component
     * @param targetPortID
     *            the ID of the target port
     * @throws AREAsapiException
     *             if either of the specified component or port IDs is not found, or if the data types of the ports do not match, or if there is already a
     *             channel connected to the specified input port
     */
    public void insertChannel(final String channelID, final String sourceComponentID, final String sourcePortID, final String targetComponentID,
            final String targetPortID) throws AREAsapiException {
        // Should also be called with AstericsModelExecutorThreadPool

        IRuntimeModel model = DeploymentManager.instance.getCurrentRuntimeModel();

        if (model.getComponentInstance(sourceComponentID) == null) {
            logger.warning(this.getClass().getName() + ".insertChannel: " + "Undefined source component ID " + sourceComponentID + "\n");
            throw new AREAsapiException("Undefined source component ID: " + sourceComponentID);
        }

        if (model.getComponentInstance(targetComponentID) == null) {
            logger.warning(this.getClass().getName() + ".insertChannel: " + "Undefined target component ID " + targetComponentID + "\n");
            throw new AREAsapiException("Undefined target component ID: " + targetComponentID);
        }

        Set<IChannel> channels = model.getChannels();
        for (IChannel ch : channels) {

            if (ch.getChannelID().equals(channelID)) {
                logger.warning(this.getClass().getName() + ".insertChannel: " + "Channel " + channelID + " already defined\n");
                throw new AREAsapiException("Channel " + channelID + " already defined");
            }

            // check if there is already a channel connected to the
            // specified input port (i.e., there is already a channel of which
            // the sourcecomponentinstanceid and sourceportid are the same
            if (ch.getSourceComponentInstanceID().equals(sourceComponentID) && model.getPort(sourceComponentID, sourcePortID) != null) {
                logger.warning(
                        this.getClass().getName() + ".insertChannel: " + "Input port already connected to a channel with ID " + ch.getChannelID() + "\n");
                throw new AREAsapiException("Input port already connected to a channel with ID: " + ch.getChannelID());
            }

        }

        if (!isOfTheSameType(sourceComponentID, sourcePortID, targetComponentID, targetPortID)) {
            logger.warning(this.getClass().getName() + ".insertChannel: " + "Icompatible port data types between port " + sourcePortID + " and " + targetPortID
                    + "\n");
            throw new AREAsapiException("Icompatible port data types between port " + sourcePortID + " and " + targetPortID);
        }

        DefaultChannel newChannel = new DefaultChannel("", sourceComponentID, sourcePortID, targetComponentID, targetPortID, channelID,
                new LinkedHashMap<String, Object>());

        model.insertChannel(newChannel);

    }

    private boolean isOfTheSameType(String sourceComponentID, String sourcePortID, String targetComponentID, String targetPortID) {

        String srcPortID, trgPortID, srcType, trgType;

        IRuntimeModel model = DeploymentManager.instance.getCurrentRuntimeModel();
        String sourceComponentTypeID = model.getComponentInstance(sourceComponentID).getComponentTypeID();
        String targetComponentTypeID = model.getComponentInstance(targetComponentID).getComponentTypeID();
        Set<IOutputPort> outPorts = model.getComponentInstance(sourceComponentID).getOutputPorts();
        Set<IInputPort> inPorts = model.getComponentInstance(targetComponentID).getInputPorts();
        for (IOutputPort op : outPorts) {

            srcPortID = op.getPortType();
            if (srcPortID.equals(sourcePortID)) {
                for (IInputPort ip : inPorts) {
                    trgPortID = ip.getPortType();
                    if (trgPortID.equals(targetPortID)) {
                        srcType = this.componentRepository.getPortDataType(sourceComponentTypeID, sourcePortID).toString();
                        trgType = this.componentRepository.getPortDataType(targetComponentTypeID, targetPortID).toString();

                        if (srcType != null && trgType != null && srcType.equals(trgType)) {
                            logger.fine(this.getClass().getName() + ".isOfTheSameType: OK\n");
                            return true;
                        } else {
                            logger.warning(this.getClass().getName() + ".isOfTheSameType: Failed\n");
                            return false;
                        }
                    }

                }
            }
        }
        return false;
    }

    /**
     * Removes an existing channel between the specified source and target components and ports. Throws an exception if the specified channel is not found.
     *
     * @param channelID
     *            the ID of the channel to be removed
     * @throws AREAsapiException
     *             if the specified channel ID is not found
     */
    public void removeChannel(final String channelID) throws AREAsapiException {
        // Should also be called with AstericsModelExecutorThreadPool

        DeploymentManager.instance.getCurrentRuntimeModel().removeChannel(channelID);
    }

    /**
     * Reads the IDs of all properties set for the specified component.
     *
     * @param componentID
     *            the ID of the component to be checked
     * @return an array (possibly empty) with all the property keys for the specified component
     * @throws AREAsapiException
     *             if the specified component is not found
     */
    public String[] getComponentPropertyKeys(final String componentID) throws AREAsapiException {
        String[] result = DeploymentManager.instance.getCurrentRuntimeModel().getComponentPropertyKeys(componentID);
        if (result == null) {
            logger.warning(this.getClass().getName() + "." + "getComponentPropertyKeys: " + "Undefined component " + componentID + "\n");
            throw new AREAsapiException("Undefined component ID: " + componentID);
        } else {
            logger.fine(this.getClass().getName() + "." + "getComponentPropertyKeys: OK\n");
            return result;
        }
    }

    /**
     * Returns the value of the property with the specified key in the component with the specified ID as a string.
     *
     * @param componentID
     *            the ID of the component to be checked
     * @param key
     *            the key of the property to be retrieved
     * @return the value of the property with the specified key in the component with the specified ID as a string
     * @throws AREAsapiException
     *             if the specified component is not found
     */
    public String getComponentProperty(final String componentID, final String key) throws AREAsapiException {
        String result;
        try {
            result = DeploymentManager.instance.getComponentProperty(componentID, key);
        } catch (BundleManagementException e) {
            result = null;
            e.printStackTrace();
        }

        if (result == null) {
            logger.warning(this.getClass().getName() + "." + "getComponentProperty: Undefined component " + componentID + "\n");
            throw new AREAsapiException("Undefined component ID: " + componentID);
        } else {
            logger.fine(this.getClass().getName() + "." + "getComponentProperty: OK\n");
            return result;
        }
    }

    /**
     * Sets the property with the specified key in the component with the specified ID with the given string representation of the value.
     *
     * @param componentID
     *            the ID of the component to be checked
     * @param key
     *            the key of the property to be set
     * @param value
     *            the string-representation of the value to be set to the specified key
     * @return the previous value of the property with the specified key in the component with the specified ID as a string, or an empty string if the property
     *         was not previously set
     * @throws AREAsapiException
     *             if the specified component is not found
     */
    public String setComponentProperty(final String componentID, final String key, final String value) throws AREAsapiException {
        try {
            return AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Callable<String>() {

                @Override
                public String call() throws Exception {

                    String result = DeploymentManager.instance.getCurrentRuntimeModel().setComponentProperty(componentID, key, value);
                    DeploymentManager.instance.setComponentProperty(componentID, key, value);
                    if (result == null) {
                        logger.warning(this.getClass().getName() + "." + "setComponentProperty: Undefined component " + componentID + "\n");
                        throw new AREAsapiException("Undefined component ID: " + componentID);
                    } else {
                        logger.fine(this.getClass().getName() + "." + "setComponentProperty: OK\n");
                        return result;
                    }

                }
            });
        } catch (Exception e) {
            throw (new AREAsapiException(e.getMessage()));
        }
    }

    /**
     * Reads the IDs of all properties set for the specified port.
     *
     * @param componentID
     *            the ID of the port's component
     * @param portID
     *            the ID of the port to be checked
     * @return an array (possibly empty) with all the property keys for the specified port, or null if the specified port is not found
     * @throws AREAsapiException
     */
    public String[] getPortPropertyKeys(final String componentID, final String portID) throws AREAsapiException {
        String[] result = DeploymentManager.instance.getCurrentRuntimeModel().getPortPropertyKeys(componentID, portID);
        if (result == null) {
            logger.warning(this.getClass().getName() + "." + "getPortPropertyKeys: Undefined component or port " + componentID + ", " + portID + "\n");
            throw new AREAsapiException("Undefined component or port ID: " + componentID + ", " + portID);
        } else {
            logger.fine(this.getClass().getName() + ".getPortPropertyKeys: OK\n");
            return result;
        }
    }

    /**
     * Returns the value of the property with the specified key of the port with the specified ID in the component with the specified ID as a string.
     *
     * @param componentID
     *            the ID of the component to be checked
     * @param portID
     *            the ID of the port to be checked
     * @param key
     *            the key of the property to be retrieved
     * @return the value of the property with the specified key in the component and port with the specified IDs as a string
     * @throws AREAsapiException
     *             if the specified component or port are not found
     */
    public String getPortProperty(final String componentID, final String portID, final String key) throws AREAsapiException {
        String result = DeploymentManager.instance.getCurrentRuntimeModel().getPortProperty(componentID, portID, key);
        if (result == null) {
            logger.warning(this.getClass().getName() + "." + "getPortProperty: Undefined component or port " + componentID + ", " + portID + "\n");
            throw new AREAsapiException("Undefined component or port ID: " + componentID + ", " + portID);
        } else {
            logger.fine(this.getClass().getName() + ".getPortProperty: OK \n");
            return result;
        }
    }

    /**
     * Sets the property with the specified key in the port with the specified ID with the given string representation of the value.
     *
     * @param componentID
     *            the ID of the component to be checked
     * @param portID
     *            the ID of the port to be checked
     * @param key
     *            the key of the property to be set
     * @param value
     *            the string-representation of the value to be set to the specified key
     * @return the previous value of the property with the specified key in the component and port with the specified IDs, as a string, or an empty string if
     *         the property was not previously set
     * @throws AREAsapiException
     *             if the specified component or port are not found
     */
    public String setPortProperty(final String componentID, final String portID, final String key, final String value) throws AREAsapiException {
        try {
            return AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Callable<String>() {

                @Override
                public String call() throws Exception {

                    String result = DeploymentManager.instance.getCurrentRuntimeModel().setPortProperty(componentID, portID, key, value);
                    if (result == null) {
                        logger.warning(this.getClass().getName() + "." + "setPortProperty: Undefined component or port " + componentID + ", " + portID + "\n");
                        throw new AREAsapiException("Undefined component or port ID: " + componentID + ", " + portID);
                    } else {
                        logger.fine(this.getClass().getName() + "." + "setPortProperty: OK\n");
                        return result;
                    }

                }
            });
        } catch (Exception e) {
            throw (new AREAsapiException(e.getMessage()));
        }
    }

    /**
     * Reads the IDs of all properties set for the specified component.
     *
     * Reads the IDs of all properties set for the specified channel.
     *
     * @param channelID
     *            the ID of the channel to be checked
     * @return an array (possibly empty) with all the property keys for the specified channel
     * @throws AREAsapiException
     *             if the specified channel is not found
     */
    public String[] getChannelPropertyKeys(final String channelID) throws AREAsapiException {
        String[] result = DeploymentManager.instance.getCurrentRuntimeModel().getChannelPropertyKeys(channelID);
        if (result == null) {
            logger.warning(this.getClass().getName() + "." + "getChannelPropertyKeys: Undefined channel " + channelID + "\n");
            throw new AREAsapiException("Undefined channel ID: " + channelID);
        } else {
            logger.fine(this.getClass().getName() + "." + "getChannelPropertyKeys: OK\n");
            return result;
        }
    }

    /**
     * Returns the value of the property with the specified key in the channel with the specified ID as a string.
     *
     * @param channelID
     *            the ID of the channel to be checked
     * @param key
     *            the key of the property to be retrieved
     * @return the value of the property with the specified key in the channel with the specified ID as a string, or null if the specified channel is not found
     * @throws AREAsapiException
     */
    public String getChannelProperty(final String channelID, final String key) throws AREAsapiException {
        String result = DeploymentManager.instance.getCurrentRuntimeModel().getChannelProperty(channelID, key);
        if (result == null) {
            logger.warning(this.getClass().getName() + "." + "getChannelProperty: Undefined channel " + channelID + "\n");
            throw new AREAsapiException("Undefined channel ID: " + channelID);
        } else {
            logger.fine(this.getClass().getName() + ".getChannelProperty: OK\n");
            return result;
        }
    }

    /**
     * Sets the property with the specified key in the channel with the specified ID with the given string representation of the value.
     *
     * @param channelID
     *            the ID of the channel to be checked
     * @param key
     *            the key of the property to be set
     * @param value
     *            the string-representation of the value to be set to the specified key
     * @return the previous value of the property with the specified key in the channel with the specified ID as a string, or an empty string if the property
     *         was not previously set
     * @throws AREAsapiException
     *             if the specified channel is not found
     */
    public String setChannelProperty(final String channelID, final String key, final String value) throws AREAsapiException {
        try {
            return AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Callable<String>() {

                @Override
                public String call() throws Exception {

                    String result = DeploymentManager.instance.getCurrentRuntimeModel().setChannelProperty(channelID, key, value);
                    if (result == null) {
                        logger.warning(this.getClass().getName() + "." + "setChannelProperty: Undefined channel " + channelID + "\n");
                        throw new AREAsapiException("Undefined channel ID: " + channelID);
                    } else {
                        logger.fine(this.getClass().getName() + ".setChannelProperty: OK\n");
                        return result;
                    }
                }
            });
        } catch (Exception e) {
            throw (new AREAsapiException(e.getMessage()));
        }
    }

    /**
     * Registers a remote consumer to the data produced by the specified source component and the corresponding output port. In the background, the ARE forms a
     * proxy component that is connected to the specified component and port, which is utilized to communicate the data to the corresponding remote consumer.
     * This is similar to the proxy-based approach used in Java RMI (see <a href="http://java.sun.com/developer/technicalArticles/RMI/rmi"> http:/
     * /java.sun.com/developer/technicalArticles/RMI/rmi</a> and
     * <a href= "http://today.java.net/article/2004/05/28/rmi-dynamic-proxies-and-evolution-deployment">
     * http://today.java.net/article/2004/05/28/rmi-dynamic-proxies-and- evolution-deployment </a>).
     *
     * @param sourceComponentID
     *            the ID of the source component instance
     * @param sourceOutputPortID
     *            the ID of the source output port from where data will be communicated
     * @return remote consumer ID - a unique ID used to select the data received for this link
     * @throws AREAsapiException
     *             if the specified component ID or port ID are not defined
     */
    public String registerRemoteConsumer(final String sourceComponentID, final String sourceOutputPortID) throws AREAsapiException {
        // TODO
        return null;
        // return
        // DeploymentManager.instance.registerRemoteConsumer(sourceComponentID,sourceOutputPortID);

    }

    /**
     * Unregisters the remote consumer channel with the specified ID.
     *
     * @param remoteConsumerID
     *            the ID of the channel to be unregistered
     * @throws AREAsapiException
     *             if the specified channel ID cannot be found
     */
    public void unregisterRemoteConsumer(final String remoteConsumerID) throws AREAsapiException {
        // todo
    }

    /**
     * Registers a remote producer to provide data to the specified target component and the corresponding input port. In the background, the ARE forms a proxy
     * component that is connected to the specified component and port, which is utilized to receive the data from the corresponding remote producer.
     *
     * @param targetComponentID
     *            the ID of the target component instance
     * @param targetInputPortID
     *            the ID of the target input port where data will be communicated to
     * @return remote producer ID - a unique ID used to mark the data sent
     * @throws AREAsapiException
     *             if the specified component ID or port ID are not found, or if the input port already has an assigned channel
     * @see #registerRemoteConsumer(String, String)
     */
    public String registerRemoteProducer(final String targetComponentID, final String targetInputPortID) throws AREAsapiException {
        // todo
        return null;
    }

    /**
     * Unregisters the remote producer channel with the specified ID.
     *
     * @param remoteProducerID
     *            the ID of the channel to be unregistered
     * @throws AREAsapiException
     *             if the specified channel ID cannot be found
     */
    public void unregisterRemoteProducer(final String remoteProducerID) throws AREAsapiException {
        // todo
    }

    /**
     * This method is used to poll (i.e., retrieve) data from the specified source component and its corresponding output port. Just one tuple of data is
     * returned. The actual amount of data (i.e., in bytes) depends on the type of the port (it is the responsibility of the developer to appropriately deal
     * with the byte array size).
     *
     * @param sourceComponentID
     *            the ID of the source component
     * @param sourceOutputPortID
     *            the ID of the corresponding output port
     * @return an array of bytes that includes the requested tuple of data (can be null if no data were produced)
     * @throws AREAsapiException
     *             if the specified component ID or port ID are not available
     */
    public byte[] pollData(final String sourceComponentID, final String sourceOutputPortID) throws AREAsapiException {
        /*
         * byte[] result = this.DeploymentManager.instance.getCurrentRuntimeModel(). pollData(sourceComponentID, sourceOutputPortID); if (result == null) throw
         * new AsapiException ("Undefined component or port ID: " +sourceComponentID+", "+sourceOutputPortID); else return result;
         */
        return null;
    }

    /**
     * This method is used to pull (i.e., send) data to the specified target component and its corresponding input port. Just one tuple of data is communicated.
     * The actual amount of data (i.e., in bytes) depends on the type of the port (it is the responsibility of the developer to appropriately deal with the byte
     * array size).
     *
     * @param targetComponentID
     *            the ID of the target component
     * @param targetInputPortID
     *            the ID of the corresponding input port
     * @param data
     *            an array of bytes that includes the communicated tuple of data (cannot be null)
     * @throws AREAsapiException
     *             if the specified component ID or port ID are not available
     */
    public void sendData(final String targetComponentID, final String targetInputPortID, final byte[] data) throws AREAsapiException {
        try {
            AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    // get runtime instances
                    IRuntimeModel model = DeploymentManager.instance.getCurrentRuntimeModel();
                    IComponentInstance instance = null;
                    IRuntimeInputPort inputPort = null;
                    byte[] sendData = data;
                    if (model != null) {
                        instance = model.getComponentInstance(targetComponentID);
                        if (instance != null) {
                            inputPort = instance.getWrapper(targetInputPortID);
                        }
                    }
                    if (model == null || instance == null || inputPort == null) {
                        throw new AREAsapiException(
                                MessageFormat.format("send data failed! model: {0}, instance: {1}, inputPort: {2}", model, instance, inputPort));
                    }
                    // convert to target datatype
                    DataType targetDatatype = null;
                    for (IInputPort port : instance.getInputPorts()) {
                        if (targetInputPortID.equals(port.getPortType())) {
                            targetDatatype = port.getPortDataType();
                        }
                    }
                    if (targetDatatype == null) {
                        throw new AREAsapiException(
                                MessageFormat.format("send data failed! model: {0}, instance: {1}, inputPort: {2}. Could not determine datatype of inputPort.",
                                        model, instance, inputPort));
                    }
                    if (!DataType.STRING.equals(targetDatatype)) {
                        String conversion = ConversionUtils.getDataTypeConversionString(DataType.STRING, targetDatatype);
                        sendData = ConversionUtils.convertData(data, conversion);
                    }

                    inputPort.receiveData(sendData);
                    return null;
                }
            });
        } catch (AREAsapiException e) {
            throw e;
        } catch (Exception e) {
            throw (new AREAsapiException(e.getMessage()));
        }
    }

    /**
     * Triggers an event on a given eventPort and given component and sends given data with the event.
     *
     * @param targetComponentID the ID of the target component
     * @param targetEventID     the ID of the eventPort to trigger
     * @param data              data that should be sent with the event
     * @throws AREAsapiException if the specified component ID or event port ID are not available
     */
    public void triggerEvent(final String targetComponentID, final String targetEventID, final String data) throws AREAsapiException {
        try {
            AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    // get runtime instances
                    IRuntimeComponentInstance componentInstance = DeploymentManager.instance.getComponentRuntimeInstance(targetComponentID);
                    IRuntimeEventListenerPort eventListenerPort = null;
                    if (componentInstance != null) {
                        eventListenerPort = componentInstance.getEventListenerPort(targetEventID);
                    }
                    if (componentInstance == null || eventListenerPort == null) {
                        throw new AREAsapiException(
                                MessageFormat.format("trigger event failed! componentInstance: {0}, eventPort: {1}", componentInstance, eventListenerPort));
                    }

                    eventListenerPort.receiveEvent(data);
                    return null;
                }
            });
        } catch (AREAsapiException e) {
            throw e;
        } catch (Exception e) {
            throw (new AREAsapiException(e.getMessage()));
        }
    }

    /**
     * Queries the status of the ARE system (i.e., OK, FAIL, etc)
     *
     * @return an array of status objects
     */
    public StatusObject[] queryStatus(boolean fullList) {
        return AstericsErrorHandling.instance.getStatusObjects(fullList);
    }

    /**
     * Registers an asynchronous log listener to the ARE platform. Returns an ID which is used to identify the data packets concerning the registered log
     * messages.
     *
     * @return an ID which is used to identify the data packets concerning the registered log messages
     */
    public String registerLogListener() {
        // todo
        return null;
    }

    /**
     * Unregisters the specified log listener ID from asynchronous log messages.
     *
     * @param logListenerID
     *            the ID of the log listener to be removed
     */
    public void unregisterLogListener(final String logListenerID) {
        // todo
    }

    /**
     * Deploys the model associated to the specified filename. The file should be already available on the ARE file system.
     *
     * @param filename
     *            the filename of the model to be deployed
     * @throws AREAsapiException
     *             if the specified filename is not found or cannot be deployed
     */
    public void deployFile(final String filename) throws AREAsapiException {
        try {
            AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Callable<Object>() {

                @Override
                public Object call() throws Exception {
                    AREServices.instance.deployFileInternal(filename);
                    return null;
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.logp(Level.SEVERE, this.getClass().getCanonicalName(), "deployFile(String)", e.getMessage(), e);
            throw (new AREAsapiException(e.getMessage()));
        }
    }

    /**
     * Deletes the file of the model specified by the filename parameter
     *
     * @param filename
     *            the name of the file to be deleted
     * @return true if the file was successfully deleted or false otherwise
     * @throws AREAsapiException
     *             if the file could not be found or failed to be deleted
     */
    public boolean deleteModelFile(String filename) throws AREAsapiException {
        File f;
        try {
            f = ResourceRegistry.getInstance().toFile(ResourceRegistry.getInstance().getResource(filename, RES_TYPE.MODEL));

            // Make sure the file or directory exists and isn't write protected
            if (!f.exists()) {
                logger.warning(this.getClass().getName() + ".deleteModelFile: " + "no such file or directory: " + filename);
                throw new AREAsapiException("deleteModelFile: no such file or directory: " + filename);
            }

            if (!f.canWrite()) {
                logger.warning(this.getClass().getName() + ".deleteModelFile: " + "file " + filename + " write protected\n");
                throw new AREAsapiException("Delete: write protected: " + filename);
            }

            // Attempt to delete it
            if (f.delete()) {
                logger.fine(this.getClass().getName() + ".deleteModelFile: OK\n");
                return true;
            } else {
                logger.warning(this.getClass().getName() + ".deleteModelFile: " + "Failed to delete file " + filename);
                return false;
            }
        } catch (URISyntaxException e) {
            logger.logp(Level.SEVERE, this.getClass().getCanonicalName(), "deleteModelFile(String)", e.getMessage(), e);
            throw new AREAsapiException("Could not delete model file");
        }

    }

    /**
     * Returns a list with all stored models (all models in the directory MODELS_FOLDER except default_model.xml)
     *
     * @return a list with all stored models
     * @throws AREAsapiException
     *             if MODELS_FOLDER directory could not be found
     */
    public String[] listAllStoredModels() throws AREAsapiException {
        List<URI> storedModelList = ResourceRegistry.getInstance().getModelList(true);
        return ResourceRegistry.getInstance().toStringArray(storedModelList);
    }

    /**
     * stores data with UTF-8. the location where the data is stored is determined by parameter resourceType
     *
     * @param data
     * @param resourcePath
     *            resourcePath of the data to store to.
     * @param resourceType the resource type to save the data
     * @throws AREAsapiException
     */
    public void storeData(String data, String resourcePath, RES_TYPE resourceType) throws AREAsapiException {
        try {
            ResourceRegistry.getInstance().storeResource(data, resourcePath, resourceType);
        } catch (IOException e) {
            String errorMsg = "Failed to store data -> \n" + e.getMessage();
            AstericsErrorHandling.instance.reportError(null, errorMsg);
            throw (new AREAsapiException(errorMsg));
        } catch (URISyntaxException e) {
            String errorMsg = "Failed to create file URI to store data -> \n" + e.getMessage();
            AstericsErrorHandling.instance.reportError(null, errorMsg);
            throw (new AREAsapiException(errorMsg));
        }
    }

    /**
     * stores data with UTF-8 to folder ARE/data
     *
     * @param data
     * @param resourcePath
     *            resourcePath of the data to store to.
     * @throws AREAsapiException
     */
    public void storeData(String data, String resourcePath) throws AREAsapiException {
        storeData(data, resourcePath, RES_TYPE.DATA);
    }

    /**
     * stores data with UTF-8 to folder ARE/web/webapps/<webappId>/data
     *
     * @param data the data to store
     * @param resourcePath
     *            resourcePath of the data to store to (folderpath + filename + extension)
     * @param webappId the id of the webapp to save the data
     * @throws AREAsapiException
     */
    public void storeWebappData(String data, String resourcePath, String webappId) throws AREAsapiException {
        String webappPath = ResourceRegistry.WEBAPP_FOLDER + webappId;
        try {
            URI webappUri = ResourceRegistry.getInstance().getResource(webappPath, RES_TYPE.WEB_DOCUMENT_ROOT);
            if(!ResourceRegistry.getInstance().resourceExists(webappUri)) {
                String msg = MessageFormat.format("tried to store data for webapp with ID <{0}>, but it does not exist. Aborting...", webappId);
                logger.log(Level.WARNING, msg);
                throw new AREAsapiException(msg);
            }
        } catch (URISyntaxException e) {
            String msg = MessageFormat.format("failed to store data for webapp with ID <{0}>, failed to open webapp-folder.", webappId);
            logger.log(Level.WARNING, msg);
            throw new AREAsapiException(msg);
        }

        String storePath = MessageFormat.format("{0}/{1}{2}", webappPath, ResourceRegistry.WEBAPP_SUBFOLDER_DATA, resourcePath);
        storeData(data, storePath, RES_TYPE.WEB_DOCUMENT_ROOT);
    }

    /**
     * Stores the XML model specified by the string parameter in the file specified by the filename parameter
     *
     * @param modelInXML
     *            the XML model as a String
     * @param filename
     *            the name of the file the model is to be stored.
     * @throws AREAsapiException
     *             if the file cannot be created or if the model cannot be stored
     */
    public void storeModel(String modelInXML, String filename) throws AREAsapiException {
        try {
            DefaultDeploymentModelParser.instance.parseModelAsXMLString(modelInXML);
            ResourceRegistry.getInstance().storeResource(modelInXML, filename, RES_TYPE.MODEL);
        } catch (ParseException e) {
            String errorMsg = "Failed to parse model, maybe model version not in sync with compononent descriptors -> \n" + e.getMessage();
            AstericsErrorHandling.instance.reportError(null, errorMsg);
            throw (new AREAsapiException(errorMsg));
        } catch (BundleManagementException e) {
            String errorMsg = "Failed to install model components -> \n" + e.getMessage();
            AstericsErrorHandling.instance.reportError(null, errorMsg);
            throw (new AREAsapiException(errorMsg));
        } catch (IOException e) {
            String errorMsg = "Failed to store model -> \n" + e.getMessage();
            AstericsErrorHandling.instance.reportError(null, errorMsg);
            throw (new AREAsapiException(errorMsg));
        } catch (URISyntaxException e) {
            String errorMsg = "Failed to create file URI to store model -> \n" + e.getMessage();
            AstericsErrorHandling.instance.reportError(null, errorMsg);
            throw (new AREAsapiException(errorMsg));
        }
    }

    /**
     * Returns the log file as a string.
     * 
     * @return the log file as a string.
     * @throws AREAsapiException
     */
    public String getLogFile() throws AREAsapiException {
        try {
            return ResourceRegistry.getInstance().getResourceContentAsString("asterics_logger.log", RES_TYPE.TMP);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            logger.warning("Could not fetch log file: " + e.getMessage());
            throw new AREAsapiException(e.getMessage());
        }
    }

    /**
     * It is called on startup by the middleware in order to autostart a default model without the need of pressing deploy and start model first.
     *
     * @param startModel
     *            TODO
     * @throws AREAsapiException
     */
    public void autostart(String startModel) throws AREAsapiException {

        if (startModel == null || startModel.equals("")) {
            try {
                // try to find autostart model
                // First look for a model file names autostart.acs
                File autostartModel = ResourceRegistry.getInstance().toFile(ResourceRegistry.getInstance().getResource(AUTO_START_MODEL, RES_TYPE.MODEL));
                if (autostartModel.exists()) {
                    startModel = autostartModel.getPath();
                } else {
                    // If there is no dedicated autostart model either use the
                    // only one existing or throw an error message
                    List<URI> models = ResourceRegistry.getInstance().getModelList(false);
                    if (models.size() == 1) {
                        startModel = ResourceRegistry.getInstance().toString(models.get(0));
                    } else {
                        throw new AREAsapiException(
                                "No model found for autostart. To define autostart model, either\n\ncreate model " + ResourceRegistry.MODELS_FOLDER
                                        + "autostart.acs or\nprovide model name as command line argument or\nopen model manually in the ARE GUI.");
                    }
                }
            } catch (URISyntaxException e) {
                throw new AREAsapiException("Error during autostart of model:\n" + e.getMessage() + "\nTry to open model manually in the ARE GUI.");
            }
        }
        deployFile(startModel);
        runModel();
    }

    public List<String> getRuntimePropertyList(String componentID, String key) throws AREAsapiException {
        Collection<IRuntimeComponentInstance> componentInstances = DeploymentManager.instance.getComponentRuntimeInstances();
        List<String> list = new ArrayList<String>();
        for (IRuntimeComponentInstance ci : componentInstances) {
            String id = DeploymentManager.instance.getIRuntimeComponentInstanceIDFromIRuntimeComponentInstance(ci);
            if (id.equals(componentID)) {
                list = ci.getRuntimePropertyList(key);
                return list;
            }
        }
        return list;
    }
}
