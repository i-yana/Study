import java.io.IOException;
import java.net.SocketException;

/**
 * Created by Yana on 14.10.15.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println(args.length);
        if(args.length != 3 && args.length !=5){
            System.err.println("Error arguments, expected: host Name, lost percent, port, parent IP, parent Port");
            return;
        }
        String hostName = args[0];
        int lostPercent = Integer.parseInt(args[1]);
        int port = Integer.parseInt(args[2]);
        Node node = null;
        try {
        if(args.length == 5){
            String parentIP = args[3];
            int parentPort = Integer.parseInt(args[4]);
                node = new Node(hostName,lostPercent,port,parentIP,parentPort);
        }
        else {
            node = new Node(hostName, lostPercent, port);
        }
            ShutdownHook shutdownHook = new ShutdownHook(node);
            Runtime.getRuntime().addShutdownHook(shutdownHook);
            node.start();

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
