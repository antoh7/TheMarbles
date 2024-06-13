package com.themarbles.game.screens;

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

import static com.themarbles.game.utils.PreGameStartedUtils.decodeToken;
import static com.themarbles.game.utils.PreGameStartedUtils.getHost;
import static com.themarbles.game.utils.PreGameStartedUtils.getPort;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.themarbles.game.EntryPoint;

import java.io.IOException;
import java.net.Socket;

public class JoinRoom implements Screen {

    private final EntryPoint entryPoint;

    private final TextField textFieldEnterToken;
    private final TextButton join, cancel;
    private final Image background;
    private final Stage stage;

    private final Sound buttonPressedSound;

    public JoinRoom(EntryPoint entryPoint) {
        this.entryPoint = entryPoint;

        stage = new Stage();

        background = new Image(new Texture(files.internal("textures/joinroom_menu_background.jpg")));

        buttonPressedSound = audio.newSound(files.internal("sounds/button_pressed.mp3"));

        join = new TextButton("JOIN", new Skin(files.internal("buttons/connectbuttonassets/connectbuttonskin.json")));
        cancel = new TextButton("CANCEL",new Skin(files.internal("buttons/cancelbuttonassets/cancelbuttonskin.json")));
        textFieldEnterToken = new TextField("ENTER TOKEN:",new Skin(files.internal("labels/enterlabel/enterlabelskin.json")));

        initBackground();
        initCancelButton();
        initJoinButton();
        initTokenInputLabel();

    }

    @Override
    public void show() {

        stage.addActor(background);
        stage.addActor(join);
        stage.addActor(cancel);
        stage.addActor(textFieldEnterToken);

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

    //########################### init methods #####################

    private void initCancelButton(){
        cancel.setPosition((float) WIDTH/2 - WIDGET_PREFERRED_WIDTH - 20, (float) HEIGHT/2 - 60);
        cancel.setSize(WIDGET_PREFERRED_WIDTH, WIDGET_PREFERRED_HEIGHT);
        cancel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor){
                buttonPressedSound.play();
                entryPoint.setScreen(entryPoint.mainMenu);
            }
        });
    }

    private void initJoinButton(){
        join.setPosition((float) WIDTH/2 + WIDGET_PREFERRED_HEIGHT, (float) HEIGHT/2 - 60);
        join.setSize(WIDGET_PREFERRED_WIDTH, WIDGET_PREFERRED_HEIGHT);
        join.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                //trying to create new client
                try {
                    String decodedToken = decodeToken(textFieldEnterToken.getText());

                    String host = getHost(decodedToken);
                    int port = getPort(decodedToken);
                    
                    entryPoint.client = new Socket(host, port);
                } catch (IOException | StringIndexOutOfBoundsException | IllegalArgumentException e) {
                    return;
                }
                entryPoint.deviceState = CLIENT;
                buttonPressedSound.play();
                entryPoint.setScreen(entryPoint.room);
            }
        });
    }

    private void initTokenInputLabel(){
        textFieldEnterToken.setPosition((float) WIDTH/2 - 130, (float) HEIGHT/2 + 100);
        textFieldEnterToken.setSize(WIDGET_PREFERRED_WIDTH + 100, WIDGET_PREFERRED_HEIGHT - 20);
        textFieldEnterToken.setAlignment(center);
        if(input.isPeripheralAvailable(OnscreenKeyboard)) {
            //TODO decide
            textFieldEnterToken.setOnscreenKeyboard(visible ->
                    input.setOnscreenKeyboardVisible(true, Password));
        }

    }

    private void initBackground(){
        background.setPosition(0, 0);
        background.setSize(WIDTH, HEIGHT);
    }

}
