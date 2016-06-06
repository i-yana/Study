package protocol.events;

import client.model.Client;
import client.model.Connection;
import protocol.messagetype.Event;
import protocol.messagetype.Message;

/**
 * Created by Yana on 03.12.15.
 */
public class UserLogin implements Event, Message {
    private final int ID;
    private final String login;

    public UserLogin(int ID, String login) {
        this.ID = ID;
        this.login = login;
    }

    public String getLogin(){
        return login;
    }

    public int getID() {
        return ID;
    }

    @Override
    public void notifyAboutEvent(Connection connection, Client client) {

    }
}
