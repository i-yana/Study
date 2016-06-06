package Server;

import java.io.IOException;

/**
 * Created by Yana on 17.10.15.
 */
public class Main {

    public static void main(String[] args) {
        if(args.length != 1){
            System.out.println("Wrong arguments, expected port");
            return;
        }
        int port = Integer.parseInt(args[0]);
        Server server = new Server(port);
        try {
            server.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
