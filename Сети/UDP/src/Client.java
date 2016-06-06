import java.io.IOException;
import java.net.*;

/**
 * Created by Yana on 12.09.15.
 */
public class Client {

    private static final String PING = "ping";
    private static final String PONG = "pong";
    private final String ipAddress;
    private final int serverPort;
    private final int clientPort;
    private final int quantity;
    private final int timeout;
    private int generalTime = 0;
    private int successPackets = 0;
    private int lostPackets = 0;

    public Client(String ip, int serverPort, int clientPort, int quantity, int timeout){
        this.ipAddress = ip;
        this.serverPort = serverPort;
        this.clientPort = clientPort;
        this.quantity = quantity;
        this.timeout = timeout;
    }

    public void connect() {
        DatagramSocket socket = null;
        double timeStart;
        double timeEnd;
        try {
            socket = new DatagramSocket(clientPort);

            byte[] buffer;
            for (int i = 0; i < quantity; i++) {
                try {
                    socket.setSoTimeout(timeout);
                    buffer = PING.getBytes();
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ipAddress), serverPort);
                    socket.send(datagramPacket);
                    System.out.println("send: " + PING);
                    timeStart = System.currentTimeMillis();
                    socket.receive(datagramPacket);
                    String answer = new String(datagramPacket.getData());
                    if (datagramPacket.getAddress().equals(InetAddress.getByName(ipAddress)) && datagramPacket.getPort() == serverPort && answer.equals(PONG)) {
                        timeEnd = System.currentTimeMillis();
                        generalTime += (timeEnd-timeStart);
                        System.out.println("receive: " + PONG);
                        successPackets++;
                    } else{
                        lostPackets++;
                    }
                } catch (SocketTimeoutException e){
                    lostPackets++;
                }
            }
            countStatistic();
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        finally {
            assert socket != null;
            socket.close();
        }
    }

    private void countStatistic() {
        System.out.println("Packets: "+successPackets + " success, " + lostPackets + " lost");
        if(successPackets!=0) {
            System.out.println("Average time: " + (generalTime / successPackets) + "ms");
        }
    }

    public static void main(String[] args){
        Client client = new Client("192.168.0.6", 1234, 5000, 100, 100);
        client.connect();
        System.exit(0);
    }
}
