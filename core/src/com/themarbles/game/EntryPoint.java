package com.themarbles.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Null;
import com.themarbles.game.screens.CreateRoom;
import com.themarbles.game.screens.DefeatScreen;
import com.themarbles.game.screens.Room;
import com.themarbles.game.screens.JoinRoom;
import com.themarbles.game.screens.MainMenu;
import com.themarbles.game.screens.VictoryScreen;

import java.net.ServerSocket;
import java.net.Socket;

/** Screens manager, entrypoint of game. Contains important fields.
 * @see Socket
 * @see ServerSocket
 * @see com.badlogic.gdx.Screen
 */

 public class EntryPoint extends Game {

    public JoinRoom joinRoom;
    public CreateRoom createRoom;
    public MainMenu mainMenu;
    public Room room;
    public DefeatScreen defeatScreen;
    public VictoryScreen victoryScreen;

    public SpriteBatch batch;

    //backends
    @Null
    public ServerSocket server;
    public Socket client;

    //invite token
    @Null
    public String inviteToken;

    //device state
    public String deviceState;


    @SuppressWarnings("NewApi")
    @Override
    public void create() {
        mainMenu = new MainMenu(this);
        joinRoom = new JoinRoom(this);
        createRoom = new CreateRoom(this);
        room = new Room(this);
        defeatScreen = new DefeatScreen(this);
        victoryScreen = new VictoryScreen(this);
        batch = new SpriteBatch();

        setScreen(mainMenu);

    }
}
