package ru.nsu.ccfit.zharkova.chat.client;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.nsu.ccfit.zharkova.chat.handlers.*;
import ru.nsu.ccfit.zharkova.chat.handlers.ClientXML.IncomingXMLClient;
import ru.nsu.ccfit.zharkova.chat.handlers.ClientXML.OutgoingXMLClient;
import ru.nsu.ccfit.zharkova.chat.handlers.SerializedObject.IncomingSerializedObject;
import ru.nsu.ccfit.zharkova.chat.handlers.SerializedObject.OutgoingSerializedObject;
import ru.nsu.ccfit.zharkova.chat.protocol.LoginMSG;
import ru.nsu.ccfit.zharkova.chat.protocol.Message;
import ru.nsu.ccfit.zharkova.chat.protocol.Request;

import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yana on 07.07.15.
 */

public class Connection {

    private static final int CONNECTION_TIMEOUT = 7000;
    private static final int SERVER_TIMEOUT = 60000;
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private IncomingMessageHandler incomingMessageHandler;
    private OutgoingMessageHandler outgoingMessageHandler;
    private Socket socket = new Socket();
    private final Controller controller;
    private String server, username;
    private int port;
    private ListenFromServer listenFromServer;
    private SendingMessageHandler sendingMessageHandler;
    private int session;
    private final Object syncObj = new Object();
    private final Logger logger = LogManager.getLogger(Connection.class);

    Connection(String server, int port, String username, Controller controller) throws IOException, BadLocationException, ParserConfigurationException {
        this.server = server;
        this.port = port;
        this.username = username;
        this.controller = controller;
        start();
    }

    public void start() throws BadLocationException, IOException, ParserConfigurationException {
        socket.connect(new InetSocketAddress(server, port), CONNECTION_TIMEOUT);
        socket.setSoTimeout(SERVER_TIMEOUT);
        logger.info("trying to connect ...");
        sOutput = new ObjectOutputStream(socket.getOutputStream());
        sInput = new ObjectInputStream(socket.getInputStream());
        logger.info("Connection successfully established");
        if (ClientConfig.XML_MODE) {
            incomingMessageHandler = new IncomingXMLClient(sInput);
            outgoingMessageHandler = new OutgoingXMLClient(sOutput);
        } else {
            incomingMessageHandler = new IncomingSerializedObject(sInput);
            outgoingMessageHandler = new OutgoingSerializedObject(sOutput);
        }
        List<Request> requestList = new ArrayList<>();
        sendingMessageHandler = new SendingMessageHandler(outgoingMessageHandler, syncObj, requestList);
        listenFromServer = new ListenFromServer(incomingMessageHandler, controller , sendingMessageHandler, syncObj, requestList);
        sendingMessageHandler.start();
        listenFromServer.start();
        sendingMessageHandler.addMessage(new LoginMSG(username, ChatFrame.CHAT_NAME));
    }

    void sendMessage(Message msg) throws BadLocationException, IOException {
        sendingMessageHandler.addMessage(msg);
    }

    public void disconnect() {
        try {
            listenFromServer.interrupt();
            sendingMessageHandler.interrupt();
            socket.close();
            logger.info("Disconnect");
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
    }
    public void setSession(int id){
        session = id;
    }

    public int getSession(){
        return session;
    }

    public String getUsername(){
        return username;
    }
}