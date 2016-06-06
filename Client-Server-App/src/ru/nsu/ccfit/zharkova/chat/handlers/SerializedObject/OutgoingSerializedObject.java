package ru.nsu.ccfit.zharkova.chat.handlers.SerializedObject;

import ru.nsu.ccfit.zharkova.chat.handlers.OutgoingMessageHandler;
import ru.nsu.ccfit.zharkova.chat.protocol.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by Yana on 05.07.15.
 */
public class OutgoingSerializedObject implements OutgoingMessageHandler {

    private final ObjectOutputStream objectOutputStream;

    public OutgoingSerializedObject(ObjectOutputStream outputStream){
        objectOutputStream = outputStream;
    }

    @Override
    public void go(Message message) throws IOException {
        if(message == null){
            throw new NullPointerException();
        }
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
    }
}
