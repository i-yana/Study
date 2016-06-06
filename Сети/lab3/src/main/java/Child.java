import java.net.InetAddress;

/**
 * Created by Yana on 14.10.15.
 */
public class Child{

    private InetAddress ip;
    private int port;

    Child(InetAddress ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
