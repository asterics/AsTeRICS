package eu.asterics.component.processor.oska;

public class OskaActionStringSendTCPMessageHandler implements
		IOskaActionStringHandler {

	@Override
	public void handleActionString(String action) 
	{
		int index = action.indexOf(':');
		if (index >= 0)
		{
			OskaInstance.instance.outputs.opAction
				.sendData(action.substring(index + 1).getBytes());
		}
	}

}
