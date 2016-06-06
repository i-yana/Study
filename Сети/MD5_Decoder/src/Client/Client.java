package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Created by Yana on 26.10.15.
 */
public class Client {

    private static final String REQUEST = "REQUEST";
    private static final String NO_TASKS = "NO TASKS";
    private static final String MD5 = "MD5";
    private static final String FOUND = "FOUND";
    private final String ip;
    private final int port;
    private final String guid;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Socket socket;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.guid = UUID.randomUUID().toString();
    }

    public void start() throws InterruptedException, IOException {

        while (!Thread.currentThread().isInterrupted()) {
            try {
                tryConnectToServer();
                dataOutputStream.writeUTF(REQUEST);
                dataOutputStream.flush();
                System.err.println("send " + REQUEST);
                String ans = dataInputStream.readUTF();
                if (ans.equals(NO_TASKS)) {
                    System.err.println("No new tasks. End work");
                    disconnect();
                    return;
                }
                if (ans.equals(MD5)) {
                    String md5 = dataInputStream.readUTF();
                    long first = dataInputStream.readLong();
                    long second = dataInputStream.readLong();
                    disconnect();
                    Decoder decoder = new Decoder(md5, first,second);
                    if (null != decoder.decrypt()) {
                        tryConnectToServer();
                        dataOutputStream.writeUTF(FOUND);
                        dataOutputStream.flush();
                        System.out.println("send "+FOUND);
                        disconnect();
                        System.err.println("Client is finished");
                        System.exit(0);
                    }
                }

            } catch (IOException e) {
                System.err.println(e.getMessage());
            } catch (NoSuchAlgorithmException e) {
                System.err.println(e.getMessage());
                disconnect();
                return;
            }
        }
    }

    private void disconnect() throws IOException {
        dataInputStream.close();
        dataOutputStream.close();
        socket.close();
    }

    private void tryConnectToServer() throws InterruptedException {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                System.err.println("Try connect to server");
                socket = new Socket(InetAddress.getByName(ip), port);

                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream.writeUTF(guid);
                dataOutputStream.flush();
                System.err.println("connect success");
                return;
            } catch (IOException e) {
                Thread.sleep(3000);
            }
        }
    }

}
