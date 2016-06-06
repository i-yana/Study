package ru.nsu.ccfit.zharkova.chat.handlers.SerializedObject;

import ru.nsu.ccfit.zharkova.chat.handlers.IncomingMessageHandler;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by Yana on 05.07.15.
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

