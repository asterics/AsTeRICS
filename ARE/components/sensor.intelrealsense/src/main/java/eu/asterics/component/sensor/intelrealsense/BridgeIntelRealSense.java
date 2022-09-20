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

package eu.asterics.component.sensor.intelrealsense;

import eu.asterics.mw.data.ConversionUtils;

public class BridgeIntelRealSense {

    private IntelRealSenseInstance owner;

    final int BROW_RAISER_LEFT = 1;
    final int BROW_RAISER_RIGHT = 0;
    final int BROW_LOWERER_LEFT = 3;
    final int BROW_LOWERER_RIGHT = 2;
    final int SMILE = 4;
    final int KISS = 5;
    final int MOUTH_OPEN = 6;
    final int TONGUE_OUT = 7;
    final int EYES_CLOSED_LEFT = 9;
    final int EYES_CLOSED_RIGHT = 8;
    final int EYES_TURND_LEFT = 11;
    final int EYES_TURND_RIGHT = 10;
    final int EYES_UP = 12;
    final int EYES_DOWN = 13;
    final int PUFF_LEFT = 15;
    final int PUFF_RIGHT = 14;

    static {
        System.out.println("Trying to load native library...");
        System.loadLibrary("IntelRealSense");
        // System.load("C:/AsTeRICS_PREP/ARE/components/sensor.realsense/src/main/resources/lib/native/IntelF200.dll");
        System.out.println("Native lib loaded successfully");
    }

    // interface definition for f200 communication
    native public int init(int devNr, int enableExpressions, int displayGUI);

    native public void deactivate();

    native public void startTracking();

    native public void pause();

    native public void resume();

    // The class constructor.
    public BridgeIntelRealSense(final IntelRealSenseInstance owner) {
        this.owner = owner;
    }
    /*
     * public void start(int devNr,int enableExpressions,int displayGUI) {
     * 
     * new Thread(new Runnable() {
     * 
     * @Override public void run() { for(int i=0;i<1;i++){
     * if(BridgeF200.this.init(devNr,enableExpressions,displayGUI)==-1){
     * System.out.println("Init failed");break; } System.out.println(
     * "Init passed"); BridgeF200.this.startTracking(); } } }).start();
     * 
     * }
     */

    // Callback from cpp -> java
    private void poseDataCallback(int h, int w, int x, int y, int roll, int yaw, int pitch) {
        // h ... The rectangle height in pixels.
        // w ... The rectangle width in pixels.
        // x ... The horizontal coordinate of the top left pixel of the
        // rectangle.
        // y ... The vertical coordinate of the top left pixel of the rectangle.
        owner.opH.sendData(ConversionUtils.intToBytes(h));
        owner.opW.sendData(ConversionUtils.intToBytes(w));
        owner.opX.sendData(ConversionUtils.intToBytes(x));
        owner.opY.sendData(ConversionUtils.intToBytes(y));
        owner.opRoll.sendData(ConversionUtils.intToBytes(roll));
        owner.opYaw.sendData(ConversionUtils.intToBytes(yaw));
        owner.opPitch.sendData(ConversionUtils.intToBytes(pitch));
    }

    // Callback expressions
    private void expressionCallback(int[] expressionScores) {
        if (expressionScores[BROW_RAISER_LEFT] > owner.propScoreBrowRaiserLeft) {
            owner.etpBrowRaiserLeft.raiseEvent();
        }
        ;
        if (expressionScores[BROW_RAISER_RIGHT] > owner.propScoreBrowRaiserRight) {
            owner.etpBrowRaiserRight.raiseEvent();
        }
        ;
        if (expressionScores[BROW_LOWERER_LEFT] > owner.propScoreBrowLowererLeft) {
            owner.etpBrowLowererLeft.raiseEvent();
        }
        ;
        if (expressionScores[BROW_LOWERER_RIGHT] > owner.propScoreBrowLowererRight) {
            owner.etpBrowLowererRight.raiseEvent();
        }
        ;
        if (expressionScores[SMILE] > owner.propScoreSmile) {
            owner.etpSmile.raiseEvent();
        }
        ;
        if (expressionScores[KISS] > owner.propScoreKiss) {
            owner.etpKiss.raiseEvent();
        }
        ;
        if (expressionScores[MOUTH_OPEN] > owner.propScoreMouthOpen) {
            owner.etpMouthOpen.raiseEvent();
        }
        ;
        if (expressionScores[TONGUE_OUT] > owner.propScoreThongueOut) {
            owner.etpThongueOut.raiseEvent();
        }
        ;
        if (expressionScores[EYES_CLOSED_LEFT] > owner.propScoreEyesClosedLeft) {
            owner.etpEyesClosedLeft.raiseEvent();
        }
        ;
        if (expressionScores[EYES_CLOSED_RIGHT] > owner.propScoreEyesClosedRight) {
            owner.etpEyesClosedRight.raiseEvent();
        }
        ;
        if (expressionScores[EYES_TURND_LEFT] > owner.propScoreEyesTurnLeft) {
            owner.etpEyesTurnLeft.raiseEvent();
        }
        ;
        if (expressionScores[EYES_TURND_RIGHT] > owner.propScoreEyesTurnRight) {
            owner.etpEyesTurnRight.raiseEvent();
        }
        ;
        if (expressionScores[EYES_UP] > owner.propScoreEyesUp) {
            owner.etpEyesUp.raiseEvent();
        }
        ;
        if (expressionScores[EYES_DOWN] > owner.propScoreEyesDown) {
            owner.etpEyesDown.raiseEvent();
        }
        ;
        if (expressionScores[PUFF_LEFT] > owner.propScorePuffLeft) {
            owner.etpPuffLeft.raiseEvent();
        }
        ;
        if (expressionScores[PUFF_RIGHT] > owner.propScorePuffRight) {
            owner.etpPuffRight.raiseEvent();
        }
        ;
    }

}
