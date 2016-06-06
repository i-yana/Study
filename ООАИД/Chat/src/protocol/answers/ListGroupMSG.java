package protocol.answers;

import client.model.Client;
import client.model.Connection;
import protocol.requests.ListRequestMSG;
import protocol.messagetype.Message;
import protocol.messagetype.ServerAnswer;
import protocol.info.Group;

import java.util.List;

/**
 * Created by Yana on 03.12.15.
 */
public class ListGroupMSG implements ServerAnswer, Message {
    private List<Group> groups;

    public ListGroupMSG(List<Group> groupList) {
        this.groups = groupList;
    }

    @Override
    public void handle(Connection connection, Client client) {
        if(connection.getLastSendCommand() instanceof ListRequestMSG){
            client.notifyAboutAnswerOnRequestGroupList(groups);
            connection.setMaySendRequest();
        }
    }
}
