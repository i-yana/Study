import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Yana on 23.09.15.
 */
public class Updater extends Thread {

    private final Map<InetAddress,UserInfo> userList;

    public Updater(Map<InetAddress, UserInfo> userList) {
        this.userList = userList;
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                sleep(1000);
                Iterator<Map.Entry<InetAddress,UserInfo>> it = userList.entrySet().iterator();
                System.out.println(userList.size()+ " user connect");
                while (it.hasNext())
                {
                    Map.Entry<InetAddress,UserInfo> item = it.next();
                    if (System.currentTimeMillis()/1000 - item.getValue().getMs() > 2) {
                        it.remove();
                    }
                }
                for(UserInfo u: userList.values()){
                    System.out.println(u.getInetAddress().getHostAddress() + " " + u.getMsg());
                }
            } catch (InterruptedException ignored) {}
        }
    }
}
