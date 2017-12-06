
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

package eu.asterics.component.sensor.eyex;

import java.awt.Point;
import java.util.LinkedList;

import eu.asterics.component.sensor.eyex.jni.Bridge;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
 
/**
 * 
 * Interfaces to the Tobii EyeX Gaze tracker server
 * 
 * 
 * 
 * @author Chris Veigl [veigl@technikum-wien.at] Date: 01/2015
 */
public class EyeXInstance extends AbstractRuntimeComponentInstance // implements
                                                                   // ICalibrationProcessHandler
{
    final static IRuntimeOutputPort opGazeX = new DefaultRuntimeOutputPort();
    final static IRuntimeOutputPort opGazeY = new DefaultRuntimeOutputPort();
    final static IRuntimeOutputPort opPosX = new DefaultRuntimeOutputPort();
    final static IRuntimeOutputPort opPosY = new DefaultRuntimeOutputPort();
    final static IRuntimeOutputPort opFixationTime = new DefaultRuntimeOutputPort();
    final static IRuntimeOutputPort opCloseTime = new DefaultRuntimeOutputPort();

    final static IRuntimeEventTriggererPort etpBlink = new DefaultRuntimeEventTriggererPort();
    final static IRuntimeEventTriggererPort etpLongblink = new DefaultRuntimeEventTriggererPort();
    final static IRuntimeEventTriggererPort etpFixation = new DefaultRuntimeEventTriggererPort();
    final static IRuntimeEventTriggererPort etpFixationEnd = new DefaultRuntimeEventTriggererPort();

    final static int MANUALOFFSETBOX = 200;
    final static int MANUALOFFSETMAXTIME = 7000;

    final static int STATE_IDLE = 0;
    final static int STATE_CALIBRATION = 1;
    final static int STATE_INITIATE_CORRECTION = 2;
    final static int STATE_AUTOCORRECTION = 3;
    final static int STATE_MANUALCORRECTION = 4;

    final static int MODE_CORRECTION_SPOTS = 0;
    final static int MODE_PERMANENT_CORRECTION = 1;
    final static int MODE_COMBINED_TRACKING = 2;
    final static int MODE_SEND_COORDINATES_EVENT = 3;

    final static int COMBINED_CORRECTION_IDLE = 0;
    final static int COMBINED_CORRECTION_ACTIVE = 1;

    final static int POS_LEFT = 0;
    final static int POS_RIGHT = 1;
    final static int POS_BOTH = 2;

    final static int MANUAL_CORRECTION_DEADZONE = 5;
    final static double MANUAL_CORRECTION_SPEEDFACTOR = 0.020;
    final static int MANUAL_CORRECTION_MAXSPEED = 2;

    final static String CALIB_SOUND_START = "./data/sounds/7.wav";
    final static String CALIB_SOUND_NOTICE = "./data/sounds/8.wav";

    static int state = STATE_IDLE;

    static boolean propEnabled = true;
    static int propAveraging = 4;
    static int propMinBlinkTime = 50;
    static int propMidBlinkTime = 200;
    static int propMaxBlinkTime = 2000;
    static int propFixationTime = 700;
    static int propOffsetCorrectionRadius = 150;
    static int propOffsetPointRemovalRadius = 50; // TBD: make this adjustable
                                                  // via property
    static int propOffsetCorrectionMode = MODE_PERMANENT_CORRECTION;
    static int propPupilPositionMode = POS_BOTH;

    static boolean measuringClose = false;
    static boolean measuringFixation = false;
    static boolean sentFixationEvent = false;
    static long startCloseTimestamp = 0;
    static long startFixationTimestamp = 0;
    static boolean eyePositionValid = false;

    static double currentManualOffsetX = 0;
    static double currentManualOffsetY = 0;

    static long offsetCorrectionStartTime, actTimestamp = 0, lastTimestamp = 0;
    static int gazeX, gazeY, eyeX, eyeY;
    static int correctedGazeX, correctedGazeY, weakGazePointX, weakGazePointY;
    static int lastGazeX = 0, lastGazeY = 0, saveCorrectedGazeX, saveCorrectedGazeY;
    static double oldOffsetX = 0, offsetX = 0, oldOffsetY = 0, offsetY = 0, sameOffset = 0;

    private final Bridge bridge = new Bridge(this);
    private final CalibrationGenerator calib = new CalibrationGenerator(this);

    private final LinkedList<Integer> bufferX = new LinkedList<Integer>();
    private final LinkedList<Integer> bufferY = new LinkedList<Integer>();
    private int sumX = 0, sumY = 0, combinedCorrectionMode = COMBINED_CORRECTION_IDLE;
    private Point offset = new Point(0, 0);

    /**
     * The class constructor.
     */
    public EyeXInstance() {
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
        if ("xOffset".equalsIgnoreCase(portID)) {
            return ipXOffset;
        }
        if ("yOffset".equalsIgnoreCase(portID)) {
            return ipYOffset;
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
        if ("gazeX".equalsIgnoreCase(portID)) {
            return opGazeX;
        }
        if ("gazeY".equalsIgnoreCase(portID)) {
            return opGazeY;
        }
        if ("posX".equalsIgnoreCase(portID)) {
            return opPosX;
        }
        if ("posY".equalsIgnoreCase(portID)) {
            return opPosY;
        }
        if ("fixationTime".equalsIgnoreCase(portID)) {
            return opFixationTime;
        }
        if ("closeTime".equalsIgnoreCase(portID)) {
            return opCloseTime;
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
        if ("offsetCorrection".equalsIgnoreCase(eventPortID)) {
            return elpOffsetCorrection;
        }
        if ("removeLastOffsetCorrection".equalsIgnoreCase(eventPortID)) {
            return elpRemoveLastOffsetCorrection;
        }
        if ("stopOffsetCorrection".equalsIgnoreCase(eventPortID)) {
            return elpStopOffsetCorrection;
        }
        if ("calibrateCurrentProfile".equalsIgnoreCase(eventPortID)) {
            return elpCalibrateCurrentProfile;
        }
        if ("createAndCalibrateGuestProfile".equalsIgnoreCase(eventPortID)) {
            return elpCreateGuestProfile;
        }
        if ("switchToOffsetCorrectionSpots".equalsIgnoreCase(eventPortID)) {
            return elpSwitchToOffsetCorrectionSpots;
        }
        if ("switchToPermanentOffsetCorrection".equalsIgnoreCase(eventPortID)) {
            return elpSwitchToPermanentOffsetCorrection;
        }
        if ("switchToCombinedOffsetCorrection".equalsIgnoreCase(eventPortID)) {
            return elpSwitchToCombinedOffsetCorrection;
        }
        if ("switchToSendCoordinatesEvent".equalsIgnoreCase(eventPortID)) {
            return elpSwitchToSendCoordinatesEvent;
        }
        if ("sendCoordinatesEvent".equalsIgnoreCase(eventPortID)) {
            return elpSendCoordinatesEvent;
        }
        if ("activate".equalsIgnoreCase(eventPortID)) {
            return elpActivate;
        }
        if ("deactivate".equalsIgnoreCase(eventPortID)) {
            return elpDeactivate;
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
        if ("blink".equalsIgnoreCase(eventPortID)) {
            return etpBlink;
        }
        if ("longblink".equalsIgnoreCase(eventPortID)) {
            return etpLongblink;
        }
        if ("fixation".equalsIgnoreCase(eventPortID)) {
            return etpFixation;
        }
        if ("fixationEnd".equalsIgnoreCase(eventPortID)) {
            return etpFixationEnd;
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
        if ("enabled".equalsIgnoreCase(propertyName)) {
            return propEnabled;
        }
        if ("averaging".equalsIgnoreCase(propertyName)) {
            return propAveraging;
        }
        if ("minBlinkTime".equalsIgnoreCase(propertyName)) {
            return propMinBlinkTime;
        }
        if ("midBlinkTime".equalsIgnoreCase(propertyName)) {
            return propMidBlinkTime;
        }
        if ("maxBlinkTime".equalsIgnoreCase(propertyName)) {
            return propMaxBlinkTime;
        }
        if ("fixationTime".equalsIgnoreCase(propertyName)) {
            return propFixationTime;
        }
        if ("offsetCorrectionRadius".equalsIgnoreCase(propertyName)) {
            return propOffsetCorrectionRadius;
        }
        if ("offsetCorrectionMode".equalsIgnoreCase(propertyName)) {
            return propOffsetCorrectionMode;
        }
        if ("pupilPositionMode".equalsIgnoreCase(propertyName)) {
            return propPupilPositionMode;
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
        if ("enabled".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propEnabled;

            if ("true".equalsIgnoreCase((String) newValue)) {
                propEnabled = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propEnabled = false;
            }

            return oldValue;
        }
        if ("averaging".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propAveraging;
            propAveraging = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("minBlinkTime".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propMinBlinkTime;
            propMinBlinkTime = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("midBlinkTime".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propMidBlinkTime;
            propMidBlinkTime = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("maxBlinkTime".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propMaxBlinkTime;
            propMaxBlinkTime = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("fixationTime".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propFixationTime;
            propFixationTime = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("offsetCorrectionRadius".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propOffsetCorrectionRadius;
            propOffsetCorrectionRadius = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("offsetCorrectionMode".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propOffsetCorrectionMode;
            propOffsetCorrectionMode = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("pupilPositionMode".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propPupilPositionMode;
            propPupilPositionMode = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        return null;
    }

    /**
     * Input Ports for receiving values.
     */
    private final IRuntimeInputPort ipXOffset = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            offsetX += ConversionUtils.doubleFromBytes(data);

        }

    };

    private final IRuntimeInputPort ipYOffset = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            offsetY += ConversionUtils.doubleFromBytes(data);
        }

    };

    /**
     * Event Listener Port for offset correction.
     */
    final IRuntimeEventListenerPort elpOffsetCorrection = new IRuntimeEventListenerPort() {
        @Override
        public synchronized void receiveEvent(String data) {
            if (propOffsetCorrectionMode == MODE_CORRECTION_SPOTS) {
                if (state == STATE_MANUALCORRECTION) {
                    calib.newOffsetPoint(weakGazePointX, weakGazePointY, (int) currentManualOffsetX,
                            (int) currentManualOffsetY);
                    System.out.println("Manual correction finished.");
                    state = STATE_IDLE;
                } else {
                    calib.playWavFile(CALIB_SOUND_START);

                    System.out.println("Offset correction triggered.");
                    measuringClose = false;
                    measuringFixation = false;
                    offsetCorrectionStartTime = System.currentTimeMillis();
                    state = STATE_INITIATE_CORRECTION;
                }
            }
        }
    };

    final IRuntimeEventListenerPort elpRemoveLastOffsetCorrection = new IRuntimeEventListenerPort() {
        @Override
        public synchronized void receiveEvent(String data) {
            int remainingPoints = calib.removeOffsetPoint();
            System.out.println(
                    "Removed last offset correction point. Now there are " + remainingPoints + " points left.");
        }
    };

    final IRuntimeEventListenerPort elpStopOffsetCorrection = new IRuntimeEventListenerPort() {
        @Override
        public synchronized void receiveEvent(String data) {
            System.out.println("stop offset correction mode");
        }
    };

    final IRuntimeEventListenerPort elpCreateGuestProfile = new IRuntimeEventListenerPort() {

        @Override
        public void receiveEvent(String data) {
            bridge.recalibrate(true);
        }
    };

    final IRuntimeEventListenerPort elpCalibrateCurrentProfile = new IRuntimeEventListenerPort() {

        @Override
        public void receiveEvent(String data) {
            bridge.recalibrate(false);
        }
    };

    final IRuntimeEventListenerPort elpActivate = new IRuntimeEventListenerPort() {
        @Override
        public synchronized void receiveEvent(String data) {
            startTracker();
        }

    };

    final IRuntimeEventListenerPort elpDeactivate = new IRuntimeEventListenerPort() {
        @Override
        public synchronized void receiveEvent(String data) {
            stopTracker();
        }
    };

    final IRuntimeEventListenerPort elpSwitchToOffsetCorrectionSpots = new IRuntimeEventListenerPort() {
        @Override
        public synchronized void receiveEvent(String data) {
            propOffsetCorrectionMode = MODE_CORRECTION_SPOTS;
        }
    };
    final IRuntimeEventListenerPort elpSwitchToPermanentOffsetCorrection = new IRuntimeEventListenerPort() {
        @Override
        public synchronized void receiveEvent(String data) {
            propOffsetCorrectionMode = MODE_PERMANENT_CORRECTION;
            initTrackingVars();
        }
    };
    final IRuntimeEventListenerPort elpSwitchToCombinedOffsetCorrection = new IRuntimeEventListenerPort() {
        @Override
        public synchronized void receiveEvent(String data) {
            propOffsetCorrectionMode = MODE_COMBINED_TRACKING;
            initTrackingVars();
        }
    };
    final IRuntimeEventListenerPort elpSwitchToSendCoordinatesEvent = new IRuntimeEventListenerPort() {
        @Override
        public synchronized void receiveEvent(String data) {
            propOffsetCorrectionMode = MODE_SEND_COORDINATES_EVENT;
            initTrackingVars();
        }
    };
    final IRuntimeEventListenerPort elpSendCoordinatesEvent = new IRuntimeEventListenerPort() {
        @Override
        public synchronized void receiveEvent(String data) {
            if (propOffsetCorrectionMode == MODE_SEND_COORDINATES_EVENT) {
                opGazeX.sendData(ConversionUtils.intToBytes(correctedGazeX));
                opGazeY.sendData(ConversionUtils.intToBytes(correctedGazeY));
            }
        }
    };

    synchronized public void newEyeData(boolean isFixated, int gazeDataX, int gazeDataY, int leftEyeX, int leftEyeY) {
        actTimestamp = System.currentTimeMillis();
        firstGazeData = false;
        waitForGazeData = 0;

        int rightEyeX = leftEyeX + 20; // TBD: improve: get right eye
                                       // coordinates from API !
        int rightEyeY = leftEyeY;

        /*
         * 
         * // TBD: get eyestate from API if ((eyestate & 7) == 0) // tracking
         * lost ? { measuringClose=false; measuringFixation=false; return; }
         * 
         */

        // calculate gazePoint and perform avaraging

        bufferX.addFirst((int) gazeDataX);
        sumX += gazeDataX;
        if (bufferX.size() > propAveraging) {
            sumX -= bufferX.removeLast();
        }

        bufferY.addFirst((int) gazeDataY);
        sumY += gazeDataY;
        if (bufferY.size() > propAveraging) {
            sumY -= bufferY.removeLast();
        }

        gazeX = (sumX / bufferX.size());
        gazeY = (sumY / bufferY.size());

        // calculate eye position

        switch (propPupilPositionMode) {
        case POS_LEFT:
            eyeX = (int) (leftEyeX);
            eyeY = (int) (leftEyeY);
            if ((eyeX == 0) && (eyeY == 0)) {
                eyePositionValid = false;
            } else {
                eyePositionValid = true;
            }
            break;
        case POS_RIGHT:
            eyeX = (int) (rightEyeX);
            eyeY = (int) (rightEyeX);
            if ((eyeX == 0) && (eyeY == 0)) {
                eyePositionValid = false;
            } else {
                eyePositionValid = true;
            }
            break;
        case POS_BOTH:
            if (((leftEyeX == 0) && (leftEyeY == 0)) || ((rightEyeX == 0) && (rightEyeY == 0))) {
                eyePositionValid = false;
            } else {
                eyePositionValid = true;
                eyeX = (int) ((leftEyeX + rightEyeX) / 2);
                eyeY = (int) ((leftEyeY + rightEyeY) / 2);
            }
            break;
        }

        // involve offset corrections spots (get linear interpolation)

        if (propOffsetCorrectionMode == MODE_CORRECTION_SPOTS) {
            offset = calib.calcOffset(gazeX, gazeY); // look if we have an
                                                     // active offset correction
                                                     // point
            correctedGazeX = gazeX + offset.x;
            correctedGazeY = gazeY + offset.y;
        } else {
            offset.x = 0;
            offset.y = 0;
            correctedGazeX = gazeX;
            correctedGazeY = gazeY;
        }

        // handle oofset correction spot creation
        switch (state) {

        case STATE_INITIATE_CORRECTION: // get weak gaze point coordinates
            if (actTimestamp >= offsetCorrectionStartTime + 1000) {
                weakGazePointX = gazeX;
                weakGazePointY = gazeY;

                saveCorrectedGazeX = correctedGazeX;
                saveCorrectedGazeY = correctedGazeY;

                calib.playWavFile(CALIB_SOUND_NOTICE);

                System.out.println("Got weak gaze spot for manual correction");
                // Point oldOffset = calib.getOffsetPoint(weakGazePointY,
                // weakGazePointY);
                currentManualOffsetX = offset.x;
                currentManualOffsetY = offset.y;
                state = STATE_MANUALCORRECTION;
            }
            break;

        case STATE_AUTOCORRECTION: // get estimated offset to desired gazepoint
            if (actTimestamp >= offsetCorrectionStartTime + 2000) {
                Point oldOffset = calib.calcOffset(weakGazePointY, weakGazePointY);
                calib.newOffsetPoint(weakGazePointX, weakGazePointY,
                        oldOffset.x + (saveCorrectedGazeX - correctedGazeX),
                        oldOffset.y + (saveCorrectedGazeY - correctedGazeY));
                System.out.println("Automatic correction finished.");
                state = STATE_IDLE;
            }
            return;

        case STATE_MANUALCORRECTION: // modify offset by gaze actions
            double currentXDirection = weakGazePointX - gazeX;
            double currentYDirection = weakGazePointY - gazeY;

            if ((currentXDirection > -MANUAL_CORRECTION_DEADZONE) && (currentXDirection < MANUAL_CORRECTION_DEADZONE)) {
                currentXDirection = 0;
            }
            if ((currentYDirection > -MANUAL_CORRECTION_DEADZONE) && (currentYDirection < MANUAL_CORRECTION_DEADZONE)) {
                currentYDirection = 0;
            }

            currentXDirection *= MANUAL_CORRECTION_SPEEDFACTOR;
            currentYDirection *= MANUAL_CORRECTION_SPEEDFACTOR;

            if (currentXDirection < -MANUAL_CORRECTION_MAXSPEED) {
                currentXDirection = -MANUAL_CORRECTION_MAXSPEED;
            }
            if (currentXDirection > MANUAL_CORRECTION_MAXSPEED) {
                currentXDirection = MANUAL_CORRECTION_MAXSPEED;
            }
            if (currentYDirection < -MANUAL_CORRECTION_MAXSPEED) {
                currentYDirection = -MANUAL_CORRECTION_MAXSPEED;
            }
            if (currentYDirection > MANUAL_CORRECTION_MAXSPEED) {
                currentYDirection = MANUAL_CORRECTION_MAXSPEED;
            }

            currentManualOffsetX += currentXDirection;
            currentManualOffsetY += currentYDirection;

            if (currentManualOffsetX < -MANUALOFFSETBOX) {
                currentManualOffsetX = -MANUALOFFSETBOX;
            }
            if (currentManualOffsetX > MANUALOFFSETBOX) {
                currentManualOffsetX = MANUALOFFSETBOX;
            }
            if (currentManualOffsetY < -MANUALOFFSETBOX) {
                currentManualOffsetX = -MANUALOFFSETBOX;
            }
            if (currentManualOffsetY > MANUALOFFSETBOX) {
                currentManualOffsetX = MANUALOFFSETBOX;
            }

            System.out.println("Manual correction: " + currentManualOffsetX + "/" + currentManualOffsetY);

            opGazeX.sendData(ConversionUtils.intToBytes(weakGazePointX + (int) currentManualOffsetX));
            opGazeY.sendData(ConversionUtils.intToBytes(weakGazePointY + (int) currentManualOffsetY));

            if (System.currentTimeMillis() >= offsetCorrectionStartTime + MANUALOFFSETMAXTIME) {
                calib.newOffsetPoint(weakGazePointX, weakGazePointY, (int) currentManualOffsetX,
                        (int) currentManualOffsetY);
                System.out.println("Manual correction finished.");
                state = STATE_IDLE;
                calib.playWavFile(CALIB_SOUND_NOTICE);
            }
            return;
        }

        if ((gazeDataX == 0) && (gazeDataY == 0)) // eyes closed
        {
            if (measuringClose == false) {
                startCloseTimestamp = System.currentTimeMillis();
                measuringClose = true;
            }
            opCloseTime.sendData(ConversionUtils.intToBytes((int) (System.currentTimeMillis() - startCloseTimestamp)));
        } else // eyes detected
        {
            // create long and short blink events
            if (measuringClose == true) {
                long blinktime = System.currentTimeMillis() - startCloseTimestamp;

                if ((blinktime > propMinBlinkTime) && (blinktime < propMidBlinkTime)) {
                    etpBlink.raiseEvent();
                } else if ((blinktime >= propMidBlinkTime) && (blinktime <= propMaxBlinkTime)) {
                    etpLongblink.raiseEvent();
                }

                measuringClose = false;
            }

            if (eyePositionValid) // send eye position
            {
                opPosX.sendData(ConversionUtils.intToBytes(eyeX));
                opPosY.sendData(ConversionUtils.intToBytes(eyeY));
            }

            // send gaze point according to offset correction mode
            if (propOffsetCorrectionMode == MODE_COMBINED_TRACKING) {

                if ((combinedCorrectionMode == COMBINED_CORRECTION_IDLE) && ((offsetX != 0) || (offsetY != 0))) {
                    combinedCorrectionMode = COMBINED_CORRECTION_ACTIVE;
                    lastGazeX = correctedGazeX;
                    lastGazeY = correctedGazeY;
                }

                if (combinedCorrectionMode == COMBINED_CORRECTION_IDLE) {
                    opGazeX.sendData(ConversionUtils.intToBytes(correctedGazeX));
                    opGazeY.sendData(ConversionUtils.intToBytes(correctedGazeY));
                } else {
                    opGazeX.sendData(ConversionUtils.intToBytes(lastGazeX + (int) offsetX));
                    opGazeY.sendData(ConversionUtils.intToBytes(lastGazeY + (int) offsetY));

                    int dist = (int) Math.sqrt(
                            (lastGazeX + (int) offsetX - correctedGazeX) * (lastGazeX + (int) offsetX - correctedGazeX)
                                    + (lastGazeY + (int) offsetY - correctedGazeY)
                                            * (lastGazeY + (int) offsetY - correctedGazeY));

                    if (dist > propOffsetCorrectionRadius) {

                        if ((Math.abs(offsetX - oldOffsetX) + Math.abs(offsetY - oldOffsetY)) == 0) {
                            sameOffset++;
                        }

                        if (sameOffset > 7) {
                            initTrackingVars();
                        }

                        oldOffsetX = offsetX;
                        oldOffsetY = offsetY;

                    } else {
                        sameOffset = 0;
                    }
                }
            } else if (propOffsetCorrectionMode == MODE_PERMANENT_CORRECTION) {
                opGazeX.sendData(ConversionUtils.intToBytes(correctedGazeX + (int) offsetX));
                opGazeY.sendData(ConversionUtils.intToBytes(correctedGazeY + (int) offsetY));

            } else if (propOffsetCorrectionMode != MODE_SEND_COORDINATES_EVENT) {
                opGazeX.sendData(ConversionUtils.intToBytes(correctedGazeX));
                opGazeY.sendData(ConversionUtils.intToBytes(correctedGazeY));
            }

            if ((isFixated == true) && (measuringFixation == false)) {
                startFixationTimestamp = System.currentTimeMillis();
                measuringFixation = true;
                sentFixationEvent = false;
            }

            if (isFixated == false) {
                if ((measuringFixation == true) && (sentFixationEvent == true)) {
                    etpFixationEnd.raiseEvent();
                }
                measuringFixation = false;
            }

            if (measuringFixation == true) {
                opFixationTime.sendData(
                        ConversionUtils.intToBytes((int) (System.currentTimeMillis() - startFixationTimestamp)));
                if ((System.currentTimeMillis() - startFixationTimestamp > propFixationTime)
                        && (sentFixationEvent == false)) {
                    etpFixation.raiseEvent();
                    sentFixationEvent = true;
                }
            }
        }
    }

    private Thread readerThread = null;
    private boolean running = false;
    private boolean firstGazeData = true;
    private int waitForGazeData = 0;
    int avgIdle = 0;

    public void closeTimeWatchDogStop() {
        running = false;
    }

    public void closeTimeWatchDogStart() {
        waitForGazeData = 0;
        firstGazeData = true;

        readerThread = new Thread(new Runnable() {

            @Override
            public void run() {
                running = true;
                while (running) {

                    try {
                        if ((firstGazeData == false) && (waitForGazeData > 100)) {
                            newEyeData(false, 0, 0, 0, 0);
                        } else {
                            Thread.sleep(10);
                            waitForGazeData += 10;
                        }
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }

        });
        readerThread.start();
    }

    synchronized private void initTrackingVars() {
        offsetX = 0;
        offsetY = 0;
        oldOffsetX = 0;
        oldOffsetY = 0;
        combinedCorrectionMode = COMBINED_CORRECTION_IDLE;
        sumX = 0;
        sumY = 0;
        bufferX.clear();
        bufferY.clear();
        avgIdle = 5;
    }

    synchronized public void stopTracker() {
        if (running == true) {
            closeTimeWatchDogStop();
            bridge.deactivate();
        }
    }

    synchronized public void startTracker() {
        if (running == false) {
            initTrackingVars();
            bridge.activate();
            closeTimeWatchDogStart();
        }
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        if (propEnabled == true) {
            startTracker();
        }
        super.start();
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        stopTracker();
        super.pause();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        startTracker();
        super.resume();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        stopTracker();
        super.stop();
    }
}