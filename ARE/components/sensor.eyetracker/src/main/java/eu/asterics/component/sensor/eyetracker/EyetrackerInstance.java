
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

package eu.asterics.component.sensor.eyetracker;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.HashMap;

import eu.asterics.component.sensor.eyetracker.jni.Bridge;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Implements the eyetracker plugin, which uses OpenCV to detect a user's eye
 * movement recorded by a head mounted webcamera with infrared light support
 * (dark pupil method)
 * 
 * This plugin uses a JNI bridge to the OpenCV tracking routines.
 * 
 * @author Chris Veigl [veigl@technikum-wien.at] Date: Oct 10, 2010 Time:
 *         2:35:00 PM
 */
public class EyetrackerInstance extends AbstractRuntimeComponentInstance {
    final OutputPort opX = new OutputPort();
    final OutputPort opY = new OutputPort();

    final IRuntimeEventTriggererPort etpBlinkDetected = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpLongBlinkDetected = new DefaultRuntimeEventTriggererPort();

    final int STATE_IDLE = 0;
    final int STATE_CALIBRATION = 1;
    final int STATE_WAIT_FOR_NEXT_CALIBPOINT = 2;
    final int STATE_RUNNING = 3;

    final int MODE_BLOBTRACKING = 0;
    final int MODE_EYETRACKING = 1;
    final int MODE_EYETRACKING_HEADPOSE = 2;
    private int lastMode = -1;

    int state = STATE_IDLE;
    int propTrackingMode = MODE_EYETRACKING;

    String propCameraProfile = "";
    int propTimePeriod = 500;

    int propXMin = 0;
    int propXMax = 2000;
    int propYMin = 0;
    int propYMax = 1024;
    double propScreenSize = 22;

    int propCalibColumns = 2;
    int propCalibRows = 2;
    int propAveraging = 10;

    int propMinBlinkDuration = 300;
    int propMaxBlinkDuration = 800;
    long idleTime, offsetTime;
    int bypassBlinks = 10;

    int eyeX = 0, offsetX = 0;
    int eyeY = 0, offsetY = 0;
    int currentCursorX = 0, currentCursorY = 0;
    int oldCursorX = 0, oldCursorY = 0;
    boolean offsetCorrection = false;

    public int rawCalibratedX = 0;
    public int rawCalibratedY = 0;
    int calibratedX = 0;
    int calibratedY = 0;

    long validTimestamp = 0;

    boolean camConnected = false;

    private final CalibrationGenerator calib = new CalibrationGenerator(this);

    final Bridge bridge = new Bridge(this);
    protected POSIT positObj = new POSIT(this);

    /**
     * The class constructor.
     */
    public EyetrackerInstance() {
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
        if ("pt1x".equalsIgnoreCase(portID)) {
            return ipPt1x;
        } else if ("pt1y".equalsIgnoreCase(portID)) {
            return ipPt1y;
        } else if ("pt2x".equalsIgnoreCase(portID)) {
            return ipPt2x;
        } else if ("pt2y".equalsIgnoreCase(portID)) {
            return ipPt2y;
        } else if ("pt3x".equalsIgnoreCase(portID)) {
            return ipPt3x;
        } else if ("pt3y".equalsIgnoreCase(portID)) {
            return ipPt3y;
        } else if ("pt4x".equalsIgnoreCase(portID)) {
            return ipPt4x;
        } else if ("pt4y".equalsIgnoreCase(portID)) {
            return ipPt4y;
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
        if ("x".equalsIgnoreCase(portID)) {
            return opX;
        } else if ("y".equalsIgnoreCase(portID)) {
            return opY;
        }
        return null;
    }

    /**
     * returns an Event Triggerer Port
     * 
     * @param enventPortID
     *            the name of the event trigger port
     * @return the event trigger port or null if not found
     */
    @Override
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {
        if ("blinkDetected".equalsIgnoreCase(eventPortID)) {
            return etpBlinkDetected;
        } else if ("longBlinkDetected".equalsIgnoreCase(eventPortID)) {
            return etpLongBlinkDetected;
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
        if ("trackingMode".equalsIgnoreCase(propertyName)) {
            return propTrackingMode;
        } else if ("cameraProfile".equalsIgnoreCase(propertyName)) {
            return propCameraProfile;
        } else if ("xMin".equalsIgnoreCase(propertyName)) {
            return propXMin;
        } else if ("xMax".equalsIgnoreCase(propertyName)) {
            return propXMax;
        } else if ("yMin".equalsIgnoreCase(propertyName)) {
            return propYMin;
        } else if ("yMax".equalsIgnoreCase(propertyName)) {
            return propYMax;
        } else if ("calibrationStepsX".equalsIgnoreCase(propertyName)) {
            return propCalibColumns + 1;
        } else if ("calibrationStepsY".equalsIgnoreCase(propertyName)) {
            return propCalibRows + 1;
        } else if ("averaging".equalsIgnoreCase(propertyName)) {
            return propAveraging;
        } else if ("screenSize".equalsIgnoreCase(propertyName)) {
            return propScreenSize;
        } else if ("minBlinkDuration".equalsIgnoreCase(propertyName)) {
            return propMinBlinkDuration;
        } else if ("maxBlinkDuration".equalsIgnoreCase(propertyName)) {
            return propMaxBlinkDuration;
        } else {
            return bridge.getProperty(propertyName);
        }
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
        if ("trackingMode".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propTrackingMode;
            propTrackingMode = Integer.parseInt(newValue.toString());

            if ((lastMode == MODE_EYETRACKING_HEADPOSE) && (propTrackingMode != MODE_EYETRACKING_HEADPOSE)) {
                positObj.pausePosit();
            } else if ((lastMode == MODE_BLOBTRACKING || lastMode == MODE_EYETRACKING)
                    && (propTrackingMode == MODE_EYETRACKING_HEADPOSE)) {
                positObj.resumePosit();
            }

            lastMode = propTrackingMode;
            return oldValue;
        } else if ("cameraProfile".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propCameraProfile;
            propCameraProfile = (String) newValue;
            return oldValue;
        } else if ("xMin".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propXMin;
            propXMin = Integer.parseInt(newValue.toString());
            calib.updateCalibParams(propXMin, propXMax, propYMin, propYMax, propCalibColumns, propCalibRows);
            return oldValue;
        } else if ("xMax".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propXMax;

            propXMax = Integer.parseInt(newValue.toString());
            if (propXMax == 0) {
                Dimension screenSize = AREServices.instance.getScreenDimension();
                propXMax = screenSize.width;
            }
            calib.updateCalibParams(propXMin, propXMax, propYMin, propYMax, propCalibColumns, propCalibRows);
            return oldValue;
        } else if ("yMin".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propYMin;
            propYMin = Integer.parseInt(newValue.toString());
            calib.updateCalibParams(propXMin, propXMax, propYMin, propYMax, propCalibColumns, propCalibRows);
            return oldValue;
        } else if ("yMax".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propYMax;
            propYMax = Integer.parseInt(newValue.toString());
            if (propYMax == 0) {
                Dimension screenSize = AREServices.instance.getScreenDimension();
                propYMax = screenSize.height;
            }
            calib.updateCalibParams(propXMin, propXMax, propYMin, propYMax, propCalibColumns, propCalibRows);
            return oldValue;
        } else if ("calibrationStepsX".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propCalibColumns;
            propCalibColumns = Integer.parseInt(newValue.toString()) - 1;
            if (propCalibColumns < 1) {
                propCalibColumns = 1;
            }
            calib.updateCalibParams(propXMin, propXMax, propYMin, propYMax, propCalibColumns, propCalibRows);
            return oldValue;
        } else if ("calibrationStepsY".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propCalibRows;
            propCalibRows = Integer.parseInt(newValue.toString()) - 1;
            if (propCalibRows < 1) {
                propCalibRows = 1;
            }
            calib.updateCalibParams(propXMin, propXMax, propYMin, propYMax, propCalibColumns, propCalibRows);
            return oldValue;
        } else if ("averaging".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propAveraging;
            propAveraging = Integer.parseInt(newValue.toString()) - 1;
            if (propAveraging > 29) {
                propAveraging = 29;
            }
            return oldValue;
        } else if ("screenSize".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propScreenSize;
            propScreenSize = Double.parseDouble(newValue.toString());
            Dimension screenResolution = Toolkit.getDefaultToolkit().getScreenSize();
            positObj.setDisplayProperties(screenResolution.width, screenResolution.height, propScreenSize);

            return oldValue;
        } else if ("minBlinkDuration".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propMinBlinkDuration;
            propMinBlinkDuration = Integer.parseInt(newValue.toString());
            return oldValue;
        } else if ("maxBlinkDuration".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propMaxBlinkDuration;
            propMaxBlinkDuration = Integer.parseInt(newValue.toString());
            return oldValue;
        } else {
            final String oldValue = bridge.getProperty(propertyName);
            bridge.setProperty(propertyName, newValue.toString());
            return oldValue;
        }
    }

    /**
     * returns an Event Listener Port
     * 
     * @param enventPortID
     *            the name of the event listener port
     * @return the event listener port or null if not found
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        if ("calibrate".equalsIgnoreCase(eventPortID)) {
            return elpCalibrate;
        }
        if ("offsetCorrection".equalsIgnoreCase(eventPortID)) {
            return elpOffsetCorrection;
        }
        if ("showCameraSettings".equalsIgnoreCase(eventPortID)) {
            return elpShowCameraSettings;
        }
        if ("togglePoseInfoWindow".equalsIgnoreCase(eventPortID)) {
            return elpTogglePoseInfoWindow;
        }
        if ("startEvaluation".equalsIgnoreCase(eventPortID)) {
            return elpStartEvaluation;
        }
        if ("saveProfile".equalsIgnoreCase(eventPortID)) {
            return elpSaveProfile;
        }

        return null;
    }

    /**
     * Input Ports for receiving head pose compensation coordinates.
     */
    private final IRuntimeInputPort ipPt1x = new DefaultRuntimeInputPort() {
    };
    private final IRuntimeInputPort ipPt1y = new DefaultRuntimeInputPort() {
    };
    private final IRuntimeInputPort ipPt2x = new DefaultRuntimeInputPort() {
    };
    private final IRuntimeInputPort ipPt2y = new DefaultRuntimeInputPort() {
    };
    private final IRuntimeInputPort ipPt3x = new DefaultRuntimeInputPort() {
    };
    private final IRuntimeInputPort ipPt3y = new DefaultRuntimeInputPort() {
    };
    private final IRuntimeInputPort ipPt4x = new DefaultRuntimeInputPort() {
    };
    private final IRuntimeInputPort ipPt4y = new DefaultRuntimeInputPort() {
    };

    @Override
    public void syncedValuesReceived(HashMap<String, byte[]> dataRow) {
        // System.out.println("syncdValuesReceived" +
        // dataRow.keySet().toString());

        int pt1x = 0, pt1y = 0, pt2x = 0, pt2y = 0, pt3x = 0, pt3y = 0, pt4x = 0, pt4y = 0;

        for (String s : dataRow.keySet()) {

            byte[] data = dataRow.get(s);
            if (s.equals("pt1x")) {
                pt1x = ConversionUtils.intFromBytes(data);
            } else if (s.equals("pt1y")) {
                pt1y = ConversionUtils.intFromBytes(data);
            } else if (s.equals("pt2x")) {
                pt2x = ConversionUtils.intFromBytes(data);
            } else if (s.equals("pt2y")) {
                pt2y = ConversionUtils.intFromBytes(data);
            } else if (s.equals("pt3x")) {
                pt3x = ConversionUtils.intFromBytes(data);
            } else if (s.equals("pt3y")) {
                pt3y = ConversionUtils.intFromBytes(data);
            } else if (s.equals("pt4x")) {
                pt4x = ConversionUtils.intFromBytes(data);
            } else if (s.equals("pt4y")) {
                pt4y = ConversionUtils.intFromBytes(data);
            }
        }
        if (camConnected == true) {
            positObj.bridgePOSIT.runPOSIT(pt1x, pt1y, pt2x, pt2y, pt3x, pt3y, pt4x, pt4y);
        }
    }

    /**
     * Output Ports for feature coordinates.
     */
    public class OutputPort extends DefaultRuntimeOutputPort {
        public void sendData(int data) {
            super.sendData(ConversionUtils.intToByteArray(data));
        }
    }

    /**
     * Event Listener Port for Eyetracker calibration.
     */
    final IRuntimeEventListenerPort elpCalibrate = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(String data) {
            if ((propTrackingMode != MODE_BLOBTRACKING) && (camConnected == true)) {
                offsetX = 0;
                offsetY = 0;
                calib.startCalibration();
                bypassBlinks = 10;
            }
        }
    };

    /**
     * Event Listener Port for offset correction.
     */
    final IRuntimeEventListenerPort elpOffsetCorrection = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(String data) {
            if ((state == STATE_RUNNING) && (camConnected == true)) {
                System.out.println("Offset correction triggered.");
                offsetTime = System.currentTimeMillis() + 2000;
                oldCursorX = calibratedX;
                oldCursorY = calibratedY;
                offsetCorrection = true;
            }
        }
    };
    /**
     * Event Listener Port for save profile.
     */
    final IRuntimeEventListenerPort elpSaveProfile = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(String data) {
            if ((propCameraProfile != "") && (camConnected == true)) {
                bridge.saveCameraProfile(propCameraProfile + ".yml");
            }
        }
    };

    /**
     * Event Listener Port for Camera Settings Window.
     */
    final IRuntimeEventListenerPort elpShowCameraSettings = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(String data) {
            if (camConnected == true) {
                bridge.showCameraSettings();
            }
        }
    };

    /**
     * Event Listener Port for Toggling Pose Info Window.
     */
    final IRuntimeEventListenerPort elpTogglePoseInfoWindow = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(String data) {
            if ((propTrackingMode == MODE_EYETRACKING_HEADPOSE) && (camConnected == true)) {
                positObj.togglePoseInfoWindow();
            } else {
                System.out.println("eyetracking with head pose is not activated or cam not available");
            }
        }
    };

    /**
     * Event Listener Port for Starting Evaluation of Accuracy.
     */
    final IRuntimeEventListenerPort elpStartEvaluation = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(String data) {
            if ((propTrackingMode == MODE_EYETRACKING_HEADPOSE) && (camConnected == true)) {
                positObj.startEvaluation();
            } else {
                System.out.println("eyetracking with head pose is not activated or cam not available");
            }

        }
    };

    synchronized public void eyeLocationCallback(final int x_location, final int y_location) {
        eyeX = x_location;
        eyeY = y_location;

        if (camConnected == false) {
            return;
        }

        if (propTrackingMode == MODE_BLOBTRACKING) {
            // just put out the tracking values
            opX.sendData(eyeX / 20);
            opY.sendData(eyeY / 20);
        } else // calibrated eyetracking (with or without head pose correction)
        {
            switch (state) {
            case STATE_IDLE:

                idleTime = System.currentTimeMillis() - validTimestamp;
                validTimestamp = System.currentTimeMillis();

                if (bypassBlinks == 0) {
                    if ((propMinBlinkDuration > 0) && (idleTime > propMinBlinkDuration)
                            && (idleTime < propMaxBlinkDuration)) {
                        etpBlinkDetected.raiseEvent();
                    }

                    if ((propMinBlinkDuration > 0) && (idleTime > propMaxBlinkDuration)) {
                        etpLongBlinkDetected.raiseEvent();
                    }
                } else {
                    bypassBlinks--;
                }

                break;
            case STATE_CALIBRATION:
                calib.captureCalibrationValues(eyeX, eyeY);
                break;
            case STATE_WAIT_FOR_NEXT_CALIBPOINT:
                break;

            case STATE_RUNNING:

                idleTime = System.currentTimeMillis() - validTimestamp;
                validTimestamp = System.currentTimeMillis();

                if (bypassBlinks == 0) {
                    if ((propMinBlinkDuration > 0) && (idleTime > propMinBlinkDuration)
                            && (idleTime < propMaxBlinkDuration)) {
                        etpBlinkDetected.raiseEvent();
                    }

                    if ((propMinBlinkDuration > 0) && (idleTime > propMaxBlinkDuration)) {
                        etpLongBlinkDetected.raiseEvent();
                    }
                } else {
                    bypassBlinks--;
                }

                if (propTrackingMode == MODE_EYETRACKING) {
                    calib.getCalibratedLocations(eyeX, eyeY);

                    if (offsetCorrection == false) {
                        opX.sendData(calibratedX + offsetX);
                        opY.sendData(calibratedY + offsetY);
                    } else {
                        opX.sendData(oldCursorX);
                        opY.sendData(oldCursorY);
                    }

                } else if (propTrackingMode == MODE_EYETRACKING_HEADPOSE) {
                    // use the head pose for pose compensation based on pose
                    // during each calib step:
                    calib.getCalibratedLocationsWithPOSIT(eyeX, eyeY);

                    if (offsetCorrection == false) {
                        opX.sendData(calibratedX + offsetX);
                        opY.sendData(calibratedY + offsetY);
                    } else {
                        opX.sendData(oldCursorX);
                        opY.sendData(oldCursorY);
                    }

                    // check if values shall be sent to the native code
                    if (positObj.sendEyeCoordinates() == 1) {
                        positObj.sendEvalValues();
                    }

                }
                break;
            }

            if (offsetCorrection == true) {
                if (System.currentTimeMillis() > offsetTime) {
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    currentCursorX = (int) p.getX();
                    currentCursorY = (int) p.getY();
                    System.out.println("Cursorpos = " + currentCursorX + "/" + currentCursorY + ")");

                    offsetX = currentCursorX - calibratedX;
                    offsetY = currentCursorY - calibratedY;
                    System.out.println("updating Offset (" + offsetX + "/" + offsetY + ")");
                    offsetCorrection = false;

                }
            }
        }
    }

    public void setCursor(int x, int y) {
        opX.sendData(x);
        opY.sendData(y);
    }

    @Override
    public void start() {

        state = STATE_IDLE;
        offsetCorrection = false;

        if (bridge.activate() == 0) {
            AstericsErrorHandling.instance.reportError(this, "Could not init Camera");
            camConnected = false;
        } else {
            AstericsErrorHandling.instance.reportDebugInfo(this, "Eyetracker Camera activated");
            camConnected = true;
            if (propCameraProfile != "") {
                bridge.loadCameraProfile(propCameraProfile + ".yml");
            }

            Point pos = AREServices.instance.getComponentPosition(this);
            Dimension d = AREServices.instance.getAvailableSpace(this);
            bridge.setDisplayPosition(pos.x, pos.y, d.width, d.height);

            if (propTrackingMode == MODE_EYETRACKING_HEADPOSE) {
                positObj.startPosit();
            }
        }
        super.start();
    }

    @Override
    public void pause() {
        if (camConnected == true) {
            calib.stopCalibration();
            bridge.deactivate();

            if (propTrackingMode == MODE_EYETRACKING_HEADPOSE) {
                positObj.pausePosit();
            }

            camConnected = false;
        }
        super.pause();
    }

    @Override
    public void resume() {
        if (bridge.activate() == 0) {
            AstericsErrorHandling.instance.reportError(this, "Could not resume Camera");
            camConnected = false;
        } else {
            AstericsErrorHandling.instance.reportDebugInfo(this, "Eyetracker Camera resumed");
            camConnected = true;

            if (propTrackingMode == MODE_EYETRACKING_HEADPOSE) {
                positObj.resumePosit();
            }
        }
        super.resume();
    }

    @Override
    public void stop() {
        if (camConnected == true) {

            calib.stopCalibration();
            bridge.deactivate();

            if ((propTrackingMode == MODE_EYETRACKING_HEADPOSE) && (camConnected == true)) {
                positObj.stopPosit();
            }

            camConnected = false;
        }
        super.stop();
    }
}
