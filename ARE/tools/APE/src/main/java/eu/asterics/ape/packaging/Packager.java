package eu.asterics.ape.packaging;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import static java.nio.file.StandardCopyOption.*;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import eu.asterics.ape.main.APE;
import eu.asterics.mw.are.exceptions.BundleManagementException;
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
		
		Path targetPath=targetDir.toPath();
		try{
			Files.deleteIfExists(targetDir.toPath());
		}catch(IOException ie) {			
		}
		CopyOption[] opt=new CopyOption[] {REPLACE_EXISTING,COPY_ATTRIBUTES};
		if(Files.notExists(targetPath)) {
			Files.createDirectories(targetPath);
		}

		
		Files.copy(templateDir.toPath(),targetDir.toPath(),opt);
	}
	
	public void copyFiles(File targetBBDir) throws URISyntaxException, MalformedURLException, IOException, ParseException, ParserConfigurationException, SAXException, TransformerException, BundleManagementException {
		Path targetSubDir=Paths.get(targetBBDir.toURI().resolve("bin/ARE/"));
		//Path targetSubDir=Paths.get(targetDir.toURI());
		URI testModel = ResourceRegistry.getInstance().getResource("CameraMouse.acs", RES_TYPE.MODEL);
		InputStream iStr=testModel.toURL().openStream();
		IRuntimeModel model=APE.getInstance().getModelInspector().parseModel(iStr);

		Set<URI> componentJarURIs = APE.getInstance().getModelInspector().getComponentJarURIsOfModel(model);
		copyURIs(componentJarURIs, targetSubDir);
		
		List<URI> uriList = ResourceRegistry.getInstance().getServicesJarList(false);
		copyURIs(uriList, targetSubDir);
		
		uriList = ResourceRegistry.getInstance().getOtherJarList(false);
		copyURIs(uriList, targetSubDir);	

		uriList = ResourceRegistry.getInstance().getDataList(false);
		copyURIs(uriList, targetSubDir);

		uriList = ResourceRegistry.getInstance().getLicensesList(false);
		copyURIs(uriList, targetSubDir);

		uriList = ResourceRegistry.getInstance().getMandatoryProfileConfigFileList(false);
		copyURIs(uriList, targetSubDir);
		
		uriList = ResourceRegistry.getInstance().getAppImagesList(false);
		copyURIs(uriList, targetSubDir);
		
		uriList = ResourceRegistry.getInstance().getOtherFilesList(false);
		copyURIs(uriList, targetSubDir);		

		copyURI(testModel,targetSubDir);
	}
	
	public void copyURIs(Set<URI> srcURIs, Path targetBaseDir) throws URISyntaxException, IOException {
		for(URI srcURI : srcURIs) {
			copyURI(srcURI,targetBaseDir);
		}		
	}
	
	public void copyURIs(List<URI> srcURIs, Path targetBaseDir) throws URISyntaxException, IOException {
		for(URI srcURI : srcURIs) {
			copyURI(srcURI,targetBaseDir);
		}		
	}
	
	public void copyURI(URI srcURI, Path targetBaseDir) throws URISyntaxException, IOException {
		CopyOption[] opt=new CopyOption[] {REPLACE_EXISTING,COPY_ATTRIBUTES};
		try {
			//first try to copy on filesystem basis. because this is much more convinient and faster for sure.
			Path relativeSrc=ResourceRegistry.toPath(ResourceRegistry.getInstance().toRelative(srcURI));
			Path src=ResourceRegistry.toPath(srcURI);
			
			//Determine relative src dir which will then be resolved against the base target dir. 
			Path targetDir=Files.isDirectory(src) ? relativeSrc : relativeSrc.getParent();

			if(targetDir!=null) {
				targetDir=targetBaseDir.resolve(targetDir);
			} else {
				targetDir=targetBaseDir;
			}
			//Create target directories recursively, if they don't exist
			if(Files.notExists(targetDir)) {
				Files.createDirectories(targetDir);
			}
			//Actually copy file
			Files.copy(src, targetDir.resolve(src.getFileName()),opt);
		} catch(MalformedURLException e) {
			//else try if it is a URL that can be fetched from anywhere else.
			Files.copy(srcURI.toURL().openStream(), targetBaseDir.resolve(srcURI.getPath()));
		}
	}
	
	public void generateFileLists(File targetDir) {
		
	}
	
	public void makeAll(File targetDir) throws IOException, URISyntaxException, ParseException, ParserConfigurationException, SAXException, TransformerException, BundleManagementException {
		copyAndExtractTemplate(targetDir);
		copyFiles(targetDir);
		generateFileLists(targetDir);
	}
}
