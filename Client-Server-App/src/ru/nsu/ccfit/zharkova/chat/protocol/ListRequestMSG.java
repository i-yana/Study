package ru.nsu.ccfit.zharkova.chat.protocol;

/**
 * Created by Yana on 28.07.15.
 */
public class ListRequestMSG extends Message implements Request{

    private final int session;

    public ListRequestMSG(int session){
        this.session = session;
    }

    public int getSession() {
        return session;
    }
}
