package ru.nsu.ccfit.zharkova.chat.server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.nsu.ccfit.zharkova.chat.protocol.Message;
import ru.nsu.ccfit.zharkova.chat.handlers.OutgoingMessageHandler;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;

/**
 * Created by Yana on 03.07.15.
 */
public class SendManager implements Runnable {

    private final List<Message> messageList;
    private final Object queueSyncObj;
    private final OutgoingMessageHandler outgoingMessageHandler;
    private final ClientConnection connection;
    private final Logger logger = LogManager.getLogger(SendManager.class);


    public SendManager(List<Message> messageList, Object queueSyncObj, OutgoingMessageHandler outgoingMessageHandler, ClientConnection clientConnection) {
        this.messageList = messageList;
        this.queueSyncObj = queueSyncObj;
        this.outgoingMessageHandler = outgoingMessageHandler;
        this.connection = clientConnection;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (queueSyncObj) {
                    if(messageList.isEmpty() && !Thread.currentThread().isInterrupted()) {
                        queueSyncObj.wait();
                    }
                }
                while (!messageList.isEmpty()) {
                    synchronized (queueSyncObj) {
                        outgoingMessageHandler.go(messageList.get(0));
                        logger.info("Send message TYPE " + messageList.get(0).getClass().getSimpleName());
                        messageList.remove(0);
                    }
                }
            }
        } catch (InterruptedException ignored) {}
        catch (IOException e) {
            connection.stop();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
