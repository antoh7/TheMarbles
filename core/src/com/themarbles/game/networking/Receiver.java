package com.themarbles.game.networking;

import com.themarbles.game.interfaces.Executable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class Receiver {

    private Socket abstractSocket;

    private ObjectInputStream reader;
    private ObjectOutputStream writer;

    private long logNum = 0;

    public Receiver(Socket abstractSocket) {
        this.abstractSocket = abstractSocket;
        try {
            writer = new ObjectOutputStream(abstractSocket.getOutputStream());
            reader = new ObjectInputStream(abstractSocket.getInputStream());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendData(DataPacket packet){
        try {

            writer.writeObject(packet);

            writer.flush();
            writer.reset();

            System.out.printf("*********COMMITTED(%d)***********\n", logNum);
            //System.out.println("[" + logNum + "]  " + "game_state: " + packet.getGameState());
            //System.out.println("[" + logNum + "]  " + "ourTurn: " + packet.getTurnOrder());
            //System.out.println("[" + logNum + "]  " + "weReady: " + packet.getPlayerReady());
            System.out.println("[" + logNum + "]  " + "bet: " + packet.getPlayerData().getBet());
            System.out.println("[" + logNum + "]  " + "statement: " + packet.getPlayerData().getStatement());
            System.out.println("[" + logNum + "]  " + "marblesAmount: " + packet.getPlayerData().getMarblesAmount());
            System.out.println("[" + logNum + "]  " + "*****************************");
            logNum++;
        } catch (SocketException ignored){
            //TODO do smth
        } catch (IOException e) {
            System.exit(1);
        }
    }

    public DataPacket getData() {
        try {
            DataPacket received = (DataPacket) reader.readObject();
            System.out.printf("*********RECEIVED(%d)***********\n", logNum);
            //System.out.println("[" + logNum + "]  " + "game_state: " + currPacket.getGameState());
            //System.out.println("[" + logNum + "]  " + "ourTurn: " + currPacket.getTurnOrder());
            //System.out.println("[" + logNum + "]  " + "weReady: " + weReady);
            //System.out.println("[" + logNum + "]  " + "opponentReady: " + currPacket.getPlayerReady());
            System.out.println("[" + logNum + "]  " + "bet: " + received.getPlayerData().getBet());
            System.out.println("[" + logNum + "]  " + "statement: " + received.getPlayerData().getStatement());
            System.out.println("[" + logNum + "]  " + "marbles amount: " + received.getPlayerData().getMarblesAmount());
            System.out.println("[" + logNum + "]  " + "*****************************");
            logNum++;
            return received;
        } catch (SocketException e){
            System.exit(1488);
            return null;
        } catch (IOException | ClassNotFoundException e){
            System.out.println("IOException/CNFException: " + e.getMessage());
            return null;
        }
    }

    public void onDisconnect(Executable task){
        System.out.println("executing: onDisconnect");
        task.execute();
    }

    public void disable(){
        try {
            abstractSocket.close();
            reader.close();
            writer.close();
        } catch (IOException e){
            System.exit(1);
        }

    }
}
