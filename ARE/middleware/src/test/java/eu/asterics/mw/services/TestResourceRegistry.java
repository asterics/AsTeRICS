package eu.asterics.mw.services;

import static org.junit.Assert.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.asterics.mw.are.BundleManager;
import eu.asterics.mw.are.DeploymentManager;
import eu.asterics.mw.are.parsers.DefaultDeploymentModelParser;
import eu.asterics.mw.are.parsers.ModelValidator;
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
		//fail("Not yet implemented");
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
	public void testSetAREBaseURI() throws URISyntaxException {
		//Test getting and relative to absolute
		testGetAREBaseURI();
		testRelativeToAbsoluteToRelative();
		
		//change base URI
		URI newURI;
		newURI=new File("C:\\Program Files (x86)\\AsTeRICS\\ARE").toURI();
		System.out.println("Setting new AREBaseURI to <"+newURI.getPath()+">");
		ResourceRegistry.getInstance().setAREBaseURI(newURI);
		
		//Test getting and relative to absolute again
		testGetAREBaseURI();
		testRelativeToAbsoluteToRelative();
	}

	/*
	@Test
	public void testToRelative() {
		URI absolute=ResourceRegistry.getAREBaseURI().resolve(ResourceRegistry.MODELS_FOLDER);
		System.out.println("absolute: "+absolute+" relative: "+ResourceRegistry.toRelative(absolute.toString()));
	}

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
