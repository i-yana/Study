package protocol.info;

import java.io.Serializable;

/**
 * Created by Yana on 03.12.15.
 */
public class RegisterINFO implements Serializable {

    private final String login;
    private final String password;

    public RegisterINFO(String login, String password){
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
