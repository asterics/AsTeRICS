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

/**
 * The bundle model can abstract any type of components:
 * <ul>
 * <li>sensor: used to generate output data;
 * <li>processor: used to process input data and generate output data;
 * <li>actuator: used to consume input data;
 * <li>special: used for special-purpose components such as signal generators,
 * oscilloscopes, etc.
 * </ul>
 *
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy] Date: Jul 14, 2010 Time:
 *         12:48:30 PM
 */
public enum ComponentType {
    SENSOR("sensor"), PROCESSOR("processor"), ACTUATOR("actuator"), SPECIAL("special"), GROUP("group");

    private final String type;

    private ComponentType(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}