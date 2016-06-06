package ru.nsu.ccfit.zharkova.LoC.Main.system;
import ru.nsu.ccfit.zharkova.LoC.Main.FileStatistic;
import ru.nsu.ccfit.zharkova.LoC.Main.filters.Filter;

import java.io.IOException;
import java.util.List;

/**
 * Created by Yana on 04.03.15.
 */
public class Console {

    private static final int NO_FILES = 0;
    private static final String TOTAL_COUNT_ID = "Total - ";
    private static final String LINES_ID = " lines in ";
    private static final String FILES_ID = " files";
    private static final String DASH_ID = " - ";
    private static final String SEPARATOR = "-----------------------";

    public static void out(FileStatistic fs) throws IOException {
        List<Filter> filters = fs.getList();
        System.out.println(TOTAL_COUNT_ID + fs.getTotalLines() + LINES_ID + fs.getTotalFiles() + FILES_ID);
        System.out.println(SEPARATOR);
        for (Filter node : filters) {
            if (node.getNumOfFiles() == NO_FILES) {
                continue;
            }
            System.out.println(node.getFilterName() + DASH_ID + node.getNumOfLines() + LINES_ID + node.getNumOfFiles() + FILES_ID);
        }
    }
}
