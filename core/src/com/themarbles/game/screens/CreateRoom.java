package com.themarbles.game.screens;

import static com.badlogic.gdx.Gdx.files;
import static com.badlogic.gdx.Gdx.graphics;
import static com.badlogic.gdx.Gdx.input;
import static com.badlogic.gdx.Input.OnscreenKeyboardType.NumberPad;
import static com.badlogic.gdx.Input.Peripheral.OnscreenKeyboard;
import static com.badlogic.gdx.utils.ScreenUtils.clear;
import static com.themarbles.game.constants.Constants.HEIGHT;
import static com.themarbles.game.constants.Constants.SERVER;
import static com.themarbles.game.constants.Constants.WIDGET_PREFERRED_HEIGHT;
import static com.themarbles.game.constants.Constants.WIDGET_PREFERRED_WIDTH;
import static com.themarbles.game.constants.Constants.WIDTH;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static java.net.InetAddress.getLocalHost;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.themarbles.game.EntryPoint;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Base64;

public class CreateRoom implements Screen {
    private final EntryPoint entryPoint;
    private final TextField textFieldEnterPort;

    private final Image background;
    private final TextButton create, cancel;
    private final Stage stage;

    public CreateRoom(EntryPoint entryPoint) {
        this.entryPoint = entryPoint;
        stage = new Stage();

        background = new Image(new Texture(files.internal("textures/createroom_menu_background.jpg")));

        create = new TextButton("CREATE", new Skin(files.internal("buttons/createbuttonassets/createbuttonskin.json")));
        cancel = new TextButton("CANCEL",new Skin(files.internal("buttons/cancelbuttonassets/cancelbuttonskin.json")));
        textFieldEnterPort = new TextField("ENTER PORT:",new Skin(files.internal("labels/enterlabel/enterlabelskin.json")));


    }

    @Override
    public void show() {

        initBackground();
        initCancelButton();
        initCreateButton();
        initInputLabel();

        stage.addActor(background);
        stage.addActor(textFieldEnterPort);
        stage.addActor(create);
        stage.addActor(cancel);

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
        input.setOnscreenKeyboardVisible(false);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    //############### private methods #####################
    private void initCancelButton(){
        cancel.setSize(WIDGET_PREFERRED_WIDTH, WIDGET_PREFERRED_HEIGHT);
        cancel.setPosition((float) WIDTH/2 - WIDGET_PREFERRED_WIDTH - 20, (float) HEIGHT/2 - 20);
        cancel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                entryPoint.setScreen(entryPoint.mainMenu);
            }
        });
    }

    private void initCreateButton(){
        create.setSize(WIDGET_PREFERRED_WIDTH, WIDGET_PREFERRED_HEIGHT);
        create.setPosition((float) WIDTH/2 + WIDGET_PREFERRED_HEIGHT, (float) HEIGHT/2 - 20);
        create.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //creating server
                try {
                    entryPoint.server = new ServerSocket(parseInt(textFieldEnterPort.getText()), 2);
                    entryPoint.inviteToken = generateToken(getLocalHost().getHostAddress(),
                            entryPoint.server.getLocalPort());
                } catch (IOException | IllegalArgumentException e) {
                    return;
                }
                entryPoint.deviceState = SERVER;
                entryPoint.setScreen(entryPoint.room);
            }
        });
    }

    private void initInputLabel(){
        textFieldEnterPort.setPosition((float) WIDTH/2 - 130, (float) HEIGHT/2 + 100);
        textFieldEnterPort.setSize(WIDGET_PREFERRED_WIDTH + 100, WIDGET_PREFERRED_HEIGHT - 20);
        textFieldEnterPort.setTextFieldFilter((textField, c) -> {
            try {
                parseInt(valueOf(c));
                return true;
            }catch (Exception e){
                return false;
            }
        });
        textFieldEnterPort.setMaxLength(5);
        textFieldEnterPort.setAlignment(Align.center);
        if(input.isPeripheralAvailable(OnscreenKeyboard)) {
            //TODO decide 2
            textFieldEnterPort.setOnscreenKeyboard(visible ->
                    input.setOnscreenKeyboardVisible(true, NumberPad));
        }
    }

    private String generateToken(String host, int port){
        Base64.Encoder encoder = Base64.getEncoder();
        String orig = host + ":" + port;
        return encoder.encodeToString(orig.getBytes());
    }

    private void initBackground(){
        background.setPosition(0, 0);
        background.setSize(WIDTH, HEIGHT);
    }
}
