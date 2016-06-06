package ru.nsu.ccfit.zharkova.LoC.Main.filters;
import java.io.File;

/**
 * Created by Yana on 03.03.15.
 */
public interface Filter {
    boolean isGoodFile(File path);
    String getFilterName();
    long getNumOfLines();
    long getNumOfFiles();
    void addLines(long num);
    void addFiles();
}
