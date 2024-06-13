package com.themarbles.game;

import com.themarbles.game.myImpls.SerializableImage;

import java.io.Serializable;

public class Player implements Serializable {

    private final SerializableImage playerHandClosed;
    private final SerializableImage playerHandOpened;
    private int marblesAmount;
    private int bet;
    private String statement;

    //hand params
    private final float default_x, default_y;
    private static final float defaultWidth = 380, defaultHeight = 460;


    public static final long serialVersionUID = 1111111111L;

    public Player(SerializableImage playerHandClosed, SerializableImage playerHandOpened, float handX, float handY){

        this.marblesAmount = 5;

        this.playerHandClosed = playerHandClosed;
        this.playerHandOpened = playerHandOpened;
        this.default_x = handX;
        this.default_y = handY;

        initHand(this.playerHandClosed, default_x, default_y, defaultWidth, defaultHeight);
        initHand(this.playerHandOpened, default_x, default_y, defaultWidth, defaultHeight);

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
        initHand(this.playerHandOpened, default_x, default_y, defaultWidth, defaultHeight);
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
