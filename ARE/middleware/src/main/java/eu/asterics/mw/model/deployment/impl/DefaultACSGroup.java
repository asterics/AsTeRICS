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

import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DefaultACSGroup {



	private String description;
	private ArrayList<String> componentIds;
	private ArrayList<DefaultPortAlias> portAlias;
	private String id;

	public DefaultACSGroup (String description, ArrayList<String> componentIds, 
			ArrayList<DefaultPortAlias> portAlias, String id)
	{
		this.description = description;
		this.componentIds = componentIds;
		this.portAlias = portAlias;
		this.id = id;
	}

	public String getId()
	{
		return this.id;
	}

	public ArrayList<DefaultPortAlias> getPortAlias()
	{
		return this.portAlias;
	}

	public ArrayList<String> getComponentIds ()
	{
		return this.componentIds;
	}

	public String getDescription ()
	{
		return this.description;
	}

	public void appendXMLElements(Document doc) 
	{

		Element group = doc.createElement("group");
		Element groups = (Element) doc.getElementsByTagName("groups").item(0);
		groups.appendChild(group);
		group.setAttribute("id", this.getId());

		if (this.getDescription()!="")
		{
			Element description = doc.createElement("description");
			group.appendChild(description);
			description.setTextContent(this.description);
		}

		//Check for component ids
		if (this.getComponentIds().size()>0)
		{
			Iterator<String> itr = this.getComponentIds().iterator();
			String cid="";
			while(itr.hasNext())
			{
				cid = itr.next();
				Element cidEl = doc.createElement("componentId");
				group.appendChild(cidEl);
				cidEl.setTextContent(cid);

			}
		}

		//Check for port aliases
		if (this.getPortAlias().size()>0)
		{
			Iterator<DefaultPortAlias> itr = this.getPortAlias().iterator();
			DefaultPortAlias alias = null;
			while(itr.hasNext())
			{
				alias = itr.next();
				Element aliasEl = doc.createElement("portAlias");
				group.appendChild(aliasEl);
				aliasEl.setAttribute("portId", alias.getPortId());
				aliasEl.setAttribute("portAlias", alias.getPortAlias());

			}
		}

	}
}
