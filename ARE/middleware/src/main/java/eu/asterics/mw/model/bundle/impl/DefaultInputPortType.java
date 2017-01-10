package eu.asterics.mw.model.bundle.impl;

import java.util.Map;

import eu.asterics.mw.model.DataType;
import eu.asterics.mw.model.Multiplicity;
import eu.asterics.mw.model.bundle.IInputPortType;
import eu.asterics.mw.model.bundle.PortType;

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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */
/**
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy] Date: Jul 15, 2010 Time:
 *         4:54:10 PM
 */
public class DefaultInputPortType extends DefaultPortType implements IInputPortType {
    private final Multiplicity multiplicity;
    private final boolean mustBeConnected;
    // private final ArrayList<String> bufferedPortIds; //Sync

    public DefaultInputPortType(final PortType portType, final String description, final DataType dataType,
            final Map<String, PropertyType> propertyTypes, final Multiplicity multiplicity,
            final boolean mustBeConnected, String id) {
        super(portType, description, dataType, propertyTypes, id);

        this.multiplicity = multiplicity;
        this.mustBeConnected = mustBeConnected;
        // this.bufferedPortIds = bufferedPortIds;

    }

    @Override
    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

    @Override
    public boolean mustBeConnected() {
        return mustBeConnected;
    }

    @Override
    final public PortType getType() {
        return PortType.INPUT;
    }

    // Sync
    // public ArrayList<String> getBufferedPortIds() {
    // return this.bufferedPortIds;
    // }
}