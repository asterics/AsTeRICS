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

import java.awt.Dimension;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.List;

/**
 * Specifies the methods for lifecycle support of AsTeRICS components.
 *
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 *         Date: Aug 19, 2010
 *         Time: 8:34:50 PM
 */
public interface IRuntimeComponentInstance
{
    // ------------------ Lifecycle support methods ------------------------- //

    public void start();

    public void pause();

    public void resume();

    public void stop();

    // -------------- End of lifecycle support methods ---------------------- //

    // ------------------ Component support methods ------------------------- //

    public IRuntimeInputPort getInputPort(final String portID);

    public IRuntimeOutputPort getOutputPort(final String portID);

    public IRuntimeEventListenerPort getEventListenerPort(final String eventPortID);

    public IRuntimeEventTriggererPort getEventTriggererPort(final String eventPortID);

    public Object getRuntimePropertyValue(String propertyName);
    
	public List<String> getRuntimePropertyList(String key);

    public Object setRuntimePropertyValue(String propertyName, Object newValue);

    // -------------- End of component support methods ---------------------- //
    
    public void syncedValuesReceived (HashMap <String, byte[]> dataRow);

    
}