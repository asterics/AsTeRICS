package eu.asterics.mw.are;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.print.attribute.standard.Severity;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceRegistration;

import eu.asterics.mw.are.exceptions.BundleManagementException;
import eu.asterics.mw.are.exceptions.ParseException;
import eu.asterics.mw.are.parsers.DefaultBundleModelParser;
import eu.asterics.mw.are.parsers.DefaultDeploymentModelParser;
import eu.asterics.mw.are.parsers.ModelValidator;
import eu.asterics.mw.model.bundle.IComponentType;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.IAREEventListener;
import eu.asterics.mw.services.ResourceRegistry;
import eu.asterics.mw.services.ResourceRegistry.RES_TYPE;
import eu.asterics.mw.utils.OSUtils;



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
 * Handles installation & uninstallation of bundles. In our case, we are
 * interested only when a bundle becomes RESOLVED or UNRESOLVED, in which case
 * we check if it contains ASTERICS components or not. If it does, then we
 * register the included components in the {@link ComponentRepository} and also
 * we register services in the OSGi repository accordingly.
 * 
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 *         Date: Aug 20, 2010
 *         Time: 12:03:20 PM
 */


public class BundleManager implements BundleListener, FrameworkListener
{
	private static final String SERVICES_FILES_DELIM = ";";
	static String PROFILE_LOCATION=new File(System.getProperty("osgi.configuration.area",ResourceRegistry.PROFILE_FOLDER)).getName();
	public static String LOADER_COMPONENTLIST_LOCATION = "loader_componentlist.ini";
	static String SERVICES_FILES=System.getProperty("eu.asterics.ARE.ServicesFiles", "services.ini");

	final int MODE_DEFAULT = 0;
	final int MODE_GET_ALL_COMPONENTS = 1;
	
	private static Logger logger = null;

	private final BundleContext bundleContext;

	private ModelValidator modelValidator;

	private static ComponentRepository componentRepository = ComponentRepository.instance;


	private static Map<Bundle,Set<IComponentType>> bundlesToComponentTypesMap
	= new HashMap<Bundle, Set<IComponentType>>();
	
	private static Map<String, Bundle> componentTypeIDToBundle
	= new HashMap<String, Bundle>();
	
	private static Map<String, String> componentTypeIDToJarName=new HashMap<String, String>();


	/**
	 * This is the default constructor and is automatically used by the {@link DeploymentManager} in OSGi mode.
	 * @param bundleContext
	 */
	public BundleManager(final BundleContext bundleContext)
	{
		super();
		this.bundleContext = bundleContext;
		this.modelValidator=new ModelValidator(bundleContext);
	
		logger = AstericsErrorHandling.instance.getLogger();
	}
	
	/**
	 * This contructor is meant for use in none OSGi mode (without BundleContext instance).
	 * @param modelValidator
	 */
	public BundleManager(ModelValidator modelValidator) {
		super();
		this.modelValidator=modelValidator;
		this.bundleContext=null;
		logger = AstericsErrorHandling.instance.getLogger();
	}

	public void start()
	{
		try {
			createComponentListCache();
		} catch (IOException | ParseException | URISyntaxException e1) {
			// TODO Auto-generated catch block
			AstericsErrorHandling.instance.reportError(null, "Could not create cache for Asterics components:\n"+e1.getMessage());
		}

		installServices();
	}

	public void stop()
	{
		uninstall();
	}

	/**
	 * This method is called when remove component is called from ACS.
	 * We check if the set of component type that contains the component
	 * type in the parameter is empty. In such a case we also deactivate 
	 * the OSGi bundle.
	 * @param componentType
	 * @throws BundleException 
	 * @throws BundleManagementException 
	 */
	public static void stopBundleComponent (IComponentType componentType) 
			throws BundleException, BundleManagementException
			{
		Collection<Set<IComponentType>> csets = 
				bundlesToComponentTypesMap.values();

		for (Set<IComponentType> cset : csets)
		{
			//we found the set that contains the removed component type
			if (cset.contains(componentType) && cset.size()==1)
			{ 
				Set<Bundle> bundles = bundlesToComponentTypesMap.keySet();
				for (Bundle bundle : bundles)
				{
					//We found the bundle that contains the set that contains 
					//the component type
					if (bundlesToComponentTypesMap.get(bundle).equals(cset))
					{
						//componentRepository.
						//uninstall(componentType);

						//bundleRepository.uninstall(componentType);
						bundle.stop();
						logger.fine(BundleManager.class.getName()+
								".stopBundleComponent: Registering bundle " +
								bundle);

						return;
					}
				}

			}
		}	
	}

	public void bundleChanged(BundleEvent bundleEvent)
	{
		int eventType = bundleEvent.getType();
		final Bundle bundle = bundleEvent.getBundle();

		switch (eventType)
		{
		case BundleEvent.INSTALLED:
//			logger.fine(this.getClass().getName()+".bundleChanged: " 
//					+bundle+" installed \n");
			break;
		case BundleEvent.RESOLVED:
//			logger.fine(this.getClass().getName()+".bundleChanged: " 
//					+bundle+" resolved \n");
			// test for AsTeRICS component metadata
//			if(checkForAstericsMetadata(bundle))
//			{
//				registerBundle(bundle);
//				logger.fine(this.getClass().getName()+".bundleChanged: " 
//						+bundle+" registered \n");
//			}
			break;
		case BundleEvent.UNRESOLVED:
			//logger.fine(this.getClass().getName()+".bundleChanged: " 
					//+bundle+" unresolved \n");
			// test for AsTeRICS component metadata
			unregisterBundle(bundle);
			//logger.fine(this.getClass().getName()+".bundleChanged: " 
					//+bundle+" unregistered \n");
			break;
		case BundleEvent.UNINSTALLED:
			//logger.fine(this.getClass().getName()+".bundleChanged: " 
					//+bundle+" uninstalled \n");
			break;
		default:
			//System.out.println (bundleEvent.getType());
			break;
		}
	}

	private static final String COMPONENTLIST_DELIM = ";";

	/**
	 * Checks if the specified bundle contains ASTERICS component(s) or not. The
	 * test is based on checking if the {@link DefaultBundleModelParser.BUNDLE_DESCRIPTOR_RELATIVE_URI}
	 * file exists or not (in the root of the enclosing JAR).
	 *
	 * @param bundle the bundle to be tested
	 * @return true if the specified bundle contains ASTERICS component(s),
	 * false otherwise
	 */
	public boolean checkForAstericsMetadata(final Bundle bundle)
	{
		final URL url = bundle.getResource(DefaultBundleModelParser.BUNDLE_DESCRIPTOR_RELATIVE_URI);

		return checkForAstericsMetadata(url,bundle.getSymbolicName());
	}

	/**
	 * Checks if the specified bundle contains ASTERICS component(s) or not. The
	 * test is based on checking if the {@link DefaultBundleModelParser.BUNDLE_DESCRIPTOR_RELATIVE_URI}
	 * file exists or not (in the root of the enclosing JAR). 
	 * 
	 * @param bundleDescriptorUrl the whole url to the bundle descriptor within the jar.
	 * @param symbolicName a symbolic name (e.g.) component name used for verbose logging.
	 * @return
	 */
	public boolean checkForAstericsMetadata(URL bundleDescriptorUrl, String symbolicName) {
		if(bundleDescriptorUrl==null) {
			return false;
		}
		
		try(InputStream bundleDescriptorInputStream=bundleDescriptorUrl.openStream())
		{
			return modelValidator.isValidBundleDescriptor(bundleDescriptorInputStream);
		}
		catch (IOException  ioe) {
			//Don't log because if it does not exist we don't expect a component. This indicates that it is a jar file without an Asterics plugin.			
		}catch(ParseException ioe)
		{
			//If there is a ParseException then we should log it, because there should be a valid plugin
			logger.warning(getClass().getName()+
					".checkForAstericsMetadata: validation error for file "+ 
					bundleDescriptorUrl +", bundle "+ 
					symbolicName+" -> \n"+
					ioe.getMessage());
					
		}
		return false;						
	}
	
	private Map <IComponentType, ServiceRegistration> serviceRegistrations
		= new HashMap<IComponentType, ServiceRegistration>();

	/**
	 * Return the OSGi Bundle object for a given componentTypeId. 
	 * @param componentTypeId
	 * @return
	 */
	public Bundle getBundleFromId(String componentTypeId)
	{
		return componentTypeIDToBundle.get(componentTypeId);
	}
	
	/**
	 * Returns a list of bundles that are installable for this platform the ARE is running on.
	 * This really tries to install the bundle via OSGi and checks if e.g. involved native libs are supported for this platform.
	 * After invoking the method the bundles are left installed.
	 * @return
	 */
	public List<Bundle> getInstallableBundleList() {
		List<Bundle> bundleList=new ArrayList<Bundle>();
		List<URI> componentJarURIs=ResourceRegistry.getInstance().getComponentJarList(false);
		for(URI componentJarURI : componentJarURIs) {
			Bundle b;
			try {
				b = installSingle(componentJarURI);
				bundleList.add(b);
			} catch (BundleException | IOException | ParseException | BundleManagementException e) {
				//If the installation of the bundle
				Throwable cause=e.getCause();
				String reason=(cause!=null && cause.getMessage() != null) ? ", Reason: "+cause.getMessage() : "";
				AstericsErrorHandling.instance.getLogger().warning("Cannot install bundle ("+e.getClass().getName()+"), skipping it: "+componentJarURI+reason);
			}
		}
		return bundleList;
	}
	
	
	/**
	 * Returns the jar name of the given componentTypeId as it was found in the componentlist cache.
	 * This is just the relative jar name, to create an absolute one use the {@link ResourceRegistry.getInstance().getResource()} method.
	 * @param componentTypeId
	 * @return
	 */
	public String getJarNameFromComponentTypeId(String componentTypeId) throws BundleManagementException {
		String jarName=componentTypeIDToJarName.get(componentTypeId);
		if(jarName==null) {
			throw new BundleManagementException("Could not find bundle jar name for componentTypeId <"+componentTypeId+">");
		}
		return jarName;
	}
	
	/**
	 * Returns the jar name URI for the given componentTypeId.
	 * @param componentTypeId
	 * @return
	 * @throws BundleManagementException
	 */
	public URI getJarNameURIFromComponentTypeId(String componentTypeId) throws BundleManagementException {
		String jarName=getJarNameFromComponentTypeId(componentTypeId);
		try {
			URI jarNameURI=ResourceRegistry.getInstance().getResource(jarName, RES_TYPE.JAR);
			return jarNameURI;
		} catch (URISyntaxException e) {
			throw new BundleManagementException(e.getMessage());
		}				
	}
	
	/**
	 * Returns the URI of the bundle_descritpor.xml file inside the component jar file.
	 * @param componentTypeId
	 * @return
	 * @throws BundleManagementException
	 */
	public URI getBundleDescriptorURIFromComponentTypeId(String componentTypeId) throws BundleManagementException {
		URI jarNameURI=getJarNameURIFromComponentTypeId(componentTypeId);
		URI bundleDescriptorURI=ResourceRegistry.toJarInternalURI(jarNameURI, DefaultBundleModelParser.BUNDLE_DESCRIPTOR_RELATIVE_URI);
		return bundleDescriptorURI;
	}	
	
	/**
	 * Returns a Set of component jar names of the currently installed components. 
	 * @return
	 */
	public Set<String> getInstalledComponentJarNames() {		
		final Set<IComponentType> componentTypeSet= componentRepository.getInstalledComponentTypes();
		HashSet<String> jarNameSet = new HashSet<String>();

		for(final IComponentType componentType : componentTypeSet) {
			String jarName;
			try {
				jarName = getJarNameFromComponentTypeId(componentType.getID());
				jarNameSet.add(jarName);
			} catch (BundleManagementException e) {
				// TODO Auto-generated catch block
				logger.warning(e.getMessage());
			}
		}

		return jarNameSet;
	}
	
	/**
	 * Loads the classes of the component and registers the given component in the {@link ComponentRepository}.
	 * @param bundle
	 * @throws IOException 
	 * @throws ParseException 
	 */
	private void registerBundle(final Bundle bundle) throws IOException, ParseException
	{
		final URL url = bundle.getResource(DefaultBundleModelParser.BUNDLE_DESCRIPTOR_RELATIVE_URI); 

		try
		{
			synchronized (DefaultBundleModelParser.instance) 
			{
			
				final Set<IComponentType> componentTypeSet
					= DefaultBundleModelParser.instance
						.parseModel(url.openStream());
			
				Set<IComponentType> successfullyLoadedComponentTypeSet=new LinkedHashSet<IComponentType>();
				for(final IComponentType componentType : componentTypeSet)
				{
					// register component's factory as a service
					final String componentCanonicalName = componentType.getCanonicalName();

					try
					{
						final Class clazz = bundle.loadClass(componentCanonicalName);
						final boolean isSingleton = componentType.isSingleton();
						componentRepository.setComponentFactory(componentCanonicalName,
								new DefaultComponentFactory(clazz, isSingleton));
						
						componentTypeIDToBundle.put(componentType.getID(), bundle);
						// install in global component repository
						ComponentRepository.instance.install(componentType);
						successfullyLoadedComponentTypeSet.add(componentType);
					}
					catch (ClassNotFoundException cnfe)
					{
						logger.warning(getClass().getName()+
								".registerBundle: Could not instantiate class " +
								"with name "+componentCanonicalName+", ignoring this component! -> \n"+ 
								cnfe.getMessage());
					}
				}
				bundlesToComponentTypesMap.put(bundle, successfullyLoadedComponentTypeSet);
			}
		}
		catch (IOException ioe)
		{
			logger.warning(this.getClass().getName()+".registerBundle: " +
					"Error while reading deployment metadata from " + 
					bundle + " -> \n" + ioe.getMessage());
			throw ioe;
		}
		catch (ParseException pe)
		{
			logger.warning(this.getClass().getName()+".registerBundle: " +
					"Parse error while reading deployment metadata from " + 
					bundle + " -> \n" + pe.getMessage());
			throw pe;
		}
	}

	/**
	 * Unregisters the given component in the {@link ComponentRepository}.
	 * @param bundle
	 */
	private void unregisterBundle(final Bundle bundle)
	{
		try
		{
			final Set<IComponentType> componentTypeSet
			= bundlesToComponentTypesMap.get(bundle);
			for(final IComponentType componentType : componentTypeSet)
			{
				ComponentRepository.instance.uninstall(componentType);

			}
		}
		catch (BundleManagementException bme)
		{
			logger.warning(this.getClass().getName()+".registerBundle: " +
					"Error while installing component type from bundle " + 
					bundle + " -> \n" + bme.getMessage());
			throw new RuntimeException(bme);
		}
	}

	@Override
	public void frameworkEvent(FrameworkEvent e) {
		//System.out.println ("Framework said: "+e.getType()+" from "+e.getBundle().getSymbolicName());
	}

	/**
	 * Shows bundle installation error message.
	 * @param bundle
	 * @param name
	 * @param reason
	 */
	public void showBundleInstallErrorMessage(Bundle bundle, String name, String reason)
	{
		AstericsErrorHandling.instance.reportError(null, createBundleInstallErrorMessage(bundle, name, reason));
	}
	
	/**
	 * Creates an error message string as a result of a bundle installation.
	 * @param bundle
	 * @param name
	 * @param reason
	 * @return
	 */
	private String createBundleInstallErrorMessage(Bundle bundle, String name, String reason)
	{		
		String optReason=reason!=null? "\nReason: "+reason : "";
		String errorMsg="Deployment Error: Couldn't start bundle "+name+optReason;
		if (bundle!=null)
		{
			// Log the exception and continue
			errorMsg="Deployment Error: Couldn't start " + bundle.getBundleId()+ " from location\n"+bundle.getLocation()+optReason;
		}
		return errorMsg;
	}
	
	/**
	 * Installs the component with the given componentTypeId.
	 * The corresponding component jar is searched and installed here.
	 * @param cTypeID
	 * @throws BundleManagementException
	 */
	public void installSingle (String cTypeID) throws BundleManagementException
	{
		Bundle bundle=null;
		try {
			URI jarURI=ResourceRegistry.getInstance().getResource(componentTypeIDToJarName.get(cTypeID), RES_TYPE.JAR);
			bundle=installSingle(jarURI);
		}
		catch (Throwable t) 
		{
			//showBundleInstallErrorMessage(bundle,cTypeID,t.getMessage());
			throw new BundleManagementException(createBundleInstallErrorMessage(bundle, cTypeID,t.getMessage()));
		}
	}
	
	/**
	 * Installs the bundle with the given jar URI.
	 * @param jarURI
	 * @return
	 * @throws MalformedURLException
	 * @throws BundleException
	 * @throws IOException
	 * @throws ParseException 
	 * @throws BundleManagementException 
	 */
	public Bundle installSingle(URI jarURI) throws MalformedURLException, BundleException, IOException, ParseException, BundleManagementException {
		logger.info("*** Installing bundle on-demand: "+jarURI);
		Bundle bundle = bundleContext.installBundle(jarURI.toString(),jarURI.toURL().openStream());	
		if(checkForAstericsMetadata(bundle)) {
			registerBundle(bundle);
		} else {
			throw new BundleManagementException("Could not find valid bundle descriptor in jarURI: "+jarURI);
		}
		return bundle;
	}
	
	
	
	/**
	 * Installs services and bundles at startup of the BundleManager.
	 */
	public void installServices() 
	{
		if(!ResourceRegistry.getInstance().isOSGIMode()) {
			logger.fine("OSGIMode=false --> Skipping services installation.");
			return;
		}
		
		String path;	
		Bundle bundle= null;

		//Hard code loading of services defined in services.ini and services-{os.name}.ini
		SERVICES_FILES="services.ini;services-"+OSUtils.getOsName()+".ini;"+SERVICES_FILES;
		logger.fine("Using the following .ini files to load services: "+SERVICES_FILES);
		
		//First load all services defined in the services-*.ini files
		for(String servicesFile : SERVICES_FILES.split(SERVICES_FILES_DELIM)) {
			try(InputStream serviceFileStream=ResourceRegistry.getInstance().getResourceInputStream(servicesFile, RES_TYPE.PROFILE);					
				BufferedReader in = new BufferedReader(new InputStreamReader(serviceFileStream))) {
				
				logger.fine("Loading services from file: "+servicesFile);
				while ( (path = in.readLine()) != null)
				{
					path=path.trim();
					//Skipping comments
					if(path.startsWith("#") || path.isEmpty() || !path.endsWith(".jar")) {
						continue;
					}
					try {
						URI jarURI = ResourceRegistry.getInstance().getResource(path, RES_TYPE.JAR);
						//bundle=installSingle(jarURI);
						logger.info("Installing service: "+jarURI);
						bundle = bundleContext.installBundle(jarURI.toString(),jarURI.toURL().openStream());							
						bundle.start();
						bundle=null;
					} catch (URISyntaxException | BundleException | IOException e) {
						showBundleInstallErrorMessage(bundle, path, e.getMessage());
					}
				}
		    } catch (MalformedURLException | URISyntaxException e) {
				String errorMsg="Could not create URI/URL for services file: "+servicesFile;
				AstericsErrorHandling.instance.reportError(null, errorMsg);
			} catch (IOException e) {
				String errorMsg="Could not read from services file: "+servicesFile;
				AstericsErrorHandling.instance.reportError(null, errorMsg);
			}
		}
		notifyAREEventListeners (AREEvent.POST_BUNDLES_EVENT);
	}
	
	/**
	 * Creates the file loader_componentlist.ini, which is actually a cache and maps Asterics components to the associated component jar. 
	 * This is necessary because, unlike in OSGi normally, there is no 1:1 mapping between a component and the jar name. In Asterics a several components may be bundled
	 * within one jar file.
	 * This method is a wrapper for @link {@link BundleManager.generateComponentListCache} and only generates the cache if it does not already exist.
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ParseException
	 * @throws URISyntaxException 
	 */
	public void createComponentListCache() throws MalformedURLException, IOException, ParseException, URISyntaxException {
		File componentList=ResourceRegistry.toFile(ResourceRegistry.getInstance().getResource(LOADER_COMPONENTLIST_LOCATION, RES_TYPE.PROFILE));
		
		//OSGIMode=true: only generate list if it does not exist.
		//OSGIMode=false: Always generate cache list. This is also needed to force parsing the bundle_descriptors and installing ComponentType instances in ComponentRepository.
		if(!componentList.exists()||!ResourceRegistry.getInstance().isOSGIMode()) {
			generateComponentListCache(componentList);
		}
		readComponentListCache(componentList.toURI().toURL());
	}
	
	/**
	 * Actually generates the loader_componentlist.ini file, which is actually a cache and maps Asterics components to the associated component jar. 
	 * This is necessary because, unlike in OSGi normally, there is no 1:1 mapping between a component and the jar name. In Asterics a several components may be bundled
	 * within one jar file. 
	 * 
	 * @param componentList
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ParseException
	 */
	public void generateComponentListCache(File componentList) throws MalformedURLException, IOException, ParseException {
		//The component list does not exist, so we create it.
		List<URI> componentJarURIs=ResourceRegistry.getInstance().getComponentJarList(false);

		try(BufferedWriter writer=new BufferedWriter(new FileWriter(componentList))) {
			for(URI componentJarURI : componentJarURIs) {
				String inputFilePath = "jar:file://" + componentJarURI.getPath() + "!/bundle_descriptor.xml" ;

				URL bundleDescriptor=new URL(inputFilePath);
				URI relativeURI=ResourceRegistry.getInstance().toRelative(componentJarURI);

				Set<IComponentType> componentTypeSet=DefaultBundleModelParser.instance.parseModel(bundleDescriptor.openStream());
				if(componentTypeSet.size()>=1) {
					StringBuffer line=new StringBuffer(relativeURI.getPath());
					for(IComponentType componentType : componentTypeSet) {											
						line.append(COMPONENTLIST_DELIM);
						line.append(" ");
						line.append(componentType.getID());
						
						if(!ResourceRegistry.getInstance().isOSGIMode()) {
							//Also install ComponentType in ComponentRepository, to support non-osgi mode. 
							//This is actually not a good location for doing it because the main purpose of this method is to generate the cache, but
							//here we have all we need.
							ComponentRepository.instance.install(componentType);
						}
					}
					writer.write(line.toString());
					writer.newLine();
				}
			}
			writer.flush();
			writer.close();
		}
	}
	
	/**
	 * Reads the loader_componentlist.ini file and initializes an internal Map to quickly find the jarname of a component.
	 * @param componentListCache
	 * @throws IOException
	 */
	public void readComponentListCache(URL componentListCache) throws IOException {
		try(BufferedReader in=new BufferedReader(new InputStreamReader(componentListCache.openStream()))) {
			String actLine="";
			componentTypeIDToJarName.clear();
			while ( (actLine = in.readLine()) != null)
			{
				StringTokenizer tokenizer=new StringTokenizer(actLine, COMPONENTLIST_DELIM);
				if(tokenizer.countTokens()>=2) {
					String jarName=tokenizer.nextToken().trim();
					while(tokenizer.hasMoreTokens()) {
						componentTypeIDToJarName.put(tokenizer.nextToken().trim(), jarName);
					}
				}
			}
			in.close();
		}
	}

	/**
	 * Checks if the specified bundle URI contains an OSGi service. The
	 * test is based on checking if the {@link DefaultBundleModelParser.BUNDLE_DESCRIPTOR_RELATIVE_URI}
	 * file does not exist and if a MANIFEST entry 'Bundle-Name' can be found. 
	 * @param serviceBundleURI The URI to the jar
	 * @return
	 */
	public boolean checkForServiceBundle(URI serviceBundleURI) {
		try {		
			//String inputFilePath = "jar:file://" + serviceBundleURI.getPath() + "!/bundle_descriptor.xml" ;
			URI jarInternalURI=ResourceRegistry.toJarInternalURI(serviceBundleURI, DefaultBundleModelParser.BUNDLE_DESCRIPTOR_RELATIVE_URI);

			if(checkForAstericsMetadata(jarInternalURI.toURL(), jarInternalURI.toString())) {
				//if it has a bundle_descirptor it can only be a component (plugin).
				return false;
			}

			//inputFilePath = "jar:file://" + serviceBundleURI.getPath() + "!/META-INF/MANIFEST.MF" ;
			jarInternalURI=ResourceRegistry.toJarInternalURI(serviceBundleURI, "/META-INF/MANIFEST.MF");
			
		    try(BufferedReader in=new BufferedReader(new InputStreamReader(jarInternalURI.toURL().openStream()))) {
		    	String actLine="";				
				while ( (actLine = in.readLine()) != null) {
					//Bundle-Name is specific for OSGi bundles. But actually we don't know if it as an OSGi service or just a plugin but by check the bundle_descriptor above
					//we can assume that it is a service.
					if(actLine.startsWith("Bundle-Name:")) {
						return true;
					}
				}
		    } catch (IOException e) {		    	
			}
		    //If we get here, either an exception was thrown, so the FILE is not contained or we could not find OSGi MANIFEST info.
			return false;
		} catch (MalformedURLException e) {
		}
		return false;
	}

	/**
	 * Uninstalls all bundles.
	 */
	public void uninstall() 
	{
			Bundle[] bundles = bundleContext.getBundles();
			for (Bundle b : bundles)
			{
				try {
					//uninstall OSGI stuff
					b.uninstall();
					//and unregister it from internal maps
					unregisterBundle(b);
				} catch (BundleException e) {
					logger.warning(this.getClass().getName()+"." +
							"Error while uninstalling bundle!"+b.getSymbolicName());
				}
			}
	}
	
	private void notifyAREEventListeners(AREEvent areEvent) 
	{
		ArrayList<IAREEventListener> listeners = 
				AREServices.instance.getAREEventListners();

		switch (areEvent)
		{
		case PRE_DEPLOY_EVENT:
			for (IAREEventListener listener : listeners)
			{
				listener.preDeployModel();
			}
			break;
		case POST_DEPLOY_EVENT:
			for (IAREEventListener listener : listeners)
			{
				listener.postDeployModel();
			}
			break;
		case PRE_START_EVENT:
			for (IAREEventListener listener : listeners)
			{
				listener.preStartModel();
			}
			break;
		case POST_START_EVENT:
			for (IAREEventListener listener : listeners)
			{
				listener.postStartModel();
			}
			break;
		case PRE_STOP_EVENT:
			for (IAREEventListener listener : listeners)
			{
				listener.preStopModel();
			}
			break;
		case POST_STOP_EVENT:
			for (IAREEventListener listener : listeners)
			{
				listener.postStopModel();
			}
			break;
		case PRE_PAUSE_EVENT:
			for (IAREEventListener listener : listeners)
			{
				listener.prePauseModel();
			}
			break;
		case POST_PAUSE_EVENT:
			for (IAREEventListener listener : listeners)
			{
				listener.postPauseModel();
			}
			break;
		case PRE_RESUME_EVENT:
			for (IAREEventListener listener : listeners)
			{
				listener.preResumeModel();
			}
			break;
		case POST_RESUME_EVENT:
			for (IAREEventListener listener : listeners)
			{
				listener.postResumeModel();
			}
			break;
		case PRE_BUNDLES_EVENT:
			for (IAREEventListener listener : listeners)
			{
				listener.preBundlesInstalled();
			}
			break;
		case POST_BUNDLES_EVENT:
			for (IAREEventListener listener : listeners)
			{
				listener.postBundlesInstalled();
			}
			break;
		default:
			break;
		}

	}

}

