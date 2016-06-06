package Server;

import java.io.*;
import java.net.Socket;

/**
 * Created by Yana on 25.10.15.
 */
public class ClientConnection extends Thread {

    private static final String REQUEST = "REQUEST";
    private static final String NO_TASKS = "NO TASKS";
    private static final String FOUND = "FOUND";
    private static final String MD5 = "MD5";
    private final Socket socket;
    private final Server server;
    private DataInputStream in;
    private DataOutputStream out;

    public ClientConnection(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());
    }

    public void run(){
        try {

            String guid = in.readUTF();
            System.err.println("guid " + guid);
            String msg = in.readUTF();
            System.err.println(msg);
            if(msg.equals(REQUEST)){
                Task task;
                if((task = server.giveTask()) == null || server.isFound()){
                    System.err.println("no tasks");
                    out.writeUTF(NO_TASKS);
                    out.flush();
                    server.removeClient(guid);
                    freeResources();
                    return;
                }
                out.writeUTF(MD5);
                out.flush();
                out.writeUTF(server.getMd5());
                out.flush();
                out.writeLong(task.fst);
                out.flush();
                out.writeLong(task.snd);
                out.flush();
                System.err.println("interval " + task.fst + " " + task.snd);
                task.setTime(System.currentTimeMillis());
                server.addClientWithTask(guid, task);
            }
            if(msg.equals(FOUND)){
                System.err.println("Row found");
                server.found();
                server.removeClient(guid);
                freeResources();
            }


        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            freeResources();
        }
    }

    private void freeResources() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }


}
