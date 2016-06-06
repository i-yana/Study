package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Yana on 17.10.15.
 */
public class Server {

    private int port;
    public Server(int port) {
        this.port = port;
    }

    public void start() throws IOException {

        ServerSocket serverSocket = new ServerSocket(port);
        Socket socket = null;
        while (!Thread.currentThread().isInterrupted()) {
            DataInputStream inputStream = null;
            DataOutputStream outputStream = null;
            FileOutputStream out = null;
            File file = null;
            try {
                socket = serverSocket.accept();
                System.out.println("socket with IP:" + socket.getInetAddress().getHostAddress() + " Port:" + socket.getPort() + " connect");
                socket.setSoTimeout(2000);
                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());
                String fileName = inputStream.readUTF();
                long fileSize = inputStream.readLong();
                System.out.println(fileName + " " + fileSize);
                byte[] buffer = new byte[64 * 1024];
                File dir = new File("./uploads");
                if(!dir.exists()) {
                    if (!dir.mkdir()) {
                        System.out.println("cannot create dir");
                    }
                }
                String filePath ="./uploads/"+ fileName;
                file = new File(filePath);

                if(file.exists()){
                    if(!file.delete()){
                        System.out.println("cannot delete file");
                        continue;
                    }
                    if(!file.createNewFile()){
                        System.out.println("cannot create file");
                        continue;
                    }
                }
                out = new FileOutputStream(file);
                int count, total = 0;

                while ((count = inputStream.read(buffer)) != 0) {
                    total += count;
                    out.write(buffer, 0, count);
                    if (total == fileSize) {
                        out.flush();
                        outputStream.writeUTF("Successful");
                        System.out.println("File received");
                        break;
                    }
                }
            }catch (IOException e) {
                System.out.println("file not received");
                if (file != null) {
                    if(!file.delete()){
                        System.out.printf("cannot delete file");
                    }
                }
            }
            finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (out != null) {
                    out.close();
                }
                if (socket != null) {
                    socket.close();
                }
            }
        }
    }
}
