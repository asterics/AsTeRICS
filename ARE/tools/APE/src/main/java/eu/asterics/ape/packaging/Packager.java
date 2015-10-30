package eu.asterics.ape.packaging;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import eu.asterics.ape.main.APE;
import eu.asterics.mw.are.exceptions.ParseException;
import eu.asterics.mw.model.deployment.IRuntimeModel;
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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 *
 */

/**
 * 
 *         Author: martin.deinhofer@technikum-wien.at
 *         Date: Oct 30, 2015
 *         Time: 14:30:00 PM
 */

public class Packager {
	private String templateName="template";
	public void copyAndExtractTemplate(File targetDir) throws IOException {
		//JarFile template=new JarFile(APE.getAPEBaseURI().resolve(templateName).getPath());
		File templateDir=new File(APE.getAPEBaseURI().resolve(templateName));
		Files.deleteIfExists(targetDir.toPath());
		Files.copy(templateDir.toPath(),targetDir.toPath());
	}
	
	public void copyFiles(File targetDir) throws URISyntaxException, MalformedURLException, IOException, ParseException, ParserConfigurationException, SAXException, TransformerException {
		Path targetSubDir=Paths.get(targetDir.toURI().resolve("bin/ARE"));
		URI testModel = ResourceRegistry.getInstance().getResource("CameraMouse.acs", RES_TYPE.MODEL);
		InputStream iStr=testModel.toURL().openStream();
		IRuntimeModel model=APE.getInstance().getModelInspector().parseModel(iStr);

		Set<URI> componentJarURIs = APE.getInstance().getModelInspector().getComponentJarURIsOfModel(model);
		for(URI uri : componentJarURIs) {
			Files.copy(uri.toURL().openStream(), targetSubDir.resolve(uri.getPath()));
		}
	}
	
	public void generateFileLists(File targetDir) {
		
	}
	
	public void makeAll(File targetDir) throws IOException, URISyntaxException, ParseException, ParserConfigurationException, SAXException, TransformerException {
		copyAndExtractTemplate(targetDir);
		copyFiles(targetDir);
		generateFileLists(targetDir);
	}
}
