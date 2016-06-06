package ru.nsu.ccfit.zharkova.chat.handlers;

/**
 * Created by Yana on 01.08.15.
 */
public class XMLParserException extends Throwable {

    private final String message;
    private static final String prefix = "cannot parse XML message: ";

    public XMLParserException(String reason){
        this.message = prefix + reason;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
