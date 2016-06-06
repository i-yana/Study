package ru.nsu.ccfit.zharkova.chat.handlers.ServerXML;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.nsu.ccfit.zharkova.chat.handlers.OutgoingMessageHandler;
import ru.nsu.ccfit.zharkova.chat.protocol.*;
import ru.nsu.ccfit.zharkova.chat.server.UserInfo;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ProtocolException;

/**
 * Created by Yana on 31.07.15.
 */
public class OutgoingXMLServer implements OutgoingMessageHandler {

    private final ObjectOutputStream outputStream;
    private final DocumentBuilder documentBuilder;

    private static final String UNKNOWN_PROTOCOL = "Unknown protocol";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";
    private static final String SUCCESS = "success";
    private static final String EVENT = "event";
    private static final String NAME = "name";
    private static final String COLOR = "color";
    private static final String USER_LOGOUT = "userlogout";
    private static final String USER_LOGIN = "userlogin";
    private static final String USER_LIST = "listusers";
    private static final String USER = "user";
    private static final String TYPE = "type";
    private static final String SESSION = "session";

    public OutgoingXMLServer(ObjectOutputStream sOutput) throws ParserConfigurationException {
        this.outputStream = sOutput;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
    }

    @Override
    public void go(Message message) throws IOException, TransformerException {
        byte[] bytes;
        if (message instanceof LoginSuccessesMSG) {
            bytes = transformLoginSuccessMSG((LoginSuccessesMSG) message);
        } else if (message instanceof ListUsersMSG) {
            bytes = transformListUsersMSG((ListUsersMSG) message);
        } else if (message instanceof ChatMessageFromServer) {
            bytes = transformChatMessageFromServer((ChatMessageFromServer) message);
        } else if (message instanceof UserLogin) {
            bytes = transformUserLoginMSG((UserLogin) message);
        } else if (message instanceof UserLogout) {
            bytes = transformUserLogoutMSG((UserLogout) message);
        } else if (message instanceof SuccessMSG) {
            bytes = transformSuccessMSG((SuccessMSG) message);
        } else if (message instanceof ErrorMSG) {
            bytes = transformErrorMSG((ErrorMSG) message);
        } else {
            throw new ProtocolException(UNKNOWN_PROTOCOL);
        }

        int size = bytes.length;
        outputStream.writeInt(size);
        outputStream.write(bytes);
        outputStream.flush();
    }

    private byte[] transformErrorMSG(ErrorMSG message) throws TransformerException {
        Document document = documentBuilder.newDocument();
        Element root = document.createElement(ERROR);
        document.appendChild(root);

        Element messageElement = document.createElement(MESSAGE);
        messageElement.appendChild(document.createTextNode(message.getReason()));
        root.appendChild(messageElement);

        return toByte(document);
    }

    private byte[] toByte(Document document) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(byteArrayOutputStream));
        return byteArrayOutputStream.toByteArray();
    }

    private byte[] transformSuccessMSG(SuccessMSG message) throws TransformerException {
        Document document = documentBuilder.newDocument();
        Element root = document.createElement(SUCCESS);
        document.appendChild(root);

        return toByte(document);
    }

    private byte[] transformUserLogoutMSG(UserLogout message) throws TransformerException {
        Document document = documentBuilder.newDocument();
        Element root = document.createElement(EVENT);
        Attr attr = document.createAttribute(NAME);
        attr.setValue(USER_LOGOUT);
        root.setAttributeNode(attr);
        document.appendChild(root);

        Element usernameElement = document.createElement(NAME);
        usernameElement.appendChild(document.createTextNode(message.getNick()));
        root.appendChild(usernameElement);

        return toByte(document);
    }

    private byte[] transformUserLoginMSG(UserLogin message) throws TransformerException {
        Document document = documentBuilder.newDocument();
        Element root = document.createElement(EVENT);
        Attr attr = document.createAttribute(NAME);
        attr.setValue(USER_LOGIN);
        root.setAttributeNode(attr);
        document.appendChild(root);

        Element usernameElement = document.createElement(NAME);
        usernameElement.appendChild(document.createTextNode(message.getNick()));
        root.appendChild(usernameElement);

        return toByte(document);
    }

    private byte[] transformChatMessageFromServer(ChatMessageFromServer message) throws TransformerException {
        Document document = documentBuilder.newDocument();
        Element root = document.createElement(EVENT);
        Attr attr = document.createAttribute(NAME);
        attr.setValue(MESSAGE);
        root.setAttributeNode(attr);
        document.appendChild(root);

        Element messageElement = document.createElement(MESSAGE);
        messageElement.appendChild(document.createTextNode(message.getMessage()));
        root.appendChild(messageElement);

        Element chatClientNameElement = document.createElement(NAME);
        chatClientNameElement.appendChild(document.createTextNode(message.getChatClientName()));
        root.appendChild(chatClientNameElement);

        Element colorElement = document.createElement(COLOR);
        colorElement.appendChild(document.createTextNode(String.valueOf(message.getColor().getRGB())));
        root.appendChild(colorElement);

        return toByte(document);
    }

    private byte[] transformListUsersMSG(ListUsersMSG message) throws TransformerException {
        Document document = documentBuilder.newDocument();
        Element root = document.createElement(SUCCESS);
        document.appendChild(root);

        Element listUsersElement = document.createElement(USER_LIST);
        root.appendChild(listUsersElement);

        for (UserInfo userInfo: message.getContacts()){
            Element userElement = document.createElement(USER);

            Element nameElement = document.createElement(NAME);
            nameElement.appendChild(document.createTextNode(userInfo.getName()));
            userElement.appendChild(nameElement);

            Element typeElement = document.createElement(TYPE);
            typeElement.appendChild(document.createTextNode(userInfo.getChatClientName()));
            userElement.appendChild(typeElement);

            listUsersElement.appendChild(userElement);
        }

        return toByte(document);
    }

    private byte[] transformLoginSuccessMSG(LoginSuccessesMSG message) throws TransformerException {
        Document document = documentBuilder.newDocument();
        Element root = document.createElement(SUCCESS);
        document.appendChild(root);

        Element sessionElement = document.createElement(SESSION);
        sessionElement.appendChild(document.createTextNode(String.valueOf(message.getSession())));
        root.appendChild(sessionElement);

        return toByte(document);
    }
}
