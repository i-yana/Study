package ru.nsu.ccfit.zharkova.chat.handlers;

import java.io.IOException;

/**
 * Created by Yana on 05.07.15.
 */
public interface IncomingMessageHandler {

    public Object getObject() throws IOException, ClassNotFoundException;
}
