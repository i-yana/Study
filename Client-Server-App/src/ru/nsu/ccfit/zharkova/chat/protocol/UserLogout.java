package ru.nsu.ccfit.zharkova.chat.protocol;

/**
 * Created by Yana on 05.07.15.
 */
public class UserLogout extends Message implements Event{

    private final String nick;

    public UserLogout(String nick) {
        this.nick = nick;
    }

    public String getNick(){
        return nick;
    }
}
