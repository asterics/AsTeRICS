
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

package eu.asterics.component.sensor.signalgenerator;

import java.util.logging.Logger;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * SignalGeneratorInstance provides signal simulation in adjustable waveform,
 * frequency, amplitude and offset
 *
 * @author Costas Kakousis [kakousis@cs.ucy.ac.cy] Date: Aug 20, 2010 Time:
 *         10:22:08 AM
 */
public class SignalGeneratorInstance extends AbstractRuntimeComponentInstance {

    private final OutputPort opOut = new OutputPort();
    private int propSendInterval = 10; // milliseconds
    private int propWaveForm = 1;
    private double propAmplitude = 100;
    private double propFrequency = 2;
    private double propPhaseShift = 0;
    private double propOffset = 0;

    static Logger logger = AstericsErrorHandling.instance.getLogger();

    private final CoordinatesGenerator cg = new CoordinatesGenerator(opOut, propSendInterval, propWaveForm,
            propFrequency, propAmplitude, propPhaseShift, propOffset);

    /**
     * The class constructor.
     */
    public SignalGeneratorInstance() {
        // empty constructor - needed for OSGi service factory operations
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

        if ("out".equalsIgnoreCase(portID)) {
            return opOut;
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
        if ("sendInterval".equalsIgnoreCase(propertyName)) {
            return this.propSendInterval;
        } else if ("waveForm".equalsIgnoreCase(propertyName)) {
            return this.propWaveForm;
        } else if ("amplitude".equalsIgnoreCase(propertyName)) {
            return this.propAmplitude;
        } else if ("frequency".equalsIgnoreCase(propertyName)) {
            return this.propFrequency;
        } else if ("phaseShift".equalsIgnoreCase(propertyName)) {
            return this.propPhaseShift;
        } else if ("offset".equalsIgnoreCase(propertyName)) {
            return this.propOffset;
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
        // Logger.getAnonymousLogger().info("enter setRuntimePropertyValue:" +
        // propertyName);
        if ("sendInterval".equalsIgnoreCase(propertyName)) {
            final Integer oldValue = this.propSendInterval;

            propSendInterval = Integer.parseInt((String) newValue);
            // System.out.println("->send interval set to " + sendInterval +"
            // (ms).");
            cg.setSendInterval(propSendInterval);
            return oldValue;
        } else if ("waveForm".equalsIgnoreCase(propertyName)) {
            final Integer oldValue = this.propWaveForm;
            propWaveForm = Integer.parseInt((String) newValue);
            cg.setWaveForm(propWaveForm);
            return oldValue;
        } else if ("frequency".equalsIgnoreCase(propertyName)) {
            final double oldValue = this.propFrequency;
            propFrequency = Double.parseDouble((String) newValue);
            cg.setFrequency(propFrequency);
            return oldValue;
        } else if ("amplitude".equalsIgnoreCase(propertyName)) {
            final double oldValue = this.propAmplitude;
            propAmplitude = Double.parseDouble((String) newValue);
            cg.setAmplitude(propAmplitude);
            return oldValue;
        } else if ("phaseShift".equalsIgnoreCase(propertyName)) {
            final double oldValue = this.propPhaseShift;
            propPhaseShift = Double.parseDouble((String) newValue);
            cg.setPhaseShift(propPhaseShift);
            return oldValue;
        } else if ("offset".equalsIgnoreCase(propertyName)) {
            final double oldValue = this.propOffset;
            propOffset = Double.parseDouble((String) newValue);
            cg.setOffset(propOffset);
            return oldValue;
        }

        return null;
    }

    /**
     * Output Port for sending values.
     */
    public class OutputPort extends DefaultRuntimeOutputPort {
        public void sendData(double data) {
            super.sendData(ConversionUtils.doubleToBytes(data));
        }
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        // logger.fine(this.getClass().getName()+" started");
        super.start();
        cg.start();

    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        super.pause();
        cg.pause();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        super.resume();
        cg.resume();

    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        super.stop();
        cg.stop();
    }

}