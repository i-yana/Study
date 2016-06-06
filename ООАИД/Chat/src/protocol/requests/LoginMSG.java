package protocol.requests;

import protocol.messagetype.Message;
import protocol.messagetype.Request;

/**
 * Created by Yana on 05.07.15.
 */
public class LoginMSG implements Request, Message {

    private final String login;
    private final String password;

    public LoginMSG(String login, String password) {
        this.login = login;
        this.password = password;
    }


    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
