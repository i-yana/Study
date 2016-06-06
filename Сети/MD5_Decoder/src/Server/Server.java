package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;

/**
 * Created by Yana on 25.10.15.
 */
public class Server {

    private String md5;
    private final int port;
    private static final int MAX_LENGTH = 14;
    private final List<Task> tasks;
    private final Map<String, Task> clientsWithTasks;
    private boolean found = false;
    private static final long STEP = 512*512*512;

    public Server(String md5, int port) {
        this.port = port;
        this.tasks = new ArrayList<>();
        this.clientsWithTasks = new HashMap<>();
        this.md5 = md5;
        System.err.println("original: " + md5);
        Generator generator = new Generator();
        generator.start();
    }

    class Generator extends Thread{

        public void run(){
            generate();

        }
        void generate(){
            long variants = 0;
            for (long i = 0; i <= MAX_LENGTH; i++) {
                variants += Math.pow(4, i);
            }
            System.err.println((int) variants + " variants");

            long j;
            for (long i = 0; i <=variants; i=j){
                Task task;
                j = i+STEP+1;
                if(j+1 >= variants) {
                    task = new Task(i, variants);
                    synchronized (tasks){
                        tasks.add(task);
                        tasks.notifyAll();
                    }
                    return;
                }
                task = new Task(i, j);
                synchronized (tasks) {
                    tasks.add(task);
                    tasks.notifyAll();
                }
            }

        }

    }

    public void start() throws IOException {
        Socket socket = null;
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            Checker checker = new Checker();
            Timer timer = new Timer();
            timer.schedule(checker, 10000);
            while(!Thread.currentThread().isInterrupted()){
                try {
                    socket = serverSocket.accept();
                    System.err.println("client connect: " + socket.getInetAddress().getHostAddress());
                    ClientConnection clientConnection = new ClientConnection(socket, this);
                    clientConnection.start();
                } catch (SocketTimeoutException e){
                    check();
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
            assert socket != null;
            socket.close();
        }
    }

    private void check() {
        System.out.println("checking...");
        synchronized (clientsWithTasks) {
            Iterator<Map.Entry<String, Task>> it = clientsWithTasks.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Task> item = it.next();
                if (System.currentTimeMillis() / 1000 - item.getValue().getTime() / 1000 > 5) {
                    tasks.add(item.getValue());
                    it.remove();
                }
            }
            if (isFound() && clientsWithTasks.isEmpty()) {
                System.err.println("server is finished");
                System.exit(0);
            }
        }
        System.out.println("check over");
    }

    class Checker extends TimerTask{
        @Override
        public void run() {
            check();
        }
    }

    public Task giveTask()  {
        synchronized (tasks){
            if(tasks.isEmpty()){
                return null;
            }
            return tasks.remove(0);
        }
    }


    public String getMd5() {
        return md5;
    }


    public void addClientWithTask(String guid, Task task) {
        synchronized (clientsWithTasks) {
            clientsWithTasks.put(guid, task);
        }
    }

    public void found() {
        found = true;
    }

    public boolean isFound(){
        return found;
    }

    public void removeClient(String guid) {
        synchronized (clientsWithTasks) {
            clientsWithTasks.remove(guid);
        }
    }
}
