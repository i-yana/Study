package ru.nsu.ccfit.zharkova.chat.protocol;

/**
 * Created by Yana on 28.07.15.
 */
public class ErrorMSG extends Message implements ServerAnswer {

    private final String reason;

    public ErrorMSG(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
