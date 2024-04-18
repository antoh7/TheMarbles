package com.themarbles.game.networking;

import static com.badlogic.gdx.Gdx.*;

import com.badlogic.gdx.Gdx;
import com.themarbles.game.interfaces.Executable;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class Receiver {

    private Socket abstractSocket;

    private ObjectInputStream reader;
    private ObjectOutputStream writer;

    public Receiver(Socket abstractSocket) {
        this.abstractSocket = abstractSocket;
        try {
            writer = new ObjectOutputStream(abstractSocket.getOutputStream());
            reader = new ObjectInputStream(abstractSocket.getInputStream());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void send_data(DataPacket packet){
        try {
            writer.writeObject(packet);
            writer.flush();
        } catch (SocketException ignored){
            //TODO do smth
        } catch (IOException e) {
            System.exit(1);
        }
    }

    public DataPacket get_data() {
        try {
            return (DataPacket) reader.readObject();
        } catch (SocketException e){
            return null;
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }

    public void onDisconnect(Executable task){
        System.out.println("executing: onDisconnect");
        task.execute();
    }

    public boolean disconnected(){
        return !abstractSocket.isConnected();
    }

    public boolean connected(){
        return abstractSocket.isConnected();
    }

    public void release(){
        try {
            abstractSocket.close();
            reader.close();
            writer.close();
        } catch (IOException e){
            System.exit(1);
        }

    }
}
