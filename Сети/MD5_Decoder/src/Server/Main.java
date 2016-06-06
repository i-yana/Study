package Server;

import java.io.IOException;

/**
 * Created by Yana on 25.10.15.
 */
public class Main {

    public static void main(String[] args) {
        if(args.length!=2){
            System.err.println("Not enough arguments, expected md5, server port");
            return;
        }
        Server server = new Server(args[0], Integer.parseInt(args[1]));
        try {
            server.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
