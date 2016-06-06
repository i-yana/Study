package ru.nsu.ccfit.zharkova.LoC.Main;
/**
 * Created by Yana on 17.03.15.
 */
public class WrongConfigFormat extends Throwable {
    public WrongConfigFormat(String incorrectLine) {
        line = incorrectLine;
    }
    private String line;
    @Override
    public String toString() {
        return "Wrong format in line: " + line;
    }
}
