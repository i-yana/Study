package ru.nsu.ccfit.zharkova.chat.protocol;

import ru.nsu.ccfit.zharkova.chat.server.UserInfo;

import java.util.List;

/**
 * Created by Yana on 05.07.15.
 */
public class ListUsersMSG extends Message implements ServerAnswer{
    private final List<UserInfo> contacts;

    public ListUsersMSG(List<UserInfo> contacts) {
        this.contacts = contacts;
    }

    public List<UserInfo> getContacts(){
        return contacts;
    }
}
