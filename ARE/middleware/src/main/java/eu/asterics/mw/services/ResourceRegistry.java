package eu.asterics.mw.services;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.*;
import java.nio.file.*;
import java.nio.file.Paths;
import java.util.*;

import eu.asterics.mw.are.BundleManager;
import eu.asterics.mw.are.DeploymentManager;

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
 * This class is the central point to find and fetch resources used by the ARE middleware, osgi services and osgi plugins.
 * The idea is to generically implement the fetching of resources to enable the same approach for the whole AsTeRICS framework. This way all plugins, services and other classes will be able to also support
 * URI schemes (e.g. http, jar,...). Furthermore base URIs can be reconfigured depending on platform specific or usecase specific requirements (e.g. readonly jar respository from a website and writable local folder for caching and model creation).
 * 
 *         Author: martin.deinhofer@technikum-wien.at
 *         Date: Oct 11, 2015
 *         Time: 00:17:00 AM
 */

public class ResourceRegistry {
	private static ResourceRegistry instance=new ResourceRegistry();
	// todo replace with ComponentRepository
	public static final String MODELS_FOLDER = "models/";
	public static final String DATA_FOLDER = "data/";
	public static final String PROFILE_FOLDER = "profile/";
	public static final String STORAGE_FOLDER = "storage/";
	public static final String LICENSES_FOLDER = "LICENSE/";
	public static final String IMAGES_FOLDER = "images/";
	
	private static URI ARE_BASE_URI = null;
	private static URI ARE_WRITABLE_URI=null;
	
	private static boolean OSGI_MODE=true;
	
	static {
		URI defaultAREBaseURI=Paths.get(".").toUri();
		try {
			defaultAREBaseURI = ResourceRegistry.toPath(ResourceRegistry.instance.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().toUri();
		} catch (URISyntaxException e) {
		}
		ARE_BASE_URI=URI.create(System.getProperty("eu.asterics.ARE.baseURI", defaultAREBaseURI.toString()));
		AstericsErrorHandling.instance.getLogger().fine("Setting ARE base URI to <"+ARE_BASE_URI+">");
		
		String areWritableURIString=System.getProperty("eu.asterics.ARE.writableURI");
		if(areWritableURIString!=null) {
			ARE_WRITABLE_URI=URI.create(areWritableURIString);
			AstericsErrorHandling.instance.getLogger().fine("Setting ARE writable URI to <"+ARE_WRITABLE_URI+">");
		}
		
		OSGI_MODE=Boolean.parseBoolean(System.getProperty("eu.asterics.ARE.OSGI_MODE","true"));
		AstericsErrorHandling.instance.getLogger().fine("Setting OSGI_MODE to <"+OSGI_MODE+">");		
	}
		
	public enum RES_TYPE {
		ANY,
		MODEL,
		DATA,
		JAR,
		PROFILE,
		STORAGE,
		LICENSE,
		IMAGE
	};
	
	/**
	 * Return the instance of the ResourceRegistry.
	 * @return
	 */
	public static ResourceRegistry getInstance() {
		return instance;
	}
	
	/**
	 * Return the URI according to the given resourceName string and the given resource type RES_TYPE.
	 * RES_TYPE.MODEL: If a relative filename was provided, the 'models' folder is prefixed.
	 * @param resourceName
	 * @param type
	 * @return
	 * @throws URISyntaxException
	 */
	public URI getResource(String resourceName, RES_TYPE type) throws URISyntaxException {
		URI uri=null;
		
		try {
			URL url=new URL(resourceName);			
			AstericsErrorHandling.instance.getLogger().fine("Resource URL: "+url);
			uri=url.toURI();			
		} catch (MalformedURLException e) {
			File resourceNameAsFile=new File(resourceName);
			
			if(!resourceNameAsFile.isAbsolute()) {
				//In case of model files, prefix the MODELS_FOLDER if the path is relative.
				switch(type) {
				case MODEL:					
					uri=toAbsolute(MODELS_FOLDER).resolve(resourceName);
					break;
				case PROFILE:
					uri=toAbsolute(PROFILE_FOLDER).resolve(resourceName);
					break;
				case LICENSE:
					uri=toAbsolute(LICENSES_FOLDER).resolve(resourceName);
					break;
				case DATA:
					uri=toAbsolute(DATA_FOLDER).resolve(resourceName);
					break;
				case IMAGE:
					uri=toAbsolute(IMAGES_FOLDER).resolve(resourceName);
					break;
				case STORAGE:
					uri=toAbsolute(STORAGE_FOLDER).resolve(resourceName);					
					break;
				default:
					uri=resourceNameAsFile.toURI();
					break;
				}
			} else {
				uri=resourceNameAsFile.toURI();
			}
		}
		//System.out.println("file absolute: "+resourceNameAsFile.isAbsolute()+", uri absolute: "+uri.isAbsolute()+", uri opaque: "+uri.isOpaque());
		//System.out.println("resource File.toURI: "+resourceNameAsFile.toURI());
		//AstericsErrorHandling.instance.getLogger().fine("URI before normalize: "+uri.normalize());
		uri=uri.normalize();
		AstericsErrorHandling.instance.getLogger().info("Final Resource URI <"+uri+">");		
		return uri;
	}
	
	/**
	 * Returns an InputStream for the given resourceName and RES_TYPE.
	 * @param resourceName
	 * @param type
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public InputStream getResourceInputStream(String resourceName, RES_TYPE type) throws MalformedURLException, IOException, URISyntaxException {
		return getResource(resourceName, type).toURL().openStream();
	}
	
	/**
	 * Set the base URI of the ARE. This URI will be used as parent path for all resources like models, data, storage,...
	 * Also the jars of the ARE and the plugins are expected in that path.
	 * @param areBaseURI
	 */
	public void setAREBaseURI(URI areBaseURI) {
		AstericsErrorHandling.instance.getLogger().fine("Setting ARE base URI to: "+areBaseURI);
		ARE_BASE_URI=areBaseURI;
	}
	
	/**
	 * Get the base URI of the ARE. This URI will be used as parent path for all resources like models, data, storage,...
	 * Also the jars of the ARE and the plugins are expected in that path.
	 * The default base URI is the location of the ARE.jar file. It can be changed by the property eu.asterics.ARE.baseURI or by using the method {@link setAREBaseURI}.
	 * @return
	 */
	public URI getAREBaseURI() {		
		return ARE_BASE_URI;		
	}
	
	/**
	 * Sets the location for writable (File) access. This is needed for temporary storage like osgi cache and log files but also for models or data files that were deployed.  
	 * @param writableURI
	 */
	public void setAREWritableURI(URI writableURI) {
		ARE_WRITABLE_URI=writableURI;
	}
	
	/**
	 * Returns the URI for writable storage.
	 * @return
	 */
	public URI getAREWritableURI() {
		return ARE_WRITABLE_URI != null ? ARE_WRITABLE_URI : getAREBaseURI();
	}

	/**
	 * Converts the given absolutePath to a path relative to the ARE base URI {@link getAREBaseURI}.
	 * @param absolutePath
	 * @return
	 */
	public URI toRelative(String absolutePath) {
		return toRelative(URI.create(absolutePath)); 
	}
	
	/**
	 * Converts the given absolutePath URI to a path relative to the ARE base URI {@link getAREBaseURI}.
	 * @param absolutePath
	 * @return
	 */
	public URI toRelative(URI absolutePath) {
		return getAREBaseURI().relativize(absolutePath);
	}

	/**
	 * Converts the given relativePath to an absolute path by resolving with the ARE base URI {@link getAREBaseURI}.
	 * @param relativePath
	 * @return
	 */
	public URI toAbsolute(String relativePath) {
		return getAREBaseURI().resolve(relativePath);
	}
	
	/**
	 * Converts the given relativePath URI to an absolute one by adding ARE base URI {@link getAREBaseURI}.
	 * @param relativePath
	 * @return
	 */
	public URI toAbsolute(URI relativePath) {
		return getAREBaseURI().resolve(relativePath);
	}
	
	/**
	 * Checks whether the given URI is a URL or not.
	 * @param uriToCheck true: URI is a URL, false: URI is not a URL but probably a file (relative or absolute)
	 * @return
	 */
	public static boolean isURL(URI uriToCheck) {
		try {
			uriToCheck.toURL();
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}
	
	/**
	 * Returns a File object representing the given URI if possible.
	 * This only works if the given URI is a relative path or is a path with a file scheme (starting with: file://)
	 * @param uri
	 * @return
	 * @throws URISyntaxException 
	 */
	public static File toFile(URI uri) throws URISyntaxException {
		String scheme=uri.getScheme();
		if(scheme!=null && !scheme.startsWith("file")) {
			throw new URISyntaxException(uri.toString(),"The uri does not start with the scheme <file:>");
		}
		File f=new File(uri.getPath());
		return f;
	}
	
	public static Path toPath(URI uri) throws URISyntaxException {
		String scheme=uri.getScheme();
		if(scheme!=null && !scheme.startsWith("file")) {
			throw new URISyntaxException(uri.toString(),"The uri does not start with the scheme <file:>");
		}
		Path p=toFile(uri).toPath();
		return p;
	}

	/**
	 * Returns the current value of the flag OSGI_MODE.
	 * 
	 * @return false: The ARE and involved classes are not running within an OSGi context. Which can be the case if the ARE is used as a library.
	 */
	public boolean isOSGIMode() {
		return OSGI_MODE;
	}
	
	/**
	 * Sets the value for the flag OSGI_MODE to the given value of OSGIMode;
	 * @param OSGIMode
	 */
	public void setOSGIMode(boolean OSGIMode) {
		OSGI_MODE=OSGIMode;
		AstericsErrorHandling.instance.getLogger().fine("Setting OSGI_MODE to <"+OSGI_MODE+">");		
	}
	
	/**
	 * Returns a list of component jarname URIs. The URIs could be a local file but also an HTTP URL.
	 * 
	 * @param relative: true: Only return name without absolute path.
	 * @return
	 */
	public List<URI> getComponentJarList(boolean relative) {
		//get asterics component bundles
		List<URI> URIs = ComponentUtils.findFiles(getAREBaseURI(), relative, 1, new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		    	return (name.startsWith("asterics.processor") || name.startsWith("asterics.actuator") || name.startsWith("asterics.sensor")) 
		    			&& name.endsWith(".jar");
		    }
		});
		return URIs;
	}
	
	/**
	 * Returns a list of license file URIs. The URIs could be a local file but also an HTTP URL.
	 * 
	 * @param relative true: Only return name without absolute path. 
	 * @return
	 */
	public List<URI> getLicensesList(boolean relative) {
		//get asterics involved LICENSES
		List<URI> URIs = ComponentUtils.findFiles(toAbsolute(LICENSES_FOLDER), relative, 1, new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		    	return name.endsWith(".txt");
		    }
		});
		return URIs;
	}
	 
	/**
	 * Returns a list of URIs asterics service bundles. The URIs could be a local file but also an HTTP URL.
	 * 
	 * @param relative true: Only return name without absolute path. 
	 * @return
	 */
	public List<URI> getServicesJarList(boolean relative) {
		//get asterics/osgi service bundles
		List<URI> URIs = ComponentUtils.findFiles(getAREBaseURI(), relative, 1, new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {		    	
		    	//Should we include the ARE here??		    	
		    	return !(name.startsWith("asterics.processor") || name.startsWith("asterics.actuator") || name.startsWith("asterics.sensor")) 
		    			&& name.endsWith(".jar") 
		    			&& DeploymentManager.instance.getBundleManager().checkForServiceBundle(new File(dir,name).toURI());		    			
		    }
		});
		return URIs;		
	}
	
	/**
	 * Returns the URI of the ARE jar. The URI could be a local file but also an HTTP URL.
	 * 
	 * @param relative true: Only return name without absolute path.
	 * @return
	 */
	public URI getAREJarURI(boolean relative) {
		return relative ? URI.create("asterics.ARE.jar") : toAbsolute("asterics.ARE.jar");
	}
	
	/**
	 * Returns a list of URIs jars other than component, service or ARE. The URIs could be a local file but also an HTTP URL.
	 * 
	 * @param relative true: Only return name without absolute path. 
	 * @return
	 */
	public List<URI> getOtherJarList(boolean relative) {
		//get asterics/osgi service bundles
		List<URI> URIs = ComponentUtils.findFiles(getAREBaseURI(), relative, 1, new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {		    	
		    	//Should we include the ARE here??
		    	return !(name.startsWith("asterics.processor") || name.startsWith("asterics.actuator") || name.startsWith("asterics.sensor") || name.startsWith("asterics.mw") || name.startsWith("asterics.ARE") || name.startsWith("asterics.proxy")) 
		    			&& name.endsWith(".jar") 
		    			&& !DeploymentManager.instance.getBundleManager().checkForServiceBundle(new File(dir,name).toURI());		    			
		    }
		});
		return URIs;		
	}
	
	/**
	 * Returns a list of URIs of other Asterics files like start scripts and areProperties. The URIs could be a local file but also an HTTP URL.
	 * 
	 * @param relative true: Only return name without absolute path.  
	 * @return
	 */
	public List<URI> getOtherFilesList(boolean relative) {
		//get other files like start scripts and config files.
					
		final List<String> whiteList=Arrays.asList(new String[]{"are.exe","start.sh","start_debug.sh","start.bat","start_debug.bat","areproperties","jtester.exe"});
		List<URI> URIs = ComponentUtils.findFiles(getAREBaseURI(), relative, 1, new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {		    	
		    	//Should we include the ARE here??
		    	return name != null && whiteList.contains(name.toLowerCase());		    			
		    }
		});
		
		return URIs;				
	}
	
	/**
	 * Returns a list of URIs of mandatory (must exist at startup) profile config files. The URIs could be a local file but also an HTTP URL.
	 * 
	 * @param relative true: Only return name without absolute path.  
	 * @return
	 */
	public List<URI> getMandatoryProfileConfigFileList(boolean relative) {
		//get profile files
		
		final List<String> whiteList=Arrays.asList(new String[]{"config.ini","services.ini","services_websocketdemo.ini","services-linux.ini","services-windows.ini"});
		List<URI> URIs = ComponentUtils.findFiles(toAbsolute(PROFILE_FOLDER), relative, 1, new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {		    	
		    	//Should we include the ARE here??
		    	return name != null && whiteList.contains(name.toLowerCase());		    			
		    }
		});
		
		return URIs;				
	}
	
	/**
	 * Returns a list of URIs all AsTeRICS application images. The URIs could be a local file but also an HTTP URL.
	 * 
	 * @param relative true: Only return name without absolute path.  
	 * @return
	 */
	public List<URI> getAppImagesList(boolean relative) {
		List<URI> URIs = ComponentUtils.findFiles(toAbsolute(IMAGES_FOLDER), relative, 1, new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		    	return true;
		    }
		});
		return URIs;
	}	
	
	/**
	 * Returns a list of URIs all jars. The URIs could be a local file but also an HTTP URL.
	 * 
	 * @param relative true: Only return name without absolute path.  
	 * @return
	 */
	public List<URI> getAllJarList(boolean relative) {
		List<URI> URIs = ComponentUtils.findFiles(getAREBaseURI(), relative, 1, new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		    	return name.endsWith(".jar");
		    }
		});
		return URIs;
	}
	
	/**
	 * Returns a list of URIs of all data resources available for plugins. The URIs could be a local file but also an HTTP URL.
	 * 
	 * @param relative true: Only return name without absolute path.  
	 * @return
	 */
	public List<URI> getDataList(boolean relative) {
		//get asterics involved data files
		//Not sure how deep we should search, but 10 seems to be enough
		List<URI> URIs = ComponentUtils.findFiles(toAbsolute(DATA_FOLDER), relative,10, new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		    	return true;
		    }
		});
		return URIs;
	}
	
	/**
	 * Returns a list of URIs of models. The URIs could be a local file but also an HTTP URL.
	 *   
	 * @param relative true: Only return name without absolute path.
	 * @return
	 */
	public List<URI> getModelList(boolean relative) {
		return getModelList(toAbsolute(MODELS_FOLDER),relative);
	}
	
	/**
	 * Returns a list of model URIs in the given model dir URI.
	 * @param modelDirURI
	 * @param relative true: Only return name without absolute path.
	 * @return
	 */
	public static List<URI> getModelList(URI modelDirURI, boolean relative) {
		//get asterics involved model files
		//Not sure how deep we should search, but 10 seems to be enough
		List<URI> URIs = ComponentUtils.findFiles(modelDirURI, relative,10, new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		    	return name!=null && name.endsWith(".acs");
		    }
		});
		return URIs;		
	}
	
	/**
	 * Returns the ARE base URI as a File object, if possible.
	 * @return
	 * @throws URISyntaxException 
	 */
	File getAREBaseURIFile() throws URISyntaxException {
		/*
		if(getAREBaseURI().getScheme().startsWith("file")) {
			return new File(getAREBaseURI());
		}*/
		return toFile(getAREBaseURI());
		//return null;
	}

	/**
	 * Return the String representation of the given URI. 
	 * @param uri
	 * @return
	 */
	public static String toString(URI uri) {
		return uri.getPath();
	}
	
	/**
	 * Creates an absolute URI defining a resource within a jarFileURI.
	 * @param jarFileURI The jarFileURI that contains the resource.
	 * @param relativeInternalURI The relative path to the resource within the jar file. The relative path must have a leading / (e.g. /bundle_descriptor.xml)
	 * @return
	 */
	public static URI toJarInternalURI(URI jarFileURI, String relativeInternalURI) {
		String jarInternalURI = "jar:"+ jarFileURI + "!"+relativeInternalURI;
		return URI.create(jarInternalURI);
	}
}
