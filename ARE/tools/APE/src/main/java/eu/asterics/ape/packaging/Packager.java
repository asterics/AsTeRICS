package eu.asterics.ape.packaging;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import static java.nio.file.StandardCopyOption.*;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.osgi.framework.BundleException;
import org.xml.sax.SAXException;

import eu.asterics.ape.main.APE;
import eu.asterics.ape.main.APEConfigurationException;
import eu.asterics.ape.main.APEProperties;
import eu.asterics.ape.main.Notifier;
import eu.asterics.ape.parse.ModelInspector;
import eu.asterics.mw.are.exceptions.BundleManagementException;
import eu.asterics.mw.are.exceptions.ParseException;
import eu.asterics.mw.model.deployment.IRuntimeModel;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.ComponentUtils;
import eu.asterics.mw.services.ResourceRegistry;
import eu.asterics.mw.services.ResourceRegistry.RES_TYPE;

import static eu.asterics.ape.main.APEProperties.*;

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
 * This class performs the packaging and copying of the AsTeRICS URIs to a given project / build location.
 * 
 *         Author: martin.deinhofer@technikum-wien.at
 *         Date: Oct 30, 2015
 *         Time: 14:30:00 PM
 */

public class Packager {
	public static final String BIN_FOLDER = "bin/";
	public static final String MERGED_FOLDER = "merged/";
	public static final String TEMPLATE_FOLDER="template/";
	public static final String CUSTOM_FOLDER="custom/";
	
	public static final String BIN_ARE_FOLDER = BIN_FOLDER+"ARE/";	
	public static final String CUSTOM_BIN_FOLDER = CUSTOM_FOLDER+"bin/";
	public static final String CUSTOM_BIN_ARE_FOLDER=CUSTOM_BIN_FOLDER+"ARE/";
	public static final String CUSTOM_BIN_ARE_MODELS_FOLDER=CUSTOM_BIN_ARE_FOLDER+"models/";
	
	private APEProperties apeProperties=null;
	private ModelInspector modelInspector=null;
	private File projectDir=null;
	private File buildDir=null;
	private File buildMergedDir=null;
	
	/**
	 * Constructs a Packager and configures it with the given Properties instance.
	 * @param apeProperties
	 * @param modelInspector TODO
	 * @throws URISyntaxException 
	 */
	public Packager(APEProperties apeProperties, ModelInspector modelInspector) throws URISyntaxException {
		super();
		this.apeProperties = apeProperties;
		this.modelInspector=modelInspector;
		this.projectDir=new File(apeProperties.getProperty(P_APE_PROJECT_DIR));
		
		buildDir=ResourceRegistry.resolveRelativeFilePath(projectDir, apeProperties.getProperty(APEProperties.P_APE_BUILD_DIR, APEProperties.DEFAULT_BUILD_DIR));
		buildMergedDir=ResourceRegistry.resolveRelativeFilePath(buildDir, MERGED_FOLDER);
		
		Notifier.info("ApeProp["+APEProperties.P_APE_BUILD_DIR+"]="+buildDir);
	}
	
	/**
	 * Copy all files/URIs to project and/or build directories.
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ParseException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws TransformerException
	 * @throws BundleManagementException
	 * @throws APEConfigurationException 
	 */
	public void copyFiles() throws URISyntaxException, MalformedURLException, IOException, ParseException, ParserConfigurationException, SAXException, TransformerException, BundleManagementException, APEConfigurationException {
		//
		File buildMergedAREDir=ResourceRegistry.resolveRelativeFilePath(buildMergedDir, BIN_ARE_FOLDER);
		Notifier.info("Copying files to "+buildMergedAREDir);

		Set<URI> modelURIs=modelInspector.getModelURIsFromProperty();
		Notifier.info("Found model URIs: "+modelURIs);
		if(modelURIs.size()==0) {			
			throw new APEConfigurationException("STOPPING: No model URIs found. Please check value of property "+APEProperties.P_APE_MODELS+": "+apeProperties.getProperty(APEProperties.P_APE_MODELS));
		}
		
		//get model instances
		Set<IRuntimeModel> modelInstances=modelInspector.getIRuntimeModelsOfModelURIs(modelURIs);
		
		//Remember all jar URIs we copied, we need this for fetching the respective license URIs afterwards.
		Set<URI> allJarURIs=new HashSet<URI>();
		Set<URI> componentJarURIs = modelInspector.getComponentTypeJarURIsOfModels(modelInstances);
		allJarURIs.addAll(componentJarURIs);
		copyURIs(componentJarURIs, buildMergedAREDir);
		
		Collection<URI> uriList = copyServices(buildMergedAREDir);
		allJarURIs.addAll(uriList);
		
		uriList = ResourceRegistry.getInstance().getOtherJarList(false);
		allJarURIs.addAll(uriList);
		copyURIs(uriList, buildMergedAREDir);	

		DATA_COPY_MODE dataCopyMode=DATA_COPY_MODE.valueOf(apeProperties.getProperty(P_APE_DATA_COPY_MODE, DATA_COPY_MODE.ALL.toString()).toUpperCase());
		Notifier.info("ApeProp["+P_APE_DATA_COPY_MODE+"]="+dataCopyMode);
		if(DATA_COPY_MODE.ALL.equals(dataCopyMode)) {
			Notifier.info("Copying all data files");
			uriList = ResourceRegistry.getInstance().getDataList(false);
			copyURIs(uriList, buildMergedAREDir);			
		} else if(DATA_COPY_MODE.FOLDER.equals(dataCopyMode) || DATA_COPY_MODE.SINGLE.equals(dataCopyMode)) {
			uriList=modelInspector.getPropertyReferredURIs(modelInstances);
			Notifier.info("Copying the following data files: "+uriList);
			copyURIs(uriList, buildMergedAREDir);
		} else {
			Notifier.info("Don't copy any data files");
		}

		uriList = ResourceRegistry.getInstance().getLicenseURIsofAsTeRICSJarURIs(allJarURIs);
		copyURIs(uriList, buildMergedAREDir);

		uriList = ResourceRegistry.getInstance().getMandatoryProfileConfigFileList(false);
		copyURIs(uriList, buildMergedAREDir);
		
		uriList = ResourceRegistry.getInstance().getAppImagesList(false);
		copyURIs(uriList, buildMergedAREDir);
		
		uriList = ResourceRegistry.getInstance().getOtherFilesList(false);
		copyURIs(uriList, buildMergedAREDir);		

		copyModels(modelURIs, buildMergedAREDir);
		
		//Finally copy all custom files from APE.projectDir/bin
		copyCustomFiles(buildDir);
	}

	/**
	 * Copies all the custom files of the folder APE.projectDir/bin to APE.buildDir/merged/bin
	 * @param buildDir
	 */
	public void copyCustomFiles(File buildDir) {
		File customBinDir=ResourceRegistry.resolveRelativeFilePath(projectDir,CUSTOM_BIN_FOLDER);
		File buildMergedBinDir=ResourceRegistry.resolveRelativeFilePath(buildMergedDir,BIN_FOLDER);
		try {
			Notifier.info("Copying custom files from <"+customBinDir+"> to <"+buildMergedBinDir+">");
			FileUtils.copyDirectory(customBinDir, buildMergedBinDir);
		} catch (IOException e) {
			Notifier.warning("Could not copy custom files of <"+customBinDir+">, to <"+buildMergedBinDir+">", e);			
		}
	}

	/**
	 * Copies the services jars which are found in either the subfolder APE.projectDir/custom//bin/ARE/profile or in ARE.baseURI/profile 
	 * @param targetSubDir
	 */
	public Collection<URI> copyServices(File targetSubDir) {
		List<URI> servicesJars=new ArrayList<URI>();
		
		//Use two-phase approach.
		//1) search in relative custom/bin/ARE/profile folder for files starting with services (excluding config.ini and other files)
		//2) if no files were found there, use the services files of the ARE.baseURI
		File servicesFileDir=ResourceRegistry.resolveRelativeFilePath(projectDir,CUSTOM_BIN_ARE_FOLDER+ResourceRegistry.PROFILE_FOLDER);
		
		FilenameFilter servicesFilesFilter=new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				String lowerName=name.toLowerCase();
				return lowerName.startsWith("services") && lowerName.endsWith(".ini");
			}
		};
		
		Collection<URI> servicesFilesURIs=ComponentUtils.findFiles(servicesFileDir.toURI(),false,1,servicesFilesFilter);

		URI areBaseURIProfileFolder=ResourceRegistry.resolveRelativeFilePath(ResourceRegistry.getInstance().getAREBaseURI(), ResourceRegistry.PROFILE_FOLDER).toURI();
		String message="Using services files in "+areBaseURIProfileFolder;
		if(servicesFilesURIs.size() == 0) {
			servicesFilesURIs=ComponentUtils.findFiles(areBaseURIProfileFolder,false,1,servicesFilesFilter);
		} else {
			message="Using custom services files in "+servicesFileDir;
		}
		Notifier.info(message);
		Notifier.debug("Found services file URIs: "+servicesFilesURIs, null);
		for(URI servicesFile : servicesFilesURIs) {
			try(BufferedReader in=new BufferedReader(new FileReader(new File(servicesFile)));) {
				String path=null;
				while ((path = in.readLine()) != null)
				{
					//sanity check, ignore empty lines and .jar entries
					if(path.equals("")|| !path.endsWith(".jar")) {
						continue;
					}
					try {
						URI jarURI = ResourceRegistry.getInstance().getResource(path, RES_TYPE.JAR);
						servicesJars.add(jarURI);
						copyURI(jarURI, targetSubDir, false);
					} catch (URISyntaxException e) {
						Notifier.warning("Cannot create servicesJarURI for path: "+path, e);
					}catch(IOException e) {
						Notifier.warning("Can not copy service jar: : "+path, e);
					}
				}
			} catch (IOException e) {
				Notifier.warning("Cannot open services-ini file: "+servicesFile, e);
			}
		}
		return servicesJars;
	}
	
	/**
	 * Copy the given models to the given directory. Also automatically resolves subdirs of bin/ARE to appropriate subdirs in a target directory.
	 * @param modelURIs
	 * @param targetSubDir
	 */
	public void copyModels(Set<URI> modelURIs, File targetSubDir) {
		URI customURI=ResourceRegistry.resolveRelativeFilePath(projectDir,CUSTOM_BIN_ARE_MODELS_FOLDER).toURI();
		for(URI modelURI : modelURIs) {
			//Check if it is a model URI based on ARE base URI, if not copy file directly
			try {
				//Don't resolve against ARE.baseURI because we just wanna copy the model files to the bin/ARE/models dir.
				if(ResourceRegistry.isSubURI(customURI, modelURI)) {
					Notifier.debug("Don't copy custom model in copyModels, will be copied in copyCustomFiles: "+modelURI, null);
					continue;
				}
				copyURI(modelURI,targetSubDir, true);
			} catch (URISyntaxException | IOException e) {
				Notifier.warning("Could not copy model: "+modelURI, e);
			}
		}
	}
		
	/**
	 * Copyies all given URIs to the given target directory. Also automatically resolves subdirs of bin/ARE to appropriate subdirs in a target directory.
	 * @param srcURIs
	 * @param targetDir
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public void copyURIs(Collection<URI> srcURIs, File targetDir) throws URISyntaxException, IOException {
		for(URI srcURI : srcURIs) {
			copyURI(srcURI,targetDir, true);
		}		
	}

	/**
	 * Copyies the given srcURI to the given targetDir directory. 
	 * @param srcURI
	 * @param targetDir
	 * @param resolveTargetSubDirs: true: Resolves subdirs of bin/ARE to appropriate subdirs in a target directory.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public void copyURI(URI srcURI, File targetDir, boolean resolveAREBaseURISubDirs) throws URISyntaxException, IOException {
		try {
			File targetSubDir=targetDir;
			File src=ResourceRegistry.toFile(srcURI);
			
			//If we should resolve against ARE subfolders
			if(resolveAREBaseURISubDirs) {
				//Only works if the URI contains the currently active ARE.baseURI.
				//Determine relative src dir which will then be resolved against the base target dir.
				File relativeSrc=ResourceRegistry.toFile(ResourceRegistry.getInstance().toRelative(srcURI));

				//Determine parent folder of relativeSrc File
				targetSubDir=src.isDirectory() ? relativeSrc : relativeSrc.getParentFile();

				if(targetSubDir!=null) {
					//Resolve targetSubDir against targetDir
					targetSubDir=ResourceRegistry.resolveRelativeFilePath(targetDir,targetSubDir.getPath());
				} else {
					targetSubDir=targetDir;
				}
			}
			//Actually copy file
			Notifier.debug("Copying "+src+" -> "+targetSubDir,null);
			if(src.isDirectory()) {
				FileUtils.copyDirectory(src, targetSubDir);
			} else {
				FileUtils.copyFileToDirectory(src, targetSubDir);
			}
		} catch(MalformedURLException e) {
			//else try if it is a URL that can be fetched from anywhere else.
			Notifier.warning("URL resources not supported so far",e);
		}
	}
	
	/**
	 * Does all steps of a build/make process. Fetching target dir properties, creating project/build directories, copying all URIs.
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws ParseException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws TransformerException
	 * @throws BundleManagementException
	 * @throws APEConfigurationException 
	 */
	public void makeAll() throws IOException, URISyntaxException, ParseException, ParserConfigurationException, SAXException, TransformerException, BundleManagementException, APEConfigurationException {
		try{
			Notifier.debug("Deleting APE.buildDir before copying: "+buildDir,null);
			FileUtils.forceDelete(buildDir);
		}catch(IOException e) {
			Notifier.warning("Could not delete APE.buildDir: "+buildDir,e);
		}

		copyFiles();
		Notifier.info("FINISHED copying ARE: Go to the following folder and try it out: "+buildMergedDir);
	}
}
