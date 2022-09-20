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

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.asterics.mw.model.deployment.IBindingEdge;
import eu.asterics.mw.model.deployment.IChannel;

/**
 * @author Costas Kakousis [kakousis@cs.ucy.ac.cy]
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy] Date: Aug 1, 2010 Time:
 *         4:03:23 PM
 */
public class DefaultChannel extends DefaultPropertyful implements IChannel {
    private final String sourceComponentID;
    private final String sourceComponentPortID;
    private final String targetComponentID;
    private final String targetComponentPortID;
    private final String description;
    private final String id;
    private final IBindingEdge sourceBindingEdge;
    private final IBindingEdge targetBindingEdge;

    public DefaultChannel(final String description, final String sourceComponentID, final String sourceComponentPortID,
            final String targetComponentID, final String targetComponentPortID, final String id,
            final Map<String, Object> propertyValues) {
        super(propertyValues);

        this.description = description;
        this.sourceComponentID = sourceComponentID;
        this.sourceComponentPortID = sourceComponentPortID;
        this.targetComponentID = targetComponentID;
        this.targetComponentPortID = targetComponentPortID;
        this.id = id;

        this.sourceBindingEdge = new DefaultBindingEdge(sourceComponentID, sourceComponentPortID);
        this.targetBindingEdge = new DefaultBindingEdge(targetComponentID, targetComponentPortID);
    }

    @Override
    public IBindingEdge getSource() {
        return sourceBindingEdge;
    }

    @Override
    public IBindingEdge getTarget() {
        return targetBindingEdge;
    }

    @Override
    public String getSourceComponentInstanceID() {
        return sourceComponentID;
    }

    @Override
    public String getSourceOutputPortID() {
        return sourceComponentPortID;
    }

    @Override
    public String getTargetComponentInstanceID() {
        return targetComponentID;
    }

    @Override
    public String getTargetInputPortID() {
        return targetComponentPortID;
    }

    @Override
    public String getChannelDescription() {
        return description;
    }

    @Override
    public String getChannelID() {

        return this.id;
    }

    @Override
    public void appendXMLElements(Document doc) {

        Element channel = doc.createElement("channel");
        Element channels = (Element) doc.getElementsByTagName("channels").item(0);
        channels.appendChild(channel);
        channel.setAttribute("id", this.id);
        if (this.description != "") {
            Element description = doc.createElement("description");
            channel.appendChild(description);
            description.setTextContent(this.description);
        }
        // Create source edge
        Element sourceElement = doc.createElement("source");
        channel.appendChild(sourceElement);
        Element sourceComponentElement = doc.createElement("component");
        sourceElement.appendChild(sourceComponentElement);
        sourceComponentElement.setAttribute("id", this.sourceComponentID);

        Element sourceComponentPortElemnt = doc.createElement("port");
        sourceElement.appendChild(sourceComponentPortElemnt);
        sourceComponentPortElemnt.setAttribute("id", this.sourceComponentPortID);

        // Create target edge
        Element targetElement = doc.createElement("target");
        channel.appendChild(targetElement);
        Element targetComponentElement = doc.createElement("component");
        targetElement.appendChild(targetComponentElement);
        targetComponentElement.setAttribute("id", this.targetComponentID);

        Element targetComponentPortElemnt = doc.createElement("port");
        targetElement.appendChild(targetComponentPortElemnt);
        targetComponentPortElemnt.setAttribute("id", this.targetComponentPortID);

    }

}
