package ru.nsu.ccfit.zharkova.chat.client;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.nsu.ccfit.zharkova.chat.handlers.OutgoingMessageHandler;
import ru.nsu.ccfit.zharkova.chat.protocol.Message;
import ru.nsu.ccfit.zharkova.chat.protocol.Request;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yana on 28.07.15.
 */
public class SendingMessageHandler extends Thread{

    private final List<Message> messagesForServer;
    private final List<Request> requestList;
    private final OutgoingMessageHandler outgoingMessageHandler;
    private final Object syncObj;
    private final Logger logger = LogManager.getLogger(Connection.class);

    public SendingMessageHandler(OutgoingMessageHandler outgoingMessageHandler, Object syncObj, List<Request> requestList){
        this.messagesForServer = new ArrayList<>();
        this.outgoingMessageHandler = outgoingMessageHandler;
        this.syncObj = syncObj;
        this.requestList = requestList;
    }

    public void addMessage(Message message){
        synchronized (messagesForServer){
            if(!message.equals(null)) {
                messagesForServer.add(message);
                messagesForServer.notifyAll();
            }
        }
    }

    public void addRequest(Request request){
        synchronized (syncObj){
            requestList.add(request);
            syncObj.notifyAll();
        }
    }

    public Message getMessage(){
        synchronized (messagesForServer) {
            return messagesForServer.get(0);
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (messagesForServer) {
                    if(messagesForServer.isEmpty()) {
                        messagesForServer.wait();
                    }
                }
                while(!messagesForServer.isEmpty()) {
                    synchronized (messagesForServer) {
                        outgoingMessageHandler.go(messagesForServer.get(0));
                        addRequest((Request) messagesForServer.remove(0));
                        logger.info("Send request to server: " + requestList.get(0).getClass().getSimpleName());
                    }
                }
            }
        } catch (InterruptedException | IOException | TransformerException e) {
            logger.info(e.getMessage());
        }
    }
}
