package protocol.requests;

import protocol.messagetype.Message;
import protocol.messagetype.Request;

/**
 * Created by Yana on 03.12.15.
 */
public class ListRequestMSG implements Request, Message {

    public enum Type{
        GROUPS,FRIENDS
    }
    private final Type type;

    public ListRequestMSG(Type type){
        this.type = type;
    }

    public Type getType() {
        return type;
    }

}
