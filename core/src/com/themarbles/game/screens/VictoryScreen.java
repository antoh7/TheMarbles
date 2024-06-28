package com.themarbles.game.screens;

import static com.badlogic.gdx.Gdx.app;
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
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
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
    private final TextButton exit;
    private final BitmapFont victoryFont;
    private final Sound victorySound;
    private final GlyphLayout victoryLayout;

    public VictoryScreen(EntryPoint entryPoint) {
        this.entryPoint = entryPoint;

        stage = new Stage(new ScalingViewport(Scaling.fill, WIDTH, HEIGHT));

        background = new Image(new Texture(files.internal("textures/victory.jpg")));
        exit = new TextButton("EXIT", new Skin(files.internal("buttons/restartbuttonassets/restartbuttonskin.json")));
        victoryFont = FontGenerator.generateFont(files.internal("fonts/victoryFont.ttf"), 80, Color.CYAN);

        victoryLayout = new GlyphLayout(victoryFont, """
                                           CONGRATULATIONS!
                                               YOU WON!
                                           """);

        victorySound = audio.newSound(files.internal("sounds/victory_sound.wav"));

        initExitButton();
        initBackground();

    }

    @Override
    public void show() {

        stage.addActor(background);
        stage.addActor(exit);

        input.setInputProcessor(stage);

        victorySound.play(1);
    }

    @Override
    public void render(float delta) {

        stage.act(delta);
        stage.draw();

        entryPoint.batch.begin();

        victoryFont.draw(entryPoint.batch, victoryLayout, (float) WIDTH/2 - victoryLayout.width/2,
                (float) HEIGHT/2 + victoryLayout.height);

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

    private void initExitButton(){
        exit.setSize(Constants.WIDGET_PREFERRED_WIDTH + 20, Constants.WIDGET_PREFERRED_HEIGHT + 10);
        exit.setPosition((float) WIDTH/2 - exit.getWidth() / 2,
                (float) HEIGHT/2 - exit.getHeight() / 2);
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.exit(0);
            }
        });
    }

    private void initBackground(){
        background.setSize(WIDTH, HEIGHT);
        background.setPosition(0, 0);
    }
}
