package ru.nsu.ccfit.zharkova.chat.protocol;

/**
 * Created by Yana on 05.07.15.
 */
public class UserLogin extends Message implements Event {
    private final String nick;

    public UserLogin(String nick) {
        this.nick = nick;
    }

    public String getNick(){
        return nick;
    }
}
