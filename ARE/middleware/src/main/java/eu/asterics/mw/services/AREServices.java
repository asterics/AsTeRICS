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

package eu.asterics.mw.services;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import eu.asterics.mw.are.AREStatus;
import eu.asterics.mw.are.DeploymentManager;
import eu.asterics.mw.are.exceptions.AREAsapiException;
import eu.asterics.mw.are.exceptions.BundleManagementException;
import eu.asterics.mw.are.exceptions.DeploymentException;
import eu.asterics.mw.are.exceptions.ParseException;
import eu.asterics.mw.are.parsers.DefaultDeploymentModelParser;
import eu.asterics.mw.model.deployment.IRuntimeModel;
import eu.asterics.mw.model.deployment.impl.ModelState;
import eu.asterics.mw.model.runtime.IRuntimeComponentInstance;
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
 * This class provides ARE functionality to software components outside the ARE
 * 
 * Date: Aug 25, 2010 Time: 11:35:35 AM
 */

public class AREServices implements IAREServices {
    private final String STORAGE_FOLDER = "storage";
    private Logger logger = null;

    private ArrayList<IAREEventListener> areEventListenerObjects;
    private ArrayList<RuntimeDataListener> runtimeDataListenerObjects;

    public static final AREServices instance = new AREServices();

    private AREServices() {
        super();
        logger = AstericsErrorHandling.instance.getLogger();
        areEventListenerObjects = new ArrayList<IAREEventListener>();
        runtimeDataListenerObjects = new ArrayList<RuntimeDataListener>();
    }

    /**
     * Deploys the model associated to the specified filename. The file should
     * be already available on the ARE file system.
     * 
     * @param filename
     *            the filename of the model to be deployed
     */
    @Override
    public void deployFile(final String filename) {
        // deployFileInternal(filename);
        // ideally this should also be executed in the same thread as the
        // others, but unfortunately AsapiSupport does not even use this.

        try {
            AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Callable() {
                @Override
                public Object call() throws Exception {
                    deployFileInternal(filename);
                    return null;
                }
            });
        } catch (Exception e) {
            String message = createErrorMsg("Could not deploy model", e);
            logger.logp(Level.SEVERE, this.getClass().getCanonicalName(), "deployFile(String)", e.getMessage(), e);
            DeploymentManager.instance.reseToCleanState();
            AstericsErrorHandling.instance.reportError(null, message);
        }
    }

    /**
     * Deploys the model associated to the specified filename. The file should
     * be already available on the ARE file system.
     * 
     * This method is not thread-safe, only use it in combination with
     * {@link AstericsModelExecutionThreadPool#execAndWaitOnModelExecutorLifecycleThread(Callable)}
     * 
     * @param filename
     *            the filename of the model to be deployed
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws TransformerException
     * @throws BundleManagementException
     * @throws ParseException
     * @throws DeploymentException
     * @throws URISyntaxException 
     */

    public void deployFileInternal(String filename) throws ParserConfigurationException, SAXException, IOException,
            TransformerException, DeploymentException, ParseException, BundleManagementException, URISyntaxException {
        final IRuntimeModel currentRuntimeModel = DeploymentManager.instance.getCurrentRuntimeModel();

        if (currentRuntimeModel != null) {
            stopModel();
        }

        String modelAsXMLString = ResourceRegistry.getInstance().getResourceContentAsString(filename, RES_TYPE.MODEL);                    
        deployModelInternal(modelAsXMLString);
        AstericsErrorHandling.instance.getLogger().info("Deployed Model " + filename + " !");        
    }

    /**
     * Deploys the model associated to the specified filename. The file should
     * be already available on the ARE file system. This method will also start
     * the model as soon as it is deployed.
     * 
     * @param filename
     *            the filename of the model to be deployed
     */
    @Override
    public void deployAndStartFile(final String filename) {
        // deployAndStartFileInternal(filename);
        // ideally this should also be executed in the same thread as the
        // others, but unfortunately AsapiSupport does not even use this.

        try {
            AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Callable() {
                @Override
                public Object call() throws Exception {
                    deployFileInternal(filename);
                    runModelInternal();
                    return null;
                }
            });
        } catch (Exception e) {
            String message = createErrorMsg("Could not deploy and start model", e);
            logger.warning(message);
            DeploymentManager.instance.reseToCleanState();

            logger.warning(message);
            AstericsErrorHandling.instance.reportError(null, message);
        }
    }

    /**
     * Sets the property with the specified key in the component with the
     * specified ID with the given string representation of the value.
     *
     * @param componentID
     *            the ID of the component to be checked
     * @param key
     *            the key of the property to be set
     * @param value
     *            the string-representation of the value to be set to the
     *            specified key
     * @return the previous value of the property with the specified key in the
     *         component with the specified ID as a string, or an empty string
     *         if the property was not previously set
     */

    @Override
    public String setComponentProperty(final String componentID, final String key, final String value) {
        /*
         * String result = DeploymentManager.instance.getCurrentRuntimeModel().
         * setComponentProperty(componentID, key, value);
         * DeploymentManager.instance.setComponentProperty (componentID, key,
         * value); if (result == null) {
         * logger.warning(this.getClass().getName()+"."+
         * "setComponentProperty: Undefined component "+ componentID+"\n");
         * return ""; } else { logger.fine(this.getClass().getName()+"."+
         * "setComponentProperty: OK\n"); return result; }
         */

        try {
            return AstericsModelExecutionThreadPool.instance
                    .execAndWaitOnModelExecutorLifecycleThread(new Callable<String>() {

                        @Override
                        public String call() throws Exception {

                            String result = DeploymentManager.instance.getCurrentRuntimeModel()
                                    .setComponentProperty(componentID, key, value);
                            DeploymentManager.instance.setComponentProperty(componentID, key, value);
                            if (result == null) {
                                logger.warning(this.getClass().getName() + "."
                                        + "setComponentProperty: Undefined component " + componentID + "\n");
                                throw new AREAsapiException("Undefined component ID: " + componentID);
                            } else {
                                return result;
                            }

                        }
                    });
        } catch (Exception e) {
            String message = createErrorMsg("Could not setComponentProperty", e);
            logger.warning(message);
            AstericsErrorHandling.instance.reportError(null, message);
        }
        return "";
    }

    /**
     * Stops the execution of the model. Unlike the {@link #pauseModel()}
     * method, this one resets the components, which means that when the model
     * is started again it starts from scratch (i.e., with a new state).
     */
    // NOTE: Don't use synchronized here, because in some cases it leads to a
    // dead lock.
    public void stopModel() {
        try {
            AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Callable() {
                @Override
                public Object call() throws Exception {
                    stopModelInternal();
                    return null;
                }
            });
        } catch (Exception e) {
            // String message="Could not execute stopModel, exception occurred:
            // "+e.getMessage()!=null ? e.getMessage() : e.toString();

            String message = createErrorMsg("Could not stop model", e);
            logger.warning(message);
            DeploymentManager.instance.reseToCleanState();
            AstericsModelExecutionThreadPool.getInstance().switchToFallbackPool();

            // Try stopping again with fallback thread
            try {
                AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        stopModelInternal();
                        return null;
                    }
                });
            } catch (Exception se) {
                // String message2="Could not execute second try of stopModel,
                // exception occurred: "+se.getMessage()!=null ? se.getMessage()
                // : se.toString();
                // setStatusObject ARE_ERROR is set in reportError
                message = createErrorMsg("Could not stop model", e);
                logger.warning("Second Try: " + message);
                AstericsErrorHandling.instance.reportError(null, message);
                DeploymentManager.instance.reseToCleanState();
                AstericsModelExecutionThreadPool.getInstance().switchToFallbackPool();
            }
        }
    }

    /**
     * Stops the currently running model.
     * 
     * This method is not thread-safe, only use it in combination with
     * {@link AstericsModelExecutionThreadPool#execAndWaitOnModelExecutorLifecycleThread(Callable)}
     */
    public void stopModelInternal() {
        logger.fine("stopModelInternal");
        if (DeploymentManager.instance.getStatus() == AREStatus.RUNNING
                || DeploymentManager.instance.getStatus() == AREStatus.PAUSED
                || DeploymentManager.instance.getStatus() == AREStatus.ERROR) {
            DeploymentManager.instance.stopModel();
            DeploymentManager.instance.getCurrentRuntimeModel().setState(ModelState.STOPPED);
            DeploymentManager.instance.setStatus(AREStatus.OK);
            AstericsErrorHandling.instance.setStatusObject(AREStatus.OK.toString(), "", "");
            logger.fine(this.getClass().getName() + ".stopModel: model stopped \n");
        }
    }

    /**
     * Deploys the model encoded in the specified string into the ARE. An
     * exception is thrown if the specified string is either not well-defined
     * XML, or not well defined ASAPI model encoding, or if a validation error
     * occurred after reading the model.
     * 
     * @param modelInXML
     *            a string representation in XML of the model to be deployed
     */

    public void deployModel(final String modelInXML) {
        // deployModelInternal(modelInXML);

        try {
            AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Callable() {
                @Override
                public Object call() throws Exception {
                    deployModelInternal(modelInXML);
                    return null;
                }
            });
        } catch (Exception e) {
            String message = createErrorMsg("Could not deploy model", e);
            logger.warning(message);
            AstericsErrorHandling.instance.reportError(null, message);
            DeploymentManager.instance.reseToCleanState();
            AstericsModelExecutionThreadPool.getInstance().switchToFallbackPool();
        }
    }

    /**
     * Deploys the given model as XML string and feeds back exceptions.
     * 
     * This method is not thread-safe, only use it in combination with
     * {@link AstericsModelExecutionThreadPool#execAndWaitOnModelExecutorLifecycleThread(Callable)}
     * 
     * @param modelInXML
     * @throws IOException
     * @throws DeploymentException
     * @throws ParseException
     * @throws BundleManagementException
     */
    public void deployModelInternal(String modelInXML)
            throws IOException, DeploymentException, ParseException, BundleManagementException {
        // Stop running model first if there is one
        if (DeploymentManager.instance.getStatus() == AREStatus.RUNNING) {
            stopModelInternal();
            DeploymentManager.instance.undeployModel();
        }
        DefaultDeploymentModelParser defaultDeploymentModelParser = DefaultDeploymentModelParser.instance;
        IRuntimeModel runtimeModel = defaultDeploymentModelParser.parseModelAsXMLString(modelInXML);
        DeploymentManager.instance.deployModel(runtimeModel);
        DeploymentManager.instance.setStatus(AREStatus.DEPLOYED);
        AstericsErrorHandling.instance.setStatusObject(AREStatus.DEPLOYED.toString(), "", "");
    }

    /**
     * It starts or resumes the execution of the model.
     */
    // NOTE: Don't use synchronized here, because in some cases it leads to a
    // dead lock.
    public void runModel() throws AREAsapiException {
        try {
            AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Callable() {
                @Override
                public Object call() throws Exception {
                    runModelInternal();
                    return null;
                }
            });
        } catch (Exception e) {
            // String message="Could not execute runModel, exception occurred:
            // "+(e.getMessage()!=null ? e.getMessage() : e.getClass());
            // setStatusObject is set in reportError
            String message = createErrorMsg("Could not start model", e);
            logger.warning(message);
            AstericsErrorHandling.instance.reportError(null, message);
            DeploymentManager.instance.reseToCleanState();
            AstericsModelExecutionThreadPool.getInstance().switchToFallbackPool();

            // Should we through exception to notify ACS?? Maybe for the next
            // release, unfortunately ACS disconnects when doing this
            // throw new AREAsapiException(message);
        }
    }

    /**
     * Starts the currently deployed model.
     * 
     * This method is not thread-safe, only use it in combination with
     * {@link AstericsModelExecutionThreadPool#execAndWaitOnModelExecutorLifecycleThread(Callable)}
     */
    public void runModelInternal() {
        // TODO Auto-generated method stub
        ModelState modelState = DeploymentManager.instance.getCurrentRuntimeModel().getState();
        logger.fine(this.getClass().getName() + ".runModel: model state: " + modelState + " \n");
        if (ModelState.STOPPED.equals(modelState)) {
            DeploymentManager.instance.runModel();
        } else if (ModelState.STARTED.equals(modelState)) {
            // if model is already running, stop it first to
            // ensure that native libs are not
            // loaded and instantiated twice.
            stopModelInternal();
            DeploymentManager.instance.runModel();
        }
        // ModelState.PAUSED
        else {
            DeploymentManager.instance.resumeModel();
        }
        DeploymentManager.instance.getCurrentRuntimeModel().setState(ModelState.STARTED);
        DeploymentManager.instance.setStatus(AREStatus.RUNNING);
        AstericsErrorHandling.instance.setStatusObject(AREStatus.RUNNING.toString(), "", "");
        logger.fine(this.getClass().getName() + ".runModel: model running \n");

    }

    /**
     * Briefly stops the execution of the model. Its main difference from the
     * {@link #stopModel()} method is that it does not reset the components
     * (e.g., the buffers are not cleared).
     *
     */
    // NOTE: Don't use synchronized here, because in some cases it leads to a
    // dead lock.
    public void pauseModel() {
        try {
            AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Callable() {
                @Override
                public Object call() throws Exception {
                    AREServices.instance.pausModelInternal();
                    return null;
                }
            });
        } catch (Exception e) {
            // String message="Could not execute pauseModel, execption occurred:
            // "+e.getMessage()!=null ? e.getMessage() : e.toString();
            String message = createErrorMsg("Could not pause model", e);
            logger.warning(message);
            AstericsErrorHandling.instance.reportError(null, message);
            DeploymentManager.instance.reseToCleanState();
            AstericsModelExecutionThreadPool.getInstance().switchToFallbackPool();
        }
    }

    /**
     * Creates an error message for an error dialog and internally logs the
     * stacktrace of the exception.
     * 
     * @param baseMsg
     * @param e
     * @return
     */
    private String createErrorMsg(String baseMsg, Exception e) {
        if (e instanceof TimeoutException) {
            return baseMsg + ", execution timeouted!";
        }
        /*
         * else if(e instanceof ExecutionException) {
         * 
         * if(e.getCause()!=null && e.getCause().getMessage()!=null) { return
         * baseMsg+", "+e.getCause().getMessage(); } }
         */
        StringWriter stackTraceWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTraceWriter));
        logger.warning(stackTraceWriter.toString());
        return baseMsg;
    }

    /**
     * Pauses the currently running model.
     * 
     * This method is not thread-safe, only use it in combination with
     * {@link AstericsModelExecutionThreadPool#execAndWaitOnModelExecutorLifecycleThread(Callable)}
     */
    public void pausModelInternal() {
        if (DeploymentManager.instance.getStatus() == AREStatus.RUNNING) {
            DeploymentManager.instance.pauseModel();
            DeploymentManager.instance.getCurrentRuntimeModel().setState(ModelState.PAUSED);
            DeploymentManager.instance.setStatus(AREStatus.PAUSED);
            AstericsErrorHandling.instance.setStatusObject(AREStatus.PAUSED.toString(), "", "");
            logger.fine(this.getClass().getName() + ".pauseModel: model paused \n");
            System.out.println("Model paused!");
        }
    }

    /**
     * 
     * @return
     */
    // TODO:Should be synchronized, but risk of dead lock due to AREMain thread
    public boolean isAREStoppedAndHealthy() {
        AREStatus status = DeploymentManager.instance.getStatus();
        if ((status == AREStatus.UNKNOWN) || (status == AREStatus.OK)) {
            return true;
        }
        return false;
    }

    /**
     * Provides the name of the currently deployed model in ARE.
     * 
     * @return the name of the model as a String object if there is one
     *         deployed, <code>null</code> otherwise.
     */
    // TODO:Should be synchronized, but risk of dead lock due to AREMain thread
    public String getRuntimeModelName() {
        IRuntimeModel currentRuntimeModel = DeploymentManager.instance.getCurrentRuntimeModel();
        if (currentRuntimeModel != null) {
            return currentRuntimeModel.getModelName();
        }
        return null;
    }

    /**
     * Opens a File object for the requested file. The method will look up the
     * current model name and the instance name of the component and open or
     * create the file if it does not exist. The file will exist in a folder
     * tree which allows each instance of a component to have its own storage on
     * a per instance per model basis
     * 
     * @return the File object if it has been created, <code>null</code> if
     *         there is currently no model deployed or the object could not be
     *         created.
     * 
     * @param component
     *            the requesting instance of a runtime component
     * 
     * @param fileName
     *            the name of the file to be opened
     */
    public synchronized File getLocalStorageFile(IRuntimeComponentInstance component, String fileName) {
        String modelName = getRuntimeModelName();

        if (modelName == null) {
            // no model running, storage not available
            return null;
        }

        modelName = modelName.replace('\\', '/');
        if (modelName.lastIndexOf('/') > 0) {
            modelName = modelName.substring(modelName.lastIndexOf('/') + 1);
        }

        StringBuffer fullFilePath = new StringBuffer(STORAGE_FOLDER);
        fullFilePath.append("/");
        fullFilePath.append(modelName);
        fullFilePath.append("/");
        fullFilePath.append(
                DeploymentManager.instance.getIRuntimeComponentInstanceIDFromIRuntimeComponentInstance(component));
        fullFilePath.append("/");
        // System.out.println("Model File Name for Local Storage
        // Service="+fullFilePath);

        File localDir = new File(fullFilePath.toString());
        File localFile = new File(fullFilePath.toString() + fileName);

        if (!localFile.exists()) {
            try {
                localDir.mkdirs();
                localFile.createNewFile();
            } catch (IOException e1) {
                DeploymentManager.instance.setStatus(AREStatus.FATAL_ERROR);
                AstericsErrorHandling.instance.setStatusObject(AREStatus.FATAL_ERROR.toString(), "",
                        "Deployment Error");
                logger.warning(this.getClass().getName() + "." + "deployModel: Failed to create file "
                        + fullFilePath.toString() + fileName + "-> \n" + e1.getMessage());
            }
        }
        return localFile;
    }

    /***************************************
     * LISTENERS - start
     ***************************************/

    @Override
    public synchronized void registerAREEventListener(IAREEventListener clazz) {

        if (!this.areEventListenerObjects.contains(clazz) && clazz != null) {
            this.areEventListenerObjects.add(clazz);
        }

    }

    @Override
    public synchronized void unregisterAREEventListener(IAREEventListener clazz) {

        Iterator<IAREEventListener> itr = this.areEventListenerObjects.iterator();
        IAREEventListener listener;
        while (itr.hasNext()) {
            listener = (IAREEventListener) itr.next();
            if (listener.equals(clazz)) {
                itr.remove();
                return;
            }
        }
    }

    public synchronized ArrayList<IAREEventListener> getAREEventListners() {
        if (this.areEventListenerObjects != null) {
            return this.areEventListenerObjects;
        } else {
            return new ArrayList<IAREEventListener>();
        }
    }

    /**
     * Registers a new {@link RuntimeDataListener} object to the
     * {@link AREServices#runtimeDataListenerObjects} list
     * 
     * @param runtimeDataListener
     *            - The class used to notify the external consumers
     */
    public synchronized void registerRuntimeDataListener(RuntimeDataListener runtimeDataListener) {

        if (!this.runtimeDataListenerObjects.contains(runtimeDataListener) && runtimeDataListener != null) {
            this.runtimeDataListenerObjects.add(runtimeDataListener);
        }

    }

    /**
     * Unregisters a {@link RuntimeDataListener} object from the
     * {@link AREServices#runtimeDataListenerObjects} list
     * 
     * @param runtimeDataListener
     *            - The class used to notify the external consumers
     */
    public synchronized void unregisterRuntimeDataListener(RuntimeDataListener runtimeDataListener) {

        Iterator<RuntimeDataListener> iterator = this.runtimeDataListenerObjects.iterator();
        RuntimeDataListener listener;
        while (iterator.hasNext()) {
            listener = (RuntimeDataListener) iterator.next();
            if (listener.equals(runtimeDataListener)) {
                iterator.remove();
                return;
            }
        }

    }

    /**
     * Iterates trough the listeners and closes their opened data channels
     */
    public void closeDataChannels() {
        for (RuntimeDataListener listener : this.runtimeDataListenerObjects) {
            listener.clearDataChannelList();
        }
    }

    /**
     * Notifies the {@link RuntimeDataListener} objects that a new
     * {@link RuntimeDataEvent} was occurred.
     * 
     * @param event
     *            - the object holding the event information
     */
    public void notifyRuntimeDataListeners(RuntimeDataEvent event) {

        switch (event.getType()) {
        case RuntimeDataEvent.TYPE_EVENT_CHANNEL:
            for (RuntimeDataListener listener : this.runtimeDataListenerObjects) {
                listener.eventChannelTransmission(event.getChannelId(), event.getComponentId());
            }
            break;
        case RuntimeDataEvent.TYPE_DATA_CHANNEL:
            for (RuntimeDataListener listener : this.runtimeDataListenerObjects) {
                if (listener.getOpenedDataChannels().contains(event.getChannelId())) {
                    listener.dataChannelTransmission(event.getChannelId(), event.getData());
                }
            }
            break;
        case RuntimeDataEvent.TYPE_COMPONENT_PROPERTY_CHANGE:
            for (RuntimeDataListener listener : this.runtimeDataListenerObjects) {
                listener.componentPropertyChanged(event.getComponentId(), event.getComponentKey(), event.getData());
            }
            break;
        }
    }

    /***************************************
     * LISTENERS - finish
     ***************************************/

    @Override
    public void displayPanel(JPanel panel, IRuntimeComponentInstance componentInstance, boolean display) {
        DeploymentManager.instance.displayPanel(panel, componentInstance, display);

    }

    @Override
    public Dimension getAvailableSpace(IRuntimeComponentInstance componentInstance) {

        return DeploymentManager.instance.getAvailableSpace(componentInstance);

    }

    @Override
    public Point getComponentPosition(IRuntimeComponentInstance componentInstance) {

        return DeploymentManager.instance.getComponentPosition(componentInstance);

    }

    public Point getScreenDimension() {
        return DeploymentManager.instance.getScreenDimension();
    }

    public Point getAREWindowDimension() {
        return DeploymentManager.instance.getAREWindowDimension();
    }

    public Point getAREWindowPosition() {
        return DeploymentManager.instance.getAREWindowLocation();
    }

    public void setAREWindowPosition(int x, int y) {
        DeploymentManager.instance.setAREWindowLocation(x, y);
    }

    public void setAREWindowState(int state) {
        DeploymentManager.instance.setAREWindowState(state);
    }

    public void setAREWindowToFront() {
        DeploymentManager.instance.setAREWindowToFront();
    }

    public void allowAREWindowModification(boolean state) {
        DeploymentManager.instance.allowAREWindowModification(state);
    }

    public void setFocusableWindowState(boolean state) {
        DeploymentManager.instance.setFocusableWindowState(state);
    }

    @Override
    public void adjustFonts(final JPanel panel, final int maxFontSize, final int minFontSize, final int offset) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                adjustPanelFonts(panel, maxFontSize, minFontSize, offset);
                panel.getComponents();
            }
        });
    }

    private void adjustPanelFonts(final JPanel c, final int maxFontSize, final int minFontSize, final int offset) {
        int containerWidth = c.getPreferredSize().width - offset;
        int containerHeight = c.getPreferredSize().height;

        Component[] comp = c.getComponents();

        for (int i = 0; i < comp.length; ++i) {
            if (JLabel.class.isAssignableFrom(comp[i].getClass())) {
                JLabel label = (JLabel) comp[i];
                Font labelFont = label.getFont();
                String labelText = label.getText();
                if (labelText.length() > 0) {
                    int stringWidth = label.getFontMetrics(labelFont).stringWidth(labelText);
                    double ratio = Math.min(containerWidth, containerHeight) / (double) stringWidth;
                    int newFontSize = (int) (labelFont.getSize() * ratio);

                    int fs = Math.min(newFontSize, containerHeight);
                    fs = Math.max(newFontSize, minFontSize);
                    fs = Math.min(fs, maxFontSize);

                    label.setFont(new Font(labelFont.getName(), labelFont.getStyle(), fs));

                }

            } else if (JTextComponent.class.isAssignableFrom(comp[i].getClass())) {

                JTextComponent label = (JTextComponent) comp[i];
                Font labelFont = label.getFont();
                String labelText = label.getText();
                if (labelText.length() > 0) {
                    int stringWidth = label.getFontMetrics(labelFont).stringWidth(labelText);
                    double ratio = Math.min(containerWidth, containerHeight) / (double) stringWidth;
                    int newFontSize = (int) (labelFont.getSize() * ratio);

                    int fs = Math.min(newFontSize, containerHeight);
                    fs = Math.max(newFontSize, minFontSize);
                    fs = Math.min(fs, maxFontSize);
                    label.setFont(new Font(labelFont.getName(), labelFont.getStyle(), fs));

                }

            } else if (JButton.class.isAssignableFrom(comp[i].getClass())) {

                JButton label = (JButton) comp[i];
                Font labelFont = label.getFont();
                String labelText = label.getText();
                if (labelText.length() > 0) {
                    int stringWidth = label.getFontMetrics(labelFont).stringWidth(labelText);
                    double ratio = Math.min(containerWidth, containerHeight) / (double) stringWidth;
                    int newFontSize = (int) (labelFont.getSize() * ratio);

                    int fs = Math.min(newFontSize, containerHeight);
                    fs = Math.max(newFontSize, minFontSize);
                    fs = Math.min(fs, maxFontSize);

                    label.setFont(new Font(labelFont.getName(), labelFont.getStyle(), fs));
                }
            } else if (JSlider.class.isAssignableFrom(comp[i].getClass())) {

                JSlider slider = (JSlider) comp[i];
                Font sliderFont = slider.getFont();
                int newFontSize = minFontSize;
                int fs = minFontSize;

                // double ratio = Math.min(containerWidth,containerHeight);
                // int newFontSize = (int)(sliderFont.getSize() * ratio);
                //
                //
                // int fs = Math.min(newFontSize, containerHeight);

                if (containerWidth > 0 && containerWidth <= 25) {
                    newFontSize = 6;
                } else if (containerWidth > 25 && containerWidth <= 50) {
                    newFontSize = 8;
                } else if (containerWidth > 50 && containerWidth <= 100) {
                    newFontSize = 10;
                } else if (containerWidth > 100 && containerWidth <= 400) {
                    newFontSize = 14;
                } else if (containerWidth > 400) {
                    newFontSize = 16;
                }

                fs = Math.max(newFontSize, minFontSize);
                fs = Math.min(fs, maxFontSize);

                slider.setFont(new Font(sliderFont.getName(), sliderFont.getStyle(), fs));

            }

            if (comp[i] instanceof JPanel) {
                adjustPanelFonts((JPanel) comp[i], maxFontSize, minFontSize, offset);
            }

        }

        // Adjust TitledBorders if any
        // Border b = c.getBorder();
        // if (TitledBorder.class.isAssignableFrom(b.getClass()))
        // {
        //
        // TitledBorder tb = (TitledBorder) b;
        //
        // Font tbFont = tb.getTitleFont();
        //
        //
        // double ratio = Math.min(containerWidth,containerHeight)
        // / (double)containerWidth;
        // int newFontSize = (int)(tbFont.getSize() * ratio);
        //
        //
        // int fs = Math.min(newFontSize, containerHeight);
        // fs = Math.max(newFontSize, minFontSize);
        // fs = Math.min(fs, maxFontSize);
        //
        // tb.setTitleFont(new Font(tbFont.getName(),
        // tbFont.getStyle(), fs));
        //
        // }

    }
}
