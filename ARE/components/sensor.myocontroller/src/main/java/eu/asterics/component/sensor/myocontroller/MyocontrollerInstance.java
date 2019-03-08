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

package eu.asterics.component.sensor.myocontroller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import eu.asterics.mw.cimcommunication.CIMPortController;
import eu.asterics.mw.cimcommunication.CIMPortManager;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.*;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.ResourceRegistry;

/**
 * Interfaces with the Myocontroller data acquisition device via bluetooth / Com port
 *
 * @author Chris
 */
public class MyocontrollerInstance extends AbstractRuntimeComponentInstance {
    private static final int NUMBER_OF_CHANNELS = 6;
    private static final String OP_CH_PREFIX = "Channel";
    private static final IRuntimeOutputPort[] opChannels = new DefaultRuntimeOutputPort[NUMBER_OF_CHANNELS];

    String propComPort = "COM4";
    int propBaudRate = 57600;
    boolean propUseFile = false;
    String propFilename = "";

    // declare member variables here
    private InputStream in = null;
    private Thread readerThread = null;
    private boolean running = false;
    private CIMPortController portController = null;
    private int parseState = 0;
    private int parsedChannel = 0;
    private int parsedValue = 0;

    /**
     * The class constructor.
     */
    public MyocontrollerInstance() {
        for (int i = 0; i < NUMBER_OF_CHANNELS; i++)
            opChannels[i] = new DefaultRuntimeOutputPort();
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
        String s;
        for (int i = 0; i < NUMBER_OF_CHANNELS; i++) {
            s = OP_CH_PREFIX + (i + 1);
            if (s.equalsIgnoreCase(portID)) {
                return opChannels[i];
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
        if ("useFile".equalsIgnoreCase(propertyName)) {
            return propUseFile;
        }
        if ("filename".equalsIgnoreCase(propertyName)) {
            return propFilename;
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
        if ("useFile".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propUseFile;
            propUseFile = Boolean.parseBoolean(newValue.toString());
            return oldValue;
        }
        if ("filename".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propFilename;
            propFilename = newValue.toString();
            return oldValue;
        }

        return null;
    }

    /**
     * method using to parse the arriving data (either from COM-port or from file)
     * 
     * @param data
     *            the last received data byte
     */
    private void handlePacketReceived(byte data) {

        switch (parseState) {
        case 0:
            parseState = data == 'S' ? parseState + 1 : 0;
            break;
        case 1:
            parseState = data == 'T' ? parseState + 1 : 0;
            break;
        case 2:
            parseState = data == 'A' ? parseState + 1 : 0;
            break;
        case 3:
            parseState = data == 0x0c ? parseState + 1 : 0; // discard the shorter (8) packages!
            break;
        case 13:
            parsedChannel = (int) data;
            parseState++;
            break;
        case 15:
            parsedValue = ((int) data) & 0xff;
            parseState++;
            break;
        case 16:
            parsedValue |= ((((int) data) & 0xff) << 8);
            if (parsedChannel < NUMBER_OF_CHANNELS) {
                opChannels[parsedChannel].sendData(ConversionUtils.doubleToBytes((float) parsedValue));
            }
            parseState = 0; // done ! look for next packet !
            break;
        default:
            parseState++;
        }
    }

    private InputStream getFileInputStream() {
        try {
            return ResourceRegistry.getInstance().getResourceInputStream(propFilename, ResourceRegistry.RES_TYPE.DATA);
        } catch (IOException | URISyntaxException e) {
            AstericsErrorHandling.instance.getLogger().info("Error reading archive file  " + propFilename);
        }
        return null;
    }

    private InputStream getComportInputStream() {
        portController = CIMPortManager.getInstance().getRawConnection(propComPort, propBaudRate, true);
        if (portController == null) {
            AstericsErrorHandling.instance.reportError(this,
                    "Myocontroller: Could not construct raw port controller, please verify that the COM port is valid.");
            return null;
        }
        return portController.getInputStream();
    }

    private void startInternal() {
        if (!propUseFile) {
            in = getComportInputStream();
        } else {
            in = getFileInputStream();
        }
        if (in == null) {
            return;
        }

        readerThread = new Thread(new Runnable() {

            @Override
            public void run() {
                running = true;
                while (running) {
                    try {
                        if (!propUseFile) { // COM-port
                            while (in.available() > 0) {
                                handlePacketReceived((byte) in.read());
                            }
                            Thread.sleep(10);
                        } else { // Archive File
                            for (int z = 0; z < 20; z++) {
                                handlePacketReceived((byte) in.read());
                            }
                            Thread.sleep(2); // gives about 500 packets / second
                        }
                    } catch (IOException e) {
                        String msg = String.format("MyoController: Could not read from %s.", propUseFile ? "file" : "COM-port");
                        AstericsErrorHandling.instance.getLogger().info(msg);
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
        readerThread.start();
        super.start();
    }

    private void stopInternal() {
        running = false;
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                AstericsErrorHandling.instance.reportError(this, "Myocontroller: error closing inputStream");
            }
            in = null;
        }
        if (portController != null) {
            CIMPortManager.getInstance().closeRawConnection(propComPort);
            portController = null;
            AstericsErrorHandling.instance.reportInfo(this, "Myocontroller raw port controller closed");
        }
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        startInternal();
        super.start();
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        stopInternal();
        super.pause();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        startInternal();
        super.resume();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        stopInternal();
        super.stop();
    }
}