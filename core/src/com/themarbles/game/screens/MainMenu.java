package com.themarbles.game.screens;


import static com.badlogic.gdx.Gdx.input;
import static com.badlogic.gdx.utils.ScreenUtils.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.themarbles.game.EntryPoint;
import com.themarbles.game.constants.Constants;

public class MainMenu implements Screen {
	private EntryPoint entryPoint;
	private Stage stage;
	private TextButton connect_button, create_button;

	public MainMenu(EntryPoint entryPoint) {
		this.entryPoint = entryPoint;

		connect_button = new TextButton("JOIN ROOM", new Skin(Gdx.files.internal("buttons/connectbuttonassets/connectbuttonskin.json")));
		create_button = new TextButton("CREATE ROOM",new Skin(Gdx.files.internal("buttons/createbuttonassets/createbuttonskin.json")));


		stage = new Stage();

	}


	@Override
	public void show() {

		initCreateRoomButton();
		initConnectToRoomButton();

		stage.addActor(connect_button);
		stage.addActor(create_button);

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

	private void initCreateRoomButton(){
		create_button.setSize(Constants.WIDGET_PREFERRED_WIDTH, Constants.WIDGET_PREFERRED_HEIGHT);
		create_button.setPosition((float) Constants.WIDTH/2 + Constants.WIDGET_PREFERRED_HEIGHT, (float) Constants.HEIGHT/2 - 60);
		create_button.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				entryPoint.setScreen(entryPoint.createRoom);
			}
		});
	}

	private void initConnectToRoomButton(){
		connect_button.setSize(Constants.WIDGET_PREFERRED_WIDTH, Constants.WIDGET_PREFERRED_HEIGHT);
		connect_button.setPosition((float) Constants.WIDTH/2 - Constants.WIDGET_PREFERRED_WIDTH - 20, (float) Constants.HEIGHT/2 - 60);
		connect_button.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				entryPoint.setScreen(entryPoint.joinRoom);
			}
		});
	}

}
