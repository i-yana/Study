package Server;

import com.sun.tools.javac.util.Pair;

/**
 * Created by Yana on 25.10.15.
 */
public class Task extends Pair<Long, Long> {

    private long time;

    public Task(long first, long second) {
        super(first,second);
    }

    public void setTime(long time){
        this.time = time;
    }

    public long getTime() {
        return time;
    }

}
