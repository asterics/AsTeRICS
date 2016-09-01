/*
Copyright 2016 OCAD University

Licensed under the MIT license.
*/

package eu.asterics.component.processor.nexusconnector;

import javax.websocket.MessageHandler;

public class NexusConnectorMessageHandler implements MessageHandler.Whole<String> {

    private NexusConnectorInstance connectorInstance;

    public NexusConnectorMessageHandler(NexusConnectorInstance connectorInstance) {
        this.connectorInstance = connectorInstance;
    }

    @Override
    public void onMessage(String message) {
        connectorInstance.onNexusMessage(message);
    }

}
