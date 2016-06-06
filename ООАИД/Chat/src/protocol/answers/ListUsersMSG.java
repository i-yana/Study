package protocol.answers;


import client.model.Client;
import client.model.Connection;
import protocol.messagetype.Message;
import protocol.messagetype.ServerAnswer;
import protocol.info.UserInfo;
import protocol.requests.ListRequestMSG;

import java.util.List;

/**
 * Created by Yana on 05.07.15.
 */
public class ListUsersMSG implements ServerAnswer, Message {
    private final List<UserInfo> contacts;

    public ListUsersMSG(List<UserInfo> contacts) {
        this.contacts = contacts;
    }

    public List<UserInfo> getContacts(){
        return contacts;
    }

    @Override
    public void handle(Connection connection, Client client) {
        if(connection.getLastSendCommand() instanceof ListRequestMSG){
            connection.setMaySendRequest();
            client.notifyAboutAnswerOnRequestUserList(getContacts());
        }

    }


}
