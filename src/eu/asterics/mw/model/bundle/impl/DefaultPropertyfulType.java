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

import eu.asterics.mw.model.DataType;
import eu.asterics.mw.model.bundle.IPropertyfulType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 *         Date: Jul 15, 2010
 *         Time: 4:46:06 PM
 */
public class DefaultPropertyfulType implements IPropertyfulType
{
    private final Map<String, PropertyType> propertyTypes;

    protected DefaultPropertyfulType(final Map<String, PropertyType> propertyTypes)
    {
        this.propertyTypes = propertyTypes;
    }

    public Set<String> getPropertyNames()
    {
    	if ( this.propertyTypes == null)
    		return null;
    	else
        return new HashSet<String>(propertyTypes.keySet());
    }

    public DataType getDataType(String propertyName)
    {
        final PropertyType propertyType = propertyTypes.get(propertyName);

        return propertyType == null ? null : propertyType.getDataType();
    }

    public String getDescription(String propertyName)
    {
        final PropertyType propertyType = propertyTypes.get(propertyName);

        return propertyType == null ? null : propertyType.getDescription();
    }
}