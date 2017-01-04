
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

package eu.asterics.component.processor.regularexpression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;

/**
 * 
 * This plugin uses regular expressions to string processing.
 * 
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl] Date: Apr 04, 2012 Time: 11:28:45
 *         AM
 */
public class RegularExpressionInstance extends AbstractRuntimeComponentInstance {
    final IRuntimeOutputPort opOutput = new DefaultRuntimeOutputPort();
    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    final IRuntimeEventTriggererPort etpMatch = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpNotMatch = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpReplace = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpNotReplace = new DefaultRuntimeEventTriggererPort();
    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    String propPattern = "";
    boolean propReplace = false;
    String propReplaceString = "";

    // declare member variables here

    /**
     * The class constructor.
     */
    public RegularExpressionInstance() {
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
        if ("input".equalsIgnoreCase(portID)) {
            return ipInput;
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
            return opOutput;
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
        if ("match".equalsIgnoreCase(eventPortID)) {
            return etpMatch;
        }
        if ("notMatch".equalsIgnoreCase(eventPortID)) {
            return etpNotMatch;
        }
        if ("replace".equalsIgnoreCase(eventPortID)) {
            return etpReplace;
        }
        if ("notReplace".equalsIgnoreCase(eventPortID)) {
            return etpNotReplace;
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
        if ("pattern".equalsIgnoreCase(propertyName)) {
            return propPattern;
        }
        if ("replace".equalsIgnoreCase(propertyName)) {
            return propReplace;
        }
        if ("replaceString".equalsIgnoreCase(propertyName)) {
            return propReplaceString;
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
        if ("pattern".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propPattern;
            propPattern = (String) newValue;
            return oldValue;
        }
        if ("replace".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propReplace;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propReplace = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propReplace = false;
            }
            return oldValue;
        }
        if ("replaceString".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propReplaceString;
            propReplaceString = (String) newValue;
            return oldValue;
        }

        return null;
    }

    /**
     * Input Ports for receiving values.
     */
    private final IRuntimeInputPort ipInput = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            String text = ConversionUtils.stringFromBytes(data);
            if (propReplace == false) {
                if (propPattern.isEmpty()) {
                    etpNotMatch.raiseEvent();
                } else {
                    try {
                        Pattern pattern = Pattern.compile(propPattern);
                        Matcher matcher = pattern.matcher(text);

                        boolean result;

                        result = matcher.matches();

                        if (result) {
                            etpMatch.raiseEvent();
                            opOutput.sendData(ConversionUtils.stringToBytes(text));
                        } else {
                            etpNotMatch.raiseEvent();
                        }
                    } catch (Exception e) {
                        etpNotMatch.raiseEvent();
                    }
                }
            } else {
                if (propPattern.isEmpty()) {
                    etpNotReplace.raiseEvent();
                    opOutput.sendData(ConversionUtils.stringToBytes(text));
                } else {
                    try {
                        Pattern pattern = Pattern.compile(propPattern);
                        Matcher matcher = pattern.matcher(text);

                        String result = "";

                        result = matcher.replaceAll(propReplaceString);

                        if (text.equals(result)) {
                            etpNotReplace.raiseEvent();
                        } else {
                            etpReplace.raiseEvent();
                        }
                        opOutput.sendData(ConversionUtils.stringToBytes(result));
                    } catch (Exception e) {
                        etpNotReplace.raiseEvent();
                        opOutput.sendData(ConversionUtils.stringToBytes(text));
                    }

                }
            }
        }
    };

    /**
     * Event Listerner Ports.
     */

    /**
     * called when model is started.
     */
    @Override
    public void start() {

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