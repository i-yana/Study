package ru.nsu.ccfit.zharkova.chat.protocol;

import java.awt.*;

/**
 * Created by Yana on 28.07.15.
 */
public class ChatMessagesFromClient extends Message implements Request{

    private final String message;
    private final int session;
    private final Color color;

    public ChatMessagesFromClient(String message, int session, Color color){
        this.message = message;
        this.session = session;
        this.color = color;
    }

    public String getMessage() {
        return message;
    }

    public int getSession() {
        return session;
    }

    public Color getColor() {
        return color;
    }
}
