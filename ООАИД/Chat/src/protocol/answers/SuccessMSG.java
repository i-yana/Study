package protocol.answers;

import client.model.Client;
import client.model.Connection;
import protocol.messagetype.Message;
import protocol.messagetype.ServerAnswer;
import protocol.events.FriendMSG;
import protocol.requests.ChatMessageFromClient;
import protocol.requests.GroupRequestMSG;
import protocol.requests.LogoutMSG;

/**
 * Created by Yana on 28.07.15.
 */
public class SuccessMSG implements ServerAnswer, Message {

    private final String info;
    private final String type;

    public SuccessMSG(String info, String type){
        this.info = info;
        this.type = type;
    }

    @Override
    public void handle(Connection connection, Client client) {
        if(connection.getLastSendCommand() instanceof LogoutMSG){
            connection.setMaySendRequest();
            client.notifyAboutSuccessLogout();

            return;
        }
        if(connection.getLastSendCommand() instanceof ChatMessageFromClient){
            connection.setMaySendRequest();
            client.notifyAboutMessageSuccessAnswer();
            return;
        }
        if(connection.getLastSendCommand() instanceof GroupRequestMSG){
            if(type.equals("kick")){
                connection.setMaySendRequest();
                client.notifyAboutLeave(info);
                return;
            }
            if(type.equals("create")) {
                connection.setMaySendRequest();
                client.notifyAboutSuccessCreateGroup(info);
                return;
            }
            if(type.equals("join")){
                connection.setMaySendRequest();
                client.notifyAboutSuccessJoin(info);
            }
        }
        if(connection.getLastSendCommand() instanceof FriendMSG){
            connection.setMaySendRequest();
            return;
        }
    }

    public String getInfo() {
        return info;
    }
}
