package ru.kbuearpov.themarbles.screens;

import static com.badlogic.gdx.Gdx.audio;
import static com.badlogic.gdx.Gdx.files;
import static com.badlogic.gdx.Gdx.input;
import static ru.kbuearpov.themarbles.constants.Constants.CHARACTERS;
import static ru.kbuearpov.themarbles.constants.Constants.HEIGHT;
import static ru.kbuearpov.themarbles.constants.Constants.WIDTH;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import ru.kbuearpov.themarbles.EntryPoint;
import ru.kbuearpov.themarbles.constants.Constants;
import ru.kbuearpov.themarbles.utils.FontGenerator;

/** Activates if you lost all your marbles (defeat).
 * @see Screen
 * **/

public class DefeatScreen implements Screen {

    private final EntryPoint entryPoint;
    private final Stage stage;
    private final Image background;
    private final TextButton exit;
    private final BitmapFont defeatFont;
    private final Sound defeatSound;
    private final GlyphLayout defeatLayout;

    public DefeatScreen(EntryPoint entryPoint) {
        this.entryPoint = entryPoint;

        stage = new Stage();

        background = new Image(new Texture(files.internal("textures/defeat.jpg")));
        exit = new TextButton("ВЫЙТИ", new Skin(files.internal("buttons/exitbuttonassets/exitbuttonskin.json")));
        defeatFont = FontGenerator.generateFont(files.internal("fonts/defeatFont.otf"), 160, Color.FIREBRICK, CHARACTERS);

        defeatLayout = new GlyphLayout(defeatFont, "ТЫ ПРОИГРАЛ!");

        defeatSound = audio.newSound(files.internal("sounds/defeat_sound.wav"));

        initExitButton();
        initBackground();

    }

    @Override
    public void show() {

        stage.addActor(background);
        stage.addActor(exit);

        input.setInputProcessor(stage);

        defeatSound.play(0.15f);
    }

    @Override
    public void render(float delta) {

        stage.act(delta);
        stage.draw();

        entryPoint.batch.begin();

        defeatFont.draw(entryPoint.batch, defeatLayout, (float) WIDTH/2 - defeatLayout.width/2, (float) HEIGHT/2 + defeatLayout.height*2);

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

    private void initExitButton(){
        exit.setSize(Constants.WIDGET_PREFERRED_WIDTH + 20, Constants.WIDGET_PREFERRED_HEIGHT + 10);
        exit.setPosition((float) WIDTH/2 - exit.getWidth() / 2,
                (float) HEIGHT/2 - exit.getHeight() / 2);

        exit.getLabel().setFontScale(MathUtils.floor(exit.getWidth()/exit.getMinWidth()),
                MathUtils.floor(exit.getHeight()/exit.getMinHeight()));

        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.exit(0);
            }
        });
    }

    private void initBackground(){
        background.setPosition(0, 0);
        background.setSize(WIDTH, HEIGHT);
    }
}
