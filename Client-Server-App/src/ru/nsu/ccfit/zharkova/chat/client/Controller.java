package ru.nsu.ccfit.zharkova.chat.client;

import ru.nsu.ccfit.zharkova.chat.protocol.*;
import ru.nsu.ccfit.zharkova.chat.server.UserInfo;

import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * Created by Yana on 28.07.15.
 */
public class Controller {

    private final ChatFrame chatFrame;
    private Connection connection;
    private final Object syncObj = new Object();

    public Controller() throws IOException, BadLocationException {
        chatFrame = new ChatFrame(this);
    }

    public void handleLoginAction(String host, int port, String nick) throws BadLocationException, ParserConfigurationException {
        try {
            connection = new Connection(host, port, nick, this);
        } catch (IOException e) {
            chatFrame.setLogoutButtonMode();
            chatFrame.setMainLabel("connection timeout");
            connection.disconnect();
        }
    }

    public void handleSendingAction(String msg)  {
        synchronized (syncObj) {
            try {
                if(!msg.isEmpty()) {
                    connection.sendMessage(new ChatMessagesFromClient(msg, connection.getSession(), chatFrame.getMessageColor()));
                }
            } catch (BadLocationException ignored) {
            } catch (IOException e) {
                connection.disconnect();
            }
        }
    }

    public void handleLogoutAction(String m) throws IOException, BadLocationException {
        synchronized (syncObj) {
            chatFrame.setLogoutButtonMode();
            chatFrame.setMainLabel(m);
            connection.sendMessage(new LogoutMSG(connection.getSession()));
            connection.disconnect();
        }
    }

    public void changeViewMode(boolean b) {
        if(b) {
            chatFrame.setLoginButtonMode();
        }
        else{
            chatFrame.setLogoutButtonMode();
        }
    }

    public void updateUsersList(List<UserInfo> contacts) {
        for (UserInfo userInfo: contacts) {
            chatFrame.getListModel().addElement(userInfo.getName());
        }
    }

    public void handleIncomingMessage(ChatMessageFromServer msg) throws IOException, BadLocationException {
        if(msg.getChatClientName().equals(ChatFrame.CHAT_NAME)) {
            chatFrame.append(msg.getMessage(), msg.getColor());
        } else {
            chatFrame.setLogoutButtonMode();
            connection.sendMessage(new LogoutMSG(connection.getSession()));
            connection.disconnect();
        }
    }

    public void addUserToList(String nick) {
        chatFrame.getListModel().addElement(nick);
        chatFrame.append("User " + nick + " joined", Color.black);
    }

    public void removeUserFromList(String nick) {
        chatFrame.getListModel().removeElement(nick);
        chatFrame.append("User " + nick + " left", Color.black);
    }

    public void handleDisconnect() {
        connection.disconnect();
    }

    public void setSessionID(int id) {
        connection.setSession(id);
    }

    public void handleSuccessMSG(ChatMessagesFromClient msg) {
        chatFrame.append(connection.getUsername()+": " + msg.getMessage(), msg.getColor());
    }
}
