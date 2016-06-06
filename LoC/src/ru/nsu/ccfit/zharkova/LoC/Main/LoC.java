package ru.nsu.ccfit.zharkova.LoC.Main;

import ru.nsu.ccfit.zharkova.LoC.Main.system.Helper;

public class LoC {

    private final static int NULL = 0;
    private final static int FIRST_ARGUMENT = 0;
    private final static int SECOND_ARGUMENT = 1;
    private final static int TWO_ARGUMENTS = 2;

    public static void main(String[] args) {

        if (args.length == NULL) {
            Helper.help();
            return;
        }
        if (args.length == TWO_ARGUMENTS) {
            Executor.run(args[FIRST_ARGUMENT], args[SECOND_ARGUMENT]);
            return;
        }
        System.err.println("Wrong count of arguments");
    }
}

