package handlers.serializedMessageHandler;

import handlers.handlersInterface.IncomingMessageHandler;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by Yana on 03.12.15.
 */
public class IncomingSerializedObject implements IncomingMessageHandler {
    private final ObjectInputStream objectInputStream;

    public IncomingSerializedObject(ObjectInputStream objectInputStream) {
        this.objectInputStream = objectInputStream;
    }

    @Override
    public Object getObject() throws IOException, ClassNotFoundException {
        return objectInputStream.readObject();
    }
}
