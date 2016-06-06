package ru.nsu.ccfit.zharkova.LoC.Main;
import ru.nsu.ccfit.zharkova.LoC.Main.system.Console;
import ru.nsu.ccfit.zharkova.LoC.Main.system.ParseConfig;

import java.io.IOException;

/**
 * Created by Yana on 03.03.15.
 */
public class Executor {

    public static void run(String pathConfig, String pathFile) {
        FileStatistic fs = null;
        try {
            fs = new FileStatistic(ParseConfig.read(pathConfig), pathFile);
            Console.out(fs);
        } catch (PathOfDirectoryException e) {
            System.err.println(e.toString());
        } catch (WrongConfigFormat e) {
            System.err.println(e.toString());
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
}
