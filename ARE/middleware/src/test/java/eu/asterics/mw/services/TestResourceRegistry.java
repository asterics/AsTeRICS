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
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.mw.services;

import static org.junit.Assert.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;

import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.asterics.mw.are.BundleManager;
import eu.asterics.mw.are.DeploymentManager;
import eu.asterics.mw.are.parsers.DefaultDeploymentModelParser;
import eu.asterics.mw.are.parsers.ModelValidator;
import eu.asterics.mw.services.ResourceRegistry.RES_TYPE;
import eu.asterics.mw.utils.OSUtils;
import jersey.repackaged.com.google.common.collect.Lists;

public class TestResourceRegistry {
	ModelValidator modelValidator=null;
	DefaultDeploymentModelParser deploymentModelParser=null;
	BundleManager bundleManager=null;

	@Before
	public void setUp() throws Exception {
		ResourceRegistry.getInstance().setOSGIMode(false);
		ResourceRegistry.getInstance().setAREBaseURI(new File("../bin/ARE").toURI());
		
		modelValidator=new ModelValidator();
		deploymentModelParser=DefaultDeploymentModelParser.create(modelValidator);
		bundleManager=new BundleManager(modelValidator);
		bundleManager.createComponentListCache();		
		DeploymentManager.instance.setBundleManager(bundleManager);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetResource() {
		try {
			URI areBaseURI=ResourceRegistry.getInstance().getAREBaseURI();
			URI dataBaseURI=ResourceRegistry.resolveRelativeFilePath(areBaseURI, ResourceRegistry.DATA_FOLDER).toURI();
			URI modelBaseURI=ResourceRegistry.resolveRelativeFilePath(areBaseURI, ResourceRegistry.MODELS_FOLDER).toURI();
					
					
			//Test absolute file URI:
			//The given URI conform string should be taken without modification and returned as URI instance.
			String testURIString=ResourceRegistry.getInstance().getAREBaseURI().toString();
			URI actual=ResourceRegistry.getInstance().getResource(testURIString, RES_TYPE.ANY);
			assertTrue(ResourceRegistry.equalsNormalizedURIs(areBaseURI, actual));
			
			//Test http:// URL
			testURIString="http://www.asterics-academy.net/test";		
			actual=ResourceRegistry.getInstance().getResource(testURIString, RES_TYPE.ANY);
			assertTrue(ResourceRegistry.equalsNormalizedURIs(new URI(testURIString), actual));
						
			//Test relative resource, will be treated as a file
			//Should be found pictures subfolder directly through step1
			testURIString="pictures/slide7.jpg";
			actual=ResourceRegistry.getInstance().getResource(testURIString, RES_TYPE.DATA);
			assertTrue(ResourceRegistry.equalsNormalizedURIs(ResourceRegistry.resolveRelativeFilePath(dataBaseURI, testURIString,true).toURI(), actual));
			
			//Should be found in pictures subfolder, through step2
			testURIString="slide7.jpg";
			actual=ResourceRegistry.getInstance().getResource(testURIString, RES_TYPE.DATA,"pictures",null);
			assertTrue(ResourceRegistry.equalsNormalizedURIs(ResourceRegistry.resolveRelativeFilePath(dataBaseURI, "pictures/"+testURIString,true).toURI(), actual)); 
			
			//Should be found in pictures subfolder, through step3
			testURIString="slide7.jpg";
			actual=ResourceRegistry.getInstance().getResource(testURIString, RES_TYPE.DATA,"Pic",null);
			assertTrue(ResourceRegistry.equalsNormalizedURIs(ResourceRegistry.resolveRelativeFilePath(dataBaseURI, "pictures/"+testURIString,true).toURI(), actual)); 			

			//Should be found in pictures subfolder, through step4
			testURIString="slide7.jpg";
			actual=ResourceRegistry.getInstance().getResource(testURIString, RES_TYPE.DATA);
			assertTrue(ResourceRegistry.equalsNormalizedURIs(ResourceRegistry.resolveRelativeFilePath(dataBaseURI, "pictures/"+testURIString,true).toURI(), actual)); 
			
			//Test spaces and \\ in string (also interesting to test it on Linux)
			testURIString="symbols\\fill glass with water.png";
			actual=ResourceRegistry.getInstance().getResource(testURIString, RES_TYPE.DATA);
			assertTrue(ResourceRegistry.equalsNormalizedURIs(ResourceRegistry.resolveRelativeFilePath(dataBaseURI, "pictures\\"+testURIString, true).toURI(), actual));
			
			//Test providing componentTypeId of facetrackerLK
			testURIString="haarcascade_frontalface_alt.xml";
			actual=ResourceRegistry.getInstance().getResource(testURIString,RES_TYPE.DATA,"asterics.FacetrackerLK",null);
			assertTrue(ResourceRegistry.equalsNormalizedURIs(ResourceRegistry.resolveRelativeFilePath(dataBaseURI, "sensor.facetrackerLK/"+testURIString,true).toURI(), actual));

			//Test models
			testURIString="CameraMouse.acs";
			actual=ResourceRegistry.getInstance().getResource(testURIString,RES_TYPE.MODEL,null,null);
			assertTrue(ResourceRegistry.equalsNormalizedURIs(ResourceRegistry.resolveRelativeFilePath(modelBaseURI, testURIString, true).toURI(), actual));
			
			testURIString="grids\\eyeX_Environment\\eyeX_Environment.acs";
			actual=ResourceRegistry.getInstance().getResource(testURIString,RES_TYPE.MODEL,null,null);
			assertTrue(ResourceRegistry.equalsNormalizedURIs(ResourceRegistry.resolveRelativeFilePath(modelBaseURI, testURIString,true).toURI(), actual));						
			
			//Test absolute file path, should be returned as valid absolute URI
			if(OSUtils.isWindows()) {
				testURIString="C:\\Program Files (x86)\\eclipse";
			} else {
				testURIString="/var/log/messages";
			}
			actual=ResourceRegistry.getInstance().getResource(testURIString,RES_TYPE.ANY,null,null);
			assertTrue(ResourceRegistry.equalsNormalizedURIs(new File(testURIString).toURI(), actual));						

			//System.out.println("testGetResource: "+arebaseURI);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Test
	public void testGetResourceInputStream() {
		//fail("Not yet implemented");
	}
	
	

	@Test
	public void testGetAREBaseURI() {
		System.out.println("baseURI: "+ResourceRegistry.getInstance().getAREBaseURI());		
	}

	@Test
	public void testRelativeToAbsoluteToRelative() {
		URI relative=URI.create(ResourceRegistry.MODELS_FOLDER);
		URI absolute=ResourceRegistry.getInstance().toAbsolute(ResourceRegistry.MODELS_FOLDER);
		URI convertedRelative=ResourceRegistry.getInstance().toRelative(absolute.toString());
		if(!relative.equals(convertedRelative)) {
			fail("Testing URI toAbsolute and back toRelative failed: original <"+relative+">, convertedRelative <"+convertedRelative+">");
		}
	}
	
	@Test
	public void testequalsAREBaseURI() {
		URI relative=URI.create(ResourceRegistry.MODELS_FOLDER);
		URI absolute=ResourceRegistry.getInstance().toAbsolute(ResourceRegistry.MODELS_FOLDER);

		assertTrue(ResourceRegistry.getInstance().equalsAREBaseURI(ResourceRegistry.getInstance().getAREBaseURI()));
		assertFalse(ResourceRegistry.getInstance().equalsAREBaseURI(absolute));
	}
	
	@Test
	public void testisSubURIOfAREBaseURI() {
		URI relative=URI.create(ResourceRegistry.MODELS_FOLDER);
		URI absolute=ResourceRegistry.getInstance().toAbsolute(ResourceRegistry.MODELS_FOLDER);

		assertTrue(ResourceRegistry.getInstance().isSubURIOfAREBaseURI(absolute));
		assertFalse(ResourceRegistry.getInstance().isSubURIOfAREBaseURI(absolute.resolve("../../")));
	}
	
	//Don't annotate as test, because it's not easily possible to set a crossplatform absolute path.
	@Test
	public void testSetAREBaseURI() throws URISyntaxException {
		//Test getting and relative to absolute
		testGetAREBaseURI();
		testRelativeToAbsoluteToRelative();
		
		//change base URI
		String testURIString="C:\\Program Files (x86)\\eclipse\\";
		if(!OSUtils.isWindows()) {
			testURIString="/var/log/";
		}

		testURIString=FilenameUtils.normalize(testURIString);
		URI newURI;
		newURI=new File(testURIString).toURI();
		System.out.println("Setting new AREBaseURI to <"+newURI.getPath()+">");
		URI oldURI=ResourceRegistry.getInstance().getAREBaseURI();
		ResourceRegistry.getInstance().setAREBaseURI(newURI);
		
		//Test getting and relative to absolute again
		testGetAREBaseURI();
		testRelativeToAbsoluteToRelative();
		
		//Set old URI again to don't influence other tests.
		ResourceRegistry.getInstance().setAREBaseURI(oldURI);
	}

	
	@Test
	public void testToRelative() throws URISyntaxException {
		String expected="autostart.acs";
		URI autostartModelURI=ResourceRegistry.getInstance().getResource(expected, RES_TYPE.MODEL);
		URI actual=ResourceRegistry.getInstance().toRelative(autostartModelURI, RES_TYPE.MODEL);
		assertEquals(expected, actual.getPath());
		
		expected="sensor.facetrackerLK/haarcascade_frontalface_alt.xml";
		autostartModelURI=ResourceRegistry.getInstance().getResource(expected, RES_TYPE.DATA);
		actual=ResourceRegistry.getInstance().toRelative(autostartModelURI, RES_TYPE.DATA);
		assertEquals(expected, actual.getPath());

		expected="neural networks/Example.eg";
		autostartModelURI=ResourceRegistry.getInstance().getResource(expected, RES_TYPE.DATA);
		actual=ResourceRegistry.getInstance().toRelative(autostartModelURI, RES_TYPE.DATA);
		assertEquals(expected, actual.getPath());
		
		expected="ARE.exe";
		autostartModelURI=ResourceRegistry.getInstance().getResource(expected, RES_TYPE.ANY);
		actual=ResourceRegistry.getInstance().toRelative(autostartModelURI, RES_TYPE.ANY);
		assertEquals(expected, actual.getPath());
		
		expected="ARE-LICENSE_MITOrGPLv3WithException.txt";
		autostartModelURI=ResourceRegistry.getInstance().getResource(expected, RES_TYPE.LICENSE);
		actual=ResourceRegistry.getInstance().toRelative(autostartModelURI, RES_TYPE.LICENSE);
		assertEquals(expected, actual.getPath());

		expected="services.ini";
		autostartModelURI=ResourceRegistry.getInstance().getResource(expected, RES_TYPE.PROFILE);
		actual=ResourceRegistry.getInstance().toRelative(autostartModelURI, RES_TYPE.PROFILE);
		assertEquals(expected, actual.getPath());

	}
/*
	@Test
	public void testToAbsolute() {
		System.out.println("relative: "+ResourceRegistry.MODELS_FOLDER+" absolute: "+ResourceRegistry.toAbsolute(ResourceRegistry.MODELS_FOLDER));
	}
*/
	
	
	
	@Test
	public void testGetComponentJarList() {
		ResourceRegistry.getInstance().setAREBaseURI(new File("../bin/ARE").toURI().normalize());
		for(URI componentJarURI : ResourceRegistry.getInstance().getComponentJarList(false)) {
			System.out.println("JarURI: "+componentJarURI);
		}
	}
	
	@Test
	public void testGetLicensesList() {
		ResourceRegistry.getInstance().setAREBaseURI(new File("../bin/ARE").toURI().normalize());
		for(URI componentJarURI : ResourceRegistry.getInstance().getLicensesList(false)) {
			System.out.println("License: "+componentJarURI);
		}
	}
	
	@Test
	public void testGetLicensesListForRemoteWindow() {
		ResourceRegistry.getInstance().setAREBaseURI(new File("../bin/ARE").toURI().normalize());
		final String compType="actuator.RemoteWindow";
		System.out.println("Licenses of compType: "+compType);
		for(URI componentJarURI : ResourceRegistry.getInstance().getLicensesList(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				String[] compTypePrefix=name.split("-");
				return compTypePrefix[0].equalsIgnoreCase(compType) && name.endsWith(".txt");
			}

		},false)) {			
			System.out.println(componentJarURI);
		}
	}	
	
	@Test
	public void testGetLicenseURIsofAsTeRICSJarURIs() throws URISyntaxException {
		System.out.println("testGetLicenseURIsofAsTeRICSJarURIs");
		ResourceRegistry.getInstance().setAREBaseURI(new File("../bin/ARE").toURI().normalize());
		
		Set<URI> alljarURIs=new HashSet<URI>();
		alljarURIs.add(ResourceRegistry.getInstance().getResource("javacv-0.10.0-merged-jars.jar", RES_TYPE.JAR));
		alljarURIs.add(ResourceRegistry.getInstance().getResource("grizzly-httpservice-bundle-2.3.23.jar", RES_TYPE.JAR));
		alljarURIs.add(ResourceRegistry.getInstance().getResource("org.eclipse.osgi.services_3.2.100.v20100503.jar", RES_TYPE.JAR));
		alljarURIs.add(ResourceRegistry.getInstance().getResource("org.eclipse.osgi_3.6.0.v20100517.jar", RES_TYPE.JAR));
		
		for(URI componentJarURI : ResourceRegistry.getInstance().getLicenseURIsofAsTeRICSJarURIs(alljarURIs)) {
			System.out.println("javacv licenses: "+componentJarURI);
		}		
	}
	
	@Test
	public void testGetLastElementOfURI() throws URISyntaxException {
		ResourceRegistry.getInstance().setAREBaseURI(new File("../bin/ARE").toURI().normalize());
		System.out.println("last part of AREBaseURI: "+ResourceRegistry.getLastElementOfURI(ResourceRegistry.getInstance().getAREBaseURI()));
		
		URI jarURI=ResourceRegistry.getInstance().getResource("javacv-0.10.0-merged-jars.jar", RES_TYPE.JAR);		
		System.out.println("last part of javacv jarURI: "+ResourceRegistry.getLastElementOfURI(jarURI));
		System.out.println("last part of relative javacv jarURI: "+ResourceRegistry.getLastElementOfURI(ResourceRegistry.getInstance().toRelative(jarURI)));
	}

	@Test
	public void testGetDataList() {
		ResourceRegistry.getInstance().setAREBaseURI(new File("../bin/ARE").toURI().normalize());
		for(URI componentJarURI : ResourceRegistry.getInstance().getDataList(false)) {
			System.out.println("Data: "+componentJarURI);
		}
	}
	
	@Test
	public void testGetModelList() {
		ResourceRegistry.getInstance().setAREBaseURI(new File("../bin/ARE").toURI().normalize());
		for(URI componentJarURI : ResourceRegistry.getInstance().getModelList(true)) {
			System.out.println("Model: "+componentJarURI);
		}
	}

	@Test
	public void testGetAllJarList() {
		ResourceRegistry.getInstance().setAREBaseURI(new File("../bin/ARE").toURI().normalize());
		for(URI componentJarURI : ResourceRegistry.getInstance().getAllJarList(true)) {
			System.out.println("Jar: "+componentJarURI);
		}
	}
	@Test
	public void testGetServicesJarList() {
		ResourceRegistry.getInstance().setAREBaseURI(new File("../bin/ARE").toURI().normalize());
		for(URI componentJarURI : ResourceRegistry.getInstance().getServicesJarList(true)) {
			System.out.println("Service Jar: "+componentJarURI);
		}
	}
	
	@Test
	public void testGetOtherJarList() {
		ResourceRegistry.getInstance().setAREBaseURI(new File("../bin/ARE").toURI().normalize());
		for(URI componentJarURI : ResourceRegistry.getInstance().getOtherJarList(true)) {
			System.out.println("Other Jar: "+componentJarURI);
		}
	}
	
	@Test
	public void testAREJarURI() {
		ResourceRegistry.getInstance().setAREBaseURI(new File("../bin/ARE").toURI().normalize());

		System.out.println("ARE Jar: "+ResourceRegistry.getInstance().getAREJarURI(true));
	}


	@Test
	public void testGetAREBaseURIFile() throws URISyntaxException {
		System.out.println("getAREBaseURIFile(): "+ResourceRegistry.getInstance().getAREBaseURIFile());
	}
	
	@Test
	public void testGetToPath() throws URISyntaxException {
		System.out.println("getAREBaseURIPath(): "+ResourceRegistry.toPath(ResourceRegistry.getInstance().getAREBaseURI()));
	}
	
	@Test
	public void testToStringArray() throws URISyntaxException {
		String[] paths={"models/ImageDemo.acs","data/webservice/index.html"};
		String[] absPaths=new String[paths.length];
		Collection<URI> uris=new ArrayList<URI>();
				
		int i=0;
		for(String path : paths) {
			URI uri=new File(path).toURI();
			absPaths[i]=ResourceRegistry.toString(uri);
			uris.add(uri);
			i++;
		}
		String[] result=ResourceRegistry.toStringArray(uris);
		assertArrayEquals(absPaths, result);
	}
	
	@Test
	public void testToStringSet() throws URISyntaxException {
		String[] paths={"models/ImageDemo.acs","data/webservice/index.html"};
		Set<String> absPaths=new TreeSet<String>();
		Collection<URI> uris=new ArrayList<URI>();
				
		for(String path : paths) {
			URI uri=new File(path).toURI();
			absPaths.add(ResourceRegistry.toString(uri));
			uris.add(uri);
		}
		Set<String> result=ResourceRegistry.toStringSet(uris);
		assertEquals(absPaths, result);
	}

	@Test
	public void testToStringList() throws URISyntaxException {
		String[] paths={"models/ImageDemo.acs","data/webservice/index.html"};
		List<String> absPaths=new ArrayList<String>();
		Collection<URI> uris=new ArrayList<URI>();
				
		for(String path : paths) {
			URI uri=new File(path).toURI();
			absPaths.add(ResourceRegistry.toString(uri));
			uris.add(uri);
		}
		List<String> result=ResourceRegistry.toStringList(uris);
		assertEquals(absPaths, result);
	}
	
	@Test
	public void testToStringCollections() throws URISyntaxException {
		Collection<URI> uris=ResourceRegistry.getInstance().getModelList(true);
		
		String[] pathsArray=ResourceRegistry.toStringArray(uris);
		System.out.println("toStringArray: "+Arrays.toString(pathsArray));

		List<String> pathsList=ResourceRegistry.toStringList(uris);
		System.out.println("toStringList: "+pathsList);
		
		Set<String> pathsSet=ResourceRegistry.toStringSet(uris);
		System.out.println("toStringSet: "+pathsSet);		
	}	
	
	@Test
	public void testGetMandatoryProfileConfigFileList() {
		List<URI> iniFiles=ResourceRegistry.getInstance().getMandatoryProfileConfigFileList(false);
		System.out.println("Found this iniFiles: "+iniFiles);
		assertEquals(6, iniFiles.size());
	}
	
	@Test
	public void testGetOtherFilesList() {
		List<URI> otherFiles=ResourceRegistry.getInstance().getOtherFilesList(false);
		System.out.println("Found this other files: "+otherFiles);
		assertEquals(13, otherFiles.size());
		
	}
	
	/*
	@Test
	public void testRecursiveFindFilesExtension() {
		ResourceRegistry.getInstance().setAREBaseURI(new File("../bin/ARE").toURI());
		File start=ResourceRegistry.getInstance().getAREBaseURIFile();
		System.out.println("start recursive search: "+start);
		
		List<File> foundFiles=ComponentUtils.findFiles(start, ".acs", 3);
		System.out.println("Found nr. files recursively: "+foundFiles.size());
		for(File found : foundFiles) {
			System.out.println(found);
		}
	}*/
	/*
	@Test
	public void testRecursiveFindFilesFilenameFilter() {
	    System.out.println("Paths.test: "+Paths.get(new File("data").toURI()));
		
		ResourceRegistry.getInstance().setAREBaseURI(new File("../bin/ARE").getAbsoluteFile().toURI());
		//File start=ResourceRegistry.getInstance().getAREBaseURIFile();
		System.out.println("start recursive search: "+ResourceRegistry.getInstance().getAREBaseURI());
		List<URI> foundFiles=ComponentUtils.findFiles(ResourceRegistry.getInstance().getAREBaseURI(), true, 3, new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				if(name.endsWith(".acs")) {
					return true;
				}
				return false;
			}
		});
		System.out.println("Found nr. files recursively: "+foundFiles.size());
		for(URI found : foundFiles) {
			System.out.println("rel found: "+found);
			System.out.println("toAbsolute: "+ResourceRegistry.getInstance().toAbsolute(found));
		}
	}*/
	
	
	
}
