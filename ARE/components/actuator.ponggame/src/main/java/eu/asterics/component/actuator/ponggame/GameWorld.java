package eu.asterics.component.actuator.ponggame;


public class GameWorld 
{
	public static GameWorld instance = new GameWorld();
	
	GameBall ball = new GameBall();
	GameBall bumper = new GameBall();

	GameBall [] paddles = null;
	GameGoal [] goals = null;
	
	Player [] players = null;
	
	private GameWorld() {}
	
	public void setNbPlayers(int nbPlayers)
	{
		paddles = new GameBall[nbPlayers];
		goals = new GameGoal[nbPlayers];
		players = new Player[nbPlayers];
		
		for (int i = 0; i < nbPlayers; i++)
		{
			paddles[i] = new GameBall();
			goals[i] = new GameGoal();
			players[i] = new Player(i);
		}
	}
	
	public void playerMovementInput(int index)
	{
		players[index].movementInput++;
		players[index].inputs++;
	}

	public void playerDirectionToggle(int index)
	{
		players[index].up = !players[index].up;
	}
	
	public void playerPosInput(int index, int position)
	{
		if ((players != null) && (players[index] != null))
			players[index].position = position;
	}

	public void playerSpeedInput(int index, int speed)
	{
		if ((players != null) && (players[index] != null))
			players[index].speed = speed;
	}
}
