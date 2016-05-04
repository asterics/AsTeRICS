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

package eu.asterics.mw.model.bundle;

import eu.asterics.mw.model.DataType;

import java.util.Set;

/**
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 *         Date: Jul 14, 2010
 *         Time: 12:28:47 PM
 */
public interface IComponentType extends IPropertyfulType
{
    public String getID();

    public String getCanonicalName();

    public ComponentType getType();

    /**
     * Returns true if and only if the corresponding component type is a
     * singleton. A singleton componentType maintains a single instance only,
     * which is always returned whenever a new instance is requested. In
     * contrast, a non-singleton component type generates a new instance of the
     * component whenever a new instance is requested.
     *
     * @return true if and only if the corresponding component type is a
     * singleton
     */
    public boolean isSingleton();

    /**
     * Returns a human-readable description of the component type.
     *
     * @return a string containing a human-readable description of the
     *         component type
     */
    public String getDescription();

    public Set<IPortType> getPorts();

    public Set<IInputPortType> getInputPorts();

    public Set<IOutputPortType> getOutputPorts();

    public DataType getPortDataType(final String portID);

    public Set<IEventPortType> getEventPorts();

    public Set<IEventListenerPortType> getEventListenerPorts();

    public Set<IEventTriggererPortType> getEventTriggererPorts();
}
