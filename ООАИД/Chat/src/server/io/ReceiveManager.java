package server.io;

import handlers.handlersInterface.IncomingMessageHandler;
import handlers.handlersInterface.OutgoingMessageHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import protocol.answers.*;
import protocol.events.ChatMessageFromServer;
import protocol.events.FriendMSG;
import protocol.events.UserLogin;
import protocol.events.UserLogout;
import protocol.requests.*;
import protocol.messagetype.Message;
import server.ClientConnection;
import server.Server;
import server.db.DataBaseController;
import protocol.info.Group;
import protocol.info.UserInfo;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yana on 03.07.15.
 */
public class ReceiveManager implements Runnable {
    private final Server server;
    private final ClientConnection connection;
    private final IncomingMessageHandler inMessageHandler;
    private final OutgoingMessageHandler outMessageHandler;
    private final Logger logger = LogManager.getLogger(ReceiveManager.class);
    private final DataBaseController dbController;
    private Integer ID = null;

    public ReceiveManager(IncomingMessageHandler messageHandler, OutgoingMessageHandler outMessageHandler, ClientConnection clientConnection, Server server, DataBaseController dbController) {
        this.server = server;
        this.connection = clientConnection;
        this.inMessageHandler = messageHandler;
        this.outMessageHandler = outMessageHandler;
        this.dbController = dbController;
    }

    @Override
    public void run() {
        try {
            while(!Thread.currentThread().isInterrupted()) {
                Message message = (Message) inMessageHandler.getObject();
                System.out.println("Receive message TYPE " + message.getClass().getSimpleName());
                parseMessage(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.info("Disconnect SESSION=" + connection.getID());
            sendLogout();
            connection.stop();
            if(ID!=0){
                dbController.setAuthorized(ID, 0);
            }
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private void sendLogout() {
        List<UserInfo> friends = dbController.getFriendList(connection.getID());
        server.broadcast(friends, new UserLogout(connection.getID()));
        for(Group group: server.getGroupList())
        {
            System.out.println(group.getGroupName());
            if(group.contains(ID)){
                server.leaveFromGroup(group.getGroupName(), ID);
            }
            if(server.getAllParticipants(group.getGroupName()).isEmpty()){
                server.deleteGroup(group.getGroupName());
            }
        }

    }

    private void parseMessage(Message message) throws IOException, TransformerException {
        if(message instanceof RegisterMSG){
            RegisterMSG registerMSG = (RegisterMSG) message;
            if (dbController.exist(registerMSG.getLogin())) {
                connection.addMessageToQueue(new ErrorMSG("User with login " + registerMSG.getLogin() + " already exist"));
                connection.interruptAll();
                return;
            }
            dbController.add(registerMSG.getLogin(), registerMSG.getPassword());
            ID = dbController.getID(registerMSG.getLogin());
            connection.setUniqueID(ID);
            connection.addMessageToQueue(new LoginSuccessMSG(ID));
            server.addConnection(ID, connection);
            logger.info("Server SUCCESS <Register>: USER " + registerMSG.getLogin() + ", SESSION=" + connection.getID() + "  register success");
            return;
        }
        if(message instanceof LoginMSG){
            LoginMSG loginMSG = (LoginMSG) message;
            if(dbController.isValid(loginMSG.getLogin(), loginMSG.getPassword())) {
                ID = dbController.getID(loginMSG.getLogin());
                for (Integer otherID : server.getConnectionsID()) {
                    if (ID.equals(otherID)) {
                        connection.addMessageToQueue(new ErrorMSG("already connected"));
                        logger.info("Server ERROR <login>: " + loginMSG.getLogin() + " already connected");
                        connection.interruptAll();
                        return;
                    }
                }
                List<UserInfo> friends = new ArrayList<>(dbController.getFriendList(ID));//получаем из бд друзей
                dbController.setAuthorized(ID, 1);
                connection.setUniqueID(ID);
                server.broadcast(friends, new UserLogin(ID, loginMSG.getLogin()));//оповещаем друзей что чувак в сети
                connection.addMessageToQueue(new LoginSuccessMSG(ID));
                server.addConnection(ID, connection);
                logger.info("Server SUCCESS <login>: USER " + loginMSG.getLogin() + ", SESSION=" + connection.getID() + "  login success");
                return;
            }
            connection.addMessageToQueue(new ErrorMSG("Incorrect login or password"));
            connection.interruptAll();
            return;
        }
        if(message instanceof ListRequestMSG){
            ListRequestMSG rm = (ListRequestMSG) message;
            if(rm.getType()== ListRequestMSG.Type.GROUPS){
                connection.addMessageToQueue(new ListGroupMSG(server.getGroupList()));
                System.out.println("send GROPS");
                logger.info("Server send Group List to USER " + connection.getID());
                return;
            }
            if(rm.getType()== ListRequestMSG.Type.FRIENDS) {
                sendContactList();
                logger.info("Server send Contact List to USER " + connection.getID());

            }
            return;
        }
        if(message instanceof ChatMessageFromClient){
            ChatMessageFromClient rm = (ChatMessageFromClient)message;
            connection.addMessageToQueue(new SuccessMSG("message",""));
            ChatMessageFromServer chatMessageFromServer = new ChatMessageFromServer(rm.getGroupName(), rm.getNick()+"> " + rm.getMessage(), rm.getColor());
            server.sendGroupMessage(rm.getGroupName(), chatMessageFromServer);
            logger.info("Server <message> from USER " + connection.getID() + " " + rm.getMessage());
            return;
        }
        if(message instanceof LogoutMSG){
            connection.addMessageToQueue(new SuccessMSG("logout",""));
            logger.info("Server <logout>: USER " + connection.getID() + " logout");
            for(Group group: server.getGroupList())
            {
                System.out.println(group.getGroupName());
                if(group.contains(ID)){
                    server.leaveFromGroup(group.getGroupName(), ID);
                }
                if(server.getAllParticipants(group.getGroupName()).isEmpty()){
                    server.deleteGroup(group.getGroupName());
                }
            }
            connection.stop();
            return;
        }
        if(message instanceof SearchUserMSG){
            SearchUserMSG sm = (SearchUserMSG) message;
            int user_id = dbController.getID(sm.getLogin());
            connection.addMessageToQueue(new SearchUserMSG(user_id));
            return;
        }
        if(message instanceof GroupRequestMSG){
            GroupRequestMSG cm = (GroupRequestMSG) message;
            if(cm.getType() == GroupRequestMSG.Type.CREATE){
                System.out.println("CREATE");
                server.addGroup(new Group(cm.getGroupName(), cm.isPublic()));
                server.addParticipant(cm.getGroupName(), dbController.getUserInfo(ID));
                connection.addMessageToQueue(new SuccessMSG(cm.getGroupName(),"create"));
                return;
            }
            if(cm.getType() == GroupRequestMSG.Type.DELETE){
                server.deleteGroup(cm.getGroupName());
                connection.addMessageToQueue(new SuccessMSG(cm.getGroupName(), "delete"));
                return;
            }
            if(cm.getType() == GroupRequestMSG.Type.JOIN){
                System.out.println("JOIN");
                String groupName = cm.getGroupName();
                server.addParticipant(groupName, dbController.getUserInfo(ID));
                connection.addMessageToQueue(new SuccessMSG(groupName, "join"));
                return;
            }
            if(cm.getType() == GroupRequestMSG.Type.KICK){
                System.out.println("KICK");
                String groupName = cm.getGroupName();
                server.leaveFromGroup(groupName, ID);
                connection.addMessageToQueue(new SuccessMSG(cm.getGroupName(), "kick"));
                if(server.getAllParticipants(groupName).isEmpty()){
                    server.deleteGroup(groupName);
                }
                return;
            }
            if(cm.getType() == GroupRequestMSG.Type.PARTICIPANTS){
                System.out.println("PARTICIPANTS");
                String groupName = cm.getGroupName();
                for(UserInfo u : server.getAllParticipants(groupName)){
                    System.out.println(u.getLogin());
                }
                connection.addMessageToQueue(new GroupParticipantsMSG(groupName, server.getAllParticipants(groupName)));
                System.out.println("send user in group");
                return;
            }
        }
        if(message instanceof FriendMSG){
            FriendMSG friendMSG = (FriendMSG) message;
            if(friendMSG.getType()== FriendMSG.Type.REQEST) {
                if(dbController.isFriends(ID, friendMSG.getFriendID())){
                    connection.addMessageToQueue(new ErrorMSG("already friends"));
                    return;
                }
                dbController.addFriend(ID, friendMSG.getFriendID());
                connection.addMessageToQueue(new SuccessMSG("","request"));
                server.broadcast(friendMSG.getFriendID(), new FriendMSG(ID, FriendMSG.Type.REQEST));
            }
            if(friendMSG.getType()== FriendMSG.Type.ACCEPT) {
                dbController.acceptFriend(ID, friendMSG.getFriendID());
                connection.addMessageToQueue(new SuccessMSG("","accept"));
                server.broadcast(ID, new FriendMSG(friendMSG.getFriendID(), FriendMSG.Type.ACCEPT));
                server.broadcast(friendMSG.getFriendID(), new FriendMSG(ID, FriendMSG.Type.ACCEPT));
            }
            return;
        }
        disconnectUserWithErrorMessage("unknown protocol");

    }

    private void disconnectUserWithErrorMessage(String reason) throws IOException, TransformerException {
        System.out.println(reason);
        outMessageHandler.go(new ErrorMSG(reason));
        connection.stop();
    }

    private void sendContactList() throws IOException, TransformerException {
        List<UserInfo> friends = dbController.getFriendList(connection.getID());
        outMessageHandler.go(new ListUsersMSG(friends));
    }
}
