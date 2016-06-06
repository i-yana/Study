package client.model.listeners;

import protocol.info.Group;

import java.util.List;

/**
 * Created by Yana on 14.12.15.
 */
public interface ClientMenuListener {
    void handleAnswerOnRequestGroupList(List<Group> groups);

    void handleSuccessAnswerOnCreateGroup(String info);

    void handleLeave(String info);

    void handleAddGroupToList(String groupName, boolean groupType);

    void handleRemoveGroup(String groupName);

    void handleSuccessAnswerJoin(String groupName);
}
