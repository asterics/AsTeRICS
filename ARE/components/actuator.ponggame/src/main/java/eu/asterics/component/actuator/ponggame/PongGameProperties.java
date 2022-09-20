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

package eu.asterics.component.actuator.ponggame;

public class PongGameProperties {
    public static final int CONTROLMODE_POSITION = 0;
    public static final int CONTROLMODE_SPEED = 1;
    public static final int CONTROLMODE_EVENT = 2;

    // public static boolean useSpeedInputs = true;
    public static int controlMode = CONTROLMODE_SPEED;

    public static String soundFileWallTouch = "data/sound/pong_1.wav";
    public static String soundFilePaddleTouch = "data/sound/pong_2.wav";
    public static String soundFileGoal = "data/sound/pong_3.wav";
    public static String soundFileEndGame = "data/sound/pong_3.wav";

    public static int goalsToWin = 5;
    public static double eventsToCaloryMultiplier = 5.0;
    public static double speedStep = 0.2f;

    public static int propGoalTouchBase = 10;

    public static int propGoalScoreBase = 1000;

    public static long propResetWaitTime = 2000;

    public static float propMaxSpeed = 5.0f;
    public static float propMinXSpeed = 1.0f;
    public static float propReflectionYImpulse = 0.5f;

}