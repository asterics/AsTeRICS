package eu.asterics.ape.parse;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.osgi.framework.internal.core.BundleContextImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.xml.sax.SAXException;

import eu.asterics.mw.are.BundleManager;
import eu.asterics.mw.are.exceptions.BundleManagementException;
import eu.asterics.mw.are.exceptions.ParseException;
import eu.asterics.mw.model.deployment.IComponentInstance;
import eu.asterics.mw.model.deployment.IRuntimeModel;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.ResourceRegistry;

public class TestModelInspector {
	ModelInspector modelInspector;

	@Before
	public void setUp() throws Exception {
		ResourceRegistry.getInstance().setOSGIMode(false);
		ResourceRegistry.getInstance().setAREBaseURI(new File("../bin/ARE").toURI());	
		modelInspector=new ModelInspector(null);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testParseModel() throws BundleManagementException {
		
		try {
			Path testModel = Paths.get("tools/APE/src/test/resources/models/test_deployment_model.acs");
			InputStream iStr=testModel.toUri().toURL().openStream();
			IRuntimeModel runtimeModel=modelInspector.parseModel(iStr);
			System.out.println("instanceIds: "+Arrays.toString(runtimeModel.getComponentInstancesIDs()));
			
			System.out.println("Componenttypes used: ");
			for(IComponentInstance compInstance : runtimeModel.getComponentInstances()) {
				System.out.println(compInstance.getComponentTypeID()+" [");

				String [] propKeys=runtimeModel.getComponentPropertyKeys(compInstance.getInstanceID());
				for(String propKey : propKeys) {
					System.out.println(propKey+"="+runtimeModel.getComponentProperty(compInstance.getInstanceID(), propKey));
				}
				System.out.println("]");				
			}
			
			Set<URI> modelComponentJars=modelInspector.getComponentTypeJarURIsOfModel(runtimeModel);
			for(URI componentJarURI : modelComponentJars) {
				System.out.println(componentJarURI);
			}
		} catch (ParseException | IOException | ParserConfigurationException | SAXException | TransformerException e) {
			// TODO Auto-generated catch block
			fail(e.getMessage());
		}

	}
	
	@Test
	public void testGetPropertyReferredURIs() {
		try {
			Path testModel = Paths.get("tools/APE/src/test/resources/models/ImageDemo.acs");
			doTestGetPropertyReferredURIs(testModel, 17);
			
			testModel = Paths.get("tools/APE/src/test/resources/models/HeadSound.acs");
			doTestGetPropertyReferredURIs(testModel, 2);

			testModel = Paths.get("tools/APE/src/test/resources/models/ergomenu.acs");
			doTestGetPropertyReferredURIs(testModel, 6);

			testModel = Paths.get("tools/APE/src/test/resources/models/Ergo-Kopf-Schreiben.acs");
			//doTestGetPropertyReferredURIs(testModel, 5);

			testModel = Paths.get("tools/APE/src/test/resources/models/Ergo-Kopf-Musik.acs");
			doTestGetPropertyReferredURIs(testModel, 11);

			testModel = Paths.get("tools/APE/src/test/resources/models/keyboard.acs");
			doTestGetPropertyReferredURIs(testModel, 1);
			
			//testModel = Paths.get("tools/APE/src/test/resources/models/artlab-showroom-cellboard/models/artlab_showroom.acs");
			//doTestGetPropertyReferredURIs(testModel, 0);			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			fail(e.getMessage());
		}
	}
	
	private void doTestGetPropertyReferredURIs(Path testModel,int nrCorrectReferredURIs) throws MalformedURLException, IOException, ParseException, ParserConfigurationException, SAXException, TransformerException, BundleManagementException {		
			InputStream iStr=testModel.toUri().toURL().openStream();
			IRuntimeModel runtimeModel=modelInspector.parseModel(iStr);
			
			Set<IRuntimeModel> modelInstances=new HashSet<IRuntimeModel>();
			modelInstances.add(runtimeModel);
			
			Collection<URI> dataURIs=modelInspector.getPropertyReferredURIs(modelInstances);
			System.out.println("In model "+testModel+", found <"+dataURIs.size()+"> property value URIs: "+dataURIs.toString());
			assertEquals(nrCorrectReferredURIs, dataURIs.size());
						
	}
	
	@Test
	public void testGenerateComponentListCache() {
		try {
			File componentListCache=new File("loader_componentlist.ini");
			modelInspector.generateComponentListCache(componentListCache);
			System.out.println("Componentlist cache created: "+componentListCache);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			fail(e.getMessage());
		}
	}		
}
