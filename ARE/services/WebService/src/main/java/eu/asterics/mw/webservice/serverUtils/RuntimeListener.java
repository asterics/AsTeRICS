package eu.asterics.mw.webservice.serverUtils;

import eu.asterics.mw.services.RuntimeDataListener;
import eu.asterics.mw.webservice.SseResource;

public class RuntimeListener extends RuntimeDataListener {

	@Override
	public void componentPropertyChanged(String componentId, String componentKey, String newValue) {
		SseResource.broadcastPropertyChangedEvent(componentId, componentKey, newValue);
	}

	
	@Override
	public void eventChannelTransmission(String channelId, String targetComponentId) {
		SseResource.broadcastEventChannelEvent(channelId, targetComponentId);
	}

	
	@Override
	public void dataChannelTransmission(String channelId, String channelData) {
		SseResource.broadcastDataChannelEvent(channelId, channelData);
	}

}
