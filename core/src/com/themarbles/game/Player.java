package com.themarbles.game;

import static com.themarbles.game.constants.Constants.HEIGHT;
import static com.themarbles.game.constants.Constants.WIDTH;

import com.themarbles.game.myImpls.SerializableImage;

import java.io.Serializable;

/** Represents basic game entity, containing marbles, hand texture, statement and bet.
 * @see SerializableImage
 */

public class Player implements Serializable {

    private final SerializableImage playerHandClosed;
    private final SerializableImage playerHandOpened;

    private int marblesAmount;
    private int bet;
    private String statement;

    // hand params
    private final float defaultX, defaultY;
    private static final float defaultWidth = (float) WIDTH/3, defaultHeight = HEIGHT - (float) HEIGHT/3;


    public static final long serialVersionUID = 1111111111L;

    public Player(SerializableImage playerHandClosed, SerializableImage playerHandOpened, float handX, float handY){

        this.marblesAmount = 5;

        this.playerHandClosed = playerHandClosed;
        this.playerHandOpened = playerHandOpened;
        this.defaultX = handX;
        this.defaultY = handY;

        initHand(this.playerHandClosed, defaultX, defaultY, defaultWidth, defaultHeight);
        initHand(this.playerHandOpened, defaultX, defaultY, defaultWidth, defaultHeight);

        setHandVisible(this.playerHandClosed, false);
        setHandVisible(this.playerHandOpened, false);
    }

    public SerializableImage getPlayerHandClosed(){
        return playerHandClosed;
    }

    public SerializableImage getPlayerHandOpened(){
        return playerHandOpened;
    }

    public void setPlayerHandOpened(SerializableImage playerHandOpened){
        this.playerHandOpened.setDrawable(playerHandOpened.getDrawable());
        initHand(this.playerHandOpened, defaultX, defaultY, defaultWidth, defaultHeight);
    }

    public void setHandVisible(SerializableImage hand, boolean state){
        hand.setVisible(state);
    }

    public int getMarblesAmount() {
        return marblesAmount;
    }

    public void setMarblesAmount(int marblesAmount) {
        this.marblesAmount = marblesAmount;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    private void initHand(SerializableImage hand, float x, float y, float width, float height){
        hand.setPosition(x, y);
        hand.setSize(width, height);
    }

    public static float getDefaultHandWidth(){
        return defaultWidth;
    }

    public static float getDefaultHandHeight(){
        return defaultHeight;
    }

}
