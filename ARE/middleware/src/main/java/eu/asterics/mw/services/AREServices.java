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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 *
 */


/**
 * This class provides ARE functionality to software components outside the ARE 
 * 
 *         Date: Aug 25, 2010
 *         Time: 11:35:35 AM
 */

public class AREServices implements IAREServices{

	private final String MODELS_FOLDER = "models"; 
	private final String STORAGE_FOLDER = "storage";
	private Logger logger = null;

	private ArrayList<IAREEventListener> areEventListenerObjects;
	public static final AREServices instance = 
			new AREServices();
	



	private AREServices()
	{
		super();
		logger = AstericsErrorHandling.instance.getLogger();
		areEventListenerObjects = new ArrayList <IAREEventListener>();

	}


	/**
	 * Deploys the model associated to the specified filename. The file 
	 * should be already available on the ARE file system.
	 * @param filename the filename of the model to be deployed
	 */
	public void deployFile(final String filename) {
		//deployFileInternal(filename);
		//ideally this should also be executed in the same thread as the others, but unfortunately AsapiSupport does not even use this.
		
		try {
			AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Runnable() {
						@Override
						public void run() {
							deployFileInternal(filename);
						}
					});
		} catch (InterruptedException | ExecutionException | TimeoutException e) {			
			String message=createErrorMsg("Could not deploy model", e);
			logger.warning(message);
			DeploymentManager.instance.reseToCleanState();					
			AstericsErrorHandling.instance.reportError(null, message);
		}		
	}
	/**
	 * Deploys the model associated to the specified filename. The file 
	 * should be already available on the ARE file system.
	 * @param filename the filename of the model to be deployed
	 */

	private void deployFileInternal(String filename) {		
		filename = MODELS_FOLDER + "/" + filename;
		logger.fine("deployFile <"+filename+">");
		
		final IRuntimeModel currentRuntimeModel
		= DeploymentManager.instance.getCurrentRuntimeModel();

		if(currentRuntimeModel != null)
		{
			this.stopModelInternal();
			DeploymentManager.instance.undeployModel();
		}

		try{


			//this is for getting the text xml and converting it to string
			String xmlFile = filename;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			synchronized (builder) {

				Document doc = builder.parse(new File(xmlFile));
				DOMSource domSource = new DOMSource(doc);
				StringWriter writer = new StringWriter();
				StreamResult result = new StreamResult(writer);
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-16");
				transformer.transform(domSource, result);
				String modelInString = writer.toString();
				//calling the asapi function with a string representation of the model
				deployModelInternal(modelInString);
				logger.fine(this.getClass().getName()+"." +
						"deployFile: OK\n");
			}

		}catch (SAXException e3) {
			logger.warning(this.getClass().getName()+"." +
					"deployFile: Failed to deploy file -> \n"
					+e3.getMessage());

		} catch (IOException e4) {
			logger.warning(this.getClass().getName()+"." +
					"deployFile: Failed to deploy file -> \n"
					+e4.getMessage());

		} catch (ParserConfigurationException e5) {
			logger.warning(this.getClass().getName()+"." +
					"deployFile: Failed to deploy file -> \n"
					+e5.getMessage());

		} catch (TransformerConfigurationException e6) {
			logger.warning(this.getClass().getName()+"." +
					"deployFile: Failed to deploy file -> \n"
					+e6.getMessage());

		} catch (TransformerException e7) {
			logger.warning(this.getClass().getName()+"." +
					"deployFile: Failed to deploy file -> \n"
					+e7.getMessage());
		}
		
	}

	/**
	 * Deploys the model associated to the specified filename. The file 
	 * should be already available on the ARE file system. 
	 * This method will also start the model as soon as it is deployed.
	 * @param filename the filename of the model to be deployed
	 */
	public void deployAndStartFile(final String filename) {
		//deployAndStartFileInternal(filename);		
		//ideally this should also be executed in the same thread as the others, but unfortunately AsapiSupport does not even use this.
		
		try {
			AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Runnable() {
						@Override
						public void run() {
							deployAndStartFileInternal(filename);
						}
					});
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			String message=createErrorMsg("Could not deploy and start model", e);
			logger.warning(message);
			DeploymentManager.instance.reseToCleanState();
			
			logger.warning(message);			
			AstericsErrorHandling.instance.reportError(null, message);
			//AstericsModelExecutionThreadPool.getInstance().switchToFallbackPool();
			//throw new AREAsapiException(message);
		}		
	}

	/**
	 * Deploys the model associated to the specified filename. The file 
	 * should be already available on the ARE file system. 
	 * This method will also start the model as soon as it is deployed.
	 * @param filename the filename of the model to be deployed
	 */

	private void deployAndStartFileInternal(String filename) {
		filename = MODELS_FOLDER + "/" + filename;
		
		logger.fine("Deploying file <"+filename+">");
		final IRuntimeModel currentRuntimeModel
		= DeploymentManager.instance.getCurrentRuntimeModel();


		try{


			//this is for getting the text xml and converting it to string
			String xmlFile = filename;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			synchronized (builder) {
				Document doc = builder.parse(new File(xmlFile));
				DOMSource domSource = new DOMSource(doc);
				StringWriter writer = new StringWriter();
				StreamResult result = new StreamResult(writer);
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-16");
				transformer.transform(domSource, result);

				String modelInString = writer.toString();

				if(currentRuntimeModel != null)
				{
					this.stopModelInternal();
					DeploymentManager.instance.undeployModel();
				}
				//calling the asapi function with a string 
				//representation of the model
				deployModelInternal(modelInString);
				logger.fine(this.getClass().getName()+"." +
						"deployAndStartFile: OK\n");
				runModelInternal();

			}
		}catch (SAXException e3) {
			logger.warning(this.getClass().getName()+"." +
					"deployAndStartFile: Failed to deploy file -> \n"
					+e3.getMessage());

		} catch (IOException e4) {
			logger.warning(this.getClass().getName()+"." +
					"deployAndStartFile: Failed to deploy file -> \n"
					+e4.getMessage());

		} catch (ParserConfigurationException e5) {
			logger.warning(this.getClass().getName()+"." +
					"deployAndStartFile: Failed to deploy file -> \n"
					+e5.getMessage());

		} catch (TransformerConfigurationException e6) {
			logger.warning(this.getClass().getName()+"." +
					"deployAndStartFile: Failed to deploy file -> \n"
					+e6.getMessage());

		} catch (TransformerException e7) {
			logger.warning(this.getClass().getName()+"." +
					"deployAndStartFile: Failed to deploy file -> \n"
					+e7.getMessage());
		}/* catch (AREAsapiException e) {
			logger.warning(this.getClass().getName()+"." +
					"deployAndStartFile: Failed to start file -> \n"
					+e.getMessage());
		}*/
	}

	/**
	 * Sets the property with the specified key in the component with the
	 * specified ID with the given string representation of the value.
	 *
	 * @param componentID the ID of the component to be checked
	 * @param key the key of the property to be set
	 * @param value the string-representation of the value to be set to the
	 * specified key
	 * @return the previous value of the property with the specified key in the
	 * component with the specified ID as a string, or an empty string if the
	 * property was not previously set
	 */
	
	public String setComponentProperty(final String componentID, final String key,
			final String value) {
		/*
		String result = DeploymentManager.instance.getCurrentRuntimeModel().
				setComponentProperty(componentID, key, value);
		DeploymentManager.instance.setComponentProperty (componentID, key, value);
		if (result == null)
		{
			logger.warning(this.getClass().getName()+"."+
					"setComponentProperty: Undefined component "+
					componentID+"\n");
			return "";
		}
		else
		{
			logger.fine(this.getClass().getName()+"."+
					"setComponentProperty: OK\n");
			return result;
		}
		*/
		
		try {
			return AstericsModelExecutionThreadPool.instance
					.execAndWaitOnModelExecutorLifecycleThread(new Callable<String>() {

						@Override
						public String call() throws Exception {

							String result = DeploymentManager.instance
									.getCurrentRuntimeModel()
									.setComponentProperty(componentID, key,
											value);
							DeploymentManager.instance.setComponentProperty(
									componentID, key, value);
							if (result == null) {
								logger.warning(this.getClass().getName()
										+ "."
										+ "setComponentProperty: Undefined component "
										+ componentID + "\n");
								throw new AREAsapiException(
										"Undefined component ID: "
												+ componentID);
							} else {
								return result;
							}

						}
					});
		} catch (Exception e) {
			String message=createErrorMsg("Could not setComponentProperty", e);
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
	//NOTE: Don't use synchronized here, because in some cases it leads to a dead lock.
	public void stopModel() {
		try {
			AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Runnable() {
				@Override
						public void run() {
							stopModelInternal();
						}
					});
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			//String message="Could not execute stopModel, exception occurred: "+e.getMessage()!=null ? e.getMessage() : e.toString();
			String message=createErrorMsg("Could not stop model", e);
			logger.warning(message);
			DeploymentManager.instance.reseToCleanState();
			AstericsModelExecutionThreadPool.getInstance().switchToFallbackPool();
			
			//Try stopping again with fallback thread
			try {
				AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Runnable() {
					@Override
							public void run() {
								stopModelInternal();
							}
						});
			} catch (InterruptedException | ExecutionException | TimeoutException se) {
				//String message2="Could not execute second try of stopModel, exception occurred: "+se.getMessage()!=null ? se.getMessage() : se.toString();
				message=createErrorMsg("Could not stop model", e);
				logger.warning("Second Try: "+message);
				AstericsErrorHandling.instance.reportError(null, message);
				DeploymentManager.instance.reseToCleanState();
				AstericsModelExecutionThreadPool.getInstance().switchToFallbackPool();				
			}
		}
	}
	
	private void stopModelInternal() {
		logger.fine("stopModelInternal");
		if (DeploymentManager.instance.getStatus() == AREStatus.RUNNING
				|| DeploymentManager.instance.getStatus() == AREStatus.PAUSED
				|| DeploymentManager.instance.getStatus() == AREStatus.ERROR) {
			DeploymentManager.instance.stopModel();
			DeploymentManager.instance
					.getCurrentRuntimeModel().setState(
							ModelState.STOPPED);
			DeploymentManager.instance
					.setStatus(AREStatus.OK);
			AstericsErrorHandling.instance.setStatusObject(
					AREStatus.OK.toString(), "", "");
			logger.fine(this.getClass().getName()
					+ ".stopModel: model stopped \n");
		}		
	}

	/**
	 * Deploys the model encoded in the specified string into the ARE. An
	 * exception is thrown if the specified string is either not well-defined
	 * XML, or not well defined ASAPI model encoding, or if a validation error
	 * occurred after reading the model.
	 * @param modelInXML a string representation in XML of the model to be
	 * deployed
	 */	

	public void deployModel(final String modelInXML) {
		deployModelInternal(modelInXML);
		/*
		try {
			AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Runnable() {
				@Override
						public void run() {
							deployModelInternal(modelInXML);
						}
					});
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			String message="Could not execute deployModel: "+e.getMessage()!=null ? e.getMessage() : e.toString();
			logger.warning(message);
			//AstericsErrorHandling.instance.reportError(null, message);
			AstericsModelExecutionThreadPool.getInstance().switchToFallbackPool();
		}*/		
	}
	
	private void deployModelInternal(String modelInXML)
	{
		//Stop running model first if there is one
		if (DeploymentManager.instance.getStatus()==AREStatus.RUNNING)
		{
			stopModelInternal();	
			DeploymentManager.instance.undeployModel();
		}
		DefaultDeploymentModelParser defaultDeploymentModelParser = 
				DefaultDeploymentModelParser.instance;

		File modelFile = new File(MODELS_FOLDER+"/model.xml");
		File modelsDir = new File(MODELS_FOLDER);
		if (!modelFile.exists())
		{
			try {
				modelsDir.mkdir();
				modelFile.createNewFile();
			} catch (IOException e1) {
				DeploymentManager.instance.setStatus(AREStatus.FATAL_ERROR);
				AstericsErrorHandling.instance.setStatusObject
				(AREStatus.FATAL_ERROR.toString(), "", "Deployment Error");
				logger.warning(this.getClass().getName()+"." +
						"deployModel: Failed to create file model.xml -> \n"
						+e1.getMessage());

			}
		}

		//Convert the string to a byte array.
		String s = modelInXML;
		byte data[] = s.getBytes();
		try {

			BufferedWriter c = new BufferedWriter(new OutputStreamWriter
					(new FileOutputStream(modelFile),"UTF-16"));

			//out = new BufferedOutputStream(new FileOutputStream(modelFile));
			for (int i=0; i<data.length; i++)
				c.write(data[i]);

			if (c != null) {
				c.flush();
				c.close();
			}
			InputStream is = new ByteArrayInputStream(modelInXML.getBytes("UTF-16"));
			IRuntimeModel runtimeModel = 
					defaultDeploymentModelParser.parseModel(is);

			/*if (runtimeModel==null)
			{
				logger.fine("Failed to create model");
			}*/

			DeploymentManager.instance.deployModel(runtimeModel);
			DeploymentManager.instance.setStatus(AREStatus.DEPLOYED);
			AstericsErrorHandling.instance.setStatusObject(AREStatus.DEPLOYED.toString(), 
					"", "");
		}  catch (IOException e2) {
			DeploymentManager.instance.setStatus(AREStatus.FATAL_ERROR);
			AstericsErrorHandling.instance.setStatusObject
			(AREStatus.FATAL_ERROR.toString(), "", "Deployment Error");
			logger.warning(this.getClass().getName()+"." +
					"deployModel: Failed to deploy model -> \n"
					+e2.getMessage());

		} catch (DeploymentException e3) {
			DeploymentManager.instance.setStatus(AREStatus.FATAL_ERROR);
			AstericsErrorHandling.instance.setStatusObject
			(AREStatus.FATAL_ERROR.toString(), "", "Deployment Error");
			logger.warning(this.getClass().getName()+"." +
					"deployModel: Failed to deploy model -> \n"
					+e3.getMessage());

		} catch (ParseException e4) {
			DeploymentManager.instance.setStatus(AREStatus.FATAL_ERROR);
			AstericsErrorHandling.instance.setStatusObject
			(AREStatus.FATAL_ERROR.toString(), "", "Deployment Error");
			logger.warning(this.getClass().getName()+"." +
					"deployModel: Failed to deploy model -> \n"
					+e4.getMessage());

		} catch (BundleManagementException e) {
			DeploymentManager.instance.setStatus(AREStatus.FATAL_ERROR);
			AstericsErrorHandling.instance.setStatusObject
			(AREStatus.FATAL_ERROR.toString(), "", "Deployment Error");
			logger.warning(this.getClass().getName()+"." +
					"deployModel: Failed to deploy model -> \n"
					+e.getMessage());
		}
	}

	/**
	 * It starts or resumes the execution of the model.
	 */
	//NOTE: Don't use synchronized here, because in some cases it leads to a dead lock.
	public void runModel() throws AREAsapiException {
		try {
			AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Runnable() {
						@Override
						public void run() {
							runModelInternal();
						}
					});
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			//String message="Could not execute runModel, exception occurred: "+(e.getMessage()!=null ? e.getMessage() : e.getClass());
			String message=createErrorMsg("Could not start model", e);
			logger.warning(message);			
			AstericsErrorHandling.instance.reportError(null, message);
			DeploymentManager.instance.reseToCleanState();
			AstericsModelExecutionThreadPool.getInstance().switchToFallbackPool();
			
			//Should we through exception to notify ACS?? Maybe for the next release, unfortunately ACS disconnects when doing this
			//throw new AREAsapiException(message);
		} 		
	}
	
	private void runModelInternal() {
		// TODO Auto-generated method stub
		ModelState modelState = DeploymentManager.instance
				.getCurrentRuntimeModel().getState();
		logger.fine(this.getClass().getName()
				+ ".runModel: model state: " + modelState
				+ " \n");
		if (ModelState.STOPPED.equals(modelState)) {
			DeploymentManager.instance.runModel();
		} else if (modelState.STARTED.equals(modelState)) {
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
		DeploymentManager.instance.getCurrentRuntimeModel()
				.setState(ModelState.STARTED);
		DeploymentManager.instance
				.setStatus(AREStatus.RUNNING);
		AstericsErrorHandling.instance.setStatusObject(
				AREStatus.RUNNING.toString(), "", "");
		logger.fine(this.getClass().getName()
				+ ".runModel: model running \n");
		
	}
	
	/**
	 * Briefly stops the execution of the model. Its main difference from the
	 * {@link #stopModel()} method is that it does not reset the components
	 * (e.g., the buffers are not cleared).
	 *
	 */
	//NOTE: Don't use synchronized here, because in some cases it leads to a dead lock.
	public void pauseModel() {
		try {
			AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Runnable() {
						@Override
						public void run() {
							AREServices.instance.pausModelInternal();
						}
					});
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			//String message="Could not execute pauseModel, execption occurred: "+e.getMessage()!=null ? e.getMessage() : e.toString();
			String message=createErrorMsg("Could not pause model",e);			
			logger.warning(message);			
			AstericsErrorHandling.instance.reportError(null, message);
			DeploymentManager.instance.reseToCleanState();
			AstericsModelExecutionThreadPool.getInstance().switchToFallbackPool();
		}
	}
	
	private String createErrorMsg(String baseMsg, Exception e) {		
		if(e instanceof TimeoutException) {
			return baseMsg+", execution timeouted!";
		} else if(e instanceof ExecutionException) {
			if(e.getCause()!=null && e.getCause().getMessage()!=null) {
				return baseMsg+", "+e.getCause().getMessage();
			}
		}
		return baseMsg;
	}
	
	private void pausModelInternal() {
		if (DeploymentManager.instance.getStatus() == AREStatus.RUNNING) {
			DeploymentManager.instance.pauseModel();
			DeploymentManager.instance
					.getCurrentRuntimeModel().setState(
							ModelState.PAUSED);
			DeploymentManager.instance
					.setStatus(AREStatus.PAUSED);
			AstericsErrorHandling.instance.setStatusObject(
					AREStatus.PAUSED.toString(), "", "");
			logger.fine(this.getClass().getName()
					+ ".pauseModel: model paused \n");
			System.out.println("Model paused!");
		}
	}

	/**
	 * 
	 * @return
	 */
	//TODO:Should be synchronized, but risk of dead lock due to AREMain thread
	public boolean isAREStoppedAndHealthy()
	{
		AREStatus status = DeploymentManager.instance.getStatus();
		if ((status == AREStatus.UNKNOWN) || (status == AREStatus.OK) )
		{
			return true;
		}
		return false;
	}


	/**
	 * Provides the name of the currently deployed model in ARE.
	 * 
	 * @return the name of the model as a String object if there is one 
	 * deployed, <code>null</code> otherwise.
	 */
	//TODO:Should be synchronized, but risk of dead lock due to AREMain thread
	public String getRuntimeModelName()
	{
		IRuntimeModel currentRuntimeModel = DeploymentManager.instance.
				getCurrentRuntimeModel();
		if(currentRuntimeModel != null)
		{
			return currentRuntimeModel.getModelName();
		}
		return null;
	}

	/**
	 * Opens a File object for the requested file. The method will look up the 
	 * current model name and the instance name of the component and open or
	 * create the file if it does not exist. The file will exist in a folder
	 * tree which allows each instance of a component to have its own storage
	 * on a per instance per model basis
	 * 
	 * @return the File object if it has been created, <code>null</code> if
	 * there is currently no model deployed or the object could not be created.
	 * 
	 * @param component	the requesting instance of a runtime component
	 * 
	 * @param fileName	the name of the file to be opened
	 */
	public synchronized File getLocalStorageFile(IRuntimeComponentInstance component, 
			String fileName)
	{
		String modelName = getRuntimeModelName();

		if (modelName == null)
		{
			// no model running, storage not available
			return null;
		}

		modelName=modelName.replace('\\', '/');
		if (modelName.lastIndexOf('/')>0)
		  modelName=modelName.substring(modelName.lastIndexOf('/')+1);

	
		StringBuffer fullFilePath = new StringBuffer(STORAGE_FOLDER);
		fullFilePath.append("/");
		fullFilePath.append(modelName);
		fullFilePath.append("/");
		fullFilePath.append(DeploymentManager.instance
				.getIRuntimeComponentInstanceIDFromIRuntimeComponentInstance(component));
		fullFilePath.append("/");
		//System.out.println("Model File Name for Local Storage Service="+fullFilePath);

		File localDir  = new File(fullFilePath.toString());
		File localFile = new File(fullFilePath.toString() + fileName);

		if (!localFile.exists())
		{
			try {
				localDir.mkdirs();
				localFile.createNewFile();
			} catch (IOException e1) {
				DeploymentManager.instance.setStatus(AREStatus.FATAL_ERROR);
				AstericsErrorHandling.instance.setStatusObject
				(AREStatus.FATAL_ERROR.toString(), "", "Deployment Error");
				logger.warning(this.getClass().getName()+"." +
						"deployModel: Failed to create file " + fullFilePath.
						toString() + fileName + "-> \n"+e1.getMessage());
			}
		}
		return localFile;
	}


	public synchronized void registerAREEventListener(IAREEventListener clazz) {

		if (!this.areEventListenerObjects.contains(clazz) && clazz!=null)
		{
			this.areEventListenerObjects.add(clazz);
		}	

	}


	public synchronized void unregisterAREEventListener(IAREEventListener clazz) {

		Iterator<IAREEventListener> itr = this.areEventListenerObjects.iterator();
		IAREEventListener listener;
		while (itr.hasNext())
		{
			listener=(IAREEventListener) itr.next();
			if (listener.equals(clazz))
			{
				itr.remove();
				return;
			}
		}
	}

	public synchronized ArrayList<IAREEventListener> getAREEventListners ()
	{
		if (this.areEventListenerObjects!=null)
			return this.areEventListenerObjects;
		else 
			return new ArrayList<IAREEventListener>();	
	}

	public void displayPanel(JPanel panel, 
			IRuntimeComponentInstance componentInstance, boolean display) {
		DeploymentManager.instance.displayPanel (panel, componentInstance, 
				display);

	}


	public Dimension getAvailableSpace(IRuntimeComponentInstance componentInstance)
	{

		return DeploymentManager.instance.getAvailableSpace(componentInstance);

	}

	public Point getComponentPosition (IRuntimeComponentInstance componentInstance)
	{

		return DeploymentManager.instance.getComponentPosition(componentInstance);

	}

	public Point getScreenDimension ()
	{
		return DeploymentManager.instance.getScreenDimension();
	}
	public Point getAREWindowDimension ()
	{
		return DeploymentManager.instance.getAREWindowDimension();
	}

	public Point getAREWindowPosition ()
	{
		return DeploymentManager.instance.getAREWindowLocation();
	}

	public void setAREWindowPosition (int x, int y)
	{
		DeploymentManager.instance.setAREWindowLocation(x,y);
	}
	
	public void setAREWindowState (int state)
	{
		DeploymentManager.instance.setAREWindowState(state);
	}
	public void setAREWindowToFront ()
	{
		DeploymentManager.instance.setAREWindowToFront();
	}
	public void allowAREWindowModification(boolean state)
	{
		DeploymentManager.instance.allowAREWindowModification(state);
    }
	public void setFocusableWindowState (boolean state){
		DeploymentManager.instance.setFocusableWindowState(state);
	}

	
	public void adjustFonts(final JPanel panel, final int maxFontSize,
			final int minFontSize, final int offset) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				adjustPanelFonts(panel, maxFontSize, minFontSize, offset);
				Component[] components = panel.getComponents();
				// if (components.length>0)
				// {
				// for (Component c : components)
				// {
				// System.out.println ("Component: "+c.getName());
				// if (c instanceof JPanel)
				// {
				// System.out.println ("Instance of JPanel");
				//
				// adjustPanelFonts ((JPanel) c, maxFontSize, minFontSize,
				// offset);
				// }
				// }
				// }
			}
		});
	}

	private void adjustPanelFonts(final JPanel c, final int maxFontSize,
			final int minFontSize, final int offset) {
		int containerWidth = c.getPreferredSize().width - offset;
		int containerHeight = c.getPreferredSize().height;

		Component[] comp = c.getComponents();

		for (int i = 0; i < comp.length; ++i) {
			if (JLabel.class.isAssignableFrom(comp[i].getClass())) {
				JLabel label = (JLabel) comp[i];
				Font labelFont = label.getFont();
				String labelText = label.getText();
				if (labelText.length() > 0) {
					int stringWidth = label.getFontMetrics(labelFont)
							.stringWidth(labelText);
					double ratio = Math.min(containerWidth, containerHeight)
							/ (double) stringWidth;
					int newFontSize = (int) (labelFont.getSize() * ratio);

					int fs = Math.min(newFontSize, containerHeight);
					fs = Math.max(newFontSize, minFontSize);
					fs = Math.min(fs, maxFontSize);

					label.setFont(new Font(labelFont.getName(), labelFont
							.getStyle(), fs));

				}

			} else if (JTextComponent.class
					.isAssignableFrom(comp[i].getClass())) {

				JTextComponent label = (JTextComponent) comp[i];
				Font labelFont = label.getFont();
				String labelText = label.getText();
				if (labelText.length() > 0) {
					int stringWidth = label.getFontMetrics(labelFont)
							.stringWidth(labelText);
					double ratio = Math.min(containerWidth, containerHeight)
							/ (double) stringWidth;
					int newFontSize = (int) (labelFont.getSize() * ratio);

					int fs = Math.min(newFontSize, containerHeight);
					fs = Math.max(newFontSize, minFontSize);
					fs = Math.min(fs, maxFontSize);
					label.setFont(new Font(labelFont.getName(), labelFont
							.getStyle(), fs));

				}

			} else if (JButton.class.isAssignableFrom(comp[i].getClass())) {

				JButton label = (JButton) comp[i];
				Font labelFont = label.getFont();
				String labelText = label.getText();
				if (labelText.length() > 0) {
					int stringWidth = label.getFontMetrics(labelFont)
							.stringWidth(labelText);
					double ratio = Math.min(containerWidth, containerHeight)
							/ (double) stringWidth;
					int newFontSize = (int) (labelFont.getSize() * ratio);

					int fs = Math.min(newFontSize, containerHeight);
					fs = Math.max(newFontSize, minFontSize);
					fs = Math.min(fs, maxFontSize);

					label.setFont(new Font(labelFont.getName(), labelFont
							.getStyle(), fs));
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

				if (containerWidth > 0 && containerWidth <= 25)
					newFontSize = 6;
				else if (containerWidth > 25 && containerWidth <= 50)
					newFontSize = 8;
				else if (containerWidth > 50 && containerWidth <= 100)
					newFontSize = 10;
				else if (containerWidth > 100 && containerWidth <= 400)
					newFontSize = 14;
				else if (containerWidth > 400)
					newFontSize = 16;

				fs = Math.max(newFontSize, minFontSize);
				fs = Math.min(fs, maxFontSize);

				slider.setFont(new Font(sliderFont.getName(), sliderFont
						.getStyle(), fs));

			}

			if (comp[i] instanceof JPanel) {
				adjustPanelFonts((JPanel) comp[i], maxFontSize, minFontSize,
						offset);
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
