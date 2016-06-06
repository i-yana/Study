package protocol.answers;

import client.model.Client;
import client.model.Connection;
import protocol.messagetype.Message;
import protocol.messagetype.Request;
import protocol.messagetype.ServerAnswer;

/**
 * Created by Yana on 03.12.15.
 */
public class SearchUserMSG implements Request, ServerAnswer, Message {

    private String login;
    private int user_ID;

    public SearchUserMSG(String login){
        this.login = login;
    }

    public SearchUserMSG(int user_id) {
        this.user_ID = user_id;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public void handle(Connection connection, Client client) {

    }
}
