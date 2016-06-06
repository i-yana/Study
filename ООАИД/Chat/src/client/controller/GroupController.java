package client.controller;

import client.model.Client;
import client.view.groups.GroupView;
import client.view.listeners.GroupViewListener;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Yana on 13.12.15.
 */
public class GroupController implements GroupViewListener{
    private Client client;
    private String groupName;
    private boolean isPublic;
    private GroupView groupView;

    public GroupController(String groupName, boolean isPublic, Client client, JTabbedPane tabbedPane){
        this.groupName = groupName;
        groupView = new GroupView(groupName, tabbedPane);
        groupView.subscribeToChange(this);
        this.client = client;
        this.isPublic = isPublic;
        this.client.subscribeToChanging(groupView);
        client.getGroupParticipant(groupName);
    }



    @Override
    public void handleLeave(String groupName) {
        client.leaveFromGroup(groupName, isPublic);
    }

    @Override
    public void handleMessageFromClient(String message, String groupName, Color color) {
        client.handleMessageFromClientCommand(message, groupName, color);
    }


    @Override
    public void handleAddFriend(Integer ID) {
        client.addFriend(ID);
    }

    public GroupView getView() {
        return groupView;
    }
}
