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

import java.text.DecimalFormat;

import com.badlogic.gdx.graphics.Texture;

public class Energy2Screen extends AbstractScreen {
    private boolean goToNextScreen = false;
    private Texture backgroundImage;
    private int p1Calories = 0, p2Calories = 0;

    public Energy2Screen(AstericsPong game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        backgroundImage = loadImage("data/actuator.ponggame/energy2background.jpg");

        p1Calories = (int) (PongGameProperties.eventsToCaloryMultiplier * GameWorld.instance.players[0].inputs);
        p2Calories = (int) (PongGameProperties.eventsToCaloryMultiplier * GameWorld.instance.players[1].inputs);

    }

    @Override
    public void auxiliaryButtonInput() {
        goToNextScreen = true;
    }

    @Override
    public void playerMovementInput(int index) {
    }

    @Override
    public void playerPosInput(int index, int position) {
    }

    @Override
    public void playerSpeedInput(int index, int speed) {
    }

    @Override
    public void playerDirectionToggle(int index) {
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        getBatch().begin();

        drawImage(backgroundImage, 0, 0);

        String numOutput;
        DecimalFormat formatter;

        formatter = new DecimalFormat("###.#");

        numOutput = formatter.format((double) (p1Calories + p2Calories) * 0.06978); // 60W
        darkfont.draw(getBatch(), " " + numOutput + " Sekunden lang", 500, 780);
        darkfont.draw(getBatch(), "       eine Gluehlampe verwenden", 500, 740);

        numOutput = formatter.format((double) (p1Calories + p2Calories) * 0.06978 / 3); // 180
                                                                                        // W
        darkfont.draw(getBatch(), " " + numOutput + " Sekunden fernsehen", 500, 580);

        numOutput = formatter.format((double) (p1Calories + p2Calories) * 0.06978 / 30); // 1800
                                                                                         // W
        darkfont.draw(getBatch(), " " + numOutput + " Sekunden Haare foehnen", 500, 360);

        numOutput = formatter.format((double) (p1Calories + p2Calories) * 0.001163 / 11.1 / 0.75 * 10);
        darkfont.draw(getBatch(), " " + numOutput + " Meter mit einem Sportwagen fahren", 40, 150);

        getBatch().end();

        if (goToNextScreen && !screenSwitchActive) {
            for (int i = 0; i < 2; i++) {
                Player p = GameWorld.instance.players[i];
                if (p.score > AstericsPong.instance.lowestHighScore) {
                    game.setScreen(new HighScoreEnterScreen(game, i));
                    screenSwitchActive = true;
                    return;
                }
            }
            screenSwitchActive = true;
            game.setScreen(new HighScoreScreen(game));
        }
    }
}
