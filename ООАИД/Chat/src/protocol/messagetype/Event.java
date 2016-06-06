package protocol.messagetype;

import client.model.Client;
import client.model.Connection;

/**
 * Created by Yana on 03.12.15.
 */
public interface Event {
    void notifyAboutEvent(Connection connection, Client client);
}
