package eu.asterics.mw.services;

import java.util.HashSet;
import java.util.Set;

import eu.asterics.mw.model.deployment.IChannel;
import eu.asterics.mw.model.deployment.IEventChannel;

/**
 * This abstract class should be implemented by anyone that desires
 * to receive runtime data of the active AsTeRICS model.
 * 
 * @author Marios Komodromos
 *
 */
public abstract class RuntimeDataListener {
	private Set<String> openedDataChannels;
	
	public RuntimeDataListener() {
		this.openedDataChannels = new HashSet<String>();
	}
	
	/**
	 * This method will be triggered when a property change was made.
	 * 
	 * @param componentId - the component that hold the changed property
	 * @param componentKey - the key of the changed property
	 * @param newValue - the new value of the property
	 */
	public abstract void componentPropertyChanged(String componentId, String componentKey, String newValue);
	
	
	/**
	 * This method will be triggered when an event is transmitted through an {@link IEventChannel}.
	 * 
	 * @param channelId - the id of the eventChannel
	 * @param targetComponentId - the id of the target component
	 */
	public abstract void eventChannelTransmission(String channelId, String targetComponentId);
	
	
	/**
	 * This method will be triggered when data are transmitted through an {@link IChannel}.
	 * Due to the fact that data channels could transmit a significant amount of data,
	 * a listener is able to choose what specific channels will be monitoring with the use of
 	 * {@link RuntimeDataListener#openedDataChannels} set.
	 * 
	 * @param channelId - the id of the eventChannel
	 * @param channelData - the actual data
	 */
	public abstract void dataChannelTransmission(String channelId, String channelData);
	
	
	
	
	/**
	 *  {@link RuntimeDataListener#openedDataChannels} methods
	 */
	
	/**
	 * Retrieve a set of {@link IChannel} objects that are opened for monitoring
	 * @return - a set of {@link IChannel} that is currenty monitored for this listener
	 */
	public Set<String> getOpenedDataChannels() {
		return this.openedDataChannels;
	}
	
	/**
	 * Set the set of {@link IChannel} objects that will be monitored
	 * @param dataChannelIds - the set of {@link IChannel} to monitor
	 */
	public void setOpenedDataChannels(Set<String> dataChannelIds) {
		this.openedDataChannels = dataChannelIds;
	}
	
	/**
	 * Open a specific {@link IChannel} for monitoring
	 * 
	 * @param dataChannelId - the {@link IChannel} id to be added to the {@link IChannel} that are monitored
	 */
	public void openDataChannel(String dataChannelId) {
		this.openedDataChannels.add(dataChannelId);
	}
	
	/**
	 * Stops monitoring the {@link IChannel} with the given id
	 * @param dataChannelId - the if of the {@link IChannel} that will not be monitored
	 * @return
	 */
	public boolean closeDataChannel(String dataChannelId) {
		return this.openedDataChannels.remove(dataChannelId);
	}
	
	/**
	 * Stops monitoring all the channels for this listener
	 */
	public void clearDataChannelList() {
		this.openedDataChannels.clear();
	}
	
	
}
