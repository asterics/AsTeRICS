
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

package eu.asterics.component.processor.yaak;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 * 
 * @author David Thaller dt@ki-i.at Date: 30.06.2012 Time: 13:37
 */
public class YaakInstance extends AbstractRuntimeComponentInstance {
    final IRuntimeOutputPort opAction = new DefaultRuntimeOutputPort();
    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    String propHostname = "localhost";
    int propPort = 44000;
    Thread reader;
    Socket s;
    BufferedReader in;
    PrintWriter out;
    String action = "";
    // declare member variables here

    /**
     * The class constructor.
     */
    public YaakInstance() {

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
        if ("action".equalsIgnoreCase(portID)) {
            return opAction;
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
        if ("trigger".equalsIgnoreCase(eventPortID)) {
            return elpTrigger;
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
        if ("hostname".equalsIgnoreCase(propertyName)) {
            return propHostname;
        }
        if ("port".equalsIgnoreCase(propertyName)) {
            return propPort;
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
        if ("hostname".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propHostname;
            propHostname = (String) newValue;
            return oldValue;
        }
        if ("port".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propPort;
            propPort = Integer.parseInt(newValue.toString());
            return oldValue;
        }

        return null;
    }

    /**
     * Input Ports for receiving values.
     */

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpTrigger = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            System.out.println("Send trigger");
            if (out.checkError() == false) {
                out.println("trigger");
                out.flush();
            } else {
                System.out.println("Error");
            }
        }
    };

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        super.start();
        openSocket();
    }

    private void openSocket() {
        try {
            s = new Socket(propHostname, propPort);
            out = new PrintWriter(s.getOutputStream());
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            reader = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        StringBuilder sb = new StringBuilder();
                        while (true) {
                            if (in.ready()) {
                                char c = (char) in.read();
                                sb.append(c);
                                System.out.println("Received char: " + c);
                                if (c == '\n') {
                                    opAction.sendData(ConversionUtils.stringToBytes(sb.toString().trim()));
                                    sb = new StringBuilder();
                                }
                            } else {
                                Thread.sleep(50);
                            }
                        }
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            });
            reader.start();
        } catch (UnknownHostException ue) {
            ue.printStackTrace();
            AstericsErrorHandling.instance.reportError(this,
                    "Yaak: Could not find host: " + propHostname + ":" + propPort);
        } catch (IOException io) {
            AstericsErrorHandling.instance.reportError(this,
                    "Yaak: Error opening socket for host: " + propHostname + ":" + propPort);
            io.printStackTrace();
        }
    }

    private void closeSocket() {
        try {
            if (out != null) {
                out.close();
            }
            if (reader != null) {
                reader.interrupt();
            }
            if (in != null) {
                in.close();
            }
            if (s != null) {
                s.close();
            }
        } catch (IOException io) {
            AstericsErrorHandling.instance.reportError(this, "Yaak: Error closing the socket");
            io.printStackTrace();
        }
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
        closeSocket();
    }
}