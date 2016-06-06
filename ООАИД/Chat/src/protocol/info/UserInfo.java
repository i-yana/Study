package protocol.info;

import java.io.Serializable;

/**
 * Created by Yana on 03.12.15.
 */
public class UserInfo implements Serializable {
    private int id;
    private String login;
    private int isAuthorized;

    public UserInfo(int id, String login, int isAuthorized) {
        this.id = id;
        this.login = login;
        this.isAuthorized = isAuthorized;
    }

    public String getLogin() {
        return login;
    }


    public int getId() {
        return id;
    }

    public int getIsAuthorized() {
        return isAuthorized;
    }
}
