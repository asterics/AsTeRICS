package eu.asterics.component.actuator.ponggame;

import com.badlogic.gdx.math.Vector2;

public class GameBounds 
{
	Vector2 [] boundVectors = null;
	
	public GameBounds()
	{
		
	}
	
	public GameBounds(Vector2 [] boundVectors)
	{
		this.boundVectors = boundVectors;
	}
}