package com.themarbles.game.constants;

import static com.badlogic.gdx.Gdx.graphics;
import static com.badlogic.gdx.math.MathUtils.ceil;

import com.badlogic.gdx.math.MathUtils;

/** Contains all necessary constants for a game.
 * @see MathUtils
 * @see com.badlogic.gdx.Graphics
 */

public class Constants {

    private static final float widthProportion = 0.2f;
    private static final float heightProportion = 0.1f;

    //widgets
    public static final int WIDTH = graphics.getWidth();
    public static final int HEIGHT = graphics.getHeight();
    public static final int WIDGET_PREFERRED_WIDTH = ceil((float) WIDTH*widthProportion);
    public static final int WIDGET_PREFERRED_HEIGHT = ceil((float) HEIGHT*heightProportion);

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
