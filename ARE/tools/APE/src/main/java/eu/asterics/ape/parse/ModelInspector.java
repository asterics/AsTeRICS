package eu.asterics.ape.parse;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

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

import eu.asterics.mw.are.BundleManager;
import eu.asterics.mw.are.exceptions.ParseException;
import eu.asterics.mw.are.parsers.DefaultBundleModelParser;
import eu.asterics.mw.are.parsers.DefaultDeploymentModelParser;
import eu.asterics.mw.are.parsers.ModelValidator;
import eu.asterics.mw.model.bundle.IComponentType;
import eu.asterics.mw.model.deployment.IRuntimeModel;
import eu.asterics.mw.model.deployment.impl.DefaultRuntimeModel;

public class ModelInspector {
	ModelValidator modelValidator=null;
	DefaultDeploymentModelParser deploymentModelParser=null;
	
	public ModelInspector() throws MalformedURLException {
		Path bundleDescriptorSchemaURL = Paths.get("middleware/src/main/resources/schemas/bundle_model.xsd");
		Path deploymentDescriptorSchemaURL = Paths.get("middleware/src/main/resources/schemas/deployment_model.xsd"); 
		modelValidator=new ModelValidator(bundleDescriptorSchemaURL.toUri().toURL(),deploymentDescriptorSchemaURL.toUri().toURL());
		deploymentModelParser=DefaultDeploymentModelParser.create(modelValidator);
	}
	
	public IRuntimeModel parseModel(InputStream modelStream) throws ParseException, ParserConfigurationException, SAXException, IOException, TransformerException {
		System.out.println("test parseModel");
		//Path testModel = Paths.get("tools/APE/src/test/resources/models/test_deployment_model.acs");
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
}
