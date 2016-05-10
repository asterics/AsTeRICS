package eu.asterics.mw.are.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import eu.asterics.mw.are.exceptions.ParseException;
import eu.asterics.mw.model.bundle.*;
import eu.asterics.mw.model.bundle.impl.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.asterics.mw.model.DataType;
import eu.asterics.mw.model.Multiplicity;
import eu.asterics.mw.services.AstericsErrorHandling;


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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */



/**
 * @author 
 * This class parses models provided as input streams or files,
 * according to the bundle descriptor format (XSD) and produces 
 * IComponentTypes as a result.
 * Date: 
 */

public class DefaultBundleModelParser {
	public static final String BUNDLE_DESCRIPTOR_RELATIVE_URI="/bundle_descriptor.xml";
	
	private Logger logger = null;
	
	private DocumentBuilder builder;
	
	//private ModelValidator modelValidator;
	private DefaultBundleModelParser() {
		logger = AstericsErrorHandling.instance.getLogger();
	}

	public static final DefaultBundleModelParser instance =
		new DefaultBundleModelParser();

	/**
	 * Parses the specified input stream according to the bundle descriptor
	 * format (XSD) and produces a set of {@link IComponentType}s as a result.
	 *
	 * @param inputStream encodes the XML-based bundle descriptor
	 * @return a set of {@link IComponentType}s
	 * @throws ParseException
	 */
	public Set<IComponentType> parseModel(InputStream inputStream)
	throws ParseException {
		final DocumentBuilderFactory builderFactory =
			DocumentBuilderFactory.newInstance();
	

		try 
		{
			builder = builderFactory.newDocumentBuilder();
			synchronized(builder){
			Document document = builder.parse(inputStream);
			return parse(document);
			}
		} catch (ParserConfigurationException e) {
			logger.warning(this.getClass().getName()+"." +
					"parseModel: Parse error -> \n"+e.getMessage());
			throw new ParseException(e.getMessage());
		} catch (SAXException e) {
			logger.warning(this.getClass().getName()+"." +
					"parseModel: Parse error -> \n"+e.getMessage());
			throw new ParseException(e.getMessage());
		} catch (IOException e) {
			logger.warning(this.getClass().getName()+"." +
					"parseModel: Parse error -> \n"+e.getMessage());
			throw new ParseException(e.getMessage());
		}
	}

	
	
	/**
	 * Parses the specified input file according to the bundle descriptor
	 * format (XSD) and produces a set of {@link IComponentType}s as a result.
	 *
	 * @param modelFile the file to be parsed
	 * @return a set of {@link IComponentType}s
	 * @throws FileNotFoundException, ParseException
	 */
	public Set<IComponentType> parseModel(File modelFile) 
	throws FileNotFoundException, ParseException
	{
		return this.parseModel(new FileInputStream(modelFile));
	}

	
	
	/**
	 * Parses the specified input URL according to the bundle descriptor
	 * format (XSD) and produces a set of {@link IComponentType}s as a result.
	 *
	 * @param url the URL to be parsed
	 * @return a set of {@link IComponentType}s
	 * @throws ParseException, UnsupportedEncodingException
	 */
	public Set<IComponentType> parseModel(String url)
	throws ParseException, UnsupportedEncodingException 
	{
		DocumentBuilderFactory builderFactory =
		DocumentBuilderFactory.newInstance();
		try {
			builder = builderFactory.newDocumentBuilder();
			synchronized(builder){
			Document document = builder.parse(url);
			return parse(document);
			}
		} catch (ParserConfigurationException e) {
			logger.warning(this.getClass().getName()+"." +
					"parseModel: Parse error -> \n"+e.getMessage());
			throw new ParseException(e.getMessage());
		} catch (SAXException e) {
			logger.warning(this.getClass().getName()+"." +
					"parseModel: Parse error -> \n"+e.getMessage());
			throw new ParseException(e.getMessage());
		} catch (IOException e) {
			logger.warning(this.getClass().getName()+"." +
					"parseModel: Parse error -> \n"+e.getMessage());
			throw new ParseException(e.getMessage());
		}
	}


	//--------------------Parser-----------------------//

	/**
	 *  This class is responsible for parsing the Bundle descriptor
	 * document and instantiating the type elements found (componentTypes,
	 * portTypes,etc).
	 * 
	 * @return A set of IComponentType elements which contain all the needed
	 * information
	 * @throws ParseException
	 */
	private Set<IComponentType> parse(Document document) throws ParseException 
	{

		//get the root element
		Element root = document.getDocumentElement();
		NodeList components = root.getChildNodes();
		if (components == null) 
		{
			logger.warning(this.getClass().getName()+"." +
					"parse: Empty bundle model \n");
			throw new ParseException(" empty bundle model ");
		}

		final Set<IComponentType> componentTypes = 
			new LinkedHashSet<IComponentType>();

		//iterate through the components
		for (int i = 0; i < components.getLength(); i++) 
		{
			Node node = components.item(i);
			if (node instanceof Element) 
			{
				Element component = (Element) node;
				componentTypes.add(getIComponentType(component));
			}
		}
		return componentTypes;
	}

	/**
	 * Returns the bundle descriptor xml string for the given componentTypeId.
	 * 
	 * @param componentTypeId
	 * @param inputStream
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public String getBundleDescriptionOfComponentTypeId(String componentTypeId, InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
		final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

		builder = builderFactory.newDocumentBuilder();
		Document document = builder.parse(inputStream);

		Element root = document.getDocumentElement();
		NodeList components = root.getChildNodes();

		//iterate through the components
		for (int i = 0; i < components.getLength(); i++) 
		{
			Node node = components.item(i);
			if (node instanceof Element) 
			{
				Element component = (Element) node;
				String ID = component.getAttribute("id");
				if(componentTypeId.equals(ID)) {
					try {
						StringWriter stw = new StringWriter(); 
						Transformer serializer;

						serializer = TransformerFactory.newInstance().newTransformer();
						serializer.transform(new DOMSource(component), new StreamResult(stw));
						System.out.println(stw.toString());
						return stw.toString();
					} catch (TransformerFactoryConfigurationError | TransformerException e) {
						// TODO Auto-generated catch block
						logger.warning(e.getMessage());
					} 
				}
			}
		}
		return "";
	}

	/**
	 * 
	 * Create an IComponentType from the given XML element
	 * @param component the XML element
	 * @return an IComponentTYpe object
	 * @throws ParseException
	 */
	private IComponentType getIComponentType (Element component) 
	throws ParseException
	{
		if (component.getTagName().compareTo("componentType") == 0) 
		{
			//Get componentType id
			String ID = component.getAttribute("id");
			//Get componentType canonical name
			String canonicalName = component.getAttribute("canonical_name");
			if (ID == null || canonicalName == null) 
			{
				logger.warning(this.getClass().getName()+"." +
						"getIComponentType:  Missing required component attributes \n");
				throw new ParseException 
						(" missing required component attributes ");
			}
			NodeList componentTypeChildNodes =	component.getChildNodes();
			ComponentType componentType = null;
			boolean singleton = false;
			boolean isExternalGui = false;
			String description = null;
			final Set<IInputPortType> inputPortTypes =
				new LinkedHashSet<IInputPortType>();
			final Set<IOutputPortType> outputPortTypes =
				new LinkedHashSet<IOutputPortType>();
			
			Map<String, PropertyType> cPropertyTypes = null;
			Set<IEventListenerPortType> eventListenerPortTypes = 
				new LinkedHashSet<IEventListenerPortType>();
			Set<IEventTriggererPortType> eventTriggererPortTypes = 
				new LinkedHashSet<IEventTriggererPortType>();

			//Iterate through the children of the componentType
			for (int j = 0; j < componentTypeChildNodes.getLength(); j++) 
			{
				Node componentTypeChild = componentTypeChildNodes.item(j);
				if (componentTypeChild instanceof Element) 
				{
					Element componentTypeChildElement =
						(Element) componentTypeChild;
					//Type
					if (componentTypeChildElement.getTagName().equals("type")) 
					{
						String componentTypeString =
							componentTypeChildElement.getTextContent();
						componentType =
							getComponentType(componentTypeString);

					}
					//Singleton
					else if (componentTypeChildElement.getTagName().
							equals("singleton")) 
					{
						String singletonString =
							componentTypeChildElement.getTextContent();
						singleton =
							getBoolean(singletonString);
					} 
					else if (componentTypeChildElement.getTagName().
							equals("description")) 
					{
						description = componentTypeChildElement.
						getTextContent();

					} 
					else if (componentTypeChildElement.getTagName().
							equals("ports")) 
					{

						NodeList portTypes =
							componentTypeChildElement.getChildNodes();
						for (int k = 0; k < portTypes.getLength(); k++)
						{
							Node portTypesChild = portTypes.item(k);

							if (portTypesChild instanceof Element) 
							{
								Element portTypeElement = (Element)
								portTypesChild;

								if (portTypeElement.getTagName().
										equals("inputPort")) 
								{
									inputPortTypes.
										add(getInputPortType(portTypeElement));
								} 
								else if (portTypeElement.getTagName().
										equals("outputPort")) 
								{
									outputPortTypes.
										add(getOutputPortType(portTypeElement));
								}
							}
						}
					} 
					else if (componentTypeChildElement.getTagName().
							equals("events")) 
					{
						setEvents(componentTypeChildElement,
								eventListenerPortTypes, 
								eventTriggererPortTypes);
					} 
					else if (componentTypeChildElement.getTagName().
							equals("properties")) 
					{
						if (componentTypeChildElement.getChildNodes().
								getLength() > 0) 
						{
							cPropertyTypes =
								getPropertyTypes
								(componentTypeChildElement.
										getChildNodes());
						} 
						else 
						{
							cPropertyTypes = new LinkedHashMap<String,PropertyType>();
						}
					}
					else if (componentTypeChildElement.getTagName().equals("gui"))
					{
						if (componentTypeChildElement.hasAttribute("IsExternalGUIElement"))
						{
							String externalGuiString = componentTypeChildElement
								.getAttribute("IsExternalGUIElement");
							isExternalGui =	getBoolean(externalGuiString);
						}
					}
					else 
					{
						logger.warning(this.getClass().getName()+"." +
								"getIComponentType:   unknown element: " + 
								componentTypeChildElement.getTagName());
						throw new ParseException
						(" unknown componentType child element: " + 
								componentTypeChildElement.getTagName());
					}
				}
			}

			final IComponentType defaultComponentType =
				new DefaultComponentType(
						ID,
						canonicalName,
						componentType,
						singleton,
						description,
						inputPortTypes,
						outputPortTypes,
						cPropertyTypes,
						eventListenerPortTypes,
						eventTriggererPortTypes,
						isExternalGui);

			return defaultComponentType;
		}
		throw new ParseException
		(" expecting componentType Element ");
	}

	/**
	 * Instantiates objects of IEventListenerPortType and
	 * IEventTriggererPortType
	 * @param componentTypeChildElement
	 * @param eventListenerPortTypes
	 * @param eventTriggererPortTypes
	 */
	private void setEvents (Node componentTypeChildElement, 
			Set<IEventListenerPortType> eventListenerPortTypes,
			Set<IEventTriggererPortType> eventTriggererPortTypes)
	{
		NodeList eventPortTypes = componentTypeChildElement.getChildNodes();
		for (int k = 0; k < eventPortTypes.getLength(); k++)
		{
			Node eventPortTypesChild = eventPortTypes.item(k);

			if (eventPortTypesChild instanceof Element) 
			{
				Element portTypeElement = (Element) eventPortTypesChild;

				if (portTypeElement.getTagName().equals("eventListenerPort"))
				{
					String eventPortID = portTypeElement.getAttribute("id");
					NodeList portTypeChildElements = portTypeElement.
					getChildNodes();
					String epDescription = null;

					for (int l = 0; l < portTypeChildElements.getLength(); l++) 
					{
						if (portTypeChildElements.item(l) instanceof Element) 
						{
							Element portTypeChildElement = 
								(Element) portTypeChildElements.item(l);

							if (portTypeChildElement.getTagName().
									equals("description")) 
							{
								epDescription = portTypeChildElement.
															getTextContent();
							}
							
						}
					}
					IEventListenerPortType eventListenerPortType = 
						new DefaultEventListenerPortType(eventPortID,
														epDescription);
					eventListenerPortTypes.add(eventListenerPortType);

				} 
				else if (portTypeElement.getTagName().
						equals("eventTriggererPort")) 
				{
					String eventPortID = portTypeElement.getAttribute("id");
					NodeList portTypeChildElements = 
						portTypeElement.getChildNodes();
					String epDescription = null;

					for (int l = 0; l < portTypeChildElements.getLength(); l++) 
					{
						if (portTypeChildElements.item(l) instanceof Element) 
						{
							Element portTypeChildElement = 
								(Element) portTypeChildElements.item(l);
							if (portTypeChildElement.getTagName().
									equals("description")) 
							{
								epDescription = portTypeChildElement.
															getTextContent();
							}
							
						}
					}
					IEventTriggererPortType eventTriggererPortType = 
						new DefaultEventTriggererPortType(eventPortID, 
														epDescription );
					eventTriggererPortTypes.add(eventTriggererPortType);
				}
			}
		}
	}

	/**
	 * 
	 * @param portTypeElement
	 * @return Objects of DefaultInputPortTypes
	 */
	private DefaultInputPortType getInputPortType (Element portTypeElement)
	{

		final String portID = portTypeElement.getAttribute("id");
	    //final ArrayList<String> bufferedPortIds = new ArrayList<String> (); //Sync


		NodeList portTypeChildElements =
			portTypeElement.getChildNodes();
		String ipDescription = null;
		DataType ipDataType = null;
		Multiplicity ipMultiplicity = null;
		boolean ipMustBeConnected = false;
		Map<String, PropertyType> ipPropertyTypes = null;
		for (int l = 0; l < portTypeChildElements.getLength(); l++) 
		{
			if (portTypeChildElements.item(l) instanceof Element) 
			{
				Element portTypeChildElement = 
					(Element)portTypeChildElements.item(l);

				if (portTypeChildElement.getTagName().equals("description")) 
				{
					ipDescription =	portTypeChildElement.getTextContent();

				} 
				else if (portTypeChildElement.getTagName().
						equals("mustBeConnected")) 
				{
					if (portTypeChildElement.getTextContent().
							compareToIgnoreCase("true") == 0) 
					{
						ipMustBeConnected = true;
					} 
					else
						ipMustBeConnected = false;
				} 
				else if (portTypeChildElement.getTagName().equals("dataType")) 
				{
					ipDataType = getDataType(portTypeChildElement.
							getTextContent());
				} 
				else if (portTypeChildElement.getTagName().equals("properties")) 
				{
					if (portTypeChildElement.getChildNodes().getLength() > 0)
					{
						ipPropertyTypes = 
							getPropertyTypes(portTypeChildElement.
									getChildNodes());
					} 
					else 
					{
						ipPropertyTypes = new LinkedHashMap<String,PropertyType>();
					}
				}
				
				//Sync
				else if (portTypeChildElement.getTagName().
						equals("bufferedPorts")) 
				{
					
					System.out.println("found bufferedPorts:"+portTypeChildElement.getTextContent());

//					if (portTypeChildElement.getChildNodes().getLength() > 0)
//					{
//						NodeList ports = portTypeChildElement.getChildNodes();
//						for (int k = 0; k < ports.getLength(); k++) 
//						{
//							Node port = ports.item(k);
//
//							if (port instanceof Element) 
//							{
//								port = (Element) port;
//								bufferedPortIds.add(port.getTextContent());
//							}
//						}
//					} 
					
				} 
			} 
		}

		DefaultInputPortType inputPortType = new DefaultInputPortType(	
															PortType.INPUT,
															ipDescription,
															ipDataType,
															ipPropertyTypes,
															ipMultiplicity,
															ipMustBeConnected,
															portID);
															//, 
															//bufferedPortIds); //Sync
		return inputPortType;
	}

	
	/**
	 * 
	 * @param portTypeElement
	 * @return
	 */
	private IOutputPortType getOutputPortType(Element portTypeElement) {
		String portID =
			portTypeElement.getAttribute("id");

		NodeList portTypeChildElements =
			portTypeElement.getChildNodes();
		String opDescription = null;
		DataType opDataType = null;

		Map<String, PropertyType>
		opPropertyTypes = null;
		for (int l = 0; l < portTypeChildElements.
		getLength(); l++) {
			if (portTypeChildElements.item(l) instanceof Element) {
				Element portTypeChildElement =
					(Element)
					portTypeChildElements.item(l);


				if (portTypeChildElement.
						getTagName().equals
						("description")) {
					opDescription =
						portTypeChildElement.
						getTextContent();

				} else if (portTypeChildElement.
						getTagName().equals
						("dataType")) {
					opDataType = getDataType
					(portTypeChildElement
							.getTextContent());

				} else if (portTypeChildElement.
						getTagName().equals
						("properties")) {

					if (portTypeChildElement.
							getChildNodes().
							getLength() > 0) {
						opPropertyTypes =
							getPropertyTypes
							(portTypeChildElement.
									getChildNodes());
					} else {
						opPropertyTypes =
							new LinkedHashMap<String,
							PropertyType>();
					}
				}
				

			}
		}

		DefaultOutputPortType outputPortType = new DefaultOutputPortType
		(PortType.OUTPUT,
				opDescription,
				opDataType,
				opPropertyTypes,
				portID);

		return outputPortType;
	}


	private Map<String, PropertyType> getPropertyTypes(NodeList properties) 
	{
		Map<String, PropertyType> propertyTypes =
			new LinkedHashMap<String, PropertyType>();
		PropertyType pt;
		Node node;
		Element property;
		String description;
		boolean getStringList=false;
		
		for (int k = 0; k < properties.getLength(); k++) {
			node = properties.item(k);

			if (node instanceof Element) {
				property = (Element) node;

				if (property.getAttribute("description") != null) {
					description = property.getAttribute("description");
				} else
					description = "";
				
				if (property.getAttribute("getStringList") != null) 
				{
					if (property.getAttribute("getStringList") == "true" ) 
							getStringList=true; 
					else
						getStringList=false ;
				}
				else
					description = "";

				pt = new PropertyType(property.getAttribute("name"),
						getDataType(property.getAttribute("type")),
						description,
						property.getAttribute("value"),
						property.getAttribute("combobox"),
						getStringList);
				propertyTypes.put(property.getAttribute("name"), pt);

			}
		}
		return propertyTypes;
	}

	private ComponentType getComponentType(String componentTypeString) {
		if (componentTypeString.compareToIgnoreCase("ACTUATOR") == 0)
			return ComponentType.ACTUATOR;
		else if (componentTypeString.compareToIgnoreCase("SENSOR") == 0)
			return ComponentType.SENSOR;
		else if (componentTypeString.compareToIgnoreCase("PROCESSOR") == 0)
			return ComponentType.PROCESSOR;
		else if (componentTypeString.compareToIgnoreCase("SPECIAL") == 0)
			return ComponentType.SPECIAL;
		else if (componentTypeString.compareToIgnoreCase("GROUP") == 0)
			return ComponentType.GROUP;
		else
			return null;

	}

	private DataType getDataType(String dataTypeString) {
		if (dataTypeString.compareToIgnoreCase("BOOLEAN") == 0)
			return DataType.BOOLEAN;
		else if (dataTypeString.compareToIgnoreCase("BYTE") == 0)
			return DataType.BYTE;
		else if (dataTypeString.compareToIgnoreCase("CHAR") == 0)
			return DataType.CHAR;
		else if (dataTypeString.compareToIgnoreCase("DOUBLE") == 0)
			return DataType.DOUBLE;
		else if (dataTypeString.compareToIgnoreCase("INTEGER") == 0)
			return DataType.INTEGER;
		else if (dataTypeString.compareToIgnoreCase("STRING") == 0)
			return DataType.STRING;
		else
			return null;
	}

	private boolean getBoolean(final String booleanString) {
		return Boolean.getBoolean(booleanString);
	}
}
