package com.themarbles.game.screens;

import static com.badlogic.gdx.Gdx.app;
import static com.badlogic.gdx.Gdx.audio;
import static com.badlogic.gdx.Gdx.files;
import static com.badlogic.gdx.Gdx.input;
import static com.badlogic.gdx.Input.OnscreenKeyboardType.Password;
import static com.badlogic.gdx.Input.Peripheral.OnscreenKeyboard;
import static com.badlogic.gdx.utils.Align.center;
import static com.themarbles.game.constants.Constants.CLIENT;
import static com.themarbles.game.constants.Constants.HEIGHT;
import static com.themarbles.game.constants.Constants.WIDGET_PREFERRED_HEIGHT;
import static com.themarbles.game.constants.Constants.WIDGET_PREFERRED_WIDTH;
import static com.themarbles.game.constants.Constants.WIDTH;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.themarbles.game.EntryPoint;
import com.themarbles.game.utils.PreGameStartedUtils;

import java.io.IOException;
import java.net.Socket;

/** Provides you a simple joining room menu.
 * @see Screen
 * @see Room
 * **/

public class JoinRoom implements Screen {

    private final EntryPoint entryPoint;

    private final TextField textFieldEnterToken;
    private final TextButton join, cancel;
    private final Image background;
    private final Stage stage;
    private final Label textFieldTokenLabel;

    private final Sound buttonPressedSound;

    public JoinRoom(EntryPoint entryPoint) {
        this.entryPoint = entryPoint;

        stage = new Stage(new ScalingViewport(Scaling.fill, WIDTH, HEIGHT));

        background = new Image(new Texture(files.internal("textures/joinroom_menu_background.jpg")));

        buttonPressedSound = audio.newSound(files.internal("sounds/button_pressed.mp3"));

        join = new TextButton("JOIN", new Skin(files.internal("buttons/connectbuttonassets/connectbuttonskin.json")));
        cancel = new TextButton("CANCEL",new Skin(files.internal("buttons/cancelbuttonassets/cancelbuttonskin.json")));
        textFieldEnterToken = new TextField("TOKEN:",new Skin(files.internal("labels/enterlabel/enterlabelskin.json")));
        textFieldTokenLabel = new Label("(TRIPLE CLICK TO PASTE)", new Skin(files.internal("labels/entertokenlabel/entertokenlabelskin.json")));

        initBackground();
        initCancelButton();
        initJoinButton();
        initTokenInputField();
        initTokenInputLabel();

    }

    @Override
    public void show() {

        stage.addActor(background);
        stage.addActor(join);
        stage.addActor(cancel);
        stage.addActor(textFieldEnterToken);
        stage.addActor(textFieldTokenLabel);

        input.setInputProcessor(stage);

    }

    @Override
    public void render(float delta) {

        stage.act(delta);
        stage.draw();

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
        input.setOnscreenKeyboardVisible(false);
    }

    @Override
    public void dispose() {
        stage.dispose();
        buttonPressedSound.dispose();
    }

    //############################# init methods ###########################

    private void initCancelButton(){
        cancel.setSize(WIDGET_PREFERRED_WIDTH, WIDGET_PREFERRED_HEIGHT);
        cancel.setPosition((float) WIDTH/2 - cancel.getWidth() - cancel.getWidth()/2,
                (float) HEIGHT/2 - 60);

        cancel.getLabel().setFontScale(MathUtils.floor(cancel.getWidth()/cancel.getMinWidth()),
                MathUtils.floor(cancel.getHeight()/cancel.getMinHeight()));

        cancel.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                buttonPressedSound.play();
                entryPoint.setScreen(entryPoint.mainMenu);
            }

        });
    }

    private void initJoinButton(){
        join.setSize(WIDGET_PREFERRED_WIDTH, WIDGET_PREFERRED_HEIGHT);
        join.setPosition((float) WIDTH/2 + join.getWidth()/2, (float) HEIGHT/2 - 60);

        join.getLabel().setFontScale(MathUtils.floor(join.getWidth()/join.getMinWidth()),
                MathUtils.floor(join.getHeight()/join.getMinHeight()));

        join.addListener(new ClickListener(){
            @Override
            public void clicked (InputEvent event, float x, float y) {
                //trying to create new client
                try {
                    String decodedToken = PreGameStartedUtils.decodeToken(textFieldEnterToken.getText());

                    String host = PreGameStartedUtils.getHost(decodedToken);
                    int port = PreGameStartedUtils.getPort(decodedToken);

                    entryPoint.client = new Socket(host, port);
                } catch (IOException | StringIndexOutOfBoundsException | IllegalArgumentException e) {
                    e.printStackTrace();
                    return;
                }
                entryPoint.deviceState = CLIENT;

                buttonPressedSound.play();
                entryPoint.menuMusic.stop();

                entryPoint.setScreen(entryPoint.room);
            }
        });
    }

    private void initTokenInputField(){
        textFieldEnterToken.setSize(WIDGET_PREFERRED_WIDTH + 100, WIDGET_PREFERRED_HEIGHT - 20);
        textFieldEnterToken.setPosition((float) WIDTH/2 - textFieldEnterToken.getWidth()/2, (float) HEIGHT/2 + 100);
        textFieldEnterToken.setAlignment(center);

        textFieldEnterToken.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                if (getTapCount() == 3) {
                    if (app.getClipboard().hasContents()) {
                        textFieldEnterToken.setText(app.getClipboard().getContents());
                    }
                }
            }
        });

        if(input.isPeripheralAvailable(OnscreenKeyboard)) {
            textFieldEnterToken.setOnscreenKeyboard(visible ->
                    input.setOnscreenKeyboardVisible(true, Password));
        }

    }

    private void initBackground(){
        background.setPosition(0, 0);
        background.setSize(WIDTH, HEIGHT);
    }

    private void initTokenInputLabel(){
        textFieldTokenLabel.setSize(WIDGET_PREFERRED_WIDTH + 100, WIDGET_PREFERRED_HEIGHT);
        textFieldTokenLabel.setPosition((float) WIDTH/2 - textFieldTokenLabel.getWidth()/2,
                textFieldEnterToken.getY() + textFieldEnterToken.getHeight());
        textFieldTokenLabel.setAlignment(center);

        textFieldTokenLabel.setFontScale(MathUtils.ceil(textFieldTokenLabel.getWidth()/ textFieldTokenLabel.getMinWidth()),
                MathUtils.ceil(textFieldTokenLabel.getHeight()/ textFieldTokenLabel.getMinHeight()));

    }

}
