package com.themarbles.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.themarbles.game.constants.Constants;
import com.themarbles.game.screens.CreateRoom;
import com.themarbles.game.screens.GameLogic;
import com.themarbles.game.screens.JoinRoom;
import com.themarbles.game.screens.MainMenu;

import java.net.ServerSocket;
import java.net.Socket;


public class EntryPoint extends Game {
    public JoinRoom joinRoom;
    public CreateRoom createRoom;
    public MainMenu mainMenu;
    public GameLogic gameLogic;
    public SpriteBatch batch;

    //backends
    public ServerSocket server;
    public Socket me;

    //invite token
    public String inviteToken;

    //player state
    public String playerState;


    @SuppressWarnings("NewApi")
    @Override
    public void create() {
        mainMenu = new MainMenu(this);
        joinRoom = new JoinRoom(this);
        createRoom = new CreateRoom(this);
        gameLogic = new GameLogic(this);
        batch = new SpriteBatch();
        OrthographicCamera

        //backends
        server = null;
        me = null;

        //token
        inviteToken = null;

        //player state
        playerState = null;

        setScreen(mainMenu);

    }
}
