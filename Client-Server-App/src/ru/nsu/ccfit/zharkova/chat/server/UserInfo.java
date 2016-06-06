package ru.nsu.ccfit.zharkova.chat.server;

import java.io.Serializable;

/**
 * Created by Yana on 05.07.15.
 */
public class UserInfo implements Serializable {
    private final String name;
    private final String chatClientName;

    public UserInfo(String name, String chatClientName){
        this.name = name;
        this.chatClientName = chatClientName;
    }

    public String getName() {
        return name;
    }

    public String getChatClientName() {
        return chatClientName;
    }
}
