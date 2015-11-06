package eu.asterics.ape.parse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import eu.asterics.ape.main.APE;
import eu.asterics.ape.main.APEProperties;
import eu.asterics.component.processor.stringsplitter.StringSplitterInstance;
import eu.asterics.mw.are.BundleManager;
import eu.asterics.mw.are.DeploymentManager;
import eu.asterics.mw.are.exceptions.BundleManagementException;
import eu.asterics.mw.are.exceptions.ParseException;
import eu.asterics.mw.are.parsers.DefaultBundleModelParser;
import eu.asterics.mw.are.parsers.DefaultDeploymentModelParser;
import eu.asterics.mw.are.parsers.ModelValidator;
import eu.asterics.mw.model.bundle.IComponentType;
import eu.asterics.mw.model.deployment.IComponentInstance;
import eu.asterics.mw.model.deployment.IRuntimeModel;
import eu.asterics.mw.model.deployment.impl.DefaultRuntimeModel;
import eu.asterics.mw.services.AstericsErrorHandling;
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

public class ModelInspector {
	private static final String MODELS_PROP_SEPERATOR = ";";
	ModelValidator modelValidator=null;
	DefaultDeploymentModelParser deploymentModelParser=null;
	BundleManager bundleManager=null;
	APEProperties apeProperties=null;
	
	public ModelInspector(APEProperties apeProperties) throws IOException, ParseException, URISyntaxException {
		this.apeProperties=apeProperties;
		
		modelValidator=new ModelValidator();
		deploymentModelParser=DefaultDeploymentModelParser.create(modelValidator);
		bundleManager=new BundleManager(modelValidator);
		bundleManager.createComponentListCache();
		DeploymentManager.instance.setBundleManager(bundleManager);
	}
	
	public IRuntimeModel parseModel(InputStream modelStream) throws ParseException, ParserConfigurationException, SAXException, IOException, TransformerException, BundleManagementException {
		String utf16String=convertToUTF16String(modelStream);
		IRuntimeModel runtimeModel = deploymentModelParser.parseModel(openStream(utf16String));
		return runtimeModel;
	}
	
	public String convertToUTF16String(InputStream modelStream) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(modelStream);
		DOMSource domSource = new DOMSource(doc);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-16");
		transformer.transform(domSource, result);
		String modelInString = writer.toString();
		return modelInString;
	}
	
	public InputStream openStream(String modelStringinUTF16) throws UnsupportedEncodingException {
		return new ByteArrayInputStream(modelStringinUTF16.getBytes("UTF-16"));
	}
	
	public Set<URI> getComponentTypeJarURIsOfModel(IRuntimeModel model) {
		Set<URI> modelComponentJarURIs=new TreeSet<URI>();
		for(IComponentInstance compInstance : model.getComponentInstances()) {
			URI absoluteURI=ResourceRegistry.getInstance().toAbsolute(bundleManager.getJarNameFromComponentTypeId(compInstance.getComponentTypeID()));
			modelComponentJarURIs.add(absoluteURI);
		}
		//System.out.println("Model: "+model.getModelName()+", comoponentTypeJarURIs:\n"+modelComponentJarURIs);
		return modelComponentJarURIs;
	}
	
	public Set<URI> getComponentTypeJarURIsOfModels(Set<URI> modelURIs) throws MalformedURLException, IOException, ParseException, ParserConfigurationException, SAXException, TransformerException, BundleManagementException {
		Set<URI> modelComponentJarURIs=new TreeSet<URI>();
		for(URI modelURI : modelURIs) {
			InputStream iStr=modelURI.toURL().openStream();
			IRuntimeModel model=parseModel(iStr);
			modelComponentJarURIs.addAll(getComponentTypeJarURIsOfModel(model));
		}
		return modelComponentJarURIs;
	}
	
	public Set<URI> getModelURIsFromProperty() {
		Set<URI> modelURIs=new TreeSet<URI>();
		String modelsPropVals=apeProperties.getProperty(APEProperties.APE_MODELS);
		for(String modelsPropVal : modelsPropVals.split(MODELS_PROP_SEPERATOR)) {
			File testFile=new File(modelsPropVal);
			URI testURI=testFile.toURI();
			if(!testFile.isAbsolute()) {
				try {
					testURI=ResourceRegistry.getInstance().getResource(modelsPropVal, RES_TYPE.MODEL);
				} catch (URISyntaxException e) {
					AstericsErrorHandling.instance.getLogger().warning("Could not create model URI for: "+modelsPropVal);
					continue;
				}
			}
			try {
				testFile=ResourceRegistry.toFile(testURI);
			} catch (URISyntaxException e) {
				AstericsErrorHandling.instance.getLogger().warning("Could not create model URI for: "+testURI);
				continue;
			}
			if(!testFile.exists()) {
				continue;
			}
			
			List<URI> URIs=new ArrayList();
			if(testFile.isDirectory()) {
				URIs=ResourceRegistry.getModelList(testURI, false);
			} else {
				URIs.add(testURI);
			}
			modelURIs.addAll(URIs);
		}
		
		return modelURIs;
	}
	
	public void generateComponentListCache(File componentList) throws MalformedURLException, IOException, ParseException {
		bundleManager.generateComponentListCache(componentList);
	}
}
