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

