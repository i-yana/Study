package ru.nsu.ccfit.zharkova.chat.client;


import javax.swing.text.BadLocationException;
import java.io.IOException;

/**
 * Created by Yana on 07.07.15.
 */
public class MainClient {
    public static void main(String args[]) {

        try {
            new Controller();
        } catch (IOException | BadLocationException e) {
            System.err.println(e.getMessage());
        }
    }
}
