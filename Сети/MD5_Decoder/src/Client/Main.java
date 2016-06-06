package Client;

import java.io.IOException;

/**
 * Created by Yana on 26.10.15.
 */
public class Main {

    public static void main(String[] args) {
        if(args.length!=2){
            System.err.println("Not enough arguments, expected server IP, server Port");
            return;
        }
        Client client = new Client(args[0], Integer.parseInt(args[1]));
        try {
            client.start();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
