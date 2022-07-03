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

package eu.asterics.component.processor.textsender;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;

/**
 * Implemets plugin which sends text when it get the event.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl] Date: Feb 15, 2011 Time: 11:41:08
 *         AM
 */
public class TextSenderInstance extends AbstractRuntimeComponentInstance {

    private final String OP_OUTPUT = "output";
    private final String ELP_SEND_TEXT = "sendText";
    private final String PROP_TEXT = "text";

    private OutputPort opOutput = new OutputPort();
    private String propText = "This is a sample text.";

    /**
     * The class constructor.
     */
    public TextSenderInstance() {
    }

    /**
     * Called when model is started.
     */
    @Override
    public void start() {
        super.start();
    }

    /**
     * Called when model is paused.
     */
    @Override
    public void pause() {
        super.pause();
    }

    /**
     * Called when model is resumed.
     */
    @Override
    public void resume() {
        super.resume();
    }

    /**
     * Called when model is stopped
     */
    @Override
    public void stop() {
        super.stop();
    }

    /**
     * Returns an Input Port.
     * 
     * @param portID
     *            the name of the port
     * @return the input port or null if not found
     */
    @Override
    public IRuntimeInputPort getInputPort(String portID) {
        if ("setText".equalsIgnoreCase(portID)) {
            return ipSetText;
        }
        return null;
    }

    /**
     * Returns an Output Port.
     * 
     * @param portID
     *            the name of the port
     * @return the output port
     */
    @Override
    public IRuntimeOutputPort getOutputPort(String portID) {
        if (OP_OUTPUT.equalsIgnoreCase(portID)) {
            return opOutput;
        } else {
            return null;
        }
    }

    /**
     * Sends the text
     */
    public void sendText() {
        opOutput.sendData(propText);
    }

    /**
     * Returns the value of the given property.
     * 
     * @param propertyName
     *            the name of the property
     * @return the property value or null if not found
     */
    @Override
    public Object getRuntimePropertyValue(String propertyName) {
        if (PROP_TEXT.equalsIgnoreCase(propertyName)) {
            return propText;
        } else {
            return null;
        }
    }

    /**
     * Sets a new value for the given property.
     * 
     * @param propertyName
     *            the name of the property
     * @param newValue
     *            the desired property value
     * @return old property value
     */
    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        if (PROP_TEXT.equalsIgnoreCase(propertyName)) {
            final Object oldValue = propText;
            propText = (String) newValue;
            return oldValue;
        } else {
            return null;
        }

    }

    /**
     * Returns an Event Listener Port.
     * 
     * @param eventPortID
     *            the name of the port
     * @return the event listener port or null if not found
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        if (ELP_SEND_TEXT.equalsIgnoreCase(eventPortID)) {
            return elpSendText;
        } else {
            return null;
        }
    }

    /**
     * Input event port.
     */
    final IRuntimeEventListenerPort elpSendText = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(String data) {
            sendText();
        }
    };

    /**
     * Plugin input port.
     */

    private final IRuntimeInputPort ipSetText = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            propText = ConversionUtils.stringFromBytes(data);
        }

    };

    /**
     * Plugin output port.
     */
    private class OutputPort extends DefaultRuntimeOutputPort {
        public void sendData(String data) {
            super.sendData(ConversionUtils.stringToBytes(data));
        }
    }
}