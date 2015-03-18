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

package eu.asterics.mw.model.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 *         Date: Aug 20, 2010
 *         Time: 11:29:06 AM
 */
abstract public class AbstractRuntimeComponentInstance implements IRuntimeComponentInstance
{
    public AbstractRuntimeComponentInstance()
    {
        super();
    }

    protected RuntimeState runtimeState = RuntimeState.READY;

    public void start()
    {
        runtimeState = RuntimeState.ACTIVE;
    }

    public void pause()
    {
        runtimeState = RuntimeState.SUSPENDED;
    }

    public void resume()
    {
        runtimeState = RuntimeState.ACTIVE;
    }

    public void stop()
    {
        runtimeState = RuntimeState.STOPPED;
    }

    @Override
    public IRuntimeInputPort getInputPort(String portID)
    {
        return null;
    }

    @Override
    public IRuntimeOutputPort getOutputPort(String portID)
    {
        return null;
    }

    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
        return null;
    }

    @Override
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
        return null;
    }
    
    public Object getRuntimePropertyValue(String propertyName)
    {
    	return null;
    }

    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
    	return null;
    }
    
    public List<String> getRuntimePropertyList(String key){
    	return new ArrayList<String>();
    }
    
    public void syncedValuesReceived (HashMap <String, byte[]> dataRow)
    {
    	
		for (Map.Entry<String, byte[]> e: dataRow.entrySet())
		{
			IRuntimeInputPort p = getInputPort(e.getKey());
			if (p != null)
			{
				p.receiveData(e.getValue());
			}
		}
     }
}