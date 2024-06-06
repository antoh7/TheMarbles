package com.themarbles.game.screens;


import static com.badlogic.gdx.Gdx.files;
import static com.badlogic.gdx.Gdx.input;
import static com.badlogic.gdx.utils.ScreenUtils.*;
import static com.themarbles.game.constants.Constants.HEIGHT;
import static com.themarbles.game.constants.Constants.WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.themarbles.game.EntryPoint;
import com.themarbles.game.constants.Constants;

public class MainMenu implements Screen {
	private final EntryPoint entryPoint;
	private final Stage stage;

	private final Image background;
	private final TextButton joinButton, createButton;

	public MainMenu(EntryPoint entryPoint) {
		this.entryPoint = entryPoint;

		background = new Image(new Texture(files.internal("textures/main_menu_background.jpg")));

		joinButton = new TextButton("JOIN ROOM", new Skin(Gdx.files.internal("buttons/connectbuttonassets/connectbuttonskin.json")));
		createButton = new TextButton("CREATE ROOM",new Skin(Gdx.files.internal("buttons/createbuttonassets/createbuttonskin.json")));

		stage = new Stage();

	}


	@Override
	public void show() {

		initBackground();
		initCreateButton();
		initJoinButton();

		stage.addActor(background);
		stage.addActor(joinButton);
		stage.addActor(createButton);

		input.setInputProcessor(stage);

	}

	@Override
	public void render(float delta) {
		clear(1,1,1,1);

		stage.act(Gdx.graphics.getDeltaTime());

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

	}

	@Override
	public void dispose () {
		stage.dispose();

	}

	//################## private methods ###################

	private void initCreateButton(){
		createButton.setSize(Constants.WIDGET_PREFERRED_WIDTH, Constants.WIDGET_PREFERRED_HEIGHT);
		createButton.setPosition((float) Constants.WIDTH/2 + Constants.WIDGET_PREFERRED_HEIGHT, (float) Constants.HEIGHT/2 - 60);
		createButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				entryPoint.setScreen(entryPoint.createRoom);
			}
		});
	}

	private void initJoinButton(){
		joinButton.setSize(Constants.WIDGET_PREFERRED_WIDTH, Constants.WIDGET_PREFERRED_HEIGHT);
		joinButton.setPosition((float) Constants.WIDTH/2 - Constants.WIDGET_PREFERRED_WIDTH - 20, (float) Constants.HEIGHT/2 - 60);
		joinButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				entryPoint.setScreen(entryPoint.joinRoom);
			}
		});
	}

	private void initBackground(){
		background.setPosition(0, 0);
		background.setSize(WIDTH, HEIGHT);
	}

}
