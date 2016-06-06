package client.view.listeners;

import javax.swing.*;

/**
 * Created by Yana on 03.12.15.
 */
public interface MenuViewListener {

    void handleLogout();

    void handleGroupRequest();

    public void handleCreateGroup(String groupName, boolean isPublic);

    public void handleJoin(String groupName, boolean isPublic);

    public void handleSearch(String userLogin);

    public void addFriend(Integer ID);

    public void removeFriend(Integer ID);

    void handleOpenGroupWindow(String groupName, boolean isPublic, JTabbedPane tabbedPane);
}
