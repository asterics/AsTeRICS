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
 * This class performs the packaging and copying of the AsTeRICS URIs to a given project / build location.
 * 
 *         Author: martin.deinhofer@technikum-wien.at
 *         Date: Oct 30, 2015
 *         Time: 14:30:00 PM
 */

public class Packager {
	private String templateName="template";
	private APEProperties apeProperties=null;
	private ModelInspector modelInspector=null;
	private Path projectDir=null;
	private Path buildDir=null;
	private Path buildMergedDir=null;
	
	/**
	 * Constructs a Packager and configures it with the given Properties instance.
	 * @param apeProperties
	 * @param modelInspector TODO
	 */
	public Packager(APEProperties apeProperties, ModelInspector modelInspector) {
		super();
		this.apeProperties = apeProperties;
		this.modelInspector=modelInspector;
	}

	/* Maybe neede later, if we have to create a project directory
	public void copyAndExtractTemplate(Path targetBaseDir) throws IOException {
		File templateDir=new File(APEProperties.APE_BASE_URI.resolve(templateName));
		
		try{
			Files.deleteIfExists(targetBaseDir);
		}catch(Exception e) {			
			AstericsErrorHandling.instance.getLogger().warning("Could not delete target base directory: "+e.getMessage());
		}
		CopyOption[] opt=new CopyOption[] {REPLACE_EXISTING,COPY_ATTRIBUTES};
		if(Files.notExists(targetBaseDir)) {
			Files.createDirectories(targetBaseDir);
		}
		try{		
			Files.copy(templateDir.toPath(),targetBaseDir,opt);
		}catch(Exception e) {			
			AstericsErrorHandling.instance.getLogger().warning("Could not copy template to target base directory: "+e.getMessage());
		}
	}
	*/
	
	/**
	 * Copy all files/URIs to project and/or build directories.
	 * @param projectDir
	 * @param buildDir
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ParseException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws TransformerException
	 * @throws BundleManagementException
	 */
	public void copyFiles(Path projectDir, Path buildDir) throws URISyntaxException, MalformedURLException, IOException, ParseException, ParserConfigurationException, SAXException, TransformerException, BundleManagementException {
		//
		Path buildMergedAREDir=buildMergedDir.resolve("bin/ARE/");
		Notifier.info("Copying files to "+buildMergedAREDir);

		Set<URI> modelURIs=modelInspector.getModelURIsFromProperty();
		Notifier.info("Found model URIs:\n"+modelURIs);
		
		//get model instances
		Set<IRuntimeModel> modelInstances=modelInspector.getIRuntimeModelsOfModelURIs(modelURIs);
		
		//Remember all jar URIs we copied, we need this for fetching the respective license URIs afterwards.
		Set<URI> allJarURIs=new HashSet<URI>();
		Set<URI> componentJarURIs = modelInspector.getComponentTypeJarURIsOfModels(modelInstances);
		allJarURIs.addAll(componentJarURIs);
		copyURIs(componentJarURIs, buildMergedAREDir);
		
		//Collection<URI> uriList = ResourceRegistry.getInstance().getServicesJarList(false);
		//copyURIs(uriList, buildMergedAREDir);
		Collection<URI> uriList = copyServices(buildMergedAREDir);
		allJarURIs.addAll(uriList);
		
		uriList = ResourceRegistry.getInstance().getOtherJarList(false);
		allJarURIs.addAll(uriList);
		copyURIs(uriList, buildMergedAREDir);	

		uriList = ResourceRegistry.getInstance().getDataList(false);
		copyURIs(uriList, buildMergedAREDir);

		//uriList = modelInspector.getLicenseURIsOfModels(modelInstances);
		uriList = ResourceRegistry.getInstance().getLicenseURIsofAsTeRICSJarURIs(allJarURIs);
		copyURIs(uriList, buildMergedAREDir);

		uriList = ResourceRegistry.getInstance().getMandatoryProfileConfigFileList(false);
		copyURIs(uriList, buildMergedAREDir);
		
		uriList = ResourceRegistry.getInstance().getAppImagesList(false);
		copyURIs(uriList, buildMergedAREDir);
		
		uriList = ResourceRegistry.getInstance().getOtherFilesList(false);
		copyURIs(uriList, buildMergedAREDir);		

		copyModels(modelURIs, buildMergedAREDir.resolve(ResourceRegistry.MODELS_FOLDER));
		
		//Finally copy all custom files from APE.projectDir/bin
		copyCustomFiles(buildDir);
	}

	/**
	 * Copies all the custom files of the folder APE.projectDir/bin to APE.buildDir/merged/bin
	 * @param buildDir
	 */
	public void copyCustomFiles(Path buildDir) {
		Path customBinDir=projectDir.resolve("bin");
		Path buildMergedBinDir=buildMergedDir.resolve("bin");
		try {
			FileUtils.copyDirectory(customBinDir.toFile(), buildMergedBinDir.toFile());
		} catch (IOException e) {
			Notifier.warning("Could not copy custom files of <"+customBinDir+">, to <"+buildMergedBinDir+">", e);			
		}
	}

	/**
	 * Copies the services jars which are found in either the subfolder APE.projectDir/bin/ARE/profile or in ARE.baseURI/profile 
	 * @param targetSubDir
	 */
	public Collection<URI> copyServices(Path targetSubDir) {
		List<URI> servicesJars=new ArrayList<URI>();
		
		//Use two-phase approach.
		//1) search in relative bin/ARE/profile folder for files starting with services (excluding config.ini and other files)
		//2) if no files were found there, use the services files of the ARE.baseURI
		Path servicesFileDir=projectDir.resolve("bin/ARE/"+ResourceRegistry.PROFILE_FOLDER);
		
		FilenameFilter servicesFilesFilter=new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				String lowerName=name.toLowerCase();
				return lowerName.startsWith("services") && lowerName.endsWith(".ini");
			}
		};
		
		Collection<URI> servicesFilesURIs=ComponentUtils.findFiles(servicesFileDir.toUri(),false,1,servicesFilesFilter);
		
		if(servicesFilesURIs.size() == 0) {
			servicesFilesURIs=ComponentUtils.findFiles(ResourceRegistry.getInstance().getAREBaseURI().resolve(ResourceRegistry.PROFILE_FOLDER),false,1,servicesFilesFilter);
			Notifier.info("Using services files of ARE.baseURI");
		} else {
			Notifier.info("Using custom services files in "+servicesFileDir);
		}
		Notifier.info("Using services files: "+servicesFilesURIs);
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
	public void copyModels(Set<URI> modelURIs, Path targetSubDir) {
		for(URI modelURI : modelURIs) {
			//Check if it is a model URI based on ARE base URI, if not copy file directly
			try {

				/*
				if(ResourceRegistry.getInstance().toRelative(modelURI).isAbsolute()) {
					//if model URI is still absolute it could not be resolved against the ARE base URI.
					copyURI(modelURI,targetSubDir.resolve(ResourceRegistry.MODELS_FOLDER), false);
				} else {
				*/
				//Don't resolve against ARE.baseURI because we just wanna copy the model files to the bin/ARE/models dir.
				copyURI(modelURI,targetSubDir, false);
				//}
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
	public void copyURIs(Collection<URI> srcURIs, Path targetDir) throws URISyntaxException, IOException {
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
	public void copyURI(URI srcURI, Path targetDir, boolean resolveAREBaseURISubDirs) throws URISyntaxException, IOException {
		CopyOption[] opt=new CopyOption[] {REPLACE_EXISTING,COPY_ATTRIBUTES};
		try {
			Path targetSubDir=targetDir;
			Path src=ResourceRegistry.toPath(srcURI);
			
			//If we should resolve against ARE subfolders
			if(resolveAREBaseURISubDirs) {
				//Only works if the URI contains the currently active ARE.baseURI.
				Path relativeSrc=ResourceRegistry.toPath(ResourceRegistry.getInstance().toRelative(srcURI));

				//Determine relative src dir which will then be resolved against the base target dir. 
				targetSubDir=Files.isDirectory(src) ? relativeSrc : relativeSrc.getParent();

				if(targetSubDir!=null) {
					targetSubDir=targetDir.resolve(targetSubDir);
				} else {
					targetSubDir=targetDir;
				}
			}
			//Create target directories recursively, if they don't exist
			if(Files.notExists(targetSubDir)) {
				Files.createDirectories(targetSubDir);
			}
			//Actually copy file
			Notifier.info("Copying "+src.getFileName()+" -> "+targetSubDir);
			//Try to copy on filesystem basis. because this is much more convinient and faster for sure.
			Files.copy(src, targetSubDir.resolve(src.getFileName()),opt);
		} catch(MalformedURLException e) {
			//else try if it is a URL that can be fetched from anywhere else.
			AstericsErrorHandling.instance.getLogger().warning("URL resources not supported so far: "+e.getMessage());
			//Files.copy(srcURI.toURL().openStream(), targetBaseDir.resolve(srcURI.getPath()));
		}
	}
	
	public void generateFileLists(Path targetBaseDir) {
		
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
	 */
	public void makeAll() throws IOException, URISyntaxException, ParseException, ParserConfigurationException, SAXException, TransformerException, BundleManagementException {
		File projectFileObject=new File(apeProperties.APE_PROP_FILE_BASE_URI.resolve(apeProperties.getProperty(APEProperties.P_APE_PROJECT_DIR,APEProperties.DEFAULT_PROJECT_DIR)));
		projectDir=projectFileObject.toPath();
		Notifier.info("Using ApeProp["+APEProperties.P_APE_PROJECT_DIR+"]="+projectDir);
		
		//We always have to resolve against a baseURI object because the Path implementation does not allow file:/// URI syntax, just OS-specific path styles.
		buildDir=Paths.get(projectDir.toUri().resolve(apeProperties.getProperty(APEProperties.P_APE_BUILD_DIR, APEProperties.DEFAULT_BUILD_DIR)));
		buildMergedDir = buildDir.resolve("merged");
		Notifier.info("Using ApeProp["+APEProperties.P_APE_BUILD_DIR+"]="+buildDir);
		Notifier.info("Deleting APE.buildDir before copying: "+buildDir);
		try{
			FileUtils.forceDelete(buildDir.toFile());
		}catch(IOException e) {
			Notifier.warning("Could not delete APE.buildDir: "+buildDir,e);
		}

		//copyAndExtractTemplate(targetBaseDir);
		copyFiles(projectDir, buildDir);
		//generateFileLists(projectDir);
		
		Notifier.info("FINISHED Copying ARE: Go to the following folder and try it out: "+buildMergedDir);
	}
}
