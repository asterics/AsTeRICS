package eu.asterics.mw.services;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;

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
 * Currently only file based ARE baseURIs are supported. Later maybe the base URI could also be an http-URL and the plugin resources directly fetched from an http-URL.
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
	//currently not used but the idea is to have a base URI for readonly, read/write and temporary data.
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
	 * Returns the URI of the requested resource.
	 * Generally the URI is not tested for existence it just constructs a valid URI for the given parameters.
	 * The returned URI can be opened with {@link URI#toURL()} and {@link URL#openStream()}, or better use directly {@link ResourceRegistry#getResourceInputStream(String, RES_TYPE)}.
	 * If you know that it is a file path URI and need to perform file operations, convert it to a file with {@link ResourceRegistry#toFile(URI)}.
	 * 
	 * resourcePath can be 
	 * 	 an absolute URL-conforming string (e.g. file://, http://). In this case the string must be encoded correctly.
	 *   or an absolute or relative file path (windows or unix-style). In this case {@link File} class and its method {@link File#toURI()} is used. The file path may contain both \\ and / seperators. Relative resourcePaths are resolved against the ARE.baseURI or a subfolder depending on the provided type ({@link RES_TYPE}). 

	 * The type ({@link RES_TYPE}) determines the type of the resource which is used to append the respective subfolder for the resource type (e.g. data, models).
	 * In case of {@value RES_TYPE#DATA} a 4 step approach is used to guess the correct subfolder (e.g. a sensor.facetrackerLK or pictures):
	 *   Step 1: If resource ARE.baseURI/data/{resourcePath} exists, return
	 *   Step 2: If componentTypeId!=null and resource ARE.baseURI/{DATA_FOLDER}/{componentTypeId}/{resourcePath} exists, return
	 *   Step 3: If componentTypeId!=null and resource ARE.baseURI/{DATA_FOLDER}/{*componentTypeId*}/{resourcePath} exists, return
	 *   Step 4: If resource ARE.baseURI/{DATA_FOLDER}/{*}/{resourcePath} exists, return
	 *   Default: If none of the above was successful, ARE.baseURI/data/{resourcePath} is returned.
	 * In case of {@value RES_TYPE#ANY} no subfolder is appended. 
	 * 
	 * @param resourcePath an absolute URL-conforming string or an absolute or relative file path string
	 * @param type The resource type {@link RES_TYPE} for the requested resource
	 * @param componentTypeId Hint for a better guessing of a {@value RES_TYPE#DATA} resource.
	 * @param runtimeComponentInstanceId Hint for a better guessing of a {@value RES_TYPE#DATA} resource.
	 * @return a valid URI.
	 * @throws URISyntaxException
	 */
	public URI getResource(String resourcePath, RES_TYPE type, String componentTypeId, String runtimeComponentInstanceId) throws URISyntaxException {
		URI uri=null;
		File resFilePath=null;
		
		try {
			URL url=new URL(resourcePath);			
			AstericsErrorHandling.instance.getLogger().fine("Resource URL: "+url);
			uri=url.toURI();			
		} catch (MalformedURLException e) {
			//Fix the string first, because it could have \ and / mixed up and stuff and convert it to path with unix-style path seperator (/)
			//Thanks to apache commons io lib this is very easy!! :-)
			final String resourcePathSlashified=FilenameUtils.separatorsToUnix(resourcePath);			
			//AstericsErrorHandling.instance.getLogger().fine("resourceNameArg: "+resourcePath+", resourceName after normalization: "+resourcePathSlashified+", concat: "+FilenameUtils.concat(getAREBaseURI().getPath(), resourcePath));
			
			File resourceNameAsFile=new File(resourcePathSlashified);
			//AstericsErrorHandling.instance.getLogger().fine("resourceName: "+resourcePathSlashified+", resourceNameAsFile: "+resourceNameAsFile+", resourceNameAsFile.toURI: "+resourceNameAsFile.toURI());
			
			if(!resourceNameAsFile.isAbsolute()) {
				switch(type) {
				case MODEL:					
					resFilePath=resolveRelativeFilePath(toAbsolute(MODELS_FOLDER), resourcePathSlashified);
					break;
				case PROFILE:
					resFilePath=resolveRelativeFilePath(toAbsolute(PROFILE_FOLDER), resourcePathSlashified);
					break;
				case LICENSE:
					resFilePath=resolveRelativeFilePath(toAbsolute(LICENSES_FOLDER), resourcePathSlashified);
					break;
				case DATA:
					/*
					 * 1) Check resourceName directly, if it exists return
					 * 2) Check resourceName with exactly matching componentTypeId, if it exists return
					 * 3) Check resourceName with first subfolder containing componentTypeId, if it exists return
					 * 4) Check all subfolders (only first level) and resolve against the given resourceName, if it exists return 
					 */
					URI dataFolderURI=toAbsolute(DATA_FOLDER);
					File dataFolderFile=ResourceRegistry.toFile(dataFolderURI);
					
					//1) Check resourceName directly, if it exists return
					resFilePath=resolveRelativeFilePath(dataFolderFile, resourcePathSlashified);
					if(resFilePath.exists()) {
						break;
					}
					
					if(componentTypeId!=null) {
						//2) Check resourceName with exactly matching componentTypeId, if it exists return
						resFilePath=resolveRelativeFilePath(dataFolderFile, resourcePathSlashified);
						if(resFilePath.exists()) {
							break;
						}

						//3) Check resourceName with first subfolder containing componentTypeId (but only last part of asterics.facetrackerLK or sensor.facetrackerLK)
						//if it exists return
						String[] componentTypeIdSplit=componentTypeId.split("\\.");
						//split returns the given string as element 0 if the regex patterns was not found.
						final String componentTypeIdLC=componentTypeIdSplit[componentTypeIdSplit.length-1].toLowerCase();
						
						File[] dataSubFolderFiles=dataFolderFile.listFiles(new FileFilter() {		
							@Override
							public boolean accept(File dirFile) {
								//AstericsErrorHandling.instance.getLogger().fine("Step3, dirFile: "+dirFile);
								if(dirFile.isDirectory() && dirFile.exists()) {
									//lowercase name contains lowercase componentTypeId
									return (dirFile.getName().toLowerCase().indexOf(componentTypeIdLC)>-1);
								}
								return false;
							}
						});
						//AstericsErrorHandling.instance.getLogger().fine("Data, Step3, resourceName="+resourceName+", componentTypeIdLC="+componentTypeIdLC+", runtimeComponentInstanceId="+runtimeComponentInstanceId+", dataSubFolderFiless <"+Arrays.toString(dataSubFolderFiles)+">");
						if(dataSubFolderFiles.length > 0) {
							resFilePath=resolveRelativeFilePath(dataSubFolderFiles[0], resourcePathSlashified);
							if(resFilePath.exists()) {
								break;
							}
						}						
					}
					
					//4) Check all subfolders (only first level) and resolve against the given resourceName, if it exists return
					File[] dataSubFolderFiles=dataFolderFile.listFiles(new FileFilter() {							
						@Override
						public boolean accept(File dirFile) {
							//AstericsErrorHandling.instance.getLogger().fine("Step3, dirFile: "+dirFile);
							if(dirFile.isDirectory() && dirFile.exists()) {
								File resourceFile;
								//resourceFile = toFile(dirFile.toURI().resolve(resourceName));
								resourceFile=resolveRelativeFilePath(dirFile, resourcePathSlashified);
								return resourceFile.exists();
							}
							return false;
						}
					});
					//AstericsErrorHandling.instance.getLogger().fine("Data, Step4, resourceName="+resourceName+", componentTypeId="+componentTypeId+", runtimeComponentInstanceId="+runtimeComponentInstanceId+", dataSubFolderFiless <"+Arrays.toString(dataSubFolderFiles)+">");
					if(dataSubFolderFiles.length > 0) {
						resFilePath=resolveRelativeFilePath(dataSubFolderFiles[0], resourcePathSlashified);
						if(resFilePath.exists()) {
							break;
						}							
					}

					
					break;
				case IMAGE:
					resFilePath=resolveRelativeFilePath(toAbsolute(IMAGES_FOLDER), resourcePathSlashified);
					break;
				case STORAGE:
					resFilePath=resolveRelativeFilePath(toAbsolute(STORAGE_FOLDER), resourcePathSlashified);					
					break;
				default:
					resFilePath=resolveRelativeFilePath(getAREBaseURIFile(), resourcePathSlashified);
					break;
				}
				
				uri=resFilePath.toURI();
			} else {
				uri=resourceNameAsFile.toURI();
			}
		}
		//System.out.println("file absolute: "+resourceNameAsFile.isAbsolute()+", uri absolute: "+uri.isAbsolute()+", uri opaque: "+uri.isOpaque());
		//System.out.println("resource File.toURI: "+resourceNameAsFile.toURI());
		//AstericsErrorHandling.instance.getLogger().fine("URI before normalize: "+uri.normalize());
		if(uri!=null) {
			uri=uri.normalize();
		}
		AstericsErrorHandling.instance.getLogger().fine("resourceName="+resourcePath+", componentTypeId="+componentTypeId+", runtimeComponentInstanceId="+runtimeComponentInstanceId+", Resource URI <"+uri+">");		
		return uri;		
	}	

	/**
	 * Resolves the given nonURIConformingFilePath to the given baseURI URI. The string may contain normal file path characters like space and supports system dependent seperators.
	 * @param baseURI
	 * @param nonURIConformingFilePath
	 * @return
	 */
	public static File resolveRelativeFilePath(URI baseURI, String nonURIConformingFilePath) {
		return resolveRelativeFilePath(baseURI, nonURIConformingFilePath, false);
	}

	/**
	 * Resolves the given nonURIConformingFilePath to the given baseURI URI. The string may contain normal file path characters like space and supports \\ and / as seperators if parameter slashify=true 
	 * @param baseURI
	 * @param nonURIConformingFilePath
	 * @param slashify true: Convert seperators to unix-style / 
	 * @return
	 */
	public static File resolveRelativeFilePath(URI baseURI, String nonURIConformingFilePath, boolean slashify) {
		if(slashify) {
			nonURIConformingFilePath=FilenameUtils.separatorsToUnix(nonURIConformingFilePath);
		}
		File resolvedFile=new File(new File(baseURI),nonURIConformingFilePath);
		return resolvedFile;
	}
	
	/**
	 * Resolves the given nonURIConformingFilePath to the given baseURI URI. The string may contain normal file path characters like space and supports system dependent seperators. 
	 * @param baseURIPath
	 * @param nonURIConformingFilePath
	 * @return
	 */
	public static File resolveRelativeFilePath(File baseURIPath, String nonURIConformingFilePath) {
		return resolveRelativeFilePath(baseURIPath, nonURIConformingFilePath, false);		
	}

	/**
	 * Resolves the given nonURIConformingFilePath to the given baseURI URI. The string may contain normal file path characters like space and supports \\ and / as seperators if parameter slashify=true 
	 * @param baseURIPath
	 * @param nonURIConformingFilePath
	 * @param slashify true: Convert seperators to unix-style /
	 * @return
	 */
	public static File resolveRelativeFilePath(File baseURIPath, String nonURIConformingFilePath, boolean slashify) {
		if(slashify) {
			nonURIConformingFilePath=FilenameUtils.separatorsToUnix(nonURIConformingFilePath);
		}

		File absFile=new File(baseURIPath,nonURIConformingFilePath);
		return absFile;
	}

	/**
	 * Compares equalness of the given uri to the ARE.baseURI. Before comparison the URIs are normalized.
	 * @param uri
	 * @return
	 */
	public boolean equalsAREBaseURI(URI uri) {
		return equalsNormalizedURIs(getAREBaseURI(), uri);
	}
	
	/**
	 * Checks if the given uri points to a subpath of the ARE.baseURI
	 * @param uri
	 * @return
	 */
	public boolean isSubURIOfAREBaseURI(URI uri) {
		return isSubURI(getAREBaseURI(), uri);
	}
	
	/**
	 * Compares equalness of the given URIs. Before comparison the URIs are normalized.
	 * @param first
	 * @param second
	 * @return
	 */
	public static boolean equalsNormalizedURIs(URI first, URI second) {
		return first.normalize().equals(second.normalize());
	}
	
	/**
	 * Checks if the given uri points to a subpath of the given baseURI URI. 
	 * @param baseURI
	 * @param toTest
	 * @return
	 */
	public static boolean isSubURI(URI baseURI, URI toTest) {
		URI relativeURI=baseURI.normalize().relativize(toTest.normalize());
		return !relativeURI.isAbsolute();		
	}
	
	/**
	 * Tests whether the given URI exists by 
	 * 1) testing if it is a File and invoking the File.exists method
	 * 2) trying to open an InputStream
	 * @param uri
	 * @return
	 */
	public static boolean resourceExists(URI uri) {
		try {
			if(ResourceRegistry.toFile(uri).exists()) {
				return true;
			}
		} catch (URISyntaxException e) {
			InputStream in=null;
			try{
				in=uri.toURL().openStream();
				return true;
			} catch (IOException e1) {
			} finally {
				if(in!=null) {
					try {
						in.close();
					} catch (IOException e1) {
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Return the URI according to the given resourcePath string and the given resource type RES_TYPE.
	 * For more details see {@link ResourceRegistry#getResource(String, RES_TYPE, String, String)}.
	 * @param resourcePath
	 * @param type
	 * @return
	 * @throws URISyntaxException
	 */
	public URI getResource(String resourcePath, RES_TYPE type) throws URISyntaxException {
		return getResource(resourcePath, type, null, null);
	}
	
	
	
	/**
	 * Returns an InputStream for the given resourcePath and RES_TYPE.
	 * For more details about resourcePath, see {@link ResourceRegistry#getResource(String, RES_TYPE, String, String)}.
	 * @param resourcePath
	 * @param type
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public InputStream getResourceInputStream(String resourcePath, RES_TYPE type) throws MalformedURLException, IOException, URISyntaxException {
		return getResource(resourcePath, type).toURL().openStream();
	}
		
	/**
	 * Set the base URI of the ARE. This URI will be used as parent path for all resources like models, data, storage,...
	 * Also the jars of the ARE and the plugins are expected in that path.
	 * @param areBaseURI
	 */
	public void setAREBaseURI(URI areBaseURI) {
		System.out.println("Setting ARE base URI to: "+areBaseURI);
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
	/*//Not yet supported
	public void setAREWritableURI(URI writableURI) {
		ARE_WRITABLE_URI=writableURI;
	}*/
	
	/**
	 * Returns the URI for writable storage.
	 * @return
	 */
	/*//Not yet supported
	public URI getAREWritableURI() {
		return ARE_WRITABLE_URI != null ? ARE_WRITABLE_URI : getAREBaseURI();
	}*/

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
	 * This only works if the given URI is a relative path or is a path with a file scheme (starting with: file)
	 * @param uri
	 * @return
	 * @throws URISyntaxException 
	 */
	public static File toFile(URI uri) throws URISyntaxException {
		String scheme=uri.getScheme();
		if(scheme!=null && !scheme.startsWith("file")) {
			throw new URISyntaxException(uri.toString(),"The uri does not start with the scheme <file>");
		}
		File f=new File(uri.getPath());
		return f;
	}
	
	public static Path toPath(URI uri) throws URISyntaxException {
		String scheme=uri.getScheme();
		if(scheme!=null && !scheme.startsWith("file")) {
			throw new URISyntaxException(uri.toString(),"The uri does not start with the scheme <file>");
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
		List<URI> URIs = getLicensesList(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		    	return name.endsWith(".txt");
		    }
		},relative);
		
		return URIs;		
	}
	
	/**
	 * Returns a set of license URIs for the given jar URIs.	 * 
	 * @param allJarURIs
	 * @return
	 */
	public Set<URI> getLicenseURIsofAsTeRICSJarURIs(Set<URI> allJarURIs) {
		Set<URI> licenseURIs=new HashSet<URI>();
		for(URI jarURI : allJarURIs) {			
			//TODO: Think about using toRelativize. 
			//otherwise when just resolving to the name we can support different base URIs as well.
			URI jarNameURI=jarURI.resolve(".").relativize(jarURI);
			String jarName=jarNameURI.getPath();
			
			if(!jarName.startsWith("asterics.")) {
				AstericsErrorHandling.instance.getLogger().fine("Skipping jar for license copying: "+jarName);
				continue;
			}
			int firstDot=jarName.indexOf(".");
			int lastDot=jarName.lastIndexOf(".");
			final String compTypeString=jarName.substring(firstDot+1, lastDot);
			AstericsErrorHandling.instance.getLogger().fine("Searching license for compTypeString="+compTypeString);
			
			List<URI> compLicenseURIs=ResourceRegistry.getInstance().getLicensesList(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					String[] compTypePrefix=name.split("-");
					//Notifier.debug("compTypePrefix: "+compTypePrefix[0]+", compType: "+compTypeString, null);
					return compTypePrefix[0].equalsIgnoreCase(compTypeString) && name.endsWith(".txt");
				}

			},false);
			//Notifier.debug("compType: "+compTypeString+", compLicensURIs: "+compLicenseURIs,null);
			licenseURIs.addAll(compLicenseURIs);

		}
		AstericsErrorHandling.instance.getLogger().fine("Found this license URIs: "+licenseURIs);
		return licenseURIs;
	}	
	
	/**
	 * Returns a list of license URIs corresponding to the given FilenameFilter instance.
	 * @param filter
	 * @param relative
	 * @return
	 */
	public List<URI> getLicensesList(FilenameFilter filter, boolean relative) {
		//get asterics involved LICENSES
		List<URI> URIs = ComponentUtils.findFiles(toAbsolute(LICENSES_FOLDER), relative, 1, filter);
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
		    	return (name.startsWith("asterics.ARE") || name.startsWith("org.eclipse.osgi") || 
		    			(!(name.startsWith("asterics.processor") || name.startsWith("asterics.actuator") || name.startsWith("asterics.sensor") || name.startsWith("asterics.mw") || name.startsWith("asterics.ARE") || name.startsWith("asterics.proxy")) && !DeploymentManager.instance.getBundleManager().checkForServiceBundle(new File(dir,name).toURI())))		    			 
		    			&& name.endsWith(".jar");		    			
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
	 * Returns an array of Strings representing URI paths.
	 * @param uris
	 * @return
	 */
	public static String[] toStringArray(Collection<URI> uris) {
		String[] result=new String[uris.size()];
		int i=0;
		for(URI uri : uris) {
			result[i]=ResourceRegistry.toString(uri);
			i++;
		}
		return result;
	}

	/**
	 * Returns a Set of Strings representing URI paths.
	 * @param uris
	 * @return
	 */
	public static Set<String> toStringSet(Collection<URI> uris) {
		Set<String> result=new TreeSet<String>();	
		for(URI uri : uris) {
			result.add(ResourceRegistry.toString(uri));
		}
		return result;
	}
	
	/**
	 * Returns a List of Strings representing URI paths.
	 * @param uris
	 * @return
	 */
	public static List<String> toStringList(Collection<URI> uris) {
		List<String> result=new ArrayList<String>();	
		for(URI uri : uris) {
			result.add(ResourceRegistry.toString(uri));
		}
		return result;
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
