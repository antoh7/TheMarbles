package com.themarbles.game.networking;

import com.themarbles.game.Player;

import java.io.Serializable;

public class DataPacket implements Serializable {
    private final Player playerData;
    private final String gameState;
    private final boolean turnOrder;
    private final boolean playerReady;

    public static final long serialVersionUID = 3333333333L;

    public DataPacket(String gameState, boolean turnOrder, boolean playerReady, Player playerData){
        this.gameState = gameState;
        this.turnOrder = turnOrder;
        this.playerData = playerData;
        this.playerReady = playerReady;
    }

    public String getGameState() {
        return gameState;
    }

    public boolean getTurnOrder() {
        return turnOrder;
    }

    public boolean getPlayerReady() {
        return playerReady;
    }

    public Player getPlayerData(){
        return playerData;
    }

}