package client.model;


import handlers.handlersInterface.IncomingMessageHandler;
import handlers.handlersInterface.OutgoingMessageHandler;
import handlers.serializedMessageHandler.IncomingSerializedObject;
import handlers.serializedMessageHandler.OutgoingSerializedObject;
import protocol.messagetype.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Yana on 05.12.15.
 */
public class Connection {

    private final Socket socket;
    private final Thread sendController;
    private final Thread receiveController;

    private final Queue<Message> commandQueue = new LinkedList<Message>();
    private volatile boolean  maySendRequest = true;
    private Message lastSendCommand = null;


    public Connection(Socket socket, Client client) throws IOException {
        this.socket = socket;
        OutgoingMessageHandler os = new OutgoingSerializedObject(new ObjectOutputStream(socket.getOutputStream()));
        IncomingMessageHandler is = new IncomingSerializedObject(new ObjectInputStream(socket.getInputStream()));
        sendController = new SendController(os, this);
        receiveController = new ReceiveController(is, this, client);
    }

    public void startConnection() {
        sendController.start();
        receiveController.start();
    }

    public void stopConnection(){
        receiveController.interrupt();
        sendController.interrupt();
        System.exit(1);
        try {
            socket.close();
        } catch (IOException ignored) {}
    }

    public void putMessage(Message message){
        synchronized (commandQueue){
            System.out.println("put message TYPE " + message.getClass().getSimpleName());
            commandQueue.offer(message);
            commandQueue.notifyAll();
        }
    }

    public Message getMessage() throws InterruptedException {
        Message command;
        synchronized (commandQueue) {
            while (!maySendRequest || commandQueue.isEmpty()) {
                commandQueue.wait();
            }
            command = commandQueue.poll();
            lastSendCommand = command;
            maySendRequest = false;
            return command;
        }
    }

    public void setMaySendRequest(){
        synchronized (commandQueue) {
            maySendRequest = true;
            commandQueue.notifyAll();
        }
    }

    public Message getLastSendCommand() {
        synchronized (commandQueue) {
            return lastSendCommand;
        }
    }


}
