/*
Copyright 2016 OCAD University

Licensed under the MIT license.
*/

package eu.asterics.component.processor.nexusconnector;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;

public class NexusConnectorInputPort extends DefaultRuntimeInputPort {

    public enum InputType {
        DOUBLE, STRING;
    }

    private NexusConnectorInstance connectorInstance;
    private String path;
    private InputType type;

    public NexusConnectorInputPort(NexusConnectorInstance connectorInstance, String path, InputType type) {
        this.connectorInstance = connectorInstance;
        this.path = path;
        this.type = type;
    }

    @Override
    public void receiveData(byte[] data) {
        switch (type) {
        case DOUBLE:
            double doubleVal = ConversionUtils.doubleFromBytes(data);
            connectorInstance.sendNexusChangeMessage(path, doubleVal);
            break;
        case STRING:
            String stringVal = ConversionUtils.stringFromBytes(data);
            connectorInstance.sendNexusChangeMessage(path, stringVal);
            break;
        }
    }

}
