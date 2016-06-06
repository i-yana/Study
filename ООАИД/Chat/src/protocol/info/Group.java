package protocol.info;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yana on 03.12.15.
 */
public class Group implements Serializable {

    private final String groupName;
    private final boolean type;
    private final List<UserInfo> participants;


    public Group(String groupName, boolean type){
        this.groupName = groupName;
        this.type = type;
        this.participants = new ArrayList<>();
    }

    public void add(UserInfo userInfo){
        synchronized (participants) {
            participants.add(userInfo);
        }
    }

    public List<UserInfo> getParticipants(){
        synchronized (participants) {
            return new ArrayList<>(participants);
        }
    }

    public void remove(int userID){
        synchronized (participants) {
            for (UserInfo u : participants) {
                if (u.getId() == userID) {
                    participants.remove(u);
                    return;
                }
            }
        }
    }
    public void  removeAll(){
        synchronized (participants) {
            participants.removeAll(participants);
        }
    }

    public String getGroupName() {
        return groupName;
    }

    public boolean isPublic() {
        return type;
    }

    public boolean contains(Integer id) {
        synchronized (participants) {
            for (UserInfo u : participants) {
                if (u.getId() == id) {
                    return true;
                }
            }
        }
        return false;
    }
}
