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

package eu.asterics.mw.model.bundle.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import eu.asterics.mw.model.DataType;
import eu.asterics.mw.model.bundle.ComponentType;
import eu.asterics.mw.model.bundle.IComponentType;
import eu.asterics.mw.model.bundle.IEventListenerPortType;
import eu.asterics.mw.model.bundle.IEventPortType;
import eu.asterics.mw.model.bundle.IEventTriggererPortType;
import eu.asterics.mw.model.bundle.IInputPortType;
import eu.asterics.mw.model.bundle.IOutputPortType;
import eu.asterics.mw.model.bundle.IPortType;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * @author Costas Kakpusis [kakousis@cs.ucy.ac.cy]
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 *         Date: Jul 15, 2010
 *         Time: 4:03:23 PM
 */
public class DefaultComponentType
        extends DefaultPropertyfulType implements IComponentType
{
    private final String ID;
    private final String canonicalName;
    private final ComponentType componentType;
    private final boolean isSingleton;
    private final boolean isExternalGUIElement;
    private final String description;
    private final Set<IInputPortType> inputPortTypes;
    private final Set<IOutputPortType> outputPortTypes;
    private final Set<IEventListenerPortType> eventListenerPortTypes;
    private final Set<IEventTriggererPortType> eventTriggererPortTypes;
    private Logger logger = null;

    public DefaultComponentType(
            final String ID,
            final String canonicalName,
            final ComponentType componentType,
            final boolean isSingleton,
            final String description,
            final Set<IInputPortType> inputPortTypes,
            final Set<IOutputPortType> outputPortTypes,
            final Map<String, PropertyType> propertyTypes,
            final Set<IEventListenerPortType> eventListenerPortTypes,
            final Set<IEventTriggererPortType> eventTriggererPortTypes, 
            final boolean IsExternalGUIElement)
    {
        super(propertyTypes);

        this.ID = ID;
        this.canonicalName = canonicalName;
        this.componentType = componentType;
        this.isSingleton = isSingleton;
        this.description = description;
        this.inputPortTypes = inputPortTypes;
        this.outputPortTypes = outputPortTypes;
        this.eventListenerPortTypes = eventListenerPortTypes;
        this.eventTriggererPortTypes = eventTriggererPortTypes;
        this.isExternalGUIElement = IsExternalGUIElement;
        logger = AstericsErrorHandling.instance.getLogger();
    }
    
    public DefaultComponentType(
            final String ID,
            final String canonicalName,
            final ComponentType componentType,
            final boolean isSingleton,
            final String description,
            final Set<IInputPortType> inputPortTypes,
            final Set<IOutputPortType> outputPortTypes,
            final Map<String, PropertyType> propertyTypes,
            final Set<IEventListenerPortType> eventListenerPortTypes,
            final Set<IEventTriggererPortType> eventTriggererPortTypes)
    {
    	this(ID, canonicalName, componentType, isSingleton, description,
    			inputPortTypes, outputPortTypes, propertyTypes, 
    			eventListenerPortTypes, eventTriggererPortTypes, false);
    }
    

    public String getID()
    {
        return ID;
    }

    public String getCanonicalName()
    {
        return canonicalName;
    }

    public ComponentType getType()
    {
        return componentType;
    }

    public boolean isSingleton()
    {
        return isSingleton;
    }

    public String getDescription()
    {
        return description;
    }

    public Set<IPortType> getPorts()
    {
        final Set<IPortType> allPortTypes = new HashSet<IPortType>();
        allPortTypes.addAll(inputPortTypes);
        allPortTypes.addAll(outputPortTypes);

        return allPortTypes;
    }

    @Override
    public Set<IEventPortType> getEventPorts()
    {
        final Set<IEventPortType> allEventPortTypes = new HashSet<IEventPortType>();
        allEventPortTypes.addAll(eventListenerPortTypes);
        allEventPortTypes.addAll(eventTriggererPortTypes);

        return allEventPortTypes;
    }

    public Set<IInputPortType> getInputPorts()
    {
        return new HashSet<IInputPortType>(inputPortTypes);
    }

    public Set<IOutputPortType> getOutputPorts()
    {
        return new HashSet<IOutputPortType>(outputPortTypes);
    }

    @Override
    public DataType getPortDataType(String portID)
    {
        final IPortType port = getPort(portID);

        return port == null ? null : port.getDataType();
    }

    private IPortType getPort(final String portID)
    {
        for(IInputPortType inputPortType : inputPortTypes)
        {
            if(inputPortType.getPortID().equals(portID))
            {
                return inputPortType;
            }
        }

        for(IOutputPortType outputPortType : outputPortTypes)
        {
            if(outputPortType.getPortID().equals(portID))
            {
                return outputPortType;
            }
        }

        logger.severe(this.getClass().getName()+": getPort-> " +
        		"Could not find specified portType:"
        		+ portID );
        throw new IndexOutOfBoundsException("Could not find specified portType:"
        		+ portID);
    }

    public Set<IEventListenerPortType> getEventListenerPorts()
    {
        return new HashSet<IEventListenerPortType>(eventListenerPortTypes);
    }

    public Set<IEventTriggererPortType> getEventTriggererPorts()
    {
        return new HashSet<IEventTriggererPortType>(eventTriggererPortTypes);
    }

    @Override
    public String toString()
    {
        final StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("ID: " ).append(ID).append(", \n");
        stringBuffer.append("component type: ").append(componentType).append(", \n");
        stringBuffer.append("isSingleton: ").append(isSingleton).append(", \n");
        stringBuffer.append("inputPortTypes: ").append(inputPortTypes).append(", \n");
        stringBuffer.append("outputPortTypes: ").append(outputPortTypes).append(", \n");
        stringBuffer.append("eventListenerPortTypes: ").append(eventListenerPortTypes).append(", \n");
        stringBuffer.append("eventTriggererPortTypes: ").append(eventTriggererPortTypes).append("\n");

        return stringBuffer.toString();
    }
}