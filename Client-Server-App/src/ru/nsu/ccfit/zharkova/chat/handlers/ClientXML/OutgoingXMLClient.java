package ru.nsu.ccfit.zharkova.chat.handlers.ClientXML;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.nsu.ccfit.zharkova.chat.handlers.OutgoingMessageHandler;
import ru.nsu.ccfit.zharkova.chat.protocol.*;

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
public class OutgoingXMLClient implements OutgoingMessageHandler {

    private final ObjectOutputStream outputStream;
    private final DocumentBuilder documentBuilder;

    private static final String UNKNOWN_PROTOCOL = "Unknown protocol";
    private static final String COMMAND = "command";
    private static final String NAME = "name";
    private static final String LIST = "list";
    private static final String SESSION = "session";
    private static final String MESSAGE = "message";
    private static final String COLOR = "color";
    private static final String LOGOUT = "logout";
    private static final String LOGIN = "login";
    private static final String TYPE = "type";

    public OutgoingXMLClient(ObjectOutputStream sOutput) throws ParserConfigurationException {
        this.outputStream = sOutput;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
    }

    @Override
    public void go(Message message) throws IOException, TransformerException {
        byte[] bytes;
        if(message instanceof LoginMSG){
            bytes = transformLoginMSG((LoginMSG) message);
        } else if(message instanceof LogoutMSG){
            bytes = transformLogoutMSG((LogoutMSG) message);
        } else if (message instanceof ChatMessagesFromClient) {
            bytes = transformChatMessagesFromClient((ChatMessagesFromClient)message);
        } else if (message instanceof ListRequestMSG) {
            bytes = transformListRequestMSG((ListRequestMSG)message);
        } else {
                throw new ProtocolException(UNKNOWN_PROTOCOL);
        }

        int size = bytes.length;
        outputStream.writeInt(size);
        outputStream.write(bytes);
        outputStream.flush();
    }

    private byte[] transformListRequestMSG(ListRequestMSG message) throws TransformerException {
        Document document = documentBuilder.newDocument();
        Element root = document.createElement(COMMAND);
        Attr attr = document.createAttribute(NAME);
        attr.setValue(LIST);
        root.setAttributeNode(attr);
        document.appendChild(root);

        Element session = document.createElement(SESSION);
        session.appendChild(document.createTextNode(String.valueOf(message.getSession())));
        root.appendChild(session);
        return toByte(document);
    }

    private byte[] toByte(Document document) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(byteArrayOutputStream));
        return byteArrayOutputStream.toByteArray();
    }

    private byte[] transformChatMessagesFromClient(ChatMessagesFromClient message) throws TransformerException {
        Document document = documentBuilder.newDocument();
        Element root = document.createElement(COMMAND);
        Attr attr = document.createAttribute(NAME);
        attr.setValue(MESSAGE);
        root.setAttributeNode(attr);
        document.appendChild(root);

        Element messageElement = document.createElement(MESSAGE);
        messageElement.appendChild(document.createTextNode(message.getMessage()));
        root.appendChild(messageElement);

        Element sessionElement = document.createElement(SESSION);
        sessionElement.appendChild(document.createTextNode(String.valueOf(message.getSession())));
        root.appendChild(sessionElement);

        Element colorElement = document.createElement(COLOR);
        colorElement.appendChild(document.createTextNode(String.valueOf(message.getColor().getRGB())));
        root.appendChild(colorElement);

        return toByte(document);
    }

    private byte[] transformLogoutMSG(LogoutMSG message) throws TransformerException {
        Document document = documentBuilder.newDocument();
        Element root = document.createElement(COMMAND);
        Attr attr = document.createAttribute(NAME);
        attr.setValue(LOGOUT);
        root.setAttributeNode(attr);
        document.appendChild(root);

        Element sessionElement = document.createElement(SESSION);
        sessionElement.appendChild(document.createTextNode(String.valueOf(message.getSession())));
        root.appendChild(sessionElement);

        return toByte(document);
    }

    private byte[] transformLoginMSG(LoginMSG message) throws TransformerException {
        Document document = documentBuilder.newDocument();
        Element root = document.createElement(COMMAND);
        Attr attr = document.createAttribute(NAME);
        attr.setValue(LOGIN);
        root.setAttributeNode(attr);
        document.appendChild(root);

        Element usernameElement = document.createElement(NAME);
        usernameElement.appendChild(document.createTextNode(message.getUsername()));
        root.appendChild(usernameElement);

        Element chatClientElement = document.createElement(TYPE);
        chatClientElement.appendChild(document.createTextNode(message.getChatClientName()));
        root.appendChild(chatClientElement);

        return toByte(document);
    }
}
