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

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class HighScoreEnterScreen
    extends
        AbstractScreen
{
	char [] name = { 'A', '.', '.' };
	
	int state = 0;
	int index;

	private boolean showNextScreen = false;
	
    public HighScoreEnterScreen(AstericsPong game, int index )
    {
        super( game );
        this.index = index;
    }

    @Override
    public void show()
    {
        super.show();

        // retrieve the default table actor
        Table table = getTable();
        table.clear();
        table.add( "NEW HIGHSCORE" ).spaceBottom( 20 );
        table.row();
        table.add( "Score: " + GameWorld.instance.players[index].score ).spaceBottom( 20 );
        table.row();
        
        
        table.add( "PLAYER " + (index + 1) ).spaceBottom( 50 );
        table.row();
        table.add(new String(name));
    }
    
	public void auxiliaryButtonInput()
	{
		state++;
		if (state >= 3)
		{
			Player p = GameWorld.instance.players[index];
			game.addHighScore(name, p.score);
			showNextScreen = true;
			state = 0;
//			show();
		}
		else
		{
			name[state] = 'A';
			show();
		}
	}
	
	public void playerMovementInput(int index)
	{
		if (this.index == index)
		{
			name[state]++;
			if (name[state] > 'Z')
			{
				name[state] = 'A';
			}
			show();
		}
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



	@Override
	public void render(float delta) {
		super.render(delta);
		
		if (showNextScreen && !screenSwitchActive)
		{
			screenSwitchActive = true;
			if ((index + 1) < 2)
			{
		        Player p = GameWorld.instance.players[index + 1];
		        if (p.score > AstericsPong.instance.lowestHighScore)
		        {
		        	game.setScreen(new HighScoreEnterScreen(game, index + 1));
		    		showNextScreen = false;
		        	return;
		        }
			}
			game.setScreen(new HighScoreScreen(game));
		}
	}    
}

