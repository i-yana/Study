package client.controller;

import client.model.Client;
import client.view.menu.MenuView;
import client.view.listeners.MenuViewListener;

import javax.swing.*;

/**
 * Created by Yana on 03.12.15.
 */
public class MenuController extends JFrame implements MenuViewListener {

    private MenuView menuView;
    private Client client;

    public MenuController(Client client){
        super("Menu");
        this.client = client;
        menuView = new MenuView();
        menuView.subscribeToChanging(this);
        this.client.subscribeToChanging(menuView);
        menuView.setVisible(true);
        handleGroupRequest();
    }

    @Override
    public void handleLogout() {
        client.handleLogout();
    }

    @Override
    public void handleGroupRequest(){
        client.getGroupList();
    }

    @Override
    public void handleCreateGroup(String groupName, boolean isPublic) {
        client.createGroup(groupName, isPublic);
    }

    @Override
    public void handleJoin(String groupName, boolean isPublic) {
        client.joinToGroup(groupName, isPublic);
    }

    @Override
    public void handleSearch(String userLogin) {
        client.searchUser(userLogin);
    }

    @Override
    public void addFriend(Integer ID) {
        client.addFriend(ID);
    }

    @Override
    public void removeFriend(Integer ID) {
        client.removeFriend(ID);
    }

    @Override
    public void handleOpenGroupWindow(String groupName, boolean isPublic, JTabbedPane tabbedPane) {
        GroupController controller = new GroupController(groupName, isPublic, client, tabbedPane);
        menuView.addGroupToPanel(groupName, controller.getView());
    }


}
