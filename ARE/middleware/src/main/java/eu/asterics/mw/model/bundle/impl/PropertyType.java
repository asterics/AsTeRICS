package eu.asterics.mw.model.bundle.impl;

import eu.asterics.mw.model.DataType;
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
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy] Kakousis Konstantinos
 *         Date: Jul 15, 2010 Time: 4:51:39 PM
 */
public class PropertyType {
    private final String name;
    private final DataType dataType;
    private final String description;
    private final String value;
    private final String combobox;
    private final boolean getStringList;

    public PropertyType(final String name, final DataType dataType, final String description, final String value,
            final String combobox, final boolean getStringList) {
        this.name = name;
        this.dataType = dataType;
        this.description = description;
        this.value = value;
        this.combobox = combobox;
        this.getStringList = getStringList;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public DataType getDataType() {
        return dataType;
    }

    public String getDescription() {
        return description;
    }

    public String getCombobox() {
        return combobox;
    }

    public boolean isGetStringList() {
        return getStringList;
    }

}