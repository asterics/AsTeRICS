package eu.asterics.mw.services;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.*;
import java.nio.file.Paths;

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
 * This class provides is the central point to find and fetch resources used by the ARE middleware, osgi services and osgi plugins.
 * The idea is to generically implement the fetching of resources to enable the same approach for the whole AsTeRICS. This way all plugins will be able to also support
 * URI schemes (http, jar,...).
 * 
 *         Author: martin.deinhofer@technikum-wien.at
 *         Date: Oct 11, 2015
 *         Time: 00:17:00 AM
 */

public class ResourceRegistry {
	public static ResourceRegistry instance=new ResourceRegistry();
	// todo replace with ComponentRepository
	public static final String MODELS_FOLDER = "models";
	public static final String DATA_FOLDER = "data";
	public static final String PROFILE_FOLDER = "profile";
	public static final String STORAGE_FOLDER = "storage";
	private static URI ARE_BASE_URI = null;
	
	static {
		ARE_BASE_URI=URI.create(System.getProperty("eu.asterics.ARE.baseURI", ResourceRegistry.instance.getClass().getProtectionDomain().getCodeSource().getLocation().toString()));
		System.out.println("Setting ARE base URI to <"+ARE_BASE_URI+">");		
	}
		
	public enum RES_TYPE {
		ANY,
		MODEL,
		DATA
	};
	
	public ResourceRegistry getInstance() {
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
			//In case of model files, prefix the MODELS_FOLDER if the path is relative.
			if(type==RES_TYPE.MODEL && !resourceNameAsFile.isAbsolute()) {
				AstericsErrorHandling.instance.getLogger().fine("Prepanding "+MODELS_FOLDER+" to URI: "+resourceNameAsFile);
				uri=new File(MODELS_FOLDER,resourceName).getAbsoluteFile().toURI();
				//uri=Paths.get(MODELS_FOLDER,resourceName).toAbsolutePath().toUri();
			} else {
				uri=resourceNameAsFile.toURI();
			}
		}
		//System.out.println("file absolute: "+resourceNameAsFile.isAbsolute()+", uri absolute: "+uri.isAbsolute()+", uri opaque: "+uri.isOpaque());
		//System.out.println("resource File.toURI: "+resourceNameAsFile.toURI());
		AstericsErrorHandling.instance.getLogger().fine("URI before normalize: "+uri.normalize());
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
	public static void setAREBaseURI(URI areBaseURI) {
		ARE_BASE_URI=areBaseURI;
	}
	
	/**
	 * Get the base URI of the ARE. This URI will be used as parent path for all resources like models, data, storage,...
	 * Also the jars of the ARE and the plugins are expected in that path.
	 * The default base URI is the location of the ARE.jar file. It can be changed by the property eu.asterics.ARE.baseURI or by using the method {@link setAREBaseURI}.
	 * @return
	 */
	public static URI getAREBaseURI() {		
		return ARE_BASE_URI;		
	}

	/**
	 * Converts the given absolutePath to a path relative to the ARE base URI {@link getAREBaseURI}.
	 * @param absolutePath
	 * @return
	 */
	public static URI toRelative(String absolutePath) {
		return getAREBaseURI().relativize(URI.create(absolutePath)); 
	}

	/**
	 * Converts the given relativePath to an absolute path by resolving with the ARE base URI {@link getAREBaseURI}.
	 * @param relativePath
	 * @return
	 */
	public static URI toAbsolute(String relativePath) {
		return getAREBaseURI().resolve(relativePath);
	}

}
