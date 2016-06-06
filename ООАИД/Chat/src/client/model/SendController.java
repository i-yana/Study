package client.model;

import handlers.handlersInterface.OutgoingMessageHandler;
import protocol.messagetype.Message;

import javax.xml.transform.TransformerException;
import java.io.IOException;

/**
 * Created by Yana on 05.12.15.
 */
public class SendController extends Thread{

    private final OutgoingMessageHandler senderToServer;
    private final Connection connection;


    public SendController(OutgoingMessageHandler senderToServer, Connection connection) {
        this.senderToServer = senderToServer;
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            while(!Thread.currentThread().isInterrupted()){
                Message message = connection.getMessage();
                System.out.println("go");
                senderToServer.go(message);
            }
        } catch (IOException | InterruptedException | TransformerException e) {
            System.err.println("server disconnect");
            connection.stopConnection();

        }
    }
}
