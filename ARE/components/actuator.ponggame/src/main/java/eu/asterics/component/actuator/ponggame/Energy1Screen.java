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

public class Energy1Screen
    extends
        AbstractScreen
{
    private boolean goToNextScreen = false;
    private Texture backgroundImage;
    private int p1Calories=0,p2Calories=0;

	public Energy1Screen(
        AstericsPong game )
    {
        super( game );
    }

    @Override
    public void show()
    {
        super.show();
        backgroundImage=loadImage("data/actuator.ponggame/energy1background.jpg");
        
        p1Calories=(int)(PongGameProperties.eventsToCaloryMultiplier*GameWorld.instance.players[0].inputs);
        p2Calories=(int)(PongGameProperties.eventsToCaloryMultiplier*GameWorld.instance.players[1].inputs);
        
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
        
        drawImage(backgroundImage,0,0);

        String numOutput;
        DecimalFormat formatter;
        
        formatter = new DecimalFormat("###.#############");
       
        numOutput = formatter.format((double)(p1Calories+p2Calories)/10000);
        darkfont.draw(getBatch()," " +numOutput+" Gramm Erdoel", 100, 150);

        numOutput = formatter.format((double)(p1Calories+p2Calories)*0.00142857);
        darkfont.draw(getBatch()," " +numOutput+" Gramm Kohle", 830, 150);

		getBatch().end();

        
		if (goToNextScreen && !screenSwitchActive)
		{
			screenSwitchActive = true;
			game.setScreen(new Energy2Screen(game));
		}
	}    
}

