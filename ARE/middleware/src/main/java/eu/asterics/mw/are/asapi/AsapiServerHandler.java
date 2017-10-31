package eu.asterics.mw.are.asapi;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.thrift.TException;

import eu.asterics.mw.are.AsapiSupport;
import eu.asterics.mw.are.exceptions.AREAsapiException;
import eu.asterics.mw.services.AstericsErrorHandling;

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
 * @author This class implements the AsapiServer Interface methods by invoking
 *         the corresponding methods in the AsapiSupport class.
 * 
 *         Date:
 */

public class AsapiServerHandler implements AsapiServer.Iface {

    AsapiSupport asapiSupport = new AsapiSupport();
    private static Logger logger = null;

    public AsapiServerHandler() {
        logger = AstericsErrorHandling.instance.getLogger();
    }

    /**
     * This method deploys a model from a file
     * 
     * @param filename
     *            the name of the file to be deployed
     */
    @Override
    public void DeployFile(String filename) throws AsapiException, TException {
        // System.out.println("*** DEPLOY FILE");

        try {
            asapiSupport.deployFile(filename);
        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "." + "DeployFile: Failed to deploy file-> \n" + e.getMessage());
            throw new AsapiException(0, e.getMessage());
        }
    }

    /**
     * This method deploys an XML model which is provided as a string parameter
     * 
     * @param modelInXml
     *            the model in XML to be deployed (as a String)
     */
    @Override
    public void DeployModel(String modelInXml) throws AsapiException, TException {
        // System.out.println("*** DEPLOY FROM STRING");

        try {
            asapiSupport.deployModel(modelInXml);
        } catch (AREAsapiException e) {
            logger.warning(
                    this.getClass().getName() + "." + "DeployModel: Failed to deploy model-> \n" + e.getMessage());
            throw new AsapiException(0, e.getMessage());
        }

    }

    @Override
    public void DeployModelWithFile(String filename, String modelInXml) throws AsapiException, TException {
        // TODO Auto-generated method stub

    }

    /**
     * This method returns the ports of a component
     * 
     * @param componentID
     *            the component who's ports are to be returned
     * @return A list of the component's ports
     */
    @Override
    public List<String> GetAllPorts(String componentID) throws AsapiException, TException {
        try {
            String[] ports = asapiSupport.getAllPorts(componentID);

            return new ArrayList<String>(Arrays.asList(ports));

        } catch (AREAsapiException e) {
            logger.warning(
                    this.getClass().getName() + "." + "GetAllPorts: Failed to get all ports -> \n" + e.getMessage());
            throw new AsapiException(0, e.getMessage());

        }

    }

    /**
     * This method returns all the current available component types in the ARE
     * 
     * @return A list of the available component types
     */
    @Override
    public List<String> GetAvailableComponentTypes() throws TException {
        // System.out.println("*** GET AVAILABLE COMPONENTS");
        try {
            String[] types = asapiSupport.getAvailableComponentTypes();
            return new ArrayList<String>(Arrays.asList(types));
        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "." + "GetAvailableComponentTypes failed -> \n"
                    + e.getMessage());
            throw new TException(e.getMessage());
        }
    }

    /**
     * Returns the value of the property with the specified key in the channel
     * with the specified ID as a string.
     * 
     * @param channelID
     *            the channel who's property is to be returned
     * @param key
     *            the key of the property to be retrieved
     * @return the value of the property as a string, or null if the specified
     *         channel is not found
     */
    @Override
    public String GetChannelProperty(String channelID, String key) throws TException {
        try {
            return asapiSupport.getChannelProperty(channelID, key);
        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "." + "GetChannelProperty: Failed to get channel property -> \n"
                    + e.getMessage());
            throw new TException(e.getMessage());
        }
    }

    /**
     * Reads the IDs of all properties set for the specified channel.
     * 
     * @param channelID
     *            the ID of the channel to be checked
     * @return an array with all the property keys for the specified channel
     */
    @Override
    public List<String> GetChannelPropertyKeys(String channelID) throws TException {
        String[] properties;
        try {
            properties = asapiSupport.getChannelPropertyKeys(channelID);

            return new ArrayList<String>(Arrays.asList(properties));
        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "." + "GetChannelPropertyKeys: Failed to get channel property "
                    + "keys-> \n" + e.getMessage());
            throw new TException(e.getMessage());
        }

    }

    /**
     * Returns a list of the IDs of all the channels that include the specified
     * component either as a source or target.
     * 
     * @param componentID
     *            the ID of the specified component instance
     * @return a list containing the IDs of all the channels which include the
     *         specified component instance
     */
    @Override
    public List<String> GetChannels(String componentID) throws TException {

        String[] channels = asapiSupport.getChannels(componentID);
        return new ArrayList<String>(Arrays.asList(channels));

    }

    /**
     * Returns the value of the property with the specified key in the component
     * with the specified ID as a string.
     * 
     * @param componentID
     *            the ID of the component to be checked
     * @param key
     *            the key of the property to be retrieved
     * @return the value of the property as a string
     */
    @Override
    public String GetComponentProperty(String componentID, String key) throws TException {
        try {
            return asapiSupport.getComponentProperty(componentID, key);
        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "." + "GetComponentProperty: Failed to get component property "
                    + "-> \n" + e.getMessage());
            throw new TException(e.getMessage());
        }
    }

    /**
     * Reads the IDs of all properties set for the specified component.
     * 
     * @param componentID
     *            the ID of the component to be checked
     * @return an array with all the property keys for the specified component
     */
    @Override
    public List<String> GetComponentPropertyKeys(String componentID) throws TException {
        String[] properties;
        try {
            properties = asapiSupport.getComponentPropertyKeys(componentID);
            return new ArrayList<String>(Arrays.asList(properties));

        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "." + "GetComponentPropertyKeys: Failed to get component "
                    + "property keys -> \n" + e.getMessage());
            throw new TException(e.getMessage());
        }
    }

    /**
     * Returns a list that includes all existing component instances in the
     * model (even multiple instances of the same component type).
     * 
     * @return a list of all the IDs of the existing component instances
     */
    @Override
    public List<String> GetComponents() throws TException {
        String[] components = asapiSupport.getComponents();
        return new ArrayList<String>(Arrays.asList(components));
    }

    /**
     * Returns a list containing the IDs of all the input ports of the specified
     * component instance.
     * 
     * @param componentID
     *            the ID of the specified component instance
     * @return a list containing the IDs of all the input ports of the specified
     *         component instance
     */
    @Override
    public List<String> GetInputPorts(String componentID) throws AsapiException, TException {
        String[] inports;
        try {
            inports = asapiSupport.getInputPorts(componentID);
            return new ArrayList<String>(Arrays.asList(inports));
        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "." + "GetInputPorts: Failed to get input ports -> \n"
                    + e.getMessage());
            throw new AsapiException(0, e.getMessage());
        }

    }

    /**
     * Returns a string encoding the currently deployed model in XML. If there
     * is no model deployed, then an empty one is returned.
     * 
     * @return a string encoding the currently deployed model in XML or an empty
     *         string if there is no model deployed
     */
    @Override
    public String GetModel() throws TException {
        try {
            return asapiSupport.getModel();
        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "." + "GetModel: Failed to get model -> \n" + e.getMessage());
            throw new TException(e.getMessage());
        }
    }

    /**
     * Returns a list containing the IDs of all the output ports of the
     * specified component instance.
     * 
     * @param componentID
     *            the ID of the specified component instance
     * @return a list containing the IDs of all the output ports of the
     *         specified component instance
     */
    @Override
    public List<String> GetOutputPorts(String componentID) throws AsapiException, TException {
        String[] outports;
        try {
            outports = asapiSupport.getOutputPorts(componentID);
            return new ArrayList<String>(Arrays.asList(outports));
        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "." + "GetOutputPorts: Failed to get output ports -> \n"
                    + e.getMessage());
            throw new AsapiException(0, e.getMessage());
        }

    }

    /**
     * Returns the value of the property with the specified key of the port with
     * the specified ID in the component with the specified ID as a string.
     * 
     * @param componentID
     *            the ID of the component to be checked
     * @param portID
     *            the ID of the port to be checked
     * @param key
     *            the key of the property to be retrieved
     * @return the value of the property with the specified key in the component
     *         and port with the specified IDs as a string
     */
    @Override
    public String GetPortProperty(String componentID, String portID, String key) throws TException {
        try {
            return asapiSupport.getPortProperty(componentID, portID, key);
        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "." + "GetPortProperty: Failed to get port property -> \n"
                    + e.getMessage());
            throw new TException(e.getMessage());
        }
    }

    /**
     * Creates a channel between the specified source and target components and
     * ports.
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
     */
    @Override
    public void InsertChannel(String channelID, String sourceComponentID, String sourcePortID, String targetComponentID,
            String targetPortID) throws AsapiException, TException {
        try {
            asapiSupport.insertChannel(channelID, sourceComponentID, sourcePortID, targetComponentID, targetPortID);
        } catch (AREAsapiException e) {
            logger.warning(
                    this.getClass().getName() + "." + "InsertChannel: Failed to insert channel -> \n" + e.getMessage());
            throw new AsapiException(0, e.getMessage());
        }

    }

    /**
     * Creates a new instance of the specified component type, with the assigned
     * ID.
     * 
     * @param componentID
     *            the unique ID to be assigned to the new component instance
     * @param componentType
     *            describes the component type of the component to be
     *            instantiated
     */
    @Override
    public void InsertComponent(String componentID, String componentType) throws AsapiException, TException {

        try {
            asapiSupport.insertComponent(componentID, componentType);
        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "." + "InsertComponent: Failed to insert component -> \n"
                    + e.getMessage());
            throw new AsapiException(0, e.getMessage());
        }
    }

    /**
     * Deploys a new empty model into the ARE. In essence, this is equivalent to
     * creating an empty model and deploying it using
     * {@link #deployModel(String)}. This results to freeing all resources in
     * the ARE (i.e., if a previous model reserved any).
     * 
     * @throws AREAsapiException
     */
    @Override
    public void NewModel() throws TException {
        try {
            asapiSupport.newModel();
        } catch (AREAsapiException e) {
            logger.warning(
                    this.getClass().getName() + "." + "NewModel: Failed to create new model -> \n" + e.getMessage());
            throw new TException(e.getMessage());
        }

    }

    /**
     * Briefly stops the execution of the model. Its main difference from the
     * {@link #stopModel()} method is that it does not reset the components
     * (e.g., the buffers are not cleared).
     */
    @Override
    public void PauseModel() throws AsapiException, TException {
        try {
            asapiSupport.pauseModel();
        } catch (AREAsapiException e) {
            logger.warning(
                    this.getClass().getName() + "." + "PauseModel: Failed to pause model -> \n" + e.getMessage());
            throw new TException(e.getMessage());
        }

    }

    @Override
    public ByteBuffer PollData(String courceComponentID, String sourceOutputPortID) throws AsapiException, TException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Queries the status of the ARE system (i.e., OK, FAIL, etc)
     * 
     * @return a list of status objects
     */
    @Override
    public List<StatusObject> QueryStatus(boolean fullList) throws TException {
        return new ArrayList<StatusObject>(Arrays.asList(asapiSupport.queryStatus(fullList)));
    }

    @Override
    public String RegisterLogListener() throws TException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String RegisterRemoteConsumer(String sourceComponentID, String sourceOutputPortID)
            throws AsapiException, TException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String RegisterRemoteProducer(String targetComponentID, String targetInputPortID)
            throws AsapiException, TException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Removes an existing channel.
     * 
     * @param channelID
     *            the ID of the channel to be removed
     */
    @Override
    public void RemoveChannel(String channelID) throws AsapiException, TException {
        try {
            asapiSupport.removeChannel(channelID);
        } catch (AREAsapiException e) {
            logger.warning(
                    this.getClass().getName() + "." + "RemoveChannel: Failed to remove channel-> \n" + e.getMessage());
            throw new TException(e.getMessage());
        }

    }

    /**
     * Used to delete the instance of the component that is specified by the
     * given ID.
     * 
     * @param componentID
     *            the ID of the component to be removed
     */
    @Override
    public void RemoveComponent(String componentID) throws AsapiException, TException {
        try {
            asapiSupport.removeComponent(componentID);
        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "." + "RemoveComponent: Failed to remove component-> \n"
                    + e.getMessage());
            throw new TException(e.getMessage());
        }
    }

    /**
     * It starts or resumes the execution of the model.
     */
    @Override
    public void RunModel() throws AsapiException, TException {
        try {
            asapiSupport.runModel();
        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "." + "RunModel: Failed to run model -> \n" + e.getMessage());
            throw new AsapiException(0, e.getMessage());
        }

    }

    @Override
    public void SendData(String targetComponentID, String targetInputPortID, ByteBuffer data)
            throws AsapiException, TException {
        // TODO Auto-generated method stub

    }

    @Override
    public String SetChannelProperty(String channelID, String key, String value) throws TException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Sets the property with the specified key in the component with the
     * specified ID with the given value.
     * 
     * @param componentID
     *            the ID of the component to be checked
     * @param key
     *            the key of the property to be set
     * @param value
     *            the string-representation of the value to be set to the
     *            specified key
     * @return the previous value of the property or an empty string if the
     *         property was not previously set
     */
    @Override
    public String SetComponentProperty(String componentID, String key, String value) throws TException {
        try {
            String res = asapiSupport.setComponentProperty(componentID, key, value);
            return res;

        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "." + "SetComponentProperty: Failed to set component "
                    + "property -> \n" + e.getMessage());
            throw new TException(e.getMessage());

        }
        // return value;
    }

    /**
     * Sets the property with the specified key in the port with the specified
     * ID with the given value.
     * 
     * @param componentID
     *            the ID of the component to be checked
     * @param portID
     *            the ID of the port to be checked
     * @param key
     *            the key of the property to be set
     * @param value
     *            the string-representation of the value to be set to the
     *            specified key
     * @return the previous value of the property as a string, or an empty
     *         string if the property was not previously set
     */
    @Override
    public String SetPortProperty(String componentID, String portID, String key, String value) throws TException {
        try {
            return asapiSupport.setPortProperty(componentID, portID, key, value);
        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "." + "SetPortProperty: Failed to set port " + "property -> \n"
                    + e.getMessage());
            throw new TException(e.getMessage());
        }
        // return value;
    }

    /**
     * Stops the execution of the model. Unlike the {@link #pauseModel()}
     * method, this one resets the components, which means that when the model
     * is started again it starts from scratch (i.e., with a new state).
     */
    @Override
    public void StopModel() throws AsapiException, TException {
        try {
            asapiSupport.stopModel();
        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "." + "StopModel: Failed to stop model -> \n" + e.getMessage());
            throw new TException(e.getMessage());
        }

    }

    @Override
    public void UnregisterLogListener(String logListenerID) throws TException {
        // TODO Auto-generated method stub

    }

    @Override
    public void UnregisterRemoteConsumer(String remoteConsumerID) throws AsapiException, TException {
        // TODO Auto-generated method stub

    }

    @Override
    public void UnregisterRemoteProducer(String remoteProducerID) throws AsapiException, TException {
        // TODO Auto-generated method stub

    }

    @Override
    public List<String> getRuntimePropertyList(String componentID, String key) throws AsapiException, TException {

        try {
            return asapiSupport.getRuntimePropertyList(componentID, key);

        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "."
                    + "getRuntimePropertyList: Failed to get port property keys-> \n" + e.getMessage());
            throw new TException(e.getMessage());
        }
    }

    @Override
    public List<String> getBundleDescriptors() throws AsapiException, TException {
        // System.out.println("*** GET BUNDLE DESCRIPTORS");

        try {
            return asapiSupport.getBundelDescriptors();

        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "."
                    + "getBundleDescriptors: Failed to get list of bundle descriptors -> \n" + e.getMessage());
            throw new TException(e.getMessage());
        }
    }

    /**
     * Reads the IDs of all properties set for the specified port of the
     * specified component
     * 
     * @param componentID
     *            the ID of the port's component
     * @param portID
     *            the ID of the port to be checked
     * @return a list with all the property keys for the specified port, or null
     *         if the specified port is not found
     */
    @Override
    public List<String> GetPortPropertyKeys(String componentID, String portID) throws TException {
        String[] properties;
        try {
            properties = asapiSupport.getPortPropertyKeys(componentID, portID);
            return new ArrayList<String>(Arrays.asList(properties));

        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "."
                    + "GetPortPropertyKeys: Failed to get port property keys-> \n" + e.getMessage());
            throw new TException(e.getMessage());
        }
    }

    /**
     * Stores the XML model specified by the string parameter in the file
     * specified by the filename parameter
     * 
     * @param modelInXML
     *            the XML model as a String
     * @param filename
     *            the name of the file the model is to be stored
     */
    @Override
    public void storeModel(String modelInXML, String filename) throws AsapiException, TException {
        try {
            asapiSupport.storeModel(modelInXML, filename);
        } catch (AREAsapiException e1) {
            logger.warning(
                    this.getClass().getName() + "." + "storeModel: Failed to store model -> \n" + e1.getMessage());
            throw new AsapiException(0, e1.getMessage());
        }
    }

    /**
     * Deletes the file of the model specified by the filename parameter
     * 
     * @param filename
     *            the name of the file to be deleted
     * @return true if the file was successfully deleted or false otherwise
     */
    @Override
    public boolean deleteModelFile(String filename) throws TException {
        try {
            return asapiSupport.deleteModelFile(filename);
        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "." + "deleteModelFile: Failed to delete model file -> \n"
                    + e.getMessage());
            throw new TException(e.getMessage());
        }
    }

    /**
     * Returns a list of all stored models
     * 
     * @return a list with all stored models
     */
    @Override
    public List<String> listAllStoredModels() throws TException {
        try {
            String[] filenames = asapiSupport.listAllStoredModels();
            return new ArrayList<String>(Arrays.asList(filenames));

        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "." + "listAllStoredModels: Failed -> \n" + e.getMessage());
            throw new TException(e.getMessage());
        }
    }

    /**
     * Returns a string encoding of the model defined in the filename given as
     * argument. If there is no model, an empty string is returned.
     * 
     * @param filename
     *            the name of the file to be checked
     * @return a string encoding of the model defined in the filename
     */
    @Override
    public String getModelFromFile(String filename) throws TException {
        // System.out.println("*** GET MODEL FROM FILE");

        try {
            return asapiSupport.getModelFromFile(filename);
        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "." + "getModelFromFile: Failed to get model from file-> \n"
                    + e.getMessage());
            throw new TException(e.getMessage());
        }
    }

    /**
     * Returns the log file as a string.
     * 
     * @return the log file as a string.
     */
    @Override
    public String getLogFile() throws TException {
        try {
            return asapiSupport.getLogFile();
        } catch (AREAsapiException e) {
            logger.warning(this.getClass().getName() + "." + "getLogFile failed -> \n"
                    + e.getMessage());
            throw new TException(e.getMessage());
        }
    }

    /**
     * Ping - return 1 to show the connection is alive
     * 
     * @return 1 if the connection is alive
     */
    @Override
    public int Ping() throws TException {
        return 1;
    }

}
