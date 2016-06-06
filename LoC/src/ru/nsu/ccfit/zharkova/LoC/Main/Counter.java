package ru.nsu.ccfit.zharkova.LoC.Main;
import java.io.*;

public class Counter {

    private final static long INITIAL = 0;

    public static long countLines(File file) throws IOException {
        long lines = INITIAL;
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        while (bufferedReader.readLine() != null) {
            lines++;
        }
        return lines;
    }
}
