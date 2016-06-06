package ru.nsu.ccfit.zharkova.chat.protocol;

/**
 * Created by Yana on 28.07.15.
 */
public class LoginSuccessesMSG extends Message implements ServerAnswer {

    private final int session;

    public LoginSuccessesMSG(int session){
        this.session = session;
    }

    public int getSession() {
        return session;
    }
}
