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
		setNextScreen(new Energy2Screen(game));
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

        
		if (nextScreen != null)
		{
			game.setScreen(nextScreen);
		}
	}    
}

