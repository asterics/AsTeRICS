
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
 *       This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.processor.relativemovesampler;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * 
 * Implements the Relative Move Sampler.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl] Date: Feb 10, 2012 Time: 11:54:11
 *         AM
 */
public class RelativeMoveSamplerInstance extends AbstractRuntimeComponentInstance {
    final IRuntimeOutputPort opOutputX = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opOutputY = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opOutputZ = new DefaultRuntimeOutputPort();
    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    double propSamplingRate = 10;

    private SamplingTimer samplingTimer = new SamplingTimer(this);
    // declare member variables here
    private int sumX = 0;
    private int sumY = 0;
    private int sumZ = 0;

    /**
     * The class constructor.
     */
    public RelativeMoveSamplerInstance() {
        // empty constructor
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
        if ("inputX".equalsIgnoreCase(portID)) {
            return ipInputX;
        }
        if ("inputY".equalsIgnoreCase(portID)) {
            return ipInputY;
        }
        if ("inputZ".equalsIgnoreCase(portID)) {
            return ipInputZ;
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
        if ("outputX".equalsIgnoreCase(portID)) {
            return opOutputX;
        }
        if ("outputY".equalsIgnoreCase(portID)) {
            return opOutputY;
        }
        if ("outputZ".equalsIgnoreCase(portID)) {
            return opOutputZ;
        }

        return null;
    }

    /**
     * returns an Event Listener Port.
     * 
     * @param eventPortID
     *            the name of the port
     * @return the EventListener port or null if not found
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * 
     * @param eventPortID
     *            the name of the port
     * @return the EventTriggerer port or null if not found
     */
    @Override
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {

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
        if ("samplingRate".equalsIgnoreCase(propertyName)) {
            return propSamplingRate;
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
        if ("samplingRate".equalsIgnoreCase(propertyName)) {
            final double oldValue = propSamplingRate;
            propSamplingRate = Double.parseDouble((String) newValue);

            if (propSamplingRate == 0) {
                propSamplingRate = 10;
                AstericsErrorHandling.instance.getLogger()
                        .warning("Sampling rate can not equal 0. Sampling rate is set to 10");
            }

            if (propSamplingRate < 0) {
                propSamplingRate = propSamplingRate * (-1);
                AstericsErrorHandling.instance.getLogger()
                        .warning("Sampling rate can not be negative. Sampling rate is set to "
                                + Double.toString(propSamplingRate));
            }

            double sampleTime = 1000.0 / propSamplingRate;

            if (sampleTime < 1) {
                sampleTime = 100;
            }

            long sampleTimelong = (long) sampleTime;

            samplingTimer.setSamplingTime(sampleTimelong);

            return oldValue;
        }

        return null;
    }

    /**
     * Input Ports for receiving values.
     */
    private final IRuntimeInputPort ipInputX = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            int move = ConversionUtils.intFromBytes(data);
            sumX = sumX + move;
        }

    };
    private final IRuntimeInputPort ipInputY = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            int move = ConversionUtils.intFromBytes(data);
            sumY = sumY + move;
        }

    };
    private final IRuntimeInputPort ipInputZ = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            int move = ConversionUtils.intFromBytes(data);
            sumZ = sumZ + move;
        }

    };

    /**
     * Sends the values.
     */
    void sendValues() {
        opOutputX.sendData(ConversionUtils.intToBytes(sumX));
        sumX = 0;
        opOutputY.sendData(ConversionUtils.intToBytes(sumY));
        sumY = 0;
        opOutputZ.sendData(ConversionUtils.intToBytes(sumZ));
        sumZ = 0;
    }

    /**
     * clears the values.
     */
    private void clearValues() {
        sumX = 0;
        sumY = 0;
        sumZ = 0;
    }

    /**
     * Event Listerner Ports.
     */

    /**
     * called when model is started.
     */
    @Override
    public void start() {

        super.start();
        clearValues();
        samplingTimer.startSampling();
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        super.pause();
        samplingTimer.stopSampling();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        super.resume();
        clearValues();
        samplingTimer.startSampling();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        super.stop();
        samplingTimer.stopSampling();
    }
}