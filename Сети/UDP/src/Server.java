import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by Yana on 12.09.15.
 */
public class Server {

    private static final int PORT = 1234;
    private static final String PING = "ping";
    private static final String PONG = "pong";
    public void start(){
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(PORT);
            byte[] buffer = new byte[4];
            while (!Thread.currentThread().isInterrupted()) {
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(datagramPacket);
                String data = new String(datagramPacket.getData());
                if(data.intern().equals(PING)) {
                    System.out.println("receive: " + new String(datagramPacket.getData()));
                    buffer = PONG.getBytes();
                    datagramPacket = new DatagramPacket(buffer, buffer.length, datagramPacket.getAddress(), datagramPacket.getPort());
                    socket.send(datagramPacket);
                    System.out.println("send: " + new String(datagramPacket.getData()));
                }
            }
        } catch (SocketException e) {
            e.getMessage();
        } catch (IOException e) {
            e.getMessage();
        } finally {
            assert socket != null;
            socket.close();
        }

    }
    public static void main(String[] args){
        Server server = new Server();
        server.start();
    }
}
