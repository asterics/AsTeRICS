
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

package com.starlab.component.processor.dissimilarity;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Implements the dissimilarity between two signals
 * 
 * @author Javier Acedo [javier.acedo@starlab.es] Date: Apr 29, 2011 Time
 *         04:51:02 PM
 */
public class DissimilarityInstance extends AbstractRuntimeComponentInstance {
    private InputPort1 ipInputPort1 = new InputPort1();
    private InputPort2 ipInputPort2 = new InputPort2();
    private OutputPort opOutputPort = new OutputPort();

    private Dissimilarity dissimilarity = new Dissimilarity();

    public static final int DEFAULT_DATALEN_VALUE = 100;
    private int propDataLen = DEFAULT_DATALEN_VALUE;

    private double[] in1 = new double[DEFAULT_DATALEN_VALUE];
    private int iBuffer1 = 0;
    private boolean isBuffer1Full = false;
    private double[] in2 = new double[DEFAULT_DATALEN_VALUE];
    private int iBuffer2 = 0;
    private boolean isBuffer2Full = false;

    /**
     * The class constructor
     */
    public DissimilarityInstance() {
        //
    }

    /**
     * called when model is started
     */
    @Override
    public void start() {
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
        if ("input1".equalsIgnoreCase(portID)) {
            return ipInputPort1;
        } else if ("input2".equalsIgnoreCase(portID)) {
            return ipInputPort2;
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
        if ("DataLen".equalsIgnoreCase(propertyName)) {
            return this.propDataLen;
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
        if ("DataLen".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propDataLen;

            if (newValue != null) {
                try {
                    int newDataLen = Integer.parseInt(newValue.toString());
                    if (newDataLen > 0) {
                        if (newDataLen != propDataLen) {
                            in1 = new double[newDataLen];
                            in2 = new double[newDataLen];
                            iBuffer1 = 0;
                            iBuffer2 = 0;
                            isBuffer1Full = false;
                            isBuffer2Full = false;
                            propDataLen = newDataLen;
                        }
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
     * Input Port for receiving values of signal #1
     */
    private class InputPort1 extends DefaultRuntimeInputPort {
        @Override
        public void receiveData(byte[] data) {
            in1[iBuffer1++] = ConversionUtils.doubleFromBytes(data);
            if (iBuffer1 >= propDataLen) {
                iBuffer1 = 0; // reset bufferIn
                if (isBuffer2Full) {
                    isBuffer1Full = false;
                    isBuffer2Full = false;
                    opOutputPort.sendData(ConversionUtils.doubleToBytes(dissimilarity.process(in1, in2)));
                } else {
                    isBuffer1Full = true;
                }
            }
        }

    }

    /**
     * Input Port for receiving values of signal #2
     */
    private class InputPort2 extends DefaultRuntimeInputPort {
        @Override
        public void receiveData(byte[] data) {
            in2[iBuffer2++] = ConversionUtils.doubleFromBytes(data);
            if (iBuffer2 >= propDataLen) {
                iBuffer2 = 0; // reset bufferIn
                if (isBuffer1Full) {
                    isBuffer1Full = false;
                    isBuffer2Full = false;
                    opOutputPort.sendData(ConversionUtils.doubleToBytes(dissimilarity.process(in1, in2)));
                } else {
                    isBuffer2Full = true;
                }
            }
        }
    }

    /**
     * Default Output Port for sending the values
     */
    private class OutputPort extends DefaultRuntimeOutputPort {
        // empty
    }
}
