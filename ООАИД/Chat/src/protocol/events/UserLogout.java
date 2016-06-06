package protocol.events;

import client.model.Client;
import client.model.Connection;
import protocol.messagetype.Event;
import protocol.messagetype.Message;

/**
 * Created by Yana on 05.07.15.
 */
public class UserLogout implements Event, Message {

    private final Integer ID;

    public UserLogout(Integer ID) {
        this.ID = ID;
    }

    public Integer getID() {
        return ID;
    }

    @Override
    public void notifyAboutEvent(Connection connection, Client client) {

    }
}
