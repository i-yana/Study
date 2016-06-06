package client.view.observers;

import client.view.listeners.MenuViewListener;

/**
 * Created by Yana on 03.12.15.
 */
public interface ObservableMenuView {

    void notifyAboutJoinToGroupRequest(String groupName);

    void subscribeToChanging(MenuViewListener listener);

    void notifyAboutLogoutCommand();

    void notifyAboutCreateGroup(String text, String text1);

    void notifyAboutOpenGroupWindow(String groupName, boolean isPublic);
}
