package protocol.requests;

import client.model.Client;
import client.model.Connection;
import protocol.messagetype.Event;
import protocol.messagetype.Message;
import protocol.messagetype.Request;
import protocol.info.UserInfo;

import java.util.List;

/**
 * Created by Yana on 03.12.15.
 */
public class GroupRequestMSG implements Request, Event, Message {


    public Type getType() {
        return type;
    }

    public List<UserInfo> getParticipants() {
        return participants;
    }

    public boolean isPublic() {
        return groupType;
    }

    public enum Type{
        CREATE, DELETE, JOIN, KICK, PARTICIPANTS;
    }
    private String groupName;
    private boolean groupType;
    private List<UserInfo> participants;
    private Type type;

    public GroupRequestMSG(String groupName, boolean groupType, Type type){//от клиента
        this.groupName = groupName;
        this.groupType = groupType;
        this.type = type;
    }

    public GroupRequestMSG(String groupName, boolean groupType, List<UserInfo> allParticipants, Type type){//от сервера
        this.participants = allParticipants;
        this.groupName = groupName;
        this.groupType = groupType;
        this.type = type;
    }
    public String getGroupName() {
        return groupName;
    }


    @Override
    public void notifyAboutEvent(Connection connection, Client client) {
        if(type.equals(Type.CREATE)) {
            client.notifyAboutNewGroup(groupName, groupType);
            return;
        }
        if(type.equals(Type.DELETE)){
            client.notifyAboutDeleteGroup(groupName);
            return;
        }
        if(type.equals(Type.JOIN)){
            for (UserInfo u:participants){
                System.out.println(u.getLogin());
            }
            client.notifyGroupForUpdateParticipants(groupName, participants);
            return;
        }
        if(type.equals(Type.KICK)){
            client.notifyGroupForUpdateParticipants(groupName, participants);
        }
    }
}
