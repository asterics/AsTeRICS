package eu.asterics.mw.are;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Hex;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceRegistration;

import eu.asterics.mw.are.exceptions.BundleManagementException;
import eu.asterics.mw.are.exceptions.DeploymentException;
import eu.asterics.mw.are.exceptions.ParseException;
import eu.asterics.mw.are.parsers.DefaultBundleModelParser;
import eu.asterics.mw.are.parsers.ModelValidator;
import eu.asterics.mw.model.bundle.IComponentType;
import eu.asterics.mw.model.deployment.IRuntimeModel;
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
 * Handles installation & uninstallation of bundles. In our case, we are interested only when a bundle becomes RESOLVED or UNRESOLVED, in which case we check if
 * it contains ASTERICS components or not. If it does, then we register the included components in the {@link ComponentRepository} and also we register services
 * in the OSGi repository accordingly.
 * 
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy] Date: Aug 20, 2010 Time: 12:03:20 PM
 */

public class BundleManager implements BundleListener, FrameworkListener {
    private static final String ARE_GENERATE_CACHES_FROM_INSTALLABLE_BUNDLES_PROP_KEY = "ARE.generate.caches.from.installable.bundles";
    public static final String DEFAULT_COMPONENT_COLLECTION_ABD = "componentCollections/defaultComponentCollection.abd";
    public static final String MESSAGE_DIGEST_ALGORITHM = "MD5";
    private static final String CACHING_PLUGIN_JARS_HASH_TXT = "caching/pluginJarsHash.txt";
    private static final String SERVICES_FILES_DELIM = ";";
    static String PROFILE_LOCATION = new File(System.getProperty("osgi.configuration.area", ResourceRegistry.PROFILE_FOLDER)).getName();
    public static String LOADER_COMPONENTLIST_LOCATION = "caching/loader_componentlist.ini";
    static String SERVICES_FILES = System.getProperty("eu.asterics.ARE.ServicesFiles", "services.ini");

    // define URIs for diverse cache files
    public static URI COMPONENT_COLLECTION_CACHE_FILE_URI;
    public static URI LOADER_COMPONENTLIST_CACHE_FILE_URI;

    final int MODE_DEFAULT = 0;
    final int MODE_GET_ALL_COMPONENTS = 1;

    private static Logger logger = null;

    private final BundleContext bundleContext;

    private ModelValidator modelValidator;

    // Reference to ComponentRepository which stores references to installed IComponentType instances from a given componentType
    private static ComponentRepository componentRepository = ComponentRepository.instance;

    // Maps to find osgi bundle according to a given componentTypeId
    private static Map<Bundle, Set<IComponentType>> bundlesToComponentTypesMap = new HashMap<Bundle, Set<IComponentType>>();
    private static Map<String, Bundle> componentTypeIDToBundle = new HashMap<String, Bundle>();

    // Maps to find the jar name and bundle descriptor string according to a given componentTypeId
    private static Map<String, String> componentTypeIDToJarName = new HashMap<String, String>();

    /**
     * This is the default constructor and is automatically used by the {@link DeploymentManager} in OSGi mode.
     * 
     * @param bundleContext
     */
    public BundleManager(final BundleContext bundleContext) {
        super();
        this.bundleContext = bundleContext;
        this.modelValidator = new ModelValidator(bundleContext);

        logger = AstericsErrorHandling.instance.getLogger();
    }

    /**
     * This contructor is meant for use in none OSGi mode (without BundleContext instance).
     * 
     * @param modelValidator
     */
    public BundleManager(ModelValidator modelValidator) {
        super();
        this.modelValidator = modelValidator;
        this.bundleContext = null;
        logger = AstericsErrorHandling.instance.getLogger();
    }

    /**
     * Initializes and starts the BundleManager. When in OSGI mode all installable bundles for this platform are tested. When in NON-OSGI mode all bundle jars
     * for this platform are used.
     */
    public void start() {
        logger.fine("BundleManager.start");
        // init properties of BundleManager
        AREProperties.instance.getProperty(ARE_GENERATE_CACHES_FROM_INSTALLABLE_BUNDLES_PROP_KEY, "false",
                "Enable/Disable if bundles should be tested for being installable on this platform via OSGI");

        installServices();

        try {
            COMPONENT_COLLECTION_CACHE_FILE_URI = ResourceRegistry.getInstance().getResource(DEFAULT_COMPONENT_COLLECTION_ABD, RES_TYPE.WEB_DOCUMENT_ROOT);
            LOADER_COMPONENTLIST_CACHE_FILE_URI = ResourceRegistry.getInstance().getResource(LOADER_COMPONENTLIST_LOCATION, RES_TYPE.TMP);

            if (ResourceRegistry.getInstance().isOSGIMode()) {
                String mdSum = generateMDSum();
                if (bundleChangeDetected(mdSum) || cacheFileMissing()) {
                    generateCacheFiles();
                }
                storeMDSum(mdSum);
            }
            initComponentTypeIDToJarNameMap();
            logger.fine("BundleManager initialization finished");
        } catch (IOException | ParseException | URISyntaxException | NoSuchAlgorithmException e1) {
            // TODO Auto-generated catch block
            AstericsErrorHandling.instance.reportError(null, "Could not create cache for Asterics components:\n" + e1.getMessage());
        }
    }

    /**
     * Initializes the internal maps either with caching (in OSGI mode) or without caching in NON-OSGI mode (used by APE).
     * 
     * @throws IOException
     * @throws URISyntaxException
     * @throws ParseException
     */
    private void initComponentTypeIDToJarNameMap() throws IOException, URISyntaxException, ParseException {
        if (ResourceRegistry.getInstance().isOSGIMode()) {
            readComponentListCache();
        } else {
            logger.fine("Not in OSGI Mode --> init maps without cache files.");
            componentTypeIDToJarName.clear();
            List<URI> jarNameURIList = ResourceRegistry.getInstance().getComponentJarList(false);
            for (URI jarNameURI : jarNameURIList) {
                URI bundleDescriptorURI = ResourceRegistry.toJarInternalURI(jarNameURI, DefaultBundleModelParser.BUNDLE_DESCRIPTOR_RELATIVE_URI);
                String bundleDescriptorAsXMLString = ResourceRegistry.getResourceContentAsString(bundleDescriptorURI.toURL().openStream());
                Set<IComponentType> componentTypeSet = DefaultBundleModelParser.instance.parseModelAsXMLString(bundleDescriptorAsXMLString);
                for (IComponentType componentType : componentTypeSet) {
                    componentTypeIDToJarName.put(componentType.getID(), ResourceRegistry.toString(ResourceRegistry.getInstance().toRelative(jarNameURI)));
                    ComponentRepository.instance.install(componentType);
                }
            }
            logger.fine("Found the following componentTypeIds: " + componentTypeIDToJarName.keySet().toString());
        }
    }

    /**
     * Checks one of the used cache files is misssing.
     * 
     * @return
     */
    private boolean cacheFileMissing() {
        logger.fine("Checking missing cache files");
        if (!ResourceRegistry.resourceExists(LOADER_COMPONENTLIST_CACHE_FILE_URI) || !ResourceRegistry.resourceExists(COMPONENT_COLLECTION_CACHE_FILE_URI)) {
            return true;
        }
        return false;
    }

    /**
     * Compares the given mdsum hex string and checks it for equality with a newly created message digest hash string of all plugin jars.
     * 
     * @param mdSum
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    private boolean bundleChangeDetected(String mdSum) throws URISyntaxException, IOException {
        logger.fine("Checking bundle change...");
        if (mdSum == null) {
            logger.warning("Given MD sum is null --> bundle change: true");
            return true;
        }

        String storedMDSum = ResourceRegistry.getInstance().getResourceContentAsString(CACHING_PLUGIN_JARS_HASH_TXT, RES_TYPE.TMP);
        if (mdSum.equals(storedMDSum)) {
            logger.fine("bundle change: false");
            return false;
        }

        logger.fine("bundle change: true");
        return true;
    }

    /**
     * Stores the given MD hash hex string to a file.
     * 
     * @param mdSum
     * @throws URISyntaxException
     * @throws IOException
     */
    private void storeMDSum(String mdSum) throws URISyntaxException, IOException {
        logger.fine("Storing MD sum...");

        if (mdSum == null) {
            logger.severe("Given message digest sum is null --> won't store it");
            return;
        }
        ResourceRegistry.getInstance().storeResource(mdSum.trim(), CACHING_PLUGIN_JARS_HASH_TXT, RES_TYPE.TMP);
    }

    /**
     * This method generates all needed cachde files.
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws ParseException
     * @throws URISyntaxException
     */
    private void generateCacheFiles() throws MalformedURLException, IOException, ParseException, URISyntaxException {
        logger.fine("Generating cache files/help files");
        long startTime = System.currentTimeMillis();
        // Before we can start, let's ensure the generation of cached files
        generateLoaderComponentListAndBundleDescriptorCacheFiles();
        logger.fine(MessageFormat.format("Generated cache files in {0} ms", (System.currentTimeMillis() - startTime)));
    }

    /**
     * Generates a message digest hex string of the bytes of all plugin jars. A plugin jar is defined as an osgi bundle containing a bundle_descriptor.xml file
     * in the root folder of the bundle.
     * 
     * @return
     * @throws NoSuchAlgorithmException
     * @throws MalformedURLException
     * @throws IOException
     */
    private String generateMDSum() throws NoSuchAlgorithmException, MalformedURLException, IOException {
        logger.fine("Generating MD sum of bundle jars using algorithm " + MESSAGE_DIGEST_ALGORITHM);
        long timeStart = System.currentTimeMillis();
        MessageDigest mdInstance = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM);
        List<URI> compJarList = ResourceRegistry.getInstance().getComponentJarList(false);

        for (URI compJarURI : compJarList) {
            try (DigestInputStream dis = new DigestInputStream(compJarURI.toURL().openStream(), mdInstance)) {
                // Just opening automatically updates the MessageDigest object.
                byte[] readBytes = new byte[1024];
                int numRead = 0;
                while ((numRead = dis.read(readBytes)) != -1) {
                    // Calling update is not necessary, as the DigestInputStream automatically calls this method when reading data from the input stream.
                    // mdInstance.update(readBytes,0,numRead);
                }
            }
        }
        byte[] mdOfJar = mdInstance.digest();
        long timeEnd = System.currentTimeMillis();
        String mdHexString = Hex.encodeHexString(mdOfJar);

        logger.fine(MessageFormat.format("Calculated message digest hash of all {0} bundle jars in {1} ms. MDHex: {2}", compJarList.size(), timeEnd - timeStart,
                mdHexString));
        return mdHexString;
    }

    public void stop() {
        uninstall();
    }

    /**
     * This method is called when remove component is called from ACS. We check if the set of component type that contains the component type in the parameter
     * is empty. In such a case we also deactivate the OSGi bundle.
     * 
     * @param componentType
     * @throws BundleException
     * @throws BundleManagementException
     */
    public static void stopBundleComponent(IComponentType componentType) throws BundleException, BundleManagementException {
        Collection<Set<IComponentType>> csets = bundlesToComponentTypesMap.values();

        for (Set<IComponentType> cset : csets) {
            // we found the set that contains the removed component type
            if (cset.contains(componentType) && cset.size() == 1) {
                Set<Bundle> bundles = bundlesToComponentTypesMap.keySet();
                for (Bundle bundle : bundles) {
                    // We found the bundle that contains the set that contains
                    // the component type
                    if (bundlesToComponentTypesMap.get(bundle).equals(cset)) {
                        // componentRepository.
                        // uninstall(componentType);

                        // bundleRepository.uninstall(componentType);
                        bundle.stop();
                        logger.fine(BundleManager.class.getName() + ".stopBundleComponent: Registering bundle " + bundle);

                        return;
                    }
                }

            }
        }
    }

    @Override
    public void bundleChanged(BundleEvent bundleEvent) {
        int eventType = bundleEvent.getType();
        final Bundle bundle = bundleEvent.getBundle();

        switch (eventType) {
        case BundleEvent.INSTALLED:
            // logger.fine(this.getClass().getName()+".bundleChanged: "
            // +bundle+" installed \n");
            break;
        case BundleEvent.RESOLVED:
            // logger.fine(this.getClass().getName()+".bundleChanged: "
            // +bundle+" resolved \n");
            // test for AsTeRICS component metadata
            // if(checkForAstericsMetadata(bundle))
            // {
            // registerBundle(bundle);
            // logger.fine(this.getClass().getName()+".bundleChanged: "
            // +bundle+" registered \n");
            // }
            break;
        case BundleEvent.UNRESOLVED:
            // logger.fine(this.getClass().getName()+".bundleChanged: "
            // +bundle+" unresolved \n");
            // test for AsTeRICS component metadata
            unregisterBundle(bundle);
            // logger.fine(this.getClass().getName()+".bundleChanged: "
            // +bundle+" unregistered \n");
            break;
        case BundleEvent.UNINSTALLED:
            // logger.fine(this.getClass().getName()+".bundleChanged: "
            // +bundle+" uninstalled \n");
            break;
        default:
            // System.out.println (bundleEvent.getType());
            break;
        }
    }

    private static final String COMPONENTLIST_DELIM = ";";

    /**
     * Checks if the specified bundle contains ASTERICS component(s) or not. The test is based on checking if the
     * {@link DefaultBundleModelParser.BUNDLE_DESCRIPTOR_RELATIVE_URI} file exists or not (in the root of the enclosing JAR).
     *
     * @param bundle
     *            the bundle to be tested
     * @return true if the specified bundle contains ASTERICS component(s), false otherwise
     */
    public boolean checkForAstericsMetadata(final Bundle bundle) {
        final URL url = bundle.getResource(DefaultBundleModelParser.BUNDLE_DESCRIPTOR_RELATIVE_URI);

        return checkForAstericsMetadata(url, bundle.getSymbolicName());
    }

    /**
     * Checks if the specified bundle contains ASTERICS component(s) or not. The test is based on checking if the
     * {@link DefaultBundleModelParser.BUNDLE_DESCRIPTOR_RELATIVE_URI} file exists or not (in the root of the enclosing JAR).
     * 
     * @param bundleDescriptorUrl
     *            the whole url to the bundle descriptor within the jar.
     * @param symbolicName
     *            a symbolic name (e.g.) component name used for verbose logging.
     * @return
     */
    public boolean checkForAstericsMetadata(URL bundleDescriptorUrl, String symbolicName) {
        if (bundleDescriptorUrl == null) {
            return false;
        }

        try {
            return ResourceRegistry.resourceExists(bundleDescriptorUrl.toURI());
        } catch (URISyntaxException e) {
            logger.warning("Could not check for Asterics metadata (bundle_descriptor.xml) due to invalid URI: " + bundleDescriptorUrl);
            return false;
        }
    }

    private Map<IComponentType, ServiceRegistration> serviceRegistrations = new HashMap<IComponentType, ServiceRegistration>();

    /**
     * Return the OSGi Bundle object for a given componentTypeId.
     * 
     * @param componentTypeId
     * @return
     */
    public Bundle getBundleFromId(String componentTypeId) {
        return componentTypeIDToBundle.get(componentTypeId);
    }

    /**
     * Returns a list of bundle descriptor URIs. The URIs are the jar internal URIs that point to the bundle descriptor within the plugin jar.
     * 
     * @return
     */
    public List<URI> getComponentTypeBundleDescriptorURIList() {
        List<URI> bundleDescriptorURIs = new ArrayList<URI>();
        List<URI> componentJarURIs = ResourceRegistry.getInstance().getComponentJarList(false);
        for (URI componentJarURI : componentJarURIs) {
            URI jarInternalURI = ResourceRegistry.toJarInternalURI(componentJarURI, DefaultBundleModelParser.BUNDLE_DESCRIPTOR_RELATIVE_URI);

            try {
                if (checkForAstericsMetadata(jarInternalURI.toURL(), jarInternalURI.toString())) {
                    // if it has a bundle_descirptor it can only be a component
                    // (plugin).
                    bundleDescriptorURIs.add(jarInternalURI);
                }
            } catch (MalformedURLException e) {
                logger.warning("Ignoring jarInternalURI, is malformed URL: " + jarInternalURI);
            }
        }
        return bundleDescriptorURIs;
    }

    /**
     * Before installing the bundles an uninstall of all bundles is done. Returns a list of bundles that are installable for this platform the ARE is running
     * on. This really tries to install the bundle via OSGi and checks if e.g. involved native libs are supported for this platform. After invoking the method
     * the bundles are left installed.
     * 
     * NOTE: This method is not thread-safe, the caller must ensure the thread-safety.
     * 
     * @return
     */
    public List<URI> getInstallableBundleList() {
        List<URI> installableBundleURIList = new ArrayList<URI>();
        List<URI> componentJarURIs = ResourceRegistry.getInstance().getComponentJarList(false);

        // Remember current runtime model state, because afterwards we have to
        // restore the current state.
        IRuntimeModel currentRuntimeModel = DeploymentManager.instance.getCurrentRuntimeModel();
        AREStatus currentModelStatus = DeploymentManager.instance.getStatus();
        // Stop and undeploy model so that we can uninstall all componentType
        // bundles afterwards.
        AREServices.instance.stopModelInternal();
        DeploymentManager.instance.undeployModel();
        uninstallComponentTypeBundles();

        for (URI componentJarURI : componentJarURIs) {
            try {
                Bundle b = null;
                b = installSingle(componentJarURI);
                if (b != null) {
                    // Remember the absolute URI, the caller expects it.
                    installableBundleURIList.add(ResourceRegistry.getInstance().toAbsolute(b.getLocation()));
                }
            } catch (BundleException | IOException | ParseException | BundleManagementException e) {
                // If the installation of the bundle fails
                Throwable cause = e.getCause();
                String reason = (cause != null && cause.getMessage() != null) ? ", Reason: " + cause.getMessage() : "";
                AstericsErrorHandling.instance.getLogger()
                        .warning("Cannot install bundle (" + e.getClass().getName() + "), skipping it: " + componentJarURI + reason);
            }
        }

        // Now try to restore previous state!
        try {
            if (currentRuntimeModel != null) {
                DeploymentManager.instance.deployModel(currentRuntimeModel);
                if (currentModelStatus == AREStatus.RUNNING || currentModelStatus == AREStatus.PAUSED) {
                    AREServices.instance.runModelInternal();
                }
                if (currentModelStatus == AREStatus.PAUSED) {
                    AREServices.instance.pausModelInternal();
                }
                // Now it should be in the same state as before - puh!!
            }
        } catch (DeploymentException e) {
            AstericsErrorHandling.instance.reportError(null, "in BundleManager.getInstallableComponentList: Could not redeploy current runtimeModel");
        }
        return installableBundleURIList;
    }

    /**
     * Returns the jar name of the given componentTypeId as it was found in the componentlist cache. This is just the relative jar name, to create an absolute
     * one use the {@link ResourceRegistry.getInstance().getResource()} method.
     * 
     * @param componentTypeId
     * @return
     */
    public String getJarNameFromComponentTypeId(String componentTypeId) throws BundleManagementException {
        String jarName = componentTypeIDToJarName.get(componentTypeId);
        if (jarName == null) {
            throw new BundleManagementException("Could not find bundle jar name for componentTypeId <" + componentTypeId + ">");
        }
        return jarName;
    }

    /**
     * Returns the jar name URI for the given componentTypeId.
     * 
     * @param componentTypeId
     * @return
     * @throws BundleManagementException
     */
    public URI getJarNameURIFromComponentTypeId(String componentTypeId) throws BundleManagementException {
        String jarName = getJarNameFromComponentTypeId(componentTypeId);
        try {
            URI jarNameURI = ResourceRegistry.getInstance().getResource(jarName, RES_TYPE.JAR);
            return jarNameURI;
        } catch (URISyntaxException e) {
            throw new BundleManagementException(e.getMessage());
        }
    }

    /**
     * Returns the URI of the bundle_descritpor.xml file inside the component jar file.
     * 
     * @param componentTypeId
     * @return
     * @throws BundleManagementException
     */
    public URI getBundleDescriptorURIFromComponentTypeId(String componentTypeId) throws BundleManagementException {
        URI jarNameURI = getJarNameURIFromComponentTypeId(componentTypeId);
        URI bundleDescriptorURI = ResourceRegistry.toJarInternalURI(jarNameURI, DefaultBundleModelParser.BUNDLE_DESCRIPTOR_RELATIVE_URI);
        return bundleDescriptorURI;
    }

    /**
     * Loads the classes of the component and registers the given component in the {@link ComponentRepository}.
     * 
     * @param bundle
     * @throws IOException
     * @throws ParseException
     */
    private void registerBundle(final Bundle bundle) throws IOException, ParseException {
        final URL url = bundle.getResource(DefaultBundleModelParser.BUNDLE_DESCRIPTOR_RELATIVE_URI);

        try {
            synchronized (DefaultBundleModelParser.instance) {

                final Set<IComponentType> componentTypeSet = DefaultBundleModelParser.instance.parseModel(url.openStream());

                Set<IComponentType> successfullyLoadedComponentTypeSet = new LinkedHashSet<IComponentType>();
                for (final IComponentType componentType : componentTypeSet) {
                    // register component's factory as a service
                    final String componentCanonicalName = componentType.getCanonicalName();

                    try {
                        final Class clazz = bundle.loadClass(componentCanonicalName);
                        final boolean isSingleton = componentType.isSingleton();
                        componentRepository.setComponentFactory(componentCanonicalName, new DefaultComponentFactory(clazz, isSingleton));

                        componentTypeIDToBundle.put(componentType.getID(), bundle);
                        // install in global component repository
                        ComponentRepository.instance.install(componentType);
                        successfullyLoadedComponentTypeSet.add(componentType);
                    } catch (ClassNotFoundException cnfe) {
                        logger.warning(getClass().getName() + ".registerBundle: Could not instantiate class " + "with name " + componentCanonicalName
                                + ", ignoring this component! -> \n" + cnfe.getMessage());
                    }
                }
                bundlesToComponentTypesMap.put(bundle, successfullyLoadedComponentTypeSet);
            }
        } catch (IOException ioe) {
            logger.warning(
                    this.getClass().getName() + ".registerBundle: " + "Error while reading deployment metadata from " + bundle + " -> \n" + ioe.getMessage());
            throw ioe;
        } catch (ParseException pe) {
            logger.warning(this.getClass().getName() + ".registerBundle: " + "Parse error while reading deployment metadata from " + bundle + " -> \n"
                    + pe.getMessage());
            throw pe;
        }
    }

    /**
     * Unregisters the given component in the {@link ComponentRepository}.
     * 
     * @param bundle
     */
    private void unregisterBundle(final Bundle bundle) {
        try {
            final Set<IComponentType> componentTypeSet = bundlesToComponentTypesMap.get(bundle);
            if (componentTypeSet != null) {
                for (final IComponentType componentType : componentTypeSet) {
                    componentTypeIDToBundle.remove(componentType.getID());
                    ComponentRepository.instance.uninstall(componentType);

                }
                bundlesToComponentTypesMap.remove(bundle);
            }
        } catch (BundleManagementException bme) {
            logger.warning(this.getClass().getName() + ".registerBundle: " + "Error while installing component type from bundle " + bundle + " -> \n"
                    + bme.getMessage());
            throw new RuntimeException(bme);
        }
    }

    @Override
    public void frameworkEvent(FrameworkEvent e) {
        // System.out.println ("Framework said: "+e.getType()+" from
        // "+e.getBundle().getSymbolicName());
    }

    /**
     * Shows bundle installation error message.
     * 
     * @param bundle
     * @param name
     * @param reason
     */
    public void showBundleInstallErrorMessage(Bundle bundle, String name, String reason) {
        AstericsErrorHandling.instance.reportError(null, createBundleInstallErrorMessage(bundle, name, reason));
    }

    /**
     * Creates an error message string as a result of a bundle installation.
     * 
     * @param bundle
     * @param name
     * @param reason
     * @return
     */
    private String createBundleInstallErrorMessage(Bundle bundle, String name, String reason) {
        String optReason = reason != null ? "\nReason: " + reason : "";
        String errorMsg = "Deployment Error: Couldn't start bundle " + name + optReason;
        if (bundle != null) {
            // Log the exception and continue
            errorMsg = "Deployment Error: Couldn't start " + bundle.getBundleId() + " from location\n" + bundle.getLocation() + optReason;
        }
        return errorMsg;
    }

    /**
     * Installs the component with the given componentTypeId. The corresponding component jar is searched and installed here.
     * 
     * @param cTypeID
     * @throws BundleManagementException
     */
    public void installSingle(String cTypeID) throws BundleManagementException {
        Bundle bundle = null;
        try {
            URI jarURI = ResourceRegistry.getInstance().getResource(componentTypeIDToJarName.get(cTypeID), RES_TYPE.JAR);
            bundle = installSingle(jarURI);
        } catch (Throwable t) {
            // showBundleInstallErrorMessage(bundle,cTypeID,t.getMessage());
            throw new BundleManagementException(createBundleInstallErrorMessage(bundle, cTypeID, t.getMessage()));
        }
    }

    /**
     * Installs the bundle with the given jar URI.
     * 
     * @param jarURI
     * @return
     * @throws MalformedURLException
     * @throws BundleException
     * @throws IOException
     * @throws ParseException
     * @throws BundleManagementException
     */
    public Bundle installSingle(URI jarURI) throws MalformedURLException, BundleException, IOException, ParseException, BundleManagementException {
        logger.info("*** Installing bundle on-demand: " + jarURI);

        Bundle bundle = bundleContext.installBundle(jarURI.toString(), jarURI.toURL().openStream());
        if (checkForAstericsMetadata(bundle)) {
            registerBundle(bundle);
        } else {
            throw new BundleManagementException("Could not find valid bundle descriptor in jarURI: " + jarURI);
        }
        return bundle;
    }

    /**
     * Removes surrounding xml tag and componentTypes tag
     * 
     * @param bundleDescriptorStringAsXML
     * @return
     */
    private String removeSurroundingComponentTypesTags(String bundleDescriptorStringAsXML) {
        bundleDescriptorStringAsXML = bundleDescriptorStringAsXML.replaceFirst("<\\?xml version=\"[0-9]\\.[0-9]\"\\?>", "");
        bundleDescriptorStringAsXML = bundleDescriptorStringAsXML.replaceFirst("^(<componentTypes)?^[^>]*>", "");
        bundleDescriptorStringAsXML = bundleDescriptorStringAsXML.replaceFirst("</componentTypes>", "");

        return bundleDescriptorStringAsXML;
    }

    /**
     * Installs services and bundles at startup of the BundleManager.
     */
    public void installServices() {
        if (!ResourceRegistry.getInstance().isOSGIMode()) {
            logger.fine("OSGIMode=false --> Skipping services installation.");
            return;
        }

        String path;
        Bundle bundle = null;

        // Hard code loading of services defined in services.ini and
        // services-{os.name}.ini
        SERVICES_FILES = "services.ini;services-" + OSUtils.getOsName() + ".ini;services_webservice.ini;" + SERVICES_FILES;
        logger.fine("Using the following .ini files to load services: " + SERVICES_FILES);

        // First load all services defined in the services-*.ini files
        for (String servicesFile : SERVICES_FILES.split(SERVICES_FILES_DELIM)) {
            try (InputStream serviceFileStream = ResourceRegistry.getInstance().getResourceInputStream(servicesFile, RES_TYPE.PROFILE);
                    BufferedReader in = new BufferedReader(new InputStreamReader(serviceFileStream))) {

                logger.fine("Loading services from file: " + servicesFile);
                while ((path = in.readLine()) != null) {
                    path = path.trim();
                    // Skipping comments
                    if (path.startsWith("#") || path.isEmpty() || !path.endsWith(".jar")) {
                        continue;
                    }
                    try {
                        URI jarURI = ResourceRegistry.getInstance().getResource(path, RES_TYPE.JAR);
                        // bundle=installSingle(jarURI);
                        logger.info("Installing service: " + jarURI);
                        bundle = bundleContext.installBundle(jarURI.toString(), jarURI.toURL().openStream());
                        bundle.start();
                        bundle = null;
                    } catch (URISyntaxException | BundleException | IOException e) {
                        showBundleInstallErrorMessage(bundle, path, e.getMessage());
                    }
                }
            } catch (MalformedURLException | URISyntaxException e) {
                String errorMsg = "Could not create URI/URL for services file: " + servicesFile;
                AstericsErrorHandling.instance.reportError(null, errorMsg);
            } catch (IOException e) {
                String errorMsg = "Could not read from services file: " + servicesFile;
                AstericsErrorHandling.instance.reportError(null, errorMsg);
            }
        }
        notifyAREEventListeners(AREEvent.POST_BUNDLES_EVENT);
    }

    /**
     * Actually generates the loader_componentlist.ini file, which is actually a cache and maps Asterics components to the associated component jar. This is
     * necessary because, unlike in OSGi normally, there is no 1:1 mapping between a component and the jar name. In Asterics a several components may be bundled
     * within one jar file.
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws ParseException
     * @throws URISyntaxException
     */
    public void generateLoaderComponentListAndBundleDescriptorCacheFiles() throws MalformedURLException, IOException, ParseException, URISyntaxException {
        File componentList = ResourceRegistry.toFile(LOADER_COMPONENTLIST_CACHE_FILE_URI);

        StringBuilder bundleDescriptorsXMLBuilder = new StringBuilder();
        bundleDescriptorsXMLBuilder.append("<?xml version=\"1.0\"?>");
        bundleDescriptorsXMLBuilder
                .append("<componentTypes xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");

        // List<Bundle> installableBundleList = getInstallableBundleList();
        List<URI> bundleURIList = getBundleURIList();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(componentList))) {
            for (URI bundleURI : bundleURIList) {
                URL bundleDescriptor = ResourceRegistry.toJarInternalURI(bundleURI, DefaultBundleModelParser.BUNDLE_DESCRIPTOR_RELATIVE_URI).toURL();

                String bundleDescriptorAsXMLString = ResourceRegistry.getResourceContentAsString(bundleDescriptor.openStream());
                Set<IComponentType> componentTypeSet = DefaultBundleModelParser.instance.parseModelAsXMLString(bundleDescriptorAsXMLString);

                // update big bundle descriptors string
                bundleDescriptorsXMLBuilder.append(removeSurroundingComponentTypesTags(bundleDescriptorAsXMLString));
                String installableBundleJarName = ResourceRegistry.getInstance().toRelative(bundleURI).getPath();

                // extract component types and store them in loader_componentlist.ini
                if (componentTypeSet.size() >= 1) {
                    StringBuilder line = new StringBuilder(installableBundleJarName);
                    for (IComponentType componentType : componentTypeSet) {
                        line.append(COMPONENTLIST_DELIM);
                        line.append(" ");
                        line.append(componentType.getID());
                    }
                    writer.write(line.toString());
                    writer.newLine();
                }
            }
        }

        // Finalize bundle descriptors string and store it into the cache file.
        bundleDescriptorsXMLBuilder.append("</componentTypes>");
        ResourceRegistry.storeResource(bundleDescriptorsXMLBuilder.toString(), COMPONENT_COLLECTION_CACHE_FILE_URI);
    }

    /**
     * Returns a list of bundle URIs depending on the configured cache generation mode (see {@value #ARE_GENERATE_CACHES_FROM_INSTALLABLE_BUNDLES_PROP_KEY}).
     * 
     * @return
     */
    private List<URI> getBundleURIList() {
        List<URI> bundleURIList = new ArrayList<URI>();
        if (Boolean.valueOf(AREProperties.instance.getProperty(ARE_GENERATE_CACHES_FROM_INSTALLABLE_BUNDLES_PROP_KEY))) {
            logger.fine("Generating caches only for platform installable bundles.");
            bundleURIList = getInstallableBundleList();
        } else {
            logger.fine("Generating caches for all bundles");
            bundleURIList = ResourceRegistry.getInstance().getComponentJarList(false);
        }
        logger.fine("Returning bundle URI list with " + bundleURIList.size() + " elements.");
        return bundleURIList;
    }

    /**
     * Reads the loader_componentlist.ini file and initializes an internal Map to quickly find the jarname of a component.
     * 
     * @param componentListCache
     * @throws IOException
     * @throws URISyntaxException
     */
    private void readComponentListCache() throws IOException, URISyntaxException {
        File componentList = ResourceRegistry.toFile(LOADER_COMPONENTLIST_CACHE_FILE_URI);

        try (BufferedReader in = new BufferedReader(new FileReader(componentList))) {
            String actLine = "";
            componentTypeIDToJarName.clear();
            while ((actLine = in.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(actLine, COMPONENTLIST_DELIM);
                if (tokenizer.countTokens() >= 2) {
                    String jarName = tokenizer.nextToken().trim();
                    while (tokenizer.hasMoreTokens()) {
                        componentTypeIDToJarName.put(tokenizer.nextToken().trim(), jarName);
                    }
                }
            }
            in.close();
        }
    }

    /**
     * Checks if the specified bundle URI contains an OSGi service. The test is based on checking if the
     * {@link DefaultBundleModelParser.BUNDLE_DESCRIPTOR_RELATIVE_URI} file does not exist and if a MANIFEST entry 'Bundle-Name' can be found.
     * 
     * @param serviceBundleURI
     *            The URI to the jar
     * @return
     */
    public boolean checkForServiceBundle(URI serviceBundleURI) {
        try {
            // String inputFilePath = "jar:file://" + serviceBundleURI.getPath()
            // + "!/bundle_descriptor.xml" ;
            URI jarInternalURI = ResourceRegistry.toJarInternalURI(serviceBundleURI, DefaultBundleModelParser.BUNDLE_DESCRIPTOR_RELATIVE_URI);

            if (checkForAstericsMetadata(jarInternalURI.toURL(), jarInternalURI.toString())) {
                // if it has a bundle_descirptor it can only be a component
                // (plugin).
                return false;
            }

            // inputFilePath = "jar:file://" + serviceBundleURI.getPath() +
            // "!/META-INF/MANIFEST.MF" ;
            jarInternalURI = ResourceRegistry.toJarInternalURI(serviceBundleURI, "/META-INF/MANIFEST.MF");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(jarInternalURI.toURL().openStream()))) {
                String actLine = "";
                while ((actLine = in.readLine()) != null) {
                    // Bundle-Name is specific for OSGi bundles. But actually we
                    // don't know if it as an OSGi service or just a plugin but
                    // by check the bundle_descriptor above
                    // we can assume that it is a service.
                    if (actLine.startsWith("Bundle-Name:")) {
                        return true;
                    }
                }
            } catch (IOException e) {
            }
            // If we get here, either an exception was thrown, so the FILE is
            // not contained or we could not find OSGi MANIFEST info.
            return false;
        } catch (MalformedURLException e) {
        }
        return false;
    }

    /**
     * Uninstalls all bundles. Be careful, this also uninstalls the ARE bundle!!!!
     */
    public void uninstall() {
        Bundle[] bundles = bundleContext.getBundles();
        for (Bundle b : bundles) {
            try {
                // and unregister it from internal maps
                unregisterBundle(b);
                // uninstall OSGI stuff
                b.uninstall();
            } catch (BundleException e) {
                logger.warning(this.getClass().getName() + "." + "Error while uninstalling bundle!" + b.getSymbolicName());
            }
        }
    }

    public void uninstallComponentTypeBundles() {
        Bundle[] bundles = bundleContext.getBundles();
        for (Bundle b : bundles) {
            if (bundlesToComponentTypesMap.containsKey(b)) {
                try {
                    logger.info("Uninstalling bundle Id: " + b.getBundleId() + ", symbolicName: " + b.getSymbolicName());
                    // and unregister it from internal maps
                    unregisterBundle(b);
                    // uninstall OSGI stuff
                    b.uninstall();
                } catch (BundleException e) {
                    logger.warning(this.getClass().getName() + "." + "Error while uninstalling bundle!" + b.getSymbolicName());
                }
            }
        }
    }

    private void notifyAREEventListeners(AREEvent areEvent) {
        ArrayList<IAREEventListener> listeners = AREServices.instance.getAREEventListners();

        switch (areEvent) {
        case PRE_DEPLOY_EVENT:
            for (IAREEventListener listener : listeners) {
                listener.preDeployModel();
            }
            break;
        case POST_DEPLOY_EVENT:
            for (IAREEventListener listener : listeners) {
                listener.postDeployModel();
            }
            break;
        case PRE_START_EVENT:
            for (IAREEventListener listener : listeners) {
                listener.preStartModel();
            }
            break;
        case POST_START_EVENT:
            for (IAREEventListener listener : listeners) {
                listener.postStartModel();
            }
            break;
        case PRE_STOP_EVENT:
            for (IAREEventListener listener : listeners) {
                listener.preStopModel();
            }
            break;
        case POST_STOP_EVENT:
            for (IAREEventListener listener : listeners) {
                listener.postStopModel();
            }
            break;
        case PRE_PAUSE_EVENT:
            for (IAREEventListener listener : listeners) {
                listener.prePauseModel();
            }
            break;
        case POST_PAUSE_EVENT:
            for (IAREEventListener listener : listeners) {
                listener.postPauseModel();
            }
            break;
        case PRE_RESUME_EVENT:
            for (IAREEventListener listener : listeners) {
                listener.preResumeModel();
            }
            break;
        case POST_RESUME_EVENT:
            for (IAREEventListener listener : listeners) {
                listener.postResumeModel();
            }
            break;
        case PRE_BUNDLES_EVENT:
            for (IAREEventListener listener : listeners) {
                listener.preBundlesInstalled();
            }
            break;
        case POST_BUNDLES_EVENT:
            for (IAREEventListener listener : listeners) {
                listener.postBundlesInstalled();
            }
            break;
        default:
            break;
        }

    }

}
