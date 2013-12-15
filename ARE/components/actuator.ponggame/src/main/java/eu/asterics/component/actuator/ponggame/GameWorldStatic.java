package eu.asterics.component.actuator.ponggame;

import com.badlogic.gdx.math.Vector2;

public class GameWorldStatic 
{
	
	static GameWorldStatic instance = new GameWorldStatic();
	
//	GameBounds bounds;
	Vector2 []boundVectors;
	float [] goalVerteces;
	
	private GameWorldStatic()
	{
	}
	
}
