
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
 *    This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package com.starlab.component.processor.derivative;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Implements the derivative of the input signal
 * 
 * @author Javier Acedo [javier.acedo@starlab.es] Date: May 1, 2011 Time
 *         08:28:57 PM
 */
public class DerivativeInstance extends AbstractRuntimeComponentInstance {

    private InputPort ipInputPort = new InputPort();
    private OutputPort opOutputPort = new OutputPort();

    private Derivative derivative = new Derivative();

    /**
     * The class constructor
     */
    public DerivativeInstance() {
        //
    }

    /**
     * called when model is started
     */
    @Override
    public void start() {
        derivative.reset();
        super.start();
    }

    /**
     * called when model is paused
     */
    @Override
    public void pause() {
        super.pause();
    }

    /**
     * called when model is resumed
     */
    @Override
    public void resume() {
        super.resume();
    }

    /**
     * called when model is stopped
     */
    @Override
    public void stop() {
        super.stop();
    }

    /**
     * returns an Input Port.
     * 
     * @param portID
     *            the name of the port
     * @return the input port or null if not found
     */
    @Override
    public IRuntimeInputPort getInputPort(String portID) {
        if ("input".equalsIgnoreCase(portID)) {
            return ipInputPort;
        }

        return null;
    }

    /**
     * returns an Output Port.
     * 
     * @param portID
     *            the name of the port
     * @return the output port or null if not found
     */
    @Override
    public IRuntimeOutputPort getOutputPort(String portID) {
        if ("output".equalsIgnoreCase(portID)) {
            return opOutputPort;
        }

        return null;
    }

    /**
     * returns the value of the given property.
     * 
     * @param propertyName
     *            the name of the property
     * @return the property value or null if not found
     */
    @Override
    public Object getRuntimePropertyValue(String propertyName) {
        if ("SampleFrequency".equalsIgnoreCase(propertyName)) {
            return derivative.getSampleFrequency();
        }
        return null;
    }

    /**
     * sets a new value for the given property.
     * 
     * @param propertyName
     *            the name of the property
     * @param newValue
     *            the desired property value or null if not found
     */
    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        if ("SampleFrequency".equalsIgnoreCase(propertyName)) {
            final Object oldValue = derivative.getSampleFrequency();

            if (newValue != null) {
                try {
                    int newSampleFrequency = Integer.parseInt(newValue.toString());
                    if (newSampleFrequency > 0) {
                        derivative.setSampleFrequency(newSampleFrequency);
                    } else {
                        AstericsErrorHandling.instance.reportInfo(this,
                                "Invalid property value for " + propertyName + ": " + newValue);
                    }
                } catch (NumberFormatException nfe) {
                    AstericsErrorHandling.instance.reportInfo(this,
                            "Invalid property value for " + propertyName + ": " + newValue);
                }
            }
            return oldValue;
        }
        return null; // To change body of implemented methods use File |
                     // Settings | File Templates.
    }

    /**
     * Input Port for receiving values.
     */
    private class InputPort extends DefaultRuntimeInputPort {
        @Override
        public void receiveData(byte[] data) {
            // convert input to int
            double in = ConversionUtils.doubleFromBytes(data);
            double out = derivative.doDerivative(in);
            opOutputPort.sendData(ConversionUtils.doubleToBytes(out));
        }

    }

    /**
     * Default Output Port for sending the values
     */
    private class OutputPort extends DefaultRuntimeOutputPort {
        // empty
    }
}
