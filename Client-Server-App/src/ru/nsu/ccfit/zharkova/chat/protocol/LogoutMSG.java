package ru.nsu.ccfit.zharkova.chat.protocol;

/**
 * Created by Yana on 05.07.15.
 */
public class LogoutMSG extends Message implements Request {

    private final int session;

    public LogoutMSG(int session){
        this.session = session;
    }

    public int getSession() {
        return session;
    }
}
