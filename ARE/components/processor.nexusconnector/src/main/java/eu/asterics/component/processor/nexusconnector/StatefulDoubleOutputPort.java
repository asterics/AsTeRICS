/*
Copyright 2016 OCAD University

Licensed under the New BSD license.
*/

package eu.asterics.component.processor.nexusconnector;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;

public class StatefulDoubleOutputPort {

    private double value = 0;
    private final IRuntimeOutputPort port = new DefaultRuntimeOutputPort();

    public IRuntimeOutputPort getPort() {
        return port;
    }

    public void update(double newValue) {
        if (value != newValue) {
            value = newValue;
            port.sendData(ConversionUtils.doubleToBytes(value));
        }
    }

}
