package client.model;

import handlers.handlersInterface.IncomingMessageHandler;
import protocol.messagetype.Event;
import protocol.messagetype.Message;
import protocol.messagetype.ServerAnswer;

import java.io.EOFException;
import java.io.IOException;
import java.io.OptionalDataException;

/**
 * Created by Yana on 05.12.15.
 */
public class ReceiveController extends Thread {


    public static final String YOU_ARE_NOT_USING_THE_PROTOCOL = "you are not using the protocol";
    private final IncomingMessageHandler receiverToServer;
    private final Connection connection;

    private Client client;

    public ReceiveController(IncomingMessageHandler receiverToServer, Connection connection, Client client) {
        this.receiverToServer = receiverToServer;
        this.connection = connection;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            while(!Thread.currentThread().isInterrupted()){

                Message message = (Message) receiverToServer.getObject();
                System.out.println("Receive message TYPE " + message.getClass().getSimpleName());
                handleMessage(message);
            }
        } catch ( ClassCastException | OptionalDataException | EOFException e) {
            client.notifyAboutAnswerAboutError(YOU_ARE_NOT_USING_THE_PROTOCOL);
            connection.stopConnection();
            System.exit(1);
        }catch (IOException | ClassNotFoundException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void handleMessage(Message message){
        if(message instanceof Event){
            handleEvent((Event) message);
        }else if(message instanceof ServerAnswer){
            handleAnswer((ServerAnswer) message);
        }
    }


    private void handleAnswer(ServerAnswer message){
        System.out.println("answer");
        message.handle(connection, client);
    }

    private void handleEvent(Event event){
        System.out.println("event");
        event.notifyAboutEvent(connection, client);
    }
}
