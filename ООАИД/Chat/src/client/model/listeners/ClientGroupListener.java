package client.model.listeners;

import protocol.info.UserInfo;

import java.util.List;

/**
 * Created by Yana on 14.12.15.
 */
public interface ClientGroupListener {

    void handleUpdateParticipants(String groupName, List<UserInfo> participants);

    void handleMessageFromServer(String groupName, String message);
}
