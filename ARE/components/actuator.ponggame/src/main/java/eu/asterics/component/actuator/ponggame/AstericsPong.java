package eu.asterics.component.actuator.ponggame;

import java.awt.Dimension;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;

public class AstericsPong extends Game 
{
	public static final String LOG = AstericsPong.class.getSimpleName();
	
	public static AstericsPong instance = null;
	
	Standings standings = null;
	boolean gameLogicRunning = false;
    public float scaleFactor= 1;

	Player player ;
	
	GameScreen gameScreen;

	
	List<HighScore> scores = null;
	int lowestHighScore = 0;
	FileHandle highscores;
	private Dimension availableScreenSize=null;
	
    void initHighScores()
    {
    	String line;

    	scores = new ArrayList<HighScore>();
    	highscores = Gdx.files.local("data/actuator.ponggame/highscores.txt");
        if (highscores.exists())
        {
        	LineNumberReader reader = new LineNumberReader(highscores.reader());
        	try {
				while (scores.size() <= 10)
				{
					line = reader.readLine();
					if (line == null)
						break;
					System.out.println("Line: " + line);
					StringTokenizer tok = new StringTokenizer(line);
					HighScore s = new HighScore();
					s.name = tok.nextToken();
					s.score = Integer.parseInt(tok.nextToken());
					lowestHighScore = s.score;
					scores.add(s);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        Collections.sort(scores);
        for (HighScore s: scores)
        {
        	System.out.println("..." +  s.name + " " + s.score);
        }
        
        
    }
	
	
	
	private AstericsPong(Dimension availableScreenSize)
	{
		this.availableScreenSize=availableScreenSize;
	}
	
	public static void reset(Dimension availableScreenSize)
	{
		instance = new AstericsPong(availableScreenSize);
		 System.out.println("reset to screen size "+availableScreenSize.height+"/"+availableScreenSize.width);

	}
	
	@Override
	public void create() 
	{
		initHighScores();
		PongGameServer.reset(2);
		createGameScreen();
		gameLogicRunning = true;
		setScreen(gameScreen);
	}
	
	public void auxiliaryButtonInput()
	{
		AbstractScreen screen =(AbstractScreen) getScreen(); 
		if (screen != null)
			screen.auxiliaryButtonInput();
	}

	public void playerMovementInput(int index)
	{
		AbstractScreen screen =(AbstractScreen) getScreen(); 
		if (screen != null)
			screen.playerMovementInput(index);
	}
	
	public void playerPosInput(int index, int position)
	{
		AbstractScreen screen =(AbstractScreen) getScreen(); 
		if (screen != null)
			screen.playerPosInput(index, position);
	}

	public void playerSpeedInput(int index, int speed)
	{
		AbstractScreen screen =(AbstractScreen) getScreen(); 
		if (screen != null)
			screen.playerSpeedInput(index, speed);
	}
	
	public void playerDirectionToggle(int index)
	{
		AbstractScreen screen =(AbstractScreen) getScreen(); 
		if (screen != null)
			screen.playerDirectionToggle(index);
	}

	protected void createGameScreen() {
		gameScreen = new GameScreen(this);
		gameScreen.setScaleFactor(availableScreenSize);
	}
	
	@Override
	public void dispose() {
	}

	boolean [] keysPressed = new boolean[10];

	@Override
	public void render() 
	{
//		gameScreen.updateGameWorldOutputs(GameWorld.instance);
		super.render();
//		Gdx.gl.glClearColor(0, 0, 0, 1);
//		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
//		renderer.render(gameLogic.world, camera.combined);
	
		if (Gdx.input.isKeyPressed(Keys.A))
		{
			if (!keysPressed[0])
			{
				this.playerMovementInput(0);
				keysPressed[0] = true;
			}
		}
		else
		{
			keysPressed[0] = false;
		}

		if (Gdx.input.isKeyPressed(Keys.S))
		{
			if (!keysPressed[1])
			{
				this.playerDirectionToggle(0);
				keysPressed[1] = true;
			}
		}
		else
		{
			keysPressed[1] = false;
		}
		
		if (Gdx.input.isKeyPressed(Keys.K))
		{
			if (!keysPressed[2])
			{
				this.playerMovementInput(1);
				keysPressed[2] = true;
			}
		}
		else
		{
			keysPressed[2] = false;
		}

		if (Gdx.input.isKeyPressed(Keys.L))
		{
			if (!keysPressed[3])
			{
				this.playerDirectionToggle(1);
				keysPressed[3] = true;
			}
		}
		else
		{
			keysPressed[3] = false;
		}

		if (Gdx.input.isKeyPressed(Keys.T))
		{
			if (!keysPressed[4])
			{
				this.auxiliaryButtonInput();
				keysPressed[4] = true;
			}
		}
		else
		{
			keysPressed[4] = false;
		}



	
			//world.step(1 / 60f, 6, 2);
			
//			System.out.println("Client: GameWorld message sent");
	}
	

	public void startGame() {
//		gameLogic.startGame();
	}
	
	public void stopGame()
	{
		PongGameServer.instance.endGame();
//		gameLogic.endGame();
	}


	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}



	public void addHighScore(char[] name, int score) {
		HighScore scre = new HighScore(new String(name), score);
		scores.add(scre);
        Collections.sort(scores);
		while (scores.size() > 10)
		{
			scores.remove(10);
		}
		this.lowestHighScore = scores.get(scores.size() - 1).score;
		
		boolean append = false;
		for (HighScore s: scores)
		{
			highscores.writeString(s.name + " " + s.score, append);
			append = true;
			highscores.writeString("\n", append);
		}
	}

}
