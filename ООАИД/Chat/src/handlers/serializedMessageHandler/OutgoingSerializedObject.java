package handlers.serializedMessageHandler;

import handlers.handlersInterface.OutgoingMessageHandler;
import protocol.messagetype.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by Yana on 03.12.15.
 */
public class OutgoingSerializedObject implements OutgoingMessageHandler {

    private final ObjectOutputStream objectOutputStream;

    public OutgoingSerializedObject(ObjectOutputStream outputStream){
        objectOutputStream = outputStream;
    }

    @Override
    public void go(Message message) throws IOException {
        if(message == null){
            System.out.println("NULL");
            throw new NullPointerException();
        }
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
        System.out.println("Send message TYPE " + message.getClass().getSimpleName());
    }
}
