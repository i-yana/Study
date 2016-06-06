package ru.nsu.ccfit.zharkova.chat.server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.nsu.ccfit.zharkova.chat.handlers.*;
import ru.nsu.ccfit.zharkova.chat.handlers.SerializedObject.IncomingSerializedObject;
import ru.nsu.ccfit.zharkova.chat.handlers.SerializedObject.OutgoingSerializedObject;
import ru.nsu.ccfit.zharkova.chat.handlers.ServerXML.IncomingXMLServer;
import ru.nsu.ccfit.zharkova.chat.handlers.ServerXML.OutgoingXMLServer;
import ru.nsu.ccfit.zharkova.chat.protocol.Message;

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

    private final Integer uniqueSessionID;
    private final Socket socket;
    private final Server server;
    private final ObjectInputStream sInput;
    private final ObjectOutputStream sOutput;
    private final List<Message> messageList;
    private Thread receiveManager;
    private Thread sendManager;
    private UserInfo user;
    private final Object queueSyncObj = new Object();
    private final Logger logger = LogManager.getLogger(ClientConnection.class);

    public ClientConnection(Integer uniqueSessionID, Socket socket, Server server) throws IOException, ParserConfigurationException {
        this.uniqueSessionID = uniqueSessionID;
        this.socket = socket;
        this.server = server;
        logger.info("trying to connect ...");
        sOutput = new ObjectOutputStream(socket.getOutputStream());
        sInput = new ObjectInputStream(socket.getInputStream());
        logger.info("Connection successfully established");
        IncomingMessageHandler incomingMessageHandler;
        OutgoingMessageHandler outgoingMessageHandler;
        if(server.isXML()){
            incomingMessageHandler = new IncomingXMLServer(sInput);
            outgoingMessageHandler = new OutgoingXMLServer(sOutput);
        }
        else {
            incomingMessageHandler = new IncomingSerializedObject(sInput);
            outgoingMessageHandler = new OutgoingSerializedObject(sOutput);
        }
        messageList = new ArrayList<Message>();
        receiveManager = new Thread(new ReceiveManager(incomingMessageHandler, outgoingMessageHandler, this));
        sendManager = new Thread(new SendManager(messageList, queueSyncObj ,outgoingMessageHandler, this));
        receiveManager.start();
        sendManager.start();
    }

    public void setUser(String name, String chatClientName){
        user = new UserInfo(name, chatClientName);
    }

    public String getUsername(){
        return user.getName();
    }

    public UserInfo getUser(){
        return user;
    }

    public Server getServer() {
        return server;
    }

    public void stop() {
        server.removeConnection(uniqueSessionID);
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

    public Integer getID() {
        return uniqueSessionID;
    }
}
