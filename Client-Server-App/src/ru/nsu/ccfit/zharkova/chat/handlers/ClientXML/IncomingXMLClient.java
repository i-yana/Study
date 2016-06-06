package ru.nsu.ccfit.zharkova.chat.handlers.ClientXML;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.nsu.ccfit.zharkova.chat.handlers.IncomingMessageHandler;
import ru.nsu.ccfit.zharkova.chat.handlers.XMLParserException;
import ru.nsu.ccfit.zharkova.chat.protocol.*;
import ru.nsu.ccfit.zharkova.chat.server.UserInfo;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yana on 31.07.15.
 */
public class IncomingXMLClient implements IncomingMessageHandler {

    private final ObjectInputStream inputStream;
    private Node root;

    private static final String EVENT = "event";
    private static final String ERROR = "error";
    private static final String SUCCESS = "success";

    private static final String SESSION = "session";
    private static final String LIST_USERS = "listusers";
    private static final String USER = "user";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String MESSAGE = "message";
    private static final String COLOR = "color";
    private static final String USER_LOGIN = "userlogin";
    private static final String USER_LOGOUT = "userlogout";

    private static final String UNKNOWN_PROTOCOL = "unknown protocol";

    private static final String LIST_USER_EXPECTED_USER = "LIST USERS expected <user>, actually ";
    private static final String LIST_USER_EXPECTED_NAME = "LIST USERS expected <name>, actually ";
    private static final String LIST_USER_EXPECTED_TYPE = "LIST USERS expected <type>, actually ";
    private static final String ERROR_EXPECTED_MESSAGE = "ERROR expected <message>, actually ";
    private static final String EVENT_MESSAGE_EXPECTED_MESSAGE = "EVENT MESSAGE expected <message>, actually ";
    private static final String EVENT_MESSAGE_EXPECTED_NAME = "EVENT MESSAGE expected <name>, actually ";
    private static final String EVENT_MESSAGE_EXPECTED_COLOR = "EVENT MESSAGE expected <color>, actually ";

    private static final String LIST_USER_WRONG_NUM_CHILD_USER = "LIST USERS wrong number of child in <user> node";
    private static final String LIST_USER_WRONG_NUM_CHILD_NAME = "LIST USERS wrong number of child in <name> node";
    private static final String LIST_USER_WRONG_NUM_CHILD_TYPE = "LIST USERS wrong number of child in <type> node";
    private static final String EVENT_MESSAGE_WRONG_NUM_CHILD_MESSAGE = "EVENT MESSAGE wrong number of child in <message> node";


    private static final String LIST_USER_NOT_FOUND_USER_INFO_VALUE = "LIST USERS expected value of <name> and <type>";
    private static final String LIST_USER_NOT_FOUND_USER_INFO = "LIST USERS not found username or client";
    private static final String EVENT_MESSAGE_NOT_FOUND_USER_INFO = "EVENT MESSAGE not found message or client";
    private static final String EVENT_LOGIN_NOT_FOUND_USERNAME = "EVENT LOGIN not found username";
    private static final String EVENT_LOGOUT_NOT_FOUND_USERNAME = "EVENT LOGOUT not found username";

    private static final int LENGTH_OF_SUCCESS_MSG = 0;
    private static final int LENGTH_OF_LIST_USER_NODE = 2;
    private static final int LENGTH_OF_NAME_NODE = 1;
    private static final int LENGTH_OF_TYPE_NODE = 1;
    private static final int LENGTH_OF_MESSAGE_NODE = 3;

    private static final int USERNAME_NODE_INDEX = 0;
    private static final int CLIENT_TYPE_NODE_INDEX = 1;
    private static final int MESSAGE_NODE_INDEX = 0;
    private static final int COLOR_NODE_INDEX = 2;
    private static final int FIRST_INDEX = 0;


    public IncomingXMLClient(ObjectInputStream input) {
        this.inputStream = input;
    }

    @Override
    public Object getObject() throws IOException {
        int messageSize = inputStream.readInt();
        byte[] bytes = new byte[messageSize];
        inputStream.read(bytes);
        try {
            return parseBytes(bytes);
        } catch (XMLParserException | ParserConfigurationException | SAXException e) {
            System.out.println(e.getMessage());
        }
        throw new IOException();
    }

    private Object parseBytes(byte[] bytes) throws XMLParserException, ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new ByteArrayInputStream(bytes));
        root = document.getFirstChild();
        String tag = root.getNodeName();
        switch (tag){
            case EVENT:
                return parseEvent();
            case ERROR:
                return parseError();
            case SUCCESS:
                return parseSuccess();
        }
        throw new XMLParserException(UNKNOWN_PROTOCOL);
    }

    private Object parseSuccess() throws XMLParserException {
        if (root.getChildNodes().getLength() == LENGTH_OF_SUCCESS_MSG) {
            return new SuccessMSG();
        }
        Node firstChild = root.getFirstChild();
        String tag = firstChild.getNodeName();
        switch (tag) {
            case SESSION:
                return new LoginSuccessesMSG(Integer.parseInt(root.getFirstChild().getTextContent()));
            case LIST_USERS: {
                List<UserInfo> contacts = new ArrayList<>();
                NodeList users = firstChild.getChildNodes();
                for (int i = FIRST_INDEX; i < users.getLength(); i++) {
                    Element user = (Element) users.item(i);
                    if(!user.getTagName().equals(USER)){
                        throw new XMLParserException(LIST_USER_EXPECTED_USER + user.getTagName());
                    }
                    NodeList userChildren = user.getChildNodes();
                    if(userChildren.getLength() != LENGTH_OF_LIST_USER_NODE){
                        throw new XMLParserException(LIST_USER_WRONG_NUM_CHILD_USER);
                    }
                    Element userName = (Element) userChildren.item(USERNAME_NODE_INDEX);
                    if(!userName.getTagName().equals(NAME)){
                        throw new XMLParserException(LIST_USER_EXPECTED_NAME + userName.getTagName());
                    }
                    Element userClientType = (Element) userChildren.item(CLIENT_TYPE_NODE_INDEX);
                    if(!userClientType.getTagName().equals(TYPE)){
                        throw new XMLParserException(LIST_USER_EXPECTED_TYPE+ userClientType.getTagName());
                    }
                    NodeList userNameChild = userName.getChildNodes();
                    if(userNameChild.getLength() != LENGTH_OF_NAME_NODE){
                        throw new XMLParserException(LIST_USER_WRONG_NUM_CHILD_NAME);
                    }
                    NodeList userClientChild = userClientType.getChildNodes();
                    if(userClientChild.getLength() != LENGTH_OF_TYPE_NODE){
                        throw new XMLParserException(LIST_USER_WRONG_NUM_CHILD_TYPE);
                    }
                    Node userNameValue = userName.getFirstChild();
                    Node chatClientValue = userClientType.getFirstChild();

                    if(userNameValue == null || chatClientValue == null){
                        throw new XMLParserException(LIST_USER_NOT_FOUND_USER_INFO_VALUE);
                    }
                    String nick = userNameValue.getNodeValue();
                    String client = chatClientValue.getNodeValue();
                    if(nick == null || client == null){
                        throw new XMLParserException(LIST_USER_NOT_FOUND_USER_INFO);
                    }
                    contacts.add(new UserInfo(nick, client));
                }
                return new ListUsersMSG(contacts);
            }
            default:
                throw new XMLParserException(UNKNOWN_PROTOCOL);
        }
    }


    private Object parseError() throws XMLParserException {
        Node messageNode = root.getFirstChild();
        if(messageNode.getNodeName().equals(MESSAGE)){
            return new ErrorMSG(messageNode.getTextContent());
        }
        throw new XMLParserException(ERROR_EXPECTED_MESSAGE + messageNode.getNodeName());
    }

    private Object parseEvent() throws XMLParserException {
        String command = root.getAttributes().getNamedItem(NAME).getNodeValue();
        switch (command){
            case (MESSAGE):{
                NodeList messageChild = root.getChildNodes();
                if(messageChild.getLength() != LENGTH_OF_MESSAGE_NODE){
                    throw new XMLParserException(EVENT_MESSAGE_WRONG_NUM_CHILD_MESSAGE);
                }

                Element messageElement = (Element) messageChild.item(MESSAGE_NODE_INDEX);
                if(!messageElement.getTagName().equals(MESSAGE)){
                    throw new XMLParserException(EVENT_MESSAGE_EXPECTED_MESSAGE+ messageElement.getTagName());
                }
                String message = messageElement.getTextContent();

                Element chatClientElement = (Element) messageChild.item(CLIENT_TYPE_NODE_INDEX);
                if(!chatClientElement.getTagName().equals(NAME)){
                    throw new XMLParserException(EVENT_MESSAGE_EXPECTED_NAME + chatClientElement.getTagName());
                }
                String chatClient = chatClientElement.getTextContent();

                Element colorElement = (Element) messageChild.item(COLOR_NODE_INDEX);
                if(!colorElement.getTagName().equals(COLOR)){
                    throw new XMLParserException(EVENT_MESSAGE_EXPECTED_COLOR + colorElement.getTagName());
                }
                Color color = new Color(Integer.parseInt(colorElement.getTextContent()));

                if(message == null || chatClient == null){
                    throw new XMLParserException(EVENT_MESSAGE_NOT_FOUND_USER_INFO);
                }

                return new ChatMessageFromServer(chatClient, message, color);

            }
            case (USER_LOGIN):{
                String userName = root.getFirstChild().getTextContent();
                if(userName == null){
                    throw new XMLParserException(EVENT_LOGIN_NOT_FOUND_USERNAME);
                }
                return new UserLogin(userName);
            }
            case (USER_LOGOUT):{
                String userName = root.getFirstChild().getTextContent();
                if(userName == null){
                    throw new XMLParserException(EVENT_LOGOUT_NOT_FOUND_USERNAME);
                }
                return new UserLogout(userName);
            }
            default:
                throw new XMLParserException(EVENT+UNKNOWN_PROTOCOL);
        }
    }
}
