package com.themarbles.game.constants;

import static com.badlogic.gdx.Gdx.graphics;

import com.badlogic.gdx.math.MathUtils;

public class Constants {

    //widgets
    public static final int WIDTH = graphics.getWidth();
    public static final int HEIGHT = graphics.getHeight();
    public static final int WIDGET_PREFERRED_WIDTH = MathUtils.ceil((float) WIDTH/HEIGHT *  100);
    public static final int WIDGET_PREFERRED_HEIGHT = MathUtils.ceil((float) HEIGHT/WIDTH * 100);

    //device states
    public static final String SERVER = "SERVER";
    public static final String CLIENT = "CLIENT";

    //game states
    public static final String WAITING_FOR_START = "WAITING_FOR_START";
    public static final String WAITING_FOR_PLAYER_CONNECT = "WAITING_FOR_PLAYER_CONNECT";
    public static final String GAME_RUNNING = "GAME_RUNNING";
    public static final String RANDOMIZING_TURN = "RANDOMIZING_TURN";
    public static final String GAME_FINISHED = "GAME_FINISHED";

    //statements
    public static final String EVEN = "EVEN";
    public static final String ODD = "ODD";
}
