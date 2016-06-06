package client.model;

import client.Config;
import client.model.listeners.ClientAuthorizeListener;
import client.model.listeners.ClientGroupListener;
import client.model.listeners.ClientMenuListener;
import client.model.observers.ObservableAuthorizeClient;
import client.model.observers.ObservableGroupClient;
import client.model.observers.ObservableMenuClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import protocol.answers.SearchUserMSG;
import protocol.events.FriendMSG;
import protocol.info.Group;
import protocol.info.RegisterINFO;
import protocol.info.UserInfo;
import protocol.requests.*;

import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yana on 03.12.15.
 */
public class Client implements ObservableAuthorizeClient, ObservableMenuClient, ObservableGroupClient {

    public static final String WRONG_PORT_OR_HOST = "wrong port or host";
    public static final String THERE_IS_NO_SUCH_HOST = "There is no such host";
    private String host = Config.HOST;
    private int port = Config.PORT;
    private List<ClientAuthorizeListener> authorizeListeners = new ArrayList<>();
    private List<ClientMenuListener> menuListeners = new ArrayList<>();
    private List<ClientGroupListener> groupListeners = new ArrayList<>();
    private String login;
    private Connection connection;
    private static final Logger logger = LogManager.getLogger(Client.class.getSimpleName());

    public Client(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void authorization(String userName, String password){
        this.login = userName;
        connect();
        connection.putMessage(new LoginMSG(userName, password));
    }

    public void registration(String login, String password){
        this.login = login;
        connect();
        connection.putMessage(new RegisterMSG(new RegisterINFO(login, password)));
    }

    private void connect(){
        InetAddress ipAddress = null;
        try {
            ipAddress = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            notifyAboutAnswerAboutError(THERE_IS_NO_SUCH_HOST);
        }

        Socket socket = null;
        try {
            socket = new Socket(ipAddress, port);
        } catch (IOException e) {
            notifyAboutAnswerAboutError(WRONG_PORT_OR_HOST);
            return;
        }

        try {
            connection = new Connection(socket,this);
            connection.startConnection();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void handleLogout() {
        connection.putMessage(new LogoutMSG());
    }

    public void createGroup(String groupName, boolean type) {
        connection.putMessage(new GroupRequestMSG(groupName, type, GroupRequestMSG.Type.CREATE));
    }

    public void joinToGroup(String groupName, boolean type) {
        connection.putMessage(new GroupRequestMSG(groupName, type, GroupRequestMSG.Type.JOIN));
    }

    public void leaveFromGroup(String groupName, boolean type) {
        connection.putMessage(new GroupRequestMSG(groupName, type, GroupRequestMSG.Type.KICK));
    }

    public void searchUser(String userLogin) {
        connection.putMessage(new SearchUserMSG(userLogin));
    }

    public void addFriend(Integer ID) {
        connection.putMessage(new FriendMSG(ID, FriendMSG.Type.REQEST));
    }

    public void removeFriend(Integer ID) {
        connection.putMessage(new FriendMSG(ID, FriendMSG.Type.REMOVE));
    }

    public void handleMessageFromClientCommand(String message, String groupName, Color color) {
        connection.putMessage(new ChatMessageFromClient(login,message,groupName,color));
    }


    public void getGroupList() {
        connection.putMessage(new ListRequestMSG(ListRequestMSG.Type.GROUPS));
    }

    @Override
    public void subscribeToChanging(ClientAuthorizeListener listener) {
        authorizeListeners.add(listener);
    }

    @Override
    public void notifyAboutAnswerAboutCompleteRegistration() {
        for(ClientAuthorizeListener listener: authorizeListeners){
            listener.handleCompleteRegistration();
        }
    }

    @Override
    public void notifyAboutAnswerAboutError(String reason) {
        for (ClientAuthorizeListener listener : authorizeListeners) {
            listener.handleAnswerAboutError(reason);
        }
    }

    @Override
    public void notifyAboutAnswerAboutCompleteAuthorization() {
        for(ClientAuthorizeListener listener: authorizeListeners){
            listener.handleCompleteAuthorization();
        }
    }


    @Override
    public void notifyAboutAnswerOnRequestGroupList(List<Group> groups) {
        for(ClientMenuListener listener:menuListeners){
            listener.handleAnswerOnRequestGroupList(groups);
        }
    }

    @Override
    public void notifyAboutSuccessCreateGroup(String info) {
        for(ClientMenuListener listener:menuListeners){
            listener.handleSuccessAnswerOnCreateGroup(info);
        }
    }

    @Override
    public void notifyAboutMessageFromServer(String groupName, String message) {
        for(ClientGroupListener clientGroupListener:groupListeners){
            clientGroupListener.handleMessageFromServer(groupName, message);
        }
    }

    @Override
    public void notifyAboutAnswerOnRequestUserList(List<UserInfo> contacts) {

    }

    @Override
    public void notifyAboutSuccessLogout() {
        System.exit(0);
    }

    @Override
    public void notifyAboutMessageSuccessAnswer() {
        System.out.println("message on server");
    }

    @Override
    public void notifyAboutNewGroup(String groupName, boolean groupType) {
        for(ClientMenuListener listener:menuListeners){
            listener.handleAddGroupToList(groupName, groupType);
        }
    }

    @Override
    public void notifyAboutDeleteGroup(String groupName) {
        for(ClientMenuListener listener:menuListeners){
            listener.handleRemoveGroup(groupName);
        }
    }

    @Override
    public void notifyAboutSuccessJoin(String groupName) {
        for(ClientMenuListener listener:menuListeners){
            listener.handleSuccessAnswerJoin(groupName);
        }
    }


    @Override
    public void subscribeToChanging(ClientMenuListener listener) {
        menuListeners.add(listener);
    }

    @Override
    public void subscribeToChanging(ClientGroupListener listener) {
        groupListeners.add(listener);
    }

    @Override
    public void notifyAboutLeave(String info) {
        for(ClientMenuListener clientMenuListener: menuListeners){
            clientMenuListener.handleLeave(info);
        }
    }

    @Override
    public void notifyGroupForUpdateParticipants(String groupName, List<UserInfo> participants) {
        for(ClientGroupListener groupListener: groupListeners){
            groupListener.handleUpdateParticipants(groupName, participants);
        }
    }

    public void getGroupParticipant(String groupName) {
        connection.putMessage(new GroupRequestMSG(groupName, true, GroupRequestMSG.Type.PARTICIPANTS));
    }
}
