package ru.kbuearpov.themarbles.networking;

import ru.kbuearpov.themarbles.Player;

import java.io.Serializable;

/** Represents a simple container for data, sent to other device.
 * @see Player
 * @see Serializable
 * **/

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

    @Override
    public String toString(){
        return """
                turnOrder: %b,
                playerReady: %b,
                playerBet: %d,
                playerStatement: %s,
                playerMarblesAmount: %d
               """
                .formatted(turnOrder,
                        playerReady,
                        playerData.getBet(),
                        playerData.getStatement(),
                        playerData.getMarblesAmount());
    }
}
