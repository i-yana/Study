package server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import protocol.requests.GroupRequestMSG;
import protocol.messagetype.Message;
import server.db.DataBaseController;
import protocol.info.Group;
import protocol.info.UserInfo;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Yana on 03.07.15.
 */
public class Server {

    private final int port = Config.PORT;
    private final HashMap<Integer, ClientConnection> connections = new LinkedHashMap<>();
    private final HashMap<String, Group> groups = new LinkedHashMap<>();
    private final Object connectionSyncObj = new Object();
    private final Logger logger = LogManager.getLogger(Server.class);
    private final DataBaseController dbController = new DataBaseController();

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
    public void start()  {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            logger.info("Server work");
            while (!Thread.currentThread().isInterrupted()){
                Socket socket = serverSocket.accept();
                logger.info("Connected new socket");
                new ClientConnection(socket, this, dbController);
            }
        } catch (ParserConfigurationException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
    }

    public List<ClientConnection> getConnections(){
        synchronized (connectionSyncObj) {
            return new ArrayList<>(connections.values());
        }
    }

    public void addConnection(Integer ID, ClientConnection clientConnection){
        synchronized (connectionSyncObj) {
            connections.put(ID,clientConnection);
        }
    }

    public void removeConnection(Integer ID){
        synchronized (connectionSyncObj) {
            connections.remove(ID);
        }
    }

    public void broadcast(Message msg, Integer id){
        for (ClientConnection c : getConnections()) {
            if(!c.getID().equals(id)) {
                c.addMessageToQueue(msg);
            }
        }
    }


    /*public List<UserInfo> getUserList() {
        List<UserInfo> contacts = new ArrayList<UserInfo>();
        synchronized (connectionSyncObj){
            for(ClientConnection c: connections.values()){
                contacts.add(c.getUser());
            }
            return contacts;
        }
    }*/
    public List<Integer> getConnectionsID(){
        synchronized (connectionSyncObj){
            return new ArrayList<>(connections.keySet());
        }
    }

    public void broadcast(List<UserInfo> friends, Message msg) { //оповещение друзей о том что мы в сети
        synchronized (connectionSyncObj) {                      //FIXME: можно попробовать сделать просто лист интов
            for (UserInfo userInfo : friends) {
                ClientConnection cc = connections.get(userInfo.getId());
                System.out.println("я отправляю точно");
                if (cc != null) {
                    System.out.println(userInfo.getLogin());
                    cc.addMessageToQueue(msg);
                }
            }
        }
    }

    public void addGroup(Group group){
        synchronized (groups){
            groups.put(group.getGroupName(), group);
            if(group.isPublic()){
                broadcast(new GroupRequestMSG(group.getGroupName(), group.isPublic(), group.getParticipants(), GroupRequestMSG.Type.CREATE), 0);
            }
        }
    }//когда приходит сообщение о создании такое группы надо отобразить в гуи

    public void deleteGroup(String groupName){
        synchronized (groups) {
            Group group = groups.get(groupName);
            if (group.isPublic()) {
                broadcast(new GroupRequestMSG(group.getGroupName(), group.isPublic(), group.getParticipants(), GroupRequestMSG.Type.DELETE), 0);
            }
            groups.remove(groupName);
        }
    }//аналогично удалить из гуи

    public List<Group> getGroupList(){
        List<Group> grs = new ArrayList<>();
        synchronized (groups){
            for(Group g: groups.values()){
                if(g.isPublic()) {
                    grs.add(g);
                }
            }
            return grs;
        }
    }

    public void sendGroupMessage(String groupID, Message msg) {
        synchronized (groups) {
            Group group = groups.get(groupID);
            broadcast(group.getParticipants(), msg);
        }
    }

    public void leaveFromGroup(String groupName, int ID){
        synchronized (groups) {
            Group group = groups.get(groupName);
            group.remove(ID);
            broadcast(getAllParticipants(groupName), new GroupRequestMSG(groupName, group.isPublic(), group.getParticipants(), GroupRequestMSG.Type.KICK));

        }
    }//кто-то покинул группу, обновить список в гуи

    public void addParticipant(String groupName, UserInfo userInfo) {
        synchronized (groups){
            Group group = groups.get(groupName);
            group.add(userInfo);
            broadcast(getAllParticipants(groupName), new GroupRequestMSG(groupName, group.isPublic(), group.getParticipants(), GroupRequestMSG.Type.JOIN));
        }
    }//обновить список в гуи


    public List<UserInfo> getAllParticipants(String groupName){
        synchronized (groups){
            Group group = groups.get(groupName);
            return group.getParticipants();
        }
    }

    public void broadcast(Integer id, Message msg) {
        ClientConnection cc = connections.get(id);
        if (cc != null) {
            cc.addMessageToQueue(msg);
        }
    }
}
