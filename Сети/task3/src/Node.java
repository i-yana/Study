import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;

/**
 * Created by Yana on 14.10.15.
 */


public class Node {
    private boolean isBoss;
    private final String hostName;
    private final int lostPercent;
    private final int port;
    private String parentIP;
    private Integer parentPort;
    private Map<String, Message> protocolMessages = new HashMap<String, Message>();
    private Map<String, Message> ackMessages = new HashMap<String, Message>();
    private final Map<String, Message> sendMessages = new HashMap<String, Message>();
    private Map<String, Message> receiveMessages = new HashMap<String, Message>();
    private Map<String, Integer> childes = new HashMap<String, Integer>();
    private final String CHILD = "CHILD";
    private final String MSG = "MSG";
    private final String PARENT = "PARENT";
    private final String ACK = "ACK";
    private final String DEAD = "DEAD";
    private final String BOSS = "BOSS";
    DatagramPacket packet = new DatagramPacket(new byte[2432], 2432);
    DatagramSocket socket;

    public Node(String hostName, int lostPercent, int port, String parentIP, int parentPort) throws SocketException {
        this.hostName = hostName;
        this.lostPercent = lostPercent;
        this.port = port;
        this.parentIP = parentIP;
        this.parentPort = parentPort;
        this.isBoss = false;
        socket = new DatagramSocket(port);
    }

    public Node(String hostName, int lostPercent, int port) throws SocketException {
        this.hostName = hostName;
        this.lostPercent = lostPercent;
        this.port = port;
        this.isBoss = true;
        socket = new DatagramSocket(port);
    }

    public void start() throws IOException {
        socket.setSoTimeout(200);
        if(!isBoss){
            sendChildMsg();
        }

        Sender sender = new Sender(socket,sendMessages,hostName);
        sender.start();

        long lastTime = 0;
        long currentTime;
        Random random = new Random();
        while(!Thread.currentThread().isInterrupted()){

            currentTime = System.currentTimeMillis()/1000;
            if(currentTime - lastTime > 0.9999) {

                sendProtocolMsgs();//отправка протокольных сообщений
                sendRecvMsg(); //отправка полученных сообщений из других узлов дальше
                sendMyMsg();//отправка моих сообщений всем
                checkAckMsg();//проверять акки, если больше 5 сек - удалять

                lastTime = currentTime;
            }

            if(random.nextInt(99)+1 < lostPercent){
                continue;
            }
            try {
                socket.receive(packet);
            }catch (SocketTimeoutException e){
                continue;
            }
            String data = new String(packet.getData(), packet.getOffset(), packet.getLength(), "UTF-8");
            Message message = new Message(data.getBytes("UTF-8"), packet.getAddress().getHostAddress(), packet.getPort(), System.currentTimeMillis()/1000);//откуда пришло
            if(message.getType().equals(MSG)){
                handleMSG(message);
            }
            if(message.getType().equals(ACK)){
                handleACK(message);
            }
            if(message.getType().equals(PARENT)){
                handleParent(message);
            }
            if(message.getType().equals(CHILD)){
                handleChild(message);
            }
            if(message.getType().equals(DEAD)){
                handleDead(message);
            }
            if(message.getType().equals(BOSS)){
                handleBOSS(message);
            }


        }
    }

    private void checkAckMsg() {
        Iterator<Map.Entry<String, Message>> it = ackMessages.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Message> item = it.next();
            if (System.currentTimeMillis() / 1000 - item.getValue().getTime() > 5) {
                it.remove();
            }
        }
    }

    private void sendMyMsg() throws IOException {
        synchronized (sendMessages) {
            Iterator<Map.Entry<String, Message>> it = sendMessages.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Message> item = it.next();
                if (System.currentTimeMillis() / 1000 - item.getValue().getTime() > 5) {
                    System.err.println("Message: " + item.getValue().getMsg() +  " - was not delivered");
                    it.remove();
                } else {
                    Message m = item.getValue();
                    if (!isBoss) {
                        socket.send(new DatagramPacket(m.getData(), m.getData().length, InetAddress.getByName(parentIP), parentPort));

                    }
                    if(!childes.isEmpty()) {
                        for (String ip : childes.keySet()) {
                            socket.send(new DatagramPacket(m.getData(), m.getData().length, InetAddress.getByName(ip), childes.get(ip)));

                        }
                    }
                }
            }
        }
    }

    private void sendRecvMsg() throws IOException {
        Iterator<Map.Entry<String, Message>> it = receiveMessages.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Message> item = it.next();
            if (System.currentTimeMillis() / 1000 - item.getValue().getTime() > 5) {
                it.remove();
            } else {
                Message m = item.getValue();
                for (String ip : childes.keySet()) {
                    if (ip.equals(m.getInetAddress())) {
                        continue;
                    }
                    socket.send(new DatagramPacket(m.getData(), m.getData().length, InetAddress.getByName(ip), childes.get(ip)));

                }
                if (!isBoss && !m.getInetAddress().equals(parentIP)) {
                    socket.send(new DatagramPacket(m.getData(), m.getData().length, InetAddress.getByName(parentIP), parentPort));
                }
            }
        }
    }

    private void sendProtocolMsgs() throws IOException {
        Iterator<Map.Entry<String, Message>> it = protocolMessages.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Message> item = it.next();
            if (System.currentTimeMillis() / 1000 - item.getValue().getTime() > 5) {
                System.err.println("Message: " + item.getValue().getMsg() +  " - was not delivered");
                it.remove();
            } else {
                Message m = item.getValue();
                socket.send(new DatagramPacket(item.getValue().getData(), item.getValue().getData().length, InetAddress.getByName(item.getValue().getInetAddress()), item.getValue().getPort()));
                if (m.getType().equals(ACK)) {
                    it.remove();
                }
            }
        }
    }

    private void sendChildMsg() throws IOException {
        String id = UUID.randomUUID().toString();
        String data = "<" + id + ">:" + CHILD;
        byte[] bytes = data.getBytes("UTF-8");
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(parentIP), parentPort);
        socket.send(packet);
        Message message = new Message(bytes, InetAddress.getByName(parentIP).getHostAddress(), parentPort, System.currentTimeMillis()/1000);//куда отправлять
        protocolMessages.put(id, message);
    }

    private void sendACK(Message message) throws IOException {
        String ack = "<" + message.getId() + ">:ACK";
        Message ackMessage = new Message(ack.getBytes("UTF-8"), packet.getAddress().getHostAddress(), packet.getPort(), System.currentTimeMillis()/1000);
        ackMessages.put(message.getId(), ackMessage);
        socket.send(new DatagramPacket(ackMessage.getData(), ackMessage.getData().length, InetAddress.getByName(ackMessage.getInetAddress()), ackMessage.getPort()));
    }

    private void handleMSG(Message message) throws IOException {
        if(ackMessages.containsKey(message.getId())){
            sendACK(message);
        }

        System.out.println(message.getMsg());

        sendACK(message);
        String newMes = "<" + UUID.randomUUID() + ">:MSG:" + message.getMsg();
        Message newMessage = new Message(newMes.getBytes("UTF-8"), packet.getAddress().getHostAddress(), packet.getPort(), System.currentTimeMillis() / 1000);
        receiveMessages.put(newMessage.getId(), newMessage);//полученное сообщение передаем дальше кроме того чей айпи указан
    }

    private void handleACK(Message message){
        if(protocolMessages.containsKey(message.getId())){
            protocolMessages.remove(message.getId());
        }
        if(sendMessages.containsKey(message.getId())){
            sendMessages.remove(message.getId());
        }
        if(receiveMessages.containsKey(message.getId())){
            receiveMessages.remove(message.getId());
        }
    }

    private void handleParent(Message message) throws IOException {
        if(ackMessages.containsKey(message.getId())){
            sendACK(message);
            return;
        }
        String tmp = message.getMsg();
        parentIP = tmp.substring(tmp.indexOf("<")+1, tmp.indexOf(">"));
        parentPort = Integer.parseInt(tmp.substring(tmp.indexOf(">:<")+3, tmp.length()-1));
        System.out.println("info: new parent with ID:"+ parentIP + " Port:" + parentPort);
        sendACK(message);

        isBoss = false;
        String ch = "<" +UUID.randomUUID().toString() + ">:CHILD";
        Message childMessage = new Message(ch.getBytes("UTF-8"), parentIP, parentPort, System.currentTimeMillis()/1000);
        protocolMessages.put(childMessage.getId(), childMessage);//отправляем CHILD родителю
    }

    private void handleChild(Message message) throws IOException {
        if(ackMessages.containsKey(message.getId())){
            sendACK(message);
            return;
        }

        if(childes.containsKey(packet.getAddress().getHostAddress())){
            return;
        }
        System.out.println("info: new child with ID:" + packet.getAddress().getHostAddress() + " Port:" + packet.getPort());
        childes.put(packet.getAddress().getHostAddress(), packet.getPort());
        sendACK(message);
    }

    private void handleDead(Message message) throws IOException {
        if(ackMessages.containsKey(message.getId())){
            sendACK(message);
            return;
        }
        if(childes.containsKey(packet.getAddress().getHostAddress())) {
            System.out.println("info: child with ID: "+ packet.getAddress().getHostAddress() + " Port:" + packet.getPort() + " dead");
            childes.remove(packet.getAddress().getHostAddress());
            sendACK(message);
        }
    }

    private void handleBOSS(Message message) throws IOException {
        if(ackMessages.containsKey(message.getId())){
            sendACK(message);
            return;
        }
        System.out.println("info: You are BOSS");
        isBoss = true;
        sendACK(message);
    }

    public void handleClosing() {
        try {
            if (isBoss) {
                if (childes.isEmpty()) {
                    return;
                }
                Object[] ip = childes.keySet().toArray();
                String bossMsg = "<" + UUID.randomUUID() + ">:BOSS";
                DatagramPacket boss = new DatagramPacket(bossMsg.getBytes("UTF-8"), bossMsg.length(), InetAddress.getByName((String)ip[0]), childes.get(ip[0]));
                for (int i = 0; i < 5; i++) {
                    socket.send(boss);
                }
                for (int i = 1; i < ip.length; i++) {
                    String parentMsg = "<" + UUID.randomUUID() + ">:PARENT:<"+ ip[0] + ">:<" + childes.get(ip[0]) + ">";
                    DatagramPacket parent = new DatagramPacket(parentMsg.getBytes("UTF-8"), parentMsg.length(), InetAddress.getByName((String)ip[i]), childes.get(ip[i]));
                    for (int j = 0; j < 5; j++) {
                        socket.send(parent);
                    }

                }
            }
            else{
                String deadMsg = "<" + UUID.randomUUID() + ">:DEAD";
                DatagramPacket dead = new DatagramPacket(deadMsg.getBytes("UTF-8"), deadMsg.length(), InetAddress.getByName(parentIP), parentPort);
                for (int i = 0; i < 5; i++) {
                    socket.send(dead);
                }
                for(String ip:childes.keySet()) {
                    String parentMsg = "<" + UUID.randomUUID() + ">:PARENT:<" + parentIP + ">:<" + parentPort + ">";
                    DatagramPacket parent = new DatagramPacket(parentMsg.getBytes("UTF-8"), parentMsg.length(), InetAddress.getByName(ip), childes.get(ip));
                    for (int j = 0; j < 5; j++) {
                        socket.send(parent);
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
