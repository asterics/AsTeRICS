package eu.asterics.mw.services;

/**
 * Holds the information that will be passed to a {@link RuntimeDataListener}.
 * This class has a private constructor, hence to create an instance of this
 * class you should use the available static methods (starting with 'new...')
 * 
 * @author Marios Komodromos
 *
 */
public class RuntimeDataEvent {
	public static final String TYPE_EVENT_CHANNEL = "type_event_channel";
	public static final String TYPE_DATA_CHANNEL = "type_data_channel";
	public static final String TYPE_COMPONENT_PROPERTY_CHANGE = "type_property_change";
	
	private String eventType;
	private String channelId;
	private String data;
	private String componentId;
	private String componentKey;
	
	
	/**
	 * Initializes the object
	 */
	private RuntimeDataEvent() {
		this.eventType = "";
		this.channelId = "";
		this.data = "";
		this.componentId = "";
		this.componentKey = "";
	}
	
	
	/**
	 * Creates a RuntimeDataEvent with type {@link RuntimeDataEvent#TYPE_EVENT_CHANNEL}
	 * 
	 * @param channelId - the id of the channel
	 * @param targetComponentId - the id of the target component
	 */
	public static RuntimeDataEvent newEventChannelTransmission(String channelId, String targetComponentId) {
		RuntimeDataEvent runtimeDataEvent = new RuntimeDataEvent();
		
		runtimeDataEvent.eventType = RuntimeDataEvent.TYPE_EVENT_CHANNEL;
		runtimeDataEvent.channelId = channelId;
		runtimeDataEvent.componentId = targetComponentId;
		
		return runtimeDataEvent;
	}
	
	/**
	 * Creates a RuntimeDataEvent with type {@link RuntimeDataEvent#TYPE_DATA_CHANNEL}
	 * 
	 * @param channelId - the id of the channel
	 * @param data - the actual data of the channel
	 */
	public static RuntimeDataEvent newDataChannelTransmission(String channelId, String data) {
		RuntimeDataEvent runtimeDataEvent = new RuntimeDataEvent();
		
		runtimeDataEvent.eventType = RuntimeDataEvent.TYPE_DATA_CHANNEL;
		runtimeDataEvent.channelId = channelId;
		runtimeDataEvent.data = data;
		
		return runtimeDataEvent;
	}
	
	/**
	 * Creates a RuntimeDataEvent with type {@link RuntimeDataEvent#TYPE_COMPONENT_PROPERTY_CHANGE}
	 * 
	 * @param componentId - the id of the modified component
	 * @param componentKey - the key of the modified property of the component
	 * @param data - the new value of the property
	 * 
	 * @return - the {@link RuntimeDataEvent} object initialized with the given parameters
	 */
	public static RuntimeDataEvent newComponentPropertyChange(String componentId, String componentKey, String data) {
		RuntimeDataEvent runtimeDataEvent = new RuntimeDataEvent();
		
		runtimeDataEvent.eventType = RuntimeDataEvent.TYPE_COMPONENT_PROPERTY_CHANGE;
		runtimeDataEvent.componentId = componentId;
		runtimeDataEvent.componentKey = componentKey;
		runtimeDataEvent.data = data;
		
		return runtimeDataEvent;
	}

	
	
	/* GETTERS */
	
	public String getType() {
		return eventType;
	}

	public String getChannelId() {
		return channelId;
	}

	public String getData() {
		return data;
	}

	public String getComponentId() {
		return componentId;
	}

	public String getComponentKey() {
		return componentKey;
	}
	
}
