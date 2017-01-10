
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

package eu.asterics.component.sensor.lipmouse;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import eu.asterics.mw.cimcommunication.CIMEvent;
import eu.asterics.mw.cimcommunication.CIMEventHandler;
import eu.asterics.mw.cimcommunication.CIMEventPacketReceived;
import eu.asterics.mw.cimcommunication.CIMPortController;
import eu.asterics.mw.cimcommunication.CIMPortManager;
import eu.asterics.mw.cimcommunication.CIMProtocolPacket;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * 
 * Interface to the Lipmouse module. Based on ArduinoInstance.java developed by
 * Chris Veigl
 * 
 * 
 * 
 * @author Alberto Ibanez [alberto_21_9@hotmail.com] Date: 07.01.2014 Time:
 *         17:50
 */
public class LipmouseInstance extends AbstractRuntimeComponentInstance implements CIMEventHandler {
    private final short LIPMOUSE_CIM_ID = (short) 0xa401;
    private static final short LIPMOUSE_CIM_FEATURE_UNIQUENUMBER = 0x0000;
    private static final short LIPMOUSE_CIM_FEATURE_SET_ADCPERIOD = 0x0001;
    private static final short LIPMOUSE_CIM_FEATURE_ADCREPORT = 0x0002;
    private static final short LIPMOUSE_CIM_FEATURE_BUTTONREPORT = 0x0003;
    private static final short LIPMOUSE_CIM_FEATURE_SETLEDS = 0x0004;

    final IRuntimeOutputPort opX = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opY = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opPressure = new DefaultRuntimeOutputPort();

    final IRuntimeEventTriggererPort etpSip = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpLongSip = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpPuff = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpLongPuff = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpSipStart = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpSipEnd = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpPuffStart = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpPuffEnd = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpButton1Pressed = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpButton1Released = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpButton2Pressed = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpButton2Released = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpButton3Pressed = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpButton3Released = new DefaultRuntimeEventTriggererPort();
    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    public int propPeriodicADCUpdate = 50;
    private String propUniqueID = "not used";
    public int propSipThreshold = 505;
    public int propSipTime = 700;
    public int propPuffThreshold = 520;
    public int propPuffTime = 700;

    private int calibX = 0;
    private int calibY = 0;
    private int ledState = 1;
    private boolean calibNow = false;

    // declare member variables here

    private CIMPortController port = null;

    /**
     * The class constructor.
     */
    public LipmouseInstance() {
        // System.out.println("Lipmouse instance created");
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
        if ("x".equalsIgnoreCase(portID)) {
            return opX;
        }
        if ("y".equalsIgnoreCase(portID)) {
            return opY;
        }
        if ("pressure".equalsIgnoreCase(portID)) {
            return opPressure;
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
        if ("calibration".equalsIgnoreCase(eventPortID)) {
            return elpCalibration;
        }
        if ("start".equalsIgnoreCase(eventPortID)) {
            return elpStart;
        }
        if ("stop".equalsIgnoreCase(eventPortID)) {
            return elpStop;
        }
        if ("setLed1".equalsIgnoreCase(eventPortID)) {
            return elpSetLed1;
        }
        if ("clearLed1".equalsIgnoreCase(eventPortID)) {
            return elpClearLed1;
        }
        if ("setLed2".equalsIgnoreCase(eventPortID)) {
            return elpSetLed2;
        }
        if ("clearLed2".equalsIgnoreCase(eventPortID)) {
            return elpClearLed2;
        }
        if ("setLed3".equalsIgnoreCase(eventPortID)) {
            return elpSetLed3;
        }
        if ("clearLed3".equalsIgnoreCase(eventPortID)) {
            return elpClearLed3;
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
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) // It
                                                                                // doesn't
                                                                                // have
                                                                                // event
                                                                                // trigger
                                                                                // ports,
                                                                                // it
                                                                                // can
                                                                                // be
                                                                                // deleted
    {
        if ("sip".equalsIgnoreCase(eventPortID)) {
            return etpSip;
        }
        if ("longSip".equalsIgnoreCase(eventPortID)) {
            return etpLongSip;
        }
        if ("puff".equalsIgnoreCase(eventPortID)) {
            return etpPuff;
        }
        if ("longPuff".equalsIgnoreCase(eventPortID)) {
            return etpLongPuff;
        }
        if ("sipStart".equalsIgnoreCase(eventPortID)) {
            return etpSipStart;
        }
        if ("sipEnd".equalsIgnoreCase(eventPortID)) {
            return etpSipEnd;
        }
        if ("puffStart".equalsIgnoreCase(eventPortID)) {
            return etpPuffStart;
        }
        if ("puffEnd".equalsIgnoreCase(eventPortID)) {
            return etpPuffEnd;
        }
        if ("button1Pressed".equalsIgnoreCase(eventPortID)) {
            return etpButton1Pressed;
        }
        if ("button1Released".equalsIgnoreCase(eventPortID)) {
            return etpButton1Released;
        }
        if ("button2Pressed".equalsIgnoreCase(eventPortID)) {
            return etpButton2Pressed;
        }
        if ("button2Released".equalsIgnoreCase(eventPortID)) {
            return etpButton2Released;
        }
        if ("button3Pressed".equalsIgnoreCase(eventPortID)) {
            return etpButton3Pressed;
        }
        if ("button3Released".equalsIgnoreCase(eventPortID)) {
            return etpButton3Released;
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
        if ("periodicADCUpdate".equalsIgnoreCase(propertyName)) {
            return propPeriodicADCUpdate;
        }
        if ("uniqueID".equalsIgnoreCase(propertyName)) {
            return propUniqueID;
        }
        if ("sipThreshold".equalsIgnoreCase(propertyName)) {
            return propSipThreshold;
        }
        if ("sipTime".equalsIgnoreCase(propertyName)) {
            return propSipTime;
        }
        if ("puffThreshold".equalsIgnoreCase(propertyName)) {
            return propPuffThreshold;
        }
        if ("puffTime".equalsIgnoreCase(propertyName)) {
            return propPuffTime;
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
        if ("periodicADCUpdate".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propPeriodicADCUpdate;
            propPeriodicADCUpdate = Integer.parseInt(newValue.toString());
            if (port != null) {
                sendLipmouseWriteFeature(LIPMOUSE_CIM_FEATURE_SET_ADCPERIOD, propPeriodicADCUpdate);
            }
            return oldValue;
        }
        if ("sipThreshold".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propSipThreshold;
            propSipThreshold = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("sipTime".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propSipTime;
            propSipTime = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("puffThreshold".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propPuffThreshold;
            propPuffThreshold = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("puffTime".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propPuffTime;
            propPuffTime = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("uniqueID".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propUniqueID;
            propUniqueID = (String) newValue;
            if (port != null) {
                // System.out.println("SET UNIQUE NUMBER TO NEW VALUE !!!!");
                // port= openCIM (LIPMOUSE_CIM_ID, propUniqueID);
            }
            return oldValue;
        }
        return null;
    }

    public CIMPortController openCIM(short CIMID, String uniqueID) {
        if ("not used".equalsIgnoreCase(propUniqueID) || (propUniqueID == "")) {
            return (CIMPortManager.getInstance().getConnection(LIPMOUSE_CIM_ID));
        } else {
            Long id;
            try {
                id = Long.parseLong(propUniqueID);
                return (CIMPortManager.getInstance().getConnection(LIPMOUSE_CIM_ID, id));
            } catch (Exception e) {
                return (null);
            }
        }
    }

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpCalibration = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            // sendLipmouseWriteFeatureByte(LIPMOUSE_CIM_FEATURE_CALIBRATION,0);
            // // calib in firmware currently not supported
            calibNow = true;

        }
    };

    final IRuntimeEventListenerPort elpStart = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            if (port != null) {
                CIMPortManager.getInstance().sendPacket(port, null, (short) 0,
                        CIMProtocolPacket.COMMAND_REQUEST_START_CIM, false);
            }
        }
    };

    final IRuntimeEventListenerPort elpStop = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            if (port != null) {
                CIMPortManager.getInstance().sendPacket(port, null, (short) 0,
                        CIMProtocolPacket.COMMAND_REQUEST_STOP_CIM, false);
            }
        }
    };

    final IRuntimeEventListenerPort elpSetLed1 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            ledState |= 1;
            sendLipmouseWriteFeatureByte(LIPMOUSE_CIM_FEATURE_SETLEDS, ledState);
        }
    };
    final IRuntimeEventListenerPort elpClearLed1 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            ledState &= (~1);
            sendLipmouseWriteFeatureByte(LIPMOUSE_CIM_FEATURE_SETLEDS, ledState);
        }
    };

    final IRuntimeEventListenerPort elpSetLed2 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            ledState |= 2;
            sendLipmouseWriteFeatureByte(LIPMOUSE_CIM_FEATURE_SETLEDS, ledState);
        }
    };
    final IRuntimeEventListenerPort elpClearLed2 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            ledState &= (~2);
            sendLipmouseWriteFeatureByte(LIPMOUSE_CIM_FEATURE_SETLEDS, ledState);
        }
    };
    final IRuntimeEventListenerPort elpSetLed3 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            ledState |= 4;
            sendLipmouseWriteFeatureByte(LIPMOUSE_CIM_FEATURE_SETLEDS, ledState);
        }
    };
    final IRuntimeEventListenerPort elpClearLed3 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            ledState &= (~4);
            sendLipmouseWriteFeatureByte(LIPMOUSE_CIM_FEATURE_SETLEDS, ledState);
        }
    };

    /**
     * Handles an input packet from Lipmouse CIM. Reads the values of all ADC
     * channels and sends the data to the corresponding output ports
     * 
     * @param packet
     *            the incoming packet
     */

    static int pressure = -1;
    static int oldPressure = -1;
    static long sipStartTime = 0;
    static long sipTime = 0;
    static long puffStartTime = 0;
    static long puffTime = 0;

    private void handleLipmouseAdcReport(CIMProtocolPacket packet) {
        // System.out.println("handleLipmouseAdcPacket");
        byte[] b = packet.getData();
        int x, y;

        x = ConversionUtils.intFromBytes(ADCDataToBytes(b[0], b[1]));
        y = ConversionUtils.intFromBytes(ADCDataToBytes(b[2], b[3]));

        if (calibNow == true) {
            calibX = x;
            calibY = y;
            calibNow = false;
        }

        opX.sendData(ConversionUtils.intToBytes(x - calibX));
        opY.sendData(ConversionUtils.intToBytes(y - calibY));

        opPressure.sendData(ADCDataToBytes(b[4], b[5]));

        pressure = ConversionUtils.intFromBytes(ADCDataToBytes(b[4], b[5]));
        // System.out.println("pressure="+pressure);

        if (oldPressure != -1) {
            if ((oldPressure > propSipThreshold) && (pressure <= propSipThreshold)) {
                sipStartTime = System.currentTimeMillis();
                etpSipStart.raiseEvent();
            } else if ((oldPressure <= propSipThreshold) && (pressure > propSipThreshold)) {
                sipTime = System.currentTimeMillis() - sipStartTime;
                etpSipEnd.raiseEvent();
                if (sipTime >= propSipTime) {
                    etpLongSip.raiseEvent();
                } else {
                    etpSip.raiseEvent();
                }
            }

            if ((oldPressure < propPuffThreshold) && (pressure >= propPuffThreshold)) {
                puffStartTime = System.currentTimeMillis();
                etpPuffStart.raiseEvent();
            } else if ((oldPressure > propPuffThreshold) && (pressure <= propPuffThreshold)) {
                puffTime = System.currentTimeMillis() - puffStartTime;
                etpPuffEnd.raiseEvent();
                if (puffTime >= propPuffTime) {
                    etpLongPuff.raiseEvent();
                } else {
                    etpPuff.raiseEvent();
                }
            }
        }
        oldPressure = pressure;
    }

    static int button1State = 0;
    static int button2State = 0;
    static int button3State = 0;

    private void handleLipmouseButtonReport(CIMProtocolPacket packet) {
        System.out.println("handleLipmouseButtonPacket");
        byte[] b = packet.getData();

        // System.out.println("buttonstate="+ConversionUtils.intFromBytes(ADCDataToBytes(b[0],(byte)0)));

        if ((button1State == 0) && ((b[0] & 1) != 0)) {
            etpButton1Pressed.raiseEvent();
            button1State = 1;
        } else if ((button1State == 1) && ((b[0] & 1) == 0)) {
            etpButton1Released.raiseEvent();
            button1State = 0;
        }

        if ((button2State == 0) && ((b[0] & 2) != 0)) {
            etpButton2Pressed.raiseEvent();
            button2State = 1;
        } else if ((button2State == 1) && ((b[0] & 2) == 0)) {
            etpButton2Released.raiseEvent();
            button2State = 0;
        }

        if ((button3State == 0) && ((b[0] & 4) != 0)) {
            etpButton3Pressed.raiseEvent();
            button3State = 1;
        } else if ((button3State == 1) && ((b[0] & 4) == 0)) {
            etpButton3Released.raiseEvent();
            button3State = 0;
        }

    }

    // This is a function to convert the data sent by the microcontroller to the
    // format that the
    // output port send it through the channel
    private byte[] ADCDataToBytes(byte first, byte second) {
        if ((second & 80) == 0) // If the number is positive, i.e, if 8th bit is
                                // 0
        {
            return new byte[] { (byte) (0x00), (byte) (0x00), second, first };
        } else { // If the number is negative, i.e, if 8th bit is 1
            return new byte[] { (byte) (0xff), (byte) (0xff), second, first };
        }

    }

    private void handleLipmouseUniqueNumber(CIMProtocolPacket packet) {
        packet.getData();
    }

    /**
     * Called by port controller if new packet has been received
     */
    @Override
    public void handlePacketReceived(CIMEvent e) {
        short featureAddress = 0;
        // System.out.println ("LipmouseCIM handlePacketReceived:");
        CIMEventPacketReceived ev = (CIMEventPacketReceived) e;
        CIMProtocolPacket packet = ev.packet;
        featureAddress = packet.getFeatureAddress();
        switch (packet.getRequestReplyCode()) {
        case CIMProtocolPacket.COMMAND_REPLY_START_CIM:
            // System.out.println ("Reply Start.");
            break;
        case CIMProtocolPacket.COMMAND_REPLY_STOP_CIM:
            // System.out.println ("Reply Stop.");
            break;
        case CIMProtocolPacket.COMMAND_REPLY_RESET_CIM:
            // System.out.println ("Reply Reset.");
            break;
        case CIMProtocolPacket.COMMAND_REPLY_READ_FEATURE:
            // System.out.print ("Reply Read: ");
            if (featureAddress == LIPMOUSE_CIM_FEATURE_UNIQUENUMBER) {
                // System.out.println ("UniqueNumber");
                handleLipmouseUniqueNumber(packet);
            } else if (featureAddress == LIPMOUSE_CIM_FEATURE_ADCREPORT) {
                // System.out.println ("ADCReport.");
                handleLipmouseAdcReport(packet);
            } else if (featureAddress == LIPMOUSE_CIM_FEATURE_BUTTONREPORT) {
                // System.out.println ("Incoming Event: ADCReport
                // "+(128+(int)packet.getSerialNumber()));
                handleLipmouseButtonReport(packet);
            }

            break;

        case CIMProtocolPacket.COMMAND_EVENT_REPLY:
            if (featureAddress == LIPMOUSE_CIM_FEATURE_ADCREPORT) {
                // System.out.println ("Incoming Event: ADCReport
                // "+(128+(int)packet.getSerialNumber()));
                handleLipmouseAdcReport(packet);
            } else if (featureAddress == LIPMOUSE_CIM_FEATURE_BUTTONREPORT) {
                // System.out.println ("Incoming Event: ADCReport
                // "+(128+(int)packet.getSerialNumber()));
                handleLipmouseButtonReport(packet);
            }

            break;
        case CIMProtocolPacket.COMMAND_REPLY_WRITE_FEATURE:
            break;
        }
    }

    /**
     * Called upon faulty packet reception
     */
    @Override
    public void handlePacketError(CIMEvent e) {
        AstericsErrorHandling.instance.reportInfo(this, "Faulty packet received");
    }

    /**
     * Returns the unique ID
     */
    @Override
    public List<String> getRuntimePropertyList(String key) {
        List<String> res = new ArrayList<String>();
        if (key.compareToIgnoreCase("uniqueID") == 0) {
            res.add("not used");
            Vector<Long> ids;
            ids = CIMPortManager.getInstance().getUniqueIdentifiersofCIMs(LIPMOUSE_CIM_ID);
            if (ids != null) {
                for (Long l : ids) {
                    res.add(l.toString());
                    // System.out.println(" found unique ID: "+l.toString());
                }
            }
        }
        return res;
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        oldPressure = -1;

        if (port != null) {
            CIMPortManager.getInstance().sendPacket(port, null, (short) 0, CIMProtocolPacket.COMMAND_REQUEST_STOP_CIM,
                    false);
            port.removeEventListener(this);
            port = null;
        }

        port = openCIM(LIPMOUSE_CIM_ID, propUniqueID);

        if (port != null) {
            port.addEventListener(this);
            CIMPortManager.getInstance().sendPacket(port, null, (short) 0, CIMProtocolPacket.COMMAND_REQUEST_START_CIM,
                    false);
            sendLipmouseWriteFeature(LIPMOUSE_CIM_FEATURE_SET_ADCPERIOD, propPeriodicADCUpdate);
        } else {
            AstericsErrorHandling.instance.reportError(this, "Could not find LipMouse Module (ID " + propUniqueID
                    + "). Please verify that the Module is connected to an USB Port and that the driver is installed.");
        }

        super.start();
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        if (port != null) {
            CIMPortManager.getInstance().sendPacket(port, null, (short) 0, CIMProtocolPacket.COMMAND_REQUEST_STOP_CIM,
                    false);
        }
        super.pause();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        if (port != null) {
            CIMPortManager.getInstance().sendPacket(port, null, (short) 0, CIMProtocolPacket.COMMAND_REQUEST_START_CIM,
                    false);
        }
        super.resume();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        if (port != null) {
            CIMPortManager.getInstance().sendPacket(port, null, (short) 0, CIMProtocolPacket.COMMAND_REQUEST_STOP_CIM,
                    false);
            port.removeEventListener(this);
            port = null;
        }
        super.stop();
    }

    synchronized private final void sendLipmouseWriteFeature(short feature, int value) {
        // send packet
        byte[] b = new byte[2];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) ((value >> 8) & 0xff);

        if (port != null) {
            // System.out.println("sending lipmouse-packet !");
            CIMPortManager.getInstance().sendPacket(port, b, feature, CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE,
                    false);
        }
    }

    synchronized private final void sendLipmouseWriteFeatureByte(short feature, int value) {
        // send packet
        byte[] b = new byte[1];
        b[0] = (byte) (value & 0xff);

        if (port != null) {
            CIMPortManager.getInstance().sendPacket(port, b, feature, CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE,
                    false);
        }
    }
}