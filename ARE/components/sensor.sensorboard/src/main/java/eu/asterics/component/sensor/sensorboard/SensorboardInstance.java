
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

package eu.asterics.component.sensor.sensorboard;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
public class SensorboardInstance extends AbstractRuntimeComponentInstance implements CIMEventHandler {

    private CIMPortController port = null;

    final IRuntimeOutputPort opAccX = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opAccY = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opAccZ = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opGyroX = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opGyroY = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opGyroZ = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opCompassX = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opCompassY = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opCompassZ = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opPt1X = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opPt1Y = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opPt2X = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opPt2Y = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opPt3X = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opPt3Y = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opPt4X = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opPt4Y = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opPressure = new DefaultRuntimeOutputPort();
    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    int propRefreshInterval = 50;
    private final short SENSORBOARD_CIM_ID = (short) 0xa201;
    private static final short SENSORBOARD_FEATURE_ACTIVATE_PERIODIC_VALUE = 0x0001;
    private static final short SENSORBOARD_FEATURE_CHANNEL_VALUE_REPORT = 0x0002;

    // declare member variables here
    ByteBuffer bb = ByteBuffer.allocate(5);

    /**
     * The class constructor.
     */
    public SensorboardInstance() {
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
        if ("accX".equalsIgnoreCase(portID)) {
            return opAccX;
        }
        if ("accY".equalsIgnoreCase(portID)) {
            return opAccY;
        }
        if ("accZ".equalsIgnoreCase(portID)) {
            return opAccZ;
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
        if ("compassX".equalsIgnoreCase(portID)) {
            return opCompassX;
        }
        if ("compassY".equalsIgnoreCase(portID)) {
            return opCompassY;
        }
        if ("compassZ".equalsIgnoreCase(portID)) {
            return opCompassZ;
        }
        if ("pt1X".equalsIgnoreCase(portID)) {
            return opPt1X;
        }
        if ("pt1Y".equalsIgnoreCase(portID)) {
            return opPt1Y;
        }
        if ("pt2X".equalsIgnoreCase(portID)) {
            return opPt2X;
        }
        if ("pt2Y".equalsIgnoreCase(portID)) {
            return opPt2Y;
        }
        if ("pt3X".equalsIgnoreCase(portID)) {
            return opPt3X;
        }
        if ("pt3Y".equalsIgnoreCase(portID)) {
            return opPt3Y;
        }
        if ("pt4X".equalsIgnoreCase(portID)) {
            return opPt4X;
        }
        if ("pt4Y".equalsIgnoreCase(portID)) {
            return opPt4Y;
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
        if ("refreshInterval".equalsIgnoreCase(propertyName)) {
            return propRefreshInterval;
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
        if ("refreshInterval".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propRefreshInterval;
            propRefreshInterval = Integer.parseInt(newValue.toString());
            if (port != null) {
                CIMPortManager.getInstance().sendPacket(port, ConversionUtils.intToBytes(propRefreshInterval),
                        SENSORBOARD_FEATURE_ACTIVATE_PERIODIC_VALUE, CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE,
                        false);
                // Logger.getAnonymousLogger().info("Sent SENSORBOARD activate
                // periodic update");
            }
            return oldValue;
        }

        return null;
    }

    int byteToSignedInt(byte low, byte high) {

        bb.clear();
        bb.order(ByteOrder.LITTLE_ENDIAN);

        bb.put(low);
        bb.put(high);
        return ((int) bb.getShort(0));
    }

    /**
     * Handles an input packet from the SENSORBOARD CIM. Reads the values on all
     * active inputs and sends the data on the corresponding output ports
     * 
     * @param packet
     *            the incoming packet
     */
    private void handleSensorboardInputValuePacket(CIMProtocolPacket packet) {
        // Logger.getAnonymousLogger().info("handleEogInputValuePacket start");
        byte[] b = packet.getData();

        // opAccX.sendData(ConversionUtils.intToBytes( (((short) b[0]) & 0xff) |
        // ((((short) b[1]) & 0xff) << 8)));
        opAccX.sendData(ConversionUtils.intToBytes(byteToSignedInt(b[0], b[1])));

        // opAccY.sendData(ConversionUtils.intToBytes( (((short) b[2]) & 0xff) |
        // ((((short) b[3]) & 0xff) << 8)));
        opAccY.sendData(ConversionUtils.intToBytes(byteToSignedInt(b[2], b[3])));

        // opAccZ.sendData(ConversionUtils.intToBytes( (((short) b[4]) & 0xff) |
        // ((((short) b[5]) & 0xff) << 8)));
        opAccZ.sendData(ConversionUtils.intToBytes(byteToSignedInt(b[4], b[5])));

        opGyroX.sendData(ConversionUtils.intToBytes(byteToSignedInt(b[6], b[7])));
        opGyroY.sendData(ConversionUtils.intToBytes(byteToSignedInt(b[8], b[9])));
        opGyroZ.sendData(ConversionUtils.intToBytes(byteToSignedInt(b[10], b[11])));

        opCompassX.sendData(ConversionUtils.intToBytes(byteToSignedInt(b[12], b[13])));
        opCompassY.sendData(ConversionUtils.intToBytes(byteToSignedInt(b[14], b[15])));
        opCompassZ.sendData(ConversionUtils.intToBytes(byteToSignedInt(b[16], b[17])));

        opPt1X.sendData(ConversionUtils.intToBytes((((int) b[18]) & 0xff) | ((((int) b[19]) & 0xff) << 8)));
        opPt1Y.sendData(ConversionUtils.intToBytes((((int) b[20]) & 0xff) | ((((int) b[21]) & 0xff) << 8)));
        opPt2X.sendData(ConversionUtils.intToBytes((((int) b[22]) & 0xff) | ((((int) b[23]) & 0xff) << 8)));
        opPt2Y.sendData(ConversionUtils.intToBytes((((int) b[24]) & 0xff) | ((((int) b[25]) & 0xff) << 8)));
        opPt3X.sendData(ConversionUtils.intToBytes((((int) b[26]) & 0xff) | ((((int) b[27]) & 0xff) << 8)));
        opPt3Y.sendData(ConversionUtils.intToBytes((((int) b[28]) & 0xff) | ((((int) b[29]) & 0xff) << 8)));
        opPt4X.sendData(ConversionUtils.intToBytes((((int) b[30]) & 0xff) | ((((int) b[31]) & 0xff) << 8)));
        opPt4Y.sendData(ConversionUtils.intToBytes((((int) b[32]) & 0xff) | ((((int) b[33]) & 0xff) << 8)));

        opPressure.sendData(ConversionUtils.intToBytes((((int) b[34]) & 0xff))); // |
                                                                                 // ((((int)
                                                                                 // b[35])
                                                                                 // &
                                                                                 // 0xff)
                                                                                 // <<
                                                                                 // 8)));

    }

    /**
     * Called by port controller if new packet has been received
     */
    @Override
    public void handlePacketReceived(CIMEvent e) {
        // Logger.getAnonymousLogger().info("handlePacketReceived start");
        CIMEventPacketReceived ev = (CIMEventPacketReceived) e;
        CIMProtocolPacket packet = ev.packet;
        if ((packet.getRequestReplyCode() == CIMProtocolPacket.COMMAND_EVENT_REPLY)
                && (packet.getFeatureAddress() == SENSORBOARD_FEATURE_CHANNEL_VALUE_REPORT)) {
            handleSensorboardInputValuePacket(packet);
        }
    }

    /**
     * Called upon faulty packet reception
     */
    @Override
    public void handlePacketError(CIMEvent e) {
        AstericsErrorHandling.instance.reportInfo(this, "Faulty packet received");
    }

    synchronized private final void sendWriteFeature(short feature, int value) {
        // send packet
        byte[] b = new byte[2];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) ((value >> 8) & 0xff);

        if (port != null) {
            CIMPortManager.getInstance().sendPacket(port, b, feature, CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE,
                    false);
        }
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        if (port == null) {
            port = CIMPortManager.getInstance().getConnection(SENSORBOARD_CIM_ID);
        }
        if (port != null) {
            port.addEventListener(this);
            // port.sendPacket(null, ARDUINO_CIM_FEATURE_UNIQUENUMBER,
            // CIMProtocolPacket.COMMAND_REQUEST_READ_FEATURE,false);
            sendWriteFeature(SENSORBOARD_FEATURE_ACTIVATE_PERIODIC_VALUE, propRefreshInterval);

            // CIMPortManager.getInstance().sendPacket(port, null, (short) 0,
            // CIMProtocolPacket.COMMAND_REPLY_START_CIM, false);
        } else {
            AstericsErrorHandling.instance.reportError(this,
                    "Could not find Sensorboard, please verify that the Sensorboard is connected to an USB port and correctly installed.");
        }

        super.start();
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        if (port != null) {
            CIMPortManager.getInstance().sendPacket(port, null, (short) 0, CIMProtocolPacket.COMMAND_REPLY_STOP_CIM,
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
            CIMPortManager.getInstance().sendPacket(port, null, (short) 0, CIMProtocolPacket.COMMAND_REPLY_START_CIM,
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
            CIMPortManager.getInstance().sendPacket(port, null, (short) 0, CIMProtocolPacket.COMMAND_REPLY_STOP_CIM,
                    false);
            port.removeEventListener(this);
        }
        super.stop();
    }
}