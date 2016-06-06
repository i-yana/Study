package protocol.answers;

import client.model.Client;
import client.model.Connection;
import protocol.requests.LoginMSG;
import protocol.messagetype.Message;
import protocol.requests.RegisterMSG;
import protocol.messagetype.ServerAnswer;

/**
 * Created by Yana on 03.12.15.
 */
public class ErrorMSG implements ServerAnswer, Message {
    private  final String reason;

    public ErrorMSG(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public void handle(Connection connection, Client client) {
        if(connection.getLastSendCommand() instanceof RegisterMSG) {
            connection.setMaySendRequest();
            client.notifyAboutAnswerAboutError(getReason());
            return;
        }
        if(connection.getLastSendCommand() instanceof LoginMSG){
            connection.setMaySendRequest();
            client.notifyAboutAnswerAboutError(getReason());
        }
    }
}
