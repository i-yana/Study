package ru.nsu.ccfit.zharkova.chat.protocol;

/**
 * Created by Yana on 05.07.15.
 */
public class LoginMSG extends Message implements Request{

    private final String username;
    private final String chatClientName;

    public LoginMSG(String username, String chatClientName) {
        this.username = username;
        this.chatClientName = chatClientName;
    }

    public String getUsername() {
        return username;
    }

    public String getChatClientName() {
        return chatClientName;
    }
}
