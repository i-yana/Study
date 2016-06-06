import java.net.InetAddress;

/**
 * Created by Yana on 02.10.15.
 */
public class UserInfo {
    private final InetAddress inetAddress;
    private final String msg;
    private final Long ms;

    public UserInfo(InetAddress inetAddress, String msg, Long ms){
        this.inetAddress = inetAddress;
        this.msg = msg;
        this.ms = ms;
    }

    public String getMsg() {
        return msg;
    }

    public Long getMs() {
        return ms;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }
}
