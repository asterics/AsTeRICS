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

package eu.asterics.component.sensor.p2_parser;

import java.io.IOException;
import java.io.InputStream;

import eu.asterics.mw.cimcommunication.CIMPortController;
import eu.asterics.mw.cimcommunication.CIMPortManager;
//import eu.asterics.component.sensor.acceleration.AccelerationInstance.OutputPort;
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
 * @author <your name> [<your email address>] Date: Time:
 */
public class P2_parserInstance extends AbstractRuntimeComponentInstance {
    private final OutputPort opChannel1 = new OutputPort();
    private final OutputPort opChannel2 = new OutputPort();
    private final OutputPort opChannel3 = new OutputPort();
    private final OutputPort opChannel4 = new OutputPort();
    private final OutputPort opChannel5 = new OutputPort();
    private final OutputPort opChannel6 = new OutputPort();

    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    private InputStream in = null;
    private Thread readerThread = null;
    private boolean running = false;
    String propComPort = "COM4";
    int propBaudRate = 57600;
    int state = 0;
    byte[] array = new byte[15];
    int[] value = new int[6];
    int i = 0;

    // declare member variables here
    CIMPortController portController = null;

    /**
     * The class constructor.
     */
    public P2_parserInstance() {
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
        if ("channel1".equalsIgnoreCase(portID)) {
            return opChannel1;
        }
        if ("channel2".equalsIgnoreCase(portID)) {
            return opChannel2;
        }
        if ("channel3".equalsIgnoreCase(portID)) {
            return opChannel3;
        }
        if ("channel4".equalsIgnoreCase(portID)) {
            return opChannel4;
        }
        if ("channel5".equalsIgnoreCase(portID)) {
            return opChannel5;
        }
        if ("channel6".equalsIgnoreCase(portID)) {
            return opChannel6;
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
     * Parses a packet in P2-Format.
     */
    void parsePacket() {
        try {
            int x = 2;
            for (int k = 0; k < 6; k++) {
                value[k] = (((int) array[x + 1]) & 0xff) | ((((int) array[x]) & 0xff) << 8);

                // value[k] =(int)( (array[x] << 8) + array[x+1]);
                x++;
                x++;
            }

            // System.out.println("Read values: Channel1 " + value[0]
            // +",Channel2: " + value[1]+",Channel3: " + value[2]+",Channel4: "
            // + value[3]+",Channel5: " + value[4]+",Channel6: " + value[5]);
            opChannel1.sendData(value[0] - 512);
            opChannel2.sendData(value[1] - 512);
            opChannel3.sendData(value[2] - 512);
            opChannel4.sendData(value[3] - 512);
            opChannel5.sendData(value[4] - 512);
            opChannel6.sendData(value[5] - 512);

        } catch (Exception e) {
            // happens when Deque does not contain full packet yet
        }
    }

    /**
     * Called by the raw port controller if data is available
     * 
     * @param ev
     *            a CIMEvent which can be ignored as it is only needed due to
     *            the interface specification
     */
    public void handlePacketReceived(byte data) {
        // System.out.println("val = " + Integer.toHexString(0x000000ff & data)
        // +" hex");

        switch (state) {
        case 0:
            // System.out.println("Got start of start sequence");
            if ((0x000000ff & data) == 0xA5) {
                state = 1;
                // System.out.println("Got A5");
            }
            break;
        case 1:
            if ((0x000000ff & data) == 0x0000005A) {
                state = 2;
                // System.out.println("Got 5A");
            } else {
                state = 0;
            }
            break;
        default:
            // System.out.println("Default"+ state);
            state++;
            array[i] = data;
            i++;
            if (state == 17) {
                parsePacket();
                state = 0;
                i = 0;
            }
        }
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        portController = CIMPortManager.getInstance().getRawConnection(propComPort, propBaudRate, true);

        if (portController == null) {
            AstericsErrorHandling.instance.reportError(this,
                    "P2-plugin: Could not construct raw port controller, please verify that the COM port is valid.");
        } else {
            in = portController.getInputStream();
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
            AstericsErrorHandling.instance.reportInfo(this, "OpenEEG raw port controller closed");
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
                    "Could not construct OpenEEG raw port controller, please make sure that the COM port is valid.");
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
            AstericsErrorHandling.instance.reportInfo(this, "OpenEEG connection closed");
            running = false;
        }
    }
}