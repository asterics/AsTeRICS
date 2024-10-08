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

package eu.asterics.mw.model.deployment.impl;

import eu.asterics.mw.model.deployment.IBindingEdge;

/**
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy] Date: Sep 2, 2010 Time:
 *         5:06:53 PM
 */
public class DefaultBindingEdge implements IBindingEdge {
    private final String componentInstanceID;
    private final String portID;

    public DefaultBindingEdge(final String componentInstanceID, final String portID) {
        super();

        this.componentInstanceID = componentInstanceID;
        this.portID = portID;
    }

    @Override
    public String getComponentInstanceID() {
        return componentInstanceID;
    }

    @Override
    public String getPortID() {
        return portID;
    }

    @Override
    public String toString() {
        return "DefaultBindingEdge(" + componentInstanceID + ":" + portID + ")";
    }
}