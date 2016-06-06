package ru.nsu.ccfit.zharkova.chat.server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.nsu.ccfit.zharkova.chat.protocol.ChatMessageFromServer;
import ru.nsu.ccfit.zharkova.chat.protocol.Message;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by Yana on 03.07.15.
 */
public class Server {

    private static Integer uniqueSessionID=0;
    private final int port = Config.PORT;
    private final boolean isXML = Config.XML_MODE;
    private final HashMap<Integer, ClientConnection> connections = new LinkedHashMap<Integer, ClientConnection>();
    private final ChatHistory chatHistory = new ChatHistory();
    private final Object connectionSyncObj = new Object();
    private final Object historySyncObj = new Object();
    private final Logger logger = LogManager.getLogger(Server.class);

    public void start()  {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            logger.info("Server work");
            while (!Thread.currentThread().isInterrupted()){
                Socket socket = serverSocket.accept();
                logger.info("Connected new socket");
                uniqueSessionID++;
                new ClientConnection(uniqueSessionID, socket, this);
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
            return new ArrayList<ClientConnection>(connections.values());
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

    public void addMessageToHistory(ChatMessageFromServer message){
        synchronized (historySyncObj) {
            chatHistory.addMessage(message);
        }
    }

    public List<ChatMessageFromServer> getChatHistory(){
        synchronized (historySyncObj) {
            return new ArrayList<ChatMessageFromServer>(chatHistory.getHistory());
        }
    }

    public boolean isXML() {
        return isXML;
    }


    public List<UserInfo> getUserList() {
        List<UserInfo> contacts = new ArrayList<UserInfo>();
        synchronized (connectionSyncObj){
            for(ClientConnection c: connections.values()){
                contacts.add(c.getUser());
            }
            return contacts;
        }
    }
}
