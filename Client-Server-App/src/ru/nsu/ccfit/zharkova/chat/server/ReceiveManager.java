package ru.nsu.ccfit.zharkova.chat.server;

import com.sun.webkit.dom.RectImpl;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.nsu.ccfit.zharkova.chat.handlers.IncomingMessageHandler;
import ru.nsu.ccfit.zharkova.chat.handlers.OutgoingMessageHandler;
import ru.nsu.ccfit.zharkova.chat.protocol.*;

import javax.xml.transform.TransformerException;
import java.io.IOException;

/**
 * Created by Yana on 03.07.15.
 */
public class ReceiveManager implements Runnable {

    private final Server server;
    private final ClientConnection connection;
    private final IncomingMessageHandler inMessageHandler;
    private final OutgoingMessageHandler outMessageHandler;
    private final Logger logger = LogManager.getLogger(ReceiveManager.class);


    public ReceiveManager(IncomingMessageHandler messageHandler, OutgoingMessageHandler outMessageHandler, ClientConnection clientConnection) {
        this.server = clientConnection.getServer();
        this.connection = clientConnection;
        this.inMessageHandler = messageHandler;
        this.outMessageHandler = outMessageHandler;
    }

    @Override
    public void run() {
        try {
        while(!Thread.currentThread().isInterrupted()) {
            Message message = (Message) inMessageHandler.getObject();
            parseMessage(message);
        }
        } catch (IOException | ClassNotFoundException e) {
            logger.info("Disconnect SESSION=" + connection.getID());
            server.broadcast(new UserLogout(connection.getUsername()), connection.getID());
            connection.stop();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private void parseMessage(Message message) throws IOException, TransformerException {
        if(message instanceof LoginMSG){
            LoginMSG im = (LoginMSG) message;
            for(UserInfo username : server.getUserList()){
                if(username.getName().equals(im.getUsername())){
                    connection.addMessageToQueue(new ErrorMSG(username.getName() + " already exist"));
                    logger.info("Server ERROR <login>: " + username + " already exist");
                    connection.interruptAll();
                    return;
                }
            }
            connection.setUser(im.getUsername(), im.getChatClientName());
            connection.addMessageToQueue(new LoginSuccessesMSG(connection.getID()));
            server.broadcast(new UserLogin(im.getUsername()), connection.getID());
            server.addConnection(connection.getID(), connection);
            logger.info("Server SUCCESS <login>: USER "+ im.getUsername() + ", SESSION=" + connection.getID()+ ", CHAT_CLIENT_NAME " + connection.getUser().getChatClientName() + " login success");
            return;
        }
        if(message instanceof ListRequestMSG){
            if(((ListRequestMSG) message).getSession() != connection.getID()){
                disconnectUserWithErrorMessage("not valid session ID");
            }
            sendContactList();
            sendHistory();
            logger.info("Server send Contact List to USER " + connection.getUsername());
            return;
        }
        if(message instanceof ChatMessagesFromClient){
            ChatMessagesFromClient rm = (ChatMessagesFromClient)message;
            if(rm.getSession() != connection.getID()){
                disconnectUserWithErrorMessage("not valid session ID");
            }
            connection.addMessageToQueue(new SuccessMSG());
            ChatMessageFromServer cm = new ChatMessageFromServer(connection.getUser().getChatClientName(), connection.getUsername() + ": " + rm.getMessage(), rm.getColor());
            server.broadcast(cm, connection.getID());
            server.addMessageToHistory(cm);
            logger.info("Server <message> from USER " + connection.getUsername() + " " + cm.getMessage());
            return;
        }
        if(message instanceof LogoutMSG){
            connection.addMessageToQueue(new SuccessMSG());
            logger.info("Server <logout>: USER " + connection.getUsername() + ", SESSION=" + connection.getID() + ", CHAT_CLIENT_NAME " + connection.getUser().getChatClientName() + " logout");
            connection.stop();
            server.broadcast(new UserLogout(connection.getUsername()), connection.getID());
            return;
        }
        disconnectUserWithErrorMessage("unknown protocol");

    }

    private void disconnectUserWithErrorMessage(String reason) throws IOException, TransformerException {
        outMessageHandler.go(new ErrorMSG(reason));
        connection.stop();
    }

    private void sendContactList() throws IOException, TransformerException {
        outMessageHandler.go(new ListUsersMSG(server.getUserList()));
    }

    private void sendHistory() throws IOException, TransformerException {
        for(ChatMessageFromServer message: server.getChatHistory()){
            outMessageHandler.go(message);
        }
        logger.info("Server send history to USER " + connection.getUsername());
    }
}
