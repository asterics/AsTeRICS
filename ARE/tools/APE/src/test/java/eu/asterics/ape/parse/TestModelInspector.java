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

package eu.asterics.ape.parse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import eu.asterics.mw.are.exceptions.BundleManagementException;
import eu.asterics.mw.are.exceptions.ParseException;
import eu.asterics.mw.model.deployment.IComponentInstance;
import eu.asterics.mw.model.deployment.IRuntimeModel;
import eu.asterics.mw.services.ResourceRegistry;

public class TestModelInspector {
    ModelInspector modelInspector;

    @Before
    public void setUp() throws Exception {
        ResourceRegistry.getInstance().setOSGIMode(false);
        ResourceRegistry.getInstance().setAREBaseURI(new File("../bin/ARE").toURI());
        modelInspector = new ModelInspector(null);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testParseModel() throws BundleManagementException {

        try {
            Path testModel = Paths.get("tools/APE/src/test/resources/models/test_deployment_model.acs");
            InputStream iStr = testModel.toUri().toURL().openStream();
            IRuntimeModel runtimeModel = modelInspector.parseModel(iStr);
            System.out.println("instanceIds: " + Arrays.toString(runtimeModel.getComponentInstancesIDs()));

            System.out.println("Componenttypes used: ");
            for (IComponentInstance compInstance : runtimeModel.getComponentInstances()) {
                System.out.println(compInstance.getComponentTypeID() + " [");

                String[] propKeys = runtimeModel.getComponentPropertyKeys(compInstance.getInstanceID());
                for (String propKey : propKeys) {
                    System.out.println(
                            propKey + "=" + runtimeModel.getComponentProperty(compInstance.getInstanceID(), propKey));
                }
                System.out.println("]");
            }

            Set<URI> modelComponentJars = modelInspector.getComponentTypeJarURIsOfModel(runtimeModel);
            for (URI componentJarURI : modelComponentJars) {
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
            // doTestGetPropertyReferredURIs(testModel, 5);

            testModel = Paths.get("tools/APE/src/test/resources/models/Ergo-Kopf-Musik.acs");
            doTestGetPropertyReferredURIs(testModel, 11);

            testModel = Paths.get("tools/APE/src/test/resources/models/keyboard.acs");
            doTestGetPropertyReferredURIs(testModel, 1);

            // testModel =
            // Paths.get("tools/APE/src/test/resources/models/artlab-showroom-cellboard/models/artlab_showroom.acs");
            // doTestGetPropertyReferredURIs(testModel, 0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            fail(e.getMessage());
        }
    }

    private void doTestGetPropertyReferredURIs(Path testModel, int nrCorrectReferredURIs)
            throws MalformedURLException, IOException, ParseException, ParserConfigurationException, SAXException,
            TransformerException, BundleManagementException {
        InputStream iStr = testModel.toUri().toURL().openStream();
        IRuntimeModel runtimeModel = modelInspector.parseModel(iStr);

        Set<IRuntimeModel> modelInstances = new HashSet<IRuntimeModel>();
        modelInstances.add(runtimeModel);

        Collection<URI> dataURIs = modelInspector.getPropertyReferredURIs(modelInstances);
        System.out.println("In model " + testModel + ", found <" + dataURIs.size() + "> property value URIs: "
                + dataURIs.toString());
        assertEquals(nrCorrectReferredURIs, dataURIs.size());

    }
}
