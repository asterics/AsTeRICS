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

package eu.asterics.component.actuator.irtrans;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsModelExecutionThreadPool;

/**
 * Component to send commands to the IR-transmitter of the company irtrans
 * (www.irtrans.de) via the ethernet.
 *
 *
 * @author Roland Ossmann [ro@ki-i.at] Date: Feb 08, 2011 Time: 11:08:01 AM
 */
public class IrTransInstance extends AbstractRuntimeComponentInstance {
    private final int NUMBER_OF_COMMANDS = 24;
    private final String KEY_PROPERTY_COMMAND = "send";
    private final String KEY_PROPERTY_EVENT = "sendprop";

    // prestring will be part of every command, which will be sent
    private String propPrestring = "";
    private String propHostname = "localhost";
    private String propPort = "21000";

    private Logger logger = AstericsErrorHandling.instance.getLogger();
    private IRuntimeInputPort ipAction = new InputPort1();
    private final IRuntimeOutputPort opOutput = new DefaultRuntimeOutputPort();
    private Socket socket;

    final EventListenerPortSendString[] elpRuntimeEventListenerCmds = new EventListenerPortSendString[NUMBER_OF_COMMANDS];

    public IrTransInstance() {
        for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
            elpRuntimeEventListenerCmds[i] = new EventListenerPortSendString();
        }
    }

    private void initSocket() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (!propHostname.isEmpty() && !propPort.isEmpty()) {
                socket = new Socket(InetAddress.getByName(propHostname), Integer.parseInt(propPort));
                if (socket != null) {
                    socket.setSoTimeout(AstericsModelExecutionThreadPool.TASK_SUBMIT_TIMEOUT);
                    socket.getOutputStream().write("ASCI".getBytes());
                    socket.getOutputStream().flush();
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "failed to open IRTrans-socket!");
        }
    }

    /**
     * Standard method from framework
     *
     * @param portID
     * @return
     */
    @Override
    public IRuntimeInputPort getInputPort(String portID) {
        if ("action".equalsIgnoreCase(portID)) {
            return ipAction;
        } else {
            return null;
        }
    }

    /**
     * Standard method from framework
     *
     * @param portID
     * @return
     */
    @Override
    public IRuntimeOutputPort getOutputPort(String portID) {
        if ("output".equalsIgnoreCase(portID)) {
            return opOutput;
        } else {
            return null;
        }
    }

    /**
     * Standard method from framework
     *
     * @return
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {

        for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
            String s = KEY_PROPERTY_EVENT + (i + 1);
            if (s.equalsIgnoreCase(eventPortID)) {
                return elpRuntimeEventListenerCmds[i];
            }
        }
        return null;

    }

    /**
     * Standard method from framework
     *
     * @return
     */
    @Override
    public Object getRuntimePropertyValue(String propertyName) {
        // Logger.getAnonymousLogger().info("IRTrans, getRuntimePropertyValue");
        // System.out.println("IRTrans, getRuntimePropertyValue");

        if ("prestring".equalsIgnoreCase(propertyName)) {
            return propPrestring;
        } else if ("hostname".equalsIgnoreCase(propertyName)) {
            return propHostname;
        } else if ("port".equalsIgnoreCase(propertyName)) {
            return propPort;
        } else {
            for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
                String s = KEY_PROPERTY_COMMAND + (i + 1);
                if (s.equalsIgnoreCase(propertyName)) {
                    return (elpRuntimeEventListenerCmds[i].stringToSend);
                }
            }
        }
        return null;
    }

    /**
     * Standard method from framework
     *
     * @return
     */
    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {

        if ("prestring".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propPrestring;
            propPrestring = (String) newValue;
            return oldValue;
        } else if ("hostname".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propHostname;
            propHostname = (String) newValue;
            initSocket();
            return oldValue;
        } else if ("port".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propPort;
            propPort = (String) newValue;
            initSocket();
            return oldValue;
        } else {
            for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
                String s = KEY_PROPERTY_COMMAND + (i + 1);
                if (s.equalsIgnoreCase(propertyName)) {
                    final Object oldValue = elpRuntimeEventListenerCmds[i].stringToSend;
                    elpRuntimeEventListenerCmds[i].stringToSend = (String) newValue;
                    return oldValue;
                }
            }
        }
        return null;
    }

    private class InputPort1 extends DefaultRuntimeInputPort {
        @Override
        public void receiveData(byte[] data) {
            String text = ConversionUtils.stringFromBytes(data);
            if (text.startsWith("@IRTRANS:")) {
                sendString((text.substring(9).trim()));
            }

        }

    }

    class EventListenerPortSendString implements IRuntimeEventListenerPort {
        String stringToSend = "";

        @Override
        public void receiveEvent(final String data) {
            sendString(propPrestring + stringToSend);
        }
    };

    /**
     * Creating the string, which will be sent as a TCP package.
     * See API and possible commands at http://www.irtrans.de/download/Docs/IRTrans%20TCP%20ASCII%20Schnittstelle_DE.pdf
     * The response is written to the Port named "output"
     *
     * @param cmdString
     *            The string to be sent, must not contain the prefix "A", it is appended in the method in order to be
     *            compatible to old UDP-commands
     */
    private void sendString(String cmdString) {
        try {
            //remove spaces at beginning and end and replace double spaces in string with single space
            byte[] sendBytes = ("A" + cmdString.trim().replaceAll("\\s+", " ") + "\n").getBytes();
            socket.getInputStream().skip(socket.getInputStream().available());
            socket.getOutputStream().write(sendBytes);
            socket.getOutputStream().flush();
            AstericsErrorHandling.instance.reportInfo(this, "IRTrans sent data: " + cmdString);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String result = bufferedReader.readLine();
            AstericsErrorHandling.instance.reportInfo(this, "IRTrans response: " + result);
            opOutput.sendData(result.getBytes());
        } catch (Exception e) {
            AstericsErrorHandling.instance.reportError(this,
                    "The IRTrans - Plugin could not send data. Please verify that the IRTrans Module is connected and installed, and that the IP-address is correctly specified.");
            AstericsErrorHandling.instance.reportInfo(this, e.toString());
        }
    }

    /**
     * Standard method from framework
     *
     * @return
     */
    @Override
    public void start() {
        super.start();
        initSocket();
        AstericsErrorHandling.instance.reportInfo(this, "IRTransmitter Instance started");
    }

    /**
     * Standard method from framework
     *
     * @return
     */
    @Override
    public void stop() {
        if (socket != null) {
            try {
                socket.close();
            } catch (Exception e) {
            }
        }
        socket = null;
        AstericsErrorHandling.instance.reportInfo(this, "IRTransmitter Instance stopped");
        super.stop();
    }
}