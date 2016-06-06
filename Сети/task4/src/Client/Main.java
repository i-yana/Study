package Client;

import java.io.IOException;

/**
 * Created by Yana on 17.10.15.
 */
public class Main {

    public static void main(String[] args) {
        if(args.length != 3){
            System.out.println("Wrong arguments, expected path to file, server IP, sever port");
            return;
        }
        Client client = new Client(args[0], args[1], Integer.parseInt(args[2]));
        try {
            client.start();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
