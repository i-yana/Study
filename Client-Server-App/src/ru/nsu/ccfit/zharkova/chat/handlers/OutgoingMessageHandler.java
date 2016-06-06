package ru.nsu.ccfit.zharkova.chat.handlers;

import ru.nsu.ccfit.zharkova.chat.protocol.Message;

import javax.xml.transform.TransformerException;
import java.io.IOException;

/**
 * Created by Yana on 05.07.15.
 */
public interface OutgoingMessageHandler {

    public void go(Message message) throws IOException, TransformerException;
}
