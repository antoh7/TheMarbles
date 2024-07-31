package com.themarbles.game.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

/** The main part of game backends,
 *  receives ({@link Receiver#getData()}) and sends ({@link Receiver#sendData(DataPacket)}) data,
 *  wrapped in {@link DataPacket}, via {@link Socket}.
 * @see Socket
 * @see DataPacket
 *  **/

public class Receiver {

    private final Socket abstractSocket;

    private ObjectInputStream reader;
    private ObjectOutputStream writer;

    public Receiver(Socket abstractSocket) {
        this.abstractSocket = abstractSocket;
        try {
            writer = new ObjectOutputStream(abstractSocket.getOutputStream());
            reader = new ObjectInputStream(abstractSocket.getInputStream());
        } catch (IOException e){
            System.exit(5);
        }
    }

    public void sendData(DataPacket packet){
        try {

            writer.writeObject(packet);
            writer.flush();
            writer.reset();

        } catch (SocketException ignore){
            // doing nothing if second player disconnected
        } catch (IOException e) {
            System.exit(5);
        }
    }

    public DataPacket getData() {
        try {
            return (DataPacket) reader.readObject();
        } catch (IOException | ClassNotFoundException e){
            return null;
        }
    }

    public void disable(){
        try {
            abstractSocket.close();
            reader.close();
            writer.close();
        } catch (IOException e){
            System.exit(5);
        }

    }
}
