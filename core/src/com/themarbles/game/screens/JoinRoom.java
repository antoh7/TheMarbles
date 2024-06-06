package com.themarbles.game.screens;

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
import com.badlogic.gdx.utils.StringBuilder;
import com.themarbles.game.EntryPoint;

import java.io.IOException;
import java.net.Socket;
import java.util.Base64;

import static com.badlogic.gdx.Gdx.*;
import static com.badlogic.gdx.Input.OnscreenKeyboardType.Password;
import static com.badlogic.gdx.Input.Peripheral.OnscreenKeyboard;
import static com.badlogic.gdx.utils.ScreenUtils.clear;
import static com.themarbles.game.constants.Constants.*;
import static java.lang.Integer.parseInt;
import static java.util.Base64.getDecoder;

public class JoinRoom implements Screen {
    private final EntryPoint entryPoint;
    private final TextField textFieldEnterToken;
    private final TextButton create, cancel;
    private final Stage stage;
    private final Image background;
    public JoinRoom(EntryPoint entryPoint) {
        this.entryPoint = entryPoint;
        stage = new Stage();

        background = new Image(new Texture(files.internal("textures/joinroom_menu_background.jpg")));

        create = new TextButton("JOIN", new Skin(files.internal("buttons/connectbuttonassets/connectbuttonskin.json")));
        cancel = new TextButton("CANCEL",new Skin(files.internal("buttons/cancelbuttonassets/cancelbuttonskin.json")));
        textFieldEnterToken = new TextField("ENTER TOKEN:",new Skin(files.internal("labels/enterlabel/enterlabelskin.json")));


    }

    @Override
    public void show() {

        initBackground();
        initCancelButton();
        initConnectButton();
        initTokenInputLabel();

        stage.addActor(background);
        stage.addActor(create);
        stage.addActor(cancel);
        stage.addActor(textFieldEnterToken);

        input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        clear(1,1,1,1);

        stage.act(graphics.getDeltaTime());
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
        cancel.setPosition((float) WIDTH/2 - WIDGET_PREFERRED_WIDTH - 20, (float) HEIGHT/2 - 60);
        cancel.setSize(WIDGET_PREFERRED_WIDTH, WIDGET_PREFERRED_HEIGHT);
        cancel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor){
                entryPoint.setScreen(entryPoint.mainMenu);
            }
        });
    }

    private void initConnectButton(){
        create.setPosition((float) WIDTH/2 + WIDGET_PREFERRED_HEIGHT, (float) HEIGHT/2 - 60);
        create.setSize(WIDGET_PREFERRED_WIDTH, WIDGET_PREFERRED_HEIGHT);
        create.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //trying to create new client
                try {
                    String decToken = decodeToken(textFieldEnterToken.getText());
                    String host = getHost(decToken);
                    int port = getPort(decToken);
                    entryPoint.client = new Socket(host, port);
                } catch (IOException | StringIndexOutOfBoundsException | IllegalArgumentException e) {
                    return;
                }
                entryPoint.deviceState = CLIENT;
                entryPoint.setScreen(entryPoint.room);
            }
        });
    }

    private void initTokenInputLabel(){
        textFieldEnterToken.setPosition((float) WIDTH/2 - 130, (float) HEIGHT/2 + 100);
        textFieldEnterToken.setSize(WIDGET_PREFERRED_WIDTH + 100, WIDGET_PREFERRED_HEIGHT - 20);
        textFieldEnterToken.setAlignment(Align.center);
        if(input.isPeripheralAvailable(OnscreenKeyboard)) {
            //TODO decide
            textFieldEnterToken.setOnscreenKeyboard(visible ->
                    input.setOnscreenKeyboardVisible(true, Password));
        }

    }

    private String decodeToken(String token){
        StringBuilder builder = new StringBuilder();
        Base64.Decoder decoder = getDecoder();
        byte[] decoded_bytes = decoder.decode(token);
        for (byte b: decoded_bytes){
            builder.append((char) b);
        }
        return builder.toString();
    }

    private String getHost(String decodedToken){
        return decodedToken.substring(0,decodedToken.indexOf(":"));
    }

    private int getPort(String decodedToken){
        return parseInt(decodedToken.substring(decodedToken.indexOf(":") + 1));
    }

    private void initBackground(){
        background.setPosition(0, 0);
        background.setSize(WIDTH, HEIGHT);
    }
}
