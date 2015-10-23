package eu.asterics.mw.are.parsers;

import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.asterics.mw.are.ComponentRepository;
import eu.asterics.mw.are.DeploymentManager;
import eu.asterics.mw.are.exceptions.ParseException;
import eu.asterics.mw.model.DataType;
import eu.asterics.mw.model.bundle.IComponentType;
import eu.asterics.mw.model.bundle.impl.GroupReferences;
import eu.asterics.mw.model.deployment.IChannel;
import eu.asterics.mw.model.deployment.IComponentInstance;
import eu.asterics.mw.model.deployment.IEventChannel;
import eu.asterics.mw.model.deployment.IEventEdge;
import eu.asterics.mw.model.deployment.IInputPort;
import eu.asterics.mw.model.deployment.IOutputPort;
import eu.asterics.mw.model.deployment.impl.AREGUIElement;
import eu.asterics.mw.model.deployment.impl.DefaultACSGroup;
import eu.asterics.mw.model.deployment.impl.DefaultChannel;
import eu.asterics.mw.model.deployment.impl.DefaultComponentInstance;
import eu.asterics.mw.model.deployment.impl.DefaultEventChannel;
import eu.asterics.mw.model.deployment.impl.DefaultEventEdge;
import eu.asterics.mw.model.deployment.impl.DefaultInputPort;
import eu.asterics.mw.model.deployment.impl.DefaultOutputPort;
import eu.asterics.mw.model.deployment.impl.DefaultPortAlias;
import eu.asterics.mw.model.deployment.impl.DefaultRuntimeModel;
import eu.asterics.mw.model.deployment.impl.ModelDescription;
import eu.asterics.mw.model.deployment.impl.ModelGUIInfo;
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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 *
 */

/**
 * @author 
 * This class parses models to be deployed and on success produces 
 * a DefaultRuntimeModel as a result.
 * Date: 
 */

public class DefaultDeploymentModelParser
{
	private Logger logger  = null;

	private final ModelValidator modelValidator;
	private DocumentBuilder builder;
	private DefaultDeploymentModelParser()
	{
		logger  = AstericsErrorHandling.instance.getLogger();
		this.modelValidator = ModelValidator.getInstance();
	}

	/**
	 * Is used to create {@link DefaultDeploymentModelParser} with custom ModelValidator.
	 * @param modelValidator
	 */
	private DefaultDeploymentModelParser(ModelValidator modelValidator)
	{
		logger  = AstericsErrorHandling.instance.getLogger();
		this.modelValidator = modelValidator;
	}

	public static final DefaultDeploymentModelParser instance =
		new DefaultDeploymentModelParser();

	private static final int MAX_INPUT_STRREAM = 1000000000;

	/**
	 * Create {@link DefaultDeploymentModelParser} instance with custom ModelValidator instance.
	 * @param modelValidator
	 * @return
	 */
	public static DefaultDeploymentModelParser create(ModelValidator modelValidator) {
		return new DefaultDeploymentModelParser(modelValidator);
	}
	
	/** 
	 * Creates an InputStream from the input file and calls parseModel
	 * to parse the InputStream
	 * @param modelFile the file to be parsed
	 * @return a DefaultRuntimeModel
	 * @throws ParseException, FileNotFoundException
	 */
	public DefaultRuntimeModel parseModel(File modelFile)
	throws ParseException, FileNotFoundException
	{
	
		return this.parseModel(new FileInputStream(modelFile));
	}

	
	/**
	 * Parses an InputStream and on success produces 
	 * a DefaultRuntimeModel as a result.
	 * @param modelInputStream the InputStream to be parsed
	 * @return a DefaultRuntimeModel
	 * @throws ParseException
	 */
	public DefaultRuntimeModel parseModel(final InputStream modelInputStream)
	throws ParseException
	{
		modelInputStream.mark(MAX_INPUT_STRREAM);
		DocumentBuilderFactory builderFactory = 
			DocumentBuilderFactory.newInstance();

		if (!modelValidator.isValidDeploymentDescriptor(modelInputStream))
		{
			throw new ParseException ("Invalid deployment model");
		}
		try
		{
			modelInputStream.reset();
			builder = builderFactory.newDocumentBuilder();
			synchronized(builder){
			Document document = builder.parse(modelInputStream);  
			return parse(document);
			}
		} 
		catch (ParserConfigurationException e)
		{
			logger.warning(this.getClass().getName()+".parseModel: " +
					"parse error -> /n"+ e.getMessage());
			throw new ParseException (" parse error: ParserConfigurationException " 
					+ e.getMessage());
		} 
		catch (SAXException e)
		{
			logger.warning(this.getClass().getName()+".parseModel: " +
					"parse error -> /n"+ e.getMessage());
			throw new ParseException (" parse error: SAXException " 
					+ e.getMessage());
		}
		catch (IOException e)
		{
			logger.warning(this.getClass().getName()+".parseModel: " +
					"parse error -> /n"+ e.getMessage());
			throw new ParseException (" parse error: IOException " 
					+ e.getMessage());
		}
	}


	/**
	 * Parses an input url and on success produces 
	 * a DefaultRuntimeModel as a result.
	 * @param url the url to be parsed
	 * @return a DefaultRuntimeModel
	 * @throws ParseException, UnsupportedEncodingException
	 */
	public DefaultRuntimeModel parseModel(String url)
	throws ParseException, UnsupportedEncodingException
	{
		DocumentBuilderFactory builderFactory = 
			DocumentBuilderFactory.newInstance();
		
		if (!modelValidator.isValidDeploymentDescriptor(
				new ByteArrayInputStream(url.getBytes("UTF-16"))))
		{
			throw new ParseException ("Invalid deployment model");
		}
		try
		{
			builder = builderFactory.newDocumentBuilder();
			synchronized(builder){

			Document document = builder.parse(url);
			
			return parse(document);
			}
		} 
		catch (ParserConfigurationException e)
		{
			logger.warning(this.getClass().getName()+".parseModel: " +
					"parse error -> /n"+ e.getMessage());
		} 
		catch (SAXException e)
		{
			logger.warning(this.getClass().getName()+".parseModel: " +
					"parse error -> /n"+ e.getMessage());
		} 
		catch (IOException e)
		{
			logger.warning(this.getClass().getName()+".parseModel: " +
					"parse error -> /n"+ e.getMessage());
		}
		return null;
	}

	public static final String COMPONENTS_TAG = "components";
	public static final String COMPONENT_TAG = "component";
	public static final String COMPONENT_ID = "id";
	public static final String PORT_TAG = "port";
	public static final String EVENT_PORT_TAG = "eventPort";
	public static final String PORT_ID = "id";
	public static final String COMPONENT_TYPE_ID = "type_id";
	public static final String CHANNELS_TAG = "channels";
	public static final String CHANNEL_TAG = "channel";
	public static final String EVENT_CHANNEL_TAG = "eventChannel";
	public static final String DESCRIPTION = "description";
	public static final String SOURCES = "sources";
	public static final String SOURCE = "source";
	public static final String TARGETS = "targets";
	public static final String TARGET = "target";
	public static final String INPUT_PORT = "inputPort";
	public static final String OUTPUT_PORT = "outputPort";
	public static final String PORT_TYPE_ID = "portTypeID";
	public static final String MULTIPLICITY_ID = "multiplicityID";
	public static final String PROPERTIES = "properties";
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_VALUE = "value";
	public static final String LAYOUT = "layout";


	private DefaultRuntimeModel parse(Document document)
	throws ParseException
	{
		// System.out.println("*** IN PARSE !");
		final Set<IChannel> channelsSet = new LinkedHashSet<IChannel>();
		final Set<IEventChannel> eventChannelsSet =
			new LinkedHashSet<IEventChannel>();
		final Set<IComponentInstance> componentsSet =
			new LinkedHashSet<IComponentInstance>();
		final ArrayList <DefaultACSGroup> groups = new ArrayList<DefaultACSGroup>();

		// model - get the root element
		final Element rootElement = document.getDocumentElement();

		String modelName = rootElement.getAttribute("modelName");
		String modelVersion = rootElement.getAttribute("version");
		//String modelDescription = rootElement.getAttribute("modelDescription");
		ModelDescription modelDescription=new ModelDescription ("", "", "");
		ModelGUIInfo modelGuiInfo = null;
			
		NodeList rootChildren = rootElement.getChildNodes();
		for (int i = 0; i < rootChildren.getLength(); i++)
		{
			Node rootChild = rootChildren.item(i);
			if (rootChild instanceof Element)
			{
				Element roodChildElement = (Element) rootChildren.item(i);
				if (roodChildElement.getTagName().equals("components"))
				{
					NodeList components = roodChildElement.getChildNodes();
					if (components.getLength() <= 0)
					{
						logger.warning(this.getClass().getName()+".parse: " +
								"Empty deployment model /n");
						throw new ParseException("Empty deployment model");
					} 
					else
					{
						//Iterate through component elements
						for (int j = 0; j < components.getLength(); j++)
						{				
							final Node componentNode = components.item(j);
							if (componentNode instanceof Element)
							{
								// get a component element
								
								final Element componentElement =
									(Element) componentNode;
								
								IComponentInstance componentInstance = getComponentInstance(componentElement);
								if(componentInstance == null) throw new ParseException("Could not find component instance for component element: "+componentElement);
								componentsSet.add(componentInstance);
							}
						}
					}
				} 
				else if (roodChildElement.getTagName().equals("channels"))
				{
					NodeList channels = roodChildElement.getChildNodes();
					for (int q = 0; q < channels.getLength(); q++)
					{
						Node channel = channels.item(q);
						if (channel instanceof Element)
						{
							Element channelElement = (Element) channel;
							if (channelElement.getTagName().equals("channel"))
							{
								channelsSet.add(getChannel(channelElement));
							}
						}
					}
				} 
				else if (roodChildElement.getTagName().equals("eventChannels"))
				{
					//get event channels
					//eventChannelsSet.add(getEventChannel(roodChildElement));
					handleEventChannels(roodChildElement, eventChannelsSet);
				}
				else if (roodChildElement.getTagName().equals("groups"))
				{
					NodeList groupsNodeList = roodChildElement.getChildNodes();
					for (int q = 0; q < groupsNodeList.getLength(); q++)
					{
						Node groupNode = groupsNodeList.item(q);
						if (groupNode instanceof Element)
						{
							Element groupElement = (Element) groupNode;
							if (groupElement.getTagName().equals("group"))
							{
								DefaultACSGroup group = createGroup(groupElement);
								if (group != null)
									groups.add(group);
							}
						}
					}
				}
				else if (roodChildElement.getTagName().equals("modelDescription"))
				{
					NodeList descList = roodChildElement.getChildNodes();
					String desc= "", shortDesc="", req="";
					for (int r = 0; r < descList.getLength(); r++)
					{
						Node child = descList.item(r);
						if (child instanceof Element)
						{
							Element descElement = (Element) child;
							if (descElement.getTagName().
									equals("shortDescription"))
							{
								shortDesc = descElement.getTextContent();
							}
							else if (descElement.getTagName().
									equals("description"))
							{
								desc=descElement.getTextContent();
							}
							else if (descElement.getTagName().
									equals("requirements"))
							{
								req=descElement.getTextContent();
							}
						}
					}
					modelDescription = 
						new ModelDescription (desc, shortDesc, req);
					
				}
				else if (roodChildElement.getTagName().equals("modelGUI"))
				{
					modelGuiInfo = getModelGuiType(roodChildElement);
				}
				else
				{
					logger.warning(this.getClass().getName()+".parse: " +
					"Unknown model element/n");
					throw new ParseException("Unknown model element");
				}
			}
		}

		return new DefaultRuntimeModel (modelName, modelDescription, 
				channelsSet, eventChannelsSet, componentsSet, modelVersion,
				groups, modelGuiInfo);
	}

	private ModelGUIInfo getModelGuiType(Element roodChildElement) 
		throws ParseException
	{
		NodeList descList = roodChildElement.getChildNodes();
		boolean decoration = false, fullscreen = false, 
		alwaysOnTop =false, toSysTray = false, 
		shopControlPanel = false;
		int x = 0, y = 0, w = 0, h = 0;
		int found = 0;
		for (int r = 0; r < descList.getLength(); r++)
		{
			Node child = descList.item(r);
			if (child instanceof Element)
			{
				Element descElement = (Element) child;
				if (descElement.getTagName().
						equals("Decoration"))
				{
					//System.out.print("\ndecoration " + descElement.getTextContent());
					decoration = getBoolean(descElement.getTextContent().trim());
					found |= 1;
				}
				else if (descElement.getTagName().
						equals("Fullscreen"))
				{
					//System.out.print("\nfullscreen " + descElement.getTextContent());
					fullscreen = getBoolean(descElement.getTextContent().trim());
					found |= 1 << 1;
				}
				else if (descElement.getTagName().
						equals("AlwaysOnTop"))
				{
					//System.out.print("\nalwaysOnTop " + descElement.getTextContent());
					alwaysOnTop = getBoolean(descElement.getTextContent().trim());
					found |= 1 << 2;
				}
				else if (descElement.getTagName().
						equals("ToSystemTray"))
				{
					//System.out.print("\ntoSysTray " + descElement.getTextContent());
					toSysTray = getBoolean(descElement.getTextContent().trim());
					found |= 1 << 3;
				}
				else if (descElement.getTagName().
						equals("ShopControlPanel"))
				{
					//System.out.print("\nshopControlPanel " + descElement.getTextContent());
					shopControlPanel = getBoolean(descElement.getTextContent().trim());
					found |= 1 << 4;
				}
				else if (descElement.getTagName().
						equals("AREGUIWindow"))
				{	
					
					NodeList dimList = child.getChildNodes();
					for (int i = 0; i < dimList.getLength(); i++)
					{
						Node node = dimList.item(i);
						if (node instanceof Element)
						{
							Element nodeElement = (Element) node;
							// System.out.println("AREGUIWindow:" + nodeElement.getTagName());
							if (nodeElement.getTagName().equals("posX"))
							{
								x = Integer.valueOf(nodeElement.getTextContent());
								found |= 1 << 5;
							}
							else if (nodeElement.getTagName().equals("posY"))
							{
								y = Integer.valueOf(nodeElement.getTextContent());
								found |= 1 << 6;
							}
							else if (nodeElement.getTagName().equals("width"))
							{
								w = Integer.valueOf(nodeElement.getTextContent());
								found |= 1 << 7;
							} 
							else if (nodeElement.getTagName().equals("height"))
							{
								h = Integer.valueOf(nodeElement.getTextContent());
								found |= 1 << 8;
							}
						}
					}
				}
			}
		}
		if (found == 0x1ff)
		{
			return new ModelGUIInfo(decoration, fullscreen, alwaysOnTop, 
					toSysTray, shopControlPanel, x, y, w, h);
		}
		else
		{
			logger.warning(this.getClass().getName()+".getModelGuiType: " +
			"not all elements found: " + found + "/n");
			throw new ParseException("Unknown model element");
		}
	}
	
	private DefaultACSGroup createGroup(Element groupElement) 
	{
		NodeList grChild =	groupElement.getChildNodes();
		String description="";
		ArrayList<String> componentIds = new ArrayList<String>();
		ArrayList<DefaultPortAlias> portAliases = new ArrayList<DefaultPortAlias>();
		
		
			for (int i = 0; i < grChild.getLength(); i++)
			{
				Node groupChild = grChild.item(i);
				if (groupChild instanceof Element)
				{
					Element groupChildElement = (Element) groupChild;
					if (groupChildElement.getTagName().
							compareTo("description") == 0)
					{
						description =
								groupChildElement.getTextContent();
					}
					else if (groupChildElement.getTagName().
							compareTo("componentId") == 0)
					{
						componentIds.add(groupChildElement.getTextContent());
					}
					else if (groupChildElement.getTagName().
							compareTo("portAlias") == 0)
					{
						portAliases.add(new DefaultPortAlias(groupChildElement.
								getAttribute("portId"),groupChildElement.
								getAttribute("portAlias")));
					}
				}
			}
			String id = groupElement.getAttribute("id");
			
			return new DefaultACSGroup(description, componentIds, 
					portAliases, id);
		
	}


	private DefaultComponentInstance getComponentInstance
	(Node componentNode) throws ParseException
	{

		if (componentNode instanceof Element)
		{
	
			ComponentRepository componentRepository = 
					ComponentRepository.instance;

			AREGUIElement areGUIElement = null;
			// get a component element
			final Element componentElement =
				(Element) componentNode;
			//Get attributes
			final String cTypeID = componentElement.getAttribute("type_id");
			final String cID = componentElement.getAttribute("id");

			//call getComponentType here to trigger installing of bundle; Should maybe moved to DeploymentManager.deployModel			
			IComponentType desiredComponent=
			componentRepository.getComponentType(cTypeID);
						
			//boolean easyConfig = 
				//Boolean.parseBoolean
				//(componentElement.getAttribute("easyConfig"));
			//get component children
			NodeList componentChildren =
				componentNode.getChildNodes();

			String cDescription = null;
			Set<IInputPort> inputPorts = new LinkedHashSet<IInputPort>();
			Set<IOutputPort> outputPorts = new LinkedHashSet<IOutputPort>();
			Set<IInputPort> bufferedPorts = new LinkedHashSet<IInputPort>();
			String posX = "0";
			String posY = "0";
			Map<String, Object> cPropertyValues = new LinkedHashMap<String, Object>();
	
			//Iterate through component children elements
			for (int k = 0; k < componentChildren.getLength(); k++)
			{
				final Node componentChildNode = componentChildren.item(k);
				if (componentChildNode instanceof Element)
				{
					// get a component element
					final Element componentChildElement =
						(Element) componentChildNode;
					if (componentChildElement.getTagName().
							equals("description"))
					{
						cDescription = componentChildElement.getTextContent();
					} 
					else if (componentChildElement.getTagName().
							equals("ports"))
					{
						NodeList ports =
							componentChildElement.getChildNodes();
						//Iterate through component children elements
						for (int m = 0; m < ports.getLength(); m++)
						{
							Node port = ports.item(m);
							if (port instanceof Element)
							{
								Element portElement = (Element) port;
								if (portElement.getTagName().
										equals("inputPort"))
								{
									IInputPort inputPort = getInputPort(portElement, cTypeID);
									inputPorts.add(inputPort);
									
																		
									if (portElement.hasAttribute("sync") && 
									portElement.getAttribute("sync").compareToIgnoreCase("true")==0)
									{
										bufferedPorts.add(inputPort);
									}
								} 
								else if (portElement.getTagName().
										equals("outputPort"))
								{
									outputPorts.
									add(getOutputPort(portElement, cTypeID));
								} 
								else
								{
									logger.warning(this.getClass().getName()+
											".parse: Unknown port element/n");
									throw new 
									ParseException("Unknown port element");
								}
							}
						}
					} 
					else if (componentChildElement.getTagName().
							equals("properties"))
					{
						if (componentChildElement.getChildNodes().
								getLength() > 0)
							cPropertyValues = 
								getPropertyValues(
										componentChildElement.getChildNodes());
						else
							cPropertyValues = new HashMap<String, Object>();

					} 
					else if (componentChildElement.getTagName().
							equals("layout"))
					{
						NodeList positions =
							componentChildElement.getChildNodes();
						//Iterate through component children elements
						for (int p = 0; p < positions.getLength(); p++)
						{
							Node position =
								positions.item(p);
							if (position instanceof Element)
							{
								Element positionElement = (Element) position;
								if (positionElement.getTagName().
										compareTo("posX") == 0)
								{
									posX = position.getTextContent();
								}
								if (positionElement.getTagName().
										compareTo("posY") == 0)
								{
									posY = position.getTextContent();
								}
							}
						}
					}
					else if (componentChildElement.getTagName().
							equals("gui"))
					{
						int x, y, w, h;
						x=y=w=h=0;
						NodeList positions =
							componentChildElement.getChildNodes();
						//Iterate through component children elements
						for (int p = 0; p < positions.getLength(); p++)
						{
							Node position =
								positions.item(p);
							if (position instanceof Element)
							{
								Element positionElement = (Element) position;
								if (positionElement.getTagName().
										compareTo("posX") == 0)
								{
									x = Integer.valueOf(position.getTextContent());
								}
								if (positionElement.getTagName().
										compareTo("posY") == 0)
								{
									y = Integer.valueOf(position.getTextContent());
								}
								if (positionElement.getTagName().
										compareTo("width") == 0)
								{
									w = Integer.valueOf(position.getTextContent());
								}
								if (positionElement.getTagName().
										compareTo("height") == 0)
								{
									h = Integer.valueOf(position.getTextContent());
								}
							}
						}
						areGUIElement = new AREGUIElement (x, y, w, h);
					}
				}
			}

			return new DefaultComponentInstance(
					cID,
					cTypeID,
					cDescription,
					inputPorts,
					outputPorts,
					cPropertyValues,
					new Point(Integer.parseInt(posX),
							Integer.parseInt(posY)),
							//easyConfig,
					areGUIElement,
					bufferedPorts);
		}
		throw new RuntimeException("Unknown runtime element");

	}


	private IOutputPort getOutputPort (Element portElement, String cTypeID)
	{
		GroupReferences groupReferences =null;
		final String opPortID = portElement.getAttribute(PORT_TYPE_ID);
		//Get port children
		NodeList propertiesList = portElement.
		getElementsByTagName("properties");
		Map<String, Object> opPropertyValues = new LinkedHashMap<String, Object>();
		if (propertiesList != null && propertiesList.getLength() > 0)
		{
			Node propertiesNode = propertiesList.item(0);
			if (propertiesNode instanceof Element)
			{
				Element propertiesElement = (Element) propertiesNode;
				if (propertiesElement.getChildNodes().getLength() > 0)
					opPropertyValues = getPropertyValues(
							propertiesElement.getChildNodes());
				else
					opPropertyValues = new HashMap<String, Object>();
			}
		}
		NodeList refsList = portElement.getElementsByTagName("refs");
		if (refsList != null && refsList.getLength() > 0)
		{
			Node refsNode = refsList.item(0);
			if (refsNode instanceof Element)
			{
				Element refsElement = (Element) refsNode;
				if (refsElement.getChildNodes().getLength() > 0)
					groupReferences = getReferences (refsElement);
			}
		}
		DataType outputPortDataType = ComponentRepository.
		instance.
		getPortDataType(cTypeID, opPortID);

		DefaultOutputPort outputPort = 
			new DefaultOutputPort(opPortID, 
					outputPortDataType, 
					opPropertyValues,
					groupReferences);
		return outputPort;
	}

	private IInputPort getInputPort(Element portElement, String cTypeID) 
	{
		GroupReferences groupReferences =null;
		final String ipPortID = portElement.getAttribute(PORT_TYPE_ID);
		final String ipMultiplicityID = portElement.
		getAttribute(MULTIPLICITY_ID);
		//Get port children
		NodeList propertiesList = portElement.
		getElementsByTagName("properties");
		Map<String, Object> ipPropertyValues = new LinkedHashMap<String, Object>();
		
		if (propertiesList != null && propertiesList.getLength() > 0)
		{
			Node propertiesNode = propertiesList.item(0);
			if (propertiesNode instanceof Element)
			{
				Element propertiesElement = (Element) propertiesNode;
				if (propertiesElement.getChildNodes().getLength() > 0)
					ipPropertyValues = getPropertyValues(
							propertiesElement.getChildNodes());
				else
					ipPropertyValues = new LinkedHashMap<String, Object>();
			}
		}
		
		NodeList refsList = portElement.getElementsByTagName("refs");
		if (refsList != null && refsList.getLength() > 0)
		{
			Node refsNode = refsList.item(0);
			if (refsNode instanceof Element)
			{
				Element refsElement = (Element) refsNode;
				if (refsElement.getChildNodes().getLength() > 0)
					groupReferences = getReferences (refsElement);
			}
		}
				
		final DataType inputPortDataType = ComponentRepository.instance.
		getPortDataType(cTypeID, ipPortID);
		
		final IInputPort inputPort = 
			new DefaultInputPort(ipPortID, 
					inputPortDataType, 
					ipMultiplicityID, 
					ipPropertyValues,
					groupReferences);
		return inputPort;
	}

	private DefaultChannel getChannel (Element channelElement)
	{
		String chID = channelElement.getAttribute("id");
		//get channel children

		String chDescription = null;
		String besComponent = null;
		String betComponent = null;
		String betPort = null;
		String besPort = null;

		NodeList chChild =
			channelElement.getChildNodes();
		for (int ij = 0; ij < chChild.getLength(); ij++)
		{
			Node chanelChild =
				chChild.item(ij);
			if (chanelChild instanceof Element)
			{
				Element chanelChildElement = (Element) chanelChild;
				if (chanelChildElement.getTagName().
						compareTo("description") == 0)
				{
					chDescription =
						chanelChildElement.getTextContent();
				}
				if (chanelChildElement.getTagName().compareTo("source") == 0)
				{


					NodeList beList =
						chanelChildElement.getChildNodes();
					for (int ik = 0; ik < beList.getLength(); ik++)
					{
						Node beListChild =
							beList.item(ik);
						if (beListChild instanceof Element)
						{
							Element beListChildElement = (Element) beListChild;
							if (beListChildElement.getTagName().
									compareTo("component") == 0)
							{
								besComponent = beListChildElement.
								getAttribute("id");
							}
							if (beListChildElement.getTagName().
									compareTo("port") == 0)
							{
								besPort = beListChildElement.getAttribute("id");
							}
						}
					}
				}
				if (chanelChildElement.getTagName().compareTo("target") == 0)
				{


					NodeList beList =
						chanelChildElement.getChildNodes();
					for (int il = 0; il < beList.getLength(); il++)
					{
						Node beListChild =
							beList.item(il);
						if (beListChild instanceof Element)
						{
							Element beListChildElement = (Element) beListChild;
							if (beListChildElement.getTagName().
									compareTo("component") == 0)
							{
								betComponent = beListChildElement.
								getAttribute("id");
							}
							if (beListChildElement.getTagName().
									compareTo("port") == 0)
							{
								betPort = beListChildElement.getAttribute("id");
							}
						}
					}
				}
			}
		}//Channel child

		final Map<String, Object> channelProperties = 
			new LinkedHashMap<String, Object>();

		return new DefaultChannel(
				chDescription,
				besComponent,
				besPort,
				betComponent,
				betPort,
				chID,
				channelProperties);
	}

	private void handleEventChannels(Element roodChildElement, Set<IEventChannel> eventChannelsSet)
	{
		NodeList eventChannelChildren = 
			roodChildElement.getChildNodes();

		for (int t = 0; t < eventChannelChildren.getLength(); t++)
		{
			Node ecChild = eventChannelChildren.item(t);
			if (ecChild instanceof Element)
			{
				eventChannelsSet.add(getEventChannel((Element) ecChild));
			}
		}
		
	}	
	
	private DefaultEventChannel getEventChannel (Element ecChild)
	{
		final Set<IEventEdge> sourcesEdge = 
			new LinkedHashSet<IEventEdge>();
		final Set<IEventEdge> targetsEdge = 
			new LinkedHashSet<IEventEdge>();

		GroupReferences groupReferences=null;
		// handle a single eventChannel

		String echID = null;
		String echDescription = null;

				echID = ((Element) ecChild).getAttribute("id");
				final NodeList sourcesOrTargetsNodeList =
					ecChild.getChildNodes();


				for(int u = 0; u < sourcesOrTargetsNodeList.getLength(); u++)
				{

					final Node node = sourcesOrTargetsNodeList.item(u);
					if((node instanceof Element) && ((Element) node).
							getTagName().equals("description"))
					{
						echDescription = ((Element) node).getTextContent();
						break;
					}
				}

				Element sourcesElement = null;
				for(int p = 0; p < sourcesOrTargetsNodeList.getLength(); p++)
				{
					final Node node = sourcesOrTargetsNodeList.item(p);
					if((node instanceof Element) && ((Element) node).
							getTagName().equals("sources"))
					{
						sourcesElement = (Element) node;
						break;
					}
				}

				Element targetsElement = null;
				for(int q = 0; q < sourcesOrTargetsNodeList.getLength(); q++)
				{
					final Node node = sourcesOrTargetsNodeList.item(q);
					if((node instanceof Element) && ((Element) node).
							getTagName().equals("targets"))
					{
						targetsElement = (Element) node;
						break;
					}
				}

				NodeList sourceNodeList = sourcesElement.getChildNodes();
				for (int u = 0; u < sourceNodeList.getLength(); u++)
				{
					Node source = sourceNodeList.item(u);
					String ecbesComponent = null;
					String ecbesPort = null;
					if (source instanceof Element)
					{
						Element sourceElement = (Element) source;
						if (sourceElement.getTagName().equals("source"))
						{
							Node c = sourceElement.
							getElementsByTagName(COMPONENT_TAG).item(0);

							if (c instanceof Element)
							{
								Element cE = (Element) c;
								ecbesComponent = cE.getAttribute("id");
							}
							Node p = sourceElement.
							getElementsByTagName(EVENT_PORT_TAG).item(0);

							if (p instanceof Element)
							{
								Element pE = (Element) p;
								ecbesPort = pE.getAttribute("id");
							}
							
							Node r = sourceElement.
							getElementsByTagName("refs").item(0);

							if (r instanceof Element)
							{
								Element rE = (Element) r;
								groupReferences = getReferences (rE);
							}

							sourcesEdge.
							add(new DefaultEventEdge(ecbesComponent,
									ecbesPort, groupReferences));
						}
					}
				}

				groupReferences=null;
				NodeList targetNodeList = targetsElement.getChildNodes();
				for (int w = 0; w < targetNodeList.getLength(); w++)
				{
					Node target = targetNodeList.item(w);
					String ecbetComponent = null;
					String ecbetPort = null;
					if (target instanceof Element)
					{
						Element targetElement = (Element) target;
						if (targetElement.getTagName().equals("target"))
						{
							Node c = targetElement.
							getElementsByTagName(COMPONENT_TAG).item(0);
							if (c instanceof Element)
							{
								Element cE = (Element) c;
								ecbetComponent = cE.getAttribute("id");
							}
							Node p = targetElement.
							getElementsByTagName(EVENT_PORT_TAG).item(0);
							if (p instanceof Element)
							{
								Element pE = (Element) p;
								ecbetPort = pE.getAttribute("id");
							}
							Node r = targetElement.
							getElementsByTagName("refs").item(0);
							if (r instanceof Element)
							{
								Element rE = (Element) r;
								groupReferences = getReferences (rE);
							}
							targetsEdge.add(
									new DefaultEventEdge(ecbetComponent,
											ecbetPort, groupReferences));
						}
					}
		}

		IEventEdge[] sourcesEdgeArray = sourcesEdge.
		toArray(new IEventEdge[sourcesEdge.size()]);
		IEventEdge[] targetsEdgeArray = targetsEdge.
		toArray(new IEventEdge[targetsEdge.size()]);

		return new DefaultEventChannel(
				sourcesEdgeArray,
				targetsEdgeArray,
				echID,
				echDescription);
	}

	private Map<String, Object> getPropertyValues(NodeList properties)
	{
		final Map<String, Object> propertyValues = 
			new LinkedHashMap<String, Object>();
		for (int k = 0; k < properties.getLength(); k++)
		{
			final Node node = properties.item(k);
			if (node instanceof Element)
			{
				final Element property = (Element) node;
				propertyValues.put(
						property.getAttribute(PROPERTY_NAME),
						property.getAttribute(PROPERTY_VALUE));
			}
		}
		return propertyValues;
	}
	
	private GroupReferences getReferences(Element elem) 
	{
		String compID="", portID="";
		NodeList list =	elem.getChildNodes();
		for (int l = 0; l < list.getLength(); l++) {
			if (list.item(l) instanceof Element) 
			{
				Element child =(Element) list.item(l);
				if (child.getTagName().equals("componentID")) 
				{
					 compID = child.getTextContent();
				} 
				else if (child.getTagName().equals("portID")) 
				{
					portID = child.getTextContent();
				}
			}
		}
		return new GroupReferences (compID, portID);
	}
	
	private boolean getBoolean(final String booleanString) 
	{
		return Boolean.parseBoolean(booleanString); 
	}
}
