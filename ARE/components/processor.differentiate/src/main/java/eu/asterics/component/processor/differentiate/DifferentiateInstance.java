
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

package eu.asterics.component.processor.differentiate;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;

/**
 * Implements the differentiate plugin, which outputs difference of the current
 * and the previous input value
 * 
 * @author Chris Veigl [veigl@technikum-wien.at] Date: Feb 15, 2011 Time:
 *         01:45:00 PM
 */
public class DifferentiateInstance extends AbstractRuntimeComponentInstance {

    private IRuntimeInputPort ipIn = new InputPort1();
    private IRuntimeOutputPort ipOut = new OutputPort1();

    private double propResetValue = 0;

    private double previousValue = 0;

    /**
     * The class constructor.
     */
    public DifferentiateInstance() {
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
        if ("in".equalsIgnoreCase(portID)) {
            return ipIn;
        } else {
            return null;
        }
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
            return ipOut;
        } else {
            return null;
        }
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
        if ("resetValue".equalsIgnoreCase(propertyName)) {
            return propResetValue;
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
        if ("resetValue".equalsIgnoreCase(propertyName)) {
            final Double oldValue = this.propResetValue;
            propResetValue = Double.parseDouble((String) newValue);
            return oldValue;
        }
        return null;
    }

    /**
     * Input Port for receiving values.
     */
    private class InputPort1 extends DefaultRuntimeInputPort {
        @Override
        public void receiveData(byte[] data) {
            // convert input to double
            double in = ConversionUtils.doubleFromBytes(data);
            // Logger.getAnonymousLogger().info("in data: " + in);

            // compute and send output
            ipOut.sendData(ConversionUtils.doubleToBytes(previousValue - in));
            previousValue = in;
        }

    }

    /**
     * Output Port for sending values.
     */
    private class OutputPort1 extends DefaultRuntimeOutputPort {
        // empty
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        previousValue = propResetValue;
        super.start();
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        super.pause();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        super.resume();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        super.stop();
    }

}