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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.MessageFormat;
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

/**
 * Component to send commands to the IR-transmitter of the company irtrans (www.irtrans.de) via the ethernet.
 *
 * @author Roland Ossmann [ro@ki-i.at] Date: Feb 08, 2011 Time: 11:08:01 AM
 */
public class IrTransInstance extends AbstractRuntimeComponentInstance {
    private final int NUMBER_OF_COMMANDS = 24;
    private final String KEY_PROPERTY_COMMAND = "send";
    private final String KEY_PROPERTY_EVENT = "sendprop";
    private final byte[] BYTES_ASCI_INIT = "ASCI".getBytes();
    private final byte[] BYTES_TESTCOMMAND = "Asnd irtrans,ok\n".getBytes();
    private final String ERROR_RESULT = "ERROR_SOCKET_NOT_OPEN";
    private final int READ_TIMEOUT_LEARN_MS = 6000;
    private final int READ_TIMEOUT_SEND_MS = 500;
    private final int TIMEOUT_JOIN_THREAD = 2000;

    // prestring will be part of every command, which will be sent
    private String propPrestring = "";
    private String propHostname = "localhost";
    private String propPort = "21000";

    private Logger logger = AstericsErrorHandling.instance.getLogger();
    private IRuntimeInputPort ipAction = new InputPort1();
    private final IRuntimeOutputPort opOutput = new DefaultRuntimeOutputPort();
    private final IRuntimeOutputPort opOutputResult = new DefaultRuntimeOutputPort();
    private Socket tcpSocket;
    private Socket tcpSocketRead;
    private Thread readerThread;

    final EventListenerPortSendString[] elpRuntimeEventListenerCmds = new EventListenerPortSendString[NUMBER_OF_COMMANDS];

    public IrTransInstance() {
        for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
            elpRuntimeEventListenerCmds[i] = new EventListenerPortSendString();
        }
    }

    private void reinitSocketIfOpen() {
        if (tcpSocket != null) {
            closeTcpSocket(tcpSocket);
            tcpSocket = initTcpSocket(tcpSocket);
        }
        if (tcpSocketRead != null) {
            closeTcpSocket(tcpSocketRead);
            tcpSocketRead = initTcpSocket(tcpSocketRead);
        }
        if(readerThread != null) {
            readerThread.interrupt();
            try {
                readerThread.join(TIMEOUT_JOIN_THREAD);
            } catch (InterruptedException e) {
                logger.info("interrupted exception on joining reader thread");
            }
            receiveCommandsToOutputPort();
        }
    }

    private Socket initTcpSocket(Socket tcpSocket) {
        if (tcpSocket != null && !tcpSocket.isClosed() && tcpSocket.isConnected()) {
            return tcpSocket;
        }
        try {
            if (tcpSocket != null) {
                tcpSocket.close();
            }
            if (!propHostname.isEmpty() && !propPort.isEmpty()) {
                tcpSocket = new Socket(InetAddress.getByName(propHostname), Integer.parseInt(propPort));
                if (tcpSocket != null) {
                    tcpSocket.getOutputStream().write(BYTES_ASCI_INIT);
                    tcpSocket.getOutputStream().write(BYTES_TESTCOMMAND);
                    tcpSocket.getOutputStream().flush();
                    // if test-command does not return within 500ms -> assume
                    // IrTrans not connected => set tcpSocket null
                    String result = readFromSocket(tcpSocket, READ_TIMEOUT_SEND_MS);
                    if (ERROR_RESULT.equals(result)) {
                        logger.log(Level.WARNING, "failed to open IRTrans-tcpSocket! Test command did not return.");
                    }
                }
            }
        } catch (Exception e) {
            closeTcpSocket(tcpSocket);
            logger.log(Level.WARNING, "failed to open IRTrans-tcpSocket!", e);
        }
        return tcpSocket;
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
        } else if ("outputResult".equalsIgnoreCase(portID)) {
            return opOutputResult;
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
            reinitSocketIfOpen();
            return oldValue;
        } else if ("port".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propPort;
            propPort = (String) newValue;
            reinitSocketIfOpen();
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
    }

    ;

    /**
     * Creating the string, which will be sent as a TCP package. See API and possible commands at
     * http://www.irtrans.de/download/Docs/IRTrans%20TCP%20ASCII%20Schnittstelle_DE.pdf The response is written to the Port named "output"
     *
     * @param cmdString
     *            The string to be sent, must not contain the prefix "A", it is appended in the method in order to be compatible to old UDP-commands
     */
    private void sendString(String cmdString) {
        try {
            tcpSocket = initTcpSocket(tcpSocket);
            int readTimeout = cmdString.contains("learn") ? READ_TIMEOUT_LEARN_MS : READ_TIMEOUT_SEND_MS;
            if (tcpSocket != null) {
                sendToSocket(tcpSocket, cmdString);
                String result = readFromSocket(tcpSocket, readTimeout);
                AstericsErrorHandling.instance.reportInfo(this, "IRTrans response: " + result);
                opOutputResult.sendData(result.getBytes());
            } else {
                opOutputResult.sendData(ERROR_RESULT.getBytes());
            }
        } catch (Exception e) {
            opOutputResult.sendData(ERROR_RESULT.getBytes());
            closeTcpSocket(tcpSocket);
            AstericsErrorHandling.instance.reportError(this,
                    "The IRTrans - Plugin could not send data. Please verify that the IRTrans Module is connected and installed, and that the IP-address is correctly specified.");
            AstericsErrorHandling.instance.reportInfo(this, e.toString());
        }
    }

    private void receiveCommandsToOutputPort() {
        readerThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    tcpSocketRead = initTcpSocket(tcpSocketRead);
                    while (!readerThread.isInterrupted()) {
                        String result = readFromSocket(tcpSocketRead, 0);
                        if(ERROR_RESULT.equals(result)) {
                            readerThread.interrupt();
                        }
                        opOutput.sendData(ConversionUtils.stringToBytes(result));
                        logger.log(Level.INFO, "IRTrans received: " + result);
                    }
                } catch (Exception e) {
                    logger.log(Level.INFO, "reading from socket to receive command stopped");
                } finally {
                    closeTcpSocket(tcpSocketRead);
                }
            }
        });
        readerThread.start();
    }

    /**
     * Standard method from framework
     *
     * @return
     */
    @Override
    public void start() {
        super.start();
        receiveCommandsToOutputPort();
        AstericsErrorHandling.instance.reportInfo(this, "IRTransmitter Instance started");
    }

    /**
     * Standard method from framework
     *
     * @return
     */
    @Override
    public void stop() {
        closeTcpSocket(tcpSocket);
        closeTcpSocket(tcpSocketRead);
        readerThread.interrupt();
        AstericsErrorHandling.instance.reportInfo(this, "IRTransmitter Instance stopped");
        super.stop();
    }

    private void closeTcpSocket(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (Exception e) {
                logger.log(Level.WARNING, "error closing tcpSocket", e);
            }
        }
    }

    private String readFromSocket(Socket socket, int timeoutMillis) {
        String result = ERROR_RESULT;
        try {
            socket.setSoTimeout(timeoutMillis);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            result = bufferedReader.readLine();
        } catch (SocketTimeoutException e) {
            logger.log(Level.WARNING, MessageFormat.format("reading from tcpSocket timed out (timeout: {0}ms)", timeoutMillis), e);
        } catch (IOException e1) {
            logger.log(Level.WARNING, "exception reading from tcpSocket", e1);
            closeTcpSocket(socket);
        }
        return result;
    }

    private void sendToSocket(Socket socket, String cmdString) throws IOException {
        // remove spaces at beginning and end and replace double spaces in
        // string with single space
        byte[] sendBytes = ("A" + cmdString.trim().replaceAll("\\s+", " ") + "\n").getBytes();
        socket.getInputStream().skip(socket.getInputStream().available());
        socket.getOutputStream().write(sendBytes);
        socket.getOutputStream().flush();
        AstericsErrorHandling.instance.reportInfo(this, "IRTrans sent data: " + cmdString);
    }
}