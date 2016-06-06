package ru.nsu.ccfit.zharkova.LoC.Main.filters;
import java.io.File;

/**
 * Created by Yana on 03.03.15.
 */
public class LaterThanFilter implements Filter {

    private static final String IDENTIFIER = ">";
    private static final int ONE_SEC = 1000;
    private long time;
    private int numOfLines = 0;
    private int numOfFiles = 0;

    public LaterThanFilter(long time){
        this.time = time;
    }

    @Override
    public boolean isGoodFile(File path) {
        return (path.lastModified() / ONE_SEC > time);
    }

    @Override
    public String getFilterName() {
        return IDENTIFIER + time;
    }

    @Override
    public long getNumOfLines() {
        return numOfLines;
    }

    @Override
    public long getNumOfFiles() {
        return numOfFiles;
    }

    @Override
    public void addLines(long num) {
        numOfLines+= num;
    }

    @Override
    public void addFiles() {
        numOfFiles++;
    }
}
