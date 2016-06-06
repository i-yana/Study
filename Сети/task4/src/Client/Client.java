package Client;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by Yana on 20.10.15.
 */
public class Client {

    private final String path;
    private final String serverIp;
    private final int serverPort;

    Client(String path, String serverIp, int serverPort){
        this.path = path;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public void start() throws IOException {
        Socket socket = null;
        try {
            File file = new File(path);
            String filename = file.getName();
            if (filename.getBytes().length > 2048) {
                System.err.println("File name is too long");
                return;
            }
            if (file.length() > 1e+12) {
                System.err.println("Cannot send File > 1 TB");
                return;
            }

            socket = new Socket(serverIp, serverPort);
            socket.setSoTimeout(2000);
            FileInputStream in = new FileInputStream(file);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            outputStream.writeUTF(file.getName());
            outputStream.writeLong(file.length());

            byte[] buffer = new byte[64 * 1024];
            int count;

            while ((count = in.read(buffer)) != 0) {
                outputStream.write(buffer, 0, count);
            }
            outputStream.flush();
            String answer = inputStream.readUTF();
            System.out.println(answer);
            in.close();
            inputStream.close();
            outputStream.close();
        } catch (SocketTimeoutException e){
            System.out.println("unsuccessful transmission "+ e.getMessage());
        } catch (IOException e) {
            System.out.println("unsuccessful transmission IO " + e.getMessage());
        }
        finally {
            if (socket != null) {
                socket.close();
            }
        }
    }


}
