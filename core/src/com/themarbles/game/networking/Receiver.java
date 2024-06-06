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

        } catch (SocketException disconnected){
            //TODO do smth
        } catch (IOException e) {
            System.exit(10);
        }
    }

    public DataPacket getData() {
        try {
            return (DataPacket) reader.readObject();
        } catch (SocketException e){
            System.exit(10);
            return null;
        } catch (IOException | ClassNotFoundException e){
            System.out.println(e.getMessage());
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
