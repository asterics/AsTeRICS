package eu.asterics.component.actuator.ponggame;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.graphics.Texture;


public class GameScreen extends AbstractScreen 
{
	final float PHYS_TO_REND = 100;
	
	public final static boolean useDebugRenderer = false;
	
	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	private Box2DDebugRenderer renderer;
	private Texture ballImage;

	public GameScreen(AstericsPong game)
	{
		super(game);
		
		
	    //ballImage=loadImage("data/actuator.ponggame/ball.png");   // CRASHES (?)

		if (useDebugRenderer)
		{
			camera = new OrthographicCamera(10, 8);
			camera.position.set(5, 4, 0);
		}
		else
		{
			camera = new OrthographicCamera(1000, 800);
			camera.position.set(500, 400, 0);
		}
		camera.update();
		if (useDebugRenderer)
			renderer = new Box2DDebugRenderer();
		else
			shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		if (useDebugRenderer)
		{
			if (PongGameServer.instance.world != null)
			{
				renderer.render(PongGameServer.instance.world, camera.combined);
			}
		}
		else
		{
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.setColor(0, 1, 0, 1);

			if (GameWorldStatic.instance != null)
			{
				// render bounds
				if (GameWorldStatic.instance.boundVectors != null)
				{
					shapeRenderer.begin(ShapeType.Line);
					for (int i = 1; 
							i < GameWorldStatic.instance.boundVectors.length; i++)
					{
						shapeRenderer.line(
								GameWorldStatic.instance.boundVectors[i].x*PHYS_TO_REND, 
								GameWorldStatic.instance.boundVectors[i].y*PHYS_TO_REND, 
								GameWorldStatic.instance.boundVectors[i-1].x*PHYS_TO_REND, 
								GameWorldStatic.instance.boundVectors[i-1].y*PHYS_TO_REND);
					}
					shapeRenderer.end();
				}

				if (GameWorldStatic.instance.goalVerteces!= null)
				{
					shapeRenderer.setColor(1, 0.2f, 0.2f, 1);
					shapeRenderer.begin(ShapeType.Line);
					for (int i = 0; i < GameWorldStatic.instance.goalVerteces.length / 8; i++)
					{
						for (int j = 0; j < 8; j += 2)
						{
							shapeRenderer.line(
									GameWorldStatic.instance.goalVerteces[i*8 + ((j + 0) % 8)]*PHYS_TO_REND, 
									GameWorldStatic.instance.goalVerteces[i*8 + ((j + 1) % 8)]*PHYS_TO_REND, 
									GameWorldStatic.instance.goalVerteces[i*8 + ((j + 2) % 8)]*PHYS_TO_REND, 
									GameWorldStatic.instance.goalVerteces[i*8 + ((j + 3) % 8)]*PHYS_TO_REND);
						}
					}
					shapeRenderer.end();
				}

			}

			if (GameWorld.instance != null)
			{
				shapeRenderer.setColor(0, 1, 0, 1);
				// render paddles
				shapeRenderer.begin(ShapeType.Filled);
				for (GameBall ball : GameWorld.instance.paddles)
				{
					if (ball.type == 0)
					{
						shapeRenderer.circle(
								ball.x * PHYS_TO_REND, 
								ball.y * PHYS_TO_REND, 
								ball.radius * PHYS_TO_REND);
					}
					else if (ball.type == 1)
					{
						shapeRenderer.rect(
								(ball.x - ball.width ) * PHYS_TO_REND, 
								(ball.y - ball.radius)* PHYS_TO_REND,
								ball.width * 2 * PHYS_TO_REND,
								ball.radius * 2 * PHYS_TO_REND);
					}
				}

				shapeRenderer.circle(
						GameWorld.instance.ball.x * PHYS_TO_REND, 
						GameWorld.instance.ball.y * PHYS_TO_REND, 
						GameWorld.instance.ball.radius * PHYS_TO_REND);

				shapeRenderer.circle(
						GameWorld.instance.bumper.x * PHYS_TO_REND, 
						GameWorld.instance.bumper.y * PHYS_TO_REND, 
						GameWorld.instance.bumper.radius * PHYS_TO_REND);
				
				int lifes = GameWorld.instance.players[0].lifes;
				for (int i = 0; i < Math.min(5, lifes); i++)
				{
					shapeRenderer.circle(
							0.2f * PHYS_TO_REND, 
							(2.0f + i * 3 * GameWorld.instance.ball.radius) * PHYS_TO_REND, 
							GameWorld.instance.ball.radius * PHYS_TO_REND);
				}
				
				lifes = GameWorld.instance.players[1].lifes;
				for (int i = 0; i < Math.min(5, lifes); i++)
				{
					shapeRenderer.circle(
							9.3f * PHYS_TO_REND, 
							(2.0f + i * 3 * GameWorld.instance.ball.radius) * PHYS_TO_REND, 
							GameWorld.instance.ball.radius * PHYS_TO_REND);
				}

				shapeRenderer.end();
			}
			getBatch().begin();
			for (Player p : GameWorld.instance.players)
			{
				getFont().draw(getBatch(), p.name 
						+ " : " + p.score, (4.0f +  9*p.index) * getScaleFactor() * PHYS_TO_REND , 0.80f * PHYS_TO_REND * getScaleFactor());
				
				//for (int i=0; i<p.lifes;i++)
				//	drawImage(ballImage,(int)((1.5f +  9 * p.index + i*0.6f)* PHYS_TO_REND) , (int)(0.50f * PHYS_TO_REND));

			}


			getBatch().end();
		}
		if (PongGameServer.instance.gameState == PongGameServer.GAME_STATE_OVER)
		{
			System.out.println("Showing game over screen");
			game.setScreen(new GameOverScreen(game));
		}
	}
	
	void updateGameWorldOutputs(GameWorld gw)
	{
//		
//		if (GameWorld.instance == null)
//		{
//			gameWorld = gw;
//		}
//		else
//		{
//			gameWorld.ball.x = gw.ball.x;
//			gameWorld.ball.y = gw.ball.y;
//			gameWorld.ball.radius = gw.ball.radius;
//			
//			for (int i = 0; i < gw.paddles.length; i++)
//			{
//				gameWorld.paddles[i].x = gw.paddles[i].x;
//				gameWorld.paddles[i].y = gw.paddles[i].y;
//				gameWorld.paddles[i].radius = gw.paddles[i].radius;
//			}
//	
//			gameWorld.bumper.x = gw.bumper.x;
//			gameWorld.bumper.y = gw.bumper.y;
//			gameWorld.bumper.radius = gw.bumper.radius;
//		}
	}

		
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	public void auxiliaryButtonInput()
	{
		PongGameServer.instance.startGame();
	}
	
	public void playerMovementInput(int index)
	{
		GameWorld.instance.playerMovementInput(index);
	}
	
	public void playerPosInput(int index, int position)
	{
		GameWorld.instance.playerPosInput(index, position);
	}

	public void playerSpeedInput(int index, int speed)
	{
		GameWorld.instance.playerSpeedInput(index, speed);
	}
	
	public void playerDirectionToggle(int index)
	{
		GameWorld.instance.playerDirectionToggle(index);
	}

}
