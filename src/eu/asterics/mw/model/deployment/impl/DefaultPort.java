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

package eu.asterics.mw.model.deployment.impl;

import java.util.Map;

import eu.asterics.mw.model.DataType;
import eu.asterics.mw.model.deployment.IPort;

/**
 * @author Costas Kakpusis [kakousis@cs.ucy.ac.cy]
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 *         Date: Jul 15, 2010
 *         Time: 4:03:23 PM
 */
public class DefaultPort extends DefaultPropertyful implements IPort
{
    final String portType;
    final DataType portDataType;

    protected DefaultPort(final String portType,
                          final DataType portDataType,
                          final Map<String, Object> propertyValues)
    {
        super(propertyValues);
        this.portType = portType;
        this.portDataType = portDataType;
    }

    public String getPortType()
    {
        return portType;
    }

    @Override
    public DataType getPortDataType()
    {
        return portDataType;
    }

    @Override
    public String toString()
    {
        return "DefaultPort(" + portType + ", " + portDataType + ")";
    }
}