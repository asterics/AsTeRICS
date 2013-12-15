package eu.asterics.component.actuator.ponggame;

public class Player 
{
	String name;
	int lifes;

	int score = 0;
	int calories = 0;
	int inputs = 0;
	int goalsFor = 0;
	
	boolean up = false; // true = up

	public int index;
	public int movementInput;
	
	public int speed;
	public int position;
	
	public Player(int i)
	{
		lifes = PongGameProperties.goalsToWin;
		index = i; 
		name = "Spieler " + (i+1); 
	} 

}
