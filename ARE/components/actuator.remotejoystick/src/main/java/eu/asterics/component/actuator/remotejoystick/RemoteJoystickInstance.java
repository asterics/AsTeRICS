
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

package eu.asterics.component.actuator.remotejoystick;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import eu.asterics.mw.cimcommunication.CIMPortController;
import eu.asterics.mw.cimcommunication.CIMPortManager;
import eu.asterics.mw.cimcommunication.CIMProtocolPacket;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * RemoteJoystickInstance interfaces with the HID actuator dongle to emulate a
 * joystick on a remote PC via USB HID
 * 
 * @author Christoph Veigl [christoph.veigl@technikum-wien.at] Date: Jan 25,
 *         2011 Time: 05:10:30 PM
 */
public class RemoteJoystickInstance extends AbstractRuntimeComponentInstance {
    public final int NUMBER_OF_BUTTONS = 13;

    private final String KEY_PROPERTY_EVENTPRESS = "pressButton";
    private final String KEY_PROPERTY_EVENTRELEASE = "releaseButton";

    // minimum time between two joystick updates
    private static final int DEFAULT_REFRESH_INTERVAL = 50;
    private static final int MIN_REFRESH_INTERVAL = 33;

    // CIM-ID for HID CIM and feature numbers of joystick actions
    private static final short HID_ACTUATOR_CIM_ID = 0x0101;
    private static final short HID_FEATURE_JOYSTICK_UPDATE = 0x20;

    public final PressButtonListener[] elpPressButton = new PressButtonListener[NUMBER_OF_BUTTONS];
    public final ReleaseButtonListener[] elpReleaseButton = new ReleaseButtonListener[NUMBER_OF_BUTTONS];

    private int propRefreshInterval = DEFAULT_REFRESH_INTERVAL;
    private String propUniqueID = "not used";
    private CIMPortController port = null;
    private int joystickX = 128;
    private int joystickY = 128;
    private int joystickZ = 128;
    private int joystickR = 128;
    private int joystickPov = 8;
    int joystickBtn = 0;
    private long lastUpdate = 0;
    private long firstForcedUpdate = 0;
    private int forcedUpdates = 0;
    private final int MAX_FORCED_UPDATES = 5;
    private final int MIN_FORCED_REFRESH_INTERVAL = 33;

    /**
     * Class constructor, initializes Event Ports.
     */
    public RemoteJoystickInstance() {
        for (int i = 0; i < NUMBER_OF_BUTTONS; i++) {
            elpPressButton[i] = new PressButtonListener(i);
            elpReleaseButton[i] = new ReleaseButtonListener(i);
        }
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
        if ("joystickX".equalsIgnoreCase(portID)) {
            return ipJoystickX;
        } else if ("joystickY".equalsIgnoreCase(portID)) {
            return ipJoystickY;
        } else if ("joystickZ".equalsIgnoreCase(portID)) {
            return ipJoystickZ;
        } else if ("joystickR".equalsIgnoreCase(portID)) {
            return ipJoystickR;
        } else if ("joystickPov".equalsIgnoreCase(portID)) {
            return ipJoystickPov;
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
        return null;
    }

    /**
     * returns an Event Listener Port.
     * 
     * @param portID
     *            the name of the port
     * @return the event listener port or null if not found
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {

        if ("reset".equalsIgnoreCase(eventPortID)) {
            return elpReset;
        }

        String s;
        for (int i = 0; i < NUMBER_OF_BUTTONS; i++) {
            s = KEY_PROPERTY_EVENTPRESS + (i + 1);
            if (s.equalsIgnoreCase(eventPortID)) {
                return elpPressButton[i];
            }
            s = KEY_PROPERTY_EVENTRELEASE + (i + 1);
            if (s.equalsIgnoreCase(eventPortID)) {
                return elpReleaseButton[i];
            }
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
        if ("refreshInterval".equalsIgnoreCase(propertyName)) {
            return propRefreshInterval;
        }
        if ("uniqueID".equalsIgnoreCase(propertyName)) {
            return propUniqueID;
        }
        return null;
    }

    /**
     * sets a new value for the given property.
     * 
     * @param propertyName
     *            the name of the property
     * @param newValue
     *            the desired property value
     */
    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        if ("refreshInterval".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propRefreshInterval;
            propRefreshInterval = Integer.parseInt(newValue.toString());
            if (propRefreshInterval < MIN_REFRESH_INTERVAL) {
                propRefreshInterval = MIN_REFRESH_INTERVAL;
            }
            return oldValue;
        }

        if ("uniqueID".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propUniqueID;
            propUniqueID = (String) newValue;
            CIMPortController tempPort = openCIM(HID_ACTUATOR_CIM_ID, propUniqueID);
            if (tempPort != null) {
                port = tempPort;
                if ((!propUniqueID.equals("")) && (!propUniqueID.equals("not used"))) {
                    for (int i = 0; i < 4; i++) {
                        CIMPortManager.getInstance().sendPacket(port, null,
                                CIMProtocolPacket.FEATURE_UNIQUE_SERIAL_NUMBER,
                                CIMProtocolPacket.COMMAND_REQUEST_READ_FEATURE, false);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
            return oldValue;
        }
        return null;
    }

    public CIMPortController openCIM(short CIMID, String uniqueID) {
        if ("not used".equalsIgnoreCase(propUniqueID) || (propUniqueID == "")) {
            return (CIMPortManager.getInstance().getConnection(HID_ACTUATOR_CIM_ID));
        } else {
            Long id;
            try {
                id = Long.parseLong(propUniqueID);
                return (CIMPortManager.getInstance().getConnection(HID_ACTUATOR_CIM_ID, id));
            } catch (Exception e) {
                return (null);
            }
        }
    }

    /**
     * Returns all the filenames inside the path folder data/music and
     * data/sounds
     */
    @Override
    public List<String> getRuntimePropertyList(String key) {
        List<String> res = new ArrayList<String>();
        if (key.compareToIgnoreCase("uniqueID") == 0) {
            res.add("not used");
            Vector<Long> ids;
            ids = CIMPortManager.getInstance().getUniqueIdentifiersofCIMs(HID_ACTUATOR_CIM_ID);
            if (ids != null) {
                for (Long l : ids) {
                    res.add(l.toString());
                    // System.out.println(" found unique ID: "+l.toString());
                }
            }
        }
        return res;
    }

    synchronized private final void sendJoystickUpdate(boolean forceSend) {
        // send packet
        byte[] b = new byte[19];

        // buttons byte 1:
        // 0: square
        // 1: cross
        // 2: circle
        // 3: triangle
        // 4: l1_btn
        // 5: r1_btn
        // 6: l2_btn
        // 7: r2_btn
        // buttons byte 2:
        // 8: select
        // 9: start
        // 10,11: 2 unused
        // 12: ps_btn
        // 13-15: 3 unused

        b[0] = (byte) (joystickBtn & 0xff);
        b[1] = (byte) ((joystickBtn >> 8) & 0xff);

        // POV- directions:
        // 8 = center, 0 = up, 1 = up/right, 2 = right, 3 = right/down
        // 4 = down, 5 = down/left, 6 = left, 7 = left/up

        b[2] = (byte) (joystickPov & 0xff);

        // 4 bytes axis: lx,ly,rx,ry

        b[3] = (byte) (joystickX & 0xff);
        b[4] = (byte) (joystickY & 0xff);
        b[5] = (byte) (joystickZ & 0xff);
        b[6] = (byte) (joystickR & 0xff);

        // 4 bytes: unknown
        b[7] = (byte) (0);
        b[8] = (byte) (0);
        b[9] = (byte) (0);
        b[10] = (byte) (0);

        b[11] = (byte) ((joystickBtn & (1 << 3)) == 0 ? 0 : 255); // triangle
                                                                  // axis
        b[12] = (byte) ((joystickBtn & (1 << 2)) == 0 ? 0 : 255); // circle axis
        b[13] = (byte) ((joystickBtn & (1 << 1)) == 0 ? 0 : 255); // cross axis
        b[14] = (byte) ((joystickBtn & (1 << 0)) == 0 ? 0 : 255); // square axis

        b[15] = (byte) ((joystickBtn & (1 << 4)) == 0 ? 0 : 255); // l1 axis
        b[16] = (byte) ((joystickBtn & (1 << 5)) == 0 ? 0 : 255); // r1 axis
        b[17] = (byte) ((joystickBtn & (1 << 6)) == 0 ? 0 : 255); // l2 axis
        b[18] = (byte) ((joystickBtn & (1 << 7)) == 0 ? 0 : 255); // r2 axis

        if (forceSend || (propRefreshInterval < 1) || (System.currentTimeMillis() - lastUpdate > propRefreshInterval)) {
            if (forceSend) {
                if (firstForcedUpdate == 0) {
                    firstForcedUpdate = System.currentTimeMillis();
                }
                forcedUpdates++;
                if (forcedUpdates < MAX_FORCED_UPDATES) {
                    if (port != null) {
                        CIMPortManager.getInstance().sendPacket(port, b, HID_FEATURE_JOYSTICK_UPDATE,
                                CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
                    }
                    lastUpdate = System.currentTimeMillis();
                } else {
                    if (System.currentTimeMillis() - firstForcedUpdate > MIN_FORCED_REFRESH_INTERVAL) {
                        if (port != null) {
                            CIMPortManager.getInstance().sendPacket(port, b, HID_FEATURE_JOYSTICK_UPDATE,
                                    CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
                        }
                        forcedUpdates = 1;
                        firstForcedUpdate = System.currentTimeMillis();
                    }
                }
            } else {
                if (port != null) {
                    CIMPortManager.getInstance().sendPacket(port, b, HID_FEATURE_JOYSTICK_UPDATE,
                            CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
                }
                lastUpdate = System.currentTimeMillis();
            }
        }
    }

    /**
     * Input Port for receiving X values.
     */
    private final IRuntimeInputPort ipJoystickX = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            joystickX = ConversionUtils.byteArrayToInt(data);
            // Logger.getAnonymousLogger().info("received Joystick y data: " +
            // joystick_y);
            sendJoystickUpdate(false);
        }

    };

    /**
     * Input Port for receiving Y values.
     */
    private final IRuntimeInputPort ipJoystickY = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            joystickY = ConversionUtils.byteArrayToInt(data);
            // Logger.getAnonymousLogger().info("received Joystick y data: " +
            // joystick_y);
            sendJoystickUpdate(false);
        }
    };

    /**
     * Input Port for receiving Z values.
     */
    private final IRuntimeInputPort ipJoystickZ = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            joystickZ = ConversionUtils.byteArrayToInt(data);
            // Logger.getAnonymousLogger().info("received Joystick z data: " +
            // joystick_y);
            sendJoystickUpdate(false);
        }
    };

    /**
     * Input Port for receiving R values.
     */
    private final IRuntimeInputPort ipJoystickR = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            joystickR = ConversionUtils.byteArrayToInt(data);
            // Logger.getAnonymousLogger().info("received Joystick y data: " +
            // joystick_y);
            sendJoystickUpdate(false);
        }

    };
    /**
     * Input Port for receiving POV values.
     */
    private final IRuntimeInputPort ipJoystickPov = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            joystickPov = ConversionUtils.byteArrayToInt(data);
            // Logger.getAnonymousLogger().info("received Joystick y data: " +
            // joystick_y);
            sendJoystickUpdate(false);
        }
    };

    final IRuntimeEventListenerPort elpReset = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            joystickPov = 8;
            joystickX = 128;
            joystickY = 128;
            joystickZ = 128;
            joystickR = 128;
            joystickBtn = 0;
            sendJoystickUpdate(true);
        }
    };

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        port = openCIM(HID_ACTUATOR_CIM_ID, propUniqueID);
        if (port != null) {
            AstericsErrorHandling.instance.reportInfo(this,
                    "RemoteJoystick Instance (ID " + propUniqueID + ") started.");
        } else {
            AstericsErrorHandling.instance.reportError(this,
                    "Could not find RemoteJoystick. Please verify that the HID actuator USB dongle is connected to the remote system and correctly installed on this computer.");
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
        joystickX = 0;
        joystickY = 0;
        joystickBtn = 0;
        port = null;
        super.stop();
        AstericsErrorHandling.instance.reportInfo(this, "RemoteJoystick Instance stopped");
    }

    class PressButtonListener implements IRuntimeEventListenerPort {
        private int index;

        PressButtonListener(int index) {
            this.index = index;
        }

        @Override
        public void receiveEvent(final String data) {
            // Logger.getAnonymousLogger().info("Joystick Button1 released");
            joystickBtn |= (1 << index);
            sendJoystickUpdate(true);
        }
    }

    class ReleaseButtonListener implements IRuntimeEventListenerPort {
        private int index;

        ReleaseButtonListener(int index) {
            this.index = index;
        }

        @Override
        public void receiveEvent(final String data) {
            // Logger.getAnonymousLogger().info("Joystick Button1 released");
            joystickBtn &= ~(1 << index);
            sendJoystickUpdate(true);
        }
    }
}
