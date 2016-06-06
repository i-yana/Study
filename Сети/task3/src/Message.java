import java.io.UnsupportedEncodingException;

/**
 * Created by Yana on 14.10.15.
 */
public class Message {

    private String id;
    private String msg;
    private String type;
    private long time;
    private byte[] data;
    private String inetAddress;
    private int port;

    public Message(byte[] data, String inetAddress, int port, long time) throws UnsupportedEncodingException {
        this.data = data;
        this.inetAddress = inetAddress;
        this.port = port;
        this.time = time;
        String strData = new String(data, "UTF-8");
        this.id = strData.substring(strData.indexOf('<') + 1, strData.indexOf('>'));
        String tmp = strData.substring(strData.indexOf(":")+1);
        if(!tmp.contains(":<")){
            this.type = tmp;
            return;
        }
        this.type = tmp.substring(0, tmp.indexOf(":"));
        this.msg = tmp.substring(tmp.indexOf(":")+1, tmp.length());
    }

    public String getMsg() {
        return msg;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public byte[] getData() {
        return data;
    }

    public String getInetAddress() {
        return inetAddress;
    }

    public int getPort() {
        return port;
    }
}
