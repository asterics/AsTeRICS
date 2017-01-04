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
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.processor.serialport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import eu.asterics.mw.cimcommunication.CIMPortController;
import eu.asterics.mw.cimcommunication.CIMPortManager;
//import eu.asterics.component.sensor.acceleration.AccelerationInstance.OutputPort;
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
 * <Describe purpose of this module>
 * 
 * 
 * 
 * @author <your name> [<your email address>] Date: Time:
 */
public class SerialPortInstance extends AbstractRuntimeComponentInstance {
    private final OutputPort opReceived = new OutputPort();

    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    private InputStream in = null;
    private OutputStream out = null;
    private Thread readerThread = null;
    private boolean running = false;
    String propComPort = "COM4";
    int propBaudRate = 9600;
    int propSendStringTerminator = 0;
    int propReceiveStringTerminator = 0;

    // declare member variables here
    CIMPortController portController = null;

    /**
     * The class constructor.
     */
    public SerialPortInstance() {
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
        if ("send".equalsIgnoreCase(portID)) {
            return ipSend;
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
        if ("received".equalsIgnoreCase(portID)) {
            return opReceived;
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
        if ("comPort".equalsIgnoreCase(propertyName)) {
            return propComPort;
        }
        if ("baudRate".equalsIgnoreCase(propertyName)) {
            return propBaudRate;
        }
        if ("sendStringTerminator".equalsIgnoreCase(propertyName)) {
            return propSendStringTerminator;
        }
        if ("receiveStringTerminator".equalsIgnoreCase(propertyName)) {
            return propReceiveStringTerminator;
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
        if ("comPort".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propComPort;
            propComPort = newValue.toString();
            return oldValue;
        }
        if ("baudRate".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propBaudRate;
            propBaudRate = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("sendStringTerminator".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propSendStringTerminator;
            propSendStringTerminator = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("receiveStringTerminator".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propReceiveStringTerminator;
            propReceiveStringTerminator = Integer.parseInt(newValue.toString());
            return oldValue;
        }

        return null;
    }

    public class OutputPort extends DefaultRuntimeOutputPort {
        /**
         * Sends data to the connected input port
         * 
         * @param data
         *            a double value to be sent
         * 
         */
        public void sendData(double data) {
            // TODO change this to a more useful conversion
            super.sendData(ConversionUtils.doubleToBytes(data));
        }
    }

    /**
     * Input Ports for receiving values.
     */
    private final IRuntimeInputPort ipSend = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            // stringBuffer.append(new String(data));
            // opActResult.sendData
            // (ConversionUtils.stringToBytes(stringBuffer.toString()));

            if (out != null) {
                try {
                    out.write(data);
                    switch (propSendStringTerminator) {
                    case 0:
                        break;
                    case 1:
                        out.write(13);
                        break;
                    case 2:
                        out.write(10);
                        break;
                    case 3:
                        out.write(13);
                        out.write(10);
                        break;
                    case 4:
                        out.write(0);
                        break;
                    }
                } catch (Exception e) {
                }
                ;

            }
        }
    };

    /**
     * Called by the raw port controller if data is available
     * 
     * @param ev
     *            a CIMEvent which can be ignored as it is only needed due to
     *            the interface specification
     * 
     */

    String received = "";
    boolean endflag = false;

    public void handlePacketReceived(byte actbyte) {
        boolean finished = false;

        // System.out.println("received " + Integer.toHexString(0x000000ff &
        // actbyte) +" hex");

        switch (propSendStringTerminator) {
        case 0:
            received += (char) actbyte;
            finished = true;
            break;
        case 1:
            if (actbyte != 13) {
                received += (char) actbyte;
            } else {
                finished = true;
            }
            break;
        case 2:
            if (actbyte != 10) {
                received += (char) actbyte;
            } else {
                finished = true;
            }
            break;
        case 3:
            if ((actbyte != 13) && (actbyte != 10)) {
                endflag = false;
                received += (char) actbyte;
            } else {
                if (actbyte == 13) {
                    endflag = true;
                }
                if ((endflag == true) && (actbyte == 10)) {
                    endflag = false;
                    finished = true;
                }
            }
            break;
        case 4:
            if (actbyte != 0) {
                received += (char) actbyte;
            } else {
                finished = true;
            }
            break;
        }

        if (finished) {
            opReceived.sendData(ConversionUtils.stringToBytes(received));
            received = "";
            endflag = false;
        }

    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        portController = CIMPortManager.getInstance().getRawConnection(propComPort, propBaudRate, true);
        received = "";
        if (portController == null) {
            AstericsErrorHandling.instance.reportError(this,
                    "SerialPort-plugin: Could not construct raw port controller, please verify that the COM port is valid.");
        } else {
            in = portController.getInputStream();
            out = portController.getOutputStream();
            readerThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    running = true;
                    while (running) {

                        try {
                            if (in.available() > 0) {
                                handlePacketReceived((byte) in.read());
                            } else {
                                Thread.sleep(10);
                            }
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        } catch (IOException io) {
                            io.printStackTrace();
                        }

                    }
                }

            });
            readerThread.start();

        }
        super.start();
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        if (portController != null) {
            CIMPortManager.getInstance().closeRawConnection(propComPort);
            portController = null;
            out = null;
            AstericsErrorHandling.instance.reportInfo(this, "SerialPort controller closed");
        }
        running = false;
        super.pause();

    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        portController = CIMPortManager.getInstance().getRawConnection(propComPort, propBaudRate, true);
        if (portController == null) {
            AstericsErrorHandling.instance.reportError(this,
                    "Could not construct SerialPort controller, please make sure that the COM port is valid.");
        }
        readerThread.start();
        super.resume();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        super.stop();
        if (portController != null) {

            CIMPortManager.getInstance().closeRawConnection(propComPort);
            portController = null;
            AstericsErrorHandling.instance.reportInfo(this, "SerialPort connection closed");
            running = false;
        }
    }
}