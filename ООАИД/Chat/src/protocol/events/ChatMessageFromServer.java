package protocol.events;

import client.model.Client;
import client.model.Connection;
import protocol.messagetype.Event;
import protocol.messagetype.Message;

import java.awt.*;

/**
 * Created by Yana on 03.12.15.
 */
public class ChatMessageFromServer implements Event, Message {
    private final String message;
    private final Color color;
    private final String groupName;

    public ChatMessageFromServer(String groupName, String message, Color color) {
        this.groupName = groupName;
        this.message = message;
        this.color = color;
    }

    public String getMessage(){
        return message;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public void notifyAboutEvent(Connection connection, Client client) {
        client.notifyAboutMessageFromServer(getGroupName(), getMessage());
    }

    public String getGroupName() {
        return groupName;
    }
}
