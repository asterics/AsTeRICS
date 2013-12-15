package eu.asterics.component.actuator.ponggame;

import java.util.Hashtable;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.*;

public class PongGameServer extends Thread implements ContactListener 
{
	public static PongGameServer instance = null;
	
	Sound wallSound = Gdx.audio.newSound(Gdx.files.local(PongGameProperties.soundFileWallTouch));	
	Sound paddleSound = Gdx.audio.newSound(Gdx.files.local(PongGameProperties.soundFilePaddleTouch));
	Sound goalSound = Gdx.audio.newSound(Gdx.files.local(PongGameProperties.soundFileGoal));
	Sound endSound = Gdx.audio.newSound(Gdx.files.local(PongGameProperties.soundFileEndGame));
	
	public static final int GAME_STATE_WAIT_FOR_PLAYERS= 0;
	public static final int GAME_STATE_RESET_FIELD = 1;
	public static final int GAME_STATE_PLAY = 2;
	public static final int GAME_STATE_OVER = 3;
	
	private static final int GOAL_SCORE_BASE = PongGameProperties.propGoalScoreBase;
	private static final int TOUCH_SCORE_BASE = PongGameProperties.propGoalTouchBase;
	
	private static final int PADDLE_TYPE_CIRCLE = 0;
	private static final int PADDLE_TYPE_RECT = 1;
	
	private static final long RESET_WAIT_TIME = PongGameProperties.propResetWaitTime;
	private static final float FIELD_CENTER = 4.8f;
	
	private static final float MAX_SPEED = PongGameProperties.propMaxSpeed;
	private static final float MIN_X_SPEED = PongGameProperties.propMinXSpeed;
	private static final float RECT_IMPULSE_BASE = PongGameProperties.propReflectionYImpulse;

	World world = null;
	long lastStepTime = 0;
	float stepTime = 1f / 60f; 
	float stepTimeMs = stepTime * 1000; 
	
	int nbPlayers = 2;

	boolean ballHadFirstContact = false;
	
	class PaddleDescriptor 
	{
		public PaddleDescriptor(float xMin, float xMax, float yMin, float yMax,
				float stepSize, float radius, float dx, float dy, int type) {
			super();
			this.xMin = xMin;
			this.xMax = xMax;
			this.yMin = yMin;
			this.yMax = yMax;
			this.stepSize = stepSize;
			this.radius = radius;
			this.dx = dx; 
			this.dy = dy;
			this.type = type;
		}

		float xMin, xMax, yMin, yMax, stepSize, radius, dx, dy;
		int type;
		float width = 0.05f;
	}
	
	
	// position and properties of paddles
	PaddleDescriptor [] twoPlayerPaddleDesc = 
	{
			new PaddleDescriptor(	1.25f, 1.25f, 
									0.8f, 7.8f, 
									0.05f, 0.5f, 
									0, 0, PADDLE_TYPE_RECT),
			new PaddleDescriptor(	8.25f, 8.25f, 
									0.8f, 7.8f, 
									0.05f, 0.5f, 
									0, 0, PADDLE_TYPE_RECT),
	};
	
	PaddleDescriptor [][] paddleDescriptors =
	{
			twoPlayerPaddleDesc,
			twoPlayerPaddleDesc
	};
	
	boolean gameLogicStarted = false;

	private final float lowY = 0.8f; 
	private final float highY = 7.8f;
	
	Vector2 [] twoPlayerboundVectors = 
	{
			new Vector2(0.50f,0.80f),
			new Vector2(9.00f,0.80f),
			new Vector2(9.00f,7.80f),
			new Vector2(0.50f,7.80f)
	};
	
	Vector2 [][] boundVectors = 
	{
		twoPlayerboundVectors,
		twoPlayerboundVectors,
		null,
		twoPlayerboundVectors
	};

	float goalHeight = 3.5f;
	float goalWidth = 0.05f;
	

	float [] twoPlayerGoals = {

			0.5f, 0.8f,
			0.6f, 0.8f, 
			0.6f, 7.8f, 
			0.5f, 7.8f, 
			
			8.9f, 0.8f,
			9.0f, 0.8f, 
			9.0f, 7.8f, 
			8.9f, 7.8f 
			
	};
	
	float [][] goalVerteces = 
	{
		twoPlayerGoals,
		twoPlayerGoals,
		null,
		twoPlayerGoals
	};
	
	
	GameBounds bounds;
	
	Body ball = null;
	
	class BallDescriptor
	{
		Vector2 pos;
		Vector2 vel;
		public BallDescriptor(Vector2 pos, Vector2 vel) {
			super();
			this.pos = pos;
			this.vel = vel;
		}
	}
	private float ballRadius = 0.08f;

	BallDescriptor [] ballDesc =
	{
		new BallDescriptor(new Vector2(4.8f, 6), new Vector2(0, -3)),
		new BallDescriptor(new Vector2(4.8f, 6), new Vector2(0, -1.5f)),
		null,
		new BallDescriptor(new Vector2(4.8f, 6), new Vector2(0, -3)),
	};
	
	
	Body [] goals;
	Body [] paddles;
	Body boundsBody;
	
//	Player [] players;
	int nextPlayerIndex = 0;
	
	Random random = new Random();
	
	class BumperDescriptor
	{
		public BumperDescriptor(float x, float y, Vector2 dir, float amplitude, float radius) {
			super();
			this.x = x;
			this.y = y;
			this.dir = dir;
			this.amplitude = amplitude;
			this.radius = radius;
		}

		float x, y, amplitude, radius;
		Vector2 dir;

	}
	
	BumperDescriptor [] bumperDesc =
	{
			new BumperDescriptor(4.8f, 0.8f, new Vector2(1,0).nor(), ((1f - 2*0.25f) / 2), 0.25f),
			new BumperDescriptor(4.8f, 0.8f, new Vector2(1,0).nor(), ((1f - 2*0.25f) / 2), 0.25f),
			new BumperDescriptor(4.8f, 1.3f, new Vector2(1,0).nor(), ((1f - 2*0.25f) / 2), 0.25f),
			new BumperDescriptor(4.8f, 1.3f, new Vector2(1,0).nor(), ((1f - 2*0.25f) / 2), 0.25f)
	};

	Body bumper;
	private float bumperRadius = 0.25f;
	int  gameState = GAME_STATE_WAIT_FOR_PLAYERS;
	
	Hashtable<Integer, Integer> connectionsToPlayers = new Hashtable<Integer, Integer>();
	
	private PongGameServer(int nbPlayers)
	{
		destroyWorld();
		Gdx.app.log( AstericsPong.LOG, "New " + nbPlayers + " player server requested" );
		this.nbPlayers = nbPlayers;
		createGameLogic();
	}
	
	public static void reset(int nbPlayers)
	{
		if (instance != null)
		{
			instance.destroyWorld();
		}
		instance = new PongGameServer(nbPlayers);
	}
	
	void destroyWorld()
	{
		if (world != null)
		{
			Array<Body> bodies = new Array<Body>();
			while (world.isLocked()) ;
			world.setContactListener(null);		
			world.getBodies(bodies);
			for (Body b : bodies)
			{
				world.destroyBody(b);
			}
			world.dispose();
			world = null;
		}
//		world.destroyBody(ball);
//		world.destroyBody(bumper);
//		world.destroyBody(boundsBody);
//		for (Body b : paddles)
//		{
//			world.destroyBody(b);
//		}
//		for (Body b : goals)
//		{
//			world.destroyBody(b);
//		}
	}
	
	void createGameLogic()
	{
		World.setVelocityThreshold(0.01f);
		world = new World(new Vector2(0,0), true);
		world.setContactListener(this);		

		GameWorld.instance.setNbPlayers(nbPlayers);
		
		createBounds();
		createGoals();	

		createBall();
		createPaddles();
		createBumper();
		
		updateGameWorldOutputs();
		
		gameState = GAME_STATE_WAIT_FOR_PLAYERS;
		Gdx.app.log( AstericsPong.LOG, "World created, waiting for players" );
		
		start();
	}

	private void createGoals() 
	{
		goals = new Body[nbPlayers];
		for (int i = 0; i < nbPlayers; i++)
		{
			// Create our body definition
			BodyDef goalABodyDef =new BodyDef();  
			// Set its world position
//			goalABodyDef.position.set(new Vector2(0.55f, 4.3f));  
	
			// Create a body from the defintion and add it to the world
			goals[i] = world.createBody(goalABodyDef);  
	
			
			// Create a polygon shape
			PolygonShape goalAShape = new PolygonShape();
			float [] goalCoords = new float[8];
			for (int j = 0; j < 8; j++)
			{
				goalCoords[j] = goalVerteces[nbPlayers-1][j + i*8];
			}
			Vector2 [] goalVects = new Vector2[4]; 
			for (int j = 0; j < 4; j++)
			{
				goalVects[j] = new Vector2(goalCoords[j*2], goalCoords[j*2 + 1]);
			}
			goalAShape.set(goalVects);
			// Set the polygon shape as a box which is twice the size of our view port and 10 high
			// Create a fixture from our polygon shape and add it to our ground body  
	
			FixtureDef goalAfixtureDef = new FixtureDef();
			goalAfixtureDef.shape = goalAShape;
			goalAfixtureDef.density = 0f;
			goalAfixtureDef.friction = 0.0f;
			goalAfixtureDef.restitution = 0f; // Make it bounce a little bit
			goals[i].createFixture(goalAfixtureDef);	
		}
		GameWorldStatic.instance.goalVerteces = goalVerteces[nbPlayers-1];
	}

	void resetGame()
	{
		ball.setTransform(ballDesc[nbPlayers-1].pos.cpy(), 0);
		ball.setLinearVelocity(0,0);
		
		ballHadFirstContact = false;
		
		for (int i = 0; i < nbPlayers; i++)
		{
			PaddleDescriptor desc = paddleDescriptors[nbPlayers-1][i];
			// Then we set our bodies starting position in the world
			paddles[i].setTransform((desc.xMax + desc.xMin)/2, 
					(desc.yMax + desc.yMin)/2,
					0);
		}
	}

	private void createBall() 
	{
		// BALL ---------------------------------------------------------------
		// First we create a body definition
		
		if (ball != null)
		{
			world.destroyBody(ball);
		}
		BodyDef bodyDef = new BodyDef();
		// We set our body to dynamic, for something like ground which doesnt move we would set it to StaticBody
		bodyDef.type = BodyType.DynamicBody;
		// Then we set our bodies starting position in the world
		bodyDef.position.set(ballDesc[nbPlayers-1].pos.cpy());
		bodyDef.linearVelocity.set(ballDesc[nbPlayers-1].vel.cpy());
		bodyDef.linearDamping = 0;
		
		// Now we create our body in the world using our body definition
		ball = world.createBody(bodyDef);

		// Now we create a circle shape and set its radius to 6
		CircleShape circle = new CircleShape();
		circle.setRadius(ballRadius);

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.5f;
		 
		fixtureDef.friction = 0.0f;
		fixtureDef.restitution = 1.00f; // Make it bounce a little bit

		// Create our fixture and attach it to the body
		ball.createFixture(fixtureDef);	
		
		circle.dispose();
	}

	private void createBounds() 
	{
		GameWorldStatic.instance.boundVectors = boundVectors[nbPlayers-1];
		
		// Create our body definition
		BodyDef boundsBodyDef =new BodyDef();  
		// Set its world position
		boundsBodyDef.position.set(new Vector2(0, 0));  

		// Create a body from the defintion and add it to the world
		boundsBody = world.createBody(boundsBodyDef);  

		// Create a polygon shape
		ChainShape boundingBox = new ChainShape();
		boundingBox.createChain(boundVectors[nbPlayers-1]);
		// Set the polygon shape as a box which is twice the size of our view port and 10 high
		// Create a fixture from our polygon shape and add it to our ground body  

		FixtureDef fixtureDef2 = new FixtureDef();
		fixtureDef2.shape = boundingBox;
		fixtureDef2.density = 0f;
		fixtureDef2.friction = 0.0f;
		fixtureDef2.restitution = 0f; // Make it bounce a little bit
		boundsBody.createFixture(fixtureDef2);
		
		boundingBox.dispose();
	}


	private void createPaddles() {
		// PADDLES ------------------------------------------------------------
		
		paddles = new Body[nbPlayers];
		for (int i = 0; i < nbPlayers; i++)
		{
			PaddleDescriptor desc = paddleDescriptors[nbPlayers-1][i];
			// First we create a body definition
			BodyDef paddleABodyDef = new BodyDef();
			paddleABodyDef.type=BodyDef.BodyType.KinematicBody;
			// Then we set our bodies starting position in the world
			paddleABodyDef.position.set(
					(desc.xMax + desc.xMin)/2, (desc.yMax + desc.yMin)/2);
			paddleABodyDef.linearDamping = 0;
			
			// Now we create our body in the world using our body definition
			paddles[i] = world.createBody(paddleABodyDef);
	
			// Now we create a circle shape and set its radius to 6
			if (desc.type == PADDLE_TYPE_CIRCLE)
			{
				CircleShape shapePaddleA = new CircleShape();
				shapePaddleA.setRadius(desc.radius);

				// Create a fixture definition to apply our shape to
				FixtureDef paddleAFixtureDef = new FixtureDef();
				paddleAFixtureDef.shape = shapePaddleA;
				paddleAFixtureDef.density = 100.5f;

				paddleAFixtureDef.friction = 0.0f;
				paddleAFixtureDef.restitution = 1.50f; // Make it bounce a little bit

				// Create our fixture and attach it to the body
				paddles[i].createFixture(paddleAFixtureDef);
				shapePaddleA.dispose();
			}
			else if (desc.type == PADDLE_TYPE_RECT)
			{
				PolygonShape shapePaddleA = new PolygonShape();
				shapePaddleA.setAsBox(desc.width, desc.radius);

				// Create a fixture definition to apply our shape to
				FixtureDef paddleAFixtureDef = new FixtureDef();
				paddleAFixtureDef.shape = shapePaddleA;
				paddleAFixtureDef.density = 100.5f;

				paddleAFixtureDef.friction = 0.0f;
				paddleAFixtureDef.restitution = 1.2500f; // Make it bounce a little bit

				// Create our fixture and attach it to the body
				paddles[i].createFixture(paddleAFixtureDef);
				shapePaddleA.dispose();
			}
		}
	}
	
	private void createBumper() 
	{
		   BodyDef bDef = new BodyDef();
		   bDef.type=BodyDef.BodyType.KinematicBody;
		   float offset = (float) (random.nextDouble() * 2 - 1) * amplitude;
		   theta = (float) Math.asin(offset);
		   bDef.position.set(new Vector2(FIELD_CENTER,1.3f));
		   bumper=world.createBody(bDef);
		   CircleShape bodyShape = new CircleShape();
		   bodyShape.setRadius(bumperRadius); //Set As Box takes half width and half height as arguments
		   FixtureDef fDef=new FixtureDef();
		   fDef.density=1f;
		   fDef.restitution=0f;
		   fDef.shape=bodyShape;
		   bumper.createFixture(fDef);
		   bodyShape.dispose();
	}

	long resetRequestTime = -1;
	boolean resetBallRequest = false;

	private float theta = 0f;
	private float amplitude = (1f - 2*bumperRadius) / 2;
	
	boolean gameRunning;
	
	@Override
	public void run() 
	{
		boolean worldStep;
		BumperDescriptor bdesc = bumperDesc[nbPlayers-1];
		gameRunning = true;
		while (gameRunning)
		{
			worldStep = false;
			if (System.currentTimeMillis() - lastStepTime > stepTimeMs)
			{
				theta += stepTime; //period
				Vector2 targetPos = new Vector2( 
						bdesc.amplitude* (float) Math.sin(2*theta)*bdesc.dir.x, 
						bdesc.amplitude* (float) Math.sin(2*theta)*bdesc.dir.y); //where the body should be now
//				bumper.setLinearVelocity( targetPos.x, 0); //make the body move to the correct position in one time step
				bumper.setTransform(bdesc.x + targetPos.x, bdesc.y + targetPos.y, bumper.getAngle());
				
				for(int i = 0; i < nbPlayers; i++)
				{
					PaddleDescriptor desc = paddleDescriptors[nbPlayers-1][i];
					if (PongGameProperties.controlMode == PongGameProperties.CONTROLMODE_SPEED)
					{
						paddles[i].setAwake(true);
						if (GameWorld.instance.players[i].up)
						{
							
							if (paddles[i].getPosition().y > desc.yMax
									- desc.radius)
							{
								paddles[i].setTransform(paddles[i].getPosition().x, 
										desc.yMax - desc.radius,
										0);
								paddles[i].setLinearVelocity(0, 0);
							}
							else
							{
								paddles[i].setLinearVelocity(0,
										GameWorld.instance.players[i].speed * 
										(float) PongGameProperties.speedStep);
							}
						} 
						else
						{
							if (paddles[i].getPosition().y < desc.yMin 
									+ desc.radius)
							{
								paddles[i].setTransform(paddles[i].getPosition().x, 
										desc.yMin + desc.radius,
										0);
								paddles[i].setLinearVelocity(0, 0);
							}
							else
							{
								paddles[i].setLinearVelocity(0,
										GameWorld.instance.players[i].speed * 
										(float) -PongGameProperties.speedStep);
							}
						}
					}
					else if (PongGameProperties.controlMode == PongGameProperties.CONTROLMODE_EVENT)
					{
						// use event movement inputs
						if (GameWorld.instance.players[i].up)
						{
							paddles[i].setTransform(paddles[i].getPosition().x, 
									paddles[i].getPosition().y + 
									GameWorld.instance.players[i].movementInput *
									(float) PongGameProperties.speedStep,
									0);

							if (paddles[i].getPosition().y > desc.yMax
									- desc.radius)
							{
								paddles[i].setTransform(paddles[i].getPosition().x, 
										desc.yMax - desc.radius,
										0);
							} 
						} 
						else
						{
							paddles[i].setTransform(paddles[i].getPosition().x, 
									paddles[i].getPosition().y - 
									GameWorld.instance.players[i].movementInput *
									(float) PongGameProperties.speedStep,
									0);

							if (paddles[i].getPosition().y < desc.yMin 
									+ desc.radius)
							{
								paddles[i].setTransform(paddles[i].getPosition().x, 
										desc.yMin + desc.radius,
										0);
							} 
						}
						GameWorld.instance.players[i].movementInput = 0;
					}
					else if (PongGameProperties.controlMode == PongGameProperties.CONTROLMODE_POSITION)
					{
						// use position input ports
						
						//System.out.println("setting transform of paddle " + i + " to " + GameWorld.instance.players[i].position *
						//		(float) PongGameProperties.speedStep);
								
						paddles[i].setTransform(paddles[i].getPosition().x, 
								(lowY + desc.radius + (highY - lowY - 2 * desc.radius) 
										* GameWorld.instance.players[i].position / 300.0f),	0);

						if (paddles[i].getPosition().y > desc.yMax
								- desc.radius)
						{
							paddles[i].setTransform(paddles[i].getPosition().x, 
									desc.yMax - desc.radius,
									0);
						} 
						if (paddles[i].getPosition().y < desc.yMin 
								+ desc.radius)
						{
							paddles[i].setTransform(paddles[i].getPosition().x, 
									desc.yMin + desc.radius,
									0);
						} 
					}
				}
				
				worldStep = true;
				lastStepTime = System.currentTimeMillis();
			}
			
			switch (gameState)
			{
			case GAME_STATE_PLAY:
				if (worldStep)
				{
					world.step(stepTime, 9, 9);
					float vel = ball.getLinearVelocity().len2();

					if (vel > PongGameProperties.propMaxSpeed)
					{
						ball.setLinearDamping(0.2f);
					}
					else
					{
						ball.setLinearDamping(0);
					}
					
					
				}
				break;
			case GAME_STATE_RESET_FIELD:
				if (resetBallRequest)
				{
					resetGame();
					resetBallRequest = false;
				}
				if (resetRequestTime + RESET_WAIT_TIME < System.currentTimeMillis())
				{
					ball.setLinearVelocity(ballDesc[nbPlayers-1].vel.cpy());
					gameState = GAME_STATE_PLAY;
				}
				break;
			}
			
			if (worldStep)
			{
				updateGameWorldOutputs();
			}
			yield();
		}
		System.out.println("Server main loop ended, destroying world");		
		destroyWorld();		
	}

	private void updateGameWorldInputs(Player p, Integer index) 
	{
	}

	private void updateGameWorldOutputs() {
		
		// copies physics engine positions into game world representation for rendering
		GameWorld.instance.ball.x = ball.getPosition().x;
		GameWorld.instance.ball.y = ball.getPosition().y;
		GameWorld.instance.ball.radius = ballRadius;
		GameWorld.instance.ball.type = 0;
		
		for (int i = 0; i < nbPlayers; i++)
		{
			GameWorld.instance.paddles[i].x = paddles[i].getPosition().x;
			GameWorld.instance.paddles[i].y = paddles[i].getPosition().y;
			
			PaddleDescriptor desc = twoPlayerPaddleDesc[i];
			GameWorld.instance.paddles[i].radius = desc.radius;
			GameWorld.instance.paddles[i].width  = desc.width;
			GameWorld.instance.paddles[i].type 	= desc.type;
		}

		GameWorld.instance.bumper.x = bumper.getPosition().x;
		GameWorld.instance.bumper.y = bumper.getPosition().y;
		GameWorld.instance.bumper.radius = bumperRadius;
		GameWorld.instance.bumper.type = 0;
	}

	Standings standings = new Standings();

	@Override
	public void beginContact(Contact contact) 
	{
		boolean reset = false;
		Body bodyA = contact.getFixtureA().getBody();
		Body bodyB = contact.getFixtureB().getBody();
		if ( (bodyA == ball) || (bodyB == ball))
		{
			ballHadFirstContact = true;
			for (int i = 0; i < nbPlayers; i++)
			{
				if ( (bodyA == goals[i]) || 
					 (bodyB == goals[i]))
				{
					Player pfor = GameWorld.instance.players[(i + 1) % 2];
					Player pagainst = GameWorld.instance.players[i];
					pfor.goalsFor++;
					pagainst.lifes--;
//					System.out.println("Goal for " + pfor.name + "!");
					pfor.score += PongGameProperties.propGoalScoreBase * (pfor.goalsFor);  
					if (pagainst.lifes < 1)
					{
//						System.out.println(GameWorld.instance.players[i].name + " lost!");
						gameState = GAME_STATE_OVER;
						endSound.play();
						endGame();
					}
					else
					{
						goalSound.play();
						reset = true;
					}
				}
				else if ((bodyA == paddles[i]) || 
						 (bodyB == paddles[i]))
				{
					Vector2 vel = ball.getLinearVelocity();
					float factor = 1.0f;
					if (vel.x != 0)
					{
						factor = vel.y / (vel.x / 4f);
					}
					if (factor < 1)
						factor = 1;
					
					GameWorld.instance.players[i].score +=   (int) (factor * PongGameProperties.propGoalTouchBase);
					paddleSound.play();
				}
				else if ((bodyA == boundsBody) || 
						 (bodyB == boundsBody))
				{
					wallSound.play();
				}
				
			}
		}
		
		if (reset)
		{
			gameState  = GAME_STATE_RESET_FIELD;
			resetBallRequest = true;
			resetRequestTime = System.currentTimeMillis();
		}
 
	}

	@Override
	public void endContact(Contact contact) 
	{
		Body bodyA = contact.getFixtureA().getBody();
		Body bodyB = contact.getFixtureB().getBody();
		Body pdl = null;
		
		if ( (bodyA == ball) || (bodyB == ball)) 
		{
			if ( (bodyA == paddles[0]) || (bodyA == paddles[1]))
			{
				pdl = bodyA;
			}
			if ( (bodyB == paddles[0]) || (bodyB == paddles[1]))
			{
				pdl = bodyB;
			}
			
			if (gameState == GAME_STATE_PLAY)
			{
				if (pdl != null)
				{
					float dy = ball.getPosition().y - pdl.getPosition().y;
					dy = dy / twoPlayerPaddleDesc[0].radius;
					ball.applyForceToCenter(new Vector2(0, dy * PongGameProperties.propReflectionYImpulse), true);
//					System.out.println("Force applied: " + dy * PongGameProperties.propReflectionYImpulse);
				}
				else
				{
					float vel = ball.getLinearVelocity().x;
					
					if (ballHadFirstContact)
					{
						if (Math.abs(vel) < MIN_X_SPEED)
						{
							ball.applyForceToCenter(new Vector2(((vel > 0) ? 
//									PongGameProperties.propReflectionYImpulse : -PongGameProperties.propReflectionYImpulse), 0 ), true);
							0.5f : -0.5f), 0 ), true);
						}
					}
				}
			}
		}

	}


	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

	public void startGame() 
	{
		if (gameState == GAME_STATE_WAIT_FOR_PLAYERS)
		{
			gameState = GAME_STATE_RESET_FIELD;
		}
	}

	public void endGame() 
	{
		gameRunning = false;
	}
}
