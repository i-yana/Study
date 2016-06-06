package client.model.observers;

import client.model.listeners.ClientMenuListener;
import protocol.info.Group;
import protocol.info.UserInfo;

import java.util.List;

/**
 * Created by Yana on 14.12.15.
 */
public interface ObservableMenuClient {

    void subscribeToChanging(ClientMenuListener listener);
    void notifyAboutAnswerOnRequestGroupList(List<Group> groups);
    void notifyAboutSuccessCreateGroup(String info);

    void notifyAboutMessageFromServer(String groupName, String message);

    void notifyAboutAnswerOnRequestUserList(List<UserInfo> contacts);

    void notifyAboutSuccessLogout();

    void notifyAboutMessageSuccessAnswer();

    void notifyAboutNewGroup(String groupName, boolean groupType);

    void notifyAboutDeleteGroup(String groupName);

    void notifyAboutSuccessJoin(String info);
}
