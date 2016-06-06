package client.view.listeners;

import java.awt.*;

/**
 * Created by Yana on 13.12.15.
 */
public interface GroupViewListener {

    void handleLeave(String groupName);

    void handleMessageFromClient(String message, String groupName, Color color);


    void handleAddFriend(Integer ID);
}
