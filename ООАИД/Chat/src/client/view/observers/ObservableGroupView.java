package client.view.observers;

import client.view.listeners.GroupViewListener;

/**
 * Created by Yana on 13.12.15.
 */
public interface ObservableGroupView {

    void notifyAboutLeaveFromGroup(String groupName);

    void notifyAboutSendMessage(String groupName, String text);

    void subscribeToChange(GroupViewListener listener);
}
