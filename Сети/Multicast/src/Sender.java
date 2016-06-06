import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Yana on 22.09.15.
 */
public class Sender extends Thread {

    private final InetAddress mcIPAddress;
    private final int mcPort;
    private final byte[] data;

    public Sender(InetAddress mcIPAddress, int mcPort, byte[] data) {
        this.mcIPAddress = mcIPAddress;
        this.mcPort = mcPort;
        this.data = data;
    }

    public void run(){
        DatagramSocket udpSocket = null;
        try {
            udpSocket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(data, data.length, mcIPAddress, mcPort);
        while(!Thread.currentThread().isInterrupted()) {
            sleep(1000);
            udpSocket.send(packet);
        }
        } catch (IOException e) {
            assert udpSocket != null;
            udpSocket.close();
        } catch (InterruptedException ignored) {}
    }
}
