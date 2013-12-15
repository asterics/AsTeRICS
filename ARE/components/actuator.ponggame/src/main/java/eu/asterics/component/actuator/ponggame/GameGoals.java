package eu.asterics.component.actuator.ponggame;

public class GameGoals 
{
	GameGoal [] goals;
	
	public GameGoals(int nb)
	{
		goals = new GameGoal[nb];
		for (int i = 0; i < nb; i++)
		{
			goals[i] = new GameGoal();
		}
	}
}
