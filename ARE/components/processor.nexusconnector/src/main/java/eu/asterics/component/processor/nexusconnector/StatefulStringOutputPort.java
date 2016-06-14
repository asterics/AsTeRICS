/*
Copyright 2016 OCAD University

Licensed under the New BSD license.
*/

package eu.asterics.component.processor.nexusconnector;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;

public class StatefulStringOutputPort {

    private String value = null;
    private final IRuntimeOutputPort port = new DefaultRuntimeOutputPort();

    public IRuntimeOutputPort getPort() {
        return port;
    }

    public void update(String newValue) {
        if (value == null || !value.equals(newValue)) {
            value = newValue;
            port.sendData(ConversionUtils.stringToBytes(value));
        }
    }

}
