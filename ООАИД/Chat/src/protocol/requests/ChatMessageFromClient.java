package protocol.requests;

import protocol.messagetype.Message;
import protocol.messagetype.Request;

import java.awt.*;

/**
 * Created by Yana on 28.07.15.
 */
public class ChatMessageFromClient implements Request, Message {

    private final String nick;
    private final String message;
    private final String groupName;
    private final Color color;

    public ChatMessageFromClient(String nick, String message, String groupName, Color color){
        this.message = message;
        this.groupName = groupName;
        this.color = color;
        this.nick = nick;
    }

    public String getMessage() {
        return message;
    }

    public Color getColor() {
        return color;
    }

    public String getNick() {
        return nick;
    }

    public String getGroupName() {
        return groupName;
    }
}
