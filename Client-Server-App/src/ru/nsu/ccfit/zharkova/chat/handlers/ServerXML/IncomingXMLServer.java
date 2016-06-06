package ru.nsu.ccfit.zharkova.chat.handlers.ServerXML;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.nsu.ccfit.zharkova.chat.handlers.IncomingMessageHandler;
import ru.nsu.ccfit.zharkova.chat.handlers.XMLParserException;
import ru.nsu.ccfit.zharkova.chat.protocol.ChatMessagesFromClient;
import ru.nsu.ccfit.zharkova.chat.protocol.ListRequestMSG;
import ru.nsu.ccfit.zharkova.chat.protocol.LoginMSG;
import ru.nsu.ccfit.zharkova.chat.protocol.LogoutMSG;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by Yana on 31.07.15.
 */
public class IncomingXMLServer implements IncomingMessageHandler {

    private final ObjectInputStream objectInputStream;
    private Node root;

    private static final String COMMAND = "command";
    private static final String LOGIN = "login";
    private static final String LOGOUT = "logout";
    private static final String MESSAGE = "message";
    private static final String LIST = "list";
    private static final String SESSION = "session";
    private static final String COLOR = "color";
    private static final String NAME = "name";
    private static final String TYPE = "type";

    private static final String UNKNOWN_PROTOCOL = "unknown protocol";
    private static final String NOT_FOUND_COMMAND = "not found command name";

    private static final String LIST_REQUEST_WRONG_NUM_CHILD_LIST = "LIST wrong number of child in <list> node";
    private static final String MESSAGE_WRONG_NUM_CHILD_MESSAGE = "MESSAGE wrong number of child <message>";
    private static final String LOGOUT_WRONG_NUM_CHILD_LOGOUT = "LOGOUT wrong number of child <logout>";
    private static final String LOGIN_WRONG_NUM_CHILD_LOGIN = "LOGIN wrong number of child <login>";

    private static final String LIST_REQUEST_EXPECTED_SESSION = "LIST expected <session>, actually ";
    private static final String MESSAGE_EXPECTED_MSG = "MESSAGE expected <message>, actually ";
    private static final String MESSAGE_EXPECTED_SESSION = "MESSAGE expected <session>, actually ";
    private static final String MESSAGE_EXPECTED_COLOR = "MESSAGE expected <color>, actually ";
    private static final String LOGOUT_EXPECTED_SESSION = "LOGOUT; expected <session>, actually ";
    private static final String LOGIN_EXPECTED_NAME = "LOGIN expected <name>, actually ";
    private static final String LOGIN_EXPECTED_TYPE = "LOGIN expected <type>, actually ";

    private static final String LIST_REQUEST_NOT_FOUND_SESSION = "LIST not found sessionID";
    private static final String MESSAGE_NOT_FOUND_INFO = "MESSAGE not found message or sessionID or color";
    private static final String LOGOUT_NOT_FOUND_SESSION = "LOGOUT not found sessionID";
    private static final String LOGIN_NOT_FOUND_INFO = "LOGIN not found username or chatClientName";

    private static final int LENGTH_OF_LIST_REQUEST_NODE = 1;
    private static final int LENGTH_OF_MESSAGE_NODE = 3;
    private static final int LENGTH_OF_LOGOUT_NODE = 1;
    private static final int LENGTH_OF_LOGIN_NODE = 2;

    private static final int LIST_SESSION_NODE_INDEX = 0;
    private static final int MESSAGE_NODE_INDEX = 0;
    private static final int SESSION_NODE_INDEX = 1;
    private static final int COLOR_NODE_INDEX = 2;
    private static final int LOGOUT_SESSION_NODE_INDEX = 0;
    private static final int LOGIN_NAME_NODE_INDEX = 0;
    private static final int LOGIN_TYPE_NODE_INDEX = 1;


    public IncomingXMLServer(ObjectInputStream sInput) {
        this.objectInputStream = sInput;
    }

    @Override
    public Object getObject() throws IOException, ClassNotFoundException {
        int size;
        size = objectInputStream.readInt();
        byte[] bytes = new byte[size];
        objectInputStream.read(bytes);
        try {
            return parseBytes(bytes);
        }catch (ParserConfigurationException | SAXException | XMLParserException e) {
            System.out.println(e.getMessage());
            throw new IOException();
        }
    }

    private Object parseBytes(byte[] bytes) throws ParserConfigurationException, IOException, SAXException, XMLParserException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new ByteArrayInputStream(bytes));
        root = document.getFirstChild();
        String tag = root.getNodeName();
        if(!tag.equals(COMMAND)){
            throw new XMLParserException(UNKNOWN_PROTOCOL);
        }
        String commandName = root.getAttributes().getNamedItem(NAME).getNodeValue();
        switch (commandName){
            case LOGIN:
                return parseLoginMSG();
            case LOGOUT:
                return parseLogoutMSG();
            case MESSAGE:
                return parseMessageFromClient();
            case LIST:
                return parseListRequest();
                default:
                    throw new XMLParserException(NOT_FOUND_COMMAND);
        }
    }

    private Object parseListRequest() throws XMLParserException {
        NodeList nodeList = root.getChildNodes();
        String session = null;
        if(nodeList.getLength() != LENGTH_OF_LIST_REQUEST_NODE){
            throw new XMLParserException(LIST_REQUEST_WRONG_NUM_CHILD_LIST);
        }
        Node node = nodeList.item(LIST_SESSION_NODE_INDEX);
        if(!node.getNodeName().equals(SESSION)){
            throw new XMLParserException(LIST_REQUEST_EXPECTED_SESSION+ node.getNodeName());
        }
        session = node.getTextContent();
        if(session == null){
            throw new XMLParserException(LIST_REQUEST_NOT_FOUND_SESSION);
        }

        return new ListRequestMSG(Integer.parseInt(session));
    }

    private Object parseMessageFromClient() throws XMLParserException {
        NodeList nodeList = root.getChildNodes();
        if(nodeList.getLength() != LENGTH_OF_MESSAGE_NODE){
            throw new XMLParserException(MESSAGE_WRONG_NUM_CHILD_MESSAGE);
        }
        Node messageNode = nodeList.item(MESSAGE_NODE_INDEX);
        if(!messageNode.getNodeName().equals(MESSAGE)){
            throw new XMLParserException(MESSAGE_EXPECTED_MSG + messageNode.getNodeName());
        }
        String message = messageNode.getTextContent();

        Node sessionNode = nodeList.item(SESSION_NODE_INDEX);
        if(!sessionNode.getNodeName().equals(SESSION)){
            throw new XMLParserException(MESSAGE_EXPECTED_SESSION +sessionNode.getNodeName());
        }
        String session = sessionNode.getTextContent();

        Node colorNode = nodeList.item(COLOR_NODE_INDEX);
        if(!colorNode.getNodeName().equals(COLOR)){
            throw new XMLParserException(MESSAGE_EXPECTED_COLOR + colorNode.getNodeName());
        }
        String colorName = colorNode.getTextContent();

        if(message == null || session == null || colorName == null){
            throw new XMLParserException(MESSAGE_NOT_FOUND_INFO);
        }
        Color color = new Color(Integer.parseInt(colorName));

        return new ChatMessagesFromClient(message,Integer.parseInt(session), color);
    }

    private Object parseLogoutMSG() throws XMLParserException {
        NodeList nodeList = root.getChildNodes();
        if(nodeList.getLength() != LENGTH_OF_LOGOUT_NODE){
            throw new XMLParserException(LOGOUT_WRONG_NUM_CHILD_LOGOUT);
        }
        Node sessionNode = nodeList.item(LOGOUT_SESSION_NODE_INDEX);
        if(!sessionNode.getNodeName().equals(SESSION)){
            throw new XMLParserException(LOGOUT_EXPECTED_SESSION + sessionNode.getNodeName());
        }
        String session = sessionNode.getTextContent();
        if(session == null){
            throw new XMLParserException(LOGOUT_NOT_FOUND_SESSION);
        }

        return new LogoutMSG(Integer.parseInt(session));
    }

    private Object parseLoginMSG() throws XMLParserException {
        NodeList nodeList = root.getChildNodes();
        if(nodeList.getLength() != LENGTH_OF_LOGIN_NODE){
            throw new XMLParserException(LOGIN_WRONG_NUM_CHILD_LOGIN);
        }
        Node nameNode = nodeList.item(LOGIN_NAME_NODE_INDEX);
        if(!nameNode.getNodeName().equals(NAME)){
            throw new XMLParserException(LOGIN_EXPECTED_NAME + nameNode.getNodeName());
        }
        String userName = nameNode.getTextContent();

        Node clientChatNode = nodeList.item(LOGIN_TYPE_NODE_INDEX);
        if(!clientChatNode.getNodeName().equals(TYPE)){
            throw new XMLParserException(LOGIN_EXPECTED_TYPE+ clientChatNode.getNodeName());
        }
        String chatClientName = clientChatNode.getTextContent();

        if(userName == null || chatClientName == null){
            throw new XMLParserException(LOGIN_NOT_FOUND_INFO);
        }

        return new LoginMSG(userName, chatClientName);
    }
}
