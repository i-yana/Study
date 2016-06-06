package handlers.handlersInterface;

import protocol.messagetype.Message;

import javax.xml.transform.TransformerException;
import java.io.IOException;

/**
 * Created by Yana on 03.12.15.
 */
public interface OutgoingMessageHandler {
    public void go(Message message) throws IOException, TransformerException;
}
