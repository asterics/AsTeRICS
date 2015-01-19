package eu.asterics.mw.are;

import eu.asterics.mw.are.exceptions.BundleManagementException;
import eu.asterics.mw.are.exceptions.ParseException;
import eu.asterics.mw.are.parsers.DefaultBundleModelParser;
import eu.asterics.mw.are.parsers.ModelValidator;
import eu.asterics.mw.model.bundle.IComponentType;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;
import eu.asterics.mw.services.IAREEventListener;

import org.osgi.framework.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;



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
	static String LOADER_LOCATION = "./profile/loader.ini";
	static String LOADER_MINIMAL_LOCATION = "./profile/loader_mini.ini";
	static String LOADER_COMPONENTLIST_LOCATION = "./profile/loader_componentlist.ini";
	static String SERVICES_LOCATION = "./profile/services.ini";

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



	BundleManager(final BundleContext bundleContext)
	{
		super();
		this.bundleContext = bundleContext;
	
		logger = AstericsErrorHandling.instance.getLogger();
	}

	void start()
	{
		install(MODE_DEFAULT);
	
	}

	void stop()
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

	public static final String DEFAULT_BUNDLE_DESCRIPTOR_URL = "/bundle_descriptor.xml";

	/**
	 * Checks if the specified bundle contains ASTERICS component(s) or not. The
	 * test is based on checking if the {@link #DEFAULT_BUNDLE_DESCRIPTOR_URL}
	 * file exists or not (in the root of the enclosing JAR).
	 *
	 * @param bundle the bundle to be tested
	 * @return true if the specified bundle contains ASTERICS component(s),
	 * false otherwise
	 */
	private boolean checkForAstericsMetadata(final Bundle bundle)
	{
		final URL url = bundle.getResource(DEFAULT_BUNDLE_DESCRIPTOR_URL);

		try
		{
            modelValidator= new ModelValidator(bundleContext);
        	return modelValidator.isValidBundleDescriptor(url.openStream());
		}
		catch (IOException ioe)
		{
			logger.warning(getClass().getName()+
					".checkForAstericsMetadata: validation error for file "+ 
					DEFAULT_BUNDLE_DESCRIPTOR_URL +", bundle "+ 
					bundle.getSymbolicName()+" -> \n"+
					ioe.getMessage());
		}
		catch (NullPointerException npe)
		{
			logger.warning(getClass().getName()+
					".checkForAstericsMetadata: error in opening URL "+ 
					DEFAULT_BUNDLE_DESCRIPTOR_URL +", bundle "+ 
					bundle.getSymbolicName()+" -> \n"+
					npe.getMessage());
		}

		return false;
	}

	private Map <IComponentType, ServiceRegistration> serviceRegistrations
		= new HashMap<IComponentType, ServiceRegistration>();

	public Bundle getBundleFromId(String componentTypeId)
	{
		return componentTypeIDToBundle.get(componentTypeId);
	}
	
	private String registerBundle(final Bundle bundle)
	{
		final URL url = bundle.getResource(DEFAULT_BUNDLE_DESCRIPTOR_URL);
		String componentTypeIDs = ""; 

		try
		{
			synchronized (DefaultBundleModelParser.instance) 
			{
			
				final Set<IComponentType> componentTypeSet
					= DefaultBundleModelParser.instance
						.parseModel(url.openStream());
			
				for(final IComponentType componentType : componentTypeSet)
				{
					componentTypeIDs+=("; "+componentType.getID());
					componentTypeIDToBundle.put(componentType.getID(), bundle);
					// install in global component repository
					ComponentRepository.instance.install(componentType);
	
					// register component's factory as a service
					final String componentCanonicalName = componentType.getCanonicalName();
					try
					{
						final Class clazz = bundle.loadClass(componentCanonicalName);
						final boolean isSingleton = componentType.isSingleton();
						componentRepository.setComponentFactory(componentCanonicalName,
								new DefaultComponentFactory(clazz, isSingleton));
					}
					catch (ClassNotFoundException cnfe)
					{
						logger.warning(getClass().getName()+
								".registerBundle: Could not instantiate class " +
								"with name "+componentCanonicalName+" -> \n"+ 
								cnfe.getMessage());
					}
				}
				bundlesToComponentTypesMap.put(bundle, componentTypeSet);
			}
		}
		catch (IOException ioe)
		{
			logger.warning(this.getClass().getName()+".registerBundle: " +
					"Error while reading deployment metadata from " + 
					bundle + " -> \n" + ioe.getMessage());
			throw new RuntimeException(ioe);
		}
		catch (ParseException pe)
		{
			logger.warning(this.getClass().getName()+".registerBundle: " +
					"Parse error while reading deployment metadata from " + 
					bundle + " -> \n" + pe.getMessage());
			throw new RuntimeException(pe);
		}
		catch (BundleManagementException bme)
		{
			logger.warning(this.getClass().getName()+".registerBundle: " +
					"Error while installing component type from bundle " + 
					bundle + " -> \n" + bme.getMessage());
			throw new RuntimeException(bme);
		}
		return(componentTypeIDs);
	}

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

	public void showBundleInstallErrorMessage( Bundle bundle, String path)
	{
		if (bundle!=null)
		{
			// Log the exception and continue
			logger.warning(this.getClass().getName()+".start: " 
					+"Couldn't start " + bundle.getBundleId());
			JOptionPane op = new JOptionPane ("Couldn't start " + bundle.getBundleId()+ "from location "+path,
				    JOptionPane.WARNING_MESSAGE);
			JDialog dialog = op.createDialog("AsTeRICS Runtime Environment - Deploy Error: Cannot start the Model !");
			dialog.setAlwaysOnTop(true);
			dialog.setModal(false);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		}
		else
		{
			logger.warning(this.getClass().getName()+".start: " 
					+"Couldn't start unknown bundle");
			JOptionPane op = new JOptionPane ("Couldn't start bundle "+path,
				    JOptionPane.WARNING_MESSAGE);
			JDialog dialog = op.createDialog("AsTeRICS Runtime Environment - Deploy Error: Cannot start the Model !");
			dialog.setAlwaysOnTop(true);
			dialog.setModal(false);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		}
	}
	
	public void install_single (String cTypeID)
	{
		BufferedReader in;
		Bundle bundle= null;
		String actLine;
		String bundleJar=null;
		String installBundleName=null;

		try {

				in = new BufferedReader(new FileReader(LOADER_COMPONENTLIST_LOCATION));
		
				while ( (actLine = in.readLine()) != null)
				{
					if (actLine.contains(cTypeID))
					{
						bundleJar=actLine.substring(0,actLine.indexOf(";"));
						// System.out.println("*** FOUND BUNDLE JAR:"+bundleJar);
						break;
					}
				}
					
				if (bundleJar!=null)
				{	
					File directory = new File (".");

					installBundleName= directory.getCanonicalPath()+"/"+bundleJar;
				    System.out.println("*** installing bundle on-demand: "+installBundleName);
					bundle = bundleContext.installBundle("file:///"+installBundleName);
					if(checkForAstericsMetadata(bundle))
						registerBundle(bundle);
			    }
			}
			catch (Throwable t) 
			{
				showBundleInstallErrorMessage(bundle,installBundleName);
			}
	}
	
	public void install(int mode) 
	{
		String path;	
		BufferedReader in;
		BufferedWriter out=null;
		Bundle bundle= null;

		if (mode==MODE_DEFAULT)
		{
			try {
				in = new BufferedReader(new FileReader(SERVICES_LOCATION));
	
				while ( (path = in.readLine()) != null)
				{
					try 
					{	
						File directory = new File (".");				
						bundle = bundleContext.installBundle("file:///"+directory.getCanonicalPath()+"/"+path);
						bundle.start();
					}
					catch (Throwable t) 
					{
						showBundleInstallErrorMessage (bundle,path);
						continue;
					}
				}
				in.close();
			} catch (FileNotFoundException e) {
				logger.severe(this.getClass().getName()+"." +
				"The services file is missing!");
			} catch (IOException e) {
				logger.severe(this.getClass().getName()+"." +
				"Error while reading the services file!");
			}
		}
		
		
		try {
			if ((mode==MODE_DEFAULT) && (new File(LOADER_MINIMAL_LOCATION).exists()))
			{
				System.out.println("*** Bundle install mode: minimal");
				in = new BufferedReader(new FileReader(LOADER_MINIMAL_LOCATION));
			}
			else
			{
				System.out.println("*** Bundle install mode: all");

				in = new BufferedReader(new FileReader(LOADER_LOCATION));
				if (mode== MODE_GET_ALL_COMPONENTS)
				   out = new BufferedWriter(new FileWriter(LOADER_COMPONENTLIST_LOCATION));
			}
			
			while ( (path = in.readLine()) != null)
			{
				try 
				{	
			
					File directory = new File (".");
					System.out.println("*** installing bundle: "+directory.getCanonicalPath()+"/"+path);
					
					bundle = bundleContext.installBundle("file:///"+directory.getCanonicalPath()+"/"+path);
					if(checkForAstericsMetadata(bundle))
					{
						String componentTypeIDs =registerBundle(bundle);
						if (mode== MODE_GET_ALL_COMPONENTS)
							out.write(path+componentTypeIDs+"\n");
					}
				}
				catch (Throwable t) 
				{
					showBundleInstallErrorMessage(bundle,path);
					continue;
				}
			}
			in.close();
			if (mode== MODE_GET_ALL_COMPONENTS)
				out.close();
			notifyAREEventListeners ("postBundlesInstalled");				
		} catch (FileNotFoundException e) {
			logger.severe(this.getClass().getName()+"." +
			"The loader file is missing!");
		} catch (IOException e) {
			logger.severe(this.getClass().getName()+"." +
			"Error while reading the loader file!");
		}
	}
	
	public void uninstall() 
	{
			Bundle[] bundles = bundleContext.getBundles();
			for (Bundle b : bundles)
			{
				try {
					b.uninstall();
				} catch (BundleException e) {
					logger.warning(this.getClass().getName()+"." +
							"Error while uninstalling bundle!"+b.getSymbolicName());
				}
			}
	}
	
	private void notifyAREEventListeners(String methodName) 
	{
		ArrayList<IAREEventListener> listeners = 
				AREServices.instance.getAREEventListners();

		if (methodName.equals("preDeployModel"))
		{
			for (IAREEventListener listener : listeners)
			{
				listener.preDeployModel();
			}
		}
		else if (methodName.equals("postDeployModel"))
		{
			for (IAREEventListener listener : listeners)
			{
				listener.postDeployModel();
			}

		}
		else if (methodName.equals("preStartModel"))
		{
			for (IAREEventListener listener : listeners)
			{
				listener.preStartModel();
			}

		}
		else if (methodName.equals("postStopModel"))
		{
			for (IAREEventListener listener : listeners)
			{
				listener.postStopModel();
			}

		}
		else if (methodName.equals("postBundlesInstalled"))
		{
			for (IAREEventListener listener : listeners)
			{
				listener.postBundlesInstalled();
			}

		}

	}

}

