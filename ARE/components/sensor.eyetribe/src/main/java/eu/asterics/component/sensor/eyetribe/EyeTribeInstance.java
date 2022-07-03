
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

package eu.asterics.component.sensor.eyetribe;

import java.awt.Point;

import com.theeyetribe.client.GazeManager;
import com.theeyetribe.client.GazeManager.ApiVersion;
import com.theeyetribe.client.GazeManager.ClientMode;
import com.theeyetribe.client.IGazeListener;
import com.theeyetribe.client.data.GazeData;

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
 * Interfaces to the EyeTribe Gaze tracker server
 * 
 * 
 * 
 * @author <your name> [<your email address>] Date: Time:
 */
public class EyeTribeInstance extends AbstractRuntimeComponentInstance // implements
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
    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    final static int STATE_IDLE = 0;
    final static int STATE_CALIBRATION = 1;
    final static int STATE_INITIATE_CORRECTION = 2;
    final static int STATE_AUTOCORRECTION = 3;
    final static int STATE_MANUALCORRECTION = 4;

    final static int MODE_MANUALCORRECTION = 0;
    final static int MODE_AUTOCORRECTION = 1;

    final static int POS_LEFT = 0;
    final static int POS_RIGHT = 1;
    final static int POS_BOTH = 2;

    final static int MANUAL_CORRECTION_DEADZONE = 5;
    final static double MANUAL_CORRECTION_SPEEDFACTOR = 0.020;
    final static int MANUAL_CORRECTION_MAXSPEED = 2;

    final static String CALIB_SOUND_START = "./data/sounds/7.wav";
    final static String CALIB_SOUND_NOTICE = "./data/sounds/8.wav";

    static int state = STATE_IDLE;

    static int propMinBlinkTime = 50;
    static int propMidBlinkTime = 200;
    static int propMaxBlinkTime = 2000;
    static int propFixationTime = 700;
    static int propOffsetCorrectionRadius = 150;
    static int propOffsetPointRemovalRadius = 50; // TBD: make this adjustable
                                                  // via property
    static int propOffsetCorrectionMode = MODE_MANUALCORRECTION;
    static int propPupilPositionMode = POS_BOTH;

    static boolean measuringClose = false;
    static boolean measuringFixation = false;
    static boolean sentFixationEvent = false;
    static long startCloseTimestamp = 0;
    static long startFixationTimestamp = 0;
    static boolean eyePositionValid = false;

    static double currentManualOffsetX = 0;
    static double currentManualOffsetY = 0;

    static long offsetCorrectionStartTime;
    static int gazeX, gazeY, eyeX, eyeY;
    static int correctedGazeX, correctedGazeY, weakGazePointX, weakGazePointY, saveCorrectedGazeX, saveCorrectedGazeY;

    private final CalibrationGenerator calib = new CalibrationGenerator(this);

    final GazeManager gm = GazeManager.getInstance();
    final GazeListener gazeListener = new GazeListener();

    /**
     * The class constructor.
     */
    public EyeTribeInstance() {
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
        if ("startCalibration".equalsIgnoreCase(eventPortID)) {
            return elpStartCalibration;
        }
        if ("offsetCorrection".equalsIgnoreCase(eventPortID)) {
            return elpOffsetCorrection;
        }
        if ("removeLastOffsetCorrection".equalsIgnoreCase(eventPortID)) {
            return elpRemoveLastOffsetCorrection;
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
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpStartCalibration = new IRuntimeEventListenerPort() {
        @Override
        public synchronized void receiveEvent(String data) {
            state = STATE_CALIBRATION;
            measuringClose = false;
            measuringFixation = false;
            calib.startCalibration();
        }
    };

    final void calibrationDone() {
        state = STATE_IDLE;
    }

    /**
     * Event Listener Port for offset correction.
     */
    final IRuntimeEventListenerPort elpOffsetCorrection = new IRuntimeEventListenerPort() {
        @Override
        public synchronized void receiveEvent(String data) {
            if (state == STATE_CALIBRATION) {
                return;
            }

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
    };

    final IRuntimeEventListenerPort elpRemoveLastOffsetCorrection = new IRuntimeEventListenerPort() {
        @Override
        public synchronized void receiveEvent(String data) {
            int remainingPoints = calib.removeOffsetPoint();
            System.out.println(
                    "Removed last offset correction point. Now there are " + remainingPoints + " points left.");
        }
    };

    private class GazeListener implements IGazeListener {
        @Override
        public synchronized void onGazeUpdate(GazeData gazeData) {
            // System.out.println(gazeData.toString());

            if (state == STATE_CALIBRATION) {
                return;
            }

            if ((gazeData.state & 7) == 0) // tracking lost ?
            {
                measuringClose = false;
                measuringFixation = false;
                return;
            }

            // here we have at least valid pupil coordinates (maybe also valid
            // gaze data ;)

            gazeX = (int) gazeData.smoothedCoordinates.x;
            gazeY = (int) gazeData.smoothedCoordinates.y;

            Point offset = calib.calcOffset(gazeX, gazeY); // look if we have an
                                                           // active offset
                                                           // correction point
            correctedGazeX = gazeX + offset.x;
            correctedGazeY = gazeY + offset.y;

            switch (propPupilPositionMode) {
            case POS_LEFT:
                eyeX = (int) (gazeData.leftEye.pupilCenterCoordinates.x * 1000);
                eyeY = (int) (gazeData.leftEye.pupilCenterCoordinates.y * 1000);
                if ((eyeX == 0) && (eyeY == 0)) {
                    eyePositionValid = false;
                } else {
                    eyePositionValid = true;
                }
                break;
            case POS_RIGHT:
                eyeX = (int) (gazeData.rightEye.pupilCenterCoordinates.x * 1000);
                eyeY = (int) (gazeData.rightEye.pupilCenterCoordinates.y * 1000);
                if ((eyeX == 0) && (eyeY == 0)) {
                    eyePositionValid = false;
                } else {
                    eyePositionValid = true;
                }
                break;
            case POS_BOTH:
                if (((gazeData.leftEye.pupilCenterCoordinates.x == 0)
                        && (gazeData.leftEye.pupilCenterCoordinates.y == 0))
                        || ((gazeData.rightEye.pupilCenterCoordinates.x == 0)
                                && (gazeData.rightEye.pupilCenterCoordinates.y == 0))) {
                    eyePositionValid = false;
                } else {
                    eyePositionValid = true;
                    eyeX = (int) ((gazeData.leftEye.pupilCenterCoordinates.x
                            + gazeData.rightEye.pupilCenterCoordinates.x) * 500);
                    eyeY = (int) ((gazeData.leftEye.pupilCenterCoordinates.y
                            + gazeData.rightEye.pupilCenterCoordinates.y) * 500);
                }
                break;
            }

            switch (state) {

            case STATE_INITIATE_CORRECTION: // get weak gaze point coordinates
                if (System.currentTimeMillis() >= offsetCorrectionStartTime + 1000) {
                    weakGazePointX = gazeX;
                    weakGazePointY = gazeY;

                    saveCorrectedGazeX = correctedGazeX;
                    saveCorrectedGazeY = correctedGazeY;

                    calib.playWavFile(CALIB_SOUND_NOTICE);

                    if (propOffsetCorrectionMode == MODE_AUTOCORRECTION) // continue
                                                                         // depending
                                                                         // on
                                                                         // mode
                    {
                        System.out.println("Got weak gaze spot for automatic correction");
                        state = STATE_AUTOCORRECTION;
                    } else {
                        System.out.println("Got weak gaze spot for manual correction");
                        // Point oldOffset =
                        // calib.getOffsetPoint(weakGazePointY, weakGazePointY);
                        currentManualOffsetX = offset.x;
                        currentManualOffsetY = offset.y;
                        state = STATE_MANUALCORRECTION;
                    }
                }
                break;

            case STATE_AUTOCORRECTION: // get estimated offset to desired
                                       // gazepoint
                if (System.currentTimeMillis() >= offsetCorrectionStartTime + 2000) {
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

                if ((currentXDirection > -MANUAL_CORRECTION_DEADZONE)
                        && (currentXDirection < MANUAL_CORRECTION_DEADZONE)) {
                    currentXDirection = 0;
                }
                if ((currentYDirection > -MANUAL_CORRECTION_DEADZONE)
                        && (currentYDirection < MANUAL_CORRECTION_DEADZONE)) {
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

                if (currentManualOffsetX < -200) {
                    currentManualOffsetX = -200;
                }
                if (currentManualOffsetX > 200) {
                    currentManualOffsetX = 200;
                }
                if (currentManualOffsetY < -200) {
                    currentManualOffsetX = -200;
                }
                if (currentManualOffsetY > 200) {
                    currentManualOffsetX = 200;
                }

                System.out.println("Manual correction: " + currentManualOffsetX + "/" + currentManualOffsetY);

                opGazeX.sendData(ConversionUtils.intToBytes(weakGazePointX + (int) currentManualOffsetX));
                opGazeY.sendData(ConversionUtils.intToBytes(weakGazePointY + (int) currentManualOffsetY));

                if (System.currentTimeMillis() >= offsetCorrectionStartTime + 7000) {
                    calib.newOffsetPoint(weakGazePointX, weakGazePointY, (int) currentManualOffsetX,
                            (int) currentManualOffsetY);
                    System.out.println("Manual correction finished.");
                    state = STATE_IDLE;
                    calib.playWavFile(CALIB_SOUND_NOTICE);
                }
                return;
            }

            if ((eyePositionValid == false) && (gazeX == 0)) // eyes closed
            {
                // System.out.print("*");

                if (measuringClose == false) {
                    startCloseTimestamp = System.currentTimeMillis();
                    measuringClose = true;
                }
                opCloseTime
                        .sendData(ConversionUtils.intToBytes((int) (System.currentTimeMillis() - startCloseTimestamp)));
            } else {
                // System.out.print("+");

                if (measuringClose == true) {
                    long blinktime = System.currentTimeMillis() - startCloseTimestamp;

                    if ((blinktime > propMinBlinkTime) && (blinktime < propMidBlinkTime)) {
                        etpBlink.raiseEvent();
                    } else if ((blinktime >= propMidBlinkTime) && (blinktime <= propMaxBlinkTime)) {
                        etpLongblink.raiseEvent();
                    }

                    measuringClose = false;
                }

                if ((gazeX != 0) || (gazeY != 0)) {
                    opGazeX.sendData(ConversionUtils.intToBytes(correctedGazeX));
                    opGazeY.sendData(ConversionUtils.intToBytes(correctedGazeY));
                }

                if (eyePositionValid) {
                    opPosX.sendData(ConversionUtils.intToBytes(eyeX));
                    opPosY.sendData(ConversionUtils.intToBytes(eyeY));
                }

                if ((gazeData.isFixated == true) && (measuringFixation == false)) {
                    startFixationTimestamp = System.currentTimeMillis();
                    measuringFixation = true;
                    sentFixationEvent = false;
                }

                if (gazeData.isFixated == false) {
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
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {

        boolean success = gm.activate(ApiVersion.VERSION_1_0, ClientMode.PUSH);

        if (success) {
            gm.addGazeListener(gazeListener);
        } else {
            AstericsErrorHandling.instance.reportError(this,
                    "Error initializing GazeManager, is EyeTribe Server running?");
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
        gm.removeGazeListener(gazeListener);
        gm.deactivate();

        super.stop();
    }
}