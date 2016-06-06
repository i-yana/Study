package server;

import handlers.handlersInterface.IncomingMessageHandler;
import handlers.handlersInterface.OutgoingMessageHandler;
import handlers.serializedMessageHandler.IncomingSerializedObject;
import handlers.serializedMessageHandler.OutgoingSerializedObject;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import protocol.messagetype.Message;
import server.db.DataBaseController;
import server.io.ReceiveManager;
import server.io.SendManager;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yana on 03.07.15.
 */
public class ClientConnection {

    private int uniqueID;
    private final Socket socket;
    private final Server server;
    private final ObjectInputStream sInput;
    private final ObjectOutputStream sOutput;
    private final List<Message> messageList;
    private Thread receiveManager;
    private Thread sendManager;
    //private UserInfo user;
    private final Object queueSyncObj = new Object();
    private final Logger logger = LogManager.getLogger(ClientConnection.class);

    public ClientConnection(Socket socket, Server server, DataBaseController dbController) throws IOException, ParserConfigurationException {
        this.socket = socket;
        this.server = server;
        logger.info("trying to connect ...");
        sOutput = new ObjectOutputStream(socket.getOutputStream());
        sInput = new ObjectInputStream(socket.getInputStream());
        logger.info("Connection successfully established");


        IncomingMessageHandler incomingMessageHandler = new IncomingSerializedObject(sInput);
        OutgoingMessageHandler outgoingMessageHandler = new OutgoingSerializedObject(sOutput);

        messageList = new ArrayList<>();
        receiveManager = new Thread(new ReceiveManager(incomingMessageHandler, outgoingMessageHandler, this, server, dbController));
        sendManager = new Thread(new SendManager(messageList, queueSyncObj ,outgoingMessageHandler, this));
        receiveManager.start();
        sendManager.start();
    }

    /*public void setUser(int ID, String login){
        user = new UserInfo(ID, login, 1);
    }

    public String getUsername(){
        return user.getLogin();
    }

    public UserInfo getUser(){
        return user;
    }
*/
    public void stop() {
        server.removeConnection(uniqueID);
        interruptAll();
    }

    public void addMessageToQueue(Message msg) {
        synchronized (queueSyncObj) {
            messageList.add(msg);
            queueSyncObj.notify();
        }
    }

    public void interruptAll() {
        sendManager.interrupt();
        receiveManager.interrupt();
    }

    public void setUniqueID(int ID){
        uniqueID = ID;
    }
    public Integer getID() {
        return uniqueID;
    }
}
