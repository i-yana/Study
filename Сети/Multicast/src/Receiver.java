import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Map;

/**
 * Created by Yana on 22.09.15.
 */
public class Receiver extends Thread {

    private final InetAddress mcIPAddress;
    private final int mcPort;
    private final Map<InetAddress,UserInfo> userList;


    public Receiver(InetAddress mcIPAddress, int mcPort, Map<InetAddress, UserInfo> userList) {
        this.mcIPAddress = mcIPAddress;
        this.mcPort = mcPort;
        this.userList = userList;
    }

    public void run(){
        MulticastSocket mcSocket = null;
        try {
            mcSocket = new MulticastSocket(mcPort);
            mcSocket.joinGroup(mcIPAddress);
            mcSocket.setTimeToLive(200);
            DatagramPacket packet = new DatagramPacket(new byte[255], 255);
            while(!Thread.currentThread().isInterrupted()) {
                mcSocket.receive(packet);
                InetAddress inetAddress = packet.getAddress();
                String msg = new String(packet.getData(), packet.getOffset(), packet.getLength(), "UTF-8");
                userList.put(inetAddress, new UserInfo(inetAddress, msg, System.currentTimeMillis() / 1000));
            }

        } catch (IOException ignored) {
            assert mcSocket != null;
            try {
                mcSocket.leaveGroup(mcIPAddress);
            } catch (IOException e) {
                mcSocket.close();
            }
            mcSocket.close();
        }
    }
}
