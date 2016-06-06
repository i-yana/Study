import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by Yana on 21.09.15.
 */
public class Client {

    private final String msg;
    private final String ip;
    private final int mcPort;
    private final Map<InetAddress, UserInfo> userList;



    public Client(String msg, String ip, int mcPort) {
        this.msg = msg;
        this.ip = ip;
        this.mcPort = mcPort;
        this.userList = Collections.synchronizedMap(new HashMap<InetAddress, UserInfo>());
    }

    void start() throws SocketException {

        byte[] data = msg.getBytes();
        try {
            InetAddress mcIPAddress = InetAddress.getByName(ip);
            Sender sender = new Sender(mcIPAddress, mcPort, data);
            Receiver receiver = new Receiver(mcIPAddress,mcPort, userList);
            Updater updater = new Updater(userList);
            sender.start();
            receiver.start();
            updater.start();

        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        }
    }
}
