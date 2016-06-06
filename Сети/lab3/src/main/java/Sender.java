import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Yana on 15.10.15.
 */
public class Sender extends Thread {

    private final DatagramSocket datagramSocket;
    private final Map<String, Message> sendMessages;
    private final String name;

    Sender(DatagramSocket datagramSocket, Map<String, Message> sendMessages, String name){
        this.datagramSocket = datagramSocket;
        this.sendMessages = sendMessages;
        this.name = name;
    }

    public void run(){
        while(!Thread.currentThread().isInterrupted()){
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String data = br.readLine();
                String msg = "<"+ UUID.randomUUID() + ">:MSG:<"+ name + ">:<" + data + ">";
                Message message = new Message(msg.getBytes("UTF-8"), InetAddress.getLocalHost().getHostAddress(), datagramSocket.getPort(), System.currentTimeMillis()/1000);
                synchronized (sendMessages){
                    sendMessages.put(message.getId(), message);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
