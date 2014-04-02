package eu.asterics.component.actuator.ponggame;

import java.awt.Dimension;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.badlogic.gdx.graphics.Texture;


/**
 * The base class for all game screens.
 */
public abstract class AbstractScreen
    implements
        Screen
{
    // the fixed viewport dimensions (ratio: 1.6)
    public static final int GAME_VIEWPORT_WIDTH = 400, GAME_VIEWPORT_HEIGHT = 240;
    public static final int MENU_VIEWPORT_WIDTH = 800, MENU_VIEWPORT_HEIGHT = 480;

    protected final AstericsPong game;
    protected final Stage stage;

    private BitmapFont font;
    private SpriteBatch batch = null;
    private Skin skin;
    private TextureAtlas atlas;
    private Table table;
  
    private Texture imageTexture;

    protected BitmapFont gamefont =new BitmapFont(Gdx.files.internal("data/actuator.ponggame/gamefont.fnt"),false);
    protected BitmapFont darkfont =new BitmapFont(Gdx.files.internal("data/actuator.ponggame/darkfont.fnt"),false);
    protected BitmapFont highscorefont =new BitmapFont(Gdx.files.internal("data/actuator.ponggame/highscorefont.fnt"),false);
    
    Screen nextScreen = null;
    boolean screenSwitchActive = false;
    
    public void setNextScreen(Screen nextScreen) {
    	if (nextScreen == null)
    		this.nextScreen = nextScreen;
	}

	public AbstractScreen(AstericsPong game )
    {
        this.game = game;
        int width = ( isGameScreen() ? GAME_VIEWPORT_WIDTH : MENU_VIEWPORT_WIDTH );
        int height = ( isGameScreen() ? GAME_VIEWPORT_HEIGHT : MENU_VIEWPORT_HEIGHT );
        this.stage = new Stage( width, height, true );
        
        batch = new SpriteBatch();     
        Texture.setEnforcePotImages(false);
    }

    public Texture loadImage(String fileName)
    {
	    FileHandle imageFileHandle = Gdx.files.internal(fileName);
	    if (imageFileHandle != null) 
	       	System.out.println("load image "+fileName+" ok!");
		     
	    return(new Texture(imageFileHandle));
    }
    
    public void drawImage(Texture imageTexture, int x, int y)
    {
         batch.draw(imageTexture, x, y);
    }
    
    protected String getName()
    {
        return getClass().getSimpleName();
    }

    protected boolean isGameScreen()
    {
        return false;
    }

    // Lazily loaded collaborators

    public BitmapFont getFont()
    {
    	if( font == null ) {
           font = new BitmapFont(Gdx.files.internal("data/actuator.ponggame/gamefont.fnt"),false);
           font.scale(getScaleFactor()-1);
        }
        return font;
    }
    
    public SpriteBatch getBatch()
    {
        if( batch == null ) {
            batch = new SpriteBatch();
        }
        return batch;
    }

    public TextureAtlas getAtlas()
    {
        if( atlas == null ) {
            atlas = new TextureAtlas( Gdx.files.internal( "image-atlases/pages.atlas" ) );
        }
        return atlas;
    }

    protected Skin getSkin()
    {
        if( skin == null ) {
            FileHandle skinFile = Gdx.files.internal( "data/actuator.ponggame/uiskin.json" );
            skin = new Skin( skinFile );
        }
        return skin;
    }

    protected Table getTable()
    {
        if( table == null ) {
            table = new Table( getSkin() );
            table.setFillParent( true );
            stage.addActor( table );
        }
        return table;
    }

    public void setScaleFactor(Dimension dim)
    {
    	AstericsPong.instance.scaleFactor=(float)dim.width / 1920.0f;    	
    }

    public float getScaleFactor()
    {
    	return(AstericsPong.instance.scaleFactor);
    }
    
    // Screen implementation

    @Override
    public void show()
    {
        Gdx.app.log( AstericsPong.LOG, "Showing screen: " + getName() );

        // set the stage as the input processor
        Gdx.input.setInputProcessor( stage );
    }

    @Override
    public void resize(
        int width,
        int height )
    {
        Gdx.app.log( AstericsPong.LOG, "Resizing screen: " + getName() + " to: " + width + " x " + height );
    }

    @Override
    public void render(float delta )
    {
        // (1) process the game logic

        // update the actors
        stage.act( delta );

        // (2) draw the result

        // clear the screen with the given RGB color (black)
        Gdx.gl.glClearColor( 0f, 0f, 0f, 1f );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        // draw the actors
        stage.draw();

        // draw the table debug lines
        Table.drawDebug( stage );
    }

    @Override
    public void hide()
    {
        Gdx.app.log( AstericsPong.LOG, "Hiding screen: " + getName() );

        // dispose the screen when leaving the screen;
        // note that the dipose() method is not called automatically by the
        // framework, so we must figure out when it's appropriate to call it
        dispose();
    }

    @Override
    public void pause()
    {
        Gdx.app.log( AstericsPong.LOG, "Pausing screen: " + getName() );
    }

    @Override
    public void resume()
    {
        Gdx.app.log( AstericsPong.LOG, "Resuming screen: " + getName() );
    }

    @Override
    public void dispose()
    {
        Gdx.app.log( AstericsPong.LOG, "Disposing screen: " + getName() );

        // the following call disposes the screen's stage, but on my computer it
        // crashes the game so I commented it out; more info can be found at:
        // http://www.badlogicgames.com/forum/viewtopic.php?f=11&t=3624
        // stage.dispose();

        // as the collaborators are lazily loaded, they may be null
        if( font != null ) font.dispose();
        if( batch != null ) batch.dispose();
//        if( skin != null ) skin.dispose();
        if( atlas != null ) atlas.dispose();
    }
    
	public abstract void auxiliaryButtonInput();
	public abstract void playerMovementInput(int index);
	public abstract void playerPosInput(int index, int position);
	public abstract void playerSpeedInput(int index, int speed);
	public abstract void playerDirectionToggle(int index);    
}
