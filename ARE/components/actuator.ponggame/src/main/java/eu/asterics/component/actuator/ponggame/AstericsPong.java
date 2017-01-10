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

import eu.asterics.mw.model.runtime.IRuntimeOutputPort;

public class AstericsPong extends Game {
    public static final String LOG = AstericsPong.class.getSimpleName();

    public static AstericsPong instance = null;

    Standings standings = null;
    boolean gameLogicRunning = false;
    public float scaleFactor = 1;

    public IRuntimeOutputPort opBallX;
    public IRuntimeOutputPort opBallY;

    Player player;

    GameScreen gameScreen;

    List<HighScore> scores = null;
    int lowestHighScore = 0;
    FileHandle highscores;
    private Dimension availableScreenSize = null;

    void initHighScores() {
        String line;

        scores = new ArrayList<HighScore>();
        highscores = Gdx.files.local("data/actuator.ponggame/highscores.txt");
        if (highscores.exists()) {
            LineNumberReader reader = new LineNumberReader(highscores.reader());
            try {
                while (scores.size() <= 10) {
                    line = reader.readLine();
                    if (line == null) {
                        break;
                    }
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
        for (HighScore s : scores) {
            System.out.println("..." + s.name + " " + s.score);
        }

    }

    private AstericsPong(Dimension availableScreenSize, IRuntimeOutputPort opBallX, IRuntimeOutputPort opBallY) {
        this.availableScreenSize = availableScreenSize;
        this.opBallX = opBallX;
        this.opBallY = opBallY;
    }

    public static void reset(Dimension availableScreenSize, IRuntimeOutputPort opBallX, IRuntimeOutputPort opBallY) {
        instance = new AstericsPong(availableScreenSize, opBallX, opBallY);
        // System.out.println("reset to screen size
        // "+availableScreenSize.height+"/"+availableScreenSize.width);
    }

    @Override
    public void create() {
        initHighScores();
        PongGameServer.reset(2);
        createGameScreen();
        gameLogicRunning = true;
        setScreen(gameScreen);
    }

    public void auxiliaryButtonInput() {
        AbstractScreen screen = (AbstractScreen) getScreen();
        if (screen != null) {
            screen.auxiliaryButtonInput();
        }
    }

    public void playerMovementInput(int index) {
        AbstractScreen screen = (AbstractScreen) getScreen();
        if (screen != null) {
            screen.playerMovementInput(index);
        }
    }

    public void playerPosInput(int index, int position) {
        AbstractScreen screen = (AbstractScreen) getScreen();
        if (screen != null) {
            screen.playerPosInput(index, position);
        }
    }

    public void playerSpeedInput(int index, int speed) {
        AbstractScreen screen = (AbstractScreen) getScreen();
        if (screen != null) {
            screen.playerSpeedInput(index, speed);
        }
    }

    public void playerDirectionToggle(int index) {
        AbstractScreen screen = (AbstractScreen) getScreen();
        if (screen != null) {
            screen.playerDirectionToggle(index);
        }
    }

    protected void createGameScreen() {
        gameScreen = new GameScreen(this);
        gameScreen.setScaleFactor(availableScreenSize);
    }

    @Override
    public void dispose() {
    }

    boolean[] keysPressed = new boolean[10];

    private PongGameInstance pongGameInstance;

    @Override
    public void render() {
        // gameScreen.updateGameWorldOutputs(GameWorld.instance);
        super.render();
        // Gdx.gl.glClearColor(0, 0, 0, 1);
        // Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        // renderer.render(gameLogic.world, camera.combined);

        if (Gdx.input.isKeyPressed(Keys.A)) {
            if (!keysPressed[0]) {
                this.playerMovementInput(0);
                keysPressed[0] = true;
            }
        } else {
            keysPressed[0] = false;
        }

        if (Gdx.input.isKeyPressed(Keys.S)) {
            if (!keysPressed[1]) {
                this.playerDirectionToggle(0);
                keysPressed[1] = true;
            }
        } else {
            keysPressed[1] = false;
        }

        if (Gdx.input.isKeyPressed(Keys.K)) {
            if (!keysPressed[2]) {
                this.playerMovementInput(1);
                keysPressed[2] = true;
            }
        } else {
            keysPressed[2] = false;
        }

        if (Gdx.input.isKeyPressed(Keys.L)) {
            if (!keysPressed[3]) {
                this.playerDirectionToggle(1);
                keysPressed[3] = true;
            }
        } else {
            keysPressed[3] = false;
        }

        if (Gdx.input.isKeyPressed(Keys.T)) {
            if (!keysPressed[4]) {
                this.auxiliaryButtonInput();
                keysPressed[4] = true;
            }
        } else {
            keysPressed[4] = false;
        }

        // world.step(1 / 60f, 6, 2);

        // System.out.println("Client: GameWorld message sent");
    }

    public void startGame() {
        // gameLogic.startGame();
    }

    public void stopGame() {
        PongGameServer.instance.endGame();
        // gameLogic.endGame();
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
        while (scores.size() > 10) {
            scores.remove(10);
        }
        this.lowestHighScore = scores.get(scores.size() - 1).score;

        boolean append = false;
        for (HighScore s : scores) {
            highscores.writeString(s.name + " " + s.score, append);
            append = true;
            highscores.writeString("\n", append);
        }
    }

    public void sendGameOver() {
        // TODO Auto-generated method stub
        pongGameInstance.etpGameOver.raiseEvent();
    }

    public void setPongGameInstane(PongGameInstance pongGameInstance) {
        // TODO Auto-generated method stub
        this.pongGameInstance = pongGameInstance;
    }

}
