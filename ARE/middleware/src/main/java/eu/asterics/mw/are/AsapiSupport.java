package eu.asterics.mw.are;

import java.awt.Component;
import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


import eu.asterics.mw.are.asapi.StatusObject;
import eu.asterics.mw.are.exceptions.AREAsapiException;
import eu.asterics.mw.are.exceptions.BundleManagementException;
import eu.asterics.mw.are.exceptions.DeploymentException;
import eu.asterics.mw.are.exceptions.ParseException;
import eu.asterics.mw.are.parsers.DefaultBundleModelParser;
import eu.asterics.mw.are.parsers.DefaultDeploymentModelParser;
import eu.asterics.mw.model.bundle.ComponentType;
import eu.asterics.mw.model.bundle.IComponentType;
import eu.asterics.mw.model.deployment.IChannel;
import eu.asterics.mw.model.deployment.IComponentInstance;
import eu.asterics.mw.model.deployment.IEventChannel;
import eu.asterics.mw.model.deployment.IInputPort;
import eu.asterics.mw.model.deployment.IOutputPort;
import eu.asterics.mw.model.deployment.IRuntimeModel;
import eu.asterics.mw.model.deployment.impl.DefaultACSGroup;
import eu.asterics.mw.model.deployment.impl.DefaultChannel;
import eu.asterics.mw.model.deployment.impl.DefaultComponentInstance;
import eu.asterics.mw.model.deployment.impl.ModelGUIInfo;
import eu.asterics.mw.model.deployment.impl.ModelState;
import eu.asterics.mw.model.runtime.IRuntimeComponentInstance;
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
 * This class implements the actual functionality of the AsapiServer Interface 
 * methods. The methods of this class are called by the corresponding methods 
 * in the AsapiSupport class.
 *         Date: Aug 25, 2010
 *         Time: 11:35:35 AM
 */
public class AsapiSupport 
{
	private final ComponentRepository componentRepository
	= ComponentRepository.instance;

	private Logger logger = null;
	private DocumentBuilder builder;

	public static final String DEFAULT_MODEL_URL = "/default_model.xml";
	public static final String AUTO_START_MODEL = "autostart.acs";

	public AsapiSupport() {
		logger = AstericsErrorHandling.instance.getLogger();
	}


	/**
	 * Returns an array containing all the available (i.e., installed) component
	 * types. These are encoded as strings, representing the absolute class
	 * name (in Java) of the corresponding implementation.
	 *
	 * @return an array containing all available component types
	 */
	public String [] getAvailableComponentTypes() {
		try {
			return AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Callable<String[]>() {

				@Override
				public String[] call() throws Exception {		

					//The method name indicates available (all components for the platform) but componentRepository.getInstalledComponentTypes()
					//returns the currently installed ones, maybe should remove this method.
					final Set<IComponentType> componentTypeSet
					= componentRepository.getInstalledComponentTypes();

					if (componentTypeSet.size()==0)
						logger.fine(this.getClass().getName()+".getAvailableComponentTypes:" 
								+" No installed component types found!");
					final String [] componentTypes = new String[componentTypeSet.size()];

					int counter = 0;
					for(final IComponentType componentType : componentTypeSet)
					{
						componentTypes[counter++] = componentType.getID();
					}

					return componentTypes;
				}
			});

		} catch (Exception e) {
			logger.severe("Error in fetching installed componentType of ComponentRepository: "+e.getMessage());				
		}		
		return new String[0];
	}	
	
	/**
	 * Returns a formatted XML String of the componentType(s) in the bundle descriptor. 
	 * @param bundleDescriptorURL
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private String getFormattedBundleDescriptorStringOfComponentTypeId(URL bundleDescriptorURL) throws MalformedURLException, IOException {
		//Actually we should ask DefaultBundleModelParser to return just the part that belongs to the requested componentTypeId
		//e.g. in case of AnalogIn there is also a second component type LegacyAnalogIn in the bundle_descriptor.xml
		//Skip it for now because result is unformatted and JavaScript showed error callback.
		//String bundleDescriptorString=DefaultBundleModelParser.instance.getBundleDescriptionOfComponentTypeId(componentTypeId, bundleDescriptorURI.toURL().openStream());
		
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(bundleDescriptorURL.openStream()));
		String bundle_descriptor = "", line;
		while ((line = bufferedReader.readLine()) != null) {
			bundle_descriptor += line + "\n";
		}

		bundle_descriptor = bundle_descriptor.replaceFirst("<\\?xml version=\"[0-9]\\.[0-9]\"\\?>", "");
		bundle_descriptor = bundle_descriptor.replaceFirst("^(<componentTypes)?^[^>]*>", "");
		bundle_descriptor = bundle_descriptor.replaceFirst("</componentTypes>", "");	
		
		return bundle_descriptor; 
	}
	
	
	/**
	 * Returns an xml String containing the component collection (bundle descriptors
	 * of every AsTeRiCS component). This function searches in the bin/ARE folder
	 * to discover the created components. 
	 *
	 * @return an xml string containing all the bundle descriptors (some parts of the descriptor are removed)
	 * and null if an error has occurred.
	 * @throws AREAsapiException 
	 */
	public String getComponentDescriptorsAsXml() throws AREAsapiException {
		try {
			return AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Callable<String>() {

				@Override
				public String call() throws Exception {

					String response = "";

					response += "<?xml version=\"1.0\"?>";
					response += "<componentTypes xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">";

					List<Bundle> bundleList=DeploymentManager.instance.getBundleManager().getInstallableBundleList();
					for (Bundle bundle : bundleList)
					{
						URL bundleDescriptorURL = bundle.getResource(DefaultBundleModelParser.BUNDLE_DESCRIPTOR_RELATIVE_URI);
						if (bundleDescriptorURL!=null)
						{
							try {
								response += getFormattedBundleDescriptorStringOfComponentTypeId(bundleDescriptorURL);
							} catch (IOException e) {
								//just logging (as 'getBundleDescriptors' function)
								AstericsErrorHandling.instance.getLogger().warning("Could not get AsTeRiCS bundle descriptor for bundle: "+bundle.getBundleId());
							}
						}
					}
					response += "</componentTypes>";

					return response;
				}
			});
		} catch (Exception e) {
			logger.severe("Error in fetching installable bundle list: "+e.getMessage());
			throw new AREAsapiException(e.getMessage());
		}		
	}
	
	
	/**
	 * Returns a string encoding the currently deployed model in XML. If there
	 * is no model deployed, then an empty one is returned.
	 *
	 * @return a string encoding the currently deployed model in XML or an empty
	 * string if there is no model deployed
	 * @throws AREAsapiException 
	 */
	public String getModel() throws AREAsapiException
	{
		final IRuntimeModel currentRuntimeModel
		= DeploymentManager.instance.getCurrentRuntimeModel();

		//If trying to get a model with no model deployed
		//we deploy the lastly used model and then we return it
		if(currentRuntimeModel == null)
		{
			try{

				//this is for getting the text xml and converting it to string
				String xmlFile = ResourceRegistry.MODELS_FOLDER+"/model.xml";

				//check if dir exists and if not create it
				File fileName = new File(xmlFile);
				File modelsDir = new File(ResourceRegistry.MODELS_FOLDER);
				if (!fileName.exists())
					modelsDir.mkdir();
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

				builder = factory.newDocumentBuilder();
				synchronized (builder){
					Document doc = builder.parse(fileName);

					DOMSource domSource = new DOMSource(doc);

					StringWriter writer = new StringWriter();
					StreamResult result = new StreamResult(writer);
					TransformerFactory tf = TransformerFactory.newInstance();
					Transformer transformer = tf.newTransformer();
					transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-16");
					transformer.transform(domSource, result);

					String modelInString = writer.toString();
					//calling the asapi function with a string representation of the model
					deployModel(modelInString);
				}

			} catch (AREAsapiException e1) {
				logger.warning(this.getClass().getName()+"." +
						"getModel: Failed to get model -> \n"+e1.getMessage());
				throw (new AREAsapiException(e1.getMessage()));
			} catch (SAXException e2) {
				logger.warning(this.getClass().getName()+"." +
						"getModel: Failed to get model -> \n"+e2.getMessage());
				throw (new AREAsapiException(e2.getMessage()));
			} catch (IOException e3) {
				logger.warning(this.getClass().getName()+"." +
						"getModel: Failed to get model -> \n"+e3.getMessage());
				throw (new AREAsapiException(e3.getMessage()));
			} catch (ParserConfigurationException e4) {
				logger.warning(this.getClass().getName()+"." +
						"getModel: Failed to get model -> \n"+e4.getMessage());
				throw (new AREAsapiException(e4.getMessage()));
			} catch (TransformerConfigurationException e5) {
				logger.warning(this.getClass().getName()+"." +
						"getModel: Failed to get model -> \n"+e5.getMessage());
				throw (new AREAsapiException(e5.getMessage()));
			} catch (TransformerException e6) {
				logger.warning(this.getClass().getName()+"." +
						"getModel: Failed to get model -> \n"+e6.getMessage());
				throw (new AREAsapiException(e6.getMessage()));
			}
		}	
		return modelToXML();
	}

	/**
	 * Returns a string encoding of the model defined in the filename given
	 * as argument. If there is no model, an empty string is returned.
	 * 
	 * @param filename the name of the file to be checked
	 * @return a string encoding of the model defined in the filename
	 * @throws AREAsapiException if could not get model from file
	 */
	public String getModelFromFile(String filename) throws AREAsapiException
	{
		filename = ResourceRegistry.MODELS_FOLDER + "/" + filename;

		//check if dir exists and if not create it
		File fileName = new File(filename);
		File modelsDir = new File(ResourceRegistry.MODELS_FOLDER);
		if (!fileName.exists())
			modelsDir.mkdir();
		String modelInString = "";
		try{
			//this is for getting the text xml and converting it to string

			String xmlFile = filename;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			synchronized (builder){
				Document doc = builder.parse(new File(xmlFile));
				DOMSource domSource = new DOMSource(doc);
				StringWriter writer = new StringWriter();
				StreamResult result = new StreamResult(writer);
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-16");
				transformer.transform(domSource, result);
				modelInString = writer.toString();
			}

		} catch (SAXException e1) {
			logger.warning(this.getClass().getName()+"." +
					"getModelFromFile: Failed to get model from file -> \n"
					+e1.getMessage());
			throw (new AREAsapiException(e1.getMessage()));
		} catch (IOException e2) {
			logger.warning(this.getClass().getName()+"." +
					"getModelFromFile: Failed to get model from file -> \n"
					+e2.getMessage());
			throw (new AREAsapiException(e2.getMessage()));
		} catch (ParserConfigurationException e3) {
			logger.warning(this.getClass().getName()+"." +
					"getModelFromFile: Failed to get model from file -> \n"
					+e3.getMessage());
			throw (new AREAsapiException(e3.getMessage()));
		} catch (TransformerConfigurationException e4) {
			logger.warning(this.getClass().getName()+"." +
					"getModelFromFile: Failed to get model from file -> \n"
					+e4.getMessage());
			throw (new AREAsapiException(e4.getMessage()));
		} catch (TransformerException e5) {
			logger.warning(this.getClass().getName()+"." +
					"getModelFromFile: Failed to get model from file -> \n"
					+e5.getMessage());
			throw (new AREAsapiException(e5.getMessage()));
		}

		return modelInString;

	}


	/**
	 * Returns the state of the current runtime model.
	 * 
	 * @return - The state of the runtime model. See {@link ModelState} class for the available states.
	 */
	public String getModelState() {
		ModelState modelState = DeploymentManager.instance.getCurrentRuntimeModel().getState();

		return modelState.toString();
	}
	
	
	/**
	 * Deploys the model encoded in the specified string into the ARE. An
	 * exception is thrown if the specified string is either not well-defined
	 * XML, or not well defined ASAPI model encoding, or if a validation error
	 * occurred after reading the model.
	 *
	 * @param modelInXML a string representation in XML of the model to be
	 * deployed
	 * @throws AREAsapiException if the specified string is either not
	 * well-defined XML, or not well defined ASAPI model encoding, or if a
	 * validation error occurred after reading the model
	 */
	public void deployModel(final String modelInXML) throws AREAsapiException {
		// Stop running model first if there is one
		if (DeploymentManager.instance
				.getCurrentRuntimeModel() != null) {
			logger.fine("Before Deploying model, trying to stop old before.");
			stopModel();
			DeploymentManager.instance.undeployModel();
		}

		try {
			AstericsModelExecutionThreadPool.instance
					.execAndWaitOnModelExecutorLifecycleThread(new Callable<Object>() {

						@Override
						public Object call() throws Exception {
							AREServices.instance.deployModelInternal(modelInXML);
							return null;
						}
					});
		} catch (ParseException e4) {
			DeploymentManager.instance.undeployModel();
			DeploymentManager.instance.setStatus(AREStatus.FATAL_ERROR);
			AstericsErrorHandling.instance.setStatusObject(
					AREStatus.FATAL_ERROR.toString(), "", "Deployment Error");
			logger.warning(this.getClass().getName() + "."
					+ "deployModel: Failed to deploy model -> \n"
					+ e4.getMessage());
			throw (new AREAsapiException("Model could not be parsed or is not compatible with installed components.\nTry to convert the model file by opening and resaving it with the AsTeRICS Configuration Suite (ACS)"));
		} catch (Throwable t) {
			DeploymentManager.instance.undeployModel();
			DeploymentManager.instance.setStatus(AREStatus.FATAL_ERROR);
			AstericsErrorHandling.instance.setStatusObject(
					AREStatus.FATAL_ERROR.toString(), "", "Deployment Error");
			logger.warning(this.getClass().getName() + "."
					+ "deployModel: Failed to deploy model -> \n"
					+ t.getMessage());
			throw (new AREAsapiException("Model could not be deployed."));
		}

	}

	/**
	 * Retrieves the descriptors of AsTeRiCS bundles.
	 * 
	 * @return A {@link List} of {@link String} which contains all the bundle descriptors.
	 * 
	 * @throws AREAsapiException
	 */
	public List<String> getBundelDescriptors() throws AREAsapiException {	
		try {
			return AstericsModelExecutionThreadPool.instance.execAndWaitOnModelExecutorLifecycleThread(new Callable<List<String>>() {

			@Override
			public List<String> call() throws Exception {
				List<String> res=new ArrayList<String>();
								
				List<Bundle> bundleList=DeploymentManager.instance.getBundleManager().getInstallableBundleList();
				for (Bundle bundle : bundleList)
				{				
					URL url = bundle.getResource(DefaultBundleModelParser.BUNDLE_DESCRIPTOR_RELATIVE_URI);
					if (url!=null)
					{
						try {
							res.add(convertXMLFileToString(url.openStream()));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							//throw (new AREAsapiException(e.getMessage()));
							//keep it by just logging
							//because we have to assume that several bundles are not supported for the platform
							AstericsErrorHandling.instance.getLogger().warning("Could not get AsTeRICS bundle descriptor for url: "+url);
						}
					}
				}

				return res;
			}
			});
		} catch (Exception e) {
			logger.severe("Error in fetching installable bundle list: "+e.getMessage());
			throw new AREAsapiException(e.getMessage());
		}
	}

	
	/**
	 * Deploys a new empty model into the ARE. In essence, this is equivalent
	 * to creating an empty model and deploying it using
	 * {@link #deployModel(String)}. This results to freeing all resources in
	 * the ARE (i.e., if a previous model reserved any).
	 * @throws AREAsapiException 
	 */
	public void newModel() throws AREAsapiException
	{
		try {
			AstericsModelExecutionThreadPool.instance
			.execAndWaitOnModelExecutorLifecycleThread(new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					final URL url = Main.getAREContext().getBundle().getResource(DEFAULT_MODEL_URL);

					//try {
					synchronized (DefaultDeploymentModelParser.instance) {


						IRuntimeModel runtimeModel = 
								DefaultDeploymentModelParser.instance.parseModel(url.toString());
						if (runtimeModel==null)
						{
							DeploymentManager.instance.setStatus(AREStatus.FATAL_ERROR);
							AstericsErrorHandling.instance.setStatusObject
							(AREStatus.FATAL_ERROR.toString(), "", "Deployment Error");

							logger.warning(this.getClass().getName()+"." +
									"newModel: Failed to create new model ->" +
									" the default model could not be found\n");
							return null;
						}
						DeploymentManager.instance.deployModel(runtimeModel);
					}
					return null;
				}
			});			
		}  catch (DeploymentException e) {
			DeploymentManager.instance.setStatus(AREStatus.FATAL_ERROR);
			AstericsErrorHandling.instance.setStatusObject
			(AREStatus.FATAL_ERROR.toString(), "", "Deployment Error");
			logger.warning(this.getClass().getName()+"." +
					"newModel: Failed to create new model -> \n" 
					+e.getMessage());
			throw (new AREAsapiException(e.getMessage()));
		} catch (ParseException e1) {
			DeploymentManager.instance.setStatus(AREStatus.FATAL_ERROR);
			AstericsErrorHandling.instance.setStatusObject
			(AREStatus.FATAL_ERROR.toString(), "", "Deployment Error");
			logger.warning(this.getClass().getName()+"." +
					"newModel: Failed to create new model -> \n" 
					+e1.getMessage());
			throw (new AREAsapiException(e1.getMessage()));
		} catch (UnsupportedEncodingException e2) {
			DeploymentManager.instance.setStatus(AREStatus.FATAL_ERROR);
			AstericsErrorHandling.instance.setStatusObject
			(AREStatus.FATAL_ERROR.toString(), "", "Deployment Error");
			logger.warning(this.getClass().getName()+"." +
					"newModel: Failed to create new model -> \n" 
					+e2.getMessage());
			throw (new AREAsapiException(e2.getMessage()));
		} catch (Exception e) {
			DeploymentManager.instance.setStatus(AREStatus.FATAL_ERROR);
			AstericsErrorHandling.instance.setStatusObject
			(AREStatus.FATAL_ERROR.toString(), "", "Deployment Error");
			logger.warning(this.getClass().getName()+"." +
					"newModel: Failed to create new model -> \n" 
					+e.getMessage());
			throw (new AREAsapiException(e.getMessage()));
		}
	}

	/**
	 * It starts or resumes the execution of the model.
	 * 
	 * @throws AREAsapiException
	 *             if an exception occurs while validating and starting the
	 *             deployed model.
	 */
	public void runModel() throws AREAsapiException {
		AREServices.instance.runModel();
		/*
		 * if (DeploymentManager.instance.getCurrentRuntimeModel().getState().
		 * equals(ModelState.STOPPED)) { DeploymentManager.instance.runModel();
		 * } else { DeploymentManager.instance.resumeModel(); }
		 * DeploymentManager.instance.getCurrentRuntimeModel().
		 * setState(ModelState.STARTED);
		 * DeploymentManager.instance.setStatus(AREStatus.RUNNING);
		 * AstericsErrorHandling
		 * .instance.setStatusObject(AREStatus.RUNNING.toString(), "", "");
		 * logger.fine(this.getClass().getName()+".runModel: model running \n");
		 * System.out.println("Model started!");
		 */
	}

	/**
	 * Briefly stops the execution of the model. Its main difference from the
	 * {@link #stopModel()} method is that it does not reset the components
	 * (e.g., the buffers are not cleared).
	 * 
	 * @throws AREAsapiException
	 *             if the deployed model is not started already, or if the
	 *             execution cannot be paused
	 */
	public void pauseModel() throws AREAsapiException {
		AREServices.instance.pauseModel();
	}

	/**
	 * Stops the execution of the model. Unlike the {@link #pauseModel()}
	 * method, this one resets the components, which means that when the model
	 * is started again it starts from scratch (i.e., with a new state).
	 * 
	 * @throws AREAsapiException
	 *             if the deployed model is not started already, or if the
	 *             execution cannot be stopped
	 */
	public void stopModel() throws AREAsapiException {
		// Delegate to AREServices
		AREServices.instance.stopModel();

		/*
		 * if (DeploymentManager.instance.getStatus()==AREStatus.RUNNING ||
		 * DeploymentManager.instance.getStatus()==AREStatus.PAUSED ||
		 * DeploymentManager.instance.getStatus()==AREStatus.ERROR) {
		 * DeploymentManager.instance.stopModel();
		 * DeploymentManager.instance.getCurrentRuntimeModel().
		 * setState(ModelState.STOPPED);
		 * DeploymentManager.instance.setStatus(AREStatus.OK);
		 * AstericsErrorHandling
		 * .instance.setStatusObject(AREStatus.OK.toString(), "", "");
		 * logger.fine
		 * (this.getClass().getName()+".stopModel: model stopped \n");
		 * System.out.println("Model stopped!");
		 * 
		 * }
		 */

	}

	/**
	 * Returns an array that includes all existing component instances in the
	 * model (even multiple instances of the same component type).
	 *
	 * @return an array of all the IDs of the existing component instances
	 */
	public String [] getComponents()
	{		
		String[] components=DeploymentManager.instance.getCurrentRuntimeModel().
				getComponentInstancesIDs();
		if (components != null)
		{
			logger.fine(this.getClass().getName()+".getComponents: OK\n");
			return components;
		}
		else
		{
			logger.warning(this.getClass().getName()+".getComponents: Failed\n");
			return null;
		}

	}

	/**
	 * Returns an array containing the IDs of all the channels that include the
	 * specified component instance either as a source or target.
	 *
	 * @param componentID the ID of the specified component instance
	 * @return an array containing the IDs of all the channels which include
	 * the specified component instance
	 */
	public String [] getChannels(final String componentID)
	{
		String[] channels	= DeploymentManager.instance.getCurrentRuntimeModel().
				getChannelsIDs(componentID);
		if (channels != null)
		{
			logger.fine(this.getClass().getName()+".getChannels: OK \n");
			return channels;
		}
		else
		{
			logger.warning(this.getClass().getName()+".getChannels: Failed \n");
			return null;
		}
	}

	/**
	 * Used to create a new instance of the specified component type, with the
	 * assigned ID. Throws an exception if the specified component type is not
	 * available, or if the specified ID is already defined.
	 *
	 * @param componentID the unique ID to be assigned to the new component
	 * instance
	 * @param componentType describes the component type of the component to be
	 * instantiated
	 * @throws AREAsapiException if the specified component type is not available,
	 * or if the specified ID is already defined
	 */
	public void insertComponent(
			final String componentID, final String componentType)
					throws AREAsapiException
					{
		//Should also be called with AstericsModelExecutorThreadPool
		
		Set<IComponentType> availableComponentTypes = 
				componentRepository.getInstalledComponentTypes();
		boolean isAvailable = false;

		for (IComponentType ct : availableComponentTypes)
		{
			if (ct.getType().equals(componentType))
			{
				isAvailable=true;
				break;
			}

		}
		if (isAvailable)
		{
			boolean alreadyDefined = false;
			Set<IComponentInstance> componentInstances =
					DeploymentManager.instance.getCurrentRuntimeModel().
					getComponentInstances();

			for (IComponentInstance ci : componentInstances)
			{
				if (ci.getInstanceID().equals(componentID))
				{
					alreadyDefined=true;
					break;
				}

			}
			if (alreadyDefined)
			{
				throw new AREAsapiException 
				("Already defined component ID: "+componentID);
			}
			//TODO All OK, insert component
			DefaultComponentInstance newInstance = new DefaultComponentInstance 
					(componentID,
							componentType,
							"", 
							new LinkedHashSet<IInputPort> (),
							new LinkedHashSet<IOutputPort> (),
							new LinkedHashMap<String, Object> (),
							new Point (0,0),
							//false,
							null,
							new LinkedHashSet<IInputPort> ());
			DeploymentManager.instance.getCurrentRuntimeModel().insertComponent
			(newInstance);
		}
		else
		{
			logger.warning(this.getClass().getName()+".insertComponent: " +
					"not available component type -> "+componentType+" \n");
			throw new AREAsapiException 
			("Not available component type: "+componentType);
		}
					}

	/**
	 * Used to delete the instance of the component that is specified by the
	 * given ID. Throws an exception if the specified component ID is not
	 * defined.
	 *
	 * @param componentID the ID of the component to be removed
	 * @throws AREAsapiException if the specified component ID is not defined
	 */
	public void removeComponent(final String componentID)
			throws AREAsapiException
			{
		//Should also be called with AstericsModelExecutorThreadPool		
		
		IComponentInstance componentInstance = 
				DeploymentManager.instance.getCurrentRuntimeModel().
				getComponentInstance(componentID);

		if (componentInstance==null)
		{
			logger.warning(this.getClass().getName()+".removeComponent: " +
					"component "+componentID+" missing \n");
			throw new AREAsapiException ("Component " +componentID+ "missing");
		}

		try {
			DeploymentManager.instance.removeComponent(componentID);
		} catch (BundleManagementException e1) {
			logger.warning(this.getClass().getName()+".removeComponent: " +
					"Failed -> \n"+e1.getMessage()+" \n");
			throw new AREAsapiException (e1.getMessage());
		}
		catch (BundleException e2) {
			logger.warning(this.getClass().getName()+".removeComponent: " +
					"Failed -> \n"+e2.getMessage()+" \n");
			throw new AREAsapiException (e2.getMessage());
		}
		DeploymentManager.instance.getCurrentRuntimeModel().
		removeComponentInstance(componentID);
			}

	/**
	 * Returns an array containing the IDs of all the ports (i.e., includes
	 * both input and output ones) of the specified component instance. An
	 * exception is thrown if the specified component instance is not defined.
	 *
	 * @param componentID the ID of the specified component instance
	 * @return an array (non empty) containing the IDs of all the ports of the
	 * specified component instance
	 * @throws AREAsapiException if the specified component instance is not defined
	 */
	public String [] getAllPorts(final String componentID)
			throws AREAsapiException
			{
		String[] ports = DeploymentManager.instance.getCurrentRuntimeModel().
				getComponentPorts(componentID);

		if (ports != null)
		{
			logger.fine(this.getClass().getName()+".getAllPorts: OK \n");
			return ports;
		}
		else
		{
			logger.warning(this.getClass().getName()+".getAllPorts: Failed \n");
			return null;
		}
			}

	/**
	 * Returns an array containing the IDs of all the input ports of the
	 * specified component instance. An exception is thrown if the specified
	 * component instance is not defined.
	 *
	 * @param componentID the ID of the specified component instance
	 * @return an array (possibly empty) containing the IDs of all the input
	 * ports of the specified component instance
	 * @throws AREAsapiException if the specified component instance is not defined
	 */
	public String [] getInputPorts(final String componentID)
			throws AREAsapiException
			{
		String[] inputPorts = DeploymentManager.instance.getCurrentRuntimeModel().
				getComponentInputPorts(componentID);

		if (inputPorts != null)
		{
			logger.fine(this.getClass().getName()+".getInputPorts: OK \n");
			return inputPorts;
		}
		else
		{
			logger.warning(this.getClass().getName()+".getInputPorts: Failed \n");
			return null;
		}
			}

	/**
	 * Returns an array containing the IDs of all the output ports of the
	 * specified component instance. An exception is thrown if the specified
	 * component instance is not defined.
	 *
	 * @param componentID the ID of the specified component instance
	 * @return an array (possibly empty) containing the IDs of all the output
	 * ports of the specified component instance
	 * @throws AREAsapiException if the specified component instance is not defined
	 */
	public String [] getOutputPorts(final String componentID)
			throws AREAsapiException
			{
		String[] outPorts = DeploymentManager.instance.getCurrentRuntimeModel().
				getComponentOutputPorts(componentID);

		if (outPorts != null)
		{
			logger.fine(this.getClass().getName()+".getOutputPorts: OK\n");
			return outPorts;
		}
		else
		{
			logger.warning(this.getClass().getName()+".getOutputPorts: Failed\n");
			return null;
		}
			}

	/**
	 * Creates a channel between the specified source and target components and
	 * ports. Throws an exception if the specified ID is already defined, or
	 * the specified component or port IDs is not found, or if the data types
	 * of the ports do not match. Also, an exception is thrown if there is
	 * already a channel connected to the specified input port (only one channel
	 * is allowed per input port).
	 *
	 * @param channelID the ID to be assigned to the formed channel
	 * @param sourceComponentID the ID of the source component
	 * @param sourcePortID the ID of the source port
	 * @param targetComponentID the ID of the target component
	 * @param targetPortID the ID of the target port
	 * @throws AREAsapiException if either of the specified component or port IDs
	 * is not found, or if the data types of the ports do not match, or if
	 * there is already a channel connected to the specified input port
	 */
	public void insertChannel(final String channelID,
			final String sourceComponentID,
			final String sourcePortID,
			final String targetComponentID,
			final String targetPortID)
					throws AREAsapiException
					{
		//Should also be called with AstericsModelExecutorThreadPool		
		
		IRuntimeModel model = DeploymentManager.instance.getCurrentRuntimeModel();

		if (model.getComponentInstance(sourceComponentID)==null){
			logger.warning(this.getClass().getName()+".insertChannel: " +
					"Undefined source component ID "+sourceComponentID+"\n");
			throw new AREAsapiException
			("Undefined source component ID: "+sourceComponentID);
		}

		if (model.getComponentInstance(targetComponentID)==null){
			logger.warning(this.getClass().getName()+".insertChannel: " +
					"Undefined target component ID "+targetComponentID+"\n");
			throw new AREAsapiException
			("Undefined target component ID: "+targetComponentID);
		}

		Set<IChannel> channels = model.getChannels();
		for (IChannel ch : channels)
		{

			if (ch.getChannelID().equals(channelID)){
				logger.warning(this.getClass().getName()+".insertChannel: " +
						"Channel "+channelID+" already defined\n");
				throw new AREAsapiException
				("Channel "+channelID+" already defined");
			}


			//check if there is already a channel connected to the 
			//specified input port (i.e., there is already a channel of which
			//the sourcecomponentinstanceid and sourceportid are the same
			if (ch.getSourceComponentInstanceID().equals(sourceComponentID)&&
					model.getPort(sourceComponentID, sourcePortID)!=null){
				logger.warning(this.getClass().getName()+".insertChannel: " +
						"Input port already connected to a channel with ID "+
						ch.getChannelID()+"\n");
				throw new AREAsapiException
				("Input port already connected to a channel with ID: "+
						ch.getChannelID());
			}

		}

		if (!isOfTheSameType(sourceComponentID, sourcePortID,targetComponentID,
				targetPortID))
		{
			logger.warning(this.getClass().getName()+".insertChannel: " +
					"Icompatible port data types between port "+sourcePortID+
					" and "+targetPortID+"\n");
			throw new AREAsapiException
			("Icompatible port data types between port "+sourcePortID+" and "+
					targetPortID);
		}


		DefaultChannel newChannel = new DefaultChannel ("",
				sourceComponentID, 
				sourcePortID,
				targetComponentID,
				targetPortID,
				channelID,
				new LinkedHashMap<String, Object> ());

		model.insertChannel(newChannel);


					}

	private boolean isOfTheSameType(String sourceComponentID, 
			String sourcePortID, String targetComponentID, String targetPortID){

		String srcPortID, trgPortID, srcType, trgType;

		IRuntimeModel model = DeploymentManager.instance.getCurrentRuntimeModel();
		String sourceComponentTypeID = 
				model.getComponentInstance(sourceComponentID).getComponentTypeID();
		String targetComponentTypeID = 
				model.getComponentInstance(targetComponentID).getComponentTypeID();
		Set<IOutputPort> outPorts = 
				model.getComponentInstance(sourceComponentID).getOutputPorts();
		Set<IInputPort> inPorts = 
				model.getComponentInstance(targetComponentID).getInputPorts();
		for (IOutputPort op:outPorts)
		{

			srcPortID = op.getPortType();
			if (srcPortID.equals(sourcePortID))
			{
				for (IInputPort ip:inPorts)
				{
					trgPortID = ip.getPortType();
					if (trgPortID.equals(targetPortID))
					{
						srcType = this.componentRepository.
								getPortDataType(sourceComponentTypeID,
										sourcePortID).toString();
						trgType = this.componentRepository.getPortDataType
								(targetComponentTypeID,targetPortID).
								toString(); 


						if ( srcType!=null &&  trgType!=null && 
								srcType.equals(trgType))
						{
							logger.fine(this.getClass().getName()+
									".isOfTheSameType: OK\n");
							return true;
						}
						else
						{
							logger.warning(this.getClass().getName()+
									".isOfTheSameType: Failed\n");
							return false;
						}
					}


				}
			}
		}
		return false;
	}

	/**
	 * Removes an existing channel between the specified source and target
	 * components and ports. Throws an exception if the specified channel is
	 * not found.
	 *
	 * @param channelID the ID of the channel to be removed
	 * @throws AREAsapiException if the specified channel ID is not found
	 */
	public void removeChannel(final String channelID)
			throws AREAsapiException
			{
		//Should also be called with AstericsModelExecutorThreadPool		
		
		DeploymentManager.instance.getCurrentRuntimeModel().
		removeChannel(channelID);
			}


	/**
	 * Reads the IDs of all properties set for the specified component.
	 *
	 * @param componentID the ID of the component to be checked
	 * @return an array (possibly empty) with all the property keys for the
	 * specified component
	 * @throws AREAsapiException if the specified component is not found
	 */
	public String [] getComponentPropertyKeys(final String componentID) 
			throws AREAsapiException
			{
		String[] result = DeploymentManager.instance.getCurrentRuntimeModel().
				getComponentPropertyKeys(componentID);
		if (result == null)
		{
			logger.warning(this.getClass().getName()+"." +
					"getComponentPropertyKeys: "+"Undefined component "+
					componentID+"\n");
			throw new AREAsapiException ("Undefined component ID: "+componentID);
		}
		else
		{
			logger.fine(this.getClass().getName()+"." +
					"getComponentPropertyKeys: OK\n");
			return result;
		}
			}

	/**
	 * Returns the value of the property with the specified key in the
	 * component with the specified ID as a string.
	 *
	 * @param componentID the ID of the component to be checked
	 * @param key the key of the property to be retrieved
	 * @return the value of the property with the specified key in the
	 * component with the specified ID as a string
	 * @throws AREAsapiException if the specified
	 * component is not found
	 */
	public String getComponentProperty(
			final String componentID, final String key) throws AREAsapiException
			{
		String result = DeploymentManager.instance.getCurrentRuntimeModel().
				getComponentProperty(componentID, key);
		if (result == null)
		{
			logger.warning(this.getClass().getName()+"." +
					"getComponentProperty: Undefined component "+
					componentID+"\n");
			throw new AREAsapiException ("Undefined component ID: "+componentID);
		}
		else
		{	
			logger.fine(this.getClass().getName()+"."+
					"getComponentProperty: OK\n");
			return result;
		}
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
	 * @throws AREAsapiException if the specified component is not found
	 */
	public String setComponentProperty(final String componentID,
			final String key, final String value) throws AREAsapiException {
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
								logger.fine(this.getClass().getName() + "."
										+ "setComponentProperty: OK\n");
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
	 * @param componentID the ID of the port's component
	 * @param portID the ID of the port to be checked
	 * @return an array (possibly empty) with all the property keys for the
	 * specified port, or null if the specified port is not found
	 * @throws AREAsapiException 
	 */
	public String [] getPortPropertyKeys(final String componentID,
			final String portID) throws AREAsapiException
			{
		String[] result = DeploymentManager.instance.getCurrentRuntimeModel().
				getPortPropertyKeys(componentID, portID);
		if (result == null)
		{
			logger.warning(this.getClass().getName()+"."+
					"getPortPropertyKeys: Undefined component or port "+
					componentID+", "+portID+"\n");
			throw new AREAsapiException ("Undefined component or port ID: "
					+componentID+", "+portID);
		}
		else
		{
			logger.fine(this.getClass().getName()+".getPortPropertyKeys: OK\n");
			return result;
		}
			}

	/**
	 * Returns the value of the property with the specified key of the port with the 
	 * specified ID in the component with the specified ID as a string.
	 *
	 * @param componentID the ID of the component to be checked
	 * @param portID the ID of the port to be checked
	 * @param key the key of the property to be retrieved
	 * @return the value of the property with the specified key in the
	 * component and port with the specified IDs as a string
	 * @throws AREAsapiException if the specified component or port are not found
	 */
	public String getPortProperty(final String componentID,
			final String portID, final String key) throws AREAsapiException
			{
		String result = DeploymentManager.instance.getCurrentRuntimeModel().
				getPortProperty(componentID, portID, key);
		if (result == null)
		{
			logger.warning(this.getClass().getName()+"."+
					"getPortProperty: Undefined component or port "+
					componentID+", "+portID+"\n");
			throw new AREAsapiException ("Undefined component or port ID: "
					+componentID+", "+portID);
		}
		else
		{
			logger.fine(this.getClass().getName()+".getPortProperty: OK \n");
			return result;
		}
			}

	/**
	 * Sets the property with the specified key in the port with the
	 * specified ID with the given string representation of the value.
	 *
	 * @param componentID the ID of the component to be checked
	 * @param portID the ID of the port to be checked
	 * @param key the key of the property to be set
	 * @param value the string-representation of the value to be set to the
	 * specified key
	 * @return the previous value of the property with the specified key in the
	 * component and port with the specified IDs, as a string, or an empty
	 * string if the property was not previously set
	 * @throws AREAsapiException if the specified component or port are not found
	 */
	public String setPortProperty(final String componentID,
			final String portID, final String key, final String value)
			throws AREAsapiException {
		try {
			return AstericsModelExecutionThreadPool.instance
					.execAndWaitOnModelExecutorLifecycleThread(new Callable<String>() {

						@Override
						public String call() throws Exception {

							String result = DeploymentManager.instance
									.getCurrentRuntimeModel().setPortProperty(
											componentID, portID, key, value);
							if (result == null) {
								logger.warning(this.getClass().getName()
										+ "."
										+ "setPortProperty: Undefined component or port "
										+ componentID + ", " + portID + "\n");
								throw new AREAsapiException(
										"Undefined component or port ID: "
												+ componentID + ", " + portID);
							} else {
								logger.fine(this.getClass().getName() + "."
										+ "setPortProperty: OK\n");
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
	 * @param channelID the ID of the channel to be checked
	 * @return an array (possibly empty) with all the property keys for the
	 * specified channel
	 * @throws AREAsapiException if the specified channel is not found
	 */
	public String [] getChannelPropertyKeys(final String channelID) 
			throws AREAsapiException
			{
		String[] result = DeploymentManager.instance.getCurrentRuntimeModel().
				getChannelPropertyKeys(channelID);
		if (result == null)
		{
			logger.warning(this.getClass().getName()+"."+
					"getChannelPropertyKeys: Undefined channel "+
					channelID+"\n");
			throw new AREAsapiException ("Undefined channel ID: "+channelID);
		}
		else
		{
			logger.fine(this.getClass().getName()+"."+
					"getChannelPropertyKeys: OK\n");
			return result;
		}
			}

	/**
	 * Returns the value of the property with the specified key in the channel
	 * with the specified ID as a string.
	 *
	 * @param channelID the ID of the channel to be checked
	 * @param key the key of the property to be retrieved
	 * @return the value of the property with the specified key in the channel
	 * with the specified ID as a string, or null if the specified channel is
	 * not found
	 * @throws AREAsapiException 
	 */
	public String getChannelProperty(final String channelID, final String key) 
			throws AREAsapiException
			{
		String result = DeploymentManager.instance.getCurrentRuntimeModel().
				getChannelProperty(channelID, key);
		if (result == null)
		{
			logger.warning(this.getClass().getName()+"."+
					"getChannelProperty: Undefined channel "+
					channelID+"\n");
			throw new AREAsapiException ("Undefined channel ID: "+channelID);
		}
		else
		{
			logger.fine(this.getClass().getName()+".getChannelProperty: OK\n");
			return result;
		}
			}

	/**
	 * Sets the property with the specified key in the channel with the
	 * specified ID with the given string representation of the value.
	 *
	 * @param channelID the ID of the channel to be checked
	 * @param key the key of the property to be set
	 * @param value the string-representation of the value to be set to the
	 * specified key
	 * @return the previous value of the property with the specified key in the
	 * channel with the specified ID as a string, or an empty string if the
	 * property was not previously set
	 * @throws AREAsapiException if the specified channel is not found
	 */
	public String setChannelProperty(final String channelID, final String key,
			final String value) throws AREAsapiException {
		try {
			return AstericsModelExecutionThreadPool.instance
					.execAndWaitOnModelExecutorLifecycleThread(new Callable<String>() {

						@Override
						public String call() throws Exception {

							String result = DeploymentManager.instance
									.getCurrentRuntimeModel()
									.setChannelProperty(channelID, key, value);
							if (result == null) {
								logger.warning(this.getClass().getName()
										+ "."
										+ "setChannelProperty: Undefined channel "
										+ channelID + "\n");
								throw new AREAsapiException(
										"Undefined channel ID: " + channelID);
							} else {
								logger.fine(this.getClass().getName()
										+ ".setChannelProperty: OK\n");
								return result;
							}
						}
					});
		} catch (Exception e) {
			throw (new AREAsapiException(e.getMessage()));
		}
	}

	/**
	 * Registers a remote consumer to the data produced by the specified source
	 * component and the corresponding output port. In the background, the ARE
	 * forms a proxy component that is connected to the specified component and
	 * port, which is utilized to communicate the data to the corresponding
	 * remote consumer. This is similar to the proxy-based approach used in
	 * Java RMI (see
	 * <a href="http://java.sun.com/developer/technicalArticles/RMI/rmi">
	 * http://java.sun.com/developer/technicalArticles/RMI/rmi</a>
	 * and <a href="http://today.java.net/article/2004/05/28/rmi-dynamic-proxies-and-evolution-deployment">
	 * http://today.java.net/article/2004/05/28/rmi-dynamic-proxies-and-evolution-deployment
	 * </a>).
	 *
	 * @param sourceComponentID the ID of the source component instance
	 * @param sourceOutputPortID the ID of the source output port from where
	 * data will be communicated
	 * @return remote consumer ID - a unique ID used to select the data received
	 * for this link
	 * @throws AREAsapiException if the specified component ID or port ID are not
	 * defined
	 */
	public String registerRemoteConsumer(final String sourceComponentID,
			final String sourceOutputPortID)
					throws AREAsapiException
					{
		//TODO
		return null;
		//return DeploymentManager.instance.registerRemoteConsumer(sourceComponentID,sourceOutputPortID);

					}

	/**
	 * Unregisters the remote consumer channel with the specified ID.
	 *
	 * @param remoteConsumerID the ID of the channel to be unregistered
	 * @throws AREAsapiException if the specified channel ID cannot be found
	 */
	public void unregisterRemoteConsumer(final String remoteConsumerID)
			throws AREAsapiException
			{
		// todo
			}

	/**
	 * Registers a remote producer to provide data to the specified target
	 * component and the corresponding input port. In the background, the ARE
	 * forms a proxy component that is connected to the specified component and
	 * port, which is utilized to receive the data from the corresponding
	 * remote producer.
	 *
	 * @param targetComponentID the ID of the target component instance
	 * @param targetInputPortID the ID of the target input port where data will
	 * be communicated to
	 * @return remote producer ID - a unique ID used to mark the data sent
	 * @throws AREAsapiException if the specified component ID or port ID are not
	 * found, or if the input port already has an assigned channel
	 * @see #registerRemoteConsumer(String, String)
	 */
	public String registerRemoteProducer(final String targetComponentID,
			final String targetInputPortID)
					throws AREAsapiException
					{
		// todo
		return null;
					}

	/**
	 * Unregisters the remote producer channel with the specified ID.
	 *
	 * @param remoteProducerID the ID of the channel to be unregistered
	 * @throws AREAsapiException if the specified channel ID cannot be found
	 */
	public void unregisterRemoteProducer(final String remoteProducerID)
			throws AREAsapiException
			{
		// todo
			}

	/**
	 * This method is used to poll (i.e., retrieve) data from the specified
	 * source component and its corresponding output port. Just one tuple of
	 * data is returned. The actual amount of data (i.e., in bytes) depends
	 * on the type of the port (it is the responsibility of the developer to
	 * appropriately deal with the byte array size).
	 *
	 * @param sourceComponentID the ID of the source component
	 * @param sourceOutputPortID the ID of the corresponding output port
	 * @return an array of bytes that includes the requested tuple of data
	 * (can be null if no data were produced)
	 * @throws AREAsapiException if the specified component ID or port ID are not
	 * available
	 */
	public byte [] pollData(final String sourceComponentID,
			final String sourceOutputPortID)
					throws AREAsapiException
					{
		/*byte[] result = this.DeploymentManager.instance.getCurrentRuntimeModel().
		pollData(sourceComponentID, sourceOutputPortID);
		if (result == null)
			throw new AsapiException ("Undefined component or port ID: "
					+sourceComponentID+", "+sourceOutputPortID);
		else
			return result;*/
		return null;
					}

	/**
	 * This method is used to pull (i.e., send) data to the specified target
	 * component and its corresponding input port. Just one tuple of data is
	 * communicated. The actual amount of data (i.e., in bytes) depends on the
	 * type of the port (it is the responsibility of the developer to
	 * appropriately deal with the byte array size).
	 *
	 * @param targetComponentID the ID of the target component
	 * @param targetInputPortID the ID of the corresponding input port
	 * @param data an array of bytes that includes the communicated tuple of
	 * data (cannot be null)
	 * @throws AREAsapiException if the specified component ID or port ID are not
	 * available
	 */
	public void sendData(final String targetComponentID,
			final String targetInputPortID,
			final byte [] data)
					throws AREAsapiException
					{
		// todo
					}

	/**
	 * Queries the status of the ARE system (i.e., OK, FAIL, etc)
	 *
	 * @return an array of status objects
	 */
	public StatusObject[] queryStatus(boolean fullList)
	{
		return AstericsErrorHandling.instance.getStatusObjects(fullList);
	}

	/**
	 * Registers an asynchronous log listener to the ARE platform. Returns an
	 * ID which is used to identify the data packets concerning the registered
	 * log messages.
	 *
	 * @return an ID which is used to identify the data packets concerning the
	 * registered log messages
	 */
	public String registerLogListener()
	{
		// todo
		return null;
	}

	/**
	 * Unregisters the specified log listener ID from asynchronous log messages.
	 *
	 * @param logListenerID the ID of the log listener to be removed
	 */
	public void unregisterLogListener(final String logListenerID)
	{
		// todo
	}

	private final String modelToXML (){

		//Get the current runtime model instance
		final IRuntimeModel currentRuntimeModel
		= DeploymentManager.instance.getCurrentRuntimeModel();

		//We need a Document

		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {

			docBuilder = dbfac.newDocumentBuilder();
			synchronized (docBuilder){
				DOMImplementation impl = docBuilder.getDOMImplementation();
				Document doc = impl.createDocument(null,null,null);

				//Create the root
				Element model = doc.createElement("model");
				doc.appendChild(model);
				model.setAttribute("xmlns:xsi", 
						"http://www.w3.org/2001/XMLSchema-instance");
				model.setAttribute("xsi:noNamespaceSchemaLocation", 
						"deployment_model.xsd");
				model.setAttribute ("modelName", 
						currentRuntimeModel.getModelName() );
				model.setAttribute ("modelVersion", 
						currentRuntimeModel.getModelVersion() );
				//model.setAttribute ("modelDescription", 
				//currentRuntimeModel.getModelDescription() );

				//Add description
				Element descElement = doc.createElement("modelDescription");
				model.appendChild(descElement);
				Element shortDescElement = doc.createElement("shortDescription");
				descElement.appendChild(shortDescElement);
				shortDescElement.setTextContent(currentRuntimeModel.
						getModelShortDescription());
				Element reqElement = doc.createElement("requirements");
				descElement.appendChild(reqElement);
				reqElement.setTextContent(currentRuntimeModel.
						getModelRequirements());
				Element descriptionElement = doc.createElement("description");
				descElement.appendChild(descriptionElement);
				descriptionElement.setTextContent(currentRuntimeModel.
						getModelDescription());

				//End of channels

				//Add components
				Element components = doc.createElement("components");

				model.appendChild(components);

				Set<IComponentInstance>componentInstances =
						currentRuntimeModel.getComponentInstances();

				for (IComponentInstance ci : componentInstances)
				{

					ci.appendXMLElements(doc);

				}

				//End of components
				//Add channels
				Set<IChannel>channels =	currentRuntimeModel.getChannels();
				if (channels.size() > 0)
				{
					Element channelsElement = doc.createElement("channels");
					model.appendChild(channelsElement);
					for (IChannel channel : channels)
					{
						channel.appendXMLElements(doc);
					}
				}
				//End of channels

				//Add event channels

				Set<IEventChannel>ecentChannels =	
						currentRuntimeModel.getEventChannels();
				if (ecentChannels.size()>0)
				{
					Element eventChannelsElement = 
							doc.createElement("eventChannels");
					model.appendChild(eventChannelsElement);

					for (IEventChannel eventChannel : ecentChannels)
					{
						eventChannel.appendXMLElements(doc);
					}
				}

				//Add Groups
				ArrayList<DefaultACSGroup>groups =	currentRuntimeModel.getACSGroups();
				if (groups.size() > 0)
				{

					Element groupsElement = doc.createElement("groups");
					model.appendChild(groupsElement);
					Iterator<DefaultACSGroup> itr = groups.iterator();
					while(itr.hasNext())
					{
						DefaultACSGroup group = itr.next();
						group.appendXMLElements(doc);
					}

				}
				//End of channels

				ModelGUIInfo modelGUIInfo =	currentRuntimeModel.getModelGuiInfo();
				if (modelGUIInfo != null)
				{
					Element modelGUI = doc.createElement("modelGUI");
					model.appendChild(modelGUI);
					modelGUIInfo.appendXMLElements(doc);
				}
				
				
				// transform the Document into a String
				DOMSource domSource = new DOMSource(doc);

				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				transformer.setOutputProperty
				(OutputKeys.OMIT_XML_DECLARATION, "yes");

				transformer.setOutputProperty(OutputKeys.METHOD, "xml");
				transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-16");
				transformer.setOutputProperty
				("{http://xml.apache.org/xslt}indent-amount", "4");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");

				java.io.StringWriter sw = new java.io.StringWriter();
				StreamResult sr = new StreamResult(sw);
				transformer.transform(domSource, sr);
				String xml = sw.toString();
				logger.fine(this.getClass().getName()+".modelToXML: OK\n");

				//System.out.println ("AsapiSupport.getModel():"+xml);
				return xml;
			}

		} catch (ParserConfigurationException e) {
			logger.warning(this.getClass().getName()+".modelToXML: Failed -> \n" 
					+e.getMessage());
			new AREAsapiException(e.getMessage());
			return null;
		}catch (TransformerException e1) {
			logger.warning(this.getClass().getName()+".modelToXML: Failed -> \n"
					+e1.getMessage());
			new AREAsapiException(e1.getMessage());
			return null;
		}

	}

	private void printFile (File modelFile){
		try {

			FileInputStream fis = new FileInputStream(modelFile);

			// Here BufferedInputStream is added for fast reading.
			BufferedInputStream bis = new BufferedInputStream(fis);
			DataInputStream dis = new DataInputStream(bis);

			// dis.available() returns 0 if the file does not have more lines.
			while (dis.available() != 0) {

				// this statement reads the line from the file and print it to
				// the console.
				System.out.println(dis.readLine());
			}

			// dispose all the resources after using them.
			fis.close();
			bis.close();
			dis.close();
			logger.fine(this.getClass().getName()+".printFile: OK\n");

		} catch (FileNotFoundException e) {
			logger.warning(this.getClass().getName()+".printFile: Failed -> \n"
					+e.getMessage());
			e.printStackTrace();
		} catch (IOException e1) {
			logger.warning(this.getClass().getName()+".printFile: Failed -> \n"
					+e1.getMessage());
			e1.printStackTrace();
		}
	}

	/**
	 * Deploys the model associated to the specified filename. The file 
	 * should be already available on the ARE file system.
	 * @param filename the filename of the model to be deployed
	 * @throws AREAsapiException if the specified filename is not found or
	 * cannot be deployed
	 */
	public void deployFile(final String filename) throws AREAsapiException {
		//stopModel outside of try catch to ensure that a current model is stopped any way.
		final IRuntimeModel currentRuntimeModel
		= DeploymentManager.instance.getCurrentRuntimeModel();

		if(currentRuntimeModel != null)
			stopModel();

		try {
			AstericsModelExecutionThreadPool.instance
			.execAndWaitOnModelExecutorLifecycleThread(new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					
					//try{
					synchronized (this){
						java.net.URI uri=ResourceRegistry.getInstance().getResource(filename,RES_TYPE.MODEL);

						DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
						DocumentBuilder builder = factory.newDocumentBuilder();
						synchronized (builder) {


							//Document doc = builder.parse(new File(xmlFile));
							Document doc = builder.parse(uri.toURL().openStream());
							DOMSource domSource = new DOMSource(doc);
							StringWriter writer = new StringWriter();
							StreamResult result = new StreamResult(writer);
							TransformerFactory tf = TransformerFactory.newInstance();
							Transformer transformer = tf.newTransformer();
							transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-16");
							transformer.transform(domSource, result);
							String modelInString = writer.toString();
							//calling the asapi function with a string representation of the model
							deployModel(modelInString);

							// logger.fine(this.getClass().getName()+"." + "deployFile: OK\n");
							AstericsErrorHandling.instance.getLogger().info("Deployed Model "+uri+" !");
						}
					}
					return null;
				}
			});

		} catch (AREAsapiException e1) {
			logger.warning(this.getClass().getName()+"." +
					"deployFile: Failed to deploy file -> \n"
					+e1.getMessage());
			throw e1;
		} catch (SAXException e3) {
			logger.warning(this.getClass().getName()+"." +
					"deployFile: Failed to deploy file -> \n"
					+e3.getMessage());
			throw (new AREAsapiException(e3.getMessage()));
		} catch (IOException e4) {
			logger.warning(this.getClass().getName()+"." +
					"deployFile: Failed to deploy file -> \n"
					+e4.getMessage());
			throw (new AREAsapiException(e4.getMessage()));
		} catch (ParserConfigurationException e5) {
			logger.warning(this.getClass().getName()+"." +
					"deployFile: Failed to deploy file -> \n"
					+e5.getMessage());
			throw (new AREAsapiException(e5.getMessage()));
		} catch (TransformerConfigurationException e6) {
			logger.warning(this.getClass().getName()+"." +
					"deployFile: Failed to deploy file -> \n"
					+e6.getMessage());
			throw (new AREAsapiException(e6.getMessage()));
		} catch (TransformerException e7) {
			logger.warning(this.getClass().getName()+"." +
					"deployFile: Failed to deploy file -> \n"
					+e7.getMessage());
			throw (new AREAsapiException(e7.getMessage()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw (new AREAsapiException(e.getMessage()));
		}
	}



	/**
	 * Deletes the file of the model specified by the filename parameter 
	 * @param filename the name of the file to be deleted
	 * @return true if the file was successfully deleted or false otherwise
	 * @throws AREAsapiException if the file could not be found or failed to be deleted
	 */
	public boolean deleteModelFile (String filename) throws AREAsapiException
	{
		filename = ResourceRegistry.MODELS_FOLDER + "/" + filename;
		String ex="";
		StringTokenizer tkz = new StringTokenizer (filename,".");
		while(tkz.hasMoreElements())
		{
			ex=tkz.nextToken();
		}

		if (!ex.equals("xml") && !ex.equals("acs"))
		{
			logger.warning(this.getClass().getName()+".deleteModelFile: " +
					"Unsupported file extension: " + ex);
//			throw new AREAsapiException(
//					"Unsupported file extension: " + ex);
			return false;
		}

		File f = new File(filename);

		// Make sure the file or directory exists and isn't write protected
		if (!f.exists())
		{
			logger.warning(this.getClass().getName()+".deleteModelFile: " +
					"no such file or directory: " + filename);
			throw new AREAsapiException(
					"deleteModelFile: no such file or directory: " + filename);
		}

		if (!f.canWrite())
		{
			logger.warning(this.getClass().getName()+".deleteModelFile: " +
					"file "+filename+" write protected\n");
			throw new AREAsapiException("Delete: write protected: "
					+ filename);
		}

		// Attempt to delete it
		if(f.delete())
		{
			logger.fine(this.getClass().getName()+".deleteModelFile: OK\n");
			return true;
		}
		else
		{
			logger.warning(this.getClass().getName()+".deleteModelFile: " +
					"Failed to delete file "+filename);
			return false;
		}

	}


	/**
	 * Returns a list with all stored models (all models in the directory MODELS_FOLDER 
	 * except default_model.xml)
	 * 
	 * @return a list with all stored models
	 * @throws AREAsapiException if MODELS_FOLDER directory could not be found
	 */
	public String[] listAllStoredModels() throws AREAsapiException
	{
		/*
		ArrayList <String> fileNames = new ArrayList<String> ();
		File dir = new File(MODELS_FOLDER+"/");

		String[] children = dir.list();
		if (children == null) {
			logger.warning(this.getClass().getName()+".listAllStoredModels: " +
					"could not find models directory\n");
			throw new AREAsapiException(
					"could not find models directory!");
		} else {
			for (int i=0; i<children.length; i++) {
				// Get filename of file or directory
				String filename = children[i];
				if (!filename.equals("model.xml")&& 
						!filename.equals("default_model.xml"))
					fileNames.add(filename);
			}
		}
		String[] res = new String[fileNames.size()];
		for (int i=0; i<res.length; i++)
		{
			res[i] = fileNames.get(i);
		}
		return res;

		*/
		/*
		List<String> res = new ArrayList<String>(); 
			List<String> nextDir = new ArrayList<String>(); //Directories
			nextDir.add(ResourceRegistry.MODELS_FOLDER);	
			//nextDir.add("data/sounds");	
			
			try 
			{
				while(nextDir.size() > 0) 
				{
					File pathName = new File(nextDir.get(0)); 
					String[] fileNames = pathName.list();  // lists all files in the directory
	
					for(int i = 0; i < fileNames.length; i++) 
					{ 
						File f = new File(pathName.getPath(), fileNames[i]); // getPath converts abstract path to path in String, 
						// constructor creates new File object with fileName name   
						if (f.isDirectory()) 
						{  
							nextDir.add(f.getPath()); 
						} 
						else 
						{
							if (f.getPath().toLowerCase().endsWith(".acs")) 
									res.add(f.getPath().substring(ResourceRegistry.MODELS_FOLDER.length()+1));
						}
					} 
					nextDir.remove(0); 
				} 
			}
			catch (Exception e) {System.out.println ("could not find directories for model files !");}

		
		String[] res2 = new String[res.size()];
		for (int i=0; i<res2.length; i++)
		{
			res2[i] = res.get(i);
		}
		
		return res2;		
		*/
		
		List<URI> storedModelList=ResourceRegistry.getInstance().getModelList(true);
		return ResourceRegistry.toStringArray(storedModelList);		
	}


	/**
	 * Stores the XML model specified by the string parameter in the file specified by the filename parameter 
	 * 
	 * @param modelInXML the XML model as a String 
	 * @param filename the name of the file the model is to be stored
	 * @throws AREAsapiException if the file cannot be created or if the model
	 * cannot be stored
	 */
	public void storeModel(String modelInXML, String filename)
			throws AREAsapiException {
		//First check if the model is a valid XML model

		File fileName = new File(ResourceRegistry.MODELS_FOLDER+"/"+filename);
		File modelsDir = new File(ResourceRegistry.MODELS_FOLDER);
		if (!fileName.exists())
		{
			try {
				modelsDir.mkdir();
				fileName.createNewFile();
			} catch (IOException e) {
				logger.warning(this.getClass().getName()+".storeModel: " +
						"The file or directory could not be created\n");
			}
		}
		try {
			InputStream is = new ByteArrayInputStream(modelInXML.getBytes("UTF-16"));

			synchronized (DefaultDeploymentModelParser.instance){
				DefaultDeploymentModelParser.instance.parseModel(is);

				//Convert the string to a byte array.
				String s = modelInXML;
				byte data[] = s.getBytes();
				BufferedWriter c = new BufferedWriter(new OutputStreamWriter
						(new FileOutputStream(fileName),"UTF-16"));

				for (int i=0; i<data.length; i++)
					c.write(data[i]);

				if (c != null) {
					c.flush();
					c.close();
				}
			}
		}  catch (IOException e) {
			String errorMsg="Failed to store model -> \n"+e.getMessage();
			AstericsErrorHandling.instance.reportError(null, errorMsg);
			throw (new AREAsapiException(errorMsg));
		} catch (ParseException e) {
			String errorMsg="Failed to parse model, maybe model version not in sync with compononent descriptors -> \n"+e.getMessage();
			AstericsErrorHandling.instance.reportError(null, errorMsg);
			throw (new AREAsapiException(errorMsg));
		} catch (BundleManagementException e) {
			String errorMsg="Failed to install model components -> \n"+e.getMessage();
			AstericsErrorHandling.instance.reportError(null, errorMsg);
			throw (new AREAsapiException(errorMsg));
		}
	}



	/**
	 * Returns the log file as a string.
	 * @return the log file as a string.
	 */
	public String getLogFile()
	{
		StringBuffer logFile = new StringBuffer();
		try {
			File file = new File("asterics_logger.log");
			if(!file.exists())
				logger.warning(this.getClass().getName()+".getLogFile: " +
						"Failed to create file asterics_logger.log");

			FileInputStream fileInputStream = new FileInputStream(file);
			BufferedInputStream bufferedInputStream = new 
					BufferedInputStream(fileInputStream);
			DataInputStream dataInputStream = new 
					DataInputStream(bufferedInputStream);
			while (dataInputStream.available() != 0) 
			{
				logFile.append(dataInputStream.readLine().toString()+"\n");
			}
			fileInputStream.close();
			bufferedInputStream.close();
			dataInputStream.close();

		} catch (FileNotFoundException e) {
			logger.warning(this.getClass().getName()+".getLogFile: Failed -> \n" 
					+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.warning(this.getClass().getName()+".getLogFile: Failed -> \n" 
					+e.getMessage());
			e.printStackTrace();
		}
		return logFile.toString();
	}


	/**
	 * It is called on startup by the middleware in order to autostart
	 * a default model without the need of pressing deploy and start model 
	 * first.
	 * @param startModel TODO
	 * @throws AREAsapiException 
	 */
	public void autostart(String startModel) throws AREAsapiException {

		if(startModel== null || startModel.equals("")) {
			try{
				//try to find autostart model
				//First look for a model file names autostart.acs
				File autostartModel=ResourceRegistry.toFile(ResourceRegistry.getInstance().getResource(AUTO_START_MODEL, RES_TYPE.MODEL));
				if(autostartModel.exists()) {
					startModel=autostartModel.getPath();
				} else {
					//If there is no dedicated autostart model either use the only one existing or throw an error message
					List<URI> models=ResourceRegistry.getInstance().getModelList(false);
					if(models.size()==1) {
						startModel=ResourceRegistry.toString(models.get(0));
					} else {
						throw new AREAsapiException("No model found for autostart. To define autostart model, either\n\ncreate model "+ResourceRegistry.MODELS_FOLDER+"autostart.acs or\nprovide model name as command line argument or\nopen model manually in the ARE GUI.");
					}
				}
			}catch (URISyntaxException e) {
				throw new AREAsapiException("Error during autostart of model:\n"+e.getMessage()+"\nTry to open model manually in the ARE GUI.");
			}
		}
		deployFile (startModel);
		runModel();
	}


	public List<String> getRuntimePropertyList(String componentID, String key) 
			throws AREAsapiException 
			{
		Collection<IRuntimeComponentInstance> componentInstances = DeploymentManager.instance.getComponentRuntimeInstances();
		List<String> list = new ArrayList<String>();
		for (IRuntimeComponentInstance ci : componentInstances)
		{
			String id = DeploymentManager.instance.getIRuntimeComponentInstanceIDFromIRuntimeComponentInstance(ci);
			if (id.equals(componentID))
			{
				list = ci.getRuntimePropertyList(key);
				return list;
			}
		}
		return list;
			}

	/**
	 * Helper method to convert an InputStream of an XML-file to an XML String object.
	 * @param inputStream
	 * @return
	 */
	private String convertXMLFileToString(InputStream inputStream) 
	{ 
		try{ 
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance(); 
			org.w3c.dom.Document doc = documentBuilderFactory.newDocumentBuilder().parse(inputStream); 
			StringWriter stw = new StringWriter(); 
			Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
			serializer.transform(new DOMSource(doc), new StreamResult(stw)); 
			return stw.toString(); 
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
		return null; 
	}

}
