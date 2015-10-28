package eu.asterics.mw.services;

import static org.junit.Assert.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestResourceRegistry {

	@Before
	public void setUp() throws Exception {
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
		ResourceRegistry.getInstance().setAREBaseURI(new File("../bin/ARE").toURI());
		for(URI componentJarURI : ResourceRegistry.getInstance().getComponentJarList()) {
			System.out.println("JarURI: "+componentJarURI);
		}
	}
	
	@Test
	public void testGetAREBaseURIFile() {
		System.out.println("getAREBaseURIFile(): "+ResourceRegistry.getInstance().getAREBaseURIFile());
	}
}
