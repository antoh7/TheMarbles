package com.themarbles.game.screens;

import static com.badlogic.gdx.Gdx.app;
import static com.badlogic.gdx.Gdx.audio;
import static com.badlogic.gdx.Gdx.files;

import static com.badlogic.gdx.Gdx.input;
import static com.badlogic.gdx.Input.OnscreenKeyboardType.NumberPad;
import static com.badlogic.gdx.Input.Peripheral.OnscreenKeyboard;
import static com.themarbles.game.constants.Constants.HEIGHT;
import static com.themarbles.game.constants.Constants.SERVER;
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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.themarbles.game.EntryPoint;
import com.themarbles.game.utils.PreGameStartedUtils;
import com.themarbles.game.utils.ThreadFactory;

import java.io.IOException;
import java.net.ServerSocket;

/** Provides you a simple room creating menu.
 * @see Screen
 * @see Room
 * **/

public class CreateRoom implements Screen {
    private final EntryPoint entryPoint;
    private final TextField textFieldEnterPort;

    private final Image background;
    private final TextButton create, cancel;
    private final Stage stage;

    private final Sound buttonPressedSound;


    public CreateRoom(EntryPoint entryPoint) {
        this.entryPoint = entryPoint;
        stage = new Stage(new ScalingViewport(Scaling.fill, WIDTH, HEIGHT));

        background = new Image(new Texture(files.internal("textures/createroom_menu_background.jpg")));

        buttonPressedSound = audio.newSound(files.internal("sounds/button_pressed.mp3"));

        create = new TextButton("CREATE", new Skin(files.internal("buttons/createbuttonassets/createbuttonskin.json")));
        cancel = new TextButton("CANCEL",new Skin(files.internal("buttons/cancelbuttonassets/cancelbuttonskin.json")));
        textFieldEnterPort = new TextField("PORT (1 - 65535):",new Skin(files.internal("labels/enterlabel/enterlabelskin.json")));

        initBackground();
        initCancelButton();
        initCreateButton();
        initInputLabel();

    }

    @Override
    public void show() {

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
        stage.clear();
        input.setOnscreenKeyboardVisible(false);
    }

    @Override
    public void dispose() {
        stage.dispose();
        buttonPressedSound.dispose();
    }


    //############################# init methods ##########################

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

    private void initCreateButton(){
        create.setSize(WIDGET_PREFERRED_WIDTH, WIDGET_PREFERRED_HEIGHT);
        create.setPosition((float) WIDTH/2 + create.getWidth()/2, (float) HEIGHT/2 - 60);

        create.getLabel().setFontScale(MathUtils.floor(create.getWidth()/create.getMinWidth()),
                MathUtils.floor(create.getHeight()/create.getMinHeight()));

        create.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                //creating server
                try {
                    entryPoint.server = new ServerSocket(Integer.parseInt(textFieldEnterPort.getText()), 2);
                    entryPoint.inviteToken = PreGameStartedUtils.generateToken(PreGameStartedUtils.getDeviceIP(),
                            entryPoint.server.getLocalPort());
                } catch (IOException | IllegalArgumentException e) {
                    e.printStackTrace();
                    return;
                }
                entryPoint.deviceState = SERVER;

                buttonPressedSound.play();
                entryPoint.menuMusic.stop();

                entryPoint.setScreen(entryPoint.room);
            }
        });
    }

    private void initInputLabel(){
        textFieldEnterPort.setSize(WIDGET_PREFERRED_WIDTH + 100, WIDGET_PREFERRED_HEIGHT - 20);
        textFieldEnterPort.setPosition((float) WIDTH/2 - textFieldEnterPort.getWidth()/2, (float) HEIGHT/2 + 100);
        textFieldEnterPort.setTextFieldFilter((textField, c) -> {
            try {
                Integer.parseInt(String.valueOf(c));
                return true;
            }catch (NumberFormatException e){
                return false;
            }
        });
        textFieldEnterPort.setMaxLength(5);
        textFieldEnterPort.setAlignment(Align.center);
        if(input.isPeripheralAvailable(OnscreenKeyboard)) {
            textFieldEnterPort.setOnscreenKeyboard(visible ->
                    input.setOnscreenKeyboardVisible(true, NumberPad));
        }
    }

    private void initBackground(){
        background.setPosition(0, 0);
        background.setSize(WIDTH, HEIGHT);
    }

}
