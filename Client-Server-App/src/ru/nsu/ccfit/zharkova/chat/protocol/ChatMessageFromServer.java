package ru.nsu.ccfit.zharkova.chat.protocol;

import java.awt.*;

/**
 * Created by Yana on 05.07.15.
 */
public class ChatMessageFromServer extends Message implements Event {

    private final String chatClientName;
    private final String message;
    private final Color color;

    public ChatMessageFromServer(String chatClientName, String message, Color color) {
        this.chatClientName = chatClientName;
        this.message = message;
        this.color = color;
    }

    public String getMessage(){
        return message;
    }

    public Color getColor() {
        return color;
    }

    public String getChatClientName() {
        return chatClientName;
    }
}
