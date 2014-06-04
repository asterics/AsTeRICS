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

import eu.asterics.mw.model.deployment.IBindingEdge;
import eu.asterics.mw.model.deployment.IEventChannel;
import eu.asterics.mw.model.deployment.IEventEdge;

import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 *         Date: Sep 2, 2010
 *         Time: 5:10:20 PM
 */
public class DefaultEventChannel implements IEventChannel
{
    private final Set<IEventEdge> sources = new LinkedHashSet<IEventEdge>();
    private final Set<IEventEdge> targets = new LinkedHashSet<IEventEdge>();

    private String id, description;

    public DefaultEventChannel(final IEventEdge [] sources,
            final IEventEdge [] targets,
            final String id,
            final String description)
    {
        super();

        this.sources.addAll(Arrays.asList(sources));
        this.targets.addAll(Arrays.asList(targets));
        this.id = id;
        this.description = description;
        
    }

    public IEventEdge[] getSources()
    {
        return sources.toArray(new IEventEdge[sources.size()]);
    }

    public IEventEdge[] getTargets()
    {
        return targets.toArray(new IEventEdge[targets.size()]);
    }
    
    public String getChannelID (){
        return this.id;
    }
    
    public void appendXMLElements(Document doc) {

		Element channel = doc.createElement("eventChannel");
		Element channels = (Element) doc.getElementsByTagName("eventChannels").item(0);
		channels.appendChild(channel);
		channel.setAttribute("id", this.id);
		if (this.description!="")
		{
			Element description = doc.createElement("description");
			channel.appendChild(description);
			description.setTextContent(this.description);
		}
		//Create sources
		Element sourcesElement = doc.createElement("sources");
		channel.appendChild(sourcesElement);
		//Create source edges
		//for (int i=0; i<sources.size(); i++)
		//{
			Iterator <IEventEdge>itr = sources.iterator();
			
			while (itr.hasNext())
			{
				IEventEdge source =  itr.next();
				
				Element sourceElement = doc.createElement("source");
				sourcesElement.appendChild(sourceElement);
				if (source.getComponentInstanceID()!="")
				{
					Element sourceComponentElement = doc.createElement("component");
					sourceElement.appendChild(sourceComponentElement);
					sourceComponentElement.setAttribute("id", source.getComponentInstanceID());
				}
				if (source.getEventPortID()!="")
				{
					Element sourceEventPortElement = doc.createElement("eventPort");
					sourceElement.appendChild(sourceEventPortElement);
					sourceEventPortElement.setAttribute("id", source.getEventPortID());
				}
			}
		//}
		//Create targets
		Element targetsElement = doc.createElement("targets");
		channel.appendChild(targetsElement);
//		//Create target edges
		//for (int i=0; i<targets.size(); i++)
		//{
			itr = targets.iterator();
			while (itr.hasNext())
			{
				IEventEdge target =  itr.next();
				Element targetElement = doc.createElement("target");
				targetsElement.appendChild(targetElement);
				if (target.getComponentInstanceID()!="")
				{
					Element targetComponentElement = doc.createElement("component");
					targetElement.appendChild(targetComponentElement);
					targetComponentElement.setAttribute("id", target.getComponentInstanceID());
				}
				if (target.getEventPortID()!="")
				{
					Element targetEventPortElement = doc.createElement("eventPort");
					targetElement.appendChild(targetEventPortElement);
					targetEventPortElement.setAttribute("id", target.getEventPortID());
				}
			}
		//}
	}

    @Override
    public String toString()
    {
        final StringBuilder stringBuilder = new StringBuilder("DefaultEventChannel([");
        for(final IEventEdge source : sources)
        {
            stringBuilder.append(source).append(" ");
        }
        stringBuilder.append("] --> [");
        for(final IEventEdge target : targets)
        {
            stringBuilder.append(target).append(" ");
        }
        stringBuilder.append("])");

        return stringBuilder.toString();
    }
    
}
