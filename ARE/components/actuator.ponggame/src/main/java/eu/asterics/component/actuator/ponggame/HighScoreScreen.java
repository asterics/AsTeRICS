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

