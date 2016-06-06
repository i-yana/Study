package protocol.answers;

import client.model.Client;
import client.model.Connection;
import protocol.messagetype.Message;
import protocol.requests.RegisterMSG;
import protocol.messagetype.ServerAnswer;
import protocol.requests.LoginMSG;

/**
 * Created by Yana on 28.07.15.
 */
public class LoginSuccessMSG implements ServerAnswer, Message {

    private Integer ID;

    public LoginSuccessMSG(Integer id) {
        this.ID = id;
    }

    @Override
    public void handle(Connection connection, Client client) {
        if(connection.getLastSendCommand() instanceof RegisterMSG){
            client.notifyAboutAnswerAboutCompleteRegistration();
            connection.setMaySendRequest();
            return;
        }
        if(connection.getLastSendCommand() instanceof LoginMSG){
            client.notifyAboutAnswerAboutCompleteAuthorization();
            connection.setMaySendRequest();
            return;
        }
    }
}
