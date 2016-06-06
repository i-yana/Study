package handlers.handlersInterface;

import java.io.IOException;

/**
 * Created by Yana on 03.12.15.
 */
public interface IncomingMessageHandler {
    public Object getObject() throws IOException, ClassNotFoundException;
}
