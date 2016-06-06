package client.model.observers;

import client.model.listeners.ClientGroupListener;
import protocol.info.UserInfo;

import java.util.List;

/**
 * Created by Yana on 14.12.15.
 */
public interface ObservableGroupClient {
    void subscribeToChanging(ClientGroupListener listener);

    void notifyAboutLeave(String info);

    void notifyGroupForUpdateParticipants(String groupName, List<UserInfo> participants);
}
