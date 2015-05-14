package eu.asterics.mw.webservice.serverUtils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * This is a class capable of serializing/deserializing Java Objects to/from JSON or XML format.
 * 
 * @author Marios Komodromos (mkomod05@cs.ucy.ac.cy)
 *
 */
public class ObjectTransformation {

	/**
	 * This method converts any object to a XML formatted string, given that the object is defined
	 * in a way that lets the automatic object transformation to XML. In the case, you want to
	 * implement this functionality with a third-party library, extend this class and overwrite
	 * this method.
	 * 
	 * @param object - The object to transform to XML formatted string
	 * 
	 * @return An XML formatted string
	 */
	public static String objectToXML(Object object, Class objectClass) {
		String XMLString = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(objectClass);
			
	        Marshaller marshaller = jc.createMarshaller();
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	        
	        StringWriter writer = new StringWriter();
	        marshaller.marshal(object, writer);
	        
	        XMLString = writer.toString();
		} 
		catch (JAXBException e) {
			e.printStackTrace();
		}
		
		return XMLString;
	}
	
	
	/**
	 * This method converts any XML formatted string to an object, given that the object is defined
	 * in a way that lets the automatic XML transformation to an object. In the case, you want to
	 * implement this functionality with a third-party library, extend this class and overwrite
	 * this method.
	 * 
	 * @param XMLString - The XML formatted string that will be transformed to an object
	 * @param objectClass - The object class.
	 * 
	 * @return An object that corresponds to the XML formatted string
	 */
	public static Object XMLToObject(String XMLString, Class objectClass) {
		Object object = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(objectClass);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			
			StringReader reader = new StringReader(XMLString);
			object = unmarshaller.unmarshal(reader);
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return object;
	}
	

	/**
	 * This method converts any object to a JSON string, given that the object is defined
	 * in a way that lets the automatic object transformation to JSON.
	 * This method uses other methods from Jackson library (http://jackson.codehaus.org/), hence
	 * the correct structure of the object is defined by Jackson lib.
	 * 
	 * @param object - The object to transform to a JSON string
	 * 
	 * @return A JSON object as string
	 */
	public static String objectToJSON(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		String JSONobj = null;
		try {
			JSONobj = mapper.writeValueAsString(object);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
			return "An unexpected error occured";
		} catch (JsonMappingException e) {
			e.printStackTrace();
			return "An unexpected error occured";
		} catch (IOException e) {
			e.printStackTrace();
			return "An unexpected error occured";
		}
		return JSONobj;
	}
	
	
	/**
	 * This method converts any JSON string to an object, given that the object is defined
	 * in a way that lets the automatic JSON transformation to an object.
	 * This method uses other methods from Jackson library (http://jackson.codehaus.org/), hence
	 * the correct structure of the object is defined by Jackson lib.
	 * 
	 * @param JSON - The JSON string that will be transformed to an object
	 * @param objectClass - The class of the object.
	 * 
	 * @return An object that corresponds to the JSON String
	 */
	public static Object JSONToObject(String JSON, Class objectClass) {
		ObjectMapper mapper = new ObjectMapper();
		
		Object object;
		try {
			object = mapper.readValue(JSON, objectClass);
			return object;
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
