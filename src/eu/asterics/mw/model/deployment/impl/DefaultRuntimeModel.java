package eu.asterics.mw.model.deployment.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import eu.asterics.mw.are.exceptions.AREAsapiException;
import eu.asterics.mw.model.DataType;
import eu.asterics.mw.model.deployment.IChannel;
import eu.asterics.mw.model.deployment.IComponentInstance;
import eu.asterics.mw.model.deployment.IInputPort;
import eu.asterics.mw.model.deployment.IOutputPort;
import eu.asterics.mw.model.deployment.IPort;
import eu.asterics.mw.model.deployment.IRuntimeModel;
import eu.asterics.mw.model.deployment.IEventChannel;
import eu.asterics.mw.services.AstericsErrorHandling;

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
 * @author Costas Kakpusis [kakousis@cs.ucy.ac.cy]
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 *         Date: Jul 15, 2010
 *         Time: 4:03:23 PM
 */
public class DefaultRuntimeModel implements IRuntimeModel
{
	private final Set<IChannel> channels;
	private final Set<IEventChannel> eventChannels;
	private final Set<IComponentInstance> components;
	private ModelState modelState;
	private Logger logger = null;
	private final String modelName, modelVersion;
	ArrayList<DefaultACSGroup> groups;
	ModelDescription modelDescription = null;
	ModelGUIInfo modelGuiInfo = null;
	
	public DefaultRuntimeModel(final String modelName, 
			final ModelDescription modelDescription, 
			final Set<IChannel> channels,
			final Set<IEventChannel> eventChannels,
			final Set<IComponentInstance> components,
			final String modelVersion, 
			ArrayList<DefaultACSGroup> groups,
			ModelGUIInfo modelGuiInfo)
	{
		this.modelName = modelName;
		this.modelDescription = modelDescription;
		this.channels = channels;
		this.eventChannels = eventChannels;
		this.components = components;
		this.modelVersion = modelVersion;
		this.modelState = ModelState.STOPPED;
		this.groups = groups;
		this.modelGuiInfo = modelGuiInfo;

		logger = AstericsErrorHandling.instance.getLogger();


	}


	public String getModelName ()
	{
		return this.modelName;
	}

	public String getModelVersion() {

		return this.modelVersion;
	}
	public String getModelDescription()
	{
		return this.modelDescription.getDescription();
	}

	public String getModelShortDescription()
	{
		return this.modelDescription.getShortDescription();
	}

	public String getModelRequirements()
	{
		return this.modelDescription.getRequirements();
	}

	public void setState(ModelState state)
	{
		if (state.equals(ModelState.STOPPED))
			modelState = ModelState.STOPPED;
		else if (state.equals(ModelState.STARTED))
			modelState = ModelState.STARTED;
		else if (state.equals(ModelState.PAUSED))
			modelState = ModelState.PAUSED;
	}

	public ModelState getState()
	{
		return this.modelState;
	}

	public Set<IChannel> getChannels()
	{
		return channels;
	}

	public Set<IComponentInstance> getComponentInstances()
	{
		return components;
	}

	public Set<IEventChannel> getEventChannels()
	{
		return eventChannels;
	}

	public IComponentInstance getComponentInstance(String componentInstanceID)
	{
		for (IComponentInstance ci : components)
		{
			if (ci.getInstanceID().equals(componentInstanceID))
			{
				return ci;
			}
		}
		return null;
	}


	public String[] getComponentInstancesIDs()
	{
		String[] instances = new String[components.size()];
		int count = 0;
		for (IComponentInstance ci : components)
		{
			ci.getInstanceID();
			instances[count++] = ci.getInstanceID();
		}
		return instances;
	}

	public String[] getChannelsIDs(String componentID)
	{
		ArrayList<String> channelIds = new ArrayList<String>();
		for (IChannel channel : channels)
		{
			if (channel.getSourceComponentInstanceID().compareTo(componentID) == 0)
				channelIds.add(channel.getChannelID());
			if (channel.getTargetComponentInstanceID().compareTo(componentID) == 0)
				channelIds.add(channel.getChannelID());
		}
		return (String[]) channelIds.toArray(new String[channelIds.size()]);
	}


	public void removeComponentInstance(String componentID) throws
	AREAsapiException
	{
		IComponentInstance ci = null;
		Iterator<IComponentInstance> itr =
				components.iterator();

		while (itr.hasNext())
		{
			ci = itr.next();
			if (ci.getInstanceID().equals(componentID))
			{

				itr.remove();
				return;
			}
		}

		logger.severe(this.getClass().getName()+"Couldn't find component " +
				"instance with ID:" + componentID);

		throw new AREAsapiException
		("Couldn't find component instance with ID:" + componentID);
	}

	public String[] getComponentInputPorts(String componentID)
			throws AREAsapiException
			{
		ArrayList<String> portIds = new ArrayList<String>();
		IComponentInstance instance = getComponentInstance(componentID);
		if (instance == null)
		{
			logger.severe(this.getClass().getName()+"Couldn't find component "+
					"instance with ID:" + componentID);
			throw new AREAsapiException("Couldn't find component instance with " +
					"ID:"+ componentID);
		} else
		{
			Set<IInputPort> inPorts = instance.getInputPorts();
			for (IInputPort inPort : inPorts)
			{
				portIds.add(inPort.getPortType());
			}
		}
		return (String[]) portIds.toArray(new String[portIds.size()]);
			}

	public String[] getComponentOutputPorts(String componentID)
			throws AREAsapiException
			{
		ArrayList<String> portIds = new ArrayList<String>();
		IComponentInstance instance = getComponentInstance(componentID);
		if (instance == null)
		{
			logger.severe(this.getClass().getName()+"Couldn't find component " +
					"instance with ID:" + componentID);
			throw new AREAsapiException("Couldn't find component instance with"+
					" ID:"+ componentID);
		} else
		{
			Set<IOutputPort> outPorts = instance.getOutputPorts();


			for (IOutputPort outPort : outPorts)
			{
				portIds.add(outPort.getPortType());
			}
		}
		return (String[]) portIds.toArray(new String[portIds.size()]);
			}

	public String[] getComponentPorts(String componentID) throws AREAsapiException
	{
		ArrayList<String> portIds = new ArrayList<String>();
		IComponentInstance instance = getComponentInstance(componentID);
		if (instance == null)
		{
			logger.severe(this.getClass().getName()+"Couldn't find component " +
					"instance with ID:" + componentID);
			throw new AREAsapiException("Couldn't find component instance with ID:"
					+ componentID);
		} else
		{
			Set<IPort> ports = instance.getPorts();
			for (IPort port : ports)
			{
				portIds.add(port.getPortType());
			}
		}
		return (String[]) portIds.toArray(new String[portIds.size()]);
	}


	public String getChannelProperty(String channelID, String key)
	{

		for (IChannel channel : channels)
		{
			if (channel.getChannelID().equals(channelID))
			{
				return channel.getPropertyValue(key).toString();
			}
		}
		return null;
	}

	public String[] getChannelPropertyKeys(String channelID)
	{
		for (IChannel channel : channels)
		{
			if (channel.getChannelID().equals(channelID))
			{
				return (String[]) channel.getPropertyKeys().toArray();
			}
		}
		return null;
	}

	public String getComponentProperty(String componentID, String key)
	{
		for (IComponentInstance component : components)
		{
			if (component.getInstanceID().equals(componentID))
			{
				return component.getPropertyValue(key).toString();
			}
		}
		return null;
	}

	public String[] getComponentPropertyKeys(String componentID)
	{
		Set<String> keys;
		for (IComponentInstance component : components)
		{
			if (component.getInstanceID().equals(componentID))
			{
				keys = component.getPropertyKeys();
				return (String[]) keys.toArray(new String[keys.size()]);

			}
		}
		return null;
	}

	public String getPortProperty(String componentID, String portID, String key)
	{
		for (IComponentInstance component : components)
		{
			if (component.getInstanceID().equals(componentID))
			{
				Set<IPort> ports = component.getPorts();
				for (IPort port : ports)
				{
					if (port.getPortType().equals(portID))
					{
						return port.getPropertyValue(key).toString();
					}
				}
			}
		}
		return null;
	}

	public String[] getPortPropertyKeys(String componentID, String portID)
	{
		Set<String> keys;
		for (IComponentInstance component : components)
		{
			if (component.getInstanceID().equals(componentID))
			{
				Set<IPort> ports = component.getPorts();
				for (IPort port : ports)
				{
					if (port.getPortType().equals(portID))
					{
						keys = port.getPropertyKeys();
						return (String[]) keys.toArray(new String[keys.size()]);

					}
				}
			}
		}
		return null;
	}


	public String setPortProperty(String componentID, String portID,
			String key, String value)
	{
		String oldValue = "";
		for (IComponentInstance component : components)
		{
			if (component.getInstanceID().equals(componentID))
			{
				Set<IPort> ports = component.getPorts();
				for (IPort port : ports)
				{
					if (port.getPortType().equals(portID))
					{
						oldValue = port.getPropertyValue(key).toString();
						port.getPropertyValues().put(key, value);
						return oldValue;
					}
				}
			}
		}
		return null;
	}

	public String setComponentProperty(String componentID, String key,
			String value)
	{
		String oldValue = "";
		for (IComponentInstance component : components)
		{
			if (component.getInstanceID().equals(componentID))
			{
				if (component.getPropertyValue(key)!=null)
				oldValue = component.getPropertyValue(key).toString();
				else
				oldValue="";
				component.getPropertyValues().put(key, value);
				return oldValue;
			}
		}

		return null;
	}


	public String setChannelProperty(String channelID, String key, String value)
	{
		String oldValue = "";
		for (IChannel channel : channels)
		{
			if (channel.getChannelID().equals(channelID))
			{
				oldValue = channel.getPropertyValue(key).toString();
				channel.getPropertyValues().put(key, value);
				return oldValue;
			}
		}

		return null;
	}

	public void removeChannel(String channelID) throws AREAsapiException
	{
		IChannel ch = null;
		Iterator<IChannel> itr =
				channels.iterator();

		while (itr.hasNext())
		{
			ch = itr.next();
			if (ch.getChannelID().equals(channelID))
			{
				itr.remove();
			}
		}
		logger.severe(this.getClass().getName()+"Couldn't find component " +
				"instance with ID:" + channelID);
		throw new AREAsapiException
		("Couldn't find component instance with ID:" + channelID);
	}


	public void insertComponent(DefaultComponentInstance newInstance)
	{
		this.components.add(newInstance);

	}

	public void insertChannel(DefaultChannel newChannel)
	{
		this.channels.add(newChannel);

	}

	@Override
	public IPort getPort(String componentID, String portID)
	{

		for (IComponentInstance component : components)
		{
			if (component.getInstanceID().equals(componentID))
			{

				Set<IPort> ports = component.getPorts();

				for (IPort port : ports)
				{

					if (port.getPortType().equals(portID))
					{
						return port;
					}
				}
			}
		}

		return null;
	}

	@Override
	public DataType getPortDataType(String componentID, String portID)
	{
		final IPort port = getPort(componentID, portID);

		return port == null ? null : port.getPortDataType();
	}


	@Override
	public ArrayList<DefaultACSGroup> getACSGroups() {
		if (this.groups == null)
			this.groups = new ArrayList<DefaultACSGroup>();
		return groups;
	}

	@Override
	public ModelGUIInfo getModelGuiInfo()
	{
		return modelGuiInfo;
	}
	



}