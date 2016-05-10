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
import com.badlogic.gdx.graphics.Texture;
import java.text.*;

public class GameOverScreen
    extends
        AbstractScreen
{
    private boolean goToNextScreen = false;
    private Texture backgroundImage;
    private int p1Calories=0,p2Calories=0;

	public GameOverScreen(
        AstericsPong game )
    {
        super( game );
    }

    @Override
    public void show()
    {
        super.show();
/*
        // retrieve the default table actor
        Table table = getTable();
        table.add( "GAME OVER!" ).spaceBottom( 50 );
        table.row();
        Player pl = (GameWorld.instance.players[0].lifes > GameWorld.instance.players[1].lifes) ?
        		GameWorld.instance.players[0] : GameWorld.instance.players[1];

        // register the button "start game"
        table.add( pl.name + " WINS!" ).spaceBottom( 100 );
        table.row();
        table.add("");
        for (Player p : GameWorld.instance.players)
        {
        	table.add(p.name);
        }
        table.row();
        table.add("Score:");
        for (Player p : GameWorld.instance.players)
        {
        	table.add("   " + p.score + "   ");
        }
        table.row();
        table.add("Calories:");
        for (Player p : GameWorld.instance.players)
        {
        	table.add("   " + PongGameProperties.eventsToCaloryMultiplier * p.inputs + "   ");
        }
        table.row();
*/      

        backgroundImage=loadImage("data/actuator.ponggame/gameoverbackground.jpg");
        
        p1Calories=(int)(PongGameProperties.eventsToCaloryMultiplier*GameWorld.instance.players[0].inputs);
        p2Calories=(int)(PongGameProperties.eventsToCaloryMultiplier*GameWorld.instance.players[1].inputs);
        game.sendGameOver();
        
    }
    
	public void auxiliaryButtonInput()
	{
		goToNextScreen = true;
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

	@Override
	public void render(float delta) {
		super.render(delta);
		
        getBatch().begin();
        
		if (PongGameProperties.eventsToCaloryMultiplier>0)
			drawImage(backgroundImage,0,0);
		
        getFont().draw(getBatch(), "Game Over !", 620* getScaleFactor(), 770* getScaleFactor());
        
        Player pl = (GameWorld.instance.players[0].score > GameWorld.instance.players[1].score) ?
        		GameWorld.instance.players[0] : GameWorld.instance.players[1];
        

		if (PongGameProperties.eventsToCaloryMultiplier>0)
		{
	        String numOutput;
	        DecimalFormat formatter;
	        
	        pl = (p1Calories > p2Calories) ? 
        		GameWorld.instance.players[0] : GameWorld.instance.players[1];
        
        		getFont().draw(getBatch(), pl.name + " siegt nach Kalorien "
        	        		//+p1Calories+" vs. "+p2Calories+")"
        	        		, 430* getScaleFactor(), 580* getScaleFactor());
        		
    	        formatter = new DecimalFormat("###.#############");
    	        
    	        getFont().draw(getBatch(),"Gesamtenergie: ", 560* getScaleFactor(), 445* getScaleFactor());
    	        numOutput = formatter.format((double)(p1Calories+p2Calories)/1000);
    	        getFont().draw(getBatch()," "+numOutput+" Kilo-Kalorien (kCal).", 390* getScaleFactor(), 390* getScaleFactor());
    	        numOutput = formatter.format((double)(p1Calories+p2Calories)*0.000001163);
    	        getFont().draw(getBatch()," "+numOutput+" Kilowatt-Stunden (kWh)", 390* getScaleFactor(), 335* getScaleFactor());

    	        getFont().draw(getBatch(), pl.name + " siegt nach Punkten "
    	            		, 450* getScaleFactor(), 630* getScaleFactor());

		}
		else
		{
	        getFont().draw(getBatch(), pl.name + " siegt nach Punkten: "
	            		+GameWorld.instance.players[0].score+" vs. " + GameWorld.instance.players[1].score
	            		, 450* getScaleFactor(), 630* getScaleFactor());
		}


        getBatch().end();
        
		if (goToNextScreen && !screenSwitchActive)
		{
			screenSwitchActive = true;
			if (PongGameProperties.eventsToCaloryMultiplier>0)
				game.setScreen(new Energy1Screen(game));
			else
			{
				for (int i = 0; i < 2; i++)
				{
					Player p = GameWorld.instance.players[i];
					if (p.score > AstericsPong.instance.lowestHighScore)
					{
						screenSwitchActive = true;					
						game.setScreen(new HighScoreEnterScreen(game, i));
						return;
					}
				}
				screenSwitchActive = true;			
				game.setScreen(new HighScoreScreen(game));				
			}
		}
	}    
}

