package eu.asterics.mw.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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
		try{
			uri=new URI(resourceName);
		} catch(URISyntaxException ue) {			
			uri=Paths.get(resourceName).toAbsolutePath().toUri();
		}
		
		//In case of model files, prefix the MODELS_FOLDER if the path is relative.
		if(type==RES_TYPE.MODEL && !uri.isAbsolute()) {
			uri=Paths.get(MODELS_FOLDER,resourceName).toAbsolutePath().toUri();
		}
		uri=uri.normalize();
		AstericsErrorHandling.instance.getLogger().info("Fetching <"+uri+">");		
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

}
