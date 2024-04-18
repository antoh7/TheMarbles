package com.themarbles.game.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;
import com.themarbles.game.EntryPoint;
import com.themarbles.game.constants.Constants;

import java.io.IOException;
import java.lang.StringIndexOutOfBoundsException;
import java.net.Socket;
import java.util.Base64;

import static com.badlogic.gdx.Gdx.*;
import static com.badlogic.gdx.utils.ScreenUtils.clear;

public class JoinRoom implements Screen {
    private EntryPoint entryPoint;
    private TextField textFieldEnterToken;
    private TextButton create, cancel;
    private Stage stage;
    public JoinRoom(EntryPoint entryPoint) {
        this.entryPoint = entryPoint;
        stage = new Stage();

        create = new TextButton("JOIN", new Skin(files.internal("buttons/connectbuttonassets/connectbuttonskin.json")));
        cancel = new TextButton("CANCEL",new Skin(files.internal("buttons/cancelbuttonassets/cancelbuttonskin.json")));
        textFieldEnterToken = new TextField("ENTER TOKEN:",new Skin(files.internal("labels/enterlabel/enterlabelskin.json")));


    }

    @Override
    public void show() {

        initCancelButton();
        initConnectButton();
        initTokenInputLabel();

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
        cancel.setPosition((float) Constants.WIDTH/2 - Constants.WIDGET_PREFERRED_WIDTH - 20, (float) Constants.HEIGHT/2 - 60);
        cancel.setSize(Constants.WIDGET_PREFERRED_WIDTH,Constants.WIDGET_PREFERRED_HEIGHT);
        cancel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor){
                entryPoint.setScreen(entryPoint.mainMenu);
            }
        });
    }

    private void initConnectButton(){
        create.setPosition((float) Constants.WIDTH/2 + Constants.WIDGET_PREFERRED_HEIGHT, (float) Constants.HEIGHT/2 - 60);
        create.setSize(Constants.WIDGET_PREFERRED_WIDTH,Constants.WIDGET_PREFERRED_HEIGHT);
        create.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //trying to create new client
                try {
                    String decToken = decodeToken(textFieldEnterToken.getText());
                    String host = getHost(decToken);
                    int port = getPort(decToken);
                    entryPoint.me = new Socket(host, port);
                } catch (IOException | StringIndexOutOfBoundsException | IllegalArgumentException e) {
                    return;
                }
                entryPoint.playerState = Constants.CLIENT;
                entryPoint.setScreen(entryPoint.gameLogic);
            }
        });
    }

    private void initTokenInputLabel(){
        textFieldEnterToken.setPosition((float) Constants.WIDTH/2 - 130, (float) Constants.HEIGHT/2 + 100);
        textFieldEnterToken.setSize(Constants.WIDGET_PREFERRED_WIDTH + 100, Constants.WIDGET_PREFERRED_HEIGHT - 20);
        textFieldEnterToken.setAlignment(Align.center);
        if(input.isPeripheralAvailable(Input.Peripheral.OnscreenKeyboard)) {
            textFieldEnterToken.setOnscreenKeyboard(visible -> {
                input.setOnscreenKeyboardVisible(true, Input.OnscreenKeyboardType.Default);
            });
        }

    }

    private String decodeToken(String token){
        StringBuilder builder = new StringBuilder();
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decoded_bytes = decoder.decode(token);
        for (byte b: decoded_bytes){
            builder.append((char) b);
        }
        return builder.toString();
    }

    private String getHost(String decoded_token){
        return decoded_token.substring(0,decoded_token.indexOf(":"));
    }

    private int getPort(String decoded_token){
        return Integer.parseInt(decoded_token.substring(decoded_token.indexOf(":") + 1));
    }
}
