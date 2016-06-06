package ru.nsu.ccfit.zharkova.chat.client;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.nsu.ccfit.zharkova.chat.handlers.IncomingMessageHandler;
import ru.nsu.ccfit.zharkova.chat.protocol.*;
import ru.nsu.ccfit.zharkova.chat.server.UserInfo;

import javax.swing.text.BadLocationException;
import java.io.IOException;
import java.util.List;

/**
 * Created by Yana on 07.07.15.
 */
class ListenFromServer extends Thread {

    private IncomingMessageHandler incomingMessageHandler;
    private int session;
    private Controller controller;
    private final List<Request> requestList;
    private final Object syncObj;
    private final SendingMessageHandler sendingMessageHandler;
    private final Logger logger = LogManager.getLogger(ListenFromServer.class);

    public ListenFromServer(IncomingMessageHandler incomingMessageHandler, Controller controller, SendingMessageHandler sendingMessageHandler, Object syncObj, List<Request> requestList) {
        this.incomingMessageHandler = incomingMessageHandler;
        this.controller = controller;
        this.sendingMessageHandler = sendingMessageHandler;
        this.requestList = requestList;
        this.syncObj = syncObj;
    }


    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Message msg = (Message) incomingMessageHandler.getObject();
                if (msg instanceof Event) {
                    handleEvent((Event) msg);
                    continue;
                }
                if(msg instanceof ServerAnswer){
                    synchronized (syncObj) {
                        while (requestList.isEmpty()){
                            syncObj.wait();
                        }
                        handleServerAnswer((ServerAnswer) msg);
                    }
                    continue;
                }
                logger.info("unknown protocol");
                controller.handleLogoutAction("unknown protocol");

            }
        } catch (InterruptedException | BadLocationException ignored) {
        } catch (IOException e) {
            try {
                controller.handleLogoutAction("Disconnect");
            } catch (IOException | BadLocationException e1) {
                logger.info(e1.getMessage());
                controller.handleDisconnect();
            }
        } catch (ListenFromServerException | ClassNotFoundException e) {
            logger.info(e.getMessage());
            controller.handleDisconnect();
        }
    }

    private void handleServerAnswer(ServerAnswer msg) throws IOException, BadLocationException, ListenFromServerException {

        if (requestList.get(0) instanceof ChatMessagesFromClient) {
            if (msg instanceof SuccessMSG) {
                controller.handleSuccessMSG((ChatMessagesFromClient) requestList.get(0));
                logger.info("SuccessMSG message on message " + (((ChatMessagesFromClient) requestList.get(0)).getMessage()));
                requestList.remove(0);
                return;
            }
            else if (msg instanceof ErrorMSG) {
                controller.handleLogoutAction(((ErrorMSG) msg).getReason());
            }
        }
        if (requestList.get(0) instanceof LoginMSG) {
            if (msg instanceof LoginSuccessesMSG) {
                LoginSuccessesMSG sm = (LoginSuccessesMSG) msg;
                this.session = sm.getSession();
                controller.setSessionID(session);
                sendingMessageHandler.addMessage(new ListRequestMSG(session));
                controller.changeViewMode(true);
                requestList.remove(0);
                logger.info("Login success");
                return;
            }
            else if (msg instanceof ErrorMSG) {
                logger.info("ErrorMSG came " + ((ErrorMSG) msg).getReason());
                controller.handleLogoutAction(((ErrorMSG) msg).getReason());
            }
            return;
        }
        if (requestList.get(0) instanceof ListRequestMSG) {
            if (msg instanceof ListUsersMSG) {
                List<UserInfo> contacts = ((ListUsersMSG) msg).getContacts();
                controller.updateUsersList(contacts);
                requestList.remove(0);
                logger.info("Contacts list came");
            }
            else if (msg instanceof ErrorMSG) {
                controller.handleLogoutAction(((ErrorMSG) msg).getReason());
            }
            return;
        }
        if (requestList.get(0) instanceof LogoutMSG) {
            if (msg instanceof SuccessMSG) {
                controller.handleDisconnect();
                requestList.remove(0);
            }
            else if (msg instanceof ErrorMSG) {
                controller.handleLogoutAction(((ErrorMSG) msg).getReason());
            }
            return;
        }
        throw new ListenFromServerException("request was not satisfied");
    }

    private void handleEvent(Event event) throws IOException, BadLocationException, ListenFromServerException {
        if (event instanceof ChatMessageFromServer) {
            controller.handleIncomingMessage((ChatMessageFromServer) event);
            logger.info("EVENT ChatMessageFromServer "+ ((ChatMessageFromServer) event).getMessage());
            return;
        }

        if (event instanceof UserLogin) {
            controller.addUserToList(((UserLogin) event).getNick());
            logger.info("EVENT message: new user in chat");
            return;
        }
        if (event instanceof UserLogout) {
            controller.removeUserFromList(((UserLogout) event).getNick());
            logger.info("EVENT message: user leave");
            return;
        }
        throw new ListenFromServerException("cannot handle EVENT");
    }
}
