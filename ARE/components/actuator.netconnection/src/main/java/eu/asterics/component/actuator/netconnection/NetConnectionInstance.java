
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

package eu.asterics.component.actuator.netconnection;

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
 * This class implements component which allows to pass data through the network
 * between AREs and applications which use Native ASAPI DLL library.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl] Date: Aug 06, 2012 Time: 15:15:53
 *         AM
 * 
 */
public class NetConnectionInstance extends AbstractRuntimeComponentInstance {
    private final int NUMBER_OF_EVENT_INPUTS = 10;
    private final int NUMBER_OF_INTEGER_INPUTS = 5;
    private final int NUMBER_OF_DOUBLE_INPUTS = 5;
    private final int NUMBER_OF_STRING_INPUTS = 5;

    private final int NUMBER_OF_EVENT_OUTPUTS = 10;
    private final int NUMBER_OF_INTEGER_OUTPUTS = 5;
    private final int NUMBER_OF_DOUBLE_OUTPUTS = 5;
    private final int NUMBER_OF_STRING_OUTPUTS = 5;

    private final String IP_INTEGER_INPUT_PORT = "integerInputPort";
    private final String IP_DOUBLE_INPUT_PORT = "doubleInputPort";
    private final String IP_STRING_INPUT_PORT = "stringInputPort";
    private final String ELP_INPUT_EVENT = "inputEvent";

    private final String OP_INTEGER_OUTPUT_PORT = "integerOutputPort";
    private final String OP_DOUBLE_OUTPUT_PORT = "doubleOutputPort";
    private final String OP_STRING_OUTPUT_PORT = "stringOutputPort";
    private final String ETP_OUTPUT_EVENT = "outputEvent";

    final IRuntimeOutputPort[] opIntegerOutputPortArray = new DefaultRuntimeOutputPort[NUMBER_OF_INTEGER_OUTPUTS];
    final IRuntimeOutputPort[] opDoubleOutputPortArray = new DefaultRuntimeOutputPort[NUMBER_OF_DOUBLE_OUTPUTS];
    final IRuntimeOutputPort[] opStringOutputPortArray = new DefaultRuntimeOutputPort[NUMBER_OF_STRING_OUTPUTS];

    private IntegerInputPort[] ipIntegerInputPortArray = new IntegerInputPort[NUMBER_OF_INTEGER_OUTPUTS];
    private DoubleInputPort[] ipDoubleInputPortArray = new DoubleInputPort[NUMBER_OF_DOUBLE_OUTPUTS];
    private StringInputPort[] ipStringInputPortArray = new StringInputPort[NUMBER_OF_STRING_OUTPUTS];
    private InputEvent[] elpInputEventArray = new InputEvent[NUMBER_OF_EVENT_OUTPUTS];

    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    final IRuntimeEventTriggererPort[] etpOutputEventArray = new DefaultRuntimeEventTriggererPort[NUMBER_OF_EVENT_OUTPUTS];

    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    int propConnectionType = 0;
    String propIP = "localhost";
    int propPort = 21112;
    boolean propMultisession = false;

    // declare member variables here
    CommunicationManager communicationManager = null;

    /**
     * The class constructor.
     */
    public NetConnectionInstance() {
        for (int i = 0; i < NUMBER_OF_EVENT_OUTPUTS; i++) {
            etpOutputEventArray[i] = new DefaultRuntimeEventTriggererPort();
        }

        for (int i = 0; i < NUMBER_OF_INTEGER_OUTPUTS; i++) {
            opIntegerOutputPortArray[i] = new DefaultRuntimeOutputPort();
        }

        for (int i = 0; i < NUMBER_OF_DOUBLE_OUTPUTS; i++) {
            opDoubleOutputPortArray[i] = new DefaultRuntimeOutputPort();
        }

        for (int i = 0; i < NUMBER_OF_STRING_OUTPUTS; i++) {
            opStringOutputPortArray[i] = new DefaultRuntimeOutputPort();
        }

        for (int i = 0; i < NUMBER_OF_EVENT_INPUTS; i++) {
            elpInputEventArray[i] = new InputEvent();
            elpInputEventArray[i].portID = i;
        }

        for (int i = 0; i < NUMBER_OF_INTEGER_INPUTS; i++) {
            ipIntegerInputPortArray[i] = new IntegerInputPort();
            ipIntegerInputPortArray[i].portID = i;
        }

        for (int i = 0; i < NUMBER_OF_DOUBLE_INPUTS; i++) {
            ipDoubleInputPortArray[i] = new DoubleInputPort();
            ipDoubleInputPortArray[i].portID = i;
        }

        for (int i = 0; i < NUMBER_OF_STRING_INPUTS; i++) {
            ipStringInputPortArray[i] = new StringInputPort();
            ipStringInputPortArray[i].portID = i;
        }

        communicationManager = new CommunicationManager(etpOutputEventArray, opIntegerOutputPortArray,
                opDoubleOutputPortArray, opStringOutputPortArray);
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
        int ipIntegerInputPortSize = IP_INTEGER_INPUT_PORT.length();
        if (portID.length() > ipIntegerInputPortSize) {
            String testName = portID.substring(0, ipIntegerInputPortSize);
            if (testName.equalsIgnoreCase(IP_INTEGER_INPUT_PORT)) {
                String portNumberText = portID.substring(ipIntegerInputPortSize);
                int portNumberValue;
                try {
                    portNumberValue = Integer.parseInt(portNumberText);
                } catch (NumberFormatException ex) {
                    return null;
                }

                if (portNumberValue > 0 && portNumberValue <= NUMBER_OF_INTEGER_INPUTS) {
                    return ipIntegerInputPortArray[portNumberValue - 1];
                } else {
                    return null;
                }
            }
        }

        int ipDoubleInputPortSize = IP_DOUBLE_INPUT_PORT.length();
        if (portID.length() > ipDoubleInputPortSize) {
            String testName = portID.substring(0, ipDoubleInputPortSize);
            if (testName.equalsIgnoreCase(IP_DOUBLE_INPUT_PORT)) {
                String portNumberText = portID.substring(ipDoubleInputPortSize);
                int portNumberValue;
                try {
                    portNumberValue = Integer.parseInt(portNumberText);
                } catch (NumberFormatException ex) {
                    return null;
                }

                if (portNumberValue > 0 && portNumberValue <= NUMBER_OF_DOUBLE_INPUTS) {
                    return ipDoubleInputPortArray[portNumberValue - 1];
                } else {
                    return null;
                }
            }
        }

        int ipStringInputPortSize = IP_STRING_INPUT_PORT.length();
        if (portID.length() > ipStringInputPortSize) {
            String testName = portID.substring(0, ipStringInputPortSize);
            if (testName.equalsIgnoreCase(IP_STRING_INPUT_PORT)) {
                String portNumberText = portID.substring(ipStringInputPortSize);
                int portNumberValue;
                try {
                    portNumberValue = Integer.parseInt(portNumberText);
                } catch (NumberFormatException ex) {
                    return null;
                }

                if (portNumberValue > 0 && portNumberValue <= NUMBER_OF_STRING_INPUTS) {
                    return ipStringInputPortArray[portNumberValue - 1];
                } else {
                    return null;
                }
            }
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
        int opIntegerOutputPortSize = OP_INTEGER_OUTPUT_PORT.length();
        if (portID.length() > opIntegerOutputPortSize) {
            String testName = portID.substring(0, opIntegerOutputPortSize);
            if (testName.equalsIgnoreCase(OP_INTEGER_OUTPUT_PORT)) {
                String portNumberText = portID.substring(opIntegerOutputPortSize);
                int portNumberValue;
                try {
                    portNumberValue = Integer.parseInt(portNumberText);
                } catch (NumberFormatException ex) {
                    return null;
                }

                if (portNumberValue > 0 && portNumberValue <= NUMBER_OF_INTEGER_OUTPUTS) {
                    return opIntegerOutputPortArray[portNumberValue - 1];
                } else {
                    return null;
                }
            }
        }

        int opDoubleOutputPortSize = OP_DOUBLE_OUTPUT_PORT.length();
        if (portID.length() > opDoubleOutputPortSize) {
            String testName = portID.substring(0, opDoubleOutputPortSize);
            if (testName.equalsIgnoreCase(OP_DOUBLE_OUTPUT_PORT)) {
                String portNumberText = portID.substring(opDoubleOutputPortSize);
                int portNumberValue;
                try {
                    portNumberValue = Integer.parseInt(portNumberText);
                } catch (NumberFormatException ex) {
                    return null;
                }

                if (portNumberValue > 0 && portNumberValue <= NUMBER_OF_DOUBLE_OUTPUTS) {
                    return opDoubleOutputPortArray[portNumberValue - 1];
                } else {
                    return null;
                }
            }
        }

        int opStringOutputPortSize = OP_STRING_OUTPUT_PORT.length();
        if (portID.length() > opStringOutputPortSize) {
            String testName = portID.substring(0, opStringOutputPortSize);
            if (testName.equalsIgnoreCase(OP_STRING_OUTPUT_PORT)) {
                String portNumberText = portID.substring(opStringOutputPortSize);
                int portNumberValue;
                try {
                    portNumberValue = Integer.parseInt(portNumberText);
                } catch (NumberFormatException ex) {
                    return null;
                }

                if (portNumberValue > 0 && portNumberValue <= NUMBER_OF_STRING_OUTPUTS) {
                    return opStringOutputPortArray[portNumberValue - 1];
                } else {
                    return null;
                }
            }
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

        int elpInputEventSize = ELP_INPUT_EVENT.length();
        if (eventPortID.length() > elpInputEventSize) {
            String testName = eventPortID.substring(0, elpInputEventSize);
            if (testName.equalsIgnoreCase(ELP_INPUT_EVENT)) {
                String portNumberText = eventPortID.substring(elpInputEventSize);
                int portNumberValue;
                try {
                    portNumberValue = Integer.parseInt(portNumberText);
                } catch (NumberFormatException ex) {
                    return null;
                }

                if (portNumberValue > 0 && portNumberValue <= NUMBER_OF_EVENT_INPUTS) {
                    return elpInputEventArray[portNumberValue - 1];
                } else {
                    return null;
                }
            }
        }

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

        int etpOutputEventSize = ETP_OUTPUT_EVENT.length();
        if (eventPortID.length() > etpOutputEventSize) {
            String testName = eventPortID.substring(0, etpOutputEventSize);
            if (testName.equalsIgnoreCase(ETP_OUTPUT_EVENT)) {
                String portNumberText = eventPortID.substring(etpOutputEventSize);
                int portNumberValue;
                try {
                    portNumberValue = Integer.parseInt(portNumberText);
                } catch (NumberFormatException ex) {
                    return null;
                }

                if (portNumberValue > 0 && portNumberValue <= NUMBER_OF_EVENT_OUTPUTS) {
                    return etpOutputEventArray[portNumberValue - 1];
                } else {
                    return null;
                }
            }
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
        if ("connectionType".equalsIgnoreCase(propertyName)) {
            return propConnectionType;
        }
        if ("iP".equalsIgnoreCase(propertyName)) {
            return propIP;
        }
        if ("port".equalsIgnoreCase(propertyName)) {
            return propPort;
        }
        if ("multisession".equalsIgnoreCase(propertyName)) {
            return propMultisession;
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
        if ("connectionType".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propConnectionType;
            propConnectionType = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("iP".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propIP;
            propIP = (String) newValue;
            return oldValue;
        }
        if ("port".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propPort;
            propPort = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("multisession".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propMultisession;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propMultisession = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propMultisession = false;
            }
            return oldValue;
        }

        return null;
    }

    /**
     * Input Ports for receiving values.
     */
    private class IntegerInputPort extends DefaultRuntimeInputPort {
        int portID = -1;

        @Override
        public void receiveData(byte[] data) {

            int value = ConversionUtils.intFromBytes(data);
            SendValue(Command.Integer, portID + 1, 0, value, "");
        }
    };

    private class DoubleInputPort extends DefaultRuntimeInputPort {
        int portID = -1;

        @Override
        public void receiveData(byte[] data) {
            Double value = ConversionUtils.doubleFromBytes(data);
            SendValue(Command.Double, portID + 1, value, 0, "");
        }
    };

    private class StringInputPort extends DefaultRuntimeInputPort {
        int portID = -1;

        @Override
        public void receiveData(byte[] data) {
            String text = ConversionUtils.stringFromBytes(data);
            SendValue(Command.String, portID + 1, 0, 0, text);
        }
    };

    /**
     * Event Listerner Ports.
     */
    final class InputEvent implements IRuntimeEventListenerPort {
        int portID = -1;

        @Override
        public void receiveEvent(final String data) {
            SendValue(Command.Event, portID + 1, 0, 0, "");
        }
    };

    /**
     * This method sends the value through the network.
     * 
     * @param command
     *            defines value type
     * @param port
     *            defines the port of the remote receiver.
     * @param doubleData
     *            double value
     * @param integerData
     *            integer value
     * @param stringData
     *            string value
     */
    private void SendValue(Command command, int port, double doubleData, int integerData, String stringData) {
        communicationManager.sendCommand(command, port, doubleData, integerData, stringData);
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        super.start();

        if (propConnectionType == 0) {
            communicationManager.start(false, propIP, propPort, propMultisession);
        } else {
            communicationManager.start(true, propIP, propPort, propMultisession);
        }
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        super.pause();
        communicationManager.setPause(true);
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        super.resume();
        communicationManager.setPause(false);
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        super.stop();
        communicationManager.stop();
    }
}