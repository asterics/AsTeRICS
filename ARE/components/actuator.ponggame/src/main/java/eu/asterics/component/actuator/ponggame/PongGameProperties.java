package eu.asterics.component.actuator.ponggame;


public class PongGameProperties
{
	public static final int CONTROLMODE_POSITION = 0;
	public static final int CONTROLMODE_SPEED = 1;
	public static final int CONTROLMODE_EVENT = 2;

	//public static boolean useSpeedInputs = true;
	public static int controlMode = CONTROLMODE_SPEED;
	
	public static String soundFileWallTouch = "data/sound/pong_1.wav";
	public static String soundFilePaddleTouch = "data/sound/pong_2.wav";
	public static String soundFileGoal = "data/sound/pong_3.wav";
	public static String soundFileEndGame = "data/sound/pong_3.wav";
	
	public static int goalsToWin = 5;
	public static double eventsToCaloryMultiplier = 5.0;
	public static double speedStep = 0.2f;

	public static  int propGoalTouchBase = 10; 

	public static  int propGoalScoreBase = 1000;
	
	public static  long propResetWaitTime= 2000;

	public static  float propMaxSpeed = 5.0f;
	public static  float propMinXSpeed = 1.0f;
	public static  float propReflectionYImpulse = 0.5f;
	
	
}