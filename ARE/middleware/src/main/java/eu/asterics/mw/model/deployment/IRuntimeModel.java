package eu.asterics.mw.model.deployment;

import java.util.ArrayList;
import java.util.Set;

import eu.asterics.mw.are.exceptions.AREAsapiException;
import eu.asterics.mw.model.DataType;
import eu.asterics.mw.model.deployment.impl.DefaultACSGroup;
import eu.asterics.mw.model.deployment.impl.DefaultChannel;
import eu.asterics.mw.model.deployment.impl.DefaultComponentInstance;
import eu.asterics.mw.model.deployment.impl.ModelGUIInfo;
import eu.asterics.mw.model.deployment.impl.ModelState;

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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 *
 */
/**
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 * @author Costas Kakousis [kakousis@cs.ucy.ac.cy] Date: Jul 14, 2010 Time:
 *         2:24:52 PM
 */
public interface IRuntimeModel {
    public Set<IComponentInstance> getComponentInstances();

    public Set<IChannel> getChannels();

    public IComponentInstance getComponentInstance(String componentInstanceID);

    public String[] getComponentInstancesIDs();

    public String[] getChannelsIDs(String componentID);

    public void removeComponentInstance(String componentID) throws AREAsapiException;

    public String[] getComponentPorts(String componentID) throws AREAsapiException;

    public String[] getComponentInputPorts(String componentID) throws AREAsapiException;

    public String[] getComponentOutputPorts(String componentID) throws AREAsapiException;

    public String getChannelProperty(String channelID, String key);

    public String[] getChannelPropertyKeys(String channelID);

    public String getComponentProperty(String componentID, String key);

    public String[] getComponentPropertyKeys(String componentID);

    public String getPortProperty(String componentID, String portID, String key);

    public String[] getPortPropertyKeys(String componentID, String portID);

    public String setPortProperty(String componentID, String portID, String key, String value);

    public String setComponentProperty(String componentID, String key, String value);

    public String setChannelProperty(String channelID, String key, String value);

    public void removeChannel(String channelID) throws AREAsapiException;

    public void insertComponent(DefaultComponentInstance newInstance);

    public void insertChannel(DefaultChannel newChannel);

    public IPort getPort(String componentID, String portID);

    public DataType getPortDataType(final String componentID, final String portID);

    public Set<IEventChannel> getEventChannels();

    public ModelState getState();

    public void setState(ModelState state);

    public String getModelName();

    public String getModelDescription();

    public String getModelVersion();

    public String getModelShortDescription();

    public String getModelRequirements();

    public ArrayList<DefaultACSGroup> getACSGroups();

    public ModelGUIInfo getModelGuiInfo();

}