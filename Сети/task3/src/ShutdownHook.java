/**
 * Created by Yana on 16.10.15.
 */
public class ShutdownHook extends Thread {

    private final Node node;

    public ShutdownHook(Node node){
        this.node = node;
    }

    public void run(){
        node.handleClosing();
        System.out.println("ShutDown");
    }
}
