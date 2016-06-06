import java.io.UnsupportedEncodingException;
import java.net.SocketException;

/**
 * Created by Yana on 22.09.15.
 */
public class Main {
    public static String cutLengthInBytes(String string, int length) throws UnsupportedEncodingException {
        if (string == null) {
            return null;
        }
        byte[] utf8 = string.getBytes("UTF-8");
        int len = length;
        for (; len > 0; len--) {
            if ((utf8[len] & 0x80) == 0) {
                break;
            }
            if ((utf8[len] & 0x40) == 0) {
                len--;
                break;
            }
        }
        String r = new String(string.getBytes("UTF-8"), 0, len, "UTF-8");
        System.out.println(r);
        return r;
    }


    public static void main(String[] args) {
        try {
            if(args.length!=2){
                System.out.println("not enough arguments");
                return;
            }
            String msg=args[0];
            int port = Integer.parseInt(args[1]);
            if(msg.getBytes("UTF-8").length>255){
                msg = cutLengthInBytes(msg, 255);
            }
            Client client = new Client(msg, "228.5.6.7", port);
            client.start();
        } catch (UnsupportedEncodingException | SocketException e) {
            e.printStackTrace();
        }
    }
}
