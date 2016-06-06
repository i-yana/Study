package protocol.events;

import client.model.Client;
import client.model.Connection;
import protocol.messagetype.Event;
import protocol.messagetype.Message;
import protocol.messagetype.Request;

/**
 * Created by Yana on 03.12.15.
 */
public class FriendMSG implements Request, Event, Message {

    private final int friendID;
    private final Type type;
    public FriendMSG(int friendID, Type type){
        this.friendID = friendID;
        this.type = type;
    }

    public int getFriendID() {
        return friendID;
    }

    public Type getType() {
        return type;
    }

    public enum Type{
        REQEST, ACCEPT, REMOVE;
    }

    @Override
    public void notifyAboutEvent(Connection connection, Client client) {
        if(type.equals(Type.REQEST)){

        }
    }


}
