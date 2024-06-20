package com.themarbles.game.screens;

import static com.badlogic.gdx.Gdx.audio;
import static com.badlogic.gdx.Gdx.files;
import static com.badlogic.gdx.Gdx.input;
import static com.themarbles.game.constants.Constants.HEIGHT;
import static com.themarbles.game.constants.Constants.WIDTH;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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

/** Activates if you lost all your marbles (defeat).
 * @see Screen
 * **/

public class DefeatScreen implements Screen {

    private final EntryPoint entryPoint;
    private final Stage stage;
    private final Image background;
    private final TextButton restart;
    private final BitmapFont defeatFont;
    private final Sound defeatSound;

    public DefeatScreen(EntryPoint entryPoint) {
        this.entryPoint = entryPoint;

        stage = new Stage();

        background = new Image(new Texture(files.internal("textures/defeat.jpg")));
        restart = new TextButton("RESTART", new Skin(files.internal("buttons/restartbuttonassets/restartbuttonskin.json")));
        defeatFont = FontGenerator.generateFont(files.internal("fonts/defeatFont.otf"), 160, Color.FIREBRICK);

        defeatSound = audio.newSound(files.internal("sounds/defeat_sound.wav"));

        initRestartButton();
        initBackground();

    }

    @Override
    public void show() {

        stage.addActor(background);
        stage.addActor(restart);

        input.setInputProcessor(stage);

        defeatSound.play(0.15f);
    }

    @Override
    public void render(float delta) {

        stage.act(delta);
        stage.draw();

        entryPoint.batch.begin();

        defeatFont.draw(entryPoint.batch, "YOU LOSE!", (float) WIDTH/4, HEIGHT - 70);

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
        defeatFont.dispose();
        defeatSound.dispose();
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
        background.setPosition(0, 0);
        background.setSize(WIDTH, HEIGHT);
    }
}
