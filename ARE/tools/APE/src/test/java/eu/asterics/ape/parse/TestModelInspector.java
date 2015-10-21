package eu.asterics.ape.parse;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import eu.asterics.mw.are.exceptions.ParseException;
import eu.asterics.mw.model.deployment.IComponentInstance;
import eu.asterics.mw.model.deployment.IRuntimeModel;

public class TestModelInspector {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testParseModel() {
		
		try {
			Path testModel = Paths.get("tools/APE/src/test/resources/models/test_deployment_model.acs");
			ModelInspector modelInspector=new ModelInspector();
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
		} catch (ParseException | IOException | ParserConfigurationException | SAXException | TransformerException e) {
			// TODO Auto-generated catch block
			fail(e.getMessage());
		}

	}

}
