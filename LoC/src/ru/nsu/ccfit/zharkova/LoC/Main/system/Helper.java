package ru.nsu.ccfit.zharkova.LoC.Main.system;
/**
 * Created by Yana on 03.03.15.
 */
public class Helper {

    private final static String MSG = "Program takes two arguments - a file with the extension (Config file) and the path to the directory. It counts how many lines of code in files";

    public static void help(){
        System.out.println(MSG);
    }
}
