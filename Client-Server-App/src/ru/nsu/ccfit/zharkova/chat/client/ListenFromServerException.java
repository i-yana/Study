package ru.nsu.ccfit.zharkova.chat.client;

/**
 * Created by Yana on 31.07.15.
 */
public class ListenFromServerException extends Throwable {

    private final String reason;

    public ListenFromServerException(String reason) {
        this.reason = reason;
    }

    public String getMessage(){
        return reason;
    }

}
