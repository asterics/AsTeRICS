package eu.asterics.component.actuator.ponggame;


import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.graphics.Texture;
import java.text.*;

public class Energy2Screen
    extends
        AbstractScreen
{
    private boolean goToNextScreen = false;
    private Texture backgroundImage;
    private int p1Calories=0,p2Calories=0;

	public Energy2Screen(
        AstericsPong game )
    {
        super( game );
    }

    @Override
    public void show()
    {
        super.show();
        backgroundImage=loadImage("data/actuator.ponggame/energy2background.jpg");
        
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
         
        formatter = new DecimalFormat("###.#");
       
        numOutput = formatter.format((double)(p1Calories+p2Calories)*0.06978);    // 60W
        darkfont.draw(getBatch()," " +numOutput+" Sekunden lang", 500, 780);
        darkfont.draw(getBatch(),"       eine Gluehlampe verwenden", 500, 740);

        numOutput = formatter.format((double)(p1Calories+p2Calories)*0.06978/3);   //180 W
        darkfont.draw(getBatch()," " +numOutput+" Sekunden fernsehen", 500, 580);

        numOutput = formatter.format((double)(p1Calories+p2Calories)*0.06978/30);   // 1800 W
        darkfont.draw(getBatch()," " +numOutput+" Sekunden Haare foehnen", 500, 360);

        numOutput = formatter.format((double)(p1Calories+p2Calories)*0.001163/11.1/0.75*10);   
        darkfont.draw(getBatch()," " +numOutput+" Meter mit einem Sportwagen fahren", 40, 150);

        
        getBatch().end();

        
		
		if (goToNextScreen && !screenSwitchActive)
		{
			for (int i = 0; i < 2; i++)
			{
				Player p = GameWorld.instance.players[i];
				if (p.score > AstericsPong.instance.lowestHighScore)
				{
					game.setScreen(new HighScoreEnterScreen(game, i));
					screenSwitchActive = true;					
					return;
				}
			}
			setNextScreen(new HighScoreScreen(game));
			screenSwitchActive = true;			
		}
	}    
}

