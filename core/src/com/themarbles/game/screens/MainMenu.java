package com.themarbles.game.screens;


import static com.badlogic.gdx.Gdx.audio;
import static com.badlogic.gdx.Gdx.files;
import static com.badlogic.gdx.Gdx.input;
import static com.themarbles.game.constants.Constants.HEIGHT;
import static com.themarbles.game.constants.Constants.WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.themarbles.game.EntryPoint;
import com.themarbles.game.constants.Constants;

/** Main menu with two buttons, activating one of these screens.
 * @see Screen
 * @see CreateRoom
 * @see JoinRoom
 * **/

public class MainMenu implements Screen {
	private final EntryPoint entryPoint;
	private final Stage stage;

	private final Image background;
	private final TextButton joinButton, createButton;

	private final Sound buttonPressedSound;

	public MainMenu(EntryPoint entryPoint) {
		this.entryPoint = entryPoint;

		stage = new Stage();

		background = new Image(new Texture(files.internal("textures/main_menu_background.jpg")));

		buttonPressedSound = audio.newSound(files.internal("sounds/button_pressed.mp3"));

		joinButton = new TextButton("JOIN ROOM", new Skin(Gdx.files.internal("buttons/connectbuttonassets/connectbuttonskin.json")));
		createButton = new TextButton("CREATE ROOM",new Skin(Gdx.files.internal("buttons/createbuttonassets/createbuttonskin.json")));

		initBackground();
		initCreateButton();
		initJoinButton();

	}


	@Override
	public void show() {

		stage.addActor(background);
		stage.addActor(joinButton);
		stage.addActor(createButton);

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
	}

	@Override
	public void dispose () {
		stage.dispose();
		buttonPressedSound.dispose();
	}

	//########################### init methods #####################

	private void initCreateButton(){
		createButton.setSize(Constants.WIDGET_PREFERRED_WIDTH, Constants.WIDGET_PREFERRED_HEIGHT);
		createButton.setPosition((float) Constants.WIDTH/2 + Constants.WIDGET_PREFERRED_HEIGHT, (float) Constants.HEIGHT/2 - 60);
		createButton.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				buttonPressedSound.play();
				entryPoint.setScreen(entryPoint.createRoom);
			}
		});
	}

	private void initJoinButton(){
		joinButton.setSize(Constants.WIDGET_PREFERRED_WIDTH, Constants.WIDGET_PREFERRED_HEIGHT);
		joinButton.setPosition((float) Constants.WIDTH/2 - Constants.WIDGET_PREFERRED_WIDTH - 20, (float) Constants.HEIGHT/2 - 60);
		joinButton.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				buttonPressedSound.play();
				entryPoint.setScreen(entryPoint.joinRoom);
			}
		});
	}

	private void initBackground(){
		background.setPosition(0, 0);
		background.setSize(WIDTH, HEIGHT);
	}

}
