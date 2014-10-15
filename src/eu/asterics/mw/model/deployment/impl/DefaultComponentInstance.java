package eu.asterics.mw.model.deployment.impl;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.asterics.mw.model.deployment.IComponentInstance;
import eu.asterics.mw.model.deployment.IInputPort;
import eu.asterics.mw.model.deployment.IOutputPort;
import eu.asterics.mw.model.deployment.IPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 *
 */
/**
 * @author Costas Kakpusis [kakousis@cs.ucy.ac.cy]
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 *         Date: Jul 15, 2010
 *         Time: 4:03:23 PM
 */
public class DefaultComponentInstance
extends DefaultPropertyful implements IComponentInstance
{
	private final String instanceID;
	private final String componentType;
	private final String description;
	private final Set<IInputPort> inputPorts;
	private final Set<IInputPort> bufferedPorts;

	private final Set<IOutputPort> outputPorts;
	private final Point2D layout;
	//private final boolean easyConfig;
	private final AREGUIElement guiElement;
	
	private HashMap<String, IRuntimeInputPort> wrappers;

	public DefaultComponentInstance(final String instanceID,
			final String componentType,
			final String description,
			final Set<IInputPort> inputPorts,
			final Set<IOutputPort> outputPorts,
			final Map<String, Object> propertyValues,
			final Point2D layout, 
			//final boolean easyConfig,
			final AREGUIElement guiElement, 
			Set<IInputPort> bufferedPorts)
	{
		super(propertyValues);

		this.instanceID = instanceID;
		this.componentType = componentType;
		this.description = description;
		this.inputPorts = inputPorts;
		this.bufferedPorts = bufferedPorts;

		this.outputPorts = outputPorts;
		this.layout = layout;
		//this.easyConfig = easyConfig;
		this.guiElement = guiElement;
		this.wrappers = new HashMap<String, IRuntimeInputPort>();
	}

	public AREGUIElement getAREGUIElement ()
	{
		return guiElement;
	}

	public String getComponentTypeID()
	{
		return componentType;
	}

	public String getDescription()
	{
		return this.description;
	}

	public Set<IInputPort> getInputPorts()
	{
		return this.inputPorts;
	}
	
		
	public Set<IInputPort> getBufferedInputPorts()
	{
		return this.bufferedPorts;
	}

	public String getInstanceID()
	{
		return this.instanceID;
	}

	public Set<IOutputPort> getOutputPorts()
	{
		return this.outputPorts;
	}

	public Set<IPort> getPorts()
	{
		final Set<IPort> allPorts = new HashSet<IPort>();
		allPorts.addAll(inputPorts);
		allPorts.addAll(outputPorts);

		return allPorts;
	}

	public Object getPropertyValue(String propertyName)
	{

		return super.getPropertyValue(propertyName);
	}

	/**
	 * IComponentInstance#getComponentLayout()
	 * @return the component's layout coordinates
	 */
	public Point2D getComponentLayout() {
		return this.layout;
	}

	/**
	 * Appends to the given DOM the XML representation 
	 * of the component instance
	 */
	public void appendXMLElements(Document doc) {

		Element component = doc.createElement("component");
		Element components = (Element) doc.getElementsByTagName("components").item(0);
		components.appendChild(component);
		component.setAttribute("type_id", this.componentType);
		component.setAttribute("id", this.instanceID);
		//component.setAttribute("easyConfig", this.easyConfig+"");
		if (this.description!="")
		{
			Element description = doc.createElement("description");
			component.appendChild(description);
			description.setTextContent(this.description);
		}
		//Create ports
		Element ports = doc.createElement("ports");
		component.appendChild(ports);
		//Add input ports if available
		Set<IInputPort> inputPorts = this.getInputPorts();
		for (IInputPort inPort : inputPorts)
		{
			Element inPortElement = doc.createElement("inputPort");
			ports.appendChild(inPortElement);
			inPortElement.setAttribute("portTypeID", inPort.getPortType());
			if (inPort.getMultiplicityID()!="")
				inPortElement.setAttribute("multiplicityID", 
						inPort.getMultiplicityID());
			//Port properties
			//Add properties if available
			Map<String, Object> portPropertySet = inPort.getPropertyValues();
			if (portPropertySet.size()>0)
			{
				Element portProperties = doc.createElement("properties");
				inPortElement.appendChild(portProperties);

				Set<String> portPropertyKeys = portPropertySet.keySet();
				for (String portPropertyName : portPropertyKeys)
				{

					Element portProperty = doc.createElement("property");
					portProperties.appendChild(portProperty);
					portProperty.setAttribute("name", portPropertyName);
					portProperty.setAttribute("value",
							portPropertySet.get(portPropertyName).toString());
				}
			}
		}
		//Add output ports if available
		Set<IOutputPort> outputPorts = this.getOutputPorts();
		for (IOutputPort outPort : outputPorts)
		{
			Element outPortElement = doc.createElement("outputPort");
			ports.appendChild(outPortElement);
			outPortElement.setAttribute("portTypeID", 
					outPort.getPortType());
			//Port properties
			//Add properties if available
			Map<String, Object> portPropertySet = outPort.getPropertyValues();
			if (portPropertySet.size()>0)
			{
				Element portProperties = doc.createElement("properties");
				outPortElement.appendChild(portProperties);

				Set<String> portPropertyKeys = portPropertySet.keySet();
				for (String portPropertyName : portPropertyKeys)
				{
					Element portProperty = doc.createElement("property");
					portProperties.appendChild(portProperty);
					portProperty.setAttribute("name", portPropertyName);
					portProperty.setAttribute("value",
							portPropertySet.get(portPropertyName).toString());
				}
			}
		}
		//Add component properties if available
		Map<String, Object> propertySet = super.getPropertyValues();
		if (propertySet.size()>0)
		{
			Element properties = doc.createElement("properties");
			component.appendChild(properties);

			Set<String> propertyKeys = propertySet.keySet();
			for (String propertyName : propertyKeys)
			{
				Element property = doc.createElement("property");
				properties.appendChild(property);
				property.setAttribute("name", propertyName);
				property.setAttribute("value",
						propertySet.get(propertyName).toString());
			}
		}

		//Add layout if available
		if (this.layout!=null)
		{
			Element layoutElement = doc.createElement("layout");
			component.appendChild(layoutElement);
			if (this.layout.getX()>=0)
			{
				Element posXElement = doc.createElement("posX");
				posXElement.setTextContent(Double.
						toString(this.layout.getX()));
				layoutElement.appendChild(posXElement);
			}
			if (this.layout.getY()>=0)
			{
				Element posYElement = doc.createElement("posY");
				posYElement.setTextContent(Double.
						toString(this.layout.getY()));
				layoutElement.appendChild(posYElement);
			}

		}
		//Add GUI if available
		if (this.guiElement!=null)
		{
			Element guiElement = doc.createElement("gui");
			component.appendChild(guiElement);
			if (this.guiElement.posX>=0)
			{
				Element posXElement = doc.createElement("posX");
				posXElement.setTextContent(Integer.
						toString(this.guiElement.posX));
				guiElement.appendChild(posXElement);
			}
			if (this.guiElement.posY>=0)
			{
				Element posYElement = doc.createElement("posY");
				posYElement.setTextContent(Integer.
						toString(this.guiElement.posY));
				guiElement.appendChild(posYElement);
			}
			if (this.guiElement.width>=0)
			{
				Element widthElement = doc.createElement("width");
				widthElement.setTextContent(Integer.
						toString(this.guiElement.width));
				guiElement.appendChild(widthElement);
			}
			if (this.guiElement.height>=0)
			{
				Element heightElement = doc.createElement("height");
				heightElement.setTextContent(Integer.
						toString(this.guiElement.height));
				guiElement.appendChild(heightElement);
			}

		}
	}

	@Override
	public String toString() {
		final StringBuffer stringBuffer = new StringBuffer();

		stringBuffer.append("instanceID ").append(instanceID).append(", \n");
		stringBuffer.append("componentType ").append(componentType).append(", \n");
		stringBuffer.append("description ").append(description).append(", \n");
		stringBuffer.append("inputPorts ").append(inputPorts).append(", \n");
		stringBuffer.append("outputPorts").append(outputPorts).append(", \n");

		return stringBuffer.toString();
	}

	@Override
	public void setWrapper(String targetInputPortID, IRuntimeInputPort wrapper) {
		this.wrappers.put(targetInputPortID, wrapper);
		
	}
	public IRuntimeInputPort getWrapper(String targetInputPortID) {
		return this.wrappers.get(targetInputPortID);		
	}



	
}