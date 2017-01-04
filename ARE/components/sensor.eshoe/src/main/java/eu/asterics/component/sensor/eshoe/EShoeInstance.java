
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

package eu.asterics.component.sensor.eshoe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import eu.asterics.mw.cimcommunication.CIMPortController;
import eu.asterics.mw.cimcommunication.CIMPortManager;
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
public class EShoeInstance extends AbstractRuntimeComponentInstance {
    final IRuntimeOutputPort opAccX = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opAccY = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opAccZ = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opHeel = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opMetaV = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opMetaI = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opToe = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opGyroX = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opGyroY = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opGyroZ = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opAngle = new DefaultRuntimeOutputPort();
    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    int propComPort = 1;
    int propSamplingRate = 50;
    boolean propSDMemory = false;

    // declare member variables here
    private InputStream in = null;
    private OutputStream out = null;
    private boolean running = false;
    CIMPortController portController = null;
    int propBaudRate = 57600;
    int position = 0;
    byte[] array = new byte[24];
    int i = 0;
    // the actual ack of the eShoe:
    // 0 = Unknown Command
    // 2 = Start
    // 3 = Stop
    // 4 = Set_Clock
    // 6 = Set_Config
    // 8 = Set_Threshold
    // 10 = Calibrate Done
    // 12 = Start Error (sdcard -aborted)
    // 22 = Start Warning (sdcard - transmitting)
    // -1 = no ack;
    int ack = -1;

    int length = 0;
    int receivedDataType = 0;

    /**
     * The class constructor.
     */
    public EShoeInstance() {
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
        if ("inputString".equalsIgnoreCase(portID)) {
            return ipInputString;
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
        if ("accX".equalsIgnoreCase(portID)) {
            return opAccX;
        }
        if ("accY".equalsIgnoreCase(portID)) {
            return opAccY;
        }
        if ("accZ".equalsIgnoreCase(portID)) {
            return opAccZ;
        }
        if ("heel".equalsIgnoreCase(portID)) {
            return opHeel;
        }
        if ("metaV".equalsIgnoreCase(portID)) {
            return opMetaV;
        }
        if ("metaI".equalsIgnoreCase(portID)) {
            return opMetaI;
        }
        if ("toe".equalsIgnoreCase(portID)) {
            return opToe;
        }
        if ("gyroX".equalsIgnoreCase(portID)) {
            return opGyroX;
        }
        if ("gyroY".equalsIgnoreCase(portID)) {
            return opGyroY;
        }
        if ("gyroZ".equalsIgnoreCase(portID)) {
            return opGyroZ;
        }
        if ("angle".equalsIgnoreCase(portID)) {
            return opAngle;
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
        if ("calibrate".equalsIgnoreCase(eventPortID)) {
            return elpCalibrate;
        }
        if ("start".equalsIgnoreCase(eventPortID)) {
            return elpStart;
        }
        if ("stop".equalsIgnoreCase(eventPortID)) {
            return elpStop;
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
        if ("comPort".equalsIgnoreCase(propertyName)) {
            return propComPort;
        }
        if ("samplingRate".equalsIgnoreCase(propertyName)) {
            return propSamplingRate;
        }
        if ("sDMemory".equalsIgnoreCase(propertyName)) {
            return propSDMemory;
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
            propComPort = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("samplingRate".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propSamplingRate;
            propSamplingRate = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("sDMemory".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propSDMemory;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propSDMemory = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propSDMemory = false;
            }
            return oldValue;
        }

        return null;
    }

    /**
     * Input Ports for receiving values. Not implemented yet.
     */
    private final IRuntimeInputPort ipInputString = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            // insert data reception handling here, e.g.:
            // myVar = ConversionUtils.doubleFromBytes(data);
            // myVar = ConversionUtils.stringFromBytes(data);
            // myVar = ConversionUtils.intFromBytes(data);
            String value = ConversionUtils.stringFromBytes(data);
            if (value.equals("start")) {
                CalibrateEvent();
                StartEvent();
                SetClockEvent();
            } else if (value.equals("stop")) {
                StopEvent();
                SetClockEvent();
            } else if (value.equals("calibrate")) {
                CalibrateEvent();
            } else {
                AstericsErrorHandling.instance.getLogger().warning(
                        "String: " + value + "is not a correct command! Please type start, strop or calibrate.");
            }
        }

    };

    private void StartEvent() {
        // send Start-Command
        try {
            if (!running) {
                return;
            }

            byte[] message = new byte[7];
            Arrays.fill(message, (byte) 0);
            // STX Type Length UART SDCARD KALMAN-SELECT KALMANT-RESULT
            // 55 02 04 01 0x 01 0x
            message[0] = (byte) 0x55;
            message[1] = (byte) 0x02;
            message[2] = (byte) 0x04;
            message[3] = (byte) 0x01;

            if (propSDMemory) {
                message[4] = 0x01;
            } else {
                message[4] = 0x00;
            }

            message[5] = (byte) 0x01;
            message[6] = 0x00;

            if (propSamplingRate == 0) {
                message[6] = 0x00;
            } else if (propSamplingRate == 1) {
                message[6] = 0x01;
            } else {
                AstericsErrorHandling.instance.reportError(this, "eShoe: Error Starting: Sampling Rate is not set");
                return;
            }
            out.write(message);
        } catch (Exception e) {
            // AstericsErrorHandling.instance.reportError(this,
            // "eShoe: Error Starting");
            return;
        }
    }

    private void StopEvent() {
        // send Start-Command
        try {
            SendCommand((byte) 0x03);
        } catch (Exception e) {
            AstericsErrorHandling.instance.reportError(this, "eShoe: Error Stopping");
            return;
        }
    }

    private void SetClockEvent() {
        // send Start-Command
        try {
            SendCommand((byte) 0x05);
        } catch (Exception e) {
            // AstericsErrorHandling.instance.reportError(this,
            // "eShoe: Error Calibrating");
            return;
        }
    }

    private void CalibrateEvent() {
        // send Start-Command
        try {
            SendCommand((byte) 0x10);
        } catch (Exception e) {
            AstericsErrorHandling.instance.reportError(this, "eShoe: Error Calibrating");
            return;
        }
    }

    private void SendCommand(byte Type) throws Exception {
        // send Start-Command
        try {
            if (!running) {
                return;
            }

            byte[] message = new byte[3];
            Arrays.fill(message, (byte) 0);
            message[0] = (byte) 0x55;
            message[1] = Type;
            message[2] = (byte) 0x00;

            out.write(message);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpCalibrate = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            CalibrateEvent();
        }
    };
    final IRuntimeEventListenerPort elpStart = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            CalibrateEvent();
            StartEvent();
            SetClockEvent();
        }
    };
    final IRuntimeEventListenerPort elpStop = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            StopEvent();
            SetClockEvent();
        }
    };

    /**
     * Parses the output packets in eShoe-Format.
     */
    void parseOutputPacket() {
        try {
            int[] datavalue = new int[11];
            Arrays.fill(datavalue, 0);
            for (int k = 1; k < 12; k++) {
                datavalue[k - 1] = (((int) array[k * 2]) & 0xff) | ((((int) array[k * 2 + 1]) & 0xff) << 8);
                if ((array[k * 2 + 1] & 128) != 0) {
                    datavalue[k - 1] |= 0xffff0000;
                }
            }

            double calc = datavalue[0] / 100;
            opAngle.sendData(ConversionUtils.doubleToBytes(calc));
            calc = datavalue[1] * 4 * 9.81;
            opAccX.sendData(ConversionUtils.doubleToBytes(calc));
            calc = datavalue[2] * 4 * 9.81;
            opAccY.sendData(ConversionUtils.doubleToBytes(calc));
            calc = datavalue[3] * 4 * 9.81;
            opAccZ.sendData(ConversionUtils.doubleToBytes(calc));
            calc = (50.034 * Math.exp(0.0063 * datavalue[4]));
            opHeel.sendData(ConversionUtils.doubleToBytes(calc));
            calc = (50.034 * Math.exp(0.0063 * datavalue[5]));
            opMetaV.sendData(ConversionUtils.doubleToBytes(calc));
            calc = (50.034 * Math.exp(0.0063 * datavalue[6]));
            opMetaI.sendData(ConversionUtils.doubleToBytes(calc));
            calc = (50.034 * Math.exp(0.0063 * datavalue[7]));
            opToe.sendData(ConversionUtils.doubleToBytes(calc));
            calc = datavalue[8] * 0.0696;
            opGyroX.sendData(ConversionUtils.doubleToBytes(calc));
            calc = datavalue[9] * 0.0696;
            opGyroY.sendData(ConversionUtils.doubleToBytes(calc));
            calc = datavalue[10] * 0.0696;
            opGyroZ.sendData(ConversionUtils.doubleToBytes(calc));

            // System.out.println("Read values: Channel1 " + datavalue[0]
            // +",Channel2: " + datavalue[1] +",Channel3: " + datavalue[2]
            // +",Channel4: " + datavalue[3]+",Channel5: " +
            // datavalue[4]+",Channel6: " + datavalue[5]);
        } catch (Exception e) {
            System.out.println("eShoe: Parsing Error");
            // happens when Deque does not contain full packet yet
        }
    }

    /**
     * Parses a ack-packet in eShoe-Format.
     */
    void parseACKPacket() {
        try {
            switch ((0x000000ff & array[0])) {
            case 0x00:
                ack = 0;
                // unknown command
                break;
            case 0x02:
                System.out.println("eShoe is online");
                // AstericsErrorHandling.instance.reportError(this,
                // "eShoe is online");
                ack = 2;
                // start
                break;
            case 0x03:
                ack = 3;
                // stop;
                break;
            case 0x04:
                ack = 4;
                // set clock
                break;
            case 0x06:
                ack = 6;
                // set config
                break;
            case 0x08:
                ack = 8;
                // set threshold
                break;
            case 0x10:
                System.out.println("eShoe is calibrated");
                // AstericsErrorHandling.instance.reportError(this,
                // "eShoe is calibrated");
                ack = 10;
                // calibrate done
                break;
            case 0x12:
                ack = 12;
                // start error - sdcard problem aborted
                break;
            case 0x22:
                ack = 22;
                // start warning - sdcard problem, transmitting
                break;
            }

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
        // +" hex; pos:" + position + "; length: " + length +"; index:" + i );

        switch (position) {
        case 0:
            if ((0x000000ff & data) == 0x55) {
                position = 1;
            }
            break;
        case 1:
            if ((0x000000ff & data) == 0x00000000) {
                receivedDataType = 1;
                position = 2;
            } else if ((0x000000ff & data) == 0x00000ff) {
                receivedDataType = 2;
                position = 2;
            } else {
                position = 0;
            }
            break;
        case 2:
            if ((0x000000ff & data) > 0x00000000) {
                position = 3;
                length = data + 2;
            } else {
                position = 0;
            }
            break;
        default:
            array[i] = data;
            i++;
            if (position == length) {
                if (receivedDataType == 1) {
                    parseACKPacket();
                } else if (receivedDataType == 2) {
                    parseOutputPacket();
                }
                receivedDataType = 0;
                position = 0;
                length = 0;
                i = 0;
                return;
            }
            position++;
        }
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        Arrays.fill(array, (byte) 0);

        portController = CIMPortManager.getInstance().getRawConnection("COM" + propComPort, propBaudRate, true);

        if (portController == null) {
            AstericsErrorHandling.instance.reportError(this,
                    "eShoe: Could not construct raw port controller, please verify that the COM port is valid.");
        } else {
            in = portController.getInputStream();
            out = portController.getOutputStream();
            Thread readThread = new Thread(new Runnable() {
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

            readThread.start();
        }
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