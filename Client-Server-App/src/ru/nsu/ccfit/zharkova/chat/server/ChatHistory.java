package ru.nsu.ccfit.zharkova.chat.server;

import ru.nsu.ccfit.zharkova.chat.protocol.ChatMessageFromServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yana on 03.07.15.
 */
public class ChatHistory {

    private List<ChatMessageFromServer> history;

    public ChatHistory() {
        this.history = new ArrayList<ChatMessageFromServer>(Config.HISTORY_LENGTH);
    }

    public void addMessage(ChatMessageFromServer message){
        if (this.history.size() > Config.HISTORY_LENGTH){
            this.history.remove(0);
        }
        this.history.add(message);
    }

    public List<ChatMessageFromServer> getHistory(){
        return this.history;
    }
}
