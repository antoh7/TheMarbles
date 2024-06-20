package com.themarbles.game.screens;

import static com.badlogic.gdx.Gdx.audio;
import static com.badlogic.gdx.Gdx.files;
import static com.badlogic.gdx.Gdx.input;
import static com.themarbles.game.constants.Constants.HEIGHT;
import static com.themarbles.game.constants.Constants.WIDTH;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.themarbles.game.EntryPoint;
import com.themarbles.game.constants.Constants;
import com.themarbles.game.utils.FontGenerator;

/** Activates if opponent lost all its marbles (you win)
 * @see Screen
 * **/

public class VictoryScreen implements Screen {

    private final EntryPoint entryPoint;
    private final Stage stage;
    private final Image background;
    private final TextButton restart;
    private final BitmapFont victoryFont;
    private final Sound victorySound;

    public VictoryScreen(EntryPoint entryPoint) {
        this.entryPoint = entryPoint;

        stage = new Stage();

        background = new Image(new Texture(files.internal("textures/victory.jpg")));
        restart = new TextButton("RESTART", new Skin(files.internal("buttons/restartbuttonassets/restartbuttonskin.json")));
        victoryFont = FontGenerator.generateFont(files.internal("fonts/victoryFont.ttf"), 80, Color.CYAN);

        victorySound = audio.newSound(files.internal("sounds/victory_sound.wav"));

        initRestartButton();
        initBackground();

    }

    @Override
    public void show() {

        stage.addActor(background);
        stage.addActor(restart);

        input.setInputProcessor(stage);

        victorySound.play(1);
    }

    @Override
    public void render(float delta) {

        stage.act(delta);
        stage.draw();

        entryPoint.batch.begin();

        victoryFont.draw(entryPoint.batch, """
                                           CONGRATULATIONS!
                                               YOU WON!
                                           """, (float) WIDTH/4 - 25, HEIGHT - 70);

        entryPoint.batch.end();
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

    @Override
    public void hide() {
        stage.clear();
    }

    @Override
    public void dispose() {
        stage.dispose();
        victoryFont.dispose();
        victorySound.dispose();
    }


    //########################### init methods ############################

    private void initRestartButton(){
        restart.setSize(Constants.WIDGET_PREFERRED_WIDTH + 20, Constants.WIDGET_PREFERRED_HEIGHT + 10);
        restart.setPosition((float) WIDTH/2 - restart.getWidth() / 2,
                (float) HEIGHT/2 - restart.getHeight() / 2);
        restart.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                entryPoint.setScreen(entryPoint.room);
            }
        });
    }

    private void initBackground(){
        background.setSize(WIDTH, HEIGHT);
        background.setPosition(0, 0);
    }
}
