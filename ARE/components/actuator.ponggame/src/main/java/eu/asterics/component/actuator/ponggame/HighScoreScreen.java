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


import java.io.IOException;
import java.io.LineNumberReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class HighScoreScreen
    extends
        AbstractScreen
{
	
	final int STATE_CHECK_PLAYER1 = 1;
	final int STATE_CHECK_PLAYER2 = 2;
	final int STATE_DISPLAY_LIST = 3;
	
	int state;
	private Texture backgroundImage;
	boolean showNextScreen = false;
	
    public HighScoreScreen(AstericsPong game )
    {
        super( game );
    }

    
   
    @Override
    public void show()
    {
        super.show();

        backgroundImage=loadImage("data/actuator.ponggame/energy.jpg");        // CRASHES (?)

        // retrieve the default table actor
        Table table = getTable();
        table.add( "EnergyPong HIGHSCORES" ).spaceBottom( 50 );
        table.row();
        for (HighScore s: AstericsPong.instance.scores)
        {
        	table.add("   " + s.name+ "   ");
        	table.add("   " + s.score + "   ");
        	table.row();
        }

    }
    
    
	@Override
	public void render(float delta) {
		super.render(delta);
		
        getBatch().begin();
		if (PongGameProperties.eventsToCaloryMultiplier>0)
			drawImage(backgroundImage,40,400);
		getBatch().end();
		
		if (showNextScreen && !screenSwitchActive)
		{
			game.setScreen(new GameScreen(game));
			screenSwitchActive = true;
		}
   	}    

	
    
	public void auxiliaryButtonInput()
	{
		PongGameServer.reset(2);                              // CRASHES (?)
		showNextScreen = true;
		
	}
	
	public void playerMovementInput(int index)
	{
	}
	
	public void playerPosInput(int index, int position)
	{
	}
	
	public void playerSpeedInput(int index, int speed)
	{
	}
	
	public void playerDirectionToggle(int index)
	{
	}    
}

