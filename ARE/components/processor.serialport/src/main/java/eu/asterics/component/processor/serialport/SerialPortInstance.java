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
import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.asterics.mw.cimcommunication.CIMPortController;
import eu.asterics.mw.cimcommunication.CIMPortManager;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.*;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

//import eu.asterics.component.sensor.acceleration.AccelerationInstance.OutputPort;

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
    private final OutputPort opReceivedBytes = new OutputPort();
    private final OutputPort opPortStatus = new OutputPort();
    private final Logger logger = AstericsErrorHandling.instance.getLogger();

    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    private final static String IN_PORT_RESCAN = "IN_PORT_RESCAN";
    private final static String NEW_PORT_RESCAN = "NEW_PORT_RESCAN";

    private InputStream in = null;
    private OutputStream out = null;
    private Thread readerThread = null;
    private volatile boolean running = false;
    private String propComPort = "COM4";
    private int propBaudRate = 9600;
    private int propSendStringTerminator = 0;
    private int propReceiveStringTerminator = 0;
    private int propSendBytesBufferSize=1;
    private Short propCimId = null;
    
    private ByteBuffer sendBytesBuffer=ByteBuffer.allocate(propSendBytesBufferSize);
    
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
        } else if ("sendBytes".equalsIgnoreCase(portID)) {
            return ipSendBytes;
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
        } else if ("receivedBytes".equalsIgnoreCase(portID)) {
            return opReceivedBytes;
        } else if ("opPortStatus".equalsIgnoreCase(portID)) {
            return opPortStatus;
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
        if ("sendBytesBufferSize".equalsIgnoreCase(propertyName)) {
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
        if ("sendBytesBufferSize".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propSendBytesBufferSize;
            propSendBytesBufferSize = Integer.parseInt(newValue.toString());
            sendBytesBuffer=ByteBuffer.allocate(propSendBytesBufferSize);
            return oldValue;
        }
        if ("cimId".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propCimId;
            if(newValue != null) {
                String s = newValue.toString().trim().toLowerCase();
                if (s.startsWith("0x")) {
                    s = s.substring(2);
                    try {
                        propCimId = (short) Integer.parseInt(s, 16);
                    } catch (NumberFormatException e) {
                        logger.warning("could not format cimId, value is: " + s);
                    }
                }
            }
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
            if(out == null) {
                init();
            }
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
                    AstericsErrorHandling.instance.getLogger().warning("Sending data to serial port <"+propComPort+"> failed, reason: "+e.getMessage());
                }
                ;

            }
        }
    };
    
    /**
     * Input Ports for receiving values.
     */
    private final IRuntimeInputPort ipSendBytes = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            // stringBuffer.append(new String(data));
            // opActResult.sendData
            // (ConversionUtils.stringToBytes(stringBuffer.toString()));
            if(out == null) {
                init();
            }
            if (out != null && data != null) {
                try {
                    if(sendBytesBuffer.hasRemaining()) {
                        sendBytesBuffer.put(data);
                    } 
                    if(!sendBytesBuffer.hasRemaining() && sendBytesBuffer.hasArray()) {
                        //If the buffer is full, send the data and then clear the buffer again.
                        byte[] sendBytes=sendBytesBuffer.array();
                        if(AstericsErrorHandling.instance.getLogger().isLoggable(Level.FINE)) {
                            AstericsErrorHandling.instance.getLogger().fine("Sending data to serial port <"+propComPort+">, data: "+Arrays.toString(sendBytes));
                        }
                        
                        out.write(sendBytes);                        
                        sendBytesBuffer.clear();
                    } 
                } catch (Exception e) {
                    AstericsErrorHandling.instance.getLogger().warning("Sending data to serial port <"+propComPort+"> failed, reason: "+e.getMessage());
                }
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

        //First directly send the byte to the receivedBytes output port.
        opReceivedBytes.sendData(ConversionUtils.byteToBytes(actbyte));

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
        init();
        super.start();
    }

    private void init() {

        received = "";
        initComPort();
        if (portController == null) {
            AstericsErrorHandling.instance.reportError(this,
                    "SerialPort-plugin: Could not construct raw port controller, please verify that the COM port is valid.");
            return;
        }
        in = portController.getInputStream();
        out = portController.getOutputStream();
        readerThread = new Thread(new Runnable() {

            @Override
            public void run() {
                running = true;
                try {
                    while (running) {
                        while (in.available() > 0) {
                            handlePacketReceived((byte) in.read());
                        }
                        Thread.sleep(10);
                    }
                } catch (IOException | InterruptedException e) {
                    logger.log(Level.WARNING, "Exception in polling data in SerialPort module.");
                    closeAll();
                }
                logger.log(Level.FINE, "SerialPort module: stopped reading thread.");
            }
        });
        readerThread.start();
        logger.log(Level.FINE, "SerialPort module: started reading thread.");

    }

    private void initComPort() {
        //Sanity check: if portController != null stop the connection first.
        if (portController != null) {
            stop();
        }

        if (propCimId != null) { //mode with given CIM id
            initComPortByCimID();
        } else { //mode with given COM Port
            portController = CIMPortManager.getInstance().getRawConnection(propComPort, propBaudRate, true);
        }
    }

    private void initComPortByCimID() {
        if(CIMPortManager.getInstance().inRescan()) {
            logger.info("do not try open COM port by CIM id, because currently rescanning");
            opPortStatus.sendData(IN_PORT_RESCAN.getBytes());
            return;
        }

        propComPort = CIMPortManager.getInstance().getCOMPortByCIMId(propCimId);
        boolean foundComPort = propComPort != null && !propComPort.isEmpty();
        if (foundComPort) {
            logger.info(MessageFormat.format("Opening device with cimID <{0}> on COM Port <{1}>", Integer.toHexString(propCimId), propComPort));
            portController = CIMPortManager.getInstance().getRawConnection(propCimId, propBaudRate);
        }
        if(portController == null) {
            logger.info(MessageFormat.format("could not find or open COM port for CIM id {0}. starting rescan...", propCimId));
            CIMPortManager.getInstance().rescan();
            opPortStatus.sendData(NEW_PORT_RESCAN.getBytes());
        }
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        stopReaderThread();
        closeAll();
        running = false;
        super.pause();

    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        init();
        super.resume();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        super.stop();
        stopReaderThread();
        closeAll();
        running = false;
    }

    private void stopReaderThread() {
        running = false;
        if(readerThread != null) {
            readerThread.interrupt();
            readerThread = null;
        }        
    }
    
    private synchronized void closeAll() {
        if (portController != null) {
            CIMPortManager.getInstance().closeRawConnection(propComPort);
            portController.closePort();
            try {
                if(out != null) out.close();
                if(in != null) in.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, "error closing streams in SerialPortInstance", e);
            }
            out = null;
            in = null;
            portController = null;
        }
        AstericsErrorHandling.instance.reportInfo(this, "SerialPort connection closed");
    }
}