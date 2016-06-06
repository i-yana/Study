package protocol.answers;

import client.model.Client;
import client.model.Connection;
import protocol.requests.GroupRequestMSG;
import protocol.messagetype.Message;
import protocol.messagetype.ServerAnswer;
import protocol.info.UserInfo;

import java.util.List;

/**
 * Created by Yana on 15.12.15.
 */
public class GroupParticipantsMSG implements ServerAnswer, Message {

    private final String groupName;
    private final List<UserInfo> participants;

    public GroupParticipantsMSG(String groupName, List<UserInfo> participants){
        this.groupName = groupName;
        this.participants = participants;
    }

    @Override
    public void handle(Connection connection, Client client) {
        if(connection.getLastSendCommand() instanceof GroupRequestMSG){
            connection.setMaySendRequest();
            System.out.println("Participants received");
            for(UserInfo u:participants){
                System.out.println(u.getLogin());
            }
            client.notifyGroupForUpdateParticipants(groupName, participants);
        }
    }
}
